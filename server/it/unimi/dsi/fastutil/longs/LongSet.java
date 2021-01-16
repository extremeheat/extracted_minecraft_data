package it.unimi.dsi.fastutil.longs;

import java.util.Set;

public interface LongSet extends LongCollection, Set<Long> {
   LongIterator iterator();

   boolean remove(long var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return LongCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Long var1) {
      return LongCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return LongCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean rem(long var1) {
      return this.remove(var1);
   }
}
