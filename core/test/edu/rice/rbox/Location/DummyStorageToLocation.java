package edu.rice.rbox.Location;

import edu.rice.rbox.Common.GameField.GameField;
import edu.rice.rbox.Common.GameField.GameFieldInteger;
import edu.rice.rbox.Common.GameField.GameFieldString;
import edu.rice.rbox.Common.GameField.InterestingGameField;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.ObjStorage.ObjectLocationStorageInterface;

public class DummyStorageToLocation implements ObjectLocationStorageInterface {

    @Override
    public InterestingGameField queryOneField(GameObjectUUID id, String field) {
        if (field.equalsIgnoreCase("team")) {
            return new GameFieldString("red");
        } else if (field.equalsIgnoreCase("location")) {
            return new GameFieldInteger(123);
        }
        return null;
    }
}
