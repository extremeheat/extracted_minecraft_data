package it.unimi.dsi.fastutil.booleans;

import java.util.List;

public interface BooleanList extends List<Boolean>, Comparable<List<? extends Boolean>>, BooleanCollection {
   BooleanListIterator iterator();

   BooleanListIterator listIterator();

   BooleanListIterator listIterator(int var1);

   BooleanList subList(int var1, int var2);

   void size(int var1);

   void getElements(int var1, boolean[] var2, int var3, int var4);

   void removeElements(int var1, int var2);

   void addElements(int var1, boolean[] var2);

   void addElements(int var1, boolean[] var2, int var3, int var4);

   boolean add(boolean var1);

   void add(int var1, boolean var2);

   /** @deprecated */
   @Deprecated
   default void add(int var1, Boolean var2) {
      this.add(var1, var2);
   }

   boolean addAll(int var1, BooleanCollection var2);

   boolean addAll(int var1, BooleanList var2);

   boolean addAll(BooleanList var1);

   boolean set(int var1, boolean var2);

   boolean getBoolean(int var1);

   int indexOf(boolean var1);

   int lastIndexOf(boolean var1);

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return BooleanCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default Boolean get(int var1) {
      return this.getBoolean(var1);
   }

   /** @deprecated */
   @Deprecated
   default int indexOf(Object var1) {
      return this.indexOf((Boolean)var1);
   }

   /** @deprecated */
   @Deprecated
   default int lastIndexOf(Object var1) {
      return this.lastIndexOf((Boolean)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Boolean var1) {
      return this.add(var1);
   }

   boolean removeBoolean(int var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return BooleanCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default Boolean remove(int var1) {
      return this.removeBoolean(var1);
   }

   /** @deprecated */
   @Deprecated
   default Boolean set(int var1, Boolean var2) {
      return this.set(var1, var2);
   }
}
