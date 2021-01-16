package it.unimi.dsi.fastutil.shorts;

import java.util.Set;

public interface ShortSet extends ShortCollection, Set<Short> {
   ShortIterator iterator();

   boolean remove(short var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return ShortCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Short var1) {
      return ShortCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return ShortCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean rem(short var1) {
      return this.remove(var1);
   }
}
