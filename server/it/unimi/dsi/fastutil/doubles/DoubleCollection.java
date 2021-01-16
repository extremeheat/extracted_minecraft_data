package it.unimi.dsi.fastutil.doubles;

import java.util.Collection;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

public interface DoubleCollection extends Collection<Double>, DoubleIterable {
   DoubleIterator iterator();

   boolean add(double var1);

   boolean contains(double var1);

   boolean rem(double var1);

   /** @deprecated */
   @Deprecated
   default boolean add(Double var1) {
      return this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return var1 == null ? false : this.contains((Double)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return var1 == null ? false : this.rem((Double)var1);
   }

   double[] toDoubleArray();

   /** @deprecated */
   @Deprecated
   double[] toDoubleArray(double[] var1);

   double[] toArray(double[] var1);

   boolean addAll(DoubleCollection var1);

   boolean containsAll(DoubleCollection var1);

   boolean removeAll(DoubleCollection var1);

   /** @deprecated */
   @Deprecated
   default boolean removeIf(Predicate<? super Double> var1) {
      return this.removeIf((var1x) -> {
         return var1.test(var1x);
      });
   }

   default boolean removeIf(DoublePredicate var1) {
      Objects.requireNonNull(var1);
      boolean var2 = false;
      DoubleIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (var1.test(var3.nextDouble())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   boolean retainAll(DoubleCollection var1);
}
