package com.cmc.web.repository;

import com.cmc.web.beans.EBookChapter;
import com.cmc.web.beans.EBookFolder;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface EBookChapterRepository extends Neo4jRepository<EBookChapter, Long> {

}