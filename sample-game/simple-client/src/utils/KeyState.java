package utils;

import java.util.EnumSet;
import java.util.HashSet;

public class KeyState {

  public enum Key {
    W, S, A, D
  }

  private HashSet<Key> state = new HashSet<>();

  public void tapped(Key key) {
    state.add(key);
  }

  public void released(Key key) {
    state.remove(key);
  }

  public double verticalMultiplier() {
    final boolean w = state.contains(Key.W);
    final boolean s = state.contains(Key.S);
    if (w == s) {
      return 0;
    }

    int horizontalComponent = w ? -1 : 1;

    final boolean a = state.contains(Key.A);
    final boolean d = state.contains(Key.D);
    if (a == d) {
      return horizontalComponent;
    } else {
      return horizontalComponent / Math.sqrt(2);
    }
  }

  public double horizontalMultiplier() {
    final boolean a = state.contains(Key.A);
    final boolean d = state.contains(Key.D);
    if (a == d) {
      return 0;
    }

    int horizontalComponent = a ? -1 : 1;

    final boolean w = state.contains(Key.W);
    final boolean s = state.contains(Key.S);
    if (w == s) {
      return horizontalComponent;
    } else {
      return horizontalComponent / Math.sqrt(2);
    }
  }

}
