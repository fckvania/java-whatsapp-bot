package mybot.maple.lib;

import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.button.Button;
import it.auties.whatsapp.model.contact.ContactCard;
import it.auties.whatsapp.model.contact.ContactJid;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.message.button.ButtonsMessage;
import it.auties.whatsapp.model.message.standard.ContactMessage;
import it.auties.whatsapp.model.message.standard.DocumentMessage;
import it.auties.whatsapp.model.message.standard.ImageMessage;
import it.auties.whatsapp.model.message.standard.VideoMessage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Simple {
    final Whatsapp client;
    final MessageInfo m;
    public Simple(Whatsapp api, MessageInfo info) {
        this.client = api;
        this.m = info;
    }

    /**
     *
     * @param text
     */
    public void Reply(String text) {
        this.client.sendMessage(this.m.chatJid(), text, this.m);
    }

    /**
     *
     * @param name
     * @param number
     */
    public void SendContact(String name, String number) {
        var vcard = ContactCard.newContactCardBuilder()
                .name(name)
                .phoneNumber(ContactJid.of(number))
                .build();
        var contactMessage = ContactMessage.newContactMessageBuilder()
                .name(name)
                .vcard(vcard)
                .build();
        this.client.sendMessage(this.m.chatJid(), contactMessage, this.m);
    }

    /**
     *
     * @param url
     * @param caption
     * @throws IOException
     */
    public void SendImage(String url, String caption) throws IOException {
        var media = new URL(url).openStream().readAllBytes();
        var img = ImageMessage.newImageMessageBuilder()
                .mediaConnection(this.client.store().mediaConnection())
                .media(media)
                .caption(caption)
                .build();
        this.client.sendMessage(this.m.chatJid(), img, this.m);
    }

    /**
     *
     * @param url
     * @param caption
     * @throws IOException
     */
    public void SendVideo(String url, String caption) throws IOException {
        var media = new URL(url).openStream().readAllBytes();
        var vid = VideoMessage.newVideoMessageBuilder()
                .mediaConnection(this.client.store().mediaConnection())
                .media(media)
                .caption(caption)
                .build();
        this.client.sendMessage(this.m.chatJid(), vid, this.m);
    }

    /**
     *
     * @param url
     * @param fileName
     * @param title
     * @throws IOException
     */
    public void SendDocument(String url, String fileName, String title) throws IOException {
        var media = new URL(url).openStream().readAllBytes();
        var doc = DocumentMessage.newDocumentMessageBuilder()
                .mediaConnection(this.client.store().mediaConnection())
                .media(media)
                .title(title)
                .fileName(fileName)
                .build();
        this.client.sendMessage(this.m.chatJid(), doc, this.m);
    }

    /**
     *
     * @param name
     * @param businessName
     * @param number
     */
    public void SendContactBusiness(String name, String businessName, String number) {
        var vcard = ContactCard.newContactCardBuilder()
                .name(name)
                .businessName(businessName)
                .phoneNumber(ContactJid.of(number))
                .build();
        var contactMessage = ContactMessage.newContactMessageBuilder()
                .name(name)
                .vcard(vcard)
                .build();
        this.client.sendMessage(this.m.chatJid(), contactMessage, this.m);
    }

    /**
     *
     * @param body
     * @param footer
     * @param buttons
     */
    public void SendButtonText(String body, String footer, List<Button> buttons) {
        var btn = ButtonsMessage.newButtonsWithoutHeaderMessageBuilder()
                .body(body)
                .footer(footer)
                .buttons(buttons)
                .build();
        this.client.sendMessage(this.m.chatJid(), btn, this.m);
    }
}
