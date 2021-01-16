package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.Size64;

public interface BooleanBigList extends BigList<Boolean>, BooleanCollection, Size64, Comparable<BigList<? extends Boolean>> {
   BooleanBigListIterator iterator();

   BooleanBigListIterator listIterator();

   BooleanBigListIterator listIterator(long var1);

   BooleanBigList subList(long var1, long var3);

   void getElements(long var1, boolean[][] var3, long var4, long var6);

   void removeElements(long var1, long var3);

   void addElements(long var1, boolean[][] var3);

   void addElements(long var1, boolean[][] var3, long var4, long var6);

   void add(long var1, boolean var3);

   boolean addAll(long var1, BooleanCollection var3);

   boolean addAll(long var1, BooleanBigList var3);

   boolean addAll(BooleanBigList var1);

   boolean getBoolean(long var1);

   boolean removeBoolean(long var1);

   boolean set(long var1, boolean var3);

   long indexOf(boolean var1);

   long lastIndexOf(boolean var1);

   /** @deprecated */
   @Deprecated
   void add(long var1, Boolean var3);

   /** @deprecated */
   @Deprecated
   Boolean get(long var1);

   /** @deprecated */
   @Deprecated
   long indexOf(Object var1);

   /** @deprecated */
   @Deprecated
   long lastIndexOf(Object var1);

   /** @deprecated */
   @Deprecated
   Boolean remove(long var1);

   /** @deprecated */
   @Deprecated
   Boolean set(long var1, Boolean var3);
}
