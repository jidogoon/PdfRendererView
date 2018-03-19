package com.jidogoon.pdfredererview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jidogoon.pdfrendererview.PdfRendererView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pdfView.statusListener = object: PdfRendererView.StatusCallBack {
            override fun onDownloadStart() {
                loading.visibility = View.VISIBLE
            }
            override fun onDownloadSuccess() {
                loading.visibility = View.GONE
            }
            override fun onError(error: Throwable) {
                loading.visibility = View.GONE
            }
        }
        pdfView.initWithUrl("https://s3.ap-northeast-2.amazonaws.com/wanted-www/events/145/Wanted_Resume_Coach_List.pdf")
        //pdfView.initWithUrl("https://www.cs.toronto.edu/~hinton/absps/fastnc.pdf")
    }
}
