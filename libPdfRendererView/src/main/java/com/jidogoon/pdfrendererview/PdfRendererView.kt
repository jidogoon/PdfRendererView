package com.jidogoon.pdfrendererview

import android.content.Context
import android.support.v7.widget.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import java.io.File

/**
 * Created by jidogoon on 2018. 3. 16..
 */
class PdfRendererView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pdfRendererCore: PdfRendererCore
    private lateinit var pdfViewAdapter: PdfViewAdapter
    var statusListener: StatusCallBack? = null

    interface StatusCallBack {
        fun onDownloadStart() {}
        fun onDownloadSuccess() {}
        fun onError(error: Throwable) {}
    }

    fun initWithUrl(url: String) {
        PdfDownloader(url, object : PdfDownloader.StatusListener {
            override fun getContext(): Context = context
            override fun onDownloadStart() {
                statusListener?.onDownloadStart()
            }
            override fun onDownloadSuccess(absolutePath: String) {
                initWithPath(absolutePath)
                statusListener?.onDownloadSuccess()
            }
            override fun onError(error: Throwable) {
                error.printStackTrace()
                statusListener?.onError(error)
            }
        })
    }

    fun initWithPath(path: String) {
        initWithFile(File(path))
    }

    fun initWithFile(file: File) {
        init(file)
    }

    private fun init(file: File) {
        pdfRendererCore = PdfRendererCore(context, file)
        pdfViewAdapter = PdfViewAdapter(pdfRendererCore)
        val v = LayoutInflater.from(context).inflate(R.layout.layout_lib_pdf_rendererview, this, false)
        addView(v)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.apply {
            adapter = pdfViewAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }
}