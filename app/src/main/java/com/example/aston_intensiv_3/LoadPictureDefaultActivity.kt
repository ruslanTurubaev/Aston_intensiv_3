package com.example.aston_intensiv_3

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.LruCache
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.net.URL

private const val IS_LOADED = "IS_PICTURE_DOWNLOADED"
private const val PICTURE = "PICTURE"

class LoadPictureDefaultActivity : AppCompatActivity() {
    private val progressBar by find<ProgressBar>(R.id.progressBar_default)
    private val editTextLink by find<EditText>(R.id.edit_text_link_default)
    private val imageViewLoadedPicture by find<ImageView>(R.id.image_view_loaded_picture_default)

    private var isPictureLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_picture_default)

        if(savedInstanceState != null){
            isPictureLoaded = savedInstanceState.getBoolean(IS_LOADED)

            if (isPictureLoaded){
                try {
                    CoroutineScope(Dispatchers.Default).launch {
                        val byteArray = savedInstanceState.getByteArray(PICTURE)
                        val loadedPicture =
                            byteArray?.let { BitmapFactory.decodeByteArray(byteArray, 0, it.size) }

                        withContext(Dispatchers.Main){
                            imageViewLoadedPicture.setImageBitmap(loadedPicture)
                        }
                    }
                }
                catch (exception : Exception){
                    Toast.makeText(this, R.string.Toast_failed_picture_recovery, Toast.LENGTH_SHORT).show()
                }
            }
        }

        editTextLink.setOnEditorActionListener { textView, i, keyEvent ->
            if(i == EditorInfo.IME_ACTION_DONE || keyEvent.keyCode == KeyEvent.KEYCODE_ENTER){
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(textView.windowToken, 0)

                startTransaction(textView.text.toString())
            }
            true
        }
    }

    private fun startTransaction(link : String) {
        if(link.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            try {
                val url = URL(link)
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val picture: Bitmap
                        withContext(Dispatchers.IO) {
                            picture = downloadPicture(url)
                        }
                        setPicture(picture, link)
                    } catch (exception: Exception) {
                        isPictureLoaded = false
                        progressBar.visibility = View.GONE
                        Toast.makeText(baseContext, R.string.Toast, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            catch (exception : Exception){
                isPictureLoaded = false
                progressBar.visibility = View.GONE
                Toast.makeText(baseContext, R.string.Toast, Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this, R.string.Toast_empty_link, Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadPicture(url : URL) : Bitmap{
        val inputStream = url.openConnection().getInputStream()
        val picture = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        return picture
    }

    private fun setPicture(picture : Bitmap, link : String){
        progressBar.visibility = View.GONE
        imageViewLoadedPicture.setImageBitmap(picture)

        isPictureLoaded = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_LOADED, isPictureLoaded)
        if(isPictureLoaded){
            val picture = imageViewLoadedPicture.drawable.toBitmap()
            val outputStream = ByteArrayOutputStream()
            picture.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            outputStream.close()
            outState.putByteArray(PICTURE, byteArray)
        }
    }
}

