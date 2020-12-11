package edu.rice.rbox.Game.Server;

import edu.rice.rbox.Common.Change.LocalChange;
import edu.rice.rbox.Common.GameField.GameField;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Location.interest.InterestPredicate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public interface Server2Store {

  GameField read(GameObjectUUID gameObjectID, String field, int bufferIndex);

  boolean write(LocalChange change, GameObjectUUID author);

  GameObjectUUID create(HashMap<String, GameField> fields, HashSet<String> interesting_fields,
                        InterestPredicate predicate, GameObjectUUID author, int bufferIndex);

  Set<LocalChange> synchronize();

  void advanceBuffer();

  boolean delete(GameObjectUUID uuid, GameObjectUUID author);

}
