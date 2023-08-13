package mybot.maple.message;

import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.controller.Store;
import it.auties.whatsapp.model.contact.ContactJid;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.message.button.ButtonsResponseMessage;
import it.auties.whatsapp.model.message.standard.ImageMessage;
import it.auties.whatsapp.model.message.standard.TextMessage;;
import mybot.maple.lib.Simple;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Message {
    public static void onNewMessage(Whatsapp api, MessageInfo msg) {
        try {
            Simple simple = new Simple(api, msg);
            String body;

            if ((msg.message().content() instanceof TextMessage textMessage)) {
                body = textMessage.text();
            } else if ((msg.message().content() instanceof ImageMessage imageMessage)) {
                body = imageMessage.caption();
            } else if ((msg.message().content() instanceof ButtonsResponseMessage buttonsMessage)) {
                body = buttonsMessage.buttonId();
            } else {
                return;
            }

            ContactJid from = msg.chatJid();
            ContactJid sender = msg.senderJid();
            Store store = api.store();
            String ownerNumber = "6289636559820@s.whatsapp.net";

            String[] args = body.split(" ");
            String command = args[0].toLowerCase();
            String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            boolean isOwner = ownerNumber.equals(msg.sender().get().jid().toString());

            switch (command) {
                case "!menu" -> {
                    String menu = """
                            *Main Menu*
                            > !about
                            > !join
                            > !menu
                            > !owner
                            > !source""";
                    simple.Reply(menu);
                }
                case "!owner" -> {
                    simple.SendContactBusiness("Violetavior", "Violetavior", "6281236031617");
                }
                case "!source" -> {
                    simple.Reply("https://github.com/fckvania/java-whatsapp-bot");
                }
                case "!about" -> {
                    MavenXpp3Reader reader = new MavenXpp3Reader();
                    Model model = reader.read(new FileReader("pom.xml"));
                    String versi = model.getVersion();
                    String about = """
                            This Bot is fully written in Java and created with ❤ by Vania

                            • Version : %s
                            • JVM version : %s
                            • Library : WhatsappWeb4j
                            • Thanks to : github.com/Auties00                                                                
                            """.formatted(versi, System.getProperty("java.version"));
                    simple.Reply(about);
                }
                case "!join" -> {
                    if (text.isEmpty()) {
                        simple.Reply("Query not supported");
                        return;
                    }

                    var resp = api.acceptGroupInvite(text.replace("https://chat.whatsapp.com/", ""));
                    if (resp.join().isEmpty()) {
                        simple.Reply("Failed To Join Group");
                        return;
                    }
                    simple.Reply("Succes Join Group");
                }


                /**
                 * Owner Menu
                 */
                case "$" -> {
                    if (!isOwner) return;
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command("bash", "-c", text);
                    Process process = processBuilder.start();
                    StringBuilder output = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line + "\n");
                    }

                    int exitVal = process.waitFor();
                    if (exitVal == 0) {
                        if (output.toString() != "") {
                            simple.Reply(output.toString());
                        } else {
                            simple.Reply("Succes");
                        }
                    }
                }
            }
        } catch (IOException e) {
            api.sendMessage(ContactJid.of("6289636559820@s.whatsapp.net").toJid(), e.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        }
    }
}
