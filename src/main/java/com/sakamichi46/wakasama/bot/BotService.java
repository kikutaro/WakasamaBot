package com.sakamichi46.wakasama.bot;

import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.profile.UserProfileResponse;
import static com.sakamichi46.wakasama.constant.WakasamaBotConst.*;
import com.sakamichi46.wakasama.model.Answer;
import com.sakamichi46.wakasama.model.ConversationMessage;
import com.sakamichi46.wakasama.model.ConversationResponse;
import com.sakamichi46.wakasama.model.Image;
import com.sakamichi46.wakasama.model.Images;
import com.sakamichi46.wakasama.model.Question;
import com.sakamichi46.wakasama.model.news.News;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
import twitter4j.Status;

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
    
    @Autowired
    private TwitterHandler twitterHandler;
    
    @Value("${docomo.apikey}")
    private String docomoApiKey;
    
    @Value("${com.microsoft.cognitive.qnamaker}")
    private String fnqMakerKey;
    
    @Value("${com.microsoft.cognitive.bing}")
    private String bingKey;
    
    private MultiValueMap<String, String> headers;
    
    private HttpEntity request;
    
    @Value("#{'${wakasama}'.split(',')}")
    private List<String> wakasamaKeywords;
    
    @Value("#{'${wakasama.photo}'.split(',')}")
    private List<String> photoKeywords;
    
    @Value("#{'${wakasama.news}'.split(',')}")
    private List<String> newsKeywords;
    
    @Value("#{'${wakasama.chopstick}'.split(',')}")
    private List<String> chopstickKeywords;
    
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
            
            if(wakasamaKeywords.stream().anyMatch(w -> message.contains(w))) {
                return showWakaInfoLink();
            } else if(photoKeywords.stream().anyMatch(w -> message.contains(w))) {
                return image(YUMI_WAKATSUKI.getValue());
            } else if(chopstickKeywords.stream().anyMatch(w -> message.contains(w))) {
                return image(YUMI_WAKATSUKI.getValue() + " 箸くん");
            } else if(newsKeywords.stream().anyMatch(w -> message.contains(w))) {
                return news(YUMI_WAKATSUKI.getValue());
            } else if(message.equals("#evatfm")) {
                return tweet();
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
    
    public TemplateMessage showWakaInfoLink() {
        //プロフィール、ブログなど
        return new TemplateMessage(YUMI_WAKATSUKI.getValue(), new ButtonsTemplate(
                "https://obs.line-scdn.net/0m0edf13c47251318a8d62c2c5cd52f6ae626d747e192f/f256x256png"
                , YUMI_WAKATSUKI.getValue()
                ,"こんにちわかつき～"
                ,Arrays.asList(
                        new URIAction("ブログ", "http://blog.nogizaka46.com/yumi.wakatsuki/"),
                        new URIAction("Wikipedia", "https://ja.wikipedia.org/wiki/%E8%8B%A5%E6%9C%88%E4%BD%91%E7%BE%8E"),
                        new URIAction("グッズ", "http://www.nogizaka46shop.com/category/18")
                )));
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
        headers.set("Ocp-Apim-Subscription-Key", bingKey);
        HttpEntity entity = new HttpEntity(headers);
        HttpEntity<Images> images = restTemplate.exchange("https://api.cognitive.microsoft.com/bing/v5.0/images/search?q="+keyword+"&count=30&mkt=ja-JP", HttpMethod.GET, entity, Images.class, headers);
        
        long seed = System.currentTimeMillis();
        Random r = new Random(seed);
        Image image = images.getBody().getValue().get(r.nextInt(30));
        return new ImageMessage(image.getWebSearchUrl(), image.getThumbnailUrl());
    }
    
    private TemplateMessage news(String message) {
        //Cognitiveのテキストで分解
        headers.set("Ocp-Apim-Subscription-Key", bingKey);
        HttpEntity entity = new HttpEntity(headers);
        HttpEntity<News> news = restTemplate.exchange("https://api.cognitive.microsoft.com/bing/v5.0/news/search?q="+message+"&count=5&mkt=ja-JP", HttpMethod.GET, entity, News.class, headers);
        return new TemplateMessage("ニュース", new CarouselTemplate(
            news.getBody().getValue().stream()
                .map(n -> 
                    new CarouselColumn(
                        n.getImage().getThumbnail().getContentUrl(),
                        n.getName().length() > 40 ? n.getName().substring(0, 40) : n.getName(),
                        n.getDescription().length() > 60 ? n.getDescription().substring(0, 60) : n.getDescription(), 
                            Arrays.asList(new URIAction("記事を読む", n.getUrl())
                        )
                    ))
                .collect(Collectors.toList())));
    }
    
    private Message tweet() {
        Status tweet = twitterHandler.selectTweet();
        List<Action> listActions = new ArrayList<>();
        listActions.add(new URIAction("@waki_evatfm", "https://twitter.com/waki_evatfm"));
        if(tweet != null) {
            if(tweet.getURLEntities() != null && tweet.getURLEntities().length > 0) {
                listActions.add(new URIAction("ツイートリンク", tweet.getURLEntities()[0].getURL()));
            }
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            return new TemplateMessage(
                    "#evatfmのツイート", new ButtonsTemplate(
                            null,
                            null,
                            df.format(tweet.getCreatedAt()) + "\r\n" + tweet.getText(),
                            listActions
                    )
            );
        }
        return new TextMessage("ツイートが見つからなかったよぉ(涙)");
    }
     
    private String faq(String word) {
        headers.set("Ocp-Apim-Subscription-Key", fnqMakerKey);
        Question question = new Question(word);
        request = new HttpEntity<>(question,headers);
        
        Answer answer = restTemplate.postForObject("https://westus.api.cognitive.microsoft.com/qnamaker/v1.0/knowledgebases/00c0abf2-3f70-4e3c-85d3-8b361755d961/generateAnswer", request, Answer.class);
        return answer.getAnswer();
    }
}
