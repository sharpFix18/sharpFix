package org.sonatype.sisu.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryStore<T> extends StoreNotifier<T> implements Store<T> {

  private final Map<Object, T> items;

  public MemoryStore() {
    items = Collections.synchronizedMap(new LinkedHashMap<Object, T>());
  }

  public Iterator<T> iterator() {
    return get().iterator();
  }

  public Store<T> add(final T... items) {
    // TODO synchronize contains/remove/add
    for (final T item : items) {
      final Object identity = identify(item);
      if (identity != null) {
        final T replaced = this.items.put(identity, item);
        if (replaced != null) {
          notifyUpdated(replaced, item);
        } else {
          notifyAdded(item);
        }
      }
    }
    return this;
  }
  
  public boolean remove(final T... items) {
    for (final T item : items) {
      final Object identity = identify(item);
      if (identity != null) {
        final T removed = this.items.remove(identity);
        if (removed != null) {
          notifyRemoved(removed);
          return true;
        }
      }
    }
    return false;
  }

  public Collection<T> get() {
    return Collections.unmodifiableCollection(items.values());
  }

  public T get(final Object key) {
    return items.get(key);
  }

  protected Object identify(final T item) {
    return item;
  }

  @Override
  protected Collection<T> snapshot() {
    final Collection<T> snapshot = new ArrayList<T>();
    snapshot.addAll(get());
    return snapshot;
  }
}
