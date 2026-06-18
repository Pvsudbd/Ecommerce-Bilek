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

function getActiveStorage(persistent) {
    return persistent ? window.localStorage : window.sessionStorage;
}

function getStoredValue(key) {
    try {
        return window.sessionStorage.getItem(key) ?? window.localStorage.getItem(key);
    } catch (error) {
        return null;
    }
}

function isTruthyStoredValue(value) {
    if (value === null || value === undefined) {
        return false;
    }

    const normalized = String(value).trim().toLowerCase();
    return normalized !== '' && normalized !== 'false' && normalized !== '0' && normalized !== 'null';
}

function buildAvatarUrl(name) {
    const safeName = String(name || 'User').trim() || 'User';
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(safeName)}&background=4338ca&color=ffffff&size=128&bold=true`;
}

function normalizeRole(role) {
    return String(role || '').trim().toUpperCase();
}

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

function isUserLoggedIn() {
    return getAuthSession() !== null;
}

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

function logout(redirectUrl) {
    clearAuthSession();
    window.location.href = redirectUrl || 'Login.html';
}

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
