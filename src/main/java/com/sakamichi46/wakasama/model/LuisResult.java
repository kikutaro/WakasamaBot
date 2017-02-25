package com.sakamichi46.wakasama.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author kikuta
 */
@Getter @Setter
@AllArgsConstructor
public class LuisResult {
    private Intent topScoringIntent;
    private List<Intent> intents;
    private List<Entity> entities;
}
