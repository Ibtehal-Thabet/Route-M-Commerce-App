package com.example.m_commerceapp.ui

data class Message(
    val message: String? = null,
    val posActionName: String? = null,
    val onPosActionClick: OnDialogActionClick? = null,
    val negActionName: String? = null,
    val onNegActionClick: OnDialogActionClick? = null,
    val isCancelable: Boolean = true
)


fun interface OnDialogActionClick {
    fun onActionClick()
}