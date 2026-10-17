
package com.lifx.htmlviewer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private lateinit var root: LinearLayout
    private lateinit var webView: WebView
    private lateinit var statusText: TextView

    private var currentUri: Uri? = null
    private var showingPreview = false

    private val backgroundColor = Color.rgb(15, 17, 16)
    private val panelColor = Color.rgb(28, 31, 29)
    private val accentColor = Color.rgb(120, 220, 140)
    private val textColor = Color.rgb(240, 242, 240)
    private val mutedColor = Color.rgb(160, 168, 161)

    private val openHtml =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {
                    // Beberapa penyedia file tidak mendukung izin persisten.
                }

                openHtmlFile(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createInterface()
        handleIncomingIntent(intent)
    }

    private fun createInterface() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
            setPadding(dp(20), dp(20), dp(20), dp(12))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val brand = TextView(this).apply {
            text = "LIFX / VIEWER"
            textSize = 12f
            setTextColor(accentColor)
            letterSpacing = 0.14f
        }

        val title = TextView(this).apply {
            text = "HTML Viewer"
            textSize = 29f
            setTextColor(textColor)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, dp(8), 0, dp(5))
        }

        val subtitle = TextView(this).apply {
            text = "Buka dan pratinjau halaman HTML lokal."
            textSize = 13f
            setTextColor(mutedColor)
        }

        header.addView(brand)
        header.addView(title)
        header.addView(subtitle)

        root.addView(
            header,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val openButton = createButton("Pilih file HTML", true) {
            openHtml.launch(arrayOf("text/html", "application/xhtml+xml", "*/*"))
        }

        val homeButton = createButton("Beranda", false) {
            showHome()
        }

        val toolbar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        toolbar.addView(
            openButton,
            LinearLayout.LayoutParams(0, dp(48), 1f).apply {
                marginEnd = dp(8)
            }
        )

        toolbar.addView(
            homeButton,
            LinearLayout.LayoutParams(0, dp(48), 1f)
        )

        root.addView(
            toolbar,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(22)
                bottomMargin = dp(14)
            }
        )

        statusText = TextView(this).apply {
            text = "SIAP DIGUNAKAN"
            textSize = 11f
            setTextColor(accentColor)
            letterSpacing = 0.08f
            setPadding(0, dp(8), 0, dp(10))
        }

        root.addView(statusText)

        webView = WebView(this).apply {
            setBackgroundColor(Color.WHITE)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = false
            settings.allowContentAccess = true
            settings.javaScriptCanOpenWindowsAutomatically = false
            settings.setSupportMultipleWindows(false)

            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val uri = request?.url ?: return true

                    // Batasi navigasi ke konten lokal yang dipilih
                    // atau situs HTTP/HTTPS.
                    return when (uri.scheme?.lowercase()) {
                        "content", "file", "http", "https", "about" -> false
                        else -> true
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    statusText.text = "PREVIEW AKTIF"
                }
            }
        }

        root.addView(
            webView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val footer = TextView(this).apply {
            text = "LIFX HTML VIEWER  •  LOCAL PREVIEW"
            textSize = 10f
            setTextColor(mutedColor)
            gravity = Gravity.CENTER
            setPadding(0, dp(14), 0, dp(4))
        }

        root.addView(footer)

        setContentView(root)
        showHome()
    }

    private fun createButton(
        label: String,
        primary: Boolean,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = label
            textSize = 13f
            isAllCaps = false
            setTextColor(if (primary) Color.BLACK else textColor)
            setBackgroundColor(if (primary) accentColor else panelColor)
            setOnClickListener { action() }
        }
    }

    private fun showHome() {
        showingPreview = false
        webView.stopLoading()
        webView.loadUrl("about:blank")
        webView.visibility = View.GONE

        statusText.text = "BERANDA"

        val existing = root.findViewWithTag<View>("home-content")
        if (existing != null) {
            existing.visibility = View.VISIBLE
            return
        }

        val homeContent = LinearLayout(this).apply {
            tag = "home-content"
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(20), dp(18), dp(20))
            setBackgroundColor(panelColor)
        }

        val heading = TextView(this).apply {
            text = "Buka halaman web."
            textSize = 21f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(textColor)
        }

        val description = TextView(this).apply {
            text = "\nPilih file HTML dari penyimpanan perangkat untuk menampilkannya di sini.\n\nFile akan dibuka melalui pemilih file Android. Akses hanya diberikan untuk file yang kamu pilih."
            textSize = 14f
            setTextColor(mutedColor)
            setLineSpacing(dp(4).toFloat(), 1f)
        }

        val chooseButton = createButton("Pilih HTML sekarang", true) {
            openHtml.launch(arrayOf("text/html", "application/xhtml+xml", "*/*"))
        }

        homeContent.addView(heading)
        homeContent.addView(description)
        homeContent.addView(
            chooseButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(48)
            ).apply {
                topMargin = dp(18)
            }
        )

        root.addView(
            homeContent,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )
    }

    private fun openHtmlFile(uri: Uri) {
        currentUri = uri
        showingPreview = true

        root.findViewWithTag<View>("home-content")?.visibility = View.GONE
        webView.visibility = View.VISIBLE

        statusText.text = "MEMBUKA HTML..."

        try {
            webView.loadUrl(uri.toString())
        } catch (e: Exception) {
            statusText.text = "GAGAL MEMBUKA FILE"
            Toast.makeText(
                this,
                "File tidak dapat dibuka: ${e.localizedMessage ?: "kesalahan tidak diketahui"}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        val uri = when (intent.action) {
            Intent.ACTION_VIEW -> intent.data
            Intent.ACTION_SEND -> {
                if (android.os.Build.VERSION.SDK_INT >= 33) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
                }
            }
            else -> null
        }

        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
                // Intent dari aplikasi lain mungkin memberi izin sementara.
            }

            openHtmlFile(uri)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    override fun onBackPressed() {
        when {
            webView.visibility == View.VISIBLE && webView.canGoBack() -> {
                webView.goBack()
            }
            showingPreview -> {
                showHome()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.destroy()
        }
        super.onDestroy()
    }
}
