package org.art.rx.reilly.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RssArticle implements Serializable {

    private String title;
    private String description;
    private String link;
    private LocalDateTime pubDate;
}
