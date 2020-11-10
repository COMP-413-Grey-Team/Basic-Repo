package edu.rice.rbox.Game.Server;

import edu.rice.rbox.Common.GameField;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class GameFieldSet<T extends GameField> implements Set<T>, GameField {

  private HashSet<T> set;

  public GameFieldSet(Set<T> set) {
    this.set = new HashSet<>(set);
  }

  @Override
  public GameField copy() {
    return new GameFieldSet<>(set.stream().map(GameField::copy).collect(Collectors.toSet()));
  }

  @Override
  public int size() {
    return set.size();
  }

  @Override
  public boolean isEmpty() {
    return set.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return set.contains(o);
  }

  @Override
  public Iterator<T> iterator() {
    return set.iterator();
  }

  @Override
  public Object[] toArray() {
    return set.toArray();
  }

  @Override
  public <T1> T1[] toArray(T1[] a) {
    return set.toArray(a);
  }

  @Override
  public boolean add(T t) {
    return set.add(t);
  }

  @Override
  public boolean remove(Object o) {
    return set.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return set.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    return set.addAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return set.retainAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return set.removeAll(c);
  }

  @Override
  public void clear() {
    set.clear();
  }

}
