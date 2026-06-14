# Dokumentasi Fitur Dashboard & Manajemen Produk

Dokumen ini menjelaskan alur sistem fitur Dashboard Admin, termasuk fitur pencarian produk, penghapusan produk, dan penambahan/pengurangan stok. Sistem ini menggunakan arsitektur REST API dengan pemisahan antara Frontend (HTML + JavaScript) dan Backend (Spring Boot Java).

---

## Alur Kerja Keseluruhan (Flow)

### 1. Load Halaman & Tampil Data Default
- **Frontend**: Saat admin membuka `DashboardAdmin.html`, script JS akan langsung memanggil `fetchDashboard()`.
- **API Call**: `GET /api/dashboard`
- **Backend (Java)**: `CDashboard` menerima request. `SDashboard` lalu mengambil 6 data produk acak menggunakan `RDashboard` (Native MySQL Query: `ORDER BY RAND() LIMIT 6`).
- **Frontend**: Mengubah JSON response menjadi elemen HTML Card Produk dan menampilkannya di bagian "Produk Pilihan (Acak)".

### 2. Fitur Pencarian (Search)
- **Frontend**: Admin mengetik nama produk di Search Bar dan menekan tombol "CARI" atau menekan enter.
- **API Call**: `GET /api/dashboard?search=kata_kunci`
- **Backend (Java)**: `CDashboard` menerima parameter `search`. `SDashboard` mengambil data pencarian melalui metode `searchProducts` di `RDashboard` menggunakan pencocokan (LIKE) nama atau kategori produk (mengabaikan kapitalisasi huruf).
- **Frontend**: Menampilkan hasil pencarian di *section* khusus "Hasil Pencarian" di atas daftar produk acak.

### 3. Fitur Hapus Produk
- **Frontend**: Admin menekan tombol **Hapus** warna merah pada sebuah *card* produk. Sebuah *prompt* konfirmasi akan muncul.
- **API Call**: `DELETE /api/dashboard/{idProduct}`
- **Backend (Java)**: `CDashboard` memanggil metode `hapusProduk(id)` pada layanan `SDhapus`. Sistem akan mengecek ketersediaan ID, lalu memanggil fungsi `deleteById` melalui repository `RDhapus`.
- **Frontend**: Jika sukses, akan muncul alert, lalu *page* akan merefresh ulang data di halaman tersebut (memanggil `fetchDashboard()` secara otomatis) sehingga produk yang terhapus menghilang dari antarmuka.

### 4. Fitur Penambahan/Perubahan Stok
- **Frontend**: Admin menekan tombol **+ Stok** warna hijau. Akan muncul *prompt input* teks yang meminta admin memasukkan jumlah stok (Bisa positif untuk menambah, atau negatif untuk mengurangi).
- **API Call**: `PATCH /api/dashboard/{idProduct}/stock` dengan body JSON `{ "jumlah": 5 }`.
- **Backend (Java)**: `CDashboard` menerima payload JSON. Metode `tambahStok(id, jumlah)` di `SDhapus` dijalankan, di mana data lama produk diambil terlebih dahulu, kemudian kolom stok ditambahkan dengan angka input, dan akhirnya disimpan (save) kembali ke database via `RDhapus`.
- **Frontend**: Setelah API mengembalikan status sukses, halaman otomatis merender ulang agar angka stok yang baru terlihat seketika.

---

## Referensi API Endpoints

Berikut adalah daftar endpoint REST API yang digunakan di Dashboard:

### 1. Ambil Data Dashboard (Pencarian & Random Produk)
- **URL**: `/api/dashboard`
- **Method**: `GET`
- **Query Parameter (Opsional)**: `?search=[keyword]`
- **Response Sukses (200 OK)**:
  ```json
  {
    "searchResults": [
      {
        "id": 1,
        "name": "Baju Kaos",
        "price": 50000,
        "category": "Pakaian",
        "stock": 10,
        "imageUrl": "http://link",
        "likeCount": 15
      }
    ],
    "randomProducts": [ ... ]
  }
  ```

### 2. Hapus Produk
- **URL**: `/api/dashboard/{id}`
- **Method**: `DELETE`
- **URL Path Variable**: `id` (ID dari produk yang akan dihapus)
- **Response Sukses (200 OK)**: String `"Produk berhasil dihapus"`
- **Response Gagal (400 Bad Request)**: Berisi pesan error, misal `"Produk tidak ditemukan"`

### 3. Tambah / Ubah Stok
- **URL**: `/api/dashboard/{id}/stock`
- **Method**: `PATCH`
- **URL Path Variable**: `id` (ID dari produk)
- **Request Body**:
  ```json
  {
    "jumlah": 5
  }
  ```
  *(Catatan: masukkan angka negatif untuk mengurangi stok)*
- **Response Sukses (200 OK)**: String `"Stok berhasil ditambahkan"`
- **Response Gagal (400 Bad Request)**: Berisi pesan error.

---
Dokumen ini dibuat otomatis oleh asisten sebagai referensi pengembangan `Ecommerce-YV`.
