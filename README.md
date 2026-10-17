# LIFX HTML Viewer

Aplikasi Android untuk membuka dan melihat file HTML melalui Android WebView dengan antarmuka gelap dan aksen hijau.

## Fitur
- Pemilih dokumen HTML Android.
- Preview menggunakan WebView.
- Riwayat sesi sederhana dan halaman Favorit/Pengaturan.
- Intent filter untuk menerima file HTML dari aplikasi lain.
- GitHub Actions membangun APK debug secara otomatis.

## Build APK lewat GitHub
1. Upload seluruh isi ZIP ini ke repository GitHub baru.
2. Buka tab **Actions** dan jalankan workflow **Build LIFX HTML Viewer APK**, atau push commit ke branch `main`/`master`.
3. Tunggu job selesai.
4. Buka hasil workflow dan unduh artifact `LIFX-HTML-Viewer-debug-APK`.
5. Ekstrak artifact ZIP untuk mendapatkan `app-debug.apk`.

## Catatan dukungan folder dan aset
Untuk website dengan beberapa file, pilih `index.html` dari folder asal bila penyedia pemilih dokumen Android memberi akses URI yang sesuai. Beberapa penyedia file mengubah URI atau tidak mengekspos direktori lokal secara langsung; dalam kasus tersebut, cara paling andal adalah mengemas website beserta asetnya sebagai ZIP dan menambahkan dukungan ekstraksi/import folder sebelum preview.

Versi awal ini belum memiliki implementasi import ZIP/akses pohon folder penuh, sinkronisasi favorit permanen, atau riwayat permanen. JavaScript diaktifkan untuk kompatibilitas website; gunakan hanya HTML dari sumber tepercaya.
