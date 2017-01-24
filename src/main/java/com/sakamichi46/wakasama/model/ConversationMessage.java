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
public class ConversationMessage {
    private String utt;
    private String nickname;
    private String nickname_y;
    private String sex;
    private String age;
}
