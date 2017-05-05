package com.sakamichi46.wakasama.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import static com.sakamichi46.wakasama.model.EmotionType.*;
import java.util.HashMap;

/**
 *
 * @author kikuta
 */
@Getter @Setter
@AllArgsConstructor
public class Emotion {
    private double anger;
    private double contempt;
    private double disgust;
    private double fear;
    private double happiness;
    private double neutral;
    private double sadness;
    private double surprise;
    
    public String getStrongestEmotion() {
        Map<String, Double> mapEmotion = new HashMap<>();
        mapEmotion.put(ANGRY.getValue(), anger);
        mapEmotion.put(CONTEMPT.getValue(), contempt);
        mapEmotion.put(DISGUST.getValue(), disgust);
        mapEmotion.put(FEAR.getValue(), fear);
        mapEmotion.put(HAPPINESS.getValue(), happiness);
        mapEmotion.put(NEUTRAL.getValue(), neutral);
        mapEmotion.put(SADNESS.getValue(), sadness);
        mapEmotion.put(SUPRISE.getValue(), surprise);
        return mapEmotion.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).findFirst().get().getKey();
    }
}
