package com.sakamichi46.wakasama.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import lombok.experimental.PackagePrivate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 *
 * @author kikuta
 */
@EnableAsync
@Configuration
@EnableScheduling
@Component
public class TwitterHandler {
    
    private Twitter twitter;
    
    private List<Status> listEvaTweet;
    
    public int MAX_PAGE = 200;
    
    public int COUNT = 100;
    
    @PostConstruct
    public void init() {
        System.out.println("init cron");
        updateTweet();
    }
    
    @Scheduled(cron ="0 0 2 * * MON")
    public void updateTweet() {
        System.out.println("update tweet");
        if(twitter == null) {
            twitter = TwitterFactory.getSingleton();
        }
        listEvaTweet = new ArrayList<>();
        Paging page = new Paging(MAX_PAGE, COUNT);

        IntStream.range(1, MAX_PAGE).forEach(i -> {
            page.setPage(i);
            try {
                ResponseList<Status> userTimeline = twitter.getUserTimeline("@waki_evatfm", page);
                userTimeline.stream().forEach(s -> {
                    if(!listEvaTweet.contains(s)) {
                        listEvaTweet.add(s);
                    }
                });
            } catch (TwitterException ex) {
                System.out.println(ex);
            }
        });
    }
    
    public Status selectTweet() {
        if(listEvaTweet != null & listEvaTweet.size() > 0) {
            int rnd = (int)(Math.random() * listEvaTweet.size());
            return listEvaTweet.get(rnd);
        }
        return null;
    }
}
