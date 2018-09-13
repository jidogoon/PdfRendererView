package com.jidogoon.pdfredererview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jidogoon.pdfrendererview.PdfRendererView
import com.jidogoon.pdfrendererview.Quality
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pdfView.statusListener = object: PdfRendererView.StatusCallBack {
            override fun onDownloadStart() {
                loading.visibility = View.VISIBLE
            }
            override fun onDownloadProgress(progress: Int, downloadedBytes: Long, totalBytes: Long?) {
                super.onDownloadProgress(progress, downloadedBytes, totalBytes)
                println("onDownloadProgress = $progress% $downloadedBytes/$totalBytes")
                title = "Downloading... $progress%"
            }
            override fun onDownloadSuccess() {
                loading.visibility = View.GONE
            }
            override fun onError(error: Throwable) {
                loading.visibility = View.GONE
            }

            override fun onPageChanged(currentPage: Int, totalPage: Int) {
                super.onPageChanged(currentPage, totalPage)
                println("onPageChanged = $currentPage/$totalPage")
                title = "${currentPage+1}/$totalPage"
            }
        }
        pdfView.initWithUrl("https://www.cs.toronto.edu/~hinton/absps/fastnc.pdf", Quality.NORMAL)
        //pdfView.initWithUrl("https://s3.ap-northeast-2.amazonaws.com/wanted-www/events/205/rcs1_coach_list.pdf", Quality.NORMAL)
        //pdfView.initWithUrl("https://www.office.xerox.com/latest/SFTBR-04U.PDF")
    }
}
