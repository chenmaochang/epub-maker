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
public class EBookChapter implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    @Property
    private String fullName;
    @Property
    private String chineseName;
    @Property
    private String title;
    @Relationship(type = "CONTAINED", direction = Relationship.Direction.INCOMING)
    private List<EBookImage> images;
}
