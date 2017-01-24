package com.sakamichi46.wakasama.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Question for QnA Maker.
 * 
 * Microsoft Cognitive Service QnA Maker.
 * 
 * @author kikuta
 */
@Getter @Setter
@AllArgsConstructor
public class Question {
    private String question;
}
