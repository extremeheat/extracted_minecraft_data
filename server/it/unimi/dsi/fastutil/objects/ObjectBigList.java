package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.Size64;

public interface ObjectBigList<K> extends BigList<K>, ObjectCollection<K>, Size64, Comparable<BigList<? extends K>> {
   ObjectBigListIterator<K> iterator();

   ObjectBigListIterator<K> listIterator();

   ObjectBigListIterator<K> listIterator(long var1);

   ObjectBigList<K> subList(long var1, long var3);

   void getElements(long var1, Object[][] var3, long var4, long var6);

   void removeElements(long var1, long var3);

   void addElements(long var1, K[][] var3);

   void addElements(long var1, K[][] var3, long var4, long var6);
}
