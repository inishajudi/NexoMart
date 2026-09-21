package com.nexomart.app.chat;

/**
 * Keyword-based fallback provider. Every answer only mentions pages that exist in NexoMart.
 */
public class MockChatProvider implements ChatProvider {

    static final String FALLBACK =
            "I can help with buying, selling, orders, reviews, categories and logging in on NexoMart. "
            + "Try asking, for example: \"How do I sell a product?\"";

    @Override
    public String name() {
        return "mock";
    }

    @Override
    public String reply(String userMessage) throws ChatProviderException {
        String m = " " + (userMessage == null ? "" : userMessage.toLowerCase())
                .replaceAll("[^a-z0-9& ]", " ").replaceAll("\\s+", " ").trim() + " ";

        if (has(m, "sell", "seller", "listing", "list a product", "add a product", "add product", "create product")) {
            return "To sell on NexoMart, log in with a seller account and click \"List a Product\" "
                    + "in the menu (/products/create). On the Browse page, your own products have "
                    + "Edit and Delete buttons. Orders for your products appear under \"My Sales\" (/orders/seller).";
        }
        if (has(m, "review", "rating", "rate ")) {
            return "To leave a review, log in as a buyer, click Browse, and open a product. "
                    + "The \"Write a review\" form is on the product page. "
                    + "You can only review products from your completed orders, once per product.";
        }
        if (has(m, "order", "checkout", "buy", "purchase", "cart")) {
            return "To buy: click Browse, choose a product and click \"Add to cart\". "
                    + "Then open Cart and go to Checkout. "
                    + "After that, you can see your orders under \"Orders\" (/orders).";
        }
        if (has(m, "categor")) {
            return "Sellers choose a category when they list a product (for example Makeup). "
                    + "On the Browse page, type a category in the category box and click Search to filter.";
        }
        if (has(m, "login", "log in", "sign in", "account", "register", "sign up", "password")) {
            return "Use the Login page (/login) to sign in. New users can create an account on the "
                    + "Register page (/register).";
        }
        if (has(m, "admin", "moderat", "remove product")) {
            return "Admin users have a dashboard at /admin/dashboard, where they can review the marketplace "
                    + "and remove products.";
        }
        if (has(m, "product", "browse", "search", "find", "shop", "items")) {
            return "Click Browse (/products) to see all listings. You can search by name or category. "
                    + "Click a product to see its details.";
        }
        if (has(m, " hi ", " hello ", " hey ", "good morning", "good evening", "help")) {
            return "Hello! I can help with buying, selling, orders and reviews on NexoMart. What would you like to know?";
        }
        return FALLBACK;
    }

    private static boolean has(String text, String... keys) {
        for (String k : keys) {
            if (text.contains(k)) {
                return true;
            }
        }
        return false;
    }
}