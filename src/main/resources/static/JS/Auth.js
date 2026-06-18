const AUTH_STORAGE_KEYS = [
    'isLoggedIn',
    'loggedIn',
    'currentUser',
    'userId',
    'role',
    'userRole',
    'currentUserRole',
    'userAddress',
    'userAvatar'
];

// Ngecek mau nyimpen data sesi di localStorage (permanen) atau sessionStorage (ilang kalau tab ditutup)
function getActiveStorage(persistent) {
    return persistent ? window.localStorage : window.sessionStorage;
}

// Ngambil data dari storage (mencari di session dulu, kalau nggak ada baru cari di local)
function getStoredValue(key) {
    try {
        return window.sessionStorage.getItem(key) ?? window.localStorage.getItem(key);
    } catch (error) {
        return null;
    }
}

// Ngecek apakah suatu nilai itu beneran ada isinya (bukan null, false, atau kosong melompong)
function isTruthyStoredValue(value) {
    if (value === null || value === undefined) {
        return false;
    }

    const normalized = String(value).trim().toLowerCase();
    return normalized !== '' && normalized !== 'false' && normalized !== '0' && normalized !== 'null';
}

// Bikin URL gambar avatar otomatis berdasarkan nama user pakai API ui-avatars
function buildAvatarUrl(name) {
    const safeName = String(name || 'User').trim() || 'User';
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(safeName)}&background=4338ca&color=ffffff&size=128&bold=true`;
}

// Bikin teks role/jabatan jadi huruf besar semua dan hapus spasi berlebih
function normalizeRole(role) {
    return String(role || '').trim().toUpperCase();
}

// Nyimpen data login user ke dalam browser storage biar pas di-refresh nggak perlu login lagi
function saveAuthSession(userData, options = {}) {
    const persistent = Boolean(options.persistent);
    const storage = getActiveStorage(persistent);
    const otherStorage = getActiveStorage(!persistent);

    const payload = {
        isLoggedIn: 'true',
        loggedIn: 'true',
        currentUser: userData.name || '',
        userId: String(userData.userId || ''),
        role: normalizeRole(userData.role),
        userRole: normalizeRole(userData.role),
        currentUserRole: normalizeRole(userData.role),
        userAddress: userData.address || '',
        userAvatar: userData.avatarUrl || buildAvatarUrl(userData.name)
    };

    AUTH_STORAGE_KEYS.forEach((key) => {
        try {
            otherStorage.removeItem(key);
        } catch (error) {
            console.warn('Gagal menghapus session auth:', error);
        }
    });

    Object.entries(payload).forEach(([key, value]) => {
        try {
            storage.setItem(key, value);
        } catch (error) {
            console.warn('Gagal menyimpan session auth:', error);
        }
    });
}

// Ngunjungi storage buat ngambil semua data user yang lagi aktif login saat ini
function getAuthSession() {
    const isLoggedIn = isTruthyStoredValue(getStoredValue('isLoggedIn'))
        || isTruthyStoredValue(getStoredValue('loggedIn'));

    if (!isLoggedIn) {
        return null;
    }

    const name = getStoredValue('currentUser') || '';
    const userId = getStoredValue('userId') || '';
    const role = normalizeRole(getStoredValue('role') || getStoredValue('userRole') || getStoredValue('currentUserRole'));
    const address = getStoredValue('userAddress') || '';
    const avatarUrl = getStoredValue('userAvatar') || buildAvatarUrl(name);

    return {
        name,
        userId,
        role,
        address,
        avatarUrl
    };
}

// Fungsi simpel buat nanya "Eh, ada yang lagi login nggak nih?" (Ngeluarin True/False)
function isUserLoggedIn() {
    return getAuthSession() !== null;
}

// Nentuin user harus dilempar ke halaman mana abis login (Admin ke dashboard, user biasa ke mainpage)
function getRedirectUrl(role, fallbackRedirect) {
    const normalizedRole = normalizeRole(role);

    if (normalizedRole === 'ADMIN') {
        return 'DashboardAdmin.html';
    }

    if (fallbackRedirect && !String(fallbackRedirect).includes('DashboardAdmin')) {
        return fallbackRedirect;
    }

    return 'Mainpage.html';
}

// Bersih-bersih! Ngilangin semua jejak login dari memori browser
function clearAuthSession() {
    AUTH_STORAGE_KEYS.forEach((key) => {
        try {
            window.sessionStorage.removeItem(key);
            window.localStorage.removeItem(key);
        } catch (error) {
            console.warn('Gagal menghapus session auth:', error);
        }
    });
}

// Tombol keluar: Bersihin data, terus lempar balik ke halaman Login
function logout(redirectUrl) {
    clearAuthSession();
    window.location.href = redirectUrl || 'Login.html';
}

// Ngatur tampilan pojok kanan atas di Mainpage (Nampilin foto profil kalau login, nampilin tombol login kalau belum)
function renderMainpageUserNav() {
    const loginLink = document.getElementById('loginNavLink');
    const userMenu = document.getElementById('userMenu');
    const userAvatar = document.getElementById('userAvatar');
    const userName = document.getElementById('userName');
    const userDropdownAvatar = document.getElementById('userDropdownAvatar');
    const userDropdownName = document.getElementById('userDropdownName');
    const userRoleBadge = document.getElementById('userRoleBadge');
    const userAddress = document.getElementById('userAddress');
    const userMenuButton = document.getElementById('userMenuButton');
    const userDropdown = document.getElementById('userDropdown');

    if (!loginLink || !userMenu) {
        return;
    }

    const session = getAuthSession();

    const promoBannerGuest = document.getElementById('promoBannerGuest');
    const promoBannerMember = document.getElementById('promoBannerMember');

    if (!session) {
        loginLink.classList.remove('hidden');
        userMenu.classList.add('hidden');
        if (promoBannerGuest) promoBannerGuest.classList.remove('hidden');
        if (promoBannerMember) promoBannerMember.classList.add('hidden');
        return;
    }

    loginLink.classList.add('hidden');
    userMenu.classList.remove('hidden');
    if (promoBannerGuest) promoBannerGuest.classList.add('hidden');
    if (promoBannerMember) promoBannerMember.classList.remove('hidden');

    if (userAvatar) {
        userAvatar.src = session.avatarUrl;
        userAvatar.alt = `Foto profil ${session.name}`;
    }

    if (userDropdownAvatar) {
        userDropdownAvatar.src = session.avatarUrl;
        userDropdownAvatar.alt = `Foto profil ${session.name}`;
    }

    if (userName) {
        userName.textContent = session.name;
    }

    if (userDropdownName) {
        userDropdownName.textContent = session.name;
    }

    if (userRoleBadge) {
        userRoleBadge.textContent = session.role === 'ADMIN' ? 'Admin' : 'Customer';
    }

    if (userAddress) {
        if (session.address) {
            userAddress.textContent = session.address;
            userAddress.classList.remove('hidden');
        } else {
            userAddress.textContent = '';
            userAddress.classList.add('hidden');
        }
    }

    const adminDashboardLink = document.getElementById('adminDashboardLink');
    if (adminDashboardLink) {
        if (session.role === 'ADMIN') {
            adminDashboardLink.classList.remove('hidden');
        } else {
            adminDashboardLink.classList.add('hidden');
        }
    }

    if (userMenuButton && userDropdown) {
        userMenuButton.onclick = () => {
            userDropdown.classList.toggle('hidden');
        };

        document.addEventListener('click', (event) => {
            if (!userMenu.contains(event.target)) {
                userDropdown.classList.add('hidden');
            }
        });
    }
}

// Biar non admin gak bisa ngakses dashboard
function guardAdminAccess() {
    const session = getAuthSession();

    if (!session) {
        window.location.href = 'Login.html?notice=auth-required&redirect=DashboardAdmin.html';
        return false;
    }

    if (session.role !== 'ADMIN') {
        window.location.href = 'Mainpage.html';
        return false;
    }

    return true;
}

// Ngatur foto profil dan nama di header halaman Dashboard Admin
function renderAdminHeader() {
    const session = getAuthSession();
    const avatar = document.getElementById('adminAvatar');
    const name = document.getElementById('adminName');
    const role = document.getElementById('adminRole');

    if (!session) {
        return;
    }

    if (avatar) {
        avatar.src = session.avatarUrl;
        avatar.alt = `Foto profil ${session.name}`;
    }

    if (name) {
        name.textContent = session.name;
    }

    if (role) {
        role.textContent = 'Administrator';
    }
}
