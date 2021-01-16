package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.Size64;

public interface FloatBigList extends BigList<Float>, FloatCollection, Size64, Comparable<BigList<? extends Float>> {
   FloatBigListIterator iterator();

   FloatBigListIterator listIterator();

   FloatBigListIterator listIterator(long var1);

   FloatBigList subList(long var1, long var3);

   void getElements(long var1, float[][] var3, long var4, long var6);

   void removeElements(long var1, long var3);

   void addElements(long var1, float[][] var3);

   void addElements(long var1, float[][] var3, long var4, long var6);

   void add(long var1, float var3);

   boolean addAll(long var1, FloatCollection var3);

   boolean addAll(long var1, FloatBigList var3);

   boolean addAll(FloatBigList var1);

   float getFloat(long var1);

   float removeFloat(long var1);

   float set(long var1, float var3);

   long indexOf(float var1);

   long lastIndexOf(float var1);

   /** @deprecated */
   @Deprecated
   void add(long var1, Float var3);

   /** @deprecated */
   @Deprecated
   Float get(long var1);

   /** @deprecated */
   @Deprecated
   long indexOf(Object var1);

   /** @deprecated */
   @Deprecated
   long lastIndexOf(Object var1);

   /** @deprecated */
   @Deprecated
   Float remove(long var1);

   /** @deprecated */
   @Deprecated
   Float set(long var1, Float var3);
}
