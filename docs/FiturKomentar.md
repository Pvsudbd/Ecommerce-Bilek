# Panduan Santai: Gimana Sih Cara Kerja Fitur Detail Produk & Komentar?

Kalau kamu bingung gimana ceritanya sebuah produk pas diklik tiba-tiba bisa muncul modal *pop-up* keren yang isinya foto, harga, dan deretan curhatan pembeli (komentar), tenang aja! Di sini bakal dijelasin alur kerjanya pakai bahasa manusia biasa, bebas dari istilah AI yang kaku.

---

## 1. Alur Cerita (Gimana Prosesnya Terjadi?)

Bayangin kamu lagi nongkrong di beranda toko (`Mainpage.html`).
1. **Pengunjung Ngeklik Produk:** Pas mouse kamu ngeklik gambar atau nama produk (misalnya Sepatu Jordan), browser bakal langsung bereaksi dan jalanin fungsi Javascript namanya `openProductModal(id_produk)`.
2. **Javascript Nanya ke Java:** Si Javascript ini nggak tau apa-apa soal isi komentar. Jadi dia "nelpon" server Java kita (Spring Boot) dan bilang: *"Eh Java, minta data lengkap buat produk nomor 5 dong, sekalian sama semua ulasan pembelinya ya!"*
3. **Java Ngebongkar Database:** Server Java menerima telepon itu. Dia buka laci database MySQL, ngambil data Sepatu Jordan di tabel `products`, lalu pindah ke tabel `komentar` buat nyari semua tulisan yang punya label `id_product = 5`.
4. **Java Ngirim Paket:** Setelah ketemu semua, Java ngebungkus data sepatu + daftar komentar itu ke dalam satu kardus JSON, lalu dikirim balik ke Javascript.
5. **Modal Terbuka Sempurna:** Javascript nerima kardus itu, ngebongkar isinya, lalu nempelin foto, harga, dan tulisan komentar ke layar. Terakhir, layar dikasih efek gelap (*blur*) dan *pop-up* modal muncul di tengah-tengah!

---

## 2. Endpoint API yang Dipakai (Jalur Komunikasi)

Cuma ada dua jalur utama yang melayani semua kebutuhan ini. Keduanya diatur di dalam `CProductDetail.java`.

### A. Ngambil Data Produk & Komentar
**Jalur:** `GET /api/product/{id}`

- **Kapan dipanggil?** Saat pengunjung ngeklik produk di beranda.
- **Tugasnya:** Mencari produk berdasarkan ID yang diminta, dan langsung sekalian narik semua komentar yang menempel di produk itu (diurutkan dari yang paling baru ke yang paling lama).
- **Hasil balesannya (JSON):**
  ```json
  {
    "product": {
      "idProduct": 5,
      "namaProduct": "Sepatu Jordan",
      "harga": 150,
      "stok": 10,
      "kategori": "Clothing - Footwear",
      "imageUrl": "https://...",
      "tlike": 12
    },
    "comments": [
      {
        "id": 1,
        "name": "Budi",
        "isi": "Sepatunya mantap bang, ukurannya pas!",
        "bintang": 5,
        "createdAt": "2026-06-18T20:00:00"
      }
    ]
  }
  ```

### B. Ngirim Komentar Baru
**Jalur:** `POST /api/product/{id}/comment`

- **Kapan dipanggil?** Saat pengunjung udah ngetik panjang lebar di kolom ulasan, milih jumlah bintang, lalu mencet tombol "Kirim".
- **Tugasnya:** Menerima teks komentar, ngecek identitas pengirim (kalau dia udah login, otomatis pake namanya. Kalau belum login/Guest, disetel jadi "Anonymous"), dan nyimpen semuanya ke dalam database MySQL.
- **Paket yang dikirim ke Java (JSON):**
  ```json
  {
    "idUser": 12,        // Opsional, null kalau guest
    "name": "Siti",      // Nama pengirim
    "isi": "Pengiriman agak telat tapi barang aman.",
    "bintang": 4         // Jumlah bintang (1-5)
  }
  ```

---

## 3. Rahasia di Balik Layar (File yang Kerja Keras)

Biar kamu gampang nyari kalau mau ngedit-ngedit, ini dia daftarnya:

- **`Entity/Komentar.java`**: Ini cetakan datanya. Isinya cuma daftar kolom yang wajib sama persis dengan yang ada di MySQL (kayak `id_product`, `name`, `isi`, dll).
- **`Repository/Mainpage/RKomentar.java`**: Ini kurirnya. Dia yang punya kekuatan sakti buat *query* ke MySQL pake perintah pendek kayak `findByIdProductOrderByCreatedAtDesc()`.
- **`Dto/Mainpage/DProductDetail.java`**: Ini kardus bungkusannya. Biar data produk dan komentar nggak berantakan pas dikirim lewat internet, mereka disatukan dulu di dalam objek DTO (Data Transfer Object) ini.
- **`Service/Mainpage/SProductDetail.java`**: Ini si mandor (otaknya). Semua aturan main, logika pengecekan barang ada apa nggak, dan pemberian nilai *default* (misal bintang kosong diisi 5) diproses di sini sebelum nyuruh Repository nyimpen ke database.
- **`Mainpage.html` (Baris 200-an)**: Kalo kamu mau ngubah warna latar ulasan, ngatur seberapa gede fotonya, atau ngubah ikon bintang, semuanya ada di bagian HTML Modal di file ini!
