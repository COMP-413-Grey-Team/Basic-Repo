package edu.rice.comp413fall2020grey.Location.registrar;


import java.util.List;
import java.util.UUID;

public interface ConnectionsManager {

    String getAddressByServerUUID(UUID serverUUID);

    List<String> getClientsByServerUUID(UUID serverUUID);
    
}
