package com.jidogoon.pdfrendererview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.createBitmap
import android.graphics.BitmapFactory
import android.os.Build
import java.io.*


/**
 * Created by jidogoon on 2018. 3. 16..
 */
class PdfRendererCore(private val context: Context, pdfFile: File, private val quality: Quality) {
    private val cachePath = "___pdf___cache___"
    private lateinit var pdfRenderer: PdfRenderer

    init {
        initCache()
        openPdfFile(pdfFile)
    }

    private fun initCache() {
        val cache = File(context.cacheDir, cachePath)
        if (cache.exists())
            cache.deleteRecursively()
        cache.mkdirs()
    }

    private fun getBitmapFromCache(pageNo: Int): Bitmap? {
        val loadPath = File(File(context.cacheDir, cachePath), pageNo.toString())
        if (!loadPath.exists())
            return null

        return BitmapFactory.decodeFile(loadPath.absolutePath)
    }

    @Throws(IOException::class)
    private fun writeBitmapToCache(pageNo: Int, bitmap: Bitmap) {
        val savePath = File(File(context.cacheDir, cachePath), pageNo.toString())
        savePath.createNewFile()
        val fos = FileOutputStream(savePath)
        bitmap.compress(CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Throws(IOException::class)
    private fun openPdfFile(pdfFile: File) {
        val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun getPageCount(): Int = pdfRenderer.pageCount

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun renderPage(imageView: ImageView, pageNo: Int) {
        if (pageNo >= getPageCount())
            return

        var bitmap = getBitmapFromCache(pageNo)
        bitmap?.let {
            imageView.setImageBitmap(bitmap)
            return@renderPage
        }

        val pdfPage = pdfRenderer.openPage(pageNo)
        bitmap = createBitmap(pdfPage.width * quality.ratio, pdfPage.height * quality.ratio, Bitmap.Config.ARGB_8888)
        pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        pdfPage.close()
        imageView.setImageBitmap(bitmap)
        writeBitmapToCache(pageNo, bitmap)
    }
}