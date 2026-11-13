# متقن — Aplikasi Setoran Hafalan
Aplikasi Android offline untuk pencatatan setoran hafalan 30 juz.

## Fitur
- 120 sesi, setiap sesi terdiri dari 2 rubu' (total 240 rubu').
- Menampilkan awal dan akhir setiap rubu' dalam format surah:ayat.
- Nama penghafal, tanggal, nilai, dan catatan.
- Filter juz 1–10, 11–20, 21–30.
- Progres setoran dan penyimpanan lokal di perangkat melalui localStorage.
- WebView offline; tidak perlu internet.

## Build APK otomatis melalui GitHub
1. Buat repository GitHub baru.
2. Upload seluruh isi folder proyek ini ke branch `main`.
3. Buka tab **Actions** dan jalankan workflow **Build Mutqin APK**.
4. Setelah selesai, unduh artifact `mutqin-debug-apk` dan ambil `app-debug.apk`.

## Build lokal
Buka folder ini dengan Android Studio, lakukan Gradle Sync, lalu pilih Build > Build APK(s).
