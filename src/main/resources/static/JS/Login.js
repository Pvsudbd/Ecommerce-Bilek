const LOGIN_ENDPOINT = '/api/login';

function showLoginNotice(type, title, message) {
    const notice = document.getElementById('loginNotice');
    const titleEl = document.getElementById('loginNoticeTitle');
    const messageEl = document.getElementById('loginNoticeMessage');
    const badgeEl = document.getElementById('loginNoticeBadge');
    const iconWrap = document.getElementById('loginNoticeIcon');

    if (!notice || !titleEl || !messageEl || !badgeEl || !iconWrap) {
        return;
    }

    const isSuccess = type === 'success';

    badgeEl.textContent = isSuccess ? 'Berhasil' : 'Perhatian';
    badgeEl.className = isSuccess
        ? 'text-[11px] font-bold uppercase tracking-[0.28em] text-emerald-500'
        : 'text-[11px] font-bold uppercase tracking-[0.28em] text-rose-500';

    titleEl.textContent = title;
    messageEl.textContent = message;

    iconWrap.className = isSuccess
        ? 'absolute inset-2 flex items-center justify-center rounded-2xl bg-emerald-600 text-white shadow-lg'
        : 'absolute inset-2 flex items-center justify-center rounded-2xl bg-rose-600 text-white shadow-lg';

    notice.classList.remove('hidden');
    requestAnimationFrame(() => {
        notice.classList.remove('opacity-0', 'translate-y-4');
    });
}

function hideLoginNotice() {
    const notice = document.getElementById('loginNotice');
    if (!notice) {
        return;
    }

    notice.classList.add('opacity-0', 'translate-y-4');
    window.setTimeout(() => notice.classList.add('hidden'), 220);
}

function setLoginFieldError(input, message) {
    const errorEl = document.getElementById(`${input.id}Error`);
    if (!errorEl) {
        return;
    }

    if (message) {
        input.classList.add('border-rose-400', 'focus:ring-rose-500', 'focus:border-rose-500');
        input.classList.remove('border-gray-200', 'focus:ring-indigo-500', 'focus:border-indigo-500');
        errorEl.textContent = message;
        errorEl.classList.remove('hidden');
        return;
    }

    input.classList.remove('border-rose-400', 'focus:ring-rose-500', 'focus:border-rose-500');
    input.classList.add('border-gray-200', 'focus:ring-indigo-500', 'focus:border-indigo-500');
    errorEl.textContent = '';
    errorEl.classList.add('hidden');
}

function validateLoginForm(name, password) {
    const nameInput = document.getElementById('nameInput');
    const passwordInput = document.getElementById('passwordInput');
    let valid = true;

    setLoginFieldError(nameInput, '');
    setLoginFieldError(passwordInput, '');

    if (!name) {
        setLoginFieldError(nameInput, 'Username wajib diisi.');
        valid = false;
    }

    if (!password) {
        setLoginFieldError(passwordInput, 'Password wajib diisi.');
        valid = false;
    }

    return valid;
}

function getLoginRedirectParam() {
    const params = new URLSearchParams(window.location.search);
    return params.get('redirect') || '';
}

async function handleLoginSubmit(event) {
    event.preventDefault();

    const submitButton = document.getElementById('loginButton');
    const name = document.getElementById('nameInput').value.trim();
    const password = document.getElementById('passwordInput').value;
    const rememberMe = document.getElementById('rememberCheckbox').checked;
    const redirectParam = getLoginRedirectParam();

    if (!validateLoginForm(name, password)) {
        return;
    }

    const defaultLabel = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = 'Memproses...';

    try {
        const response = await fetch(LOGIN_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name, password })
        });

        const data = await response.json();

        if (!response.ok || !data.success) {
            showLoginNotice(
                'error',
                'Login gagal',
                data.message || 'Username atau password salah.'
            );
            return;
        }

        saveAuthSession({
            name: data.name || name,
            role: data.role,
            address: data.address || '',
            avatarUrl: buildAvatarUrl(data.name || name)
        }, { persistent: rememberMe });

        const destination = getRedirectUrl(data.role, redirectParam);
        const roleLabel = normalizeRole(data.role) === 'ADMIN' ? 'admin dashboard' : 'halaman utama';

        showLoginNotice(
            'success',
            'Login berhasil',
            `Kamu akan diarahkan ke ${roleLabel}.`
        );

        window.setTimeout(() => {
            window.location.href = destination;
        }, 900);
    } catch (error) {
        showLoginNotice(
            'error',
            'Koneksi bermasalah',
            'Tidak dapat terhubung ke server. Periksa koneksi lalu coba lagi.'
        );
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = defaultLabel;
    }
}

function togglePasswordVisibility(inputId, eyeOffId, eyeOnId) {
    const input = document.getElementById(inputId);
    const eyeOff = document.getElementById(eyeOffId);
    const eyeOn = document.getElementById(eyeOnId);

    if (!input || !eyeOff || !eyeOn) {
        return;
    }

    const isHidden = input.type === 'password';
    input.type = isHidden ? 'text' : 'password';
    eyeOff.classList.toggle('hidden', !isHidden);
    eyeOn.classList.toggle('hidden', isHidden);
}

function initAuthRequiredNotice() {
    const params = new URLSearchParams(window.location.search);

    if (params.get('notice') !== 'auth-required') {
        return;
    }

    if (window.history && window.history.replaceState) {
        const redirect = params.get('redirect');
        const nextParams = new URLSearchParams();
        if (redirect) {
            nextParams.set('redirect', redirect);
        }
        const query = nextParams.toString();
        window.history.replaceState({}, '', query ? `${window.location.pathname}?${query}` : window.location.pathname);
    }

    showLoginNotice(
        'error',
        'Login/Register dulu',
        'Silakan login atau daftar dulu supaya kamu bisa lanjut ke fitur yang dipilih.'
    );
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    const closeButton = document.getElementById('loginNoticeClose');

    if (form) {
        form.addEventListener('submit', handleLoginSubmit);
    }

    if (closeButton) {
        closeButton.addEventListener('click', hideLoginNotice);
    }

    initAuthRequiredNotice();
});
