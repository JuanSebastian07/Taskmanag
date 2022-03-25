package com.projects.projemanag.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class SelectedMembers(
    val id : String = "",
    val image : String = ""
): Parcelable