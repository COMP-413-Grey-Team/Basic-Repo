package edu.rice.comp413fall2020grey.ObjStorage;
import edu.rice.comp413fall2020grey.Common.Change.LocalFieldChange;
import edu.rice.comp413fall2020grey.Common.GameObjectUUID;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class Test extends TestCase{
    public void testConstructor() throws IOException {
        try {
            ObjectStore asdf = new ObjectStore(new DummyReplicaManager(), 10);
        } catch(Exception error) {
            fail("does not construct");
        }
    }

    public void testRead() throws IOException {
        ObjectStore asdf = new ObjectStore(new DummyReplicaManager(), 10);

        HashMap<String, Serializable> gameObject = new HashMap<>();
        gameObject.put("x", 0);
        gameObject.put("y", 1);

        GameObjectUUID id = asdf.create(gameObject, new HashSet<>(), null, 0);
        assertEquals(0, asdf.read(id, "x", 0));
        assertEquals(1, asdf.read(id, "y", 0));
    }
}