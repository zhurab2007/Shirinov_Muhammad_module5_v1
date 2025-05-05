package uz.pdp.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import uz.pdp.Entity.UserStates;
import uz.pdp.Entity.Book;
import uz.pdp.Entity.Order;

import java.util.*;

public class UpdateHandler {

    private static final TelegramBot bot = new TelegramBot("7347216925:AAE43B73K5D1GmqfuC3BE8kljiq3Rd7VidQ");
    private static final Long ADMIN_ID = 6624967040L;

    private static final Map<Long, UserStates> userStates = new HashMap<>();
    private static final Map<Long, Book> tempBooks = new HashMap<>();

    private static final List<Book> bookDatabase = new ArrayList<>();
    private static final List<Order> orderDatabase = new ArrayList<>();

    public static void handle(Update update) {
        Long chatId = getChatId(update);
        if (chatId == null) return;

        userStates.putIfAbsent(chatId, UserStates.START);
        UserStates state = userStates.get(chatId);

        if (!chatId.equals(ADMIN_ID)) {
            sendMessage(chatId, "⛔ Sizda admin ruxsati yo‘q.");
            return;
        }

        if (update.message() != null) {
            handleMessage(update.message(), chatId, state);
        } else if (update.callbackQuery() != null) {
            handleCallback(update.callbackQuery(), chatId);
        }
    }

    private static Long getChatId(Update update) {
        if (update.message() != null && update.message().from() != null)
            return update.message().from().id();
        else if (update.callbackQuery() != null && update.callbackQuery().from() != null)
            return update.callbackQuery().from().id();
        return null;
    }

    private static void handleMessage(Message message, Long chatId, UserStates state) {
        String text = message.text();

        if ("/start".equals(text)) {
            userStates.put(chatId, UserStates.START);
            showAdminMenu(chatId);
            return;
        }

        switch (state) {
            case AWAITING_BOOK_NAME -> {
                Book book = new Book();
                book.setName(text);
                tempBooks.put(chatId, book);
                userStates.put(chatId, UserStates.AWAITING_BOOK_PRICE);
                askPrice(chatId);
            }
            case AWAITING_BOOK_PRICE -> {
                try {
                    int price = Integer.parseInt(text);
                    tempBooks.get(chatId).setPrice(price);
                    userStates.put(chatId, UserStates.AWAITING_BOOK_QUANTITY);
                    askQuantity(chatId);
                } catch (NumberFormatException e) {
                    sendMessage(chatId, "❗ Narx raqamda bo‘lishi kerak!");
                }
            }
            case AWAITING_BOOK_QUANTITY -> {
                try {
                    int qty = Integer.parseInt(text);
                    tempBooks.get(chatId).setQuantity(qty);
                    userStates.put(chatId, UserStates.AWAITING_BOOK_IMAGE);
                    askImage(chatId);
                } catch (NumberFormatException e) {
                    sendMessage(chatId, "❗ Son raqamda bo‘lishi kerak!");
                }
            }
            case AWAITING_BOOK_IMAGE -> {
                if (message.photo() != null && message.photo().length > 0) {
                    String fileId = message.photo()[message.photo().length - 1].fileId();
                    tempBooks.get(chatId).setImageFileId(fileId);
                    userStates.put(chatId, UserStates.AWAITING_BOOK_FILE);
                    askFile(chatId);
                } else {
                    sendMessage(chatId, "❗ Rasm yuboring!");
                }
            }
            case AWAITING_BOOK_FILE -> {
                if (message.document() != null) {
                    String fileId = message.document().fileId();
                    Book book = tempBooks.get(chatId);
                    book.setDocumentFileId(fileId);
                    bookDatabase.add(book);
                    tempBooks.remove(chatId);
                    userStates.put(chatId, UserStates.START);
                    sendMessage(chatId, "✅ Kitob muvaffaqiyatli saqlandi!");
                    showAdminMenu(chatId);
                } else {
                    sendMessage(chatId, "❗ Faqat .pdf yoki boshqa hujjat yuboring!");
                }
            }
            default -> sendMessage(chatId, "Iltimos, tugmalardan foydalaning yoki Cancel bosing.");
        }
    }

    private static void handleCallback(CallbackQuery callback, Long chatId) {
        String data = callback.data();
        switch (data) {
            case "add_book" -> {
                userStates.put(chatId, UserStates.AWAITING_BOOK_NAME);
                sendMessageWithCancel(chatId, "📚 Kitob nomini kiriting:");
            }
            case "cancel" -> {
                userStates.put(chatId, UserStates.START);
                tempBooks.remove(chatId);
                sendMessage(chatId, "❌ Amal bekor qilindi.");
                showAdminMenu(chatId);
            }
            case "show_books" -> {
                if (bookDatabase.isEmpty()) {
                    sendMessage(chatId, "📚 Hozircha kitoblar mavjud emas.");
                    return;
                }

                StringBuilder text = new StringBuilder("📚 Kitoblar ro‘yxati:\n");
                for (int i = 0; i < bookDatabase.size(); i++) {
                    Book b = bookDatabase.get(i);
                    text.append(i + 1).append(". ")
                            .append(b.getName())
                            .append(" - ").append(b.getPrice()).append(" so‘m, ")
                            .append("soni: ").append(b.getQuantity()).append("\n");
                }
                sendMessage(chatId, text.toString());
            }
            case "show_orders" -> {
                if (orderDatabase.isEmpty()) {
                    sendMessage(chatId, "📦 Buyurtmalar mavjud emas.");
                    return;
                }
                StringBuilder text = new StringBuilder("📦 Buyurtmalar ro‘yxati:\n");
                for (int i = 0; i < orderDatabase.size(); i++) {
                    Order o = orderDatabase.get(i);
                    text.append(i + 1).append(". Buyurtma ID: ").append(o.getId()).append("\n");
                }
                sendMessage(chatId, text.toString());
            }
            default -> sendMessage(chatId, "⚠️ Noma’lum buyruq!");
        }
    }

    private static void sendMessage(Long chatId, String text) {
        bot.execute(new SendMessage(chatId, text));
    }

    private static void sendMessageWithCancel(Long chatId, String text) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                new InlineKeyboardButton("❌ Cancel").callbackData("cancel")
        );
        bot.execute(new SendMessage(chatId, text).replyMarkup(markup));
    }

    private static void showAdminMenu(Long chatId) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("➕ Add Book").callbackData("add_book"),
                new InlineKeyboardButton("📚 Show Books").callbackData("show_books"),
                new InlineKeyboardButton("📦 Show Orders").callbackData("show_orders")
        );
        sendMessage(chatId, "📋 Admin paneliga xush kelibsiz!");
        bot.execute(new SendMessage(chatId, "Tanlang:").replyMarkup(keyboard));
    }

    private static void askPrice(Long chatId) {
        sendMessageWithCancel(chatId, "💰 Kitob narxini kiriting:");
    }

    private static void askQuantity(Long chatId) {
        sendMessageWithCancel(chatId, "📦 Kitob sonini kiriting:");
    }

    private static void askImage(Long chatId) {
        sendMessageWithCancel(chatId, "🖼 Kitob rasmi (cover) ni yuboring:");
    }

    private static void askFile(Long chatId) {
        sendMessageWithCancel(chatId, "📄 Kitob faylini yuboring (PDF, TXT va boshqalar):");
    }
}

