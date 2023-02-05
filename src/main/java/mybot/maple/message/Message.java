package mybot.maple.message;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.controller.Store;
import it.auties.whatsapp.model.button.Button;
import it.auties.whatsapp.model.contact.ContactJid;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.message.button.ButtonsResponseMessage;
import it.auties.whatsapp.model.message.standard.ImageMessage;
import it.auties.whatsapp.model.message.standard.TextMessage;
import mybot.maple.lib.Functions;
import mybot.maple.lib.Simple;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Base64;
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
            ContactJid sender = msg.senderJid();
            Store store = api.store();
            String ownerNumber = "6281236031617@s.whatsapp.net";

            String[] args = body.split(" ");
            String command = args[0].toLowerCase();
            String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            Boolean isOwner = ownerNumber.equals(msg.sender().get().jid().toString());
            Boolean isGroup = msg.chatJid().hasServer(ContactJid.Server.GROUP);
            Boolean isAdmin = simple.CheckGroupAdmin(from, sender);
            Boolean isMeAdmin = simple.CheckGroupAdmin(from, store.userCompanionJid().toUserJid());

            switch (command) {
                /**
                 * Main Menu
                 */
                case "!menu":
                    String menu = """
                            *Main Menu*
                            > !join
                            > !menu
                            > !owner
                            
                            *Converter Menu*
                            > !sticker

                            *Downloader Menu*
                            > !play
                            > !tiktok

                            *Group Menu*
                            > !link
                             
                            *Owner Menu*
                            > $        
                                                   """;
                    Button btn = Button.of("!owner", "Creator");
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
                case "!join": {
                    if (text.equals("")) {
                        simple.Reply("Query not supported");
                        return;
                    }

                    var resp = api.acceptGroupInvite(text.replace("https://chat.whatsapp.com/", ""));
                    if(resp.join().isEmpty()) {
                        simple.Reply("Failed To Join Group");
                        return;
                    }
                    simple.Reply("Succes Join Group");
                    break;
                }
                /**,
                 * Downloader Menu
                  */
                case "!play": {
                    if (text.equals("")) {
                        simple.Reply("Apa Yang Mau di Cari?.");
                        return;
                    }
                    simple.Reply("Mohon Di Tunggu.");
                    String resp = new Functions().Fetch("http://localhost:5555/youtube/download/%s?type=mp3".formatted(text.replace(" ", "+")));
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
                    String resp = new Functions().Fetch("http://localhost:5555/tiktok/download?url=%s".formatted(text));
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
                    if (!isMeAdmin) {
                        simple.Reply("Nalle Not Admin :(");
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
                 * Convert Menu
                 */
                case "!sticker":
                case "!s":
                    if (msg.quotedMessage().isEmpty() && !(msg.message().content() instanceof ImageMessage)) {
                        simple.Reply("Media Not Found");
                        return;
                    }
                    byte[] media;
                    if (msg.message().content() instanceof ImageMessage) {
                        media = api.downloadMedia(msg).join();
                    } else if (msg.quotedMessage().get().message().content() instanceof  ImageMessage){
                        var getMsg = store.findMessageById(msg.chat(), msg.quotedMessage().get().id()).get();
                        media = api.downloadMedia(getMsg).join();
                    } else {
                        simple.Reply("Media Not Found");
                        return;
                    }
                    byte[] bass64 = Base64.getEncoder().encode(media);

                    JSONObject json = new JSONObject();

                    HttpClient httpClient = HttpClientBuilder.create().build();
                    var ok = new String(bass64);

                    json.put("image", ok);
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    HttpPost request = new HttpPost("http://localhost:5555/sticker/create");
                    request.setEntity(se);

                    HttpResponse response = httpClient.execute(request);
                    String result = "";
                    if(response!=null){
                        InputStream in = response.getEntity().getContent();
                        result = new Functions().convertStreamToString(in);
                        in.close();
                    }

                    var res = new Gson().fromJson(result, JsonObject.class);
                    byte[] dec = Base64.getDecoder().decode(new String(res.get("webpBase64").getAsString()).getBytes());
                    simple.SendSticker(dec);
                    break;
                /**
                 * Owner Menu
                 */
                case "$":
                    if (!isOwner) return;
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command("cmd.exe", "/c", text);
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
            api.sendMessage(ContactJid.of("6281236031617@s.whatsapp.net").toJid(), e.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
