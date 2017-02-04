package com.sakamichi46.wakasama.constant;

import lombok.Getter;

/**
 *
 * @author kikuta
 */
public enum WakasamaBotConst {
    YUMI_WAKATSUKI("若月佑美");
    
    @Getter
    private final String value;
    
    private WakasamaBotConst(String value) {
        this.value = value;
    }
}
