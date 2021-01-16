package it.unimi.dsi.fastutil.objects;

import java.util.Collection;

public interface ReferenceCollection<K> extends Collection<K>, ObjectIterable<K> {
   ObjectIterator<K> iterator();
}
