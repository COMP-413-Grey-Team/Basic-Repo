package edu.rice.comp413fall2020grey.Common;

import edu.rice.comp413fall2020grey.Common.metadata.ObjectIntMetadataField;
import edu.rice.comp413fall2020grey.Common.metadata.ObjectStringMetadataField;

import java.util.UUID;

public class ExamplePersonObjectMetadata implements GameObjectMetadata {
    public static ObjectStringMetadataField typeField = new ObjectStringMetadataField("person");
    public static ObjectStringMetadataField nameField = new ObjectStringMetadataField("name");
    public static ObjectIntMetadataField x_posField = new ObjectIntMetadataField("x_pos");
    public static ObjectIntMetadataField y_posField = new ObjectIntMetadataField("y_pos");

    private final String name;
    private final int x_pos;
    private final int y_pos;

    public ExamplePersonObjectMetadata(String name, int x_pos, int y_pos) {
        this.name = name;
        this.x_pos = x_pos;
        this.y_pos = y_pos;
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public Class<?> getUnderlyingType() {
        return null;
    }

    public String toJson() {
        //TODO: GSON? in object using @exclude?
        return null;
    }
}
