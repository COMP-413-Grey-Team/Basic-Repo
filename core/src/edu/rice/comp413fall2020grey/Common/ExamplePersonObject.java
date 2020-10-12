package edu.rice.comp413fall2020grey.Common;

import edu.rice.comp413fall2020grey.Common.metadata.ObjectStringMetadataField;
import edu.rice.comp413fall2020grey.Location.interest.EqualityPredicate;
import edu.rice.comp413fall2020grey.Location.interest.InterestPredicate;

public class ExamplePersonObject extends GameObject {

    private final String name;
    private final int x_pos;
    private final int y_pos;

    private final String random1;

    public ExamplePersonObject() {
    }


    @Override
    public GameObjectMetadata getMetadata() {
        return new ExamplePersonObjectMetadata(name, x_pos, y_pos);
    }

    public InterestPredicate getInterestPredicate() {
        return new EqualityPredicate<>(ExamplePersonObjectMetadata.nameField, "billy");
    }
}
