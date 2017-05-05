package com.sakamichi46.wakasama.model;

import lombok.Getter;

/**
 *
 * @author kikuta
 */
public enum EmotionType {
    
    ANGRY("怒ってそうな"),
    CONTEMPT("ちょっと人を軽蔑してそうな"),
    DISGUST("イライラしてそうな"),
    FEAR("何かに怯えてそうな"),
    HAPPINESS("幸せそうな"),
    NEUTRAL("自然な"),
    SADNESS("悲しそうな"),
    SUPRISE("驚いてるような");
    
    @Getter
    private String value;
    
    EmotionType(String value) {
        this.value = value;
    }
}
