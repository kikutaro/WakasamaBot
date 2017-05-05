package com.sakamichi46.wakasama.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author kikuta
 */
@Getter @Setter
@AllArgsConstructor
public class FaceAttributes {
    private double age;
    private String gender;
    private double smile;
    private Emotion emotion;
}
