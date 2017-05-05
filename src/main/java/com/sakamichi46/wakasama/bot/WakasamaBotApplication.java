package com.sakamichi46.wakasama.bot;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 若様LINE BOTアプリケーション.
 * 
 * @author kikuta
 */
@SpringBootApplication
@LineMessageHandler
public class WakasamaBotApplication {
    
    @Autowired
    private BotService bot;
    
    @Autowired
    private LineMessagingClient lineMessagingClient;
    
    @Autowired
    private SendMailService mail;

    public static void main(String[] args) {
        SpringApplication.run(WakasamaBotApplication.class, args);
    }
    
    /**
     * フォロー(友達追加)イベント処理.
     * 
     * @param event フォローイベント
     * @return メッセージ
     */
    @EventMapping
    public Message handleFollowEvent(FollowEvent event) {
        String name = bot.profile(event);
        mail.sendMail(event.getTimestamp().atZone(ZoneId.of("Asia/Tokyo")).toString() + "\r\n");
        return new TextMessage(name + "さんと友達になっちゃった！"
                + "「若様」"
                + "「ニュース」");
    }
    
    /**
     * テキストメッセージイベント処理.
     * 
     * Microsoft Cognitive ServiceやDocomo APIとの連携など.
     * 
     * @param event メッセージイベント
     * @return メッセージ
     */
    @EventMapping
    public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        return bot.handler(event);
    }
    
    /**
     * 画像メッセージイベント処理.
     * 
     * @param event 画像イベント
     * @return メッセージ
     */
    @EventMapping
    public Message handleImageEvent(MessageEvent<ImageMessageContent> event) {
        MessageContentResponse response = null;
        try {
            response = lineMessagingClient.getMessageContent(event.getMessage().getId()).get();
            try(InputStream is = response.getStream()) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                byte[] byteArray = buffer.toByteArray();
                return bot.faceImage(byteArray);
            } catch (IOException ex) {
                Logger.getLogger(WakasamaBotApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(WakasamaBotApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @EventMapping
    public Message handleLineBeaconEvent(BeaconEvent event) {
        return new TextMessage("あっ、これ、LINE Beaconからの通知だよ！私は近くにいるよー！");
    }
    
    /**
     * デフォルトイベント処理.
     * 
     * 現状はテキスト以外のメッセージを受け付けたときに固定文言を返す.
     * 
     * @param event イベント
     * @return メッセージ
     */
    @EventMapping
    public Message handleDefaultMessageEvent(Event event) {
        return new TextMessage("若月佑美だよ～");
    }
}
