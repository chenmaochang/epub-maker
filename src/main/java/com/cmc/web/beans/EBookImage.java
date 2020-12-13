package com.cmc.web.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Node
public class EBookImage implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    @Property
    private String fullName;
    @Builder.Default
    @Property
    private String suffix=".jpeg";
    @Property
    private String downloadUrl;
}
