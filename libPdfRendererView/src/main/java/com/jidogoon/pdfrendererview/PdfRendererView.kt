package com.jidogoon.pdfrendererview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.support.v7.widget.*
import android.support.v7.widget.RecyclerView.NO_POSITION
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
import java.net.URLEncoder

/**
 * Created by jidogoon on 2018. 3. 16..
 */
class PdfRendererView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pdfRendererCore: PdfRendererCore
    private lateinit var pdfViewAdapter: PdfViewAdapter
    private var quality = Quality.NORMAL
    private var engine = Engine.DEFAULT
    private var showDivider = true

    var statusListener: StatusCallBack? = null
    val totalPageCount: Int
    get() { return pdfRendererCore.getPageCount() }

    interface StatusCallBack {
        fun onDownloadStart() {}
        fun onDownloadProgress(progress: Int, downloadedBytes: Long, totalBytes: Long?) {}
        fun onDownloadSuccess() {}
        fun onError(error: Throwable) {}
        fun onPageChanged(currentPage: Int, totalPage: Int) {}
    }

    fun initWithUrl(url: String, quality: Quality = this.quality, engine: Engine = this.engine) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || engine == Engine.GOOGLE) {
            initUnderKitkat(url)
            statusListener?.onDownloadStart()
            return
        }

        PdfDownloader(url, object : PdfDownloader.StatusListener {
            override fun getContext(): Context = context
            override fun onDownloadStart() {
                statusListener?.onDownloadStart()
            }
            override fun onDownloadProgress(currentBytes: Long, totalBytes: Long) {
                var progress = (currentBytes.toFloat() / totalBytes.toFloat() * 100F).toInt()
                if (progress >= 100)
                    progress = 100
                statusListener?.onDownloadProgress(progress, currentBytes, totalBytes)
            }
            override fun onDownloadSuccess(absolutePath: String) {
                initWithPath(absolutePath, quality)
                statusListener?.onDownloadSuccess()
            }
            override fun onError(error: Throwable) {
                error.printStackTrace()
                statusListener?.onError(error)
            }
        })
    }

    fun initWithPath(path: String, quality: Quality = this.quality) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            throw UnsupportedOperationException("should be over API 21")
        initWithFile(File(path), quality)
    }

    fun initWithFile(file: File, quality: Quality = this.quality) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            throw UnsupportedOperationException("should be over API 21")
        init(file, quality)
    }

    private fun init(file: File, quality: Quality) {
        pdfRendererCore = PdfRendererCore(context, file, quality)
        pdfViewAdapter = PdfViewAdapter(pdfRendererCore)
        val v = LayoutInflater.from(context).inflate(R.layout.layout_lib_pdf_rendererview, this, false)
        addView(v)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.apply {
            adapter = pdfViewAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            if (showDivider)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            addOnScrollListener(scrollListener)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            (recyclerView.layoutManager as LinearLayoutManager).run {
                var foundPosition = findFirstCompletelyVisibleItemPosition()
                if (foundPosition != NO_POSITION) {
                    statusListener?.onPageChanged(foundPosition, totalPageCount)
                    return@run
                }
                foundPosition = findFirstVisibleItemPosition()
                if (foundPosition != NO_POSITION) {
                    statusListener?.onPageChanged(foundPosition, totalPageCount)
                    return@run
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initUnderKitkat(url: String) {
        val v = LayoutInflater.from(context).inflate(R.layout.layout_lib_pdf_rendererview, this, false)
        addView(v)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.visibility = View.GONE
        webView.visibility = View.VISIBLE
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = PdfWebViewClient(statusListener)
        webView.loadUrl("https://drive.google.com/viewer/viewer?hl=en&embedded=true&url=${URLEncoder.encode(url, "UTF-8")}")
    }

    internal class PdfWebViewClient(private val statusListener: StatusCallBack?): WebViewClient() {
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

    init {
        getAttrs(attrs, defStyleAttr)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyle: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PdfRendererView, defStyle, 0)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val ratio = typedArray.getInt(R.styleable.PdfRendererView_quality, Quality.NORMAL.ratio)
        quality = Quality.values().first { it.ratio == ratio }
        val engineValue = typedArray.getInt(R.styleable.PdfRendererView_engine, Engine.DEFAULT.value)
        engine = Engine.values().first { it.value == engineValue }
        showDivider = typedArray.getBoolean(R.styleable.PdfRendererView_showDivider, true)

        typedArray.recycle()
    }
}