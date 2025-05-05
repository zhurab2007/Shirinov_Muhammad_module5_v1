package uz.pdp.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import uz.pdp.Entity.UserStates;

import java.util.HashMap;
import java.util.Map;

import static uz.pdp.Entity.UserStates.START;

public class UpdateHandler {
    private static final Map<Long, UserStates> userStates = new HashMap<>();
    private static final TelegramBot bot = new TelegramBot("bot.token");


    private static final Long ADMIN_ID = 123456789L;

    public static void handle(Update update) {
        Long chatId = null;
        Message message = update.message();
        CallbackQuery callbackQuery = update.callbackQuery();

        if (message != null && message.from() != null) {
            chatId = message.from().id();
        } else if (callbackQuery != null && callbackQuery.from() != null) {
            chatId = callbackQuery.from().id();
        }

        if (chatId == null) return;

        userStates.putIfAbsent(chatId, START);
        UserStates state = userStates.get(chatId);

        try {
            if (message != null) {
                handleMessage(update, bot, chatId, message, state);
            } else if (callbackQuery != null) {
                handleCallbackQuery(bot, chatId, callbackQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
            bot.execute(new SendMessage(chatId, "❗ Botda xatolik yuz berdi: " + e.getMessage()));
        }
    }

    private static void handleCallbackQuery(TelegramBot bot, Long chatId, CallbackQuery callbackQuery) {
    }

    private static void handleMessage(Update update, TelegramBot bot, Long chatId, Message message, UserStates state) {
        if (chatId.equals(ADMIN_ID)) {
            if (message.text().equals("/addbook")) {
                userStates.put(chatId, UserStates.ADDING_BOOK);
                bot.execute(new SendMessage(chatId, "Iltimos, kitob nomini kiriting."));
            } else if (state == UserStates.ADDING_BOOK) {
                if (message.text() != null && !message.text().isEmpty()) {
                    String bookTitle = message.text();
                    userStates.put(chatId, UserStates.AWAITING_AUTHOR);
                    bot.execute(new SendMessage(chatId, "Kitob muallifini kiriting."));
                }
            } else if (state == UserStates.AWAITING_AUTHOR) {
                if (message.text() != null && !message.text().isEmpty()) {
                    String authorName = message.text();
                    String bookDetails = "Kitob nomi: " + message.text() + "\nMuallif: " + authorName;
                    bot.execute(new SendMessage(chatId, "Kitob qo'shildi: \n" + bookDetails));

                    userStates.put(chatId, START);
                }
            }
        } else {
            bot.execute(new SendMessage(chatId, "Sizda admin ruxsati yo'q."));
        }
    }
}
