package com.cmc.web.repository;

import com.cmc.web.beans.EBook;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface EBookRepository extends Neo4jRepository<EBook, String> {

}