package uz;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import uz.pdp.Service.UpdateHandler;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class App {
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("settings");
    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ThreadLocal<UpdateHandler> threadLocal = ThreadLocal.withInitial(UpdateHandler::new);

    public static void main( String[] args ) {

        TelegramBot bot = new TelegramBot(resourceBundle.getString("bot.token"));

        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                CompletableFuture.runAsync(() -> {
                    threadLocal.get().handle(update);
                }, executorService);
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);

    }
}
