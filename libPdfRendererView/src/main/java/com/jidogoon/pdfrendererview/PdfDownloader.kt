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
        fun onDownloadProgress(currentBytes: Long, totalBytes: Long) {}
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
            val bufferSize = 8192
            val url = URL(downloadUrl)
            val connection = url.openConnection()
            connection.connect()

            val totalLength = connection.contentLength
            val inputStream = BufferedInputStream(url.openStream(), bufferSize)
            val outputStream = outputFile.outputStream()
            var downloaded = 0

            do {
                val data = ByteArray(bufferSize)
                val count = inputStream.read(data)
                if (count == -1)
                    break
                if (totalLength > 0) {
                    downloaded += bufferSize
                    if (BuildConfig.DEBUG)
                        println("downloaded = $downloaded/$totalLength")
                    launch(UI) { listener.onDownloadProgress(downloaded.toLong(), totalLength.toLong()) }
                }
                outputStream.write(data, 0, count)
            } while (true)
        }
        catch (e: Exception) {
            e.printStackTrace()
            launch(UI) { listener.onError(e) }
            return
        }
        launch(UI) { listener.onDownloadSuccess(outputFile.absolutePath) }
    }
}