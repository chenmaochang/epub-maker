package com.cmc.web.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

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

    public String calculateEbookFullPath() {
        return this.path + this.name;
    }
}
