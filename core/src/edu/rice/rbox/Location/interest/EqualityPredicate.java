package edu.rice.rbox.Location.interest;

public abstract class EqualityPredicate<T> implements InterestPredicate {
    final T value;
    final String field;
    final Boolean isRelative;

    public EqualityPredicate(String field, T value, Boolean isRelative) {
        this.value = value;
        this.field = field;
        this.isRelative = isRelative;
    }

}
