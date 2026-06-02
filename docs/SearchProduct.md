# Fitur Search Produk

Endpoint = GET /products?search=

Request Body : Json

    {
        "Nama_produk" : "Kacang ijo"
    }

Response Body : Failed
    
    {
        "Response": "Nope",
        "message": "Product not found"
    }
Response Body : Succeed
    
    {
        "Nama_produk" : "Kacang ijo",
        "Foto_produk",
        "Harga",
        "Kategori",
        "Stok"
    }