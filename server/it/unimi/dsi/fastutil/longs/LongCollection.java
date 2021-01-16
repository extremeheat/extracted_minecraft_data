package it.unimi.dsi.fastutil.longs;

import java.util.Collection;
import java.util.Objects;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

public interface LongCollection extends Collection<Long>, LongIterable {
   LongIterator iterator();

   boolean add(long var1);

   boolean contains(long var1);

   boolean rem(long var1);

   /** @deprecated */
   @Deprecated
   default boolean add(Long var1) {
      return this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return var1 == null ? false : this.contains((Long)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return var1 == null ? false : this.rem((Long)var1);
   }

   long[] toLongArray();

   /** @deprecated */
   @Deprecated
   long[] toLongArray(long[] var1);

   long[] toArray(long[] var1);

   boolean addAll(LongCollection var1);

   boolean containsAll(LongCollection var1);

   boolean removeAll(LongCollection var1);

   /** @deprecated */
   @Deprecated
   default boolean removeIf(Predicate<? super Long> var1) {
      return this.removeIf((var1x) -> {
         return var1.test(var1x);
      });
   }

   default boolean removeIf(LongPredicate var1) {
      Objects.requireNonNull(var1);
      boolean var2 = false;
      LongIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (var1.test(var3.nextLong())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   boolean retainAll(LongCollection var1);
}
