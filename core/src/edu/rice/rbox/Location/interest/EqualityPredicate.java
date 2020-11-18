package edu.rice.rbox.Location.interest;

public abstract class EqualityPredicate<T> extends InterestPredicate {
    final T value;

    public EqualityPredicate(String field, T value, Boolean isRelative) {
        super(field, isRelative);
        this.value = value;
    }

}
