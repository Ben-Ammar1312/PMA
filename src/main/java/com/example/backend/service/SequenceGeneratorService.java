package com.example.backend.service;

import com.example.backend.model.DatabaseSequence;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class SequenceGeneratorService {
    private final MongoOperations mongo;

    public SequenceGeneratorService(MongoOperations mongo) {
        this.mongo = mongo;
    }

    public long getNextSequence(String seqName) {
        // find the counter for seqName and increment seq by 1, atomically
        var query = Query.query(Criteria.where("_id").is(seqName));
        var update = new Update().inc("seq", 1);
        var opts   = FindAndModifyOptions.options().returnNew(true).upsert(true);
        var counter = mongo.findAndModify(query, update, opts, DatabaseSequence.class);
        return counter.getSeq();
    }
}

