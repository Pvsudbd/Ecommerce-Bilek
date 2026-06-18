# Filter Produk

Endpoint = GET /products/filter?search=&sort=

Parameter Query :

    search = kata kunci produk
    sort   = default | name-asc | name-desc | price-asc | price-desc | recommendation

Contoh Request :

    /products/filter?search=sepatu&sort=recommendation

Request Body : Tidak ada, karena endpoint memakai query parameter.

Response Body : Failed

    [
    ]

Jika tidak ada produk yang cocok dengan keyword atau urutan yang dipilih, response akan mengembalikan array kosong.

Response Body : Succeed

    [
        {
            "id": 1,
            "name": "Kacang ijo",
            "price": 150000,
            "category": "Sale",
            "stock": 10,
            "imageUrl": "https://...",
            "likeCount": 120
        }
    ]

Keterangan sort :

    default        = Acak (Random)
    name-asc       = Nama: A - Z
    name-desc      = Nama: Z - A
    price-asc      = Harga: Rendah ke Tinggi
    price-desc     = Harga: Tinggi ke Rendah
    recommendation = Stok langka (<= 20) diurutkan berdasarkan Like terbanyak

Catatan :

    Endpoint ini dipakai untuk search dan sorting di backend Java.
    Filter kategori dan range harga masih dijalankan di Mainpage.js.
