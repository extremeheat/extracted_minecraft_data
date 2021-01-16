package it.unimi.dsi.fastutil.objects;

import java.util.Set;

public interface ObjectSet<K> extends ObjectCollection<K>, Set<K> {
   ObjectIterator<K> iterator();
}
