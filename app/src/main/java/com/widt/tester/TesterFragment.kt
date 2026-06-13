package com.widt.tester

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.widt.R
import com.widt.databinding.FragmentTesterBinding
import com.widt.model.WifiNetwork
import com.widt.utils.NotificationHelper

class TesterFragment : Fragment() {

    private var _binding: FragmentTesterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TesterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTesterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[TesterViewModel::class.java]
        viewModel.init(requireContext())

        // Get selected network from arguments
        val networkJson = arguments?.getString("selected_network_json")
        val network = networkJson?.let { Gson().fromJson(it, WifiNetwork::class.java) }

        network?.let {
            viewModel.selectNetwork(it)
            binding.tvSelectedNetwork.text = "Selected: ${it.ssid} (${it.vendor ?: "Unknown"})"
        } ?: run {
            binding.tvSelectedNetwork.text = "No network selected — go to Analyzer tab first."
        }

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnStartTest.setOnClickListener {
            if (viewModel.selectedNetwork.value == null) {
                Toast.makeText(requireContext(), "Please select a network from the Analyzer tab first", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.startTest()
        }

        binding.btnStopTest.setOnClickListener {
            viewModel.stopTest()
        }

        // Dictionary Select → show BottomSheet
        binding.btnSelectDictionary.setOnClickListener {
            showDictionaryBottomSheet()
        }

        // Engine Details button
        binding.btnEngineStatus.setOnClickListener {
            val status = viewModel.getEngineStatus()
            Toast.makeText(requireContext(), status, Toast.LENGTH_LONG).show()
        }
    }

    private fun showDictionaryBottomSheet() {
        val sheet = DictionaryBottomSheet()

        sheet.onDictionarySelected = { fileName, displayName ->
            viewModel.loadFromRaw(fileName)
            Toast.makeText(requireContext(), "Loaded: $displayName", Toast.LENGTH_SHORT).show()
        }

        sheet.onFilePickerRequested = {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
            }
            startActivityForResult(intent, FILE_PICKER_REQUEST)
        }

        sheet.show(parentFragmentManager, DictionaryBottomSheet.TAG)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_PICKER_REQUEST && resultCode == android.app.Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val success = viewModel.loadCustomDictionary(uri)
                if (success) {
                    Toast.makeText(requireContext(), "Custom dictionary loaded successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to load custom dictionary", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeViewModel() {
        val pulseAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse)

        // Dictionary count observer (Phase 2 fix: show real count)
        viewModel.dictionary.observe(viewLifecycleOwner) { dict ->
            val count = dict.size
            binding.tvDictSize.text = "$count passwords loaded"
            binding.tvInfo.text = "Ready to test ${count} passwords"
        }

        // Testing state observer — controls all animations and colors
        viewModel.isTesting.observe(viewLifecycleOwner) { isTesting ->
            binding.btnStartTest.isEnabled = !isTesting && viewModel.selectedNetwork.value != null
            binding.btnStopTest.isEnabled = isTesting

            if (isTesting) {
                // ── Show animated indicators ──
                binding.tvStatus.text = getString(R.string.testing_in_progress)
                binding.tvStatus.setTextColor(
                    resources.getColor(R.color.purple_200, null)
                )
                binding.spinnerTesting.visibility = View.VISIBLE
                binding.viewPulseDot.visibility = View.VISIBLE
                binding.viewPulseDot.startAnimation(pulseAnim)
                binding.cardProgress.strokeWidth = 3
                binding.cardProgress.strokeColor =
                    resources.getColor(R.color.purple_200, null)

                // Show current password + ETA
                binding.tvCurrentPassword.visibility = View.VISIBLE
                binding.tvEta.visibility = View.VISIBLE
            } else {
                // ── Reset to idle ──
                binding.tvStatus.text = getString(R.string.ready)
                binding.tvStatus.setTextColor(Color.WHITE)
                binding.spinnerTesting.visibility = View.GONE
                binding.viewPulseDot.clearAnimation()
                binding.viewPulseDot.visibility = View.GONE
                binding.cardProgress.strokeWidth = 0
                binding.tvCurrentPassword.visibility = View.GONE
                binding.tvEta.visibility = View.GONE
            }
        }

        // Progress observer
        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            val total = viewModel.total.value ?: 0
            binding.progressBar.max = total
            binding.progressBar.progress = progress
            binding.tvProgress.text = "$progress / $total"

            // ETA calculation (~3.5s per attempt: 3s timeout + 0.5s delay)
            if (progress > 0 && total > 0) {
                val remaining = total - progress
                val etaSeconds = remaining * 3
                binding.tvEta.text = when {
                    etaSeconds < 60 -> "~${etaSeconds}s remaining"
                    else -> "~${etaSeconds / 60}m ${etaSeconds % 60}s remaining"
                }
            }
        }

        // Current password observer
        viewModel.currentPassword.observe(viewLifecycleOwner) { password ->
            if (password.isNotEmpty()) {
                binding.tvCurrentPassword.text = "${getString(R.string.testing_label)} $password"
            }
        }

        // Result observer
        viewModel.result.observe(viewLifecycleOwner) { result ->
            val progress = viewModel.progress.value ?: 0
            val isTesting = viewModel.isTesting.value == true

            when {
                result != null -> {
                    // ── SUCCESS ──
                    val ssid = viewModel.selectedNetwork.value?.ssid ?: "Network"
                    binding.tvResult.text = "\u2705 ${getString(R.string.password_found)}: $result"
                    binding.tvResult.setTextColor(
                        resources.getColor(R.color.signal_strong, null)
                    )
                    binding.tvResult.visibility = View.VISIBLE

                    // In-app dialog
                    showResultDialog(
                        success = true,
                        ssid = ssid,
                        password = result
                    )

                    // System notification
                    NotificationHelper.showSuccess(requireContext(), ssid, result)
                }
                !isTesting && progress > 0 -> {
                    // ── FAILURE ──
                    val ssid = viewModel.selectedNetwork.value?.ssid ?: "Network"
                    val dictName = "dictionary"
                    binding.tvResult.text = "\u274C ${getString(R.string.password_not_found)}"
                    binding.tvResult.setTextColor(
                        resources.getColor(R.color.signal_none, null)
                    )
                    binding.tvResult.visibility = View.VISIBLE

                    // In-app dialog
                    showResultDialog(
                        success = false,
                        ssid = ssid,
                        password = null
                    )

                    // System notification
                    NotificationHelper.showFailure(requireContext(), ssid, dictName)
                }
                else -> binding.tvResult.visibility = View.GONE
            }
        }

        // Engine status + speed badge
        viewModel.engineStatus.observe(viewLifecycleOwner) { status ->
            binding.tvEngineStatus.text = "Engine: $status"
            updateEngineBadge(status)
        }
    }

    /**
     * Shows colored badge based on engine speed.
     * 🟢 Fast: CmdWifi, WifiConfiguration (Shizuku)
     * 🟡 Medium: DeviceOwner
     * 🔴 Slow: Suggestion+A11y, Manual
     */
    private fun updateEngineBadge(engineName: String) {
        val (badgeText, badgeColor) = when {
            engineName.contains("Cmd", ignoreCase = true) ||
            engineName.contains("WifiConfig", ignoreCase = true) ->
                "● Fast" to resources.getColor(R.color.signal_strong, null)

            engineName.contains("DeviceOwner", ignoreCase = true) ->
                "● Medium" to resources.getColor(R.color.signal_weak, null)

            engineName.contains("Suggestion", ignoreCase = true) ||
            engineName.contains("Manual", ignoreCase = true) ||
            engineName.contains("Accessibility", ignoreCase = true) ->
                "● Slow" to resources.getColor(R.color.signal_none, null)

            else -> null to null
        }

        if (badgeText != null && badgeColor != null) {
            binding.tvEngineBadge.text = badgeText
            binding.tvEngineBadge.setBackgroundColor(badgeColor)
            binding.tvEngineBadge.visibility = View.VISIBLE
        } else {
            binding.tvEngineBadge.visibility = View.GONE
        }
    }

    /**
     * Shows an AlertDialog with the test result.
     * ✅ Success: green title, displays found password with Copy button.
     * ❌ Failure: red title, suggests trying another dictionary.
     */
    private fun showResultDialog(success: Boolean, ssid: String, password: String?) {
        if (!isAdded || activity == null) return

        val ctx = requireContext()

        if (success && password != null) {
            AlertDialog.Builder(ctx)
                .setTitle("✅ Tìm thấy mật khẩu!")
                .setMessage(
                    "Mạng WiFi: $ssid\n\nMật khẩu:\n\n$password"
                )
                .setPositiveButton("Sao chép") { dialog, _ ->
                    val clipboard = ctx.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                            as android.content.ClipboardManager
                    clipboard.setPrimaryClip(
                        android.content.ClipData.newPlainText("WiFi Password", password)
                    )
                    Toast.makeText(ctx, "Đã sao chép mật khẩu!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Đóng") { dialog, _ -> dialog.dismiss() }
                .setCancelable(true)
                .show()
        } else {
            AlertDialog.Builder(ctx)
                .setTitle("❌ Không tìm thấy mật khẩu")
                .setMessage(
                    "Mạng WiFi: $ssid\n\nĐã kiểm tra toàn bộ dictionary nhưng không tìm thấy mật khẩu phù hợp.\n\n💡 Thử chọn dictionary khác hoặc thêm file từ điển tùy chỉnh."
                )
                .setPositiveButton("Chọn dictionary khác") { dialog, _ ->
                    dialog.dismiss()
                    showDictionaryBottomSheet()
                }
                .setNegativeButton("Đóng") { dialog, _ -> dialog.dismiss() }
                .setCancelable(true)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.viewPulseDot?.clearAnimation()
        viewModel.stopTest()
        _binding = null
    }

    companion object {
        private const val FILE_PICKER_REQUEST = 2001
    }
}