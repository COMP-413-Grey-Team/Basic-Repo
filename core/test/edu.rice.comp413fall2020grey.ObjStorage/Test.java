package edu.rice.rbox.ObjStorage;
import edu.rice.rbox.Common.Change.*;
import edu.rice.rbox.Common.GameObjectUUID;
import junit.framework.TestCase;

import javax.management.timer.Timer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Thread.sleep;

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

    public void testWrite() throws IOException {
        ObjectStore asdf = new ObjectStore(new DummyReplicaManager(), 10);

        HashMap<String, Serializable> gameObject = new HashMap<>();
        gameObject.put("x", 0);
        gameObject.put("y", 1);

        GameObjectUUID id = asdf.create(gameObject, new HashSet<>(), null, 0);
        asdf.write(new LocalFieldChange(id, "x", 100, 0), id);
        assertEquals(100, asdf.read(id, "x", 0));
        assertEquals(1, asdf.read(id, "y", 0));
    }

    public void testBuffer() throws IOException {
        ObjectStore asdf = new ObjectStore(new DummyReplicaManager(), 10);

        HashMap<String, Serializable> gameObject = new HashMap<>();
        gameObject.put("x", 0);
        gameObject.put("y", 1);

        GameObjectUUID id = asdf.create(gameObject, new HashSet<>(), null, 0);
        asdf.advanceBuffer();
        asdf.write(new LocalFieldChange(id, "x", 100, 0), id);
        assertEquals(0, asdf.read(id, "x", 1));
        assertEquals(1, asdf.read(id, "y", 1));
        assertEquals(100, asdf.read(id, "x", 0));
        assertEquals(1, asdf.read(id, "y", 0));
    }

    public void testSynchronize() throws IOException, InterruptedException {
        ObjectStore asdf = new ObjectStore(new DummyReplicaManager(), 10);

        HashMap<String, Serializable> gameObject = new HashMap<>();
        gameObject.put("x", 0);
        gameObject.put("y", 1);

        GameObjectUUID id = asdf.create(gameObject, new HashSet<>(), null, 0);
        Date changeTime = Date.from(Instant.now());
        asdf.advanceBuffer();
        asdf.receiveChange(new RemoteFieldChange(id, "x", 100, changeTime));
        asdf.synchronize();
        assertEquals(100, asdf.read(id, "x", 1));
        assertEquals(1, asdf.read(id, "y", 1));
        assertEquals(0, asdf.read(id, "x", 0));
        assertEquals(1, asdf.read(id, "y", 0));
    }

    public void testReplicas() throws IOException {
        ObjectStore asdf = new ObjectStore(new DummyReplicaManager(), 10);

        HashMap<String, Serializable> gameObject = new HashMap<>();
        gameObject.put("x", 0);
        gameObject.put("y", 1);
        GameObjectUUID id = asdf.create(gameObject, new HashSet<>(), null, 0);

        HashMap<String, Serializable> remoteObject = new HashMap<>();
        remoteObject.put("x", 40);
        remoteObject.put("y", 50);
        Date changeTime = Date.from(Instant.now());
        asdf.receiveChange(new RemoteAddReplicaChange(GameObjectUUID.randomUUID(), remoteObject, changeTime));

        asdf.advanceBuffer();

        Set<LocalChange> updates = asdf.synchronize();
        GameObjectUUID remote_id = (updates.iterator().next()).getTarget();

        assertEquals(0, asdf.read(id, "x", 1));
        assertEquals(1, asdf.read(id, "y", 1));
        assertEquals(0, asdf.read(id, "x", 0));
        assertEquals(1, asdf.read(id, "y", 0));

        assertEquals(40, asdf.read(remote_id, "x", 1));
        assertEquals(50, asdf.read(remote_id, "y", 1));
    }
}