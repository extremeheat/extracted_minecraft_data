package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class IntBigLists {
   public static final IntBigLists.EmptyBigList EMPTY_BIG_LIST = new IntBigLists.EmptyBigList();

   private IntBigLists() {
      super();
   }

   public static IntBigList shuffle(IntBigList var0, Random var1) {
      long var2 = var0.size64();

      while(var2-- != 0L) {
         long var4 = (var1.nextLong() & 9223372036854775807L) % (var2 + 1L);
         int var6 = var0.getInt(var2);
         var0.set(var2, var0.getInt(var4));
         var0.set(var4, var6);
      }

      return var0;
   }

   public static IntBigList singleton(int var0) {
      return new IntBigLists.Singleton(var0);
   }

   public static IntBigList singleton(Object var0) {
      return new IntBigLists.Singleton((Integer)var0);
   }

   public static IntBigList synchronize(IntBigList var0) {
      return new IntBigLists.SynchronizedBigList(var0);
   }

   public static IntBigList synchronize(IntBigList var0, Object var1) {
      return new IntBigLists.SynchronizedBigList(var0, var1);
   }

   public static IntBigList unmodifiable(IntBigList var0) {
      return new IntBigLists.UnmodifiableBigList(var0);
   }

   public static IntBigList asBigList(IntList var0) {
      return new IntBigLists.ListBigList(var0);
   }

   public static class ListBigList extends AbstractIntBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final IntList list;

      protected ListBigList(IntList var1) {
         super();
         this.list = var1;
      }

      private int intIndex(long var1) {
         if (var1 >= 2147483647L) {
            throw new IndexOutOfBoundsException("This big list is restricted to 32-bit indices");
         } else {
            return (int)var1;
         }
      }

      public long size64() {
         return (long)this.list.size();
      }

      public void size(long var1) {
         this.list.size(this.intIndex(var1));
      }

      public IntBigListIterator iterator() {
         return IntBigListIterators.asBigListIterator(this.list.iterator());
      }

      public IntBigListIterator listIterator() {
         return IntBigListIterators.asBigListIterator(this.list.listIterator());
      }

      public IntBigListIterator listIterator(long var1) {
         return IntBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(var1)));
      }

      public boolean addAll(long var1, Collection<? extends Integer> var3) {
         return this.list.addAll(this.intIndex(var1), (Collection)var3);
      }

      public IntBigList subList(long var1, long var3) {
         return new IntBigLists.ListBigList(this.list.subList(this.intIndex(var1), this.intIndex(var3)));
      }

      public boolean contains(int var1) {
         return this.list.contains(var1);
      }

      public int[] toIntArray() {
         return this.list.toIntArray();
      }

      public void removeElements(long var1, long var3) {
         this.list.removeElements(this.intIndex(var1), this.intIndex(var3));
      }

      /** @deprecated */
      @Deprecated
      public int[] toIntArray(int[] var1) {
         return this.list.toArray(var1);
      }

      public boolean addAll(long var1, IntCollection var3) {
         return this.list.addAll(this.intIndex(var1), var3);
      }

      public boolean addAll(IntCollection var1) {
         return this.list.addAll(var1);
      }

      public boolean addAll(long var1, IntBigList var3) {
         return this.list.addAll(this.intIndex(var1), (IntCollection)var3);
      }

      public boolean addAll(IntBigList var1) {
         return this.list.addAll(var1);
      }

      public boolean containsAll(IntCollection var1) {
         return this.list.containsAll(var1);
      }

      public boolean removeAll(IntCollection var1) {
         return this.list.removeAll(var1);
      }

      public boolean retainAll(IntCollection var1) {
         return this.list.retainAll(var1);
      }

      public void add(long var1, int var3) {
         this.list.add(this.intIndex(var1), var3);
      }

      public boolean add(int var1) {
         return this.list.add(var1);
      }

      public int getInt(long var1) {
         return this.list.getInt(this.intIndex(var1));
      }

      public long indexOf(int var1) {
         return (long)this.list.indexOf(var1);
      }

      public long lastIndexOf(int var1) {
         return (long)this.list.lastIndexOf(var1);
      }

      public int removeInt(long var1) {
         return this.list.removeInt(this.intIndex(var1));
      }

      public int set(long var1, int var3) {
         return this.list.set(this.intIndex(var1), var3);
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      public <T> T[] toArray(T[] var1) {
         return this.list.toArray(var1);
      }

      public boolean containsAll(Collection<?> var1) {
         return this.list.containsAll(var1);
      }

      public boolean addAll(Collection<? extends Integer> var1) {
         return this.list.addAll(var1);
      }

      public boolean removeAll(Collection<?> var1) {
         return this.list.removeAll(var1);
      }

      public boolean retainAll(Collection<?> var1) {
         return this.list.retainAll(var1);
      }

      public void clear() {
         this.list.clear();
      }

      public int hashCode() {
         return this.list.hashCode();
      }
   }

   public static class UnmodifiableBigList extends IntCollections.UnmodifiableCollection implements IntBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntBigList list;

      protected UnmodifiableBigList(IntBigList var1) {
         super(var1);
         this.list = var1;
      }

      public int getInt(long var1) {
         return this.list.getInt(var1);
      }

      public int set(long var1, int var3) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, int var3) {
         throw new UnsupportedOperationException();
      }

      public int removeInt(long var1) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(int var1) {
         return this.list.indexOf(var1);
      }

      public long lastIndexOf(int var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(long var1, Collection<? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }

      public void getElements(long var1, int[][] var3, long var4, long var6) {
         this.list.getElements(var1, var3, var4, var6);
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, int[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, int[][] var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void size(long var1) {
         this.list.size(var1);
      }

      public long size64() {
         return this.list.size64();
      }

      public IntBigListIterator iterator() {
         return this.listIterator();
      }

      public IntBigListIterator listIterator() {
         return IntBigListIterators.unmodifiable(this.list.listIterator());
      }

      public IntBigListIterator listIterator(long var1) {
         return IntBigListIterators.unmodifiable(this.list.listIterator(var1));
      }

      public IntBigList subList(long var1, long var3) {
         return IntBigLists.unmodifiable(this.list.subList(var1, var3));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.list.equals(var1);
      }

      public int hashCode() {
         return this.list.hashCode();
      }

      public int compareTo(BigList<? extends Integer> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(long var1, IntCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(IntBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, IntBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer get(long var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Integer var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer set(long var1, Integer var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer remove(long var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public long indexOf(Object var1) {
         return this.list.indexOf(var1);
      }

      /** @deprecated */
      @Deprecated
      public long lastIndexOf(Object var1) {
         return this.list.lastIndexOf(var1);
      }
   }

   public static class SynchronizedBigList extends IntCollections.SynchronizedCollection implements IntBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntBigList list;

      protected SynchronizedBigList(IntBigList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedBigList(IntBigList var1) {
         super(var1);
         this.list = var1;
      }

      public int getInt(long var1) {
         synchronized(this.sync) {
            return this.list.getInt(var1);
         }
      }

      public int set(long var1, int var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      public void add(long var1, int var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      public int removeInt(long var1) {
         synchronized(this.sync) {
            return this.list.removeInt(var1);
         }
      }

      public long indexOf(int var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public long lastIndexOf(int var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(long var1, Collection<? extends Integer> var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var3);
         }
      }

      public void getElements(long var1, int[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.getElements(var1, var3, var4, var6);
         }
      }

      public void removeElements(long var1, long var3) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var3);
         }
      }

      public void addElements(long var1, int[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.addElements(var1, var3, var4, var6);
         }
      }

      public void addElements(long var1, int[][] var3) {
         synchronized(this.sync) {
            this.list.addElements(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public void size(long var1) {
         synchronized(this.sync) {
            this.list.size(var1);
         }
      }

      public long size64() {
         synchronized(this.sync) {
            return this.list.size64();
         }
      }

      public IntBigListIterator iterator() {
         return this.list.listIterator();
      }

      public IntBigListIterator listIterator() {
         return this.list.listIterator();
      }

      public IntBigListIterator listIterator(long var1) {
         return this.list.listIterator(var1);
      }

      public IntBigList subList(long var1, long var3) {
         synchronized(this.sync) {
            return IntBigLists.synchronize(this.list.subList(var1, var3), this.sync);
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.sync) {
               return this.list.equals(var1);
            }
         }
      }

      public int hashCode() {
         synchronized(this.sync) {
            return this.list.hashCode();
         }
      }

      public int compareTo(BigList<? extends Integer> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(long var1, IntCollection var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(long var1, IntBigList var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(IntBigList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Integer var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer get(long var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer set(long var1, Integer var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer remove(long var1) {
         synchronized(this.sync) {
            return this.list.remove(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public long indexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public long lastIndexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }
   }

   public static class Singleton extends AbstractIntBigList implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final int element;

      protected Singleton(int var1) {
         super();
         this.element = var1;
      }

      public int getInt(long var1) {
         if (var1 == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(int var1) {
         throw new UnsupportedOperationException();
      }

      public int removeInt(long var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(int var1) {
         return var1 == this.element;
      }

      public int[] toIntArray() {
         int[] var1 = new int[]{this.element};
         return var1;
      }

      public IntBigListIterator listIterator() {
         return IntBigListIterators.singleton(this.element);
      }

      public IntBigListIterator listIterator(long var1) {
         if (var1 <= 1L && var1 >= 0L) {
            IntBigListIterator var3 = this.listIterator();
            if (var1 == 1L) {
               var3.nextInt();
            }

            return var3;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public IntBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return (IntBigList)(var1 == 0L && var3 == 1L ? this : IntBigLists.EMPTY_BIG_LIST);
         }
      }

      public boolean addAll(long var1, Collection<? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(Collection<? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(IntBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, IntBigList var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, IntCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(IntCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(IntCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(IntCollection var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public long size64() {
         return 1L;
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyBigList extends IntCollections.EmptyCollection implements IntBigList, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
         super();
      }

      public int getInt(long var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(int var1) {
         throw new UnsupportedOperationException();
      }

      public int removeInt(long var1) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, int var3) {
         throw new UnsupportedOperationException();
      }

      public int set(long var1, int var3) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(int var1) {
         return -1L;
      }

      public long lastIndexOf(int var1) {
         return -1L;
      }

      public boolean addAll(long var1, Collection<? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(IntCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(IntBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, IntCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, IntBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Integer var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Integer var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer get(long var1) {
         throw new IndexOutOfBoundsException();
      }

      /** @deprecated */
      @Deprecated
      public Integer set(long var1, Integer var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer remove(long var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public long indexOf(Object var1) {
         return -1L;
      }

      /** @deprecated */
      @Deprecated
      public long lastIndexOf(Object var1) {
         return -1L;
      }

      public IntBigListIterator listIterator() {
         return IntBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public IntBigListIterator iterator() {
         return IntBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public IntBigListIterator listIterator(long var1) {
         if (var1 == 0L) {
            return IntBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public IntBigList subList(long var1, long var3) {
         if (var1 == 0L && var3 == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(long var1, int[][] var3, long var4, long var6) {
         IntBigArrays.ensureOffsetLength(var3, var4, var6);
         if (var1 != 0L) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, int[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, int[][] var3) {
         throw new UnsupportedOperationException();
      }

      public void size(long var1) {
         throw new UnsupportedOperationException();
      }

      public long size64() {
         return 0L;
      }

      public int compareTo(BigList<? extends Integer> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return IntBigLists.EMPTY_BIG_LIST;
      }

      public int hashCode() {
         return 1;
      }

      public boolean equals(Object var1) {
         return var1 instanceof BigList && ((BigList)var1).isEmpty();
      }

      public String toString() {
         return "[]";
      }

      private Object readResolve() {
         return IntBigLists.EMPTY_BIG_LIST;
      }
   }
}
