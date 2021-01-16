package it.unimi.dsi.fastutil.floats;

import java.util.List;

public interface FloatList extends List<Float>, Comparable<List<? extends Float>>, FloatCollection {
   FloatListIterator iterator();

   FloatListIterator listIterator();

   FloatListIterator listIterator(int var1);

   FloatList subList(int var1, int var2);

   void size(int var1);

   void getElements(int var1, float[] var2, int var3, int var4);

   void removeElements(int var1, int var2);

   void addElements(int var1, float[] var2);

   void addElements(int var1, float[] var2, int var3, int var4);

   boolean add(float var1);

   void add(int var1, float var2);

   /** @deprecated */
   @Deprecated
   default void add(int var1, Float var2) {
      this.add(var1, var2);
   }

   boolean addAll(int var1, FloatCollection var2);

   boolean addAll(int var1, FloatList var2);

   boolean addAll(FloatList var1);

   float set(int var1, float var2);

   float getFloat(int var1);

   int indexOf(float var1);

   int lastIndexOf(float var1);

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return FloatCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float get(int var1) {
      return this.getFloat(var1);
   }

   /** @deprecated */
   @Deprecated
   default int indexOf(Object var1) {
      return this.indexOf((Float)var1);
   }

   /** @deprecated */
   @Deprecated
   default int lastIndexOf(Object var1) {
      return this.lastIndexOf((Float)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Float var1) {
      return this.add(var1);
   }

   float removeFloat(int var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return FloatCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float remove(int var1) {
      return this.removeFloat(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float set(int var1, Float var2) {
      return this.set(var1, var2);
   }
}
