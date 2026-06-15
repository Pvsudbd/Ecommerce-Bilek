# Fitur Hapus Produk dan Tambah Stok

## 1. Fitur Hapus Produk

Endpoint = DELETE /api/dashboard/{id}

Request Body : None

Response Body : Failed
    
    "Product not found" (atau pesan error lainnya)

Response Body : Succeed
    
    "Produk berhasil dihapus"


## 2. Fitur Tambah Stok

Endpoint = PATCH /api/dashboard/{id}/stock

Request Body : Json

    {
        "jumlah" : 10
    }

Response Body : Failed
    
    "Parameter 'jumlah' diperlukan" (atau pesan error lainnya)

Response Body : Succeed
    
    "Stok berhasil ditambahkan"
