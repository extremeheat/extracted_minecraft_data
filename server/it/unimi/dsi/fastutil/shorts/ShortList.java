package it.unimi.dsi.fastutil.shorts;

import java.util.List;

public interface ShortList extends List<Short>, Comparable<List<? extends Short>>, ShortCollection {
   ShortListIterator iterator();

   ShortListIterator listIterator();

   ShortListIterator listIterator(int var1);

   ShortList subList(int var1, int var2);

   void size(int var1);

   void getElements(int var1, short[] var2, int var3, int var4);

   void removeElements(int var1, int var2);

   void addElements(int var1, short[] var2);

   void addElements(int var1, short[] var2, int var3, int var4);

   boolean add(short var1);

   void add(int var1, short var2);

   /** @deprecated */
   @Deprecated
   default void add(int var1, Short var2) {
      this.add(var1, var2);
   }

   boolean addAll(int var1, ShortCollection var2);

   boolean addAll(int var1, ShortList var2);

   boolean addAll(ShortList var1);

   short set(int var1, short var2);

   short getShort(int var1);

   int indexOf(short var1);

   int lastIndexOf(short var1);

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return ShortCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short get(int var1) {
      return this.getShort(var1);
   }

   /** @deprecated */
   @Deprecated
   default int indexOf(Object var1) {
      return this.indexOf((Short)var1);
   }

   /** @deprecated */
   @Deprecated
   default int lastIndexOf(Object var1) {
      return this.lastIndexOf((Short)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Short var1) {
      return this.add(var1);
   }

   short removeShort(int var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return ShortCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short remove(int var1) {
      return this.removeShort(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short set(int var1, Short var2) {
      return this.set(var1, var2);
   }
}
