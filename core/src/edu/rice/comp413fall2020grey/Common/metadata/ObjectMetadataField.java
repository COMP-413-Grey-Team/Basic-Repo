package edu.rice.comp413fall2020grey.Common.metadata;

public abstract class ObjectMetadataField {
    private final String fieldName;

    public ObjectMetadataField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return this.fieldName;
    }
}
