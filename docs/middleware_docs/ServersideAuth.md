# Server-Side Auth — Dokumentasi Keamanan

---

## Situasi Sebelumnya

Sebelum ada perubahan ini, ni website punya masalah cukup serius. Semua validasi "apakah user ini boleh melakukan ini?" cuma ada di JavaScript — alias di browser si pengunjung sendiri.

Masalahnya, browser itu milik pengunjung. Mereka bisa buka DevTools, lihat semua kode JS kita, dan langsung kirim request ke API kita tanpa lewat tampilan web sama sekali. Dengan tool seperti Postman atau curl, siapapun bisa nembak endpoint kita langsung. Dan karena Java tidak pernah ngecek "eh, siapa sih yang ngirim request ini?", semua request itu akan diproses tanpa masalah.

---

## Serangan yang Bisa Terjadi (dan Contohnya)

### 1. Nambahin Stok Sembarangan
**Target:** `PATCH /api/dashboard/{id}/stock`

Orang buka browser, tekan F12, masuk ke tab Console, ketik:
```javascript
fetch('/api/dashboard/3/stock', {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ jumlah: 9999 })
})
```
Hasilnya? Stok produk id=3 langsung jadi nambah 9999 tanpa ada yang tahu. Tidak perlu login, tidak perlu jadi admin, tinggal copy-paste di console.

---

### 2. Hapus Semua Produk Toko
**Target:** `DELETE /api/dashboard/{id}`

Sama persis. Tinggal ganti nomor id-nya dan loop satu-satu, semua produk bisa dihapus. Ini bisa merusak toko secara total.

---

### 3. Upload Produk Palsu
**Target:** `POST /api/dashboard/product`

Bisa bikin produk baru dengan nama, harga, dan gambar apapun tanpa perlu masuk ke halaman Dashboard Admin sama sekali.

---

### 4. Hapus Komentar Orang Lain dengan Pura-pura Jadi Admin
**Target:** `DELETE /api/product/comment/{id}?role=ADMIN`

Ini celahnya sedikit beda. Parameter `role` dikirim dari browser lewat URL. Jadi siapapun yang tahu formatnya tinggal tambahkan `?userId=1&role=ADMIN` di request dan bisa hapus komentar milik siapapun.

---

## Apa yang Ditambahkan dan Gimana Cara Kerjanya

### File 1 — `AdminInterceptor.java`
**Lokasi:** `src/main/java/.../Security/AdminInterceptor.java`

Ini adalah si "penjaga pintu" yang dipasang di depan semua endpoint Dashboard.

Cara kerjanya simpel: sebelum request masuk ke Controller manapun di bawah `/api/dashboard/`, si interceptor ini dicegat dulu ke sini. Dia baca dua header dari request yang datang:
- `X-User-Id` — berisi angka ID si pengirim
- `X-User-Role` — berisi role-nya, harus `ADMIN`

Kalau salah satu tidak ada, atau role-nya bukan ADMIN, dia langsung bales dengan **HTTP 403 Forbidden** dan request tidak dilanjutkan. Controller bahkan tidak tahu ada request yang datang.

```java
// Pengecekan intinya ada di sini
String role = request.getHeader("X-User-Role");
String userId = request.getHeader("X-User-Id");

if (userId == null || userId.isBlank() || !"ADMIN".equalsIgnoreCase(role)) {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    // request berhenti di sini, tidak dilanjutkan
    return false;
}
```

---

### File 2 — `WebConfig.java`
**Lokasi:** `src/main/java/.../Security/WebConfig.java`

Kalau `AdminInterceptor` itu si satpam, maka `WebConfig` ini yang nulis SK penugasannya dan nentuin dia jaga di mana.

Tanpa file ini, kelas Interceptor-nya udah ada tapi gak dipasang di mana-mana. `WebConfig` yang mendaftarkan interceptor ke Spring dan bilang: *"Kamu jaga semua request yang masuk ke `/api/dashboard/**`"*.

```java
registry.addInterceptor(adminInterceptor)
        .addPathPatterns("/api/dashboard/**");
```

Tanda `/**` artinya semua sub-path di bawahnya: `/api/dashboard/product`, `/api/dashboard/1/stock`, `/api/dashboard/1`, semuanya masuk dalam jangkauan penjagaan.

---

### File 3 — `Auth.js` (penambahan fungsi `getAuthHeaders`)
**Lokasi:** `src/main/resources/static/JS/Auth.js`

Ditambah satu fungsi baru di bagian paling bawah:

```javascript
function getAuthHeaders(extraHeaders = {}) {
    const session = getAuthSession();
    return {
        'X-User-Id': session ? String(session.userId) : '',
        'X-User-Role': session ? session.role : '',
        ...extraHeaders
    };
}
```

Fungsi ini tugasnya bikin "kartu identitas" dari data sesi yang tersimpan di localStorage. Setiap kali Dashboard mau nembak API yang sensitif, dia panggil fungsi ini untuk dapetin objek header yang berisi `X-User-Id` dan `X-User-Role` dari sesi yang lagi aktif.

Fungsi ini ditulis di `Auth.js` karena Auth.js sudah diload duluan dan berisi semua hal yang berhubungan dengan sesi login — jadi logis kalau helper ini ikut di sana juga.

---

### File 4 — `DashboardAdmin.html` (update fetch)
**Lokasi:** `src/main/resources/static/DashboardAdmin.html`

Tiga fetch call yang sebelumnya tidak pakai header apapun sekarang disisipin `getAuthHeaders()`:

**Hapus produk:**
```javascript
// sebelum
fetch(`/api/dashboard/${id}`, { method: 'DELETE' })

// sesudah
fetch(`/api/dashboard/${id}`, {
    method: 'DELETE',
    headers: getAuthHeaders()  // << kartu identitas dikirim
})
```

**Tambah stok:**
```javascript
// sebelum
headers: { 'Content-Type': 'application/json' }

// sesudah
headers: getAuthHeaders({ 'Content-Type': 'application/json' })
// getAuthHeaders menerima object extra, jadi Content-Type ikut digabung otomatis
```

**Upload produk baru:**
```javascript
// sebelum
fetch('/api/dashboard/product', { method: 'POST', body: formData })

// sesudah
fetch('/api/dashboard/product', {
    method: 'POST',
    headers: getAuthHeaders(),  // << kartu identitas dikirim
    body: formData
})
```

---

## Alur Lengkapnya Sekarang

```
[Admin buka Dashboard]
        |
        v
[Klik "Hapus Produk"]
        |
        v
[DashboardAdmin.html memanggil getAuthHeaders()]
        |
        v
[Auth.js baca localStorage → ambil userId & role]
        |
        v
[Fetch dikirim ke Java dengan header X-User-Id dan X-User-Role]
        |
        v
[AdminInterceptor.java mencegat request]
        |
    (cek header)
        |
   [Ada & ADMIN] ---------> [Request dilanjut ke Controller] → OK
        |
   [Tidak ada / bukan ADMIN] → [403 Forbidden, request ditolak]
```

---

## Yang Masih Perlu Diperhatikan

Solusi ini sudah jauh lebih baik dari sebelumnya, tapi ada satu kelemahan yang perlu diketahui: header `X-User-Role` dan `X-User-Id` masih bisa dipalsukan oleh orang yang cukup tahu teknisnya. Mereka bisa tambahkan header itu manual di request Postman mereka.

Kenapa belum sempurna? Karena Java tidak bisa memverifikasi apakah nilai `ADMIN` itu asli atau palsu — tidak ada tanda tangan kriptografis (yang ada di JWT). Untuk keamanan tingkat produksi, solusi selanjutnya adalah migrasi ke **JWT (JSON Web Token)** di mana Java sendiri yang "menandatangani" token saat login, sehingga tidak bisa dipalsukan.

Tapi untuk saat ini, implementasi ini sudah cukup mencegah serangan dari orang awam yang sekadar iseng buka DevTools.