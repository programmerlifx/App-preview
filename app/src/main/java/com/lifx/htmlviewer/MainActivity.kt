package com.lifx.htmlviewer

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : Activity() {
    private val bg = Color.rgb(8, 11, 13)
    private val panel = Color.rgb(17, 21, 24)
    private val green = Color.rgb(28, 210, 128)
    private lateinit var root: LinearLayout
    private var selectedUri: Uri? = null
    private var selectedName = "Belum ada file dipilih"

    private val openHtml = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
            selectedUri = uri
            selectedName = uri.lastPathSegment?.substringAfterLast('/') ?: "File HTML"
            showHome()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncoming(intent)
        showHome()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncoming(intent)
        showHome()
    }

    private fun handleIncoming(intent: Intent?) {
        val uri = intent?.data ?: (intent?.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))
        if (uri != null) {
            selectedUri = uri
            selectedName = uri.lastPathSegment?.substringAfterLast('/') ?: "File HTML"
            try { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
        }
    }

    private fun showHome() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(12))
            setBackgroundColor(bg)
        }
        val scroll = ScrollView(this).apply { addView(root) }
        setContentView(scroll)

        label(root, "‹  LIFX", 26, green, true, 0, 0, 0, 4)
        label(root, "HTML Viewer", 30, Color.WHITE, true, 0, 0, 0, 6)
        label(root, "Buka dan preview website langsung dari perangkat.", 14, 0xFF9CA5AD.toInt(), false, 0, 0, 0, 24)

        val hero = card(root)
        label(hero, "⌘", 52, green, true, 0, 0, 0, 4, Gravity.CENTER)
        label(hero, "PREVIEW HTML", 20, Color.WHITE, true, 0, 0, 0, 8, Gravity.CENTER)
        label(hero, "File HTML tunggal atau website satu folder dengan CSS, JavaScript, gambar, font, dan aset pendukung.", 14, 0xFF9CA5AD.toInt(), false, 8, 0, 8, 18, Gravity.CENTER)
        button(hero, "Pilih File HTML", true) { openHtml.launch(arrayOf("text/html", "application/xhtml+xml", "text/*")) }
        button(hero, "Buka dari aplikasi lain", false) {
            Toast.makeText(this, "Pilih LIFX HTML Viewer dari menu Bagikan atau Buka dengan.", Toast.LENGTH_LONG).show()
        }

        val fileCard = card(root)
        label(fileCard, "FILE TERPILIH", 12, green, true, 0, 0, 0, 8)
        label(fileCard, selectedName, 17, Color.WHITE, true, 0, 0, 0, 4)
        label(fileCard, selectedUri?.toString() ?: "Pilih file untuk memulai preview.", 12, 0xFF9CA5AD.toInt(), false, 0, 0, 0, 12)
        button(fileCard, "Tampilkan Preview", true) {
            val uri = selectedUri
            if (uri == null) Toast.makeText(this, "Pilih file HTML terlebih dahulu.", Toast.LENGTH_SHORT).show()
            else {
                val i = Intent(this, PreviewActivity::class.java).putExtra("uri", uri.toString()).putExtra("name", selectedName)
                startActivity(i)
            }
        }

        val history = card(root)
        label(history, "ALUR PENGGUNAAN", 12, green, true, 0, 0, 0, 10)
        label(history, "01  Pilih file HTML", 15, Color.WHITE, true, 0, 0, 0, 8)
        label(history, "02  Untuk website lengkap, pilih index.html di folder asal agar tautan relatif ke CSS dan gambar tetap bekerja.", 13, 0xFF9CA5AD.toInt(), false, 0, 0, 0, 8)
        label(history, "03  Gunakan menu Buka dengan / Bagikan pada pengelola file untuk membuka HTML di aplikasi ini.", 13, 0xFF9CA5AD.toInt(), false, 0, 0, 0, 4)

        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, dp(10), 0, dp(4))
        }
        root.addView(nav, LinearLayout.LayoutParams(-1, -2))
        listOf("⌂\nBeranda", "◷\nRiwayat", "☆\nFavorit", "⚙\nPengaturan").forEachIndexed { idx, title ->
            val b = TextView(this).apply {
                text = title
                gravity = Gravity.CENTER
                textSize = 12f
                setTextColor(if (idx == 0) green else 0xFF929BA4.toInt())
                setPadding(dp(8), dp(10), dp(8), dp(10))
                setOnClickListener { showSection(idx) }
            }
            nav.addView(b, LinearLayout.LayoutParams(0, -2, 1f))
        }
    }

    private fun showSection(which: Int) {
        if (which == 0) { showHome(); return }
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(16))
            setBackgroundColor(bg)
        }
        val scroll = ScrollView(this).apply { addView(root) }
        setContentView(scroll)
        label(root, "‹  LIFX HTML Viewer", 24, green, true, 0, 0, 0, 24)
        when (which) {
            1 -> {
                label(root, "Riwayat File", 27, Color.WHITE, true, 0, 0, 0, 10)
                label(root, "Riwayat sesi ini", 14, 0xFF9CA5AD.toInt(), false, 0, 0, 0, 12)
                val c = card(root)
                label(c, selectedName, 17, Color.WHITE, true, 0, 0, 0, 5)
                label(c, if (selectedUri == null) "Belum ada file dibuka." else selectedUri.toString(), 12, 0xFF9CA5AD.toInt(), false, 0, 0, 0, 10)
                button(c, "Buka Lagi", true) { selectedUri?.let { startActivity(Intent(this, PreviewActivity::class.java).putExtra("uri", it.toString()).putExtra("name", selectedName)) } }
            }
            2 -> {
                label(root, "Favorit", 27, Color.WHITE, true, 0, 0, 0, 10)
                label(root, "Simpan file yang sering digunakan dengan memilih file dari beranda. Fitur favorit tersimpan akan dikembangkan pada versi berikutnya.", 14, 0xFF9CA5AD.toInt(), false, 0, 0, 0, 12)
            }
            3 -> {
                label(root, "Pengaturan", 27, Color.WHITE, true, 0, 0, 0, 12)
                val c = card(root)
                label(c, "Privasi & Izin", 17, Color.WHITE, true, 0, 0, 0, 8)
                label(c, "Aplikasi menggunakan pemilih dokumen Android. Akses diberikan untuk file yang Anda pilih, tanpa meminta izin seluruh penyimpanan.", 14, 0xFF9CA5AD.toInt(), false, 0, 0, 0, 8)
                label(c, "Mode preview", 17, Color.WHITE, true, 0, 12, 0, 8)
                label(c, "Konten HTML lokal dapat menjalankan JavaScript. Buka hanya file dari sumber tepercaya.", 14, 0xFF9CA5AD.toInt(), false, 0, 0, 0, 8)
                label(root, "LIFX HTML Viewer  •  Versi 1.0.0", 13, 0xFF9CA5AD.toInt(), false, 0, 20, 0, 0)
            }
        }
        button(root, "Kembali ke Beranda", true) { showHome() }
    }

    private fun card(parent: LinearLayout): LinearLayout {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(panel); cornerRadius = dp(18).toFloat()
                setStroke(dp(1), 0xFF292F34.toInt())
            }
        }
        val p = LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(16) }
        parent.addView(box, p)
        return box
    }

    private fun label(parent: LinearLayout, text: String, size: Int, color: Int, bold: Boolean,
                      left: Int, top: Int, right: Int, bottom: Int, gravity: Int = Gravity.START) {
        val t = TextView(this).apply {
            this.text = text; textSize = size.toFloat(); setTextColor(color)
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
            this.gravity = gravity
            setLineSpacing(dp(2).toFloat(), 1.0f)
        }
        val p = LinearLayout.LayoutParams(-1, -2).apply {
            setMargins(dp(left), dp(top), dp(right), dp(bottom))
        }
        parent.addView(t, p)
    }

    private fun button(parent: LinearLayout, text: String, primary: Boolean, action: () -> Unit) {
        val b = TextView(this).apply {
            this.text = text; textSize = 15f; gravity = Gravity.CENTER
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(if (primary) Color.rgb(4, 25, 17) else Color.WHITE)
            setPadding(dp(16), dp(15), dp(16), dp(15))
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(if (primary) green else panel)
                cornerRadius = dp(16).toFloat()
                setStroke(dp(1), if (primary) green else 0xFF343B40.toInt())
            }
            setOnClickListener { action() }
        }
        parent.addView(b, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(10) })
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
