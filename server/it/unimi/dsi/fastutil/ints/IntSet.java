package it.unimi.dsi.fastutil.ints;

import java.util.Set;

public interface IntSet extends IntCollection, Set<Integer> {
   IntIterator iterator();

   boolean remove(int var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return IntCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Integer var1) {
      return IntCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return IntCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean rem(int var1) {
      return this.remove(var1);
   }
}
