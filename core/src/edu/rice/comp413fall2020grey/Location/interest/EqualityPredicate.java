package edu.rice.comp413fall2020grey.Location.interest;

import edu.rice.comp413fall2020grey.Common.metadata.ObjectMetadataField;

public class EqualityPredicate<T> implements InterestPredicate {
    final String fieldName;
    final T value;


    public EqualityPredicate(ObjectMetadataField field, T value) {
        this.fieldName = field.getFieldName();
        this.value = value;
    }

    @Override
    public String toMongoQuery() {
        return String.format("eq(%s, %s)", fieldName, value);
    }
}
