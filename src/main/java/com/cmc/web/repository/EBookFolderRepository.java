package com.cmc.web.repository;

import com.cmc.web.beans.EBookFolder;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface EBookFolderRepository extends Neo4jRepository<EBookFolder, Long> {

}