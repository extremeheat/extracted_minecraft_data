package it.unimi.dsi.fastutil.ints;

import java.util.Collection;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public interface IntCollection extends Collection<Integer>, IntIterable {
   IntIterator iterator();

   boolean add(int var1);

   boolean contains(int var1);

   boolean rem(int var1);

   /** @deprecated */
   @Deprecated
   default boolean add(Integer var1) {
      return this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return var1 == null ? false : this.contains((Integer)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return var1 == null ? false : this.rem((Integer)var1);
   }

   int[] toIntArray();

   /** @deprecated */
   @Deprecated
   int[] toIntArray(int[] var1);

   int[] toArray(int[] var1);

   boolean addAll(IntCollection var1);

   boolean containsAll(IntCollection var1);

   boolean removeAll(IntCollection var1);

   /** @deprecated */
   @Deprecated
   default boolean removeIf(Predicate<? super Integer> var1) {
      return this.removeIf((var1x) -> {
         return var1.test(var1x);
      });
   }

   default boolean removeIf(IntPredicate var1) {
      Objects.requireNonNull(var1);
      boolean var2 = false;
      IntIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (var1.test(var3.nextInt())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   boolean retainAll(IntCollection var1);
}
