package com.jidogoon.pdfrendererview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.createBitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.min


/**
 * Created by jidogoon on 2018. 3. 16..
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class PdfRendererCore(private val context: Context, pdfFile: File, private val quality: Quality) {
    companion object {
        private const val PREFETCH_COUNT = 3
    }
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

    @Throws(IOException::class)
    private fun openPdfFile(pdfFile: File) {
        val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor)
    }

    fun getPageCount(): Int = pdfRenderer.pageCount

    fun renderPage(pageNo: Int, onBitmapReady: ((bitmap: Bitmap?, pageNo: Int) -> Unit)? = null) {
        if (pageNo >= getPageCount())
            return

        launch {
            synchronized(this@PdfRendererCore) {
                buildBitmap(pageNo) { bitmap ->
                    launch(UI) { onBitmapReady?.invoke(bitmap, pageNo) }
                }
                onBitmapReady?.let {
                    prefetchNext(pageNo + 1)
                }
            }
        }
    }

    private fun prefetchNext(pageNo: Int) {
        val countForPrefetch = min(getPageCount(), pageNo + PREFETCH_COUNT)
        for (pageToPrefetch in pageNo until countForPrefetch) {
            renderPage(pageToPrefetch)
        }
    }

    private fun buildBitmap(pageNo: Int, onBitmap: (Bitmap?) -> Unit) {
        var bitmap = getBitmapFromCache(pageNo)
        bitmap?.let {
            onBitmap(it)
            return@buildBitmap
        }

        val startTime = System.currentTimeMillis()
        //println("building pdf page start = $pageNo")

        val pdfPage = pdfRenderer.openPage(pageNo)
        bitmap = createBitmap(pdfPage.width * quality.ratio, pdfPage.height * quality.ratio, Bitmap.Config.ARGB_8888)
        pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        pdfPage.close()
        writeBitmapToCache(pageNo, bitmap)

        val endTime = System.currentTimeMillis()
        //println("building pdf page done = $pageNo ${endTime - startTime}ms")
        onBitmap(bitmap)
    }
}