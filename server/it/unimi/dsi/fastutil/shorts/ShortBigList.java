package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.Size64;

public interface ShortBigList extends BigList<Short>, ShortCollection, Size64, Comparable<BigList<? extends Short>> {
   ShortBigListIterator iterator();

   ShortBigListIterator listIterator();

   ShortBigListIterator listIterator(long var1);

   ShortBigList subList(long var1, long var3);

   void getElements(long var1, short[][] var3, long var4, long var6);

   void removeElements(long var1, long var3);

   void addElements(long var1, short[][] var3);

   void addElements(long var1, short[][] var3, long var4, long var6);

   void add(long var1, short var3);

   boolean addAll(long var1, ShortCollection var3);

   boolean addAll(long var1, ShortBigList var3);

   boolean addAll(ShortBigList var1);

   short getShort(long var1);

   short removeShort(long var1);

   short set(long var1, short var3);

   long indexOf(short var1);

   long lastIndexOf(short var1);

   /** @deprecated */
   @Deprecated
   void add(long var1, Short var3);

   /** @deprecated */
   @Deprecated
   Short get(long var1);

   /** @deprecated */
   @Deprecated
   long indexOf(Object var1);

   /** @deprecated */
   @Deprecated
   long lastIndexOf(Object var1);

   /** @deprecated */
   @Deprecated
   Short remove(long var1);

   /** @deprecated */
   @Deprecated
   Short set(long var1, Short var3);
}
