package com.sakamichi46.wakasama.model.news;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author kikuta
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Value {
    private String name;
    private String url;
    private NewsImage image;
    private String description;
}
