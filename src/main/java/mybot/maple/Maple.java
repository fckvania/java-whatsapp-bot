package mybot.maple;
;
import it.auties.whatsapp.api.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.signal.auth.Version;
import mybot.maple.message.Message;

import java.util.concurrent.ExecutionException;

public class Maple {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var waOpts = Whatsapp.Options.newOptions()
                .version(new Version(2, 2244, 6))
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
