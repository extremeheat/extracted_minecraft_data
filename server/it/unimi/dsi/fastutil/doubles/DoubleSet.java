package it.unimi.dsi.fastutil.doubles;

import java.util.Set;

public interface DoubleSet extends DoubleCollection, Set<Double> {
   DoubleIterator iterator();

   boolean remove(double var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return DoubleCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Double var1) {
      return DoubleCollection.super.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return DoubleCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean rem(double var1) {
      return this.remove(var1);
   }
}
