package com.jidogoon.pdfrendererview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_lib_pdf_page.view.*

/**
 * Created by jidogoon on 2018. 3. 16..
 */
class PdfViewAdapter(private val renderer: PdfRendererCore): RecyclerView.Adapter<PdfViewAdapter.PdfPageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfPageViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_lib_pdf_page, parent, false)
        return PdfPageViewHolder(v)
    }

    override fun getItemCount(): Int {
        return renderer.getPageCount()
    }

    override fun onBindViewHolder(holder: PdfPageViewHolder, position: Int) {
        holder.bind()
    }

    inner class PdfPageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind() {
            renderer.renderPage(itemView.pdfPageView, adapterPosition)
        }
    }
}