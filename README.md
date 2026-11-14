# متقن — Aplikasi Setoran Hafalan

Aplikasi Android native (Kotlin + Jetpack Compose + Room) offline untuk pencatatan setoran hafalan Al-Qur'an 30 juz.

## Fitur
- 120 sesi setoran, setiap sesi terdiri dari 2 rubu' (total 240 rubu' dari Juz 1 hingga Juz 30).
- Menampilkan awal dan akhir setiap rubu' dalam format `surah:ayat`.
- Input nama penghafal, pemilihan tanggal setoran melalui Material 3 DatePicker, pemilihan nilai (`Mumtaz`, `Jayyid Jiddan`, `Jayyid`, `Maqbul`, `Dhaif`), serta catatan penguji.
- Filter juz: `Semua juz`, `1–10`, `11–20`, dan `21–30`.
- Perhitungan progres setoran otomatis dan penyimpanan lokal offline di perangkat menggunakan Room Database.

## Build Lokal
Pastikan JDK 21 dan Android SDK 36 tersedia, lalu jalankan:
```bash
gradle assembleDebug
```
APK debug dihasilkan di `app/build/outputs/apk/debug/app-debug.apk`.
