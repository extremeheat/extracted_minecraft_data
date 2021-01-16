package it.unimi.dsi.fastutil.objects;

import java.util.SortedSet;

public interface ObjectSortedSet<K> extends ObjectSet<K>, SortedSet<K>, ObjectBidirectionalIterable<K> {
   ObjectBidirectionalIterator<K> iterator(K var1);

   ObjectBidirectionalIterator<K> iterator();

   ObjectSortedSet<K> subSet(K var1, K var2);

   ObjectSortedSet<K> headSet(K var1);

   ObjectSortedSet<K> tailSet(K var1);
}
