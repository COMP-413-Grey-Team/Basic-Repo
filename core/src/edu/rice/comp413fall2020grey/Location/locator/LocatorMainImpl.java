package edu.rice.comp413fall2020grey.Location.locator;

import edu.rice.comp413fall2020grey.Location.interest.InterestPredicate;
import org.bson.conversions.Bson;

public class LocatorMainImpl implements LocatorMain {
    private String registrar_address = "";


    public LocatorMainImpl() {

    }

    private Boolean connectToRegistrar() {

        return true;
    }

    @Override
    public void updateInterest(Bson predicate) {

    }
}
