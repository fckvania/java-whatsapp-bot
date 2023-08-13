package mybot.maple.lib;

import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.model.contact.ContactCard;
import it.auties.whatsapp.model.contact.ContactJid;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.message.standard.*;

import java.io.IOException;
import java.net.URL;

public class Simple {
    final Whatsapp client;
    final MessageInfo m;
    public Simple(Whatsapp api, MessageInfo info) {
        this.client = api;
        this.m = info;
    }

    public void Reply(String text) {
        this.client.sendMessage(this.m.chatJid(), text, this.m);
    }

    public void SendContact(String name, String number) {
        var vcard = ContactCard.builder()
                .name(name)
                .phoneNumber(ContactJid.of(number))
                .build();
        var contactMessage = ContactMessage.builder()
                .name(name)
                .vcard(vcard)
                .build();
        this.client.sendMessage(this.m.chatJid(), contactMessage, this.m);
    }

    public void SendImage(String url, String caption) throws IOException {
        var media = new URL(url).openStream().readAllBytes();
        var img = ImageMessage.simpleBuilder()
                .media(media)
                .caption(caption)
                .build();
        this.client.sendMessage(this.m.chatJid(), img, this.m);
    }

    public void SendVideo(String url, String caption) throws IOException {
        var media = new URL(url).openStream().readAllBytes();
        var vid = VideoMessage.simpleVideoBuilder()
                .media(media)
                .caption(caption)
                .build();
        this.client.sendMessage(this.m.chatJid(), vid, this.m);
    }

    public void SendSticker(byte[] buffer) {
        var stc = StickerMessage.simpleBuilder()
                .media(buffer)
                .animated(false)
                .build();
        this.client.sendMessage(this.m.chatJid(), stc, this.m);
    }

    public void SendDocument(String url, String fileName, String title) throws IOException {
        var media = new URL(url).openStream().readAllBytes();
        var doc = DocumentMessage.simpleBuilder()
                .media(media)
                .title(title)
                .fileName(fileName)
                .build();
        this.client.sendMessage(this.m.chatJid(), doc, this.m);
    }


    public void SendContactBusiness(String name, String businessName, String number) {
        var vcard = ContactCard.builder()
                .name(name)
                .businessName(businessName)
                .phoneNumber(ContactJid.of(number))
                .build();
        var contactMessage = ContactMessage.builder()
                .name(name)
                .vcard(vcard)
                .build();
        this.client.sendMessage(this.m.chatJid(), contactMessage, this.m);
    }


    /**
     *
     * @param jid
     * @param sender
     * @return
     */
    public boolean CheckGroupAdmin(ContactJid jid, ContactJid sender) {
        if (!this.m.chatJid().hasServer(ContactJid.Server.GROUP)) {
            return false;
        }
        var meta = this.client.queryGroupMetadata(jid).join().participants();
        for (var admin: meta) {
            if (admin.role().name() == "ADMIN" || admin.role().name() == "FOUNDER") {
                if (admin.jid().toString().equals(sender.toString())) {
                    return true;
                }
            }
        }
        return false;
    }
}
