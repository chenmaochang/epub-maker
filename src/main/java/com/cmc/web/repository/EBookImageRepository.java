package com.cmc.web.repository;

import com.cmc.web.beans.EBookFolder;
import com.cmc.web.beans.EBookImage;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface EBookImageRepository extends Neo4jRepository<EBookImage, Long> {

}