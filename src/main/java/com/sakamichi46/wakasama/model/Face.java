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
public class Face {
    private String faceId;
    private FaceAttributes faceAttributes;
}
