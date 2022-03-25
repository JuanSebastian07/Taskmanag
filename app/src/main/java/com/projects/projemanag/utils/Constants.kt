package com.projects.projemanag.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val USERS : String = "users"
    const val IMAGE : String = "image"
    const val NAME : String = "name"
    const val MOBILE : String = "mobile"
    const val ASSIGNED_TO : String = "assignedTo"
    const val BOARDS : String = "boards"
    const val DOCUMENT_ID : String = "documentId"
    const val TASK_LIST: String = "taskList"
    const val BOARD_DETAIL: String = "board_detail"
    const val ID : String = "id"
    const val EMAIL : String = "email"
    const val BOARD_MEMBER_LIST: String = "board_members_list"
    const val TASK_LIST_ITEM_POSITION : String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION : String = "card_list_item_position"
    const val BOARD_MEMBERS_LIST : String = "board_members_list"
    const val SELECT : String = "Select"
    const val UN_SELECT : String = "UnSelect"
    const val PROJEMANAG_PREFERENCES = "ProjemanagePrefs"
    const val FCM_TOKEN_UPDATE = "fcmTokenUpdate"
    const val FCM_TOKEN = "fcmToken"

    // TODO (Add the base url  and key params for sending firebase notification.)
    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = "AAAARuPwbEg:APA91bGPJrcct-tYx1NUhQJI0f7hob_ZYlNbeabEaPl8HnePWdMXbYGK_tOWbE2BzJvdUpX50SaePqBpEzbQvJXR2AFbzYS6BQ1VZFGGxtysozSTTw3z9kW7TYwfBuItusvAZHDQjEPI"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"
}

    /**
     * A function to get the extension of selected image.
     */
    fun getFileExtension(activity: Activity, uri : Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
}