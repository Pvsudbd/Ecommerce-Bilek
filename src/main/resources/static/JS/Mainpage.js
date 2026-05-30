const JAVA_SEARCH_ENDPOINT = '/products';
const PAGE_SIZE = 15;

let products = [];
let currentCategory = 'all';
let currentPage = 1;
let cart = {};
let wishlist = {};
let activeDrawer = 'cart';
let loadError = false;
const productCache = new Map();

const searchForm = document.getElementById('searchForm');
const searchInput = document.getElementById('searchInput');
const sortSelect = document.getElementById('sortSelect');
const priceRange = document.getElementById('priceRange');
const priceLabel = document.getElementById('priceLabel');

const formatUsd = (angka) =>
    new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
        maximumFractionDigits: 0
    }).format(Number(angka) || 0);

function escapeHtml(value) {
    return String(value ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

function normalizeText(value) {
    return String(value ?? '').trim().toLowerCase();
}

function normalizeProduct(product) {
    if (!product) {
        return null;
    }

    const id = product.id ?? product.idProduct ?? product.id_product;
    const name = product.name ?? product.namaProduct ?? product.nama_product ?? 'Produk';
    const price = product.price ?? product.harga ?? 0;
    const category = product.category ?? product.kategori ?? 'Lainnya';
    const stock = product.stock ?? product.stok ?? 0;

    if (id === undefined || id === null) {
        return null;
    }

    return {
        id: Number(id),
        name: String(name),
        price: Number(price) || 0,
        category: String(category),
        stock: Number(stock) || 0,
        imageUrl: product.imageUrl ?? product.image_url ?? product.foto_produk ?? null
    };
}

function getProductById(id) {
    return productCache.get(String(id))
        || products.find((product) => String(product.id) === String(id))
        || null;
}

function getInitials(name) {
    const words = String(name ?? '')
        .trim()
        .split(/\s+/)
        .filter(Boolean);

    if (words.length === 0) {
        return 'P';
    }

    if (words.length === 1) {
        return words[0].slice(0, 2).toUpperCase();
    }

    return `${words[0][0]}${words[1][0]}`.toUpperCase();
}

function updatePriceLabel() {
    if (!priceLabel || !priceRange) {
        return;
    }

    const value = Number(priceRange.value) || 0;

    if (value >= 1000000) {
        priceLabel.textContent = `$${(value / 1000000).toFixed(1)}M`;
        return;
    }

    priceLabel.textContent = `$${Math.round(value / 1000)}K`;
}

async function fetchProductsFromJava(keyword = '') {
    currentPage = 1;

    const url = keyword
        ? `${JAVA_SEARCH_ENDPOINT}?search=${encodeURIComponent(keyword)}`
        : JAVA_SEARCH_ENDPOINT;

    try {
        const response = await fetch(url, {
            headers: {
                Accept: 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const data = await response.json();
        const normalizedProducts = Array.isArray(data)
            ? data.map(normalizeProduct).filter(Boolean)
            : [];

        normalizedProducts.forEach((product) => {
            productCache.set(String(product.id), product);
        });

        products = normalizedProducts;
        loadError = false;
        renderProducts();
    } catch (error) {
        console.warn('Gagal memuat produk:', error);
        products = [];
        loadError = true;
        renderProducts();
    }
}

function setCategory(cat) {
    currentCategory = cat;
    currentPage = 1;

    document.querySelectorAll('.cat-btn').forEach((btn) => {
        btn.classList.remove('bg-brand-blue', 'text-white', 'border-brand-blue');
        btn.classList.add('bg-white', 'text-black', 'border-gray-300');
    });

    const activeBtn = document.getElementById(`cat-${cat}`);
    if (activeBtn) {
        activeBtn.classList.remove('bg-white', 'text-black', 'border-gray-300');
        activeBtn.classList.add('bg-brand-blue', 'text-white', 'border-brand-blue');
    }

    renderProducts();
}

function matchesCategory(productCategory) {
    if (currentCategory === 'all') {
        return true;
    }

    const source = normalizeText(productCategory).replace(/[_-]+/g, ' ');
    const target = normalizeText(currentCategory).replace(/[_-]+/g, ' ');

    if (!source || !target) {
        return false;
    }

    return source === target || source.includes(target) || target.includes(source);
}

function renderProducts() {
    const grid = document.getElementById('productGrid');
    const empty = document.getElementById('emptyState');
    const pagination = document.getElementById('pagination');

    if (!grid || !empty || !pagination) {
        return;
    }

    let filtered = [...products];

    const maxPrice = Number(priceRange?.value || Number.MAX_SAFE_INTEGER);
    const sortType = sortSelect?.value || 'default';

    filtered = filtered.filter((product) => matchesCategory(product.category));
    filtered = filtered.filter((product) => product.price <= maxPrice);

    if (sortType === 'name-asc') {
        filtered.sort((a, b) => a.name.localeCompare(b.name));
    } else if (sortType === 'name-desc') {
        filtered.sort((a, b) => b.name.localeCompare(a.name));
    } else if (sortType === 'price-asc') {
        filtered.sort((a, b) => a.price - b.price);
    } else if (sortType === 'price-desc') {
        filtered.sort((a, b) => b.price - a.price);
    }

    const totalItems = filtered.length;
    const totalPages = Math.max(1, Math.ceil(totalItems / PAGE_SIZE));

    if (currentPage > totalPages) {
        currentPage = totalPages;
    }

    if (currentPage < 1) {
        currentPage = 1;
    }

    const startIndex = (currentPage - 1) * PAGE_SIZE;
    const visibleProducts = filtered.slice(startIndex, startIndex + PAGE_SIZE);

    grid.innerHTML = '';

    if (totalItems === 0) {
        empty.classList.remove('hidden');
        empty.textContent = loadError
            ? 'Gagal memuat produk dari database.'
            : 'Produk tidak ditemukan atau belum berhasil dimuat.';
        pagination.innerHTML = '';
        updateBadges();
        renderDrawer();
        return;
    }

    empty.classList.add('hidden');

    visibleProducts.forEach((product) => {
        const inWishlist = Boolean(wishlist[product.id]);
        const inCart = Boolean(cart[product.id]);
        const categoryLabel = product.category || 'Produk';
        const stockLabel = product.stock > 0 ? `${product.stock} stok` : 'Stok habis';
        const initials = getInitials(product.name);
        const media = product.imageUrl
            ? `<img src="${escapeHtml(product.imageUrl)}" alt="${escapeHtml(product.name)}" class="w-full h-full object-cover"/>`
            : `
                <div class="absolute inset-0 bg-[radial-gradient(circle_at_top,rgba(67,56,202,0.12),transparent_50%)]"></div>
                <div class="relative flex h-full w-full items-center justify-center p-6">
                    <div class="flex h-24 w-24 items-center justify-center rounded-3xl bg-black text-2xl font-black tracking-widest text-white shadow-lg">
                        ${escapeHtml(initials)}
                    </div>
                </div>
            `;

        grid.innerHTML += `
        <article class="group relative flex flex-col overflow-hidden border border-gray-200 bg-white transition duration-200 hover:-translate-y-1 hover:border-brand-blue hover:shadow-lg">
            <div class="relative aspect-[4/5] overflow-hidden bg-gradient-to-br from-gray-100 via-white to-gray-200">
                <button onclick="toggleWishlist(${product.id})"
                    class="absolute right-3 top-3 z-10 inline-flex h-9 w-9 items-center justify-center rounded-full bg-white/90 shadow-sm transition ${inWishlist ? 'text-brand-blue' : 'text-gray-400 hover:text-brand-blue'}"
                    aria-label="Wishlist">
                    &hearts;
                </button>

                ${media}

                <span class="absolute left-3 bottom-3 rounded-full border border-gray-200 bg-white/90 px-2 py-1 text-[10px] font-bold uppercase tracking-[0.18em] text-brand-blue">
                    ${escapeHtml(categoryLabel)}
                </span>
            </div>

            <div class="flex flex-1 flex-col px-3 py-3">
                <h3 class="text-sm font-semibold leading-5 text-gray-900">${escapeHtml(product.name)}</h3>

                <p class="mt-1 text-sm font-bold text-brand-blue">
                    ${formatUsd(product.price)}
                </p>

                <p class="mt-1 text-xs text-gray-500">${escapeHtml(stockLabel)}</p>

                <button onclick="addToCart(${product.id})"
                    class="mt-auto pt-4 text-xs font-bold uppercase tracking-wide transition ${inCart ? 'text-brand-blue' : 'text-gray-900 hover:text-brand-blue'}">
                    ${inCart ? 'Sudah di Keranjang' : '+ Tambah Keranjang'}
                </button>
            </div>
        </article>`;
    });

    renderPagination(totalItems, totalPages);
    updateBadges();
    renderDrawer();
}

function changePage(page) {
    currentPage = page;
    renderProducts();
}

function renderPagination(totalItems, totalPages) {
    const pagination = document.getElementById('pagination');

    if (!pagination) {
        return;
    }

    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }

    const pageInfoStart = (currentPage - 1) * PAGE_SIZE + 1;
    const pageInfoEnd = Math.min(currentPage * PAGE_SIZE, totalItems);
    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);

    const pageButtons = [];

    pageButtons.push(`
        <button
            onclick="changePage(${Math.max(1, currentPage - 1)})"
            ${currentPage === 1 ? 'disabled' : ''}
            class="rounded-full border px-4 py-2 text-xs font-bold uppercase tracking-wide transition ${
                currentPage === 1
                    ? 'cursor-not-allowed border-gray-200 text-gray-300'
                    : 'border-gray-300 text-gray-700 hover:border-brand-blue hover:text-brand-blue'
            }"
        >
            Prev
        </button>
    `);

    if (startPage > 1) {
        pageButtons.push(pageButton(1));
        if (startPage > 2) {
            pageButtons.push(`<span class="px-1 text-sm text-gray-400">...</span>`);
        }
    }

    for (let page = startPage; page <= endPage; page += 1) {
        pageButtons.push(pageButton(page, page === currentPage));
    }

    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            pageButtons.push(`<span class="px-1 text-sm text-gray-400">...</span>`);
        }
        pageButtons.push(pageButton(totalPages));
    }

    pageButtons.push(`
        <button
            onclick="changePage(${Math.min(totalPages, currentPage + 1)})"
            ${currentPage === totalPages ? 'disabled' : ''}
            class="rounded-full border px-4 py-2 text-xs font-bold uppercase tracking-wide transition ${
                currentPage === totalPages
                    ? 'cursor-not-allowed border-gray-200 text-gray-300'
                    : 'border-gray-300 text-gray-700 hover:border-brand-blue hover:text-brand-blue'
            }"
        >
            Next
        </button>
    `);

    pagination.innerHTML = `
        <div class="w-full text-center text-xs uppercase tracking-[0.2em] text-gray-500">
            Menampilkan ${pageInfoStart}-${pageInfoEnd} dari ${totalItems} produk
        </div>
        <div class="flex flex-wrap items-center justify-center gap-2">
            ${pageButtons.join('')}
        </div>
    `;
}

function pageButton(page, isActive = false) {
    return `
        <button
            onclick="changePage(${page})"
            class="min-w-10 rounded-full border px-4 py-2 text-xs font-bold uppercase tracking-wide transition ${
                isActive
                    ? 'border-brand-blue bg-brand-blue text-white'
                    : 'border-gray-300 text-gray-700 hover:border-brand-blue hover:text-brand-blue'
            }"
        >
            ${page}
        </button>
    `;
}

function addToCart(id) {
    const key = String(id);

    cart[key] = (cart[key] || 0) + 1;

    updateBadges();
    renderProducts();
}

function updateCartQty(id, delta) {
    const key = String(id);

    if (!cart[key]) {
        return;
    }

    cart[key] += delta;

    if (cart[key] <= 0) {
        delete cart[key];
    }

    updateBadges();
    renderProducts();
}

function toggleWishlist(id) {
    const key = String(id);

    if (wishlist[key]) {
        delete wishlist[key];
    } else {
        wishlist[key] = true;
    }

    updateBadges();
    renderProducts();
}

function updateBadges() {
    const cartCount = Object.values(cart).reduce((total, qty) => total + qty, 0);
    const wishlistCount = Object.keys(wishlist).length;

    const cartBadge = document.getElementById('cartCount');
    const wishlistBadge = document.getElementById('wishlistCount');

    if (cartBadge) {
        cartBadge.innerText = cartCount;
    }

    if (wishlistBadge) {
        wishlistBadge.innerText = wishlistCount;
    }
}

function toggleDrawer(type) {
    activeDrawer = type === 'wishlist' ? 'wishlist' : 'cart';

    const overlay = document.getElementById('overlay');
    const drawer = document.getElementById('drawer');
    const drawerTitle = document.getElementById('drawerTitle');

    if (!overlay || !drawer || !drawerTitle) {
        return;
    }

    drawerTitle.textContent = activeDrawer === 'wishlist' ? 'Wishlist' : 'Keranjang';
    renderDrawer();

    overlay.classList.remove('hidden');
    requestAnimationFrame(() => {
        overlay.classList.remove('opacity-0');
        drawer.classList.remove('translate-x-full');
    });
}

function closeDrawer() {
    const overlay = document.getElementById('overlay');
    const drawer = document.getElementById('drawer');

    if (!overlay || !drawer) {
        return;
    }

    overlay.classList.add('opacity-0');
    drawer.classList.add('translate-x-full');

    window.setTimeout(() => {
        overlay.classList.add('hidden');
    }, 300);
}

function renderDrawer() {
    const drawerItems = document.getElementById('drawerItems');
    const drawerFooter = document.getElementById('drawerFooter');
    const cartTotal = document.getElementById('cartTotal');

    if (!drawerItems || !drawerFooter || !cartTotal) {
        return;
    }

    const isWishlist = activeDrawer === 'wishlist';

    const items = isWishlist
        ? Object.keys(wishlist)
            .map((id) => getProductById(id))
            .filter(Boolean)
        : Object.entries(cart)
            .map(([id, qty]) => {
                const product = getProductById(id);
                return product ? { product, qty } : null;
            })
            .filter(Boolean);

    if (items.length === 0) {
        drawerItems.innerHTML = `
            <div class="rounded-2xl border border-dashed border-gray-300 px-4 py-8 text-center text-sm text-gray-500">
                ${isWishlist ? 'Wishlist masih kosong.' : 'Keranjang masih kosong.'}
            </div>
        `;

        drawerFooter.classList.add('hidden');
        cartTotal.textContent = formatUsd(0);
        return;
    }

    drawerItems.innerHTML = items.map((item) => {
        const product = isWishlist ? item : item.product;
        const qty = isWishlist ? 1 : item.qty;
        const stockInfo = product.stock > 0 ? `${product.stock} stok` : 'Stok habis';

        return `
            <div class="flex gap-3 rounded-2xl border border-gray-200 bg-white p-3 shadow-sm">
                <div class="flex h-16 w-16 flex-shrink-0 items-center justify-center rounded-xl bg-gray-100 text-sm font-black text-gray-900">
                    ${escapeHtml(getInitials(product.name))}
                </div>
                <div class="min-w-0 flex-1">
                    <div class="flex items-start justify-between gap-2">
                        <div class="min-w-0">
                            <p class="truncate text-sm font-semibold text-gray-900">${escapeHtml(product.name)}</p>
                            <p class="mt-0.5 text-xs text-gray-500">${escapeHtml(product.category || 'Produk')}</p>
                        </div>
                        <button onclick="toggleWishlist(${product.id})" class="text-xs font-bold uppercase tracking-wide ${wishlist[product.id] ? 'text-brand-blue' : 'text-gray-400'}">
                            ${wishlist[product.id] ? 'Saved' : 'Save'}
                        </button>
                    </div>
                    <div class="mt-2 flex items-center justify-between gap-3">
                        <div>
                            <p class="text-sm font-bold text-brand-blue">${formatUsd(product.price)}</p>
                            <p class="text-xs text-gray-500">${escapeHtml(stockInfo)}</p>
                        </div>
                        ${
                            isWishlist
                                ? `<button onclick="addToCart(${product.id})" class="rounded-full bg-brand-blue px-3 py-1.5 text-xs font-bold uppercase text-white transition hover:bg-brand-blue-hover">Tambah</button>`
                                : `
                                    <div class="flex items-center gap-2">
                                        <button onclick="updateCartQty(${product.id}, -1)" class="flex h-8 w-8 items-center justify-center rounded-full border border-gray-300 text-sm font-bold text-gray-700 transition hover:border-brand-blue hover:text-brand-blue">-</button>
                                        <span class="min-w-6 text-center text-sm font-bold text-gray-900">${qty}</span>
                                        <button onclick="addToCart(${product.id})" class="flex h-8 w-8 items-center justify-center rounded-full border border-gray-300 text-sm font-bold text-gray-700 transition hover:border-brand-blue hover:text-brand-blue">+</button>
                                    </div>
                                `
                        }
                    </div>
                </div>
            </div>
        `;
    }).join('');

    if (isWishlist) {
        drawerFooter.classList.add('hidden');
        cartTotal.textContent = formatUsd(0);
        return;
    }

    const total = items.reduce((sum, item) => sum + (item.product.price * item.qty), 0);
    drawerFooter.classList.remove('hidden');
    cartTotal.textContent = formatUsd(total);
}

function bindSearch() {
    if (!searchForm || !searchInput) {
        return;
    }

    searchForm.addEventListener('submit', (event) => {
        event.preventDefault();
        currentPage = 1;
        fetchProductsFromJava(searchInput.value.trim());
    });
}

function initPage() {
    updatePriceLabel();
    bindSearch();

    if (priceRange) {
        priceRange.addEventListener('input', () => {
            currentPage = 1;
            updatePriceLabel();
            renderProducts();
        });
    }

    updateBadges();
    fetchProductsFromJava('');
}

initPage();
