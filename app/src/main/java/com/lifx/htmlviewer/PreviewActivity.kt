package com.lifx.htmlviewer

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.webkit.*
import android.widget.*
import android.content.Intent

class PreviewActivity : Activity() {
    private lateinit var web: WebView
    private lateinit var address: TextView
    private val dark = Color.rgb(8, 11, 13)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uriString = intent.getStringExtra("uri") ?: intent.data?.toString()
        val name = intent.getStringExtra("name") ?: "Preview HTML"
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(dark) }
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(10), dp(12), dp(10))
        }
        val back = TextView(this).apply {
            text = "‹"; textSize = 32f; setTextColor(Color.WHITE); gravity = Gravity.CENTER
            setPadding(dp(8), 0, dp(16), 0); setOnClickListener { finish() }
        }
        top.addView(back)
        val title = TextView(this).apply { text = name; textSize = 17f; setTextColor(Color.WHITE); setTypeface(typeface, 1) }
        top.addView(title, LinearLayout.LayoutParams(0, -2, 1f))
        val refresh = TextView(this).apply {
            text = "⟳"; textSize = 27f; setTextColor(Color.WHITE); setPadding(dp(12), 0, dp(12), 0)
            setOnClickListener { web.reload() }
        }
        top.addView(refresh)
        root.addView(top)
        address = TextView(this).apply {
            text = uriString ?: "Tidak ada alamat"
            textSize = 12f; setTextColor(0xFFB8C0C7.toInt()); setPadding(dp(14), dp(12), dp(14), dp(12))
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(0xFF171C20.toInt()); cornerRadius = dp(16).toFloat()
                setStroke(dp(1), 0xFF292F34.toInt())
            }
        }
        root.addView(address, LinearLayout.LayoutParams(-1, -2).apply { setMargins(dp(10), 0, dp(10), dp(8)) })
        web = WebView(this)
        web.setBackgroundColor(Color.WHITE)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = true
        web.settings.allowContentAccess = true
        web.settings.javaScriptCanOpenWindowsAutomatically = false
        web.settings.setSupportMultipleWindows(false)
        web.webChromeClient = WebChromeClient()
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url ?: return false
                if (url.scheme == "file" || url.scheme == "content" || url.scheme == "https" || url.scheme == "http") {
                    address.text = url.toString()
                    return false
                }
                return true
            }
        }
        root.addView(web, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)

        if (uriString != null) {
            val uri = Uri.parse(uriString)
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
            web.loadUrl(uri.toString())
        } else {
            web.loadDataWithBaseURL(null, "<html><body style='font-family:sans-serif;padding:24px;color:#222'><h2>File tidak ditemukan</h2><p>Kembali ke beranda dan pilih file HTML.</p></body></html>", "text/html", "UTF-8", null)
        }
    }

    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else super.onBackPressed()
    }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
