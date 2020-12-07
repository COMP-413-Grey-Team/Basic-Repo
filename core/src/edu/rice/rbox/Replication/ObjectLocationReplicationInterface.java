package edu.rice.rbox.Replication;

import java.util.List;

public interface ObjectLocationReplicationInterface {
    void handleQueryResult(List<HolderInfo> interestedObjects);

}
