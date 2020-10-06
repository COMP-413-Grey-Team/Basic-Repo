package edu.rice.comp413fall2020grey.ObjStorage;

import edu.rice.comp413fall2020grey.Common.GameObjectUUID;

import java.io.Serializable;

public class Change {
    int bufferIndex;
    GameObjectUUID target;
    String field;
    Serializable value;
}
