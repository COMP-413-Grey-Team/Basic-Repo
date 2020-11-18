package edu.rice.rbox.Location.interest;

import com.mongodb.client.model.Filters;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Location.locator.Locator2Replication;
import edu.rice.rbox.ObjStorage.ObjectLocationStorageInterface;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public class CompositePredicate extends InterestPredicate {
    private final PredicateBiOperator op;
    private final InterestPredicate p1;
    private final InterestPredicate p2;

    public CompositePredicate(InterestPredicate p1, InterestPredicate p2, PredicateBiOperator op) {
        super(null, null);
        this.op = op;
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public Bson toMongoQuery(GameObjectUUID relative_object_uuid, ObjectLocationStorageInterface storage) {
        return switch (op) {
            case AND -> Filters.and(
                    p1.toMongoQuery(relative_object_uuid, storage),
                    p2.toMongoQuery(relative_object_uuid, storage)
            );
            case OR -> Filters.or(
                    p1.toMongoQuery(relative_object_uuid, storage),
                    p2.toMongoQuery(relative_object_uuid, storage)
            );
        };
    }
}
