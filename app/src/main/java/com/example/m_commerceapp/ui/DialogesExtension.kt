package com.example.m_commerceapp.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.view.View
import androidx.fragment.app.Fragment
import com.example.m_commerceapp.R
import com.example.m_commerceapp.ui.OnDialogActionClick
import com.google.android.material.snackbar.Snackbar

fun Activity.showMessage(
    message: String,
    posActionName: String? = null,
    posAction: OnDialogActionClick? = null,
    negActionName: String? = null,
    negAction: OnDialogActionClick? = null,
    isCancelable: Boolean = true
): AlertDialog {
    val dialogBuilder = AlertDialog.Builder(this)
    dialogBuilder.setMessage(message)
    if (posActionName != null) {
        dialogBuilder.setPositiveButton(posActionName)
        { dialog, id ->
            dialog.dismiss()
            posAction?.onActionClick()
        }
    }
    if (negActionName != null) {
        dialogBuilder.setNegativeButton(negActionName)
        { dialog, id ->
            dialog.dismiss()
            negAction?.onActionClick()
        }
    }
    dialogBuilder.setCancelable(isCancelable)
    return dialogBuilder.show()
}

fun Fragment.showMessage(
    message: String,
    posActionName: String? = null,
    posAction: DialogInterface.OnClickListener? = null,
    negActionName: String? = null,
    negAction: DialogInterface.OnClickListener? = null
): AlertDialog {
    val dialogBuilder = AlertDialog.Builder(context)
    dialogBuilder.setMessage(message)
    if (posActionName != null) {
        dialogBuilder.setPositiveButton(posActionName, posAction)
    }
    if (negActionName != null) {
        dialogBuilder.setNegativeButton(negActionName, negAction)
    }
    return dialogBuilder.show()
}

fun Activity.showLoadingProgressDialog(message: String, isCancelable: Boolean = true): AlertDialog{
    val alertDialog = ProgressDialog(this)
    alertDialog.setMessage(message)
    alertDialog.setCancelable(isCancelable)
    return alertDialog
}

fun Fragment.showSnackBar(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
        .setTextColor(requireContext().getColor(R.color.primary_color))
        .setBackgroundTint(requireContext().getColor(R.color.white))
        .show()
}

fun View.showActivitySnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
        .setTextColor(resources.getColor(R.color.primary_color))
        .setBackgroundTint(resources.getColor(R.color.white))
        .show()
}