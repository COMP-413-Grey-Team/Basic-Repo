package edu.rice.rbox.Location.locator;

import edu.rice.rbox.Common.GameObjectUUID;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface Locator2Replication {

    //TODO: use their class;
    void fetchObjectUpdates(List<Object> object_to_server);
    //TODO: use their class;
    Object getObjectFieldValue(GameObjectUUID object_uuid, String field);
}
