package mybot.maple;

import it.auties.whatsapp.api.ErrorHandler;
import it.auties.whatsapp.api.HistoryLength;
import it.auties.whatsapp.api.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.message.model.TextPreviewSetting;
import it.auties.whatsapp.model.signal.auth.Version;
import mybot.maple.message.Message;

import java.util.concurrent.ExecutionException;

public class Maple {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var waOpts = Whatsapp.Options.newOptions()
                .autodetectListeners(true)
                .textPreviewSetting(TextPreviewSetting.ENABLED)
                .version(new Version(2, 2244, 6))
                .description("Maple")
                .historyLength(HistoryLength.THREE_MONTHS)
                .errorHandler(ErrorHandler.toFile())
                .qrHandler(QrHandler.toTerminal())
                .build();
        Whatsapp.lastConnection(waOpts)
                .addLoggedInListener(() -> System.out.println("Bot Terkonek"))
                .addNewMessageListener(Message::onNewMessage)
                .connect()
                .get();
    }
}
