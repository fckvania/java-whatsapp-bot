package mybot.maple;

import it.auties.whatsapp.api.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.message.model.TextPreviewSetting;
import it.auties.whatsapp.model.signal.auth.Version;
import mybot.maple.message.Message;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class Maple {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var waOpts = Whatsapp.Options.builder()
                .textPreviewSetting(TextPreviewSetting.DISABLED)
                .defaultSerialization(true)
                .description("Maple")
                .qrHandler(QrHandler.toTerminal())
                .build();
        Whatsapp.lastConnection(waOpts)
                .addLoggedInListener(() -> System.out.println("Connected :>"))
                .addNewMessageListener(Message::onNewMessage)
                .connect()
                .get();
    }
}
