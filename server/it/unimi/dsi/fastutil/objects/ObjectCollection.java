package it.unimi.dsi.fastutil.objects;

import java.util.Collection;

public interface ObjectCollection<K> extends Collection<K>, ObjectIterable<K> {
   ObjectIterator<K> iterator();
}
