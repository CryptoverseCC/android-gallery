package io.userfeeds.gallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addImage.setOnClickListener { pickImage() }
        openGallery.setOnClickListener { openGallery() }
        configure.setOnClickListener { configure() }
    }

    private fun pickImage() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        startActivityForResult(pickIntent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            val inputStream = contentResolver.openInputStream(data!!.data)
            val outputStream = openFileOutput("image", Context.MODE_PRIVATE)
            inputStream.copyTo(outputStream)
            val file = File(filesDir, "image")
            val intent = Intent(this, LabelImageActivity::class.java)
            intent.putExtra("file", file)
            startActivity(intent)
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun openGallery() {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }

    private fun configure() {
        val intent = Intent(this, ConfigurationActivity::class.java)
        startActivity(intent)
    }

    companion object {

        private const val PICK_IMAGE = 1
    }
}
