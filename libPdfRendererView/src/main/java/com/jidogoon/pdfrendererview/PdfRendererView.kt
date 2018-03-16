package com.jidogoon.pdfrendererview

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import kotlinx.coroutines.experimental.launch
import java.io.File

/**
 * Created by jidogoon on 2018. 3. 16..
 */
class PdfRendererView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private val recyclerView: RecyclerView
    private lateinit var pdfRendererCore: PdfRendererCore
    private lateinit var pdfViewAdapter: PdfViewAdapter

    init {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_lib_pdf_rendererview, this, false)
        recyclerView = v.findViewById(R.id.recyclerView)
    }

    fun initWithUrl(url: String) {
        PdfDownloader(url, object : PdfDownloader.StatusListener {
            override fun getContext(): Context = context
            override fun onDownloadSuccess(absolutePath: String) {
                initWithPath(absolutePath)
            }
        })
    }

    fun initWithPath(path: String) {
        initWithFile(File(path))
    }

    fun initWithFile(file: File) {
        launch {
            pdfRendererCore = PdfRendererCore(file)
            pdfViewAdapter = PdfViewAdapter(pdfRendererCore)
            recyclerView.apply {
                adapter = pdfViewAdapter
                //layoutManager = LinearLayoutManager(context)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                //setHasFixedSize(true)
            }
            pdfViewAdapter.notifyDataSetChanged()
        }
    }
}