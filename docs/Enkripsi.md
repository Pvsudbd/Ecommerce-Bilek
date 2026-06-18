# Sistem Keamanan Enkripsi Password (BCrypt)

Dokumen ini menjelaskan bagaimana password *user* dilindungi di aplikasi E-Commerce ini.

## Kenapa Tidak Disimpan Dalam Teks Asli (Plaintext)?
Menyimpan *password* secara polos (misal: "rahasia123") di dalam *database* sangatlah berbahaya. Jika *database* bocor (di-hack) atau ada admin nakal yang melihat tabel `users`, mereka akan langsung tahu *password* semua orang.

## Kenapa Memilih BCrypt?
Kita menggunakan standar **BCrypt**. Kenapa bukan MD5 atau SHA-256 biasa?
1. **Slow by Design (Sengaja Dibuat Lambat):** BCrypt secara matematis memang dibuat berat untuk diproses. Kenapa? Biar kalau *hacker* mencoba menebak *password* pakai mesin komputer yang bisa nyoba jutaan kombinasi per detik (*Brute Force*), komputernya bakal "ngos-ngosan" karena lambat.
2. **Otomatis Punya "Salt":** *Salt* (garam) itu ibarat teks acak yang dilempar dan ditempelkan ke setiap *password* sebelum di-enkripsi. Tujuannya supaya meskipun ada dua user yang password-nya sama-sama "123", hasil *hash*-nya di *database* bakal beda total! Ini mencegah *hacker* memakai kamus *Rainbow Tables*.

## Cara Kerja di Java

Semua ini dikontrol di dalam file `Security/BcryptUtil.java`.

### Saat Pendaftaran (Register)
File: `Service/Auth/SRegister.java`

Ketika ada user baru yang daftar, Java gak langsung nyimpen teksnya ke database, melainkan disaring dulu lewat fungsi ini:
```java
customer.setPassword(BcryptUtil.hashPassword(request.getPassword()));
```
Hasil akhirnya di database akan berupa teks aneh sepanjang ~60 karakter yang selalu berawalan `$2a$`, contohnya:
`$2a$10$wT/tW6sB9g8D7F.u.Lz8e.mJdJ1F2R5K8N1Q...`

### Saat Masuk (Login)
File: `Service/Auth/SLogin.java`

Saat mau mencocokkan *password*, kita **TIDAK BISA** dan **TIDAK BOLEH** mengecek pakai fungsi teks biasa `equals()`. Kita harus menyuruh algoritma BCrypt yang mengeceknya, karena cuma dia yang tau cara memecahkan kombinasi *salt* dan *hash*-nya.
```java
if (!BcryptUtil.checkPassword(request.getPassword(), user.getPassword())) {
    // Password salah!
}
```

## Penting: Nasib Akun Lama!
Karena sistem keamanan ini baru dipasang, **semua akun lama yang ada di MySQL kamu (termasuk akun Admin-mu sendiri) sudah tidak bisa dipakai lagi**. Karena password lama berformat polos, sedangkan Java sekarang meminta format BCrypt.

**Solusinya:** Hapus isi tabel `users` milikmu (kalau di lokal), lalu buatlah/daftarkan akun baru agar ter-enkripsi secara otomatis.
