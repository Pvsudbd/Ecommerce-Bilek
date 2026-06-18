# Session & Cookie: Gimana Sih Cara Kerjanya?

Pernah kepikiran nggak, pas kamu buka Tokopedia atau Shopee, kenapa sehabis login kamu nggak perlu login lagi tiap pindah-pindah halaman? Padahal setiap kali kamu klik link, browser itu sebenarnya ngirim permintaan baru yang ibaratnya "Nggak kenal siapa kamu".

Nah, biar webnya punya ingatan (nggak pikun), diciptakanlah yang namanya **Session** dan **Cookie**. 

Biar gampang bayanginnya, mari kita pakai **Analogi Nonton Bioskop / Masuk Kelab Malam**.

---

### 1. Proses Login (Minta Tiket)
Kamu datang ke depan pintu bioskop (Website). Satpam di depan nanya, "Siapa nama dan password lo?". Kamu ngasih KTP (Username & Password) ke satpam. 
Satpam ngecek KTP kamu ke daftar tamu (*Database*). Oh ternyata bener, namamu ada.

### 2. Session (Buku Catatan Satpam)
Karena Satpam ini nggak mau repot nginget-nginget muka semua orang yang masuk, dia bikin catatan di buku tebelnya (ini yang disebut **Session di Server**). 
Di bukunya dia nulis: 
*"Tamu No. 8921 adalah si Budi, umurnya 20 tahun, dan dia boleh masuk teater VIP"*.

### 3. Cookie (Gelang / Tiket Kertas Buat Kamu)
Nah, gimana caranya pas Budi mau beli popcorn di dalem, si penjual tau kalau itu Budi? 
Satpam tadi nggak mungkin kan ikutin Budi ke mana-mana. Jadi, Satpam ngasih **Gelang Kertas** ke tangannya Budi yang ada tulisan *"Tamu No. 8921"*.
Gelang inilah yang disebut **Cookie**. Gelang ini cuma nempel di tanganmu (disimpan di *Browser*).

### 4. Buka Halaman Lain (Nunjukin Gelang)
Sekarang Budi mau masuk toilet atau beli popcorn (Buka halaman web lain). Tiap Budi jalan ke penjual popcorn, penjualnya tinggal liat tangan Budi: *"Oh, dia pakai gelang No. 8921"*. 
Lalu si penjual telpon Satpam depan: *"Eh Pam, No. 8921 ini siapa sih?"*. Satpam buka bukunya (Session) dan jawab: *"Oh itu Budi, dia tamu VIP"*.
Maka Budi dilayani tanpa harus ditanya KTP-nya lagi dari awal. Mantap kan?

### 5. Logout (Gunting Gelang)
Pas Budi mau pulang, dia bilang ke Satpam, "Gue cabut ya!".
Satpam bakal nyoret nama Budi dari buku catatannya (Session dihapus di server). Terus gelang Budi digunting dan dibuang (Cookie dihapus dari browser).
Besoknya kalau Budi mau masuk lagi dan bawa gelang bekas yang udah disambung selotip, Satpam bakal cek buku: *"Lho, No. 8921 udah dicoret kemarin. Elu harus kasih KTP lagi dari awal!"*. Ini yang bikin *session hijacking* susah kalau udah di-*logout*.

---

### Terus, Bedanya Sama LocalStorage (Yang Kita Pakai Sekarang)?
Di aplikasi kita yang sekarang (di file `Auth.js`), kita pakai konsep **LocalStorage** dan **SessionStorage**. 
Kalau pakai Cookie asli, server (Java) yang bikinin gelang terus masangin langsung ke tanganmu secara paksa, kamu nggak usah ngapa-ngapain. Tiap pindah halaman, tanganmu otomatis nyodorin gelang itu ke server.

Tapi di `Auth.js`, kita pakai model **Self-Service**. Server cuma ngasih secarik kertas *"Ini data lu Budi"*. Terus Javascript di browser ngambil kertas itu, nyatet sendiri di dompet (`localStorage`). Pas mau ngakses fitur rahasia, si Javascript sendiri yang buka dompet, liat kertasnya, terus bilang *"Oke, lo admin, silakan lewat"*. 

Kekurangannya? Kalau datanya dimanipulasi sama *hacker* dari dalam dompet (Browser), web bisa aja ketipu, kecuali Server (Java) ikut ngecek ulang tiap kali ada permintaan *database*.
