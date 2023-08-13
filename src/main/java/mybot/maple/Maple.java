package mybot.maple;

import it.auties.whatsapp.api.QrHandler;
import it.auties.whatsapp.api.Whatsapp;
import mybot.maple.message.Message;

import java.util.concurrent.ExecutionException;

public class Maple {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Whatsapp.webBuilder()
                .lastConnection()
                .unregistered(QrHandler.toTerminal())
                .addLoggedInListener(() -> System.out.println("Connected :>"))
                .addNewMessageListener(Message::onNewMessage)
                .connect()
                .get();
    }
}
