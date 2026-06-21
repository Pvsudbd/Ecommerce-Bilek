# Dokumentasi Fitur Sellora (Toko Online)

Selamat datang di repositori proyek Sellora! Aplikasi E-Commerce ini dibangun dengan menggunakan arsitektur MVC (*Model-View-Controller*) di ekosistem Spring Boot. Secara umum, kami memisahkan alur kerja sistem menjadi **Controller** (sebagai penerima/pengatur lalu lintas *request*) dan **Service** (sebagai otak/pusat pemrosesan *business logic*).

Berikut adalah daftar 11 fitur utama yang ada di proyek ini beserta rincian kegunaan dari setiap *method* Java dan bagaimana mereka terhubung dengan *Database Entity*:

---

## 1. Ulasan Produk
- **Controller & Service terkait:** `CProductDetail` & `SProductDetail`
  - **Kegunaan:** Menampilkan umpan balik (*rating* atau komentar) dari pelanggan terhadap suatu barang.
  - **Logic:** Biasanya fitur ini di-integrasikan saat menampilkan detail produk. Service akan mengambil data utama dari Entity `Product` sekaligus menarik data ulasan yang relevan dari *database* untuk disajikan di antarmuka halaman barang, sehingga calon pembeli lain dapat melihatnya.

## 2. Search Produk
- **Controller & Service terkait:** `Cfilter` / `CDashboard` & `Sfilter` / `SDashboard`
  - **Kegunaan:** Memudahkan pengguna atau Admin menemukan barang tertentu cukup dengan mengetikkan nama/kata kunci.
  - **Logic:** *Method* pencarian menerima inputan teks dari kolom *search bar*. Service lalu melakukan pencarian dinamis (misalnya memakai `LIKE %keyword%`) pada Entity `Product`, dan hanya mengembalikan produk yang namanya mengandung unsur kata pencarian tersebut.

## 3. Filter Produk
- **Controller & Service terkait:** `Cfilter` & `Sfilter`
  - **Kegunaan:** Menyaring etalase toko agar hanya menampilkan produk sesuai kategori tertentu (misalnya, hanya kategori *Elektronik* atau *Baju*).
  - **Logic:** `Sfilter` menerima parameter kategori yang diklik pengguna. Daripada memuat seluruh barang, *method* ini akan secara spesifik me-*request* daftar dari Entity `Product` yang cocok dengan kategori yang diminta, menghemat waktu proses dan memudahkan belanja.

## 4. Pembelian (Checkout)
- **Controller & Service terkait:** `CCheckout` & `SCheckout`
  - **Kegunaan:** Mengurus proses "bayar sekarang" untuk barang-barang yang sudah dimasukkan ke keranjang.
  - **Logic:** Ini adalah fitur inti transaksi. Saat dieksekusi, `SCheckout` menjumlahkan total harga belanjaan, memotong jumlah stok persediaan dari Entity `Product`, lalu memigrasikan data barang dari Entity `Cart` menjadi struk belanja permanen di Entity `Order` dan `OrderItems`. Begitu proses ini sukses, keranjang pengguna langsung dikosongkan.

## 5. Fitur Keranjang (Add to Cart)
- **Controller & Service terkait:** `CAddKeranjang` & `SAddKeranjang`
  - **Kegunaan:** Menampung sementara barang-barang yang rencananya akan dibeli oleh *user*.
  - **Logic:** Saat pengguna menekan "Tambah Keranjang", `SAddKeranjang` akan memverifikasi ketersediaan stok terlebih dahulu, barulah menyimpannya ke Entity `Cart`. Khusus di sini, ada penanganan spesial menggunakan `CartExceptionHandler` pada Controller, agar pesan *error* (misalnya kalau stok tidak muat atau *user* lupa login) tampil rapi di layar pengguna.

## 6. Login
- **Controller & Service terkait:** `CLogin` & `SLogin`
  - **Kegunaan:** Pintu masuk keamanan sistem. Mengatur autentikasi pengguna agar bisa mulai bertransaksi atau mengakses *dashboard* (khusus Admin).
  - **Logic:** `SLogin` bertugas melacak kecocokan email di Entity `User` dan mengecek perizinan akses di Entity `Role`. Demi keamanan tingkat tinggi, *password* asli tidak pernah dicocokkan secara mentah, melainkan wajib melalui validasi kecocokan enkripsi algoritma **BCrypt**. Jika valid, akses diberikan.

## 7. Register
- **Controller & Service terkait:** `CRegister` & `SRegister`
  - **Kegunaan:** Tempat di mana pengunjung bisa mendaftarkan akun baru agar resmi menjadi pengguna terdaftar.
  - **Logic:** `SRegister` memastikan *email* tersebut belum pernah dipakai. Setelah itu, *password* yang diketik pengguna tidak disimpan polos; *method* ini akan meng-*hash* *password* tersebut dengan **BCrypt**, barulah menyimpannya ke dalam *database* sebagai Entity `User` baru (dengan tipe peran *default* sebagai pembeli biasa/Customer).

## 8. Add Barang (Tambah Produk & Upload Gambar)
- **Controller & Service terkait:** `CDSupaimg` & `SDsupaimg`
  - **Kegunaan:** Fitur yang digunakan oleh Admin untuk menjual barang baru, lengkap dengan rincian dan foto barangnya.
  - **Logic:** `SDsupaimg` memiliki alur kerja ganda: menerima data teks (nama, harga, kategori) sekaligus mengolah *file* foto yang diunggah. Gambar akan disimpan dengan aman ke dalam *storage* lokal (direktori proyek), dan detail teksnya direkam rapat-rapat ke dalam Entity `Product`.

## 9. Add Stok Barang & Mengurangi (Update Stock)
- **Controller & Service terkait:** `CDashboard` & `SDashboard`
  - **Kegunaan:** Membantu Admin mengatur sirkulasi barang secara instan, seperti menambah stok saat barang baru tiba, atau menguranginya jika barang rusak.
  - **Logic:** Menggunakan mekanisme *PATCH mapping*, Admin cukup memasukkan seberapa banyak stok ingin ditambah (atau dikurangi dengan memakai angka minus). Service akan mencari Entity `Product` berdasarkan ID-nya, mengalkulasi jumlah stok barunya, dan langsung menimpanya di *database*.

## 10. Delete Barang
- **Controller & Service terkait:** `CDhapus` / `CDashboard` & `SDhapus`
  - **Kegunaan:** Membuang produk selamanya dari sistem apabila sudah tidak lagi diproduksi atau dijual.
  - **Logic:** Fitur sederhana namun sangat berdampak. `SDhapus` akan memanggil perintah *delete* langsung ke *database* berdasarkan ID spesifik, merobohkan rekaman data di Entity `Product` agar produk itu tidak pernah muncul lagi di halaman pencarian.

## 11. Cek Order (Manajemen & Riwayat Pesanan)
- **Controller & Service terkait:** `COrder` (untuk *Customer*), `CAdminOrder` (untuk *Admin*) & `Sorder` / `SAdminOrder`
  - **Kegunaan:** Menampilkan resi riwayat apa saja yang sudah dibeli pelanggan, sekaligus tempat di mana Admin mengubah status pengiriman.
  - **Logic:**
    - Pada sisi **Pembeli**, `Sorder` mencari Entity `Order` dan `OrderItems` miliknya secara spesifik.
    - Pada sisi **Admin**, `SAdminOrder` mengambil *semua* pesanan dari seluruh pengguna. Datanya diurutkan secara sekuensial (misalnya berdasar ID yang terus *Ascending*) agar rapi. Admin juga dapat mengaktifkan fungsi edit status, yang mana hal itu akan langsung mengubah entri status (*Diproses*, *Dikirim*, dll.) di dalam Entity `Order` yang sama.
