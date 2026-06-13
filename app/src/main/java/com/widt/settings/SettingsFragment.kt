package com.widt.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.widt.R
import com.widt.common.shizuku.ShizukuHelper
import com.widt.databinding.FragmentSettingsBinding
import com.widt.utils.LanguageHelper

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupShizuku()
        setupTheme()
        setupLanguage()
        setupDictionary()
    }

    private fun setupShizuku() {
        updateShizukuStatus()

        binding.btnSetupShizuku.setOnClickListener {
            if (ShizukuHelper.isShizukuRunning()) {
                if (!ShizukuHelper.hasPermission()) {
                    ShizukuHelper.requestPermission()
                } else {
                    Toast.makeText(requireContext(), "Shizuku is already running and authorized", Toast.LENGTH_SHORT).show()
                }
            } else {
                ShizukuHelper.openShizukuSettings(requireContext())
                binding.tvShizukuGuide.text = ShizukuHelper.getSetupGuide()
                binding.tvShizukuGuide.visibility = View.VISIBLE
            }
        }
    }

    private fun updateShizukuStatus() {
        val isRunning = ShizukuHelper.isShizukuRunning()
        binding.tvShizukuStatus.text = if (isRunning) {
            "Status: Running (${ShizukuHelper.getShizukuVersion()})"
        } else {
            "Status: Not running"
        }
    }

    private fun setupTheme() {
        val isDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        binding.switchDarkTheme.isChecked = isDark

        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setupLanguage() {
        // Restore saved selection
        when (LanguageHelper.getSavedLanguage(requireContext())) {
            LanguageHelper.LANG_VI -> binding.rgLanguage.check(R.id.rb_lang_vi)
            LanguageHelper.LANG_EN -> binding.rgLanguage.check(R.id.rb_lang_en)
            LanguageHelper.LANG_ZH -> binding.rgLanguage.check(R.id.rb_lang_zh)
        }

        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val langCode = when (checkedId) {
                R.id.rb_lang_vi -> LanguageHelper.LANG_VI
                R.id.rb_lang_en -> LanguageHelper.LANG_EN
                R.id.rb_lang_zh -> LanguageHelper.LANG_ZH
                else -> LanguageHelper.LANG_EN
            }
            LanguageHelper.setLanguage(requireContext(), langCode)
            // Restart activity to apply new locale across all UI
            Toast.makeText(requireContext(), "Language changed. Restarting...", Toast.LENGTH_SHORT).show()
            requireActivity().recreate()
        }
    }

    private fun setupDictionary() {
        binding.btnChangeDictionary.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/plain"
            }
            startActivityForResult(intent, DICT_PICKER_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DICT_PICKER_REQUEST && resultCode == android.app.Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val path = getPathFromUri(uri)
                if (path != null) {
                    binding.tvDictionaryPath.text = path
                    Toast.makeText(requireContext(), "Dictionary selected: $path", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPathFromUri(uri: Uri): String? {
        return try {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex("_display_name")
                if (it.moveToFirst()) {
                    return it.getString(nameIndex)
                }
            }
            uri.lastPathSegment
        } catch (e: Exception) {
            null
        }
    }

    override fun onResume() {
        super.onResume()
        updateShizukuStatus()
        
        if (ShizukuHelper.isShizukuRunning() && !ShizukuHelper.hasPermission()) {
            ShizukuHelper.requestPermission()
        }
    }
    
    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DICT_PICKER_REQUEST = 1001
    }
}