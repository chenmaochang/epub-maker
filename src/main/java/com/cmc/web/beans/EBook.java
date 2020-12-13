package com.cmc.web.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.neo4j.core.schema.*;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Node
public class EBook implements Serializable {
    @Id
    private String identifier;
    @Property("title")
    private String title;
    @Property("chineseName")
    private String chineseName;
    @Builder.Default
    @Property("language")
    private String language = "zh";
    @Property("creator")
    private String creator;
    @Builder.Default
    @Property("publisher")
    private String publisher = "暂无";
    @Property("date")
    private String date;

    @Relationship(type = "WITH_COVER", direction = Relationship.Direction.INCOMING)
    private EBookChapter cover;

    @Relationship(type = "CONTAINED", direction = Relationship.Direction.INCOMING)
    private List<EBookChapter> chapters;

}
