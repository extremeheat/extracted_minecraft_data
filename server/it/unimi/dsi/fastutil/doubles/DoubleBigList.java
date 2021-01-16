package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.Size64;

public interface DoubleBigList extends BigList<Double>, DoubleCollection, Size64, Comparable<BigList<? extends Double>> {
   DoubleBigListIterator iterator();

   DoubleBigListIterator listIterator();

   DoubleBigListIterator listIterator(long var1);

   DoubleBigList subList(long var1, long var3);

   void getElements(long var1, double[][] var3, long var4, long var6);

   void removeElements(long var1, long var3);

   void addElements(long var1, double[][] var3);

   void addElements(long var1, double[][] var3, long var4, long var6);

   void add(long var1, double var3);

   boolean addAll(long var1, DoubleCollection var3);

   boolean addAll(long var1, DoubleBigList var3);

   boolean addAll(DoubleBigList var1);

   double getDouble(long var1);

   double removeDouble(long var1);

   double set(long var1, double var3);

   long indexOf(double var1);

   long lastIndexOf(double var1);

   /** @deprecated */
   @Deprecated
   void add(long var1, Double var3);

   /** @deprecated */
   @Deprecated
   Double get(long var1);

   /** @deprecated */
   @Deprecated
   long indexOf(Object var1);

   /** @deprecated */
   @Deprecated
   long lastIndexOf(Object var1);

   /** @deprecated */
   @Deprecated
   Double remove(long var1);

   /** @deprecated */
   @Deprecated
   Double set(long var1, Double var3);
}
