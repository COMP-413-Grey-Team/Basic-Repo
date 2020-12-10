package edu.rice.rbox.Location;

import edu.rice.rbox.Replication.HolderInfo;
import edu.rice.rbox.Replication.ObjectLocationReplicationInterface;

import java.util.List;

public class DummyReplicationToLocation implements ObjectLocationReplicationInterface {
    @Override
    public void handleQueryResult(List<HolderInfo> interestedObjects) {

    }
}
