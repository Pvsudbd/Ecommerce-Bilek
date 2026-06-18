# Fitur Tambah Produk (Dashboard Admin)

Endpoint = POST /api/dashboard/product

Tipe Konten = multipart/form-data

Request Form Data :
- nama: String (contoh: "Sepatu Sneakers Klasik")
- stok: Integer (contoh: 40)
- harga: Integer (contoh: 250000)
- kategori: String (contoh: "Sepatu Pria")
- file: File Gambar (JPG/PNG) -> opsional

Response Body : Failed

    "Gagal menambahkan produk: [Pesan Error]"

Response Body : Succeed

    "Produk berhasil ditambahkan"

---
Catatan:
- Backend akan mengirimkan file gambar secara otomatis ke Supabase Storage lewat API.
- Link gambar yang masuk ke database murni link public dari Supabase.
- Kalo input gambar dikosongkan, bakal otomatis diisi dengan link gambar placeholder.
