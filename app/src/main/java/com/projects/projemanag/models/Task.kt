package com.projects.projemanag.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task (
    var title : String = "",
    val createdBy : String = "",
    var cards : ArrayList<Card> = ArrayList()
): Parcelable