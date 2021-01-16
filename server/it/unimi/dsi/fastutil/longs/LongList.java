package it.unimi.dsi.fastutil.longs;

import java.util.List;

public interface LongList extends List<Long>, Comparable<List<? extends Long>>, LongCollection {
   LongListIterator iterator();

   LongListIterator listIterator();

   LongListIterator listIterator(int var1);

   LongList subList(int var1, int var2);

   void size(int var1);

   void getElements(int var1, long[] var2, int var3, int var4);

   void removeElements(int var1, int var2);

   void addElements(int var1, long[] var2);

   void addElements(int var1, long[] var2, int var3, int var4);

   boolean add(long var1);

   void add(int var1, long var2);

   /** @deprecated */
   @Deprecated
   default void add(int var1, Long var2) {
      this.add(var1, var2);
   }

   boolean addAll(int var1, LongCollection var2);

   boolean addAll(int var1, LongList var2);

   boolean addAll(LongList var1);

   long set(int var1, long var2);

   long getLong(int var1);

   int indexOf(long var1);

   int lastIndexOf(long var1);

   /** @deprecated */
   @Deprecated
   default boolean contains(Object var1) {
      return LongCollection.super.contains(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long get(int var1) {
      return this.getLong(var1);
   }

   /** @deprecated */
   @Deprecated
   default int indexOf(Object var1) {
      return this.indexOf((Long)var1);
   }

   /** @deprecated */
   @Deprecated
   default int lastIndexOf(Object var1) {
      return this.lastIndexOf((Long)var1);
   }

   /** @deprecated */
   @Deprecated
   default boolean add(Long var1) {
      return this.add(var1);
   }

   long removeLong(int var1);

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1) {
      return LongCollection.super.remove(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long remove(int var1) {
      return this.removeLong(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long set(int var1, Long var2) {
      return this.set(var1, var2);
   }
}
