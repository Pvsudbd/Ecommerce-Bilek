
---

## 1. JpaRepository: Ngebuat CRUD Otomatis
Di file *Repository*, kamu pasti sering lihat ini:
`public interface RProduct extends JpaRepository<Product, Integer>`

Bagian `extends JpaRepository`, Spring Boot langsung membuati kamu fungsi-fungsi database seperti:
- `findAll()` -> Buat *Select All*
- `findById(id)` -> Buat cari berdasarkan ID
- `save(data)` -> Bisa buat *Insert* (kalau data baru) DAN *Update* (kalau ID-nya sudah ada)
- `deleteById(id)` -> Buat *Delete*

Kamu sama sekali **TIDAK PERLU** nulis SQL manual untuk 4 operasi dasar di atas!

---

## 2. Papan Nama: @Repository, @Service, dan @RestController
Kamu pasti sadar di atas setiap *Class* ada anotasi (tulisan pakai `@`). Ini bukan pajangan, ini adalah **Papan Nama** buat ngasih tahu Spring Boot tugas dari kelas tersebut.

- **`@Repository`**: "Halo Spring, kelasku urusannya sama Database ya!"
- **`@Service`**: "Halo Spring, kelasku isinya logika bisnis, hitung-hitungan, dan ngecek syarat ya!"
- **`@RestController`**: "Halo Spring, kelasku ini pintunya API. Kalau ada *request* dari Frontend (JS/React), aku yang terima!"

**Yang Paling Spesial: `@RestController`**
Beda dengan controller biasa, dengan `@RestController`, apa pun data yang di-*return* oleh Java (entah itu *List*, *Object*, dll), **otomatis langsung diubah menjadi JSON**. Kamu tidak perlu konversi manual!

---

## 3. @Autowired: Gak Perlu Pusing Bikin Objek Baru
Di Java biasa, kalau mau pakai kelas lain, kamu harus bikin objeknya:
`Ssearch service = new Ssearch();`

**Apa Ajaibnya?**
Di Spring Boot, kamu cukup pakai:
```java
@Autowired
private Ssearch service;
```
Spring Boot akan otomatis mencarikan kelas `Ssearch`, membuatkan objeknya di belakang layar, lalu memberikannya ke kamu siap pakai. Konsep ini namanya **Dependency Injection**.

---

## 4. Keajaiban Nama Fungsi di Repository (Derived Query Methods)
Misal kamu butuh cari barang berdasarkan kategorinya. Normalnya kamu tulis SQL: `SELECT * FROM product WHERE kategori = ?`.

**Apa Ajaibnya?**
Di `JpaRepository`, kamu cukup bikin nama fungsi yang pas, dan SQL-nya dibuatkan otomatis!
Cukup tulis di interface:
```java
List<Product> findByKategori(String kategori);
```
Selesai! Spring Boot otomatis paham bahasa inggris `findBy...` dan menjadikannya *query SQL* di belakang layar tanpa perlu anotasi `@Query`!

---

## 5. @RequestParam dan @RequestBody
Ini yang menghubungkan data dari Frontend ke Backend.

- **`@RequestParam`**: Dipakai kalau Frontend ngirim data lewat URL. Contoh: `/products?search=baju`. Backend nangkap "baju" pakai `@RequestParam String search`.
- **`@RequestBody`**: Dipakai kalau Frontend ngirim data berupa bungkusan JSON yang panjang (seperti saat isi *form register*). Spring Boot akan **otomatis** memasukkan data JSON tersebut ke dalam *class DTO* yang kamu sediakan!

---
*Semoga catatan ini membantu kamu (Valen) dan tim untuk makin jago ngulik Spring Boot ya!*
