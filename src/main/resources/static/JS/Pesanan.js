document.addEventListener('DOMContentLoaded', () => {
    // 1. Render Admin Header info
    if (typeof renderAdminHeader === 'function') {
        renderAdminHeader();
    }
    
    // Set current date
    const dateLabel = document.getElementById('currentDateLabel');
    if (dateLabel) {
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        dateLabel.textContent = new Date().toLocaleDateString('id-ID', options);
    }

    // 2. Fetch Orders
    fetchOrders();
});

let globalOrders = [];
let currentSelectedOrderId = null;

async function fetchOrders() {
    const tableBody = document.getElementById('ordersTableBody');
    if (!tableBody) return;
    
    tableBody.innerHTML = `
        <tr>
            <td colspan="6" class="px-6 py-8 text-center text-sm text-slate-500">
                <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-indigo-500 inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
                Sedang memuat data pesanan...
            </td>
        </tr>
    `;

    try {
        const response = await fetch('/api/dashboard/orders', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeaders()
            }
        });

        if (!response.ok) {
            throw new Error(`Gagal mengambil data: ${response.status}`);
        }

        const data = await response.json();
        globalOrders = data;
        renderOrdersTable(data);
    } catch (error) {
        console.error('Error fetching orders:', error);
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="px-6 py-8 text-center text-sm text-red-500">
                    Gagal memuat data pesanan. Silakan coba lagi.
                </td>
            </tr>
        `;
        showToast('Gagal memuat data pesanan', false);
    }
}

function getStatusBadgeClass(status) {
    const s = String(status || '').toLowerCase();
    if (s.includes('selesai')) return 'bg-emerald-100 text-emerald-700 border-emerald-200';
    if (s.includes('dikirim')) return 'bg-blue-100 text-blue-700 border-blue-200';
    if (s.includes('diproses')) return 'bg-amber-100 text-amber-700 border-amber-200';
    if (s.includes('batal')) return 'bg-red-100 text-red-700 border-red-200';
    return 'bg-slate-100 text-slate-700 border-slate-200'; // Menunggu Konfirmasi
}

function formatRupiah(amount) {
    return new Intl.NumberFormat('id-ID', {
        style: 'currency',
        currency: 'IDR',
        minimumFractionDigits: 0
    }).format(amount || 0);
}

function renderOrdersTable(orders) {
    const tableBody = document.getElementById('ordersTableBody');
    if (!tableBody) return;

    if (!orders || orders.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="px-6 py-8 text-center text-sm text-slate-500">
                    Belum ada pesanan masuk.
                </td>
            </tr>
        `;
        return;
    }

    let html = '';
    orders.forEach(order => {
        const badgeClass = getStatusBadgeClass(order.status);
        
        let orderDate = order.orderDate;
        try {
            orderDate = new Date(order.orderDate).toLocaleDateString('id-ID', {
                year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
            });
        } catch (e) {
            // Biarkan aslinya kalau gagal parse
        }

        html += `
            <tr class="hover:bg-slate-50 transition">
                <td class="px-6 py-4 text-sm font-medium text-slate-800 border-b border-slate-100">#${order.orderId}</td>
                <td class="px-6 py-4 text-sm text-slate-600 border-b border-slate-100 font-medium">${order.customerName || 'Pelanggan'}</td>
                <td class="px-6 py-4 text-sm text-slate-500 border-b border-slate-100">${orderDate}</td>
                <td class="px-6 py-4 text-sm font-semibold text-slate-800 border-b border-slate-100">${formatRupiah(order.totalPrice)}</td>
                <td class="px-6 py-4 border-b border-slate-100">
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ${badgeClass}">
                        ${order.status || 'Status tidak diketahui'}
                    </span>
                </td>
                <td class="px-6 py-4 text-right border-b border-slate-100">
                    <button onclick="openDetailModal(${order.orderId})" class="text-indigo-600 hover:text-indigo-900 text-sm font-medium hover:underline focus:outline-none">Detail</button>
                </td>
            </tr>
        `;
    });

    tableBody.innerHTML = html;
}

function openDetailModal(orderId) {
    const order = globalOrders.find(o => o.orderId === orderId);
    if (!order) return;

    currentSelectedOrderId = orderId;

    document.getElementById('modalTitle').textContent = `Detail Pesanan #${order.orderId}`;
    
    let orderDate = order.orderDate;
    try {
        orderDate = new Date(order.orderDate).toLocaleDateString('id-ID', {
            year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
        });
    } catch(e) {}

    document.getElementById('modalSubtitle').textContent = `Pemesan: ${order.customerName || 'Pelanggan'} • Tanggal: ${orderDate}`;
    
    const itemsContainer = document.getElementById('modalItemsContainer');
    let itemsHtml = '';
    
    if (order.items && order.items.length > 0) {
        order.items.forEach(item => {
            itemsHtml += `
                <div class="flex items-center justify-between p-3 bg-white border border-slate-100 rounded-xl shadow-sm">
                    <div class="flex items-center gap-3">
                        <div class="w-10 h-10 bg-indigo-50 rounded-lg flex items-center justify-center flex-shrink-0 text-indigo-500">
                            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/></svg>
                        </div>
                        <div>
                            <p class="text-sm font-semibold text-slate-800">${item.productName || 'Produk'}</p>
                            <p class="text-xs text-slate-500">${item.quantity} x ${formatRupiah(item.totalPrice / (item.quantity || 1))}</p>
                        </div>
                    </div>
                    <div class="text-right">
                        <p class="text-sm font-bold text-slate-800">${formatRupiah(item.totalPrice)}</p>
                    </div>
                </div>
            `;
        });
    } else {
        itemsHtml = '<p class="text-sm text-slate-500 italic px-2">Tidak ada detail item pada pesanan ini.</p>';
    }
    
    itemsContainer.innerHTML = itemsHtml;
    document.getElementById('modalTotalPrice').textContent = formatRupiah(order.totalPrice);
    
    const statusSelect = document.getElementById('statusSelect');
    if (statusSelect) {
        let matched = false;
        for(let i=0; i<statusSelect.options.length; i++) {
            if(statusSelect.options[i].value.toLowerCase() === (order.status || '').toLowerCase()) {
                statusSelect.selectedIndex = i;
                matched = true;
                break;
            }
        }
        if(!matched && order.status) {
            const opt = document.createElement('option');
            opt.value = order.status;
            opt.text = order.status;
            statusSelect.add(opt, statusSelect.options[0]);
            statusSelect.selectedIndex = 0;
        }
    }

    const modal = document.getElementById('orderDetailModal');
    modal.classList.remove('hidden');
}

function closeDetailModal() {
    const modal = document.getElementById('orderDetailModal');
    if(modal) {
        modal.classList.add('hidden');
    }
    currentSelectedOrderId = null;
}

async function saveOrderStatus() {
    if (!currentSelectedOrderId) return;
    
    const statusSelect = document.getElementById('statusSelect');
    const newStatus = statusSelect.value;
    
    const btn = event.currentTarget;
    const originalText = btn.innerHTML;
    btn.innerHTML = `<svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white inline" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg> Menyimpan...`;
    btn.disabled = true;

    try {
        const response = await fetch(`/api/dashboard/orders/${currentSelectedOrderId}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeaders()
            },
            body: JSON.stringify({ status: newStatus })
        });

        if (!response.ok) {
            throw new Error(`Gagal memperbarui: ${response.status}`);
        }

        showToast('Status pesanan berhasil diperbarui!', true);
        closeDetailModal();
        fetchOrders(); // Refresh table
    } catch (error) {
        console.error('Error updating status:', error);
        showToast('Gagal memperbarui status.', false);
    } finally {
        btn.innerHTML = originalText;
        btn.disabled = false;
    }
}

function showToast(message, isSuccess = true) {
    const toast = document.getElementById('toast');
    const toastIcon = document.getElementById('toastIcon');
    const toastMessage = document.getElementById('toastMessage');

    if (!toast || !toastIcon || !toastMessage) return;

    toastMessage.textContent = message;

    if (isSuccess) {
        toast.classList.remove('border-red-200', 'bg-red-50');
        toast.classList.add('border-emerald-200', 'bg-emerald-50');
        toastMessage.classList.remove('text-red-800');
        toastMessage.classList.add('text-emerald-800');
        
        toastIcon.className = 'w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 bg-emerald-100 text-emerald-600';
        toastIcon.innerHTML = `<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>`;
    } else {
        toast.classList.remove('border-emerald-200', 'bg-emerald-50');
        toast.classList.add('border-red-200', 'bg-red-50');
        toastMessage.classList.remove('text-emerald-800');
        toastMessage.classList.add('text-red-800');
        
        toastIcon.className = 'w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 bg-red-100 text-red-600';
        toastIcon.innerHTML = `<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>`;
    }

    toast.classList.remove('translate-y-20', 'opacity-0', 'pointer-events-none');
    
    setTimeout(() => {
        toast.classList.add('translate-y-20', 'opacity-0', 'pointer-events-none');
    }, 3000);
}
