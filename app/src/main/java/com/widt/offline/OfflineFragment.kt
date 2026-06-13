package com.widt.offline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.widt.R
import com.widt.common.shizuku.ShizukuHelper
import com.widt.databinding.FragmentOfflineBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineFragment : Fragment() {
    
    private var _binding: FragmentOfflineBinding? = null
    private val binding get() = _binding!!
    
    private val offlineCracker = OfflineCracker()
    private var capturedHandshake: ByteArray? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfflineBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupCapture()
        setupImport()
        setupCrack()
    }
    
    private fun setupCapture() {
        binding.btnCapture.setOnClickListener {
            if (!ShizukuHelper.isShizukuRunning()) {
                Toast.makeText(requireContext(), "Shizuku required for handshake capture. Please setup Shizuku first.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            
            binding.tvCaptureStatus.text = "Capturing... (requires root/Shizuku with tcpdump)"
            Toast.makeText(requireContext(), "Handshake capture - requires tcpdump. Coming soon!", Toast.LENGTH_SHORT).show()
            
            // TODO: Implement full handshake capture through Shizuku
            // lifecycleScope.launch {
            //     capturedHandshake = offlineCracker.captureHandshake(ssid)
            //     binding.tvCaptureStatus.text = if (capturedHandshake != null) "Captured successfully" else "Capture failed"
            //     binding.btnStartCrack.isEnabled = capturedHandshake != null
            // }
        }
    }
    
    private fun setupImport() {
        binding.btnImportPcap.setOnClickListener {
            // Open file picker for pcap
            val intent = android.content.Intent(android.content.Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(android.content.Intent.CATEGORY_OPENABLE)
                type = "application/vnd.tcpdump.pcap"
            }
            startActivityForResult(intent, PCAP_PICKER_REQUEST)
        }
    }
    
    private fun setupCrack() {
        binding.btnStartCrack.setOnClickListener {
            if (capturedHandshake == null) {
                Toast.makeText(requireContext(), "No handshake captured or imported", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val dictionary = listOf(
                "12345678", "password", "123456789", "qwerty123", "admin123",
                "viettel123", "fpt12345", "vnpt1234", "tendawifi", "11111111"
            )
            
            binding.btnStartCrack.isEnabled = false
            binding.tvCrackProgress.text = "Cracking..."
            binding.progressCrack.isIndeterminate = true
            
            lifecycleScope.launch {
                // Placeholder for dictionary attack
                // val result = offlineCracker.dictionaryAttack(ssid, capturedHandshake!!, dictionary) { progress ->
                //     withContext(Dispatchers.Main) {
                //         binding.tvCrackProgress.text = "Progress: ${progress.current}/${progress.total}"
                //         binding.progressCrack.progress = (progress.current * 100 / progress.total)
                //     }
                // }
                
                withContext(Dispatchers.Main) {
                    binding.btnStartCrack.isEnabled = true
                    binding.progressCrack.isIndeterminate = false
                    Toast.makeText(requireContext(), "Full offline cracking coming soon with PBKDF2 implementation", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PCAP_PICKER_REQUEST && resultCode == android.app.Activity.RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    capturedHandshake = inputStream?.readBytes()
                    inputStream?.close()
                    
                    binding.tvCaptureStatus.text = "PCAP imported: ${uri.lastPathSegment}"
                    binding.btnStartCrack.isEnabled = true
                    Toast.makeText(requireContext(), "PCAP imported successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Failed to import PCAP: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val PCAP_PICKER_REQUEST = 1002
    }
}