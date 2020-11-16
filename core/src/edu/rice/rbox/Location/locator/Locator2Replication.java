package edu.rice.comp413fall2020grey.Location.locator;

import java.util.HashMap;
import java.util.UUID;

public interface Locator2Replication {
    void fetchObjectUpdates(HashMap<UUID, UUID> object_to_server);
}
