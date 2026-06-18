# Fitur Tambah Produk Baru

Endpoint = POST /api/dashboard/product

Request Body : `multipart/form-data` (Bukan JSON)

Karena ada file gambar yang akan diupload (bukan sekadar link/teks), kita harus menggunakan format `form-data`.

- `nama_product` : (Text) "Kemeja Flanel Kotak-kotak"
- `harga`        : (Text/Number) 150000
- `kategori`     : (Text) "Pakaian Pria"
- `stok`         : (Text/Number) 50
- `image_file`   : (File) [File gambar kemeja.jpg/png diupload di sini]

Response Body : Failed (Contoh jika data tidak lengkap)
    
    {
        "Response": "Failed",
        "message": "Nama produk dan harga wajib diisi"
    }

Response Body : Failed (Contoh jika produk sudah ada)

    {
        "Response": "Failed",
        "message": "Produk dengan nama tersebut sudah ada di database"
    }

Response Body : Succeed
    
    {
        "Response": "Success",
        "message": "Produk berhasil ditambahkan",
        "data": {
            "id_product": 101,
            "nama_product": "Kemeja Flanel Kotak-kotak",
            "harga": 150000,
            "kategori": "Pakaian Pria",
            "stok": 50,
            "image_url": "https://link-gambar.com/kemeja.jpg"
        }
    }
