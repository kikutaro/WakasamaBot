package com.sakamichi46.wakasama.bot;

import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.sakamichi46.wakasama.model.Answer;
import com.sakamichi46.wakasama.model.ConversationMessage;
import com.sakamichi46.wakasama.model.ConversationResponse;
import com.sakamichi46.wakasama.model.Image;
import com.sakamichi46.wakasama.model.Images;
import com.sakamichi46.wakasama.model.Question;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import retrofit2.Response;

/**
 * Botサービス.
 * 
 * 会話のハンドリングなど行うBotのメイン処理となるサービスクラス.
 * 
 * @author kikuta
 */
@Service
public class BotService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private LineMessagingService lineMessagingService;
    
    @Value("docomo.apikey")
    private String docomoApiKey;
    
    @Value("${com.microsoft.cognitive.qnamaker}")
    private String fnqMakerKey;
    
    @Value("${com.microsoft.cognitive.bing.image}")
    private String bingImageKey;
    
    private MultiValueMap<String, String> headers;
    
    private HttpEntity request;
    
    @PostConstruct
    public void init() {
        System.out.println("init");
        headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        
    }
    
    public Message handler(MessageEvent event) {
        event.getSource().getUserId();
        if(event.getMessage() instanceof TextMessageContent) {
            String message = ((TextMessageContent)event.getMessage()).getText();
            if(message.contains("画像") || message.contains("写真") || message.contains("顔")) {
                return image("若月佑美");
            } else if(message.contains("はし") || message.contains("はしくん") || message.contains("箸") || message.contains("箸くん")) {
                return image("若月佑美 箸くん");
            }
            
            String answer = faq(message);
            if(!answer.equals("No good match found in the KB")) {
                return new TextMessage(answer);
            }
        }
        return talk(event);
    }
    
    public String profile(Event event) {
        Response<UserProfileResponse> execute;
        try {
            execute = lineMessagingService.getProfile(event.getSource().getUserId()).execute();
            return execute.body().getDisplayName();
        } catch (IOException ex) {
            Logger.getLogger(BotService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ななしのごんべえ";
    }
    
    private TextMessage talk(MessageEvent event) {
        Response<UserProfileResponse> execute;
        try {
            execute = lineMessagingService.getProfile(event.getSource().getUserId()).execute();
            String name = execute.body().getDisplayName();
            ConversationMessage message = new ConversationMessage(((TextMessageContent)event.getMessage()).getText(), name, name, "男", "35");
            request = new HttpEntity<>(message,headers);
            ConversationResponse response = restTemplate.postForObject("https://api.apigw.smt.docomo.ne.jp/dialogue/v1/dialogue?APIKEY=" + docomoApiKey, message, ConversationResponse.class, headers);
            return new TextMessage(response.getUtt());       
        } catch (IOException ex) {
            Logger.getLogger(BotService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new TextMessage("こんばんわかつき！");
    }
    
    private ImageMessage image(String keyword) {
        headers.set("Ocp-Apim-Subscription-Key", bingImageKey);
        HttpEntity entity = new HttpEntity(headers);
        HttpEntity<Images> images = restTemplate.exchange("https://api.cognitive.microsoft.com/bing/v5.0/images/search?q="+keyword+"&count=30&mkt=ja-JP", HttpMethod.GET, entity, Images.class, headers);
        
        long seed = System.currentTimeMillis();
        Random r = new Random(seed);
        Image image = images.getBody().getValue().get(r.nextInt(30));
        return new ImageMessage(image.getWebSearchUrl(), image.getThumbnailUrl());
    }
    
    private String faq(String word) {
        headers.set("Ocp-Apim-Subscription-Key", fnqMakerKey);
        Question question = new Question(word);
        request = new HttpEntity<>(question,headers);
        
        Answer answer = restTemplate.postForObject("https://westus.api.cognitive.microsoft.com/qnamaker/v1.0/knowledgebases/00c0abf2-3f70-4e3c-85d3-8b361755d961/generateAnswer", request, Answer.class);
        return answer.getAnswer();
    }
}
