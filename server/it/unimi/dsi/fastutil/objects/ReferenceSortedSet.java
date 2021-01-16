package it.unimi.dsi.fastutil.objects;

import java.util.SortedSet;

public interface ReferenceSortedSet<K> extends ReferenceSet<K>, SortedSet<K>, ObjectBidirectionalIterable<K> {
   ObjectBidirectionalIterator<K> iterator(K var1);

   ObjectBidirectionalIterator<K> iterator();

   ReferenceSortedSet<K> subSet(K var1, K var2);

   ReferenceSortedSet<K> headSet(K var1);

   ReferenceSortedSet<K> tailSet(K var1);
}
