package io.userfeeds.gallery

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.squareup.moshi.Moshi
import io.ipfs.kotlin.IPFS
import io.reactivex.android.schedulers.AndroidSchedulers
import io.userfeeds.sdk.core.UserfeedsService
import io.userfeeds.sdk.core.storage.Claim
import io.userfeeds.sdk.core.storage.ClaimWrapper
import io.userfeeds.sdk.core.storage.Signature
import kotlinx.android.synthetic.main.label_image_activity.*
import java.io.File
import kotlin.concurrent.thread

class LabelImageActivity : AppCompatActivity() {

    private val file by lazy { intent.getSerializableExtra("file") as File }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.label_image_activity)
        image.setImageURI(Uri.parse(file.absolutePath))
        submit.setOnClickListener { submit() }
    }

    private fun submit() {
        submit.isEnabled = false
        thread {
            val claimWrapperString = claimWrapper.toJson()
            runOnUiThread {
                val intent = Intent("io.userfeeds.identity.SIGN_MESSAGE")
                intent.putExtra("io.userfeeds.identity.message", claimWrapperString)
                startActivityForResult(intent, SIGN_REQUEST_CODE)
            }
        }
    }

    private val claimWrapper: ClaimWrapper get() {
        val labels = labelsView.text.split(",").map { it.trim() }.filterNot { it.isEmpty() }
        val hash = IPFS("http://207.154.252.54:57775/api/v0/").add.file(file).Hash
        val ipfsUri = "fs:/ipfs/$hash"
        return ClaimWrapper.create(
                context = UserfeedIdPrefs(this).load(),
                type = if (labels.isNotEmpty()) listOf("labels") else emptyList(),
                claim = Claim(
                        target = ipfsUri,
                        labels = if (labels.isNotEmpty()) labels else null
                ),
                clientId = "android:io.userfeeds.gallery")
    }

    private inline fun <reified T> T.toJson(): String {
        return Moshi.Builder()
                .build()
                .adapter(T::class.java)
                .toJson(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SIGN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val signature = Signature.fromIntentData(data!!)
            postClaim(signature)
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun postClaim(signature: Signature) {
        thread {
            UserfeedsService.get().putClaim(claimWrapper, signature)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { submit.isEnabled = false }
                    .doOnError { submit.isEnabled = true }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onSuccess, this::onError)
        }
    }

    private fun onSuccess() {
        finish()
    }

    private fun onError(error: Throwable) {
        Log.e("TAG", "error", error)
    }

    companion object {

        const private val SIGN_REQUEST_CODE = 1001
    }
}
