package com.sakamichi46.wakasama.bot;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
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

    public static void main(String[] args) {
        SpringApplication.run(WakasamaBotApplication.class, args);
    }
    
    @Autowired
    private BotService bot;
    
    /**
     * フォロー(友達追加)イベント処理.
     * 
     * @param event フォローイベント
     * @return メッセージ
     */
    @EventMapping
    public Message handleFollowEvent(FollowEvent event) {
        String name = bot.profile(event);
        return new TextMessage(name + "さんと友達になっちゃった！");
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
