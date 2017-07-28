package io.userfeeds.gallery

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.configuration_activity.*

class ConfigurationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configuration_activity)
        val userfeedId = UserfeedIdPrefs(this).load()
        userfeedIdView.setText(userfeedId)
        submit.setOnClickListener { submit() }
    }

    private fun submit() {
        val userfeedId = userfeedIdView.text.toString().trim().takeIf { it.isNotEmpty() }
        UserfeedIdPrefs(this).save(userfeedId)
        finish()
    }
}
