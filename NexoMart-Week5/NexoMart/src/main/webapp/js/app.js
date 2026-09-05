/**
 * Vanilla JS + fetch() AJAX client for NexoMart's browse -> cart -> checkout flow.
 * Talks to /api/v1/products, /api/v1/cart, /api/v1/orders per the API contract in
 * Section 13 (fixed envelope: { success, data, error }).
 */
const NexoMart = (() => {

    async function apiFetch(url, options = {}) {
        const resp = await fetch(url, {
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin',
            ...options,
        });
        const body = await resp.json();
        if (!body.success) {
            throw new Error(body.error ? body.error.message : 'Request failed');
        }
        return body.data;
    }

    function escapeHtml(str) {
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    // ---------- Browse page ----------

    async function loadProducts() {
        const q = document.getElementById('searchInput').value.trim();
        const category = document.getElementById('categorySelect').value;
        const minPrice = document.getElementById('minPriceInput').value;
        const maxPrice = document.getElementById('maxPriceInput').value;
        const sort = document.getElementById('sortSelect').value;
        const params = new URLSearchParams();
        if (q) params.set('q', q);
        if (category) params.set('category', category);
        if (minPrice) params.set('minPrice', minPrice);
        if (maxPrice) params.set('maxPrice', maxPrice);
        if (sort) params.set('sort', sort);

        const grid = document.getElementById('productGrid');
        grid.innerHTML = '<p>Loading...</p>';

        try {
            const products = await apiFetch(`/api/v1/products?${params.toString()}`);
            if (products.length === 0) {
                grid.innerHTML = '<p>No products match your search.</p>';
                return;
            }
            grid.innerHTML = products.map(p => `
                <div class="product-card">
                    <h3>${escapeHtml(p.name)}</h3>
                    <p>${escapeHtml(p.category || '')}</p>
                    <p class="price">₹${Number(p.price).toFixed(2)}</p>
                    <p>${p.stockQty} in stock</p>
                    <button data-product-id="${p.id}" class="addToCartBtn">Add to cart</button>
                </div>
            `).join('');

            grid.querySelectorAll('.addToCartBtn').forEach(btn => {
                btn.addEventListener('click', () => addToCart(Number(btn.dataset.productId)));
            });
        } catch (err) {
            grid.innerHTML = `<p>Could not load products: ${escapeHtml(err.message)}</p>`;
        }
    }

    async function addToCart(productId) {
        try {
            await apiFetch('/api/v1/cart', {
                method: 'POST',
                body: JSON.stringify({ productId, quantity: 1 }),
            });
            alert('Added to cart.');
        } catch (err) {
            alert('Could not add to cart: ' + err.message);
        }
    }

    function initBrowsePage() {
        document.getElementById('searchBtn').addEventListener('click', loadProducts);
        document.getElementById('searchInput').addEventListener('keydown', e => {
            if (e.key === 'Enter') loadProducts();
        });
        document.getElementById('sortSelect').addEventListener('change', loadProducts);
        loadProducts();
    }

    // ---------- Cart page ----------

    async function loadCart() {
        const body = document.getElementById('cartBody');
        try {
            const { items, total } = await apiFetch('/api/v1/cart');
            document.getElementById('cartTotal').textContent = Number(total).toFixed(2);

            if (items.length === 0) {
                body.innerHTML = '<tr><td colspan="5">Your cart is empty.</td></tr>';
                return;
            }
            body.innerHTML = items.map(i => `
                <tr>
                    <td>${escapeHtml(i.productName)}</td>
                    <td>₹${Number(i.unitPrice).toFixed(2)}</td>
                    <td>
                        <input type="number" min="1" value="${i.quantity}"
                               data-cart-item-id="${i.cartItemId}" class="qtyInput" style="width:60px">
                    </td>
                    <td>₹${Number(i.lineTotal).toFixed(2)}</td>
                    <td><button data-cart-item-id="${i.cartItemId}" class="removeBtn">Remove</button></td>
                </tr>
            `).join('');

            body.querySelectorAll('.qtyInput').forEach(input => {
                input.addEventListener('change', () =>
                    updateQuantity(Number(input.dataset.cartItemId), Number(input.value)));
            });
            body.querySelectorAll('.removeBtn').forEach(btn => {
                btn.addEventListener('click', () => removeItem(Number(btn.dataset.cartItemId)));
            });
        } catch (err) {
            body.innerHTML = `<tr><td colspan="5">Could not load cart: ${escapeHtml(err.message)}</td></tr>`;
        }
    }

    async function updateQuantity(cartItemId, quantity) {
        try {
            await apiFetch(`/api/v1/cart/${cartItemId}`, {
                method: 'PUT',
                body: JSON.stringify({ quantity }),
            });
            loadCart();
        } catch (err) {
            alert('Could not update quantity: ' + err.message);
        }
    }

    async function removeItem(cartItemId) {
        try {
            await apiFetch(`/api/v1/cart/${cartItemId}`, { method: 'DELETE' });
            loadCart();
        } catch (err) {
            alert('Could not remove item: ' + err.message);
        }
    }

    async function checkout() {
        const resultEl = document.getElementById('checkoutResult');
        resultEl.textContent = 'Processing mock payment...';
        try {
            const order = await apiFetch('/api/v1/orders', {
                method: 'POST',
                body: JSON.stringify({ paymentConfirmed: true }),
            });
            resultEl.textContent = `Order #${order.id} placed. Total ₹${Number(order.totalAmount).toFixed(2)}. Status: ${order.status}.`;
            loadCart();
        } catch (err) {
            resultEl.textContent = 'Checkout failed: ' + err.message;
        }
    }

    function initCartPage() {
        document.getElementById('checkoutBtn').addEventListener('click', checkout);
        loadCart();
    }

    // ---------- Seller dashboard (Week 3: listing management, F2) ----------

    function readListingForm() {
        return {
            name: document.getElementById('nameInput').value.trim(),
            description: document.getElementById('descriptionInput').value.trim(),
            price: Number(document.getElementById('priceInput').value),
            stockQty: Number(document.getElementById('stockInput').value),
            category: document.getElementById('categoryInput').value.trim(),
            imageUrl: document.getElementById('imageUrlInput').value.trim(),
        };
    }

    function fillListingForm(product) {
        document.getElementById('productId').value = product.id;
        document.getElementById('nameInput').value = product.name;
        document.getElementById('descriptionInput').value = product.description || '';
        document.getElementById('priceInput').value = product.price;
        document.getElementById('stockInput').value = product.stockQty;
        document.getElementById('categoryInput').value = product.category || '';
        document.getElementById('imageUrlInput').value = product.imageUrl || '';
        document.getElementById('saveBtn').textContent = 'Save changes';
        document.getElementById('cancelEditBtn').style.display = 'inline-block';
    }

    function resetListingForm() {
        document.getElementById('listingForm').reset();
        document.getElementById('productId').value = '';
        document.getElementById('saveBtn').textContent = 'Add listing';
        document.getElementById('cancelEditBtn').style.display = 'none';
    }

    async function loadMyListings() {
        const body = document.getElementById('listingsBody');
        try {
            const products = await apiFetch('/api/v1/products/mine');
            if (products.length === 0) {
                body.innerHTML = '<tr><td colspan="5">You have no listings yet.</td></tr>';
                return;
            }
            body.innerHTML = products.map(p => `
                <tr>
                    <td>${escapeHtml(p.name)}</td>
                    <td>${escapeHtml(p.category || '')}</td>
                    <td>₹${Number(p.price).toFixed(2)}</td>
                    <td>${p.stockQty}</td>
                    <td>
                        <button class="editBtn" data-id="${p.id}">Edit</button>
                        <button class="deleteBtn" data-id="${p.id}">Delete</button>
                    </td>
                </tr>
            `).join('');

            const productsById = Object.fromEntries(products.map(p => [p.id, p]));
            body.querySelectorAll('.editBtn').forEach(btn => {
                btn.addEventListener('click', () => fillListingForm(productsById[btn.dataset.id]));
            });
            body.querySelectorAll('.deleteBtn').forEach(btn => {
                btn.addEventListener('click', () => deleteListing(Number(btn.dataset.id)));
            });
        } catch (err) {
            body.innerHTML = `<tr><td colspan="5">Could not load listings: ${escapeHtml(err.message)}</td></tr>`;
        }
    }

    async function saveListing(event) {
        event.preventDefault();
        const errorEl = document.getElementById('formError');
        errorEl.textContent = '';

        const productId = document.getElementById('productId').value;
        const payload = readListingForm();

        try {
            if (productId) {
                await apiFetch(`/api/v1/products/${productId}`, {
                    method: 'PUT',
                    body: JSON.stringify(payload),
                });
            } else {
                await apiFetch('/api/v1/products', {
                    method: 'POST',
                    body: JSON.stringify(payload),
                });
            }
            resetListingForm();
            loadMyListings();
        } catch (err) {
            errorEl.textContent = err.message;
        }
    }

    async function deleteListing(productId) {
        if (!confirm('Delete this listing? This cannot be undone.')) return;
        try {
            await apiFetch(`/api/v1/products/${productId}`, { method: 'DELETE' });
            loadMyListings();
        } catch (err) {
            alert('Could not delete listing: ' + err.message);
        }
    }

    // ---------- Seller: incoming orders (Week 4 dashboard; Week 5 adds status actions) ----------

    const NEXT_STATUS = {
        PENDING: 'CONFIRMED',
        CONFIRMED: 'SHIPPED',
        SHIPPED: 'DELIVERED',
    };
    const NEXT_STATUS_LABEL = {
        PENDING: 'Confirm',
        CONFIRMED: 'Mark shipped',
        SHIPPED: 'Mark delivered',
    };

    async function loadIncomingOrders() {
        const body = document.getElementById('incomingOrdersBody');
        try {
            const orders = await apiFetch('/api/v1/orders?as=seller');
            if (orders.length === 0) {
                body.innerHTML = '<tr><td colspan="5">No orders yet.</td></tr>';
                return;
            }
            body.innerHTML = orders.map(o => {
                const next = NEXT_STATUS[o.status];
                const actionCell = next
                    ? `<button class="advanceBtn" data-id="${o.id}" data-next="${next}">${NEXT_STATUS_LABEL[o.status]}</button>`
                    : '—';
                return `
                <tr>
                    <td>#${o.id}</td>
                    <td>${escapeHtml(o.status)}</td>
                    <td>${o.items.length} line item(s)</td>
                    <td>${o.createdAt ? new Date(o.createdAt).toLocaleString() : ''}</td>
                    <td>${actionCell}</td>
                </tr>`;
            }).join('');

            body.querySelectorAll('.advanceBtn').forEach(btn => {
                btn.addEventListener('click', () =>
                    advanceOrderStatus(Number(btn.dataset.id), btn.dataset.next, loadIncomingOrders));
            });
        } catch (err) {
            body.innerHTML = `<tr><td colspan="5">Could not load orders: ${escapeHtml(err.message)}</td></tr>`;
        }
    }

    async function advanceOrderStatus(orderId, newStatus, onDone) {
        try {
            await apiFetch(`/api/v1/orders/${orderId}/status`, {
                method: 'PUT',
                body: JSON.stringify({ status: newStatus }),
            });
            if (onDone) onDone();
        } catch (err) {
            alert('Could not update order status: ' + err.message);
        }
    }

    function initSellerDashboard() {
        document.getElementById('listingForm').addEventListener('submit', saveListing);
        document.getElementById('cancelEditBtn').addEventListener('click', resetListingForm);
        loadMyListings();
        loadIncomingOrders();
    }

    // ---------- Buyer: My Orders (Week 5 — view status, cancel while cancellable) ----------

    async function loadMyOrders() {
        const body = document.getElementById('myOrdersBody');
        try {
            const orders = await apiFetch('/api/v1/orders');
            if (orders.length === 0) {
                body.innerHTML = '<tr><td colspan="5">No orders placed yet.</td></tr>';
                return;
            }
            body.innerHTML = orders.map(o => {
                const cancellable = o.status === 'PENDING' || o.status === 'CONFIRMED';
                const actionCell = cancellable
                    ? `<button class="cancelBtn" data-id="${o.id}">Cancel order</button>`
                    : '—';
                return `
                <tr>
                    <td>#${o.id}</td>
                    <td>${escapeHtml(o.status)}</td>
                    <td>₹${Number(o.totalAmount).toFixed(2)}</td>
                    <td>${o.createdAt ? new Date(o.createdAt).toLocaleString() : ''}</td>
                    <td>${actionCell}</td>
                </tr>`;
            }).join('');

            body.querySelectorAll('.cancelBtn').forEach(btn => {
                btn.addEventListener('click', () => {
                    if (confirm('Cancel this order?')) {
                        advanceOrderStatus(Number(btn.dataset.id), 'CANCELLED', loadMyOrders);
                    }
                });
            });
        } catch (err) {
            body.innerHTML = `<tr><td colspan="5">Could not load orders: ${escapeHtml(err.message)}</td></tr>`;
        }
    }

    function initMyOrdersPage() {
        loadMyOrders();
    }

    // ---------- Admin panel (Week 4: F7 — view users/orders, moderate listings) ----------

    async function loadUsers() {
        const body = document.getElementById('usersBody');
        try {
            const users = await apiFetch('/api/v1/admin/users');
            body.innerHTML = users.map(u => `
                <tr>
                    <td>${u.id}</td>
                    <td>${escapeHtml(u.name)}</td>
                    <td>${escapeHtml(u.email)}</td>
                    <td>${escapeHtml(u.role)}</td>
                    <td>${u.createdAt ? new Date(u.createdAt).toLocaleDateString() : ''}</td>
                </tr>
            `).join('') || '<tr><td colspan="5">No users.</td></tr>';
        } catch (err) {
            body.innerHTML = `<tr><td colspan="5">${escapeHtml(err.message)}</td></tr>`;
        }
    }

    async function loadAllOrders() {
        const body = document.getElementById('allOrdersBody');
        try {
            const orders = await apiFetch('/api/v1/admin/orders');
            body.innerHTML = orders.map(o => `
                <tr>
                    <td>#${o.id}</td>
                    <td>${escapeHtml(o.status)}</td>
                    <td>₹${Number(o.totalAmount).toFixed(2)}</td>
                    <td>${o.createdAt ? new Date(o.createdAt).toLocaleString() : ''}</td>
                </tr>
            `).join('') || '<tr><td colspan="4">No orders.</td></tr>';
        } catch (err) {
            body.innerHTML = `<tr><td colspan="4">${escapeHtml(err.message)}</td></tr>`;
        }
    }

    async function loadModerationList() {
        const q = document.getElementById('moderateSearchInput').value.trim();
        const params = new URLSearchParams();
        if (q) params.set('q', q);

        const body = document.getElementById('moderateBody');
        try {
            const products = await apiFetch(`/api/v1/products?${params.toString()}`);
            body.innerHTML = products.map(p => `
                <tr>
                    <td>${escapeHtml(p.name)}</td>
                    <td>${escapeHtml(p.category || '')}</td>
                    <td>₹${Number(p.price).toFixed(2)}</td>
                    <td>${p.stockQty}</td>
                    <td><button class="deleteBtn" data-id="${p.id}">Remove listing</button></td>
                </tr>
            `).join('') || '<tr><td colspan="5">No listings.</td></tr>';

            body.querySelectorAll('.deleteBtn').forEach(btn => {
                btn.addEventListener('click', () => moderateRemove(Number(btn.dataset.id)));
            });
        } catch (err) {
            body.innerHTML = `<tr><td colspan="5">${escapeHtml(err.message)}</td></tr>`;
        }
    }

    async function moderateRemove(productId) {
        if (!confirm('Remove this listing from the marketplace?')) return;
        try {
            await apiFetch(`/api/v1/admin/products/${productId}`, { method: 'DELETE' });
            loadModerationList();
        } catch (err) {
            alert('Could not remove listing: ' + err.message);
        }
    }

    function initAdminDashboard() {
        document.getElementById('moderateSearchBtn').addEventListener('click', loadModerationList);
        loadUsers();
        loadAllOrders();
        loadModerationList();
    }

    return { initBrowsePage, initCartPage, initSellerDashboard, initAdminDashboard, initMyOrdersPage };
})();
