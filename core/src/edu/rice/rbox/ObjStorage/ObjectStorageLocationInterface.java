package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.GameField.GameField;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.GameField.InterestingGameField;
import edu.rice.rbox.Location.interest.InterestPredicate;

import java.util.HashMap;
import java.util.Map;

/**
 * The interface implemented by Location that will be used by Object Storage
 */
public interface ObjectStorageLocationInterface {

  void add(GameObjectUUID id, InterestPredicate predicate, HashMap<String, InterestingGameField> value);

  void update(GameObjectUUID id, String field, InterestingGameField value);

  void delete(GameObjectUUID id);

  void addGlobalObjectField(String fieldname, Object fieldvalue);

  Map<String, Object> getGlobalObjectFields();

  void queryInterest();
}
