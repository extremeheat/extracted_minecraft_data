package it.unimi.dsi.fastutil.objects;

public interface ObjectIterable<K> extends Iterable<K> {
   ObjectIterator<K> iterator();
}
