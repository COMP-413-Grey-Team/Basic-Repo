package test.edu.rice.rbox.Location;

import edu.rice.rbox.Common.GameField.GameField;
import edu.rice.rbox.Common.GameField.GameFieldInteger;
import edu.rice.rbox.Common.GameField.InterestingGameField;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.ObjStorage.ObjectLocationStorageInterface;

public class DummyStorageToLocation implements ObjectLocationStorageInterface {

    private static class Test_Game_Field implements InterestingGameField<Integer> {

        @Override
        public Integer getValue() {
            return 1;
        }

        @Override
        public GameField copy() {
            return null;
        }
    }

    @Override
    public InterestingGameField queryOneField(GameObjectUUID id, String field) {
        return new Test_Game_Field();
    }
}
