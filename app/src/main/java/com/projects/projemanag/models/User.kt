package com.projects.projemanag.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User (
    val id : String = "",
    val name : String = "",
    val email: String = "",
    val password : String = "",
    val image: String = "",
    val mobile: Long = 0,
    val fcmToken: String = "",
    var selected : Boolean = false
) : Parcelable