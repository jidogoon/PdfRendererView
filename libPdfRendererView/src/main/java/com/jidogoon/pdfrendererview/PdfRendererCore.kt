package com.jidogoon.pdfrendererview

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import java.io.File
import java.io.IOException

/**
 * Created by jidogoon on 2018. 3. 16..
 */
class PdfRendererCore(pdfFile: File) {
    init {
        openPdfFile(pdfFile)
    }

    private lateinit var pdfRenderer: PdfRenderer

    @Throws(IOException::class)
    private fun openPdfFile(pdfFile: File) {
        val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor)
    }

    fun getPageCount(): Int = pdfRenderer.pageCount

    fun renderPage(imageView: ImageView, pageNo: Int) {
        if (pageNo >= getPageCount())
            return

        val pdfPage = pdfRenderer.openPage(pageNo)
        val bitmap = createBitmap(pdfPage.width, pdfPage.height, Bitmap.Config.ARGB_8888)
        pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        imageView.setImageBitmap(bitmap)
    }
}