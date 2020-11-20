package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.Change.*;
import edu.rice.rbox.Common.GameField.GameField;
import edu.rice.rbox.Common.GameObjectUUID;
import junit.framework.TestCase;

import java.time.Instant;
import java.util.*;

public class Test extends TestCase {

    private static class GameInteger implements GameField {
        int value;
        GameInteger(int i) {
            value = i;
        }

        @Override
        public GameField copy() {
            return new GameInteger(value);
        }

        public boolean equals(Object o) {
            return o instanceof GameInteger && ((GameInteger) o).value == value;
        }
    }

    private static class GameList implements GameField {
        ArrayList<Integer> value;
        GameList(ArrayList<Integer> l) {
            value = l;
        }

        @Override
        public GameField copy() {
            return new GameList((ArrayList<Integer>) value.clone());
        }

        public boolean equals(Object o) {
            return o instanceof GameList && ((GameList) o).value.equals(value);
        }
    }

    public void testConstructor() {
        try {
            ObjectStore asdf = new ObjectStore(new DummyReplicaManager(), new DummyLocationManager(), 10);
        } catch(Exception error) {
            fail("does not construct");
        }
    }

    public void testRead() {
        ObjectStore asdf = new ObjectStore(new DummyReplicaManager(), new DummyLocationManager(), 10);

        HashMap<String, GameField> gameObject = new HashMap<>();
        gameObject.put("x", new GameInteger(0));
        gameObject.put("y", new GameInteger(1));

        GameObjectUUID id = asdf.create(gameObject, new HashSet<>(), null, null, 0);
        assertEquals(new GameInteger(0), asdf.read(id, "x", 0));
        assertEquals(new GameInteger(1), asdf.read(id, "y", 0));
    }

    public void testWrite() {
        ObjectStore asdf = new ObjectStore(new DummyReplicaManager(), new DummyLocationManager(), 10);

        HashMap<String, GameField> gameObject = new HashMap<>();
        gameObject.put("x", new GameInteger(0));
        gameObject.put("y", new GameInteger(1));

        GameObjectUUID id = asdf.create(gameObject, new HashSet<>(), null, null, 0);
        asdf.write(new LocalFieldChange(id, "x", new GameInteger(100), 0), id);
        assertEquals(new GameInteger(100), asdf.read(id, "x", 0));
        assertEquals(new GameInteger(1), asdf.read(id, "y", 0));
    }

    public void testBuffer() {
        ObjectStore asdf = new ObjectStore(new DummyReplicaManager(), new DummyLocationManager(), 10);

        HashMap<String, GameField> gameObject = new HashMap<>();
        gameObject.put("x", new GameInteger(0));
        gameObject.put("y", new GameInteger(1));

        GameObjectUUID id = asdf.create(gameObject, new HashSet<>(), null, null, 0);
        asdf.advanceBuffer();
        asdf.write(new LocalFieldChange(id, "x", new GameInteger(100), 0), id);
        assertEquals(new GameInteger(0), asdf.read(id, "x", 1));
        assertEquals(new GameInteger(1), asdf.read(id, "y", 1));
        assertEquals(new GameInteger(100), asdf.read(id, "x", 0));
        assertEquals(new GameInteger(1), asdf.read(id, "y", 0));
    }

    public void testSynchronize() {
        ObjectStore asdf = new ObjectStore(new DummyReplicaManager(), new DummyLocationManager(), 10);

        HashMap<String, GameField> gameObject = new HashMap<>();
        gameObject.put("x", new GameInteger(0));
        gameObject.put("y", new GameInteger(1));

        GameObjectUUID id = asdf.create(gameObject, new HashSet<>(), null, null, 0);
        Date changeTime = Date.from(Instant.now());
        asdf.advanceBuffer();
        asdf.receiveChange(new RemoteFieldChange(id, "x", new GameInteger(100), changeTime));
        asdf.synchronize();
        assertEquals(new GameInteger(100), asdf.read(id, "x", 1));
        assertEquals(new GameInteger(1), asdf.read(id, "y", 1));
        assertEquals(new GameInteger(0), asdf.read(id, "x", 0));
        assertEquals(new GameInteger(1), asdf.read(id, "y", 0));
    }

    public void testReplicas() {
        ObjectStore asdf = new ObjectStore(new DummyReplicaManager(), new DummyLocationManager(), 10);

        HashMap<String, GameField> gameObject = new HashMap<>();
        gameObject.put("x", new GameInteger(0));
        gameObject.put("y", new GameInteger(1));
        GameObjectUUID id = asdf.create(gameObject, new HashSet<>(), null, null, 0);

        HashMap<String, GameField> remoteObject = new HashMap<>();
        remoteObject.put("x", new GameInteger(40));
        remoteObject.put("y", new GameInteger(50));
        Date changeTime = Date.from(Instant.now());
        asdf.receiveChange(new RemoteAddReplicaChange(GameObjectUUID.randomUUID(), remoteObject, changeTime));

        asdf.advanceBuffer();

        Set<LocalChange> updates = asdf.synchronize();
        GameObjectUUID remote_id = (updates.iterator().next()).getTarget();

        assertEquals(new GameInteger(0), asdf.read(id, "x", 1));
        assertEquals(new GameInteger(1), asdf.read(id, "y", 1));
        assertEquals(new GameInteger(0), asdf.read(id, "x", 0));
        assertEquals(new GameInteger(1), asdf.read(id, "y", 0));

        assertEquals(new GameInteger(40), asdf.read(remote_id, "x", 1));
        assertEquals(new GameInteger(50), asdf.read(remote_id, "y", 1));
        try {
            assertEquals(new GameInteger(40), asdf.read(remote_id, "x", 0));
            assertEquals(new GameInteger(50), asdf.read(remote_id, "y", 0));
            fail();
        } catch (Exception E) {
        }

        asdf.receiveChange(new RemoteDeleteReplicaChange(remote_id, Date.from(Instant.now()), true));
        asdf.advanceBuffer();
        assertEquals(new GameInteger(40), asdf.read(remote_id, "x", 2));
        assertEquals(new GameInteger(50), asdf.read(remote_id, "y", 2));
        updates = asdf.synchronize();

        assertEquals(new GameInteger(0), asdf.read(id, "x", 2));
        assertEquals(new GameInteger(1), asdf.read(id, "y", 2));
        assertEquals(new GameInteger(0), asdf.read(id, "x", 1));
        assertEquals(new GameInteger(1), asdf.read(id, "y", 1));
        assertEquals(new GameInteger(0), asdf.read(id, "x", 0));
        assertEquals(new GameInteger(1), asdf.read(id, "y", 0));

        for (int i = 0; i < 3; i++) {
            try {
                asdf.read(remote_id, "x", 1);
                fail();
            } catch (Exception E) {
            }
        }
    }

    public void testDeep() {
        ObjectStore asdf = new ObjectStore(new DummyReplicaManager(), new DummyLocationManager(), 10);

        HashMap<String, GameField> gameObject = new HashMap<>();
        ArrayList<Integer> pos = new ArrayList<>(2);
        pos.add(0, 0);
        pos.add(1, 1);
        gameObject.put("pos", new GameList(pos));
        GameObjectUUID id = asdf.create(gameObject, new HashSet<>(), null, null, 0);

        assertEquals(0, ((GameList)asdf.read(id, "pos", 0)).value.get(0).intValue());
        assertEquals(1, ((GameList)asdf.read(id, "pos", 0)).value.get(1).intValue());


        asdf.advanceBuffer();

        assertEquals(0, ((GameList)asdf.read(id, "pos", 1)).value.get(0).intValue());
        assertEquals(1, ((GameList)asdf.read(id, "pos", 1)).value.get(1).intValue());
        assertEquals(0, ((GameList)asdf.read(id, "pos", 0)).value.get(0).intValue());
        assertEquals(1, ((GameList)asdf.read(id, "pos", 0)).value.get(1).intValue());

        ArrayList<Integer> pos2 = new ArrayList<>(2);
        pos2.add(0, 100);
        pos2.add(1, 100);

        asdf.write(new LocalFieldChange(id, "pos", new GameList(pos2), 0), id);

        assertEquals(100, ((GameList)asdf.read(id, "pos", 0)).value.get(0).intValue());
        assertEquals(100, ((GameList)asdf.read(id, "pos", 0)).value.get(1).intValue());
        assertEquals(0, ((GameList)asdf.read(id, "pos", 1)).value.get(0).intValue());
        assertEquals(1, ((GameList)asdf.read(id, "pos", 1)).value.get(1).intValue());
    }
}