package com.randomappsinc.irandom.presentation

import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.randomappsinc.irandom.R
import com.randomappsinc.irandom.utils.PreferencesManager

class PresentationColorChooserDialog(
        private val fragmentActivity: FragmentActivity,
        preferencesManager: PreferencesManager
) {

    private var dialog: ColorChooserDialog
    private val textNormalColor = ContextCompat.getColor(fragmentActivity, R.color.text_normal)

    init {
        dialog = ColorChooserDialog.Builder(fragmentActivity, R.string.set_text_color_title)
                .dynamicButtonColor(false)
                .preselect(preferencesManager.getPresentationTextColor(textNormalColor))
                .build()
    }

    fun show() {
        dialog.show(fragmentActivity)
    }
}