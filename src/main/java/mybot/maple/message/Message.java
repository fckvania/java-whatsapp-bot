package mybot.maple.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.button.Button;
import it.auties.whatsapp.model.contact.ContactJid;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.message.button.ButtonsResponseMessage;
import it.auties.whatsapp.model.message.standard.ImageMessage;
import it.auties.whatsapp.model.message.standard.TextMessage;
import mybot.maple.lib.Functions;
import mybot.maple.lib.Simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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
            String ownerNumber = "6281236031617@s.whatsapp.net";

            String[] args = body.split(" ");
            String command = args[0].toLowerCase();
            String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            Boolean isOwner = ownerNumber.equals(msg.sender().get().jid().toString());
            Boolean isGroup = msg.chatJid().hasServer(ContactJid.Server.GROUP);

            switch (command) {
                /**
                 * Main Menu
                 */
                case "!menu":
                    String menu = """
                            *Main Menu*
                            > !menu
                            > !owner

                            *Downloader Menu*
                            > !play
                            > !tiktok

                            *Group Menu*
                            > !link
                             
                            *Owner Menu*
                            > $        
                                                   """;
                    Button btn = Button.newResponseButton("!owner", "Creator");
                    simple.SendButtonText(menu, "This Bot fully written in Java", List.of(btn));
                    break;
                case "!owner":
                    simple.SendContactBusiness("Violetavior", "Violetavior", "6281236031617");
                    break;
                case "!about":
                    String about = """
                            This Bot is fully written in Java and created with ?? by Vania

                            ? Version : 1.0-SNAPSHOT
                            ? Thanks to : github.com/Auties00
                                                
                            """;
                    simple.Reply(about);
                    break;
                /**,
                 * Downloader Menu
                  */
                case "!play": {
                    if (text.equals("")) {
                        simple.Reply("Apa Yang Mau di Cari?.");
                        return;
                    }
                    simple.Reply("Mohon Di Tunggu.");
                    String resp = new Functions().Fetch("http://20.168.230.160:5555/youtube/download/%s?type=mp3".formatted(text.replace(" ", "+")));
                    JsonObject json = new Gson().fromJson(resp, JsonObject.class);
                    String title = json.get("title").getAsString();
                    String duration = json.get("timestamp").getAsString();
                    String capt = "*Youtube Downloader*\n\n*Title :* %s\n*Duration :* %s\n\n*Audio Sedang Dikirim*"
                            .formatted(title, duration);
                    simple.SendImage(json.get("thumbnail").getAsString(), capt);
                    simple.SendDocument(json.get("results").getAsString(), "%s.mp3".formatted(title), title);
                    break;
                }
                case "!tiktok": {
                    if (text.equals("")) {
                        simple.Reply("Apa Yang Mau di Cari?.");
                        return;
                    } else if(!Pattern.compile("tiktok", Pattern.CASE_INSENSITIVE).matcher(text).find()) {
                        simple.Reply("Silahkan Masukan Link Tiktok.");
                        return;
                    }
                    simple.Reply("Mohon Di Tunggu.");
                    String resp = new Functions().Fetch("http://20.168.230.160:5555/tiktok/download?url=%s".formatted(text));
                    JsonObject json = new Gson().fromJson(resp, JsonObject.class);
                    String capt = json.get("caption").getAsString();
                    simple.SendVideo(json.get("mp4").getAsString(), capt);
                    break;
                }
                /**
                 * Group Menu
                 */
                case "!link": {
                    if(!isGroup) {
                        simple.Reply("Command Tidak Support Di Luar Group!.");
                        return;
                    }
                    String subject = msg.chat().name();
                    String anu = api.queryGroupInviteCode(from)
                            .join();
                    String res = "Link For Group *%s* :\n\nhttps://chat.whatsapp.com/%s"
                            .formatted(subject, anu);
                    simple.Reply(res);
                    break;
                }
                /**
                 * Owner Menu
                 */
                case "$":
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
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
