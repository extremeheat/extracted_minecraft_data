package it.unimi.dsi.fastutil.doubles;

import java.util.List;

public interface DoubleList extends List<Double>, Comparable<List<? extends Double>>, DoubleCollection {
   DoubleListIterator iterator();

   DoubleListIterator listIterator();

   DoubleListIterator listIterator(int var1);

   DoubleList subList(int var1, int var2);

   void size(int var1);

   void getElements(int var1, double[] var2, int var3, int var4);

   void removeElements(int var1, int var2);

   void addElements(int var1, double[] var2);

   void addElements(int var1, double[] var2, int var3, int var4);

   boolean add(double var1);

   void add(int var1, double var2);

   /** @deprecated */
   @Deprecated
   default void add(int var1, Double var2) {
      this.add(var1, var2);
   }

   boolean addAll(int var1, DoubleCollection var2);

   boolean addAll(int var1, DoubleList var2);

   boolean addAll(DoubleList var1);

   double set(int var1, double var2);

   double getDouble(int var1);

   int indexOf(double var1);

   int lastIndexOf(double var1);

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return DoubleCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double get(int var1) {
      return this.getDouble(var1);
   }

   /** @deprecated */
   @Deprecated
   default int indexOf(Object var1) {
      return this.indexOf((Double)var1);
   }

   /** @deprecated */
   @Deprecated
   default int lastIndexOf(Object var1) {
      return this.lastIndexOf((Double)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Double var1) {
      return this.add(var1);
   }

   double removeDouble(int var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return DoubleCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double remove(int var1) {
      return this.removeDouble(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double set(int var1, Double var2) {
      return this.set(var1, var2);
   }
}
