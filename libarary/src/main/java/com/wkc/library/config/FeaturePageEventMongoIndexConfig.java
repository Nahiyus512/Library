package com.wkc.library.config;

import com.wkc.library.entity.feature.FeaturePageEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

@Configuration
public class FeaturePageEventMongoIndexConfig implements InitializingBean {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void afterPropertiesSet() {
        IndexOperations indexOps = mongoTemplate.indexOps(FeaturePageEvent.class);
        indexOps.ensureIndex(new Index().on("eventId", Sort.Direction.ASC).unique());
        indexOps.ensureIndex(new Index().on("createdAt", Sort.Direction.DESC));
        indexOps.ensureIndex(new Index().on("bookId", Sort.Direction.ASC).on("createdAt", Sort.Direction.DESC));
        indexOps.ensureIndex(new Index().on("userId", Sort.Direction.ASC).on("createdAt", Sort.Direction.DESC));
        indexOps.ensureIndex(new Index().on("eventType", Sort.Direction.ASC).on("createdAt", Sort.Direction.DESC));
        indexOps.ensureIndex(new Index().on("source", Sort.Direction.ASC).on("createdAt", Sort.Direction.DESC));
    }
}

