package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.Size64;

public interface LongBigList extends BigList<Long>, LongCollection, Size64, Comparable<BigList<? extends Long>> {
   LongBigListIterator iterator();

   LongBigListIterator listIterator();

   LongBigListIterator listIterator(long var1);

   LongBigList subList(long var1, long var3);

   void getElements(long var1, long[][] var3, long var4, long var6);

   void removeElements(long var1, long var3);

   void addElements(long var1, long[][] var3);

   void addElements(long var1, long[][] var3, long var4, long var6);

   void add(long var1, long var3);

   boolean addAll(long var1, LongCollection var3);

   boolean addAll(long var1, LongBigList var3);

   boolean addAll(LongBigList var1);

   long getLong(long var1);

   long removeLong(long var1);

   long set(long var1, long var3);

   long indexOf(long var1);

   long lastIndexOf(long var1);

   /** @deprecated */
   @Deprecated
   void add(long var1, Long var3);

   /** @deprecated */
   @Deprecated
   Long get(long var1);

   /** @deprecated */
   @Deprecated
   long indexOf(Object var1);

   /** @deprecated */
   @Deprecated
   long lastIndexOf(Object var1);

   /** @deprecated */
   @Deprecated
   Long remove(long var1);

   /** @deprecated */
   @Deprecated
   Long set(long var1, Long var3);
}
