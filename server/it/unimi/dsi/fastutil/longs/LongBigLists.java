package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class LongBigLists {
   public static final LongBigLists.EmptyBigList EMPTY_BIG_LIST = new LongBigLists.EmptyBigList();

   private LongBigLists() {
      super();
   }

   public static LongBigList shuffle(LongBigList var0, Random var1) {
      long var2 = var0.size64();

      while(var2-- != 0L) {
         long var4 = (var1.nextLong() & 9223372036854775807L) % (var2 + 1L);
         long var6 = var0.getLong(var2);
         var0.set(var2, var0.getLong(var4));
         var0.set(var4, var6);
      }

      return var0;
   }

   public static LongBigList singleton(long var0) {
      return new LongBigLists.Singleton(var0);
   }

   public static LongBigList singleton(Object var0) {
      return new LongBigLists.Singleton((Long)var0);
   }

   public static LongBigList synchronize(LongBigList var0) {
      return new LongBigLists.SynchronizedBigList(var0);
   }

   public static LongBigList synchronize(LongBigList var0, Object var1) {
      return new LongBigLists.SynchronizedBigList(var0, var1);
   }

   public static LongBigList unmodifiable(LongBigList var0) {
      return new LongBigLists.UnmodifiableBigList(var0);
   }

   public static LongBigList asBigList(LongList var0) {
      return new LongBigLists.ListBigList(var0);
   }

   public static class ListBigList extends AbstractLongBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final LongList list;

      protected ListBigList(LongList var1) {
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

      public LongBigListIterator iterator() {
         return LongBigListIterators.asBigListIterator(this.list.iterator());
      }

      public LongBigListIterator listIterator() {
         return LongBigListIterators.asBigListIterator(this.list.listIterator());
      }

      public LongBigListIterator listIterator(long var1) {
         return LongBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(var1)));
      }

      public boolean addAll(long var1, Collection<? extends Long> var3) {
         return this.list.addAll(this.intIndex(var1), (Collection)var3);
      }

      public LongBigList subList(long var1, long var3) {
         return new LongBigLists.ListBigList(this.list.subList(this.intIndex(var1), this.intIndex(var3)));
      }

      public boolean contains(long var1) {
         return this.list.contains(var1);
      }

      public long[] toLongArray() {
         return this.list.toLongArray();
      }

      public void removeElements(long var1, long var3) {
         this.list.removeElements(this.intIndex(var1), this.intIndex(var3));
      }

      /** @deprecated */
      @Deprecated
      public long[] toLongArray(long[] var1) {
         return this.list.toArray(var1);
      }

      public boolean addAll(long var1, LongCollection var3) {
         return this.list.addAll(this.intIndex(var1), var3);
      }

      public boolean addAll(LongCollection var1) {
         return this.list.addAll(var1);
      }

      public boolean addAll(long var1, LongBigList var3) {
         return this.list.addAll(this.intIndex(var1), (LongCollection)var3);
      }

      public boolean addAll(LongBigList var1) {
         return this.list.addAll(var1);
      }

      public boolean containsAll(LongCollection var1) {
         return this.list.containsAll(var1);
      }

      public boolean removeAll(LongCollection var1) {
         return this.list.removeAll(var1);
      }

      public boolean retainAll(LongCollection var1) {
         return this.list.retainAll(var1);
      }

      public void add(long var1, long var3) {
         this.list.add(this.intIndex(var1), var3);
      }

      public boolean add(long var1) {
         return this.list.add(var1);
      }

      public long getLong(long var1) {
         return this.list.getLong(this.intIndex(var1));
      }

      public long indexOf(long var1) {
         return (long)this.list.indexOf(var1);
      }

      public long lastIndexOf(long var1) {
         return (long)this.list.lastIndexOf(var1);
      }

      public long removeLong(long var1) {
         return this.list.removeLong(this.intIndex(var1));
      }

      public long set(long var1, long var3) {
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

      public boolean addAll(Collection<? extends Long> var1) {
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

   public static class UnmodifiableBigList extends LongCollections.UnmodifiableCollection implements LongBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongBigList list;

      protected UnmodifiableBigList(LongBigList var1) {
         super(var1);
         this.list = var1;
      }

      public long getLong(long var1) {
         return this.list.getLong(var1);
      }

      public long set(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public long removeLong(long var1) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(long var1) {
         return this.list.indexOf(var1);
      }

      public long lastIndexOf(long var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(long var1, Collection<? extends Long> var3) {
         throw new UnsupportedOperationException();
      }

      public void getElements(long var1, long[][] var3, long var4, long var6) {
         this.list.getElements(var1, var3, var4, var6);
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, long[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, long[][] var3) {
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

      public LongBigListIterator iterator() {
         return this.listIterator();
      }

      public LongBigListIterator listIterator() {
         return LongBigListIterators.unmodifiable(this.list.listIterator());
      }

      public LongBigListIterator listIterator(long var1) {
         return LongBigListIterators.unmodifiable(this.list.listIterator(var1));
      }

      public LongBigList subList(long var1, long var3) {
         return LongBigLists.unmodifiable(this.list.subList(var1, var3));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.list.equals(var1);
      }

      public int hashCode() {
         return this.list.hashCode();
      }

      public int compareTo(BigList<? extends Long> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(long var1, LongCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(LongBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, LongBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long get(long var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Long var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long set(long var1, Long var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long remove(long var1) {
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

   public static class SynchronizedBigList extends LongCollections.SynchronizedCollection implements LongBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongBigList list;

      protected SynchronizedBigList(LongBigList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedBigList(LongBigList var1) {
         super(var1);
         this.list = var1;
      }

      public long getLong(long var1) {
         synchronized(this.sync) {
            return this.list.getLong(var1);
         }
      }

      public long set(long var1, long var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      public void add(long var1, long var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      public long removeLong(long var1) {
         synchronized(this.sync) {
            return this.list.removeLong(var1);
         }
      }

      public long indexOf(long var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public long lastIndexOf(long var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(long var1, Collection<? extends Long> var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var3);
         }
      }

      public void getElements(long var1, long[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.getElements(var1, var3, var4, var6);
         }
      }

      public void removeElements(long var1, long var3) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var3);
         }
      }

      public void addElements(long var1, long[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.addElements(var1, var3, var4, var6);
         }
      }

      public void addElements(long var1, long[][] var3) {
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

      public LongBigListIterator iterator() {
         return this.list.listIterator();
      }

      public LongBigListIterator listIterator() {
         return this.list.listIterator();
      }

      public LongBigListIterator listIterator(long var1) {
         return this.list.listIterator(var1);
      }

      public LongBigList subList(long var1, long var3) {
         synchronized(this.sync) {
            return LongBigLists.synchronize(this.list.subList(var1, var3), this.sync);
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

      public int compareTo(BigList<? extends Long> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(long var1, LongCollection var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(long var1, LongBigList var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(LongBigList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Long var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long get(long var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long set(long var1, Long var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long remove(long var1) {
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

   public static class Singleton extends AbstractLongBigList implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final long element;

      protected Singleton(long var1) {
         super();
         this.element = var1;
      }

      public long getLong(long var1) {
         if (var1 == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(long var1) {
         throw new UnsupportedOperationException();
      }

      public long removeLong(long var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(long var1) {
         return var1 == this.element;
      }

      public long[] toLongArray() {
         long[] var1 = new long[]{this.element};
         return var1;
      }

      public LongBigListIterator listIterator() {
         return LongBigListIterators.singleton(this.element);
      }

      public LongBigListIterator listIterator(long var1) {
         if (var1 <= 1L && var1 >= 0L) {
            LongBigListIterator var3 = this.listIterator();
            if (var1 == 1L) {
               var3.nextLong();
            }

            return var3;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public LongBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return (LongBigList)(var1 == 0L && var3 == 1L ? this : LongBigLists.EMPTY_BIG_LIST);
         }
      }

      public boolean addAll(long var1, Collection<? extends Long> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(Collection<? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(LongBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, LongBigList var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, LongCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(LongCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(LongCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(LongCollection var1) {
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

   public static class EmptyBigList extends LongCollections.EmptyCollection implements LongBigList, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
         super();
      }

      public long getLong(long var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(long var1) {
         throw new UnsupportedOperationException();
      }

      public long removeLong(long var1) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public long set(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(long var1) {
         return -1L;
      }

      public long lastIndexOf(long var1) {
         return -1L;
      }

      public boolean addAll(long var1, Collection<? extends Long> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(LongCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(LongBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, LongCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, LongBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Long var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Long var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long get(long var1) {
         throw new IndexOutOfBoundsException();
      }

      /** @deprecated */
      @Deprecated
      public Long set(long var1, Long var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long remove(long var1) {
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

      public LongBigListIterator listIterator() {
         return LongBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public LongBigListIterator iterator() {
         return LongBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public LongBigListIterator listIterator(long var1) {
         if (var1 == 0L) {
            return LongBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public LongBigList subList(long var1, long var3) {
         if (var1 == 0L && var3 == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(long var1, long[][] var3, long var4, long var6) {
         LongBigArrays.ensureOffsetLength(var3, var4, var6);
         if (var1 != 0L) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, long[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, long[][] var3) {
         throw new UnsupportedOperationException();
      }

      public void size(long var1) {
         throw new UnsupportedOperationException();
      }

      public long size64() {
         return 0L;
      }

      public int compareTo(BigList<? extends Long> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return LongBigLists.EMPTY_BIG_LIST;
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
         return LongBigLists.EMPTY_BIG_LIST;
      }
   }
}
