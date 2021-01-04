package com.cmc.web.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Node
public class EBookFolder {
    @Id
    @GeneratedValue
    private Long id;
    @Property
    private String path;
    @Property
    private String name;
    @Relationship(type = "INSIDE", direction = Relationship.Direction.INCOMING)
    private EBook eBook;

    public String calculateEbookFullPath() {
        return this.path + this.name;
    }
}
