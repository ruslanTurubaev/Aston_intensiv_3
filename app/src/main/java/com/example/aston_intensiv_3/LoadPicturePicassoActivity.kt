package com.example.aston_intensiv_3

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

private const val IS_LOADED = "IS_PICTURE_DOWNLOADED"
private const val LINK = "LINK"

class LoadPicturePicassoActivity : AppCompatActivity() {
    private val progressBar by find<ProgressBar>(R.id.progressBar_picasso)
    private val editTextLink by find<EditText>(R.id.edit_text_link_picasso)
    private val imageViewLoadedPicture by find<ImageView>(R.id.image_view_loaded_picture_picasso)
    private var isPictureLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_picture_picasso)

        if(savedInstanceState != null){
            isPictureLoaded = savedInstanceState.getBoolean(IS_LOADED)
            if(isPictureLoaded){
                val link = savedInstanceState.getString(LINK)
                Picasso.get().load(link).into(imageViewLoadedPicture)
            }
        }

        editTextLink.setOnEditorActionListener { textView, i, keyEvent ->
            if(i == EditorInfo.IME_ACTION_DONE || keyEvent.keyCode == KeyEvent.KEYCODE_ENTER){
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(textView.windowToken, 0)

                val link = textView.text.toString()

                if(link.isNotEmpty()) {
                    progressBar.visibility = View.VISIBLE
                    Picasso.get().load(textView.text.toString()).into(imageViewLoadedPicture, object : Callback{
                        override fun onSuccess() {
                            isPictureLoaded = true
                            progressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            isPictureLoaded = false
                            progressBar.visibility = View.GONE
                            Toast.makeText(baseContext, R.string.Toast, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                else{
                    Toast.makeText(this, R.string.Toast_empty_link, Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_LOADED, isPictureLoaded)
        if(isPictureLoaded) {
            outState.putString(LINK, editTextLink.text.toString())
        }
    }
}