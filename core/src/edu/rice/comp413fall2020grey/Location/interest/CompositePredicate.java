package edu.rice.comp413fall2020grey.Location.interest;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public class CompositePredicate implements InterestPredicate {
    private final PredicateBiOperator op;
    private final InterestPredicate p1;
    private final InterestPredicate p2;

    public CompositePredicate(InterestPredicate p1, InterestPredicate p2, PredicateBiOperator op) {
        this.op = op;
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public Bson toMongoQuery(HashMap<String, Serializable> map) {
        switch (op) {
            case AND:
                return Filters.and(
                        p1.toMongoQuery(map),
                        p2.toMongoQuery(map)
                );
            case OR:
                return Filters.or(
                        p1.toMongoQuery(map),
                        p2.toMongoQuery(map)
                );
        }
        return null;
    }
}
