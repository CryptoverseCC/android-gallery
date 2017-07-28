package io.userfeeds.gallery

import android.content.Context
import android.preference.PreferenceManager

class UserfeedIdPrefs(private val context: Context) {

    fun load(): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("userfeedId", null)
                ?: "userfeeds:gallery"
    }

    fun save(userfeedId: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("userfeedId", userfeedId)
                .apply()
    }
}