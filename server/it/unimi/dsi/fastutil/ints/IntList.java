package it.unimi.dsi.fastutil.ints;

import java.util.List;

public interface IntList extends List<Integer>, Comparable<List<? extends Integer>>, IntCollection {
   IntListIterator iterator();

   IntListIterator listIterator();

   IntListIterator listIterator(int var1);

   IntList subList(int var1, int var2);

   void size(int var1);

   void getElements(int var1, int[] var2, int var3, int var4);

   void removeElements(int var1, int var2);

   void addElements(int var1, int[] var2);

   void addElements(int var1, int[] var2, int var3, int var4);

   boolean add(int var1);

   void add(int var1, int var2);

   /** @deprecated */
   @Deprecated
   default void add(int var1, Integer var2) {
      this.add(var1, var2);
   }

   boolean addAll(int var1, IntCollection var2);

   boolean addAll(int var1, IntList var2);

   boolean addAll(IntList var1);

   int set(int var1, int var2);

   int getInt(int var1);

   int indexOf(int var1);

   int lastIndexOf(int var1);

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return IntCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default Integer get(int var1) {
      return this.getInt(var1);
   }

   /** @deprecated */
   @Deprecated
   default int indexOf(Object var1) {
      return this.indexOf((Integer)var1);
   }

   /** @deprecated */
   @Deprecated
   default int lastIndexOf(Object var1) {
      return this.lastIndexOf((Integer)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Integer var1) {
      return this.add(var1);
   }

   int removeInt(int var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return IntCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default Integer remove(int var1) {
      return this.removeInt(var1);
   }

   /** @deprecated */
   @Deprecated
   default Integer set(int var1, Integer var2) {
      return this.set(var1, var2);
   }
}
