package com.jidogoon.pdfredererview

import android.content.Context
import android.os.ParcelFileDescriptor
import com.jidogoon.pdfrendererview.PdfDownloader
import com.jidogoon.pdfrendererview.PdfRendererCore
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito.mock
import java.io.File
import java.io.FileDescriptor

/**
 * Created by jidogoon on 2018. 3. 19..
 */
class PdfRendererViewTest {
    @Test
    fun downloadTest() {
        val listener = object : PdfDownloader.StatusListener {
            override fun getContext(): Context = mock(Context::class.java)
            override fun onDownloadStart() {
                super.onDownloadStart()
                print("onDownloadStart")
            }

            override fun onDownloadSuccess(absolutePath: String) {
                super.onDownloadSuccess(absolutePath)
                print("onDownloadSuccess")
            }

            override fun onError(error: Throwable) {
                super.onError(error)
                print("onError")
            }
        }
        PdfDownloader("https://s3.ap-northeast-2.amazonaws.com/wanted-www/events/145/Wanted_Resume_Coach_List.pdf", listener)
    }

    @Test
    fun initializeTest() {
        val pdfFile = File("Wanted_Resume_Coach_List.pdf")
        val pdfRendererCore = PdfRendererCore(mock(Context::class.java), pdfFile)
        Assert.assertEquals(pdfRendererCore.getPageCount(), 18)
    }
}