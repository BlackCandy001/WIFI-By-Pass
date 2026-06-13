package com.widt.tester

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.widt.R

class DictionaryBottomSheet : BottomSheetDialogFragment() {

    var onDictionarySelected: ((String, String) -> Unit)? = null
    var onFilePickerRequested: (() -> Unit)? = null

    private data class DictOption(
        val fileName: String,
        val displayName: String,
        val description: String,
        val entryCount: Int
    )

    private val options = listOf(
        DictOption("top400",    "top400.txt",         "Mật khẩu phổ biến toàn cầu",       727),
        DictOption("vietnam",   "vietnam.txt",         "Mật khẩu phổ biến tại Việt Nam",    25),
        DictOption("router_defaults", "router_defaults.txt", "Mật khẩu mặc định router",   30)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.layout_dictionary_bottom_sheet, container, false)

        val radioGroup = view.findViewById<RadioGroup>(R.id.rg_dictionaries)

        options.forEachIndexed { index, option ->
            val radio = RadioButton(requireContext()).apply {
                id = View.generateViewId()
                text = "${option.displayName}  (${option.entryCount} entries)\n${option.description}"
                setPadding(8, 16, 8, 16)
                textSize = 13f
                tag = option
            }
            radioGroup.addView(radio)
            if (index == 0) radio.isChecked = true
        }

        view.findViewById<View>(R.id.btn_file_picker).setOnClickListener {
            onFilePickerRequested?.invoke()
            dismiss()
        }

        view.findViewById<View>(R.id.btn_confirm).setOnClickListener {
            val selected = radioGroup.checkedRadioButtonId
            val radioBtn = view.findViewById<RadioButton>(selected)
            val option = radioBtn?.tag as? DictOption
            if (option != null) {
                onDictionarySelected?.invoke(option.fileName, option.displayName)
            }
            dismiss()
        }

        view.findViewById<View>(R.id.btn_cancel).setOnClickListener {
            dismiss()
        }

        return view
    }

    companion object {
        const val TAG = "DictionaryBottomSheet"
    }
}
