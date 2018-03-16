package com.jidogoon.pdfredererview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pdfView.initWithUrl("https://s3.ap-northeast-2.amazonaws.com/wanted-www/events/145/Wanted_Resume_Coach_List.pdf")
    }
}
