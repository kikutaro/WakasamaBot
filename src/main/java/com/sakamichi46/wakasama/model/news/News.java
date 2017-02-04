package com.sakamichi46.wakasama.model.news;

import java.util.List;
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
public class News {
    private List<Value> value;
}
