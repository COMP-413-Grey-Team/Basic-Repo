package test.edu.rice.rbox.Location;

import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.Location.locator.LocatorMainImpl;
import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LocatorMainImplTest extends TestCase {
    private  LocatorMainImpl impl;
    private  ServerUUID test_server_uuid;

    @BeforeAll
    private void init() {
        this.test_server_uuid = ServerUUID.randomUUID();
        //TODO: testing DB and collection
        impl = new LocatorMainImpl(test_server_uuid, new DummyStorageToLocation(), new DummyReplicationToLocation());
    }

    @Test
    //W.I.P. -- will involve mongo...
    public void testAdd() {
        assertTrue(true);
    }
}
