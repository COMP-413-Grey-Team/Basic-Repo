package edu.rice.rbox.Location.interest;

import com.mongodb.client.model.Filters;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.ObjStorage.ObjectLocationStorageInterface;
import org.bson.conversions.Bson;

import static edu.rice.rbox.Location.interest.PredicateBiOperator.AND;

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
    public Bson toMongoQuery(GameObjectUUID relative_object_uuid, ObjectLocationStorageInterface storage) {
        if (op == AND) {
            return Filters.and(
                p1.toMongoQuery(relative_object_uuid, storage),
                p2.toMongoQuery(relative_object_uuid, storage)
            );
        } else {
            return Filters.or(
                    p1.toMongoQuery(relative_object_uuid, storage),
                    p2.toMongoQuery(relative_object_uuid, storage)
            );
        }
    }
}
