package edu.rice.rbox.Common.GameField;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GameFieldMap<Key extends GameField, Value extends GameField> implements Map<Key, Value>, GameField {

  private HashMap<Key, Value> map;

  public GameFieldMap(Map<Key, Value> map) { this.map = new HashMap<>((Map<Key, Value>)map.entrySet().stream().collect(
      Collectors.toMap(
          entry -> entry.getKey().copy(),
          entry -> entry.getValue().copy()
      ))); }

  @Override
  public GameField copy() {
    return new GameFieldMap<>(map);
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  public Value get(Object key) {
    return map.get(key);
  }

  @Override
  public Value put(Key key, Value value) {
    return map.put(key, value);
  }

  @Override
  public Value remove(Object key) {
    return map.remove(key);
  }

  @Override
  public void putAll(Map<? extends Key, ? extends Value> m) {
    map.putAll(m);
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public Set<Key> keySet() {
    return map.keySet();
  }

  @Override
  public Collection<Value> values() {
    return map.values();
  }

  @Override
  public Set<Entry<Key, Value>> entrySet() {
    return map.entrySet();
  }
}
