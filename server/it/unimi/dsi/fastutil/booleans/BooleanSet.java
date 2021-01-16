package it.unimi.dsi.fastutil.booleans;

import java.util.Set;

public interface BooleanSet extends BooleanCollection, Set<Boolean> {
   BooleanIterator iterator();

   boolean remove(boolean var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return BooleanCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Boolean var1) {
      return BooleanCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return BooleanCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean rem(boolean var1) {
      return this.remove(var1);
   }
}
