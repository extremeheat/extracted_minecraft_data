package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.Collection;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public interface ShortCollection extends Collection<Short>, ShortIterable {
   ShortIterator iterator();

   boolean add(short var1);

   boolean contains(short var1);

   boolean rem(short var1);

   /** @deprecated */
   @Deprecated
   default boolean add(Short var1) {
      return this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return var1 == null ? false : this.contains((Short)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return var1 == null ? false : this.rem((Short)var1);
   }

   short[] toShortArray();

   /** @deprecated */
   @Deprecated
   short[] toShortArray(short[] var1);

   short[] toArray(short[] var1);

   boolean addAll(ShortCollection var1);

   boolean containsAll(ShortCollection var1);

   boolean removeAll(ShortCollection var1);

   /** @deprecated */
   @Deprecated
   default boolean removeIf(Predicate<? super Short> var1) {
      return this.removeIf((var1x) -> {
         return var1.test(SafeMath.safeIntToShort(var1x));
      });
   }

   default boolean removeIf(IntPredicate var1) {
      Objects.requireNonNull(var1);
      boolean var2 = false;
      ShortIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (var1.test(var3.nextShort())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   boolean retainAll(ShortCollection var1);
}
