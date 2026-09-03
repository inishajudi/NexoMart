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
        const params = new URLSearchParams();
        if (q) params.set('q', q);
        if (category) params.set('category', category);

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

    return { initBrowsePage, initCartPage };
})();
