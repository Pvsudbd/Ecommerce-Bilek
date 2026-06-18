const CHECKOUT_CART_ENDPOINT = '/api/cart';
const CHECKOUT_SUBMIT_ENDPOINT = '/api/checkout/submit';

let checkoutItems = [];
let checkoutSession = null;

const checkoutForm = document.getElementById('checkoutForm');
const checkoutItemsEl = document.getElementById('checkoutItems');
const checkoutSummaryEl = document.getElementById('checkoutSummary');
const checkoutMetaEl = document.getElementById('checkoutMeta');
const summaryItemsEl = document.getElementById('summaryItems');
const summaryTotalEl = document.getElementById('summaryTotal');
const checkoutButtonEl = document.getElementById('checkoutButton');
const checkoutMessageEl = document.getElementById('checkoutMessage');

function formatUsd(angka) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
        maximumFractionDigits: 0
    }).format(Number(angka) || 0);
}

function escapeHtml(value) {
    return String(value ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

function getPaymentMethodLabel(method) {
    const normalized = String(method || '').trim().toUpperCase();
    return normalized === 'TRANSFER' ? 'Transfer Bank' : 'EWallet';
}

function showMessage(message, isError = false) {
    if (!checkoutMessageEl) {
        return;
    }

    checkoutMessageEl.textContent = message || '';
    checkoutMessageEl.className = isError
        ? 'mt-3 text-sm font-medium text-rose-600'
        : 'mt-3 text-sm font-medium text-gray-500';
}

function requireSession() {
    const session = getAuthSession();

    if (!session) {
        window.location.href = 'Login.html?notice=auth-required&redirect=Checkout.html';
        return null;
    }

    return session;
}

function getCheckoutCustomerPayload() {
    const customerId = Number(checkoutSession?.userId);
    const normalizedCustomerId = Number.isFinite(customerId) && customerId > 0 ? customerId : null;
    const customerName = String(checkoutSession?.name || '').trim();

    return {
        customerId: normalizedCustomerId,
        customerName
    };
}

function normalizeItems(items) {
    return (Array.isArray(items) ? items : []).map((item) => ({
        productId: Number(item.productId) || 0,
        productName: String(item.productName || 'Produk'),
        quantity: Number(item.quantity) || 0,
        unitPrice: Number(item.unitPrice) || 0,
        totalPrice: Number(item.totalPrice) || 0
    })).filter((item) => item.productId > 0 && item.quantity > 0);
}

function setEmptyState() {
    if (checkoutItemsEl) {
        checkoutItemsEl.innerHTML = `
            <div class="rounded-2xl border border-dashed border-gray-300 px-4 py-10 text-center text-sm text-gray-500">
                Keranjang kosong. Silakan kembali belanja dulu.
            </div>
        `;
    }

    if (checkoutSummaryEl) {
        checkoutSummaryEl.innerHTML = '';
    }

    if (summaryItemsEl) {
        summaryItemsEl.textContent = '0';
    }

    if (summaryTotalEl) {
        summaryTotalEl.textContent = formatUsd(0);
    }

    if (checkoutButtonEl) {
        checkoutButtonEl.disabled = true;
    }
}

function renderCheckout() {
    const totalItems = checkoutItems.reduce((total, item) => total + item.quantity, 0);
    const totalPrice = checkoutItems.reduce((total, item) => total + item.totalPrice, 0);

    if (!checkoutItems.length) {
        setEmptyState();
        if (checkoutMetaEl) {
            checkoutMetaEl.textContent = 'Belum ada barang di keranjang.';
        }
        return;
    }

    if (checkoutItemsEl) {
        checkoutItemsEl.innerHTML = checkoutItems.map((item) => `
            <article class="flex items-center justify-between gap-4 rounded-2xl border border-gray-200 bg-gray-50 p-4">
                <div class="min-w-0">
                    <h3 class="truncate text-sm font-bold text-gray-900">${escapeHtml(item.productName)}</h3>
                    <p class="mt-1 text-xs text-gray-500">${item.quantity} x ${formatUsd(item.unitPrice)}</p>
                </div>
                <div class="shrink-0 text-sm font-black text-brand-blue">${formatUsd(item.totalPrice)}</div>
            </article>
        `).join('');
    }

    if (checkoutSummaryEl) {
        checkoutSummaryEl.innerHTML = `
            <div class="flex items-center justify-between text-sm text-gray-600">
                <span>Subtotal</span>
                <span class="font-bold text-gray-900">${formatUsd(totalPrice)}</span>
            </div>
            <div class="flex items-center justify-between text-sm text-gray-600">
                <span>Metode</span>
                <span id="paymentMethodLabel" class="font-bold text-gray-900">EWallet</span>
            </div>
            <div class="rounded-2xl bg-brand-blue/5 p-4 text-sm text-brand-blue">
                Barang akan otomatis hilang dari keranjang setelah checkout berhasil dan stok produk akan berkurang di database.
            </div>
        `;
    }

    if (summaryItemsEl) {
        summaryItemsEl.textContent = String(totalItems);
    }

    if (summaryTotalEl) {
        summaryTotalEl.textContent = formatUsd(totalPrice);
    }

    if (checkoutMetaEl) {
        if (checkoutSession && checkoutSession.address) {
            checkoutMetaEl.textContent = `Pesanan akan dikirim ke ${checkoutSession.address}. ${totalItems} item siap dibayar.`;
        } else {
            checkoutMetaEl.textContent = `${totalItems} item siap dibayar.`;
        }
    }

    if (checkoutButtonEl) {
        checkoutButtonEl.disabled = false;
    }

    const paymentMethodInputs = Array.from(document.querySelectorAll('input[name="paymentMethod"]'));
    const paymentMethodLabel = document.getElementById('paymentMethodLabel');

    paymentMethodInputs.forEach((input) => {
        input.addEventListener('change', () => {
            if (paymentMethodLabel) {
                paymentMethodLabel.textContent = getPaymentMethodLabel(input.value);
            }
        });
    });
}

async function loadCheckoutData() {
    checkoutSession = requireSession();
    if (!checkoutSession) {
        return;
    }

    try {
        const customerPayload = getCheckoutCustomerPayload();
        const params = new URLSearchParams();

        if (customerPayload.customerId) {
            params.set('customerId', String(customerPayload.customerId));
        } else if (customerPayload.customerName) {
            params.set('customerName', customerPayload.customerName);
        }

        const response = await fetch(`${CHECKOUT_CART_ENDPOINT}?${params.toString()}`, {
            headers: {
                Accept: 'application/json'
            }
        });

        const contentType = response.headers.get('content-type') || '';
        const data = contentType.includes('application/json')
            ? await response.json()
            : { success: false };

        if (!response.ok || !data.success) {
            checkoutItems = [];
            renderCheckout();
            showMessage(data.message || 'Keranjang tidak dapat dimuat.', true);
            return;
        }

        checkoutItems = normalizeItems(data.items || []);
        renderCheckout();
    } catch (error) {
        checkoutItems = [];
        renderCheckout();
        showMessage('Gagal memuat data checkout.', true);
    }
}

function getSelectedPaymentMethod() {
    const checked = document.querySelector('input[name="paymentMethod"]:checked');
    return checked ? checked.value : 'EWALLET';
}

async function handleCheckoutSubmit(event) {
    event.preventDefault();

    if (!checkoutItems.length) {
        showMessage('Keranjang kosong, tidak bisa checkout.', true);
        return;
    }

    if (!checkoutSession) {
        checkoutSession = requireSession();
        if (!checkoutSession) {
            return;
        }
    }

    if (checkoutButtonEl) {
        checkoutButtonEl.disabled = true;
        checkoutButtonEl.textContent = 'Memproses...';
    }

    try {
        const customerPayload = getCheckoutCustomerPayload();
        const response = await fetch(CHECKOUT_SUBMIT_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                Accept: 'application/json'
            },
            body: JSON.stringify({
                customerId: customerPayload.customerId,
                customerName: customerPayload.customerName,
                paymentMethod: getSelectedPaymentMethod(),
                shippingAddress: checkoutSession.address || '',
                items: checkoutItems
            })
        });

        const contentType = response.headers.get('content-type') || '';
        const data = contentType.includes('application/json')
            ? await response.json()
            : { success: false, message: await response.text() };

        if (!response.ok || !data.success) {
            showMessage(data.message || 'Checkout gagal.', true);
            return;
        }

        window.location.replace('Mainpage.html');
        return;
    } catch (error) {
        showMessage('Tidak dapat terhubung ke server.', true);
    } finally {
        if (checkoutButtonEl) {
            checkoutButtonEl.disabled = checkoutItems.length === 0;
            checkoutButtonEl.textContent = 'Bayar Sekarang';
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    renderMainpageUserNav();
    loadCheckoutData();

    if (checkoutForm) {
        checkoutForm.addEventListener('submit', handleCheckoutSubmit);
    }
});
