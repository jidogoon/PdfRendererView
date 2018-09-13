package com.jidogoon.pdfrendererview

import android.content.Context
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.BufferedInputStream
import java.io.File
import java.net.URL

/**
 * Created by jidogoon on 2018. 3. 16..
 */
class PdfDownloader(url: String, private val listener: StatusListener) {
    interface StatusListener {
        fun getContext(): Context
        fun onDownloadStart() {}
        fun onDownloadSuccess(absolutePath: String) {}
        fun onError(error: Throwable) {}
    }

    init {
        launch { download(url) }
    }

    private fun download(downloadUrl: String) {
        launch(UI) { listener.onDownloadStart() }
        val outputFile = File(listener.getContext().cacheDir, "downloaded_pdf.pdf")
        if (outputFile.exists())
            outputFile.delete()
        try {
            val url = URL(downloadUrl)
            val connection = url.openConnection()
            connection.connect()
            val inputStream = BufferedInputStream(url.openStream(), 8192)
            inputStream.use {
                outputFile.outputStream().use { inputStream.copyTo(it) }
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            launch(UI) { listener.onError(e) }
            return
        }
        launch(UI) { listener.onDownloadSuccess(outputFile.absolutePath) }
    }
}