package com.widt.analyzer

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.widt.R
import com.widt.databinding.FragmentAnalyzerBinding
import com.widt.model.WifiNetwork
import com.widt.utils.WifiUtils

class AnalyzerFragment : Fragment() {
    
    private var _binding: FragmentAnalyzerBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: AnalyzerViewModel
    private lateinit var adapter: WifiListAdapter
    private var scanReceiver: BroadcastReceiver? = null
    private var hasRequestedScan = false
    private val scanHandler = Handler(Looper.getMainLooper())
    private var scanPollRunnable: Runnable? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyzerBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[AnalyzerViewModel::class.java]
        
        setupRecyclerView()
        setupScanButton()
        setupScanReceiver()
        observeViewModel()
        
        // Initial scan (will silently skip if permission not yet granted)
        startScan()
    }
    
    override fun onResume() {
        super.onResume()
        // Re-scan when returning from permission dialog with newly granted permission
        if (isAdded && hasLocationPermission() && !hasRequestedScan) {
            startScan()
        }
    }
    
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun setupRecyclerView() {
        adapter = WifiListAdapter { network ->
            viewModel.selectNetwork(network)
            // Navigate to tester fragment with selected network
            val bundle = Bundle().apply {
                putString("selected_network_json", Gson().toJson(network))
            }
            view?.findNavController()?.navigate(R.id.action_navigation_analyzer_to_navigation_tester, bundle)
        }
        binding.rvWifiList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWifiList.adapter = adapter
    }
    
    private fun setupScanButton() {
        binding.btnScan.setOnClickListener {
            if (!hasLocationPermission()) {
                Toast.makeText(requireContext(), "Location permission required for WiFi scanning", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            startScan()
        }
    }
    
    private fun setupScanReceiver() {
        scanReceiver = WifiScanReceiver { networks ->
            viewModel.updateNetworks(networks)
        }
        
        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        // SCAN_RESULTS_AVAILABLE_ACTION is a system broadcast, must use RECEIVER_EXPORTED
        ContextCompat.registerReceiver(
            requireContext(),
            scanReceiver,
            intentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }
    
    private fun startScan() {
        if (!hasLocationPermission()) {
            return
        }

        if (!WifiUtils.isWifiEnabled(requireContext())) {
            Toast.makeText(requireContext(), "Please enable WiFi first", Toast.LENGTH_SHORT).show()
            return
        }

        hasRequestedScan = true
        viewModel.setScanning(true)
        binding.tvScanStatus.visibility = View.VISIBLE
        binding.tvScanStatus.text = getString(R.string.scanning)

        // Trigger scan as a hint to the OS (return value is unreliable on Android 9+/ColorOS)
        val canProceed = WifiUtils.startScan(requireContext())
        if (!canProceed) {
            // Only reaches here if Location permission was revoked between check and scan
            viewModel.setScanning(false)
            binding.tvScanStatus.visibility = View.GONE
            Toast.makeText(requireContext(), "Location permission required", Toast.LENGTH_SHORT).show()
            return
        }

        // Always read scanResults immediately — the OS may have fresh cached results
        // even if startScan() was throttled. BroadcastReceiver updates when new scan arrives.
        val networks = WifiUtils.getScanResults(requireContext())
        if (networks.isNotEmpty()) {
            viewModel.updateNetworks(networks)
            viewModel.setScanning(false)
            binding.tvScanStatus.text = "Found ${networks.size} networks"
        } else {
            // No cached results yet — keep scanning status, wait for broadcast.
            // Also schedule a fallback poll after 3s in case broadcast never fires (ColorOS throttle).
            binding.tvScanStatus.text = "Scanning\u2026 (waiting for results)"
            scheduleScanFallbackPoll()
        }
    }

    /**
     * Schedules a fallback poll of scanResults after a delay.
     * On ColorOS/OPPO, SCAN_RESULTS_AVAILABLE_ACTION broadcast may never fire when
     * startScan() was throttled. This ensures we still read results from the OS cache.
     */
    private fun scheduleScanFallbackPoll(delayMs: Long = 3000L) {
        scanPollRunnable?.let { scanHandler.removeCallbacks(it) }
        val runnable = Runnable {
            if (!isAdded || _binding == null) return@Runnable
            val polledNetworks = WifiUtils.getScanResults(requireContext())
            if (polledNetworks.isNotEmpty()) {
                viewModel.updateNetworks(polledNetworks)
                viewModel.setScanning(false)
                binding.tvScanStatus.text = "Found ${polledNetworks.size} networks"
            } else {
                // Still empty — try once more after another 4 seconds
                binding.tvScanStatus.text = "No networks found yet. Retrying\u2026"
                scheduleScanFallbackPoll(4000L)
            }
        }
        scanPollRunnable = runnable
        scanHandler.postDelayed(runnable, delayMs)
    }
    
    private fun observeViewModel() {
        viewModel.wifiNetworks.observe(viewLifecycleOwner) { networks ->
            adapter.submitList(networks)
            binding.tvScanStatus.text = "Found ${networks.size} networks"
            if (networks.isEmpty()) {
                binding.tvScanStatus.text = getString(R.string.no_wifi_found)
            }
        }
        
        viewModel.isScanning.observe(viewLifecycleOwner) { isScanning ->
            if (!isScanning) {
                _binding?.tvScanStatus?.postDelayed({
                    if (_binding != null && viewModel.wifiNetworks.value?.isNotEmpty() == true) {
                        _binding?.tvScanStatus?.visibility = View.GONE
                    }
                }, 2000)
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        scanPollRunnable?.let { scanHandler.removeCallbacks(it) }
        scanPollRunnable = null
        try {
            scanReceiver?.let { requireContext().unregisterReceiver(it) }
        } catch (e: Exception) {
            // Receiver might not be registered if view was destroyed before registration completed
        }
        _binding = null
    }
}