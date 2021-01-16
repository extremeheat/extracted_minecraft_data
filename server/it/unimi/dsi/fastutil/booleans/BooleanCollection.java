package it.unimi.dsi.fastutil.booleans;

import java.util.Collection;

public interface BooleanCollection extends Collection<Boolean>, BooleanIterable {
   BooleanIterator iterator();

   boolean add(boolean var1);

   boolean contains(boolean var1);

   boolean rem(boolean var1);

   /** @deprecated */
   @Deprecated
   default boolean add(Boolean var1) {
      return this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return var1 == null ? false : this.contains((Boolean)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return var1 == null ? false : this.rem((Boolean)var1);
   }

   boolean[] toBooleanArray();

   /** @deprecated */
   @Deprecated
   boolean[] toBooleanArray(boolean[] var1);

   boolean[] toArray(boolean[] var1);

   boolean addAll(BooleanCollection var1);

   boolean containsAll(BooleanCollection var1);

   boolean removeAll(BooleanCollection var1);

   boolean retainAll(BooleanCollection var1);
}
