package com.example.qrized

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.qrized.databinding.ActivityMainBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


private const val TAG = "MainActivity"
private const val QR_SIZE = 1024
val appId = "com.example.qrized"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var qrBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerUiListener()
    }

    private fun registerUiListener() {
        binding.generateQrBtn.setOnClickListener {
            generateQrCode()
        }
        binding.shareQrBtn.setOnClickListener {
            shareQrCode()
        }
    }

    private fun generateQrCode() {
        val inputText = binding.qrTextEt.text.toString()
        try {
            val encoder = BarcodeEncoder()
            qrBitmap = encoder.encodeBitmap(inputText, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE)
            binding.generatedQrImage.setImageBitmap(qrBitmap)
        } catch (e: WriterException) {
            Log.e(TAG, "generateQrCode: ${e.message}")
        }
    }

    private fun shareQrCode() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, getUriFromBitmap())
            putExtra(Intent.EXTRA_TEXT, "Scan this code")
            type = "image/*"
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun getUriFromBitmap(): Uri? {
        try {

            val file = File(
                this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "qr_code_${System.currentTimeMillis()}.png"
            )
            val outStream = FileOutputStream(file)
            qrBitmap?.compress(Bitmap.CompressFormat.PNG, 90, outStream)
            return FileProvider.getUriForFile(this, "$appId.provider", file)
        } catch (e: IOException) {
            Log.e(TAG, "getUriFromBitmap: ${e.message}")
        }
        return null
    }

}