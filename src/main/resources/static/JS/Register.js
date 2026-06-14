const REGISTER_ENDPOINT = '/api/register';

function showNotice(type, title, message) {
    const notice = document.getElementById('registerNotice');
    const titleEl = document.getElementById('registerNoticeTitle');
    const messageEl = document.getElementById('registerNoticeMessage');
    const badgeEl = document.getElementById('registerNoticeBadge');
    const iconWrap = document.getElementById('registerNoticeIcon');

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

function hideNotice() {
    const notice = document.getElementById('registerNotice');
    if (!notice) {
        return;
    }

    notice.classList.add('opacity-0', 'translate-y-4');
    window.setTimeout(() => notice.classList.add('hidden'), 220);
}

function setFieldError(input, message) {
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

function clearFieldErrors() {
    ['nameInput', 'addressInput', 'passwordInput', 'confirmInput'].forEach((id) => {
        const input = document.getElementById(id);
        if (input) {
            setFieldError(input, '');
        }
    });
}

function validateForm({ name, address, password, confirmPassword, termsAccepted }) {
    clearFieldErrors();

    let valid = true;

    if (!name) {
        setFieldError(document.getElementById('nameInput'), 'Username wajib diisi.');
        valid = false;
    }

    if (!address) {
        setFieldError(document.getElementById('addressInput'), 'Alamat wajib diisi.');
        valid = false;
    }

    if (!password) {
        setFieldError(document.getElementById('passwordInput'), 'Password wajib diisi.');
        valid = false;
    } else if (password.length < 8) {
        setFieldError(document.getElementById('passwordInput'), 'Password minimal 8 karakter.');
        valid = false;
    }

    if (!confirmPassword) {
        setFieldError(document.getElementById('confirmInput'), 'Konfirmasi password wajib diisi.');
        valid = false;
    } else if (password !== confirmPassword) {
        setFieldError(document.getElementById('confirmInput'), 'Konfirmasi password tidak sama.');
        valid = false;
    }

    if (!termsAccepted) {
        showNotice('error', 'Syarat belum disetujui', 'Centang persetujuan Terms of Service dan Privacy Policy terlebih dahulu.');
        valid = false;
    }

    return valid;
}


async function handleRegisterSubmit(event) {
    event.preventDefault();

    const form = event.currentTarget;
    const submitButton = document.getElementById('registerButton');
    const name = document.getElementById('nameInput').value.trim();
    const address = document.getElementById('addressInput').value.trim();
    const password = document.getElementById('passwordInput').value;
    const confirmPassword = document.getElementById('confirmInput').value;
    const termsAccepted = document.getElementById('termsCheckbox').checked;

    if (!validateForm({ name, address, password, confirmPassword, termsAccepted })) {
        return;
    }

    const defaultLabel = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.textContent = 'Memproses...';

    try {
        const response = await fetch(REGISTER_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name,
                password,
                address
            })
        });

        const data = await response.json();

        if (!response.ok || !data.success) {
            showNotice(
                'error',
                'Register gagal',
                data.message || 'Terjadi kesalahan saat mendaftar. Coba lagi.'
            );
            return;
        }

        saveAuthSession({
            name: data.name || name,
            role: data.role || 'CUSTOMER',
            address: data.address || address,
            avatarUrl: buildAvatarUrl(data.name || name)
        });

        const destination = getRedirectUrl(data.role || 'CUSTOMER');

        showNotice(
            'success',
            'Akun berhasil dibuat',
            'Kamu akan diarahkan ke halaman utama sebagai customer.'
        );

        window.setTimeout(() => {
            window.location.href = destination;
        }, 1200);
    } catch (error) {
        showNotice(
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

function checkStrength(value) {
    const bars = [
        document.getElementById('bar1'),
        document.getElementById('bar2'),
        document.getElementById('bar3'),
        document.getElementById('bar4')
    ];
    const label = document.getElementById('strengthLabel');

    let score = 0;
    if (value.length >= 8) score++;
    if (/[A-Z]/.test(value)) score++;
    if (/[0-9]/.test(value)) score++;
    if (/[^A-Za-z0-9]/.test(value)) score++;

    const colors = ['#ef4444', '#f97316', '#eab308', '#22c55e'];
    const labels = ['Weak', 'Fair', 'Good', 'Strong'];

    bars.forEach((bar, index) => {
        if (!bar) {
            return;
        }

        if (value.length === 0) {
            bar.style.background = '#f3f4f6';
        } else {
            bar.style.background = index < score ? colors[score - 1] : '#f3f4f6';
        }
    });

    if (!label) {
        return;
    }

    label.textContent = value.length > 0 ? `Password strength: ${labels[score - 1] || 'Weak'}` : '';
    label.style.color = value.length > 0 ? colors[score - 1] : '#9ca3af';
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('registerForm');
    const closeButton = document.getElementById('registerNoticeClose');
    const passwordInput = document.getElementById('passwordInput');

    if (form) {
        form.addEventListener('submit', handleRegisterSubmit);
    }

    if (closeButton) {
        closeButton.addEventListener('click', hideNotice);
    }

    if (passwordInput) {
        passwordInput.addEventListener('input', (event) => {
            checkStrength(event.target.value);
        });
    }
});
