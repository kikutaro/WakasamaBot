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
public class Entity {
    private String entity;
    private String type;
    private double score;
}
