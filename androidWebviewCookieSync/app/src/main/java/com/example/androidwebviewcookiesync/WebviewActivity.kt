package com.example.androidwebviewcookiesync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.webkit.WebView

class WebviewActivity : AppCompatActivity() {

    private lateinit var webview: WebView
    companion object {
        const val URL = "URL-KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        webview = findViewById(R.id.webview_main)
        if (savedInstanceState != null) {
            webview.restoreState(savedInstanceState)
        } else {
            intent.getStringExtra(URL)?.let {
                webview.loadUrl(it)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webview.saveState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
