package com.nexomart.app.chat;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MockChatProviderTest {

    private final MockChatProvider mock = new MockChatProvider();

    @Test
    void nameIsMock() {
        assertEquals("mock", mock.name());
    }

    @Test
    void sellQuestionMentionsListAProduct() throws Exception {
        String r = mock.reply("How do I sell a product?");
        assertTrue(r.contains("List a Product"));
        assertTrue(r.contains("My Sales"));
    }

    @Test
    void orderQuestionMentionsOrdersPage() throws Exception {
        assertTrue(mock.reply("How do I place an order?").contains("/orders"));
    }

    @Test
    void categoryQuestionMentionsBrowse() throws Exception {
        assertTrue(mock.reply("What categories are there?").contains("Browse"));
    }

    @Test
    void reviewQuestionMentionsCompletedOrdersRule() throws Exception {
        String r = mock.reply("How do I leave a review?");
        assertTrue(r.contains("completed orders"));
    }

    @Test
    void loginQuestionMentionsLoginPage() throws Exception {
        assertTrue(mock.reply("How do I log in?").contains("/login"));
    }

    @Test
    void unknownQuestionGivesFallback() throws Exception {
        assertEquals(MockChatProvider.FALLBACK, mock.reply("zzzz qqqq"));
    }

    @Test
    void nullMessageGivesFallback() throws Exception {
        assertEquals(MockChatProvider.FALLBACK, mock.reply(null));
    }
}