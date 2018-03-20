package com.jidogoon.pdfrendererview

import android.content.Context
import android.os.Build
import android.support.v7.widget.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.layout_lib_pdf_rendererview.view.*
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            initUnderKitkat(url)
            statusListener?.onDownloadStart()
            return
        }

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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            throw UnsupportedOperationException("should be over API 21")
        initWithFile(File(path))
    }

    fun initWithFile(file: File) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            throw UnsupportedOperationException("should be over API 21")
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

    private fun initUnderKitkat(url: String) {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_lib_pdf_rendererview, this, false)
        addView(v)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.visibility = View.GONE
        webView.visibility = View.VISIBLE
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = PdfWebViewClient()
        webView.loadUrl("https://drive.google.com/viewer/viewer?hl=en&embedded=true&url=$url")
    }

    inner class PdfWebViewClient: WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            statusListener?.onDownloadSuccess()
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            statusListener?.onError(Throwable("Web resource error"))
        }

        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            statusListener?.onError(Throwable("Web resource error"))
        }
    }
}