package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.Size64;

public interface IntBigList extends BigList<Integer>, IntCollection, Size64, Comparable<BigList<? extends Integer>> {
   IntBigListIterator iterator();

   IntBigListIterator listIterator();

   IntBigListIterator listIterator(long var1);

   IntBigList subList(long var1, long var3);

   void getElements(long var1, int[][] var3, long var4, long var6);

   void removeElements(long var1, long var3);

   void addElements(long var1, int[][] var3);

   void addElements(long var1, int[][] var3, long var4, long var6);

   void add(long var1, int var3);

   boolean addAll(long var1, IntCollection var3);

   boolean addAll(long var1, IntBigList var3);

   boolean addAll(IntBigList var1);

   int getInt(long var1);

   int removeInt(long var1);

   int set(long var1, int var3);

   long indexOf(int var1);

   long lastIndexOf(int var1);

   /** @deprecated */
   @Deprecated
   void add(long var1, Integer var3);

   /** @deprecated */
   @Deprecated
   Integer get(long var1);

   /** @deprecated */
   @Deprecated
   long indexOf(Object var1);

   /** @deprecated */
   @Deprecated
   long lastIndexOf(Object var1);

   /** @deprecated */
   @Deprecated
   Integer remove(long var1);

   /** @deprecated */
   @Deprecated
   Integer set(long var1, Integer var3);
}
