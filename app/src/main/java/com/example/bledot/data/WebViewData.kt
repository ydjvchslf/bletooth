package com.example.bledot.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WebViewData(
    val meaId: String,
    val daId: String,
    val spId: String
): Parcelable
