package it.unimi.dsi.fastutil.longs;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

public final class LongLists {
   public static final LongLists.EmptyList EMPTY_LIST = new LongLists.EmptyList();

   private LongLists() {
      super();
   }

   public static LongList shuffle(LongList var0, Random var1) {
      int var2 = var0.size();

      while(var2-- != 0) {
         int var3 = var1.nextInt(var2 + 1);
         long var4 = var0.getLong(var2);
         var0.set(var2, var0.getLong(var3));
         var0.set(var3, var4);
      }

      return var0;
   }

   public static LongList singleton(long var0) {
      return new LongLists.Singleton(var0);
   }

   public static LongList singleton(Object var0) {
      return new LongLists.Singleton((Long)var0);
   }

   public static LongList synchronize(LongList var0) {
      return (LongList)(var0 instanceof RandomAccess ? new LongLists.SynchronizedRandomAccessList(var0) : new LongLists.SynchronizedList(var0));
   }

   public static LongList synchronize(LongList var0, Object var1) {
      return (LongList)(var0 instanceof RandomAccess ? new LongLists.SynchronizedRandomAccessList(var0, var1) : new LongLists.SynchronizedList(var0, var1));
   }

   public static LongList unmodifiable(LongList var0) {
      return (LongList)(var0 instanceof RandomAccess ? new LongLists.UnmodifiableRandomAccessList(var0) : new LongLists.UnmodifiableList(var0));
   }

   public static class UnmodifiableRandomAccessList extends LongLists.UnmodifiableList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected UnmodifiableRandomAccessList(LongList var1) {
         super(var1);
      }

      public LongList subList(int var1, int var2) {
         return new LongLists.UnmodifiableRandomAccessList(this.list.subList(var1, var2));
      }
   }

   public static class UnmodifiableList extends LongCollections.UnmodifiableCollection implements LongList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongList list;

      protected UnmodifiableList(LongList var1) {
         super(var1);
         this.list = var1;
      }

      public long getLong(int var1) {
         return this.list.getLong(var1);
      }

      public long set(int var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public long removeLong(int var1) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(long var1) {
         return this.list.indexOf(var1);
      }

      public int lastIndexOf(long var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(int var1, Collection<? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      public void getElements(int var1, long[] var2, int var3, int var4) {
         this.list.getElements(var1, var2, var3, var4);
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, long[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, long[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         this.list.size(var1);
      }

      public LongListIterator listIterator() {
         return LongIterators.unmodifiable(this.list.listIterator());
      }

      public LongListIterator iterator() {
         return this.listIterator();
      }

      public LongListIterator listIterator(int var1) {
         return LongIterators.unmodifiable(this.list.listIterator(var1));
      }

      public LongList subList(int var1, int var2) {
         return new LongLists.UnmodifiableList(this.list.subList(var1, var2));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.collection.equals(var1);
      }

      public int hashCode() {
         return this.collection.hashCode();
      }

      public int compareTo(List<? extends Long> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(int var1, LongCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(LongList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, LongList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long get(int var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long set(int var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long remove(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public int indexOf(Object var1) {
         return this.list.indexOf(var1);
      }

      /** @deprecated */
      @Deprecated
      public int lastIndexOf(Object var1) {
         return this.list.lastIndexOf(var1);
      }
   }

   public static class SynchronizedRandomAccessList extends LongLists.SynchronizedList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected SynchronizedRandomAccessList(LongList var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedRandomAccessList(LongList var1) {
         super(var1);
      }

      public LongList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new LongLists.SynchronizedRandomAccessList(this.list.subList(var1, var2), this.sync);
         }
      }
   }

   public static class SynchronizedList extends LongCollections.SynchronizedCollection implements LongList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongList list;

      protected SynchronizedList(LongList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedList(LongList var1) {
         super(var1);
         this.list = var1;
      }

      public long getLong(int var1) {
         synchronized(this.sync) {
            return this.list.getLong(var1);
         }
      }

      public long set(int var1, long var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      public void add(int var1, long var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      public long removeLong(int var1) {
         synchronized(this.sync) {
            return this.list.removeLong(var1);
         }
      }

      public int indexOf(long var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public int lastIndexOf(long var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(int var1, Collection<? extends Long> var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var2);
         }
      }

      public void getElements(int var1, long[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.getElements(var1, var2, var3, var4);
         }
      }

      public void removeElements(int var1, int var2) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var2);
         }
      }

      public void addElements(int var1, long[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2, var3, var4);
         }
      }

      public void addElements(int var1, long[] var2) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2);
         }
      }

      public void size(int var1) {
         synchronized(this.sync) {
            this.list.size(var1);
         }
      }

      public LongListIterator listIterator() {
         return this.list.listIterator();
      }

      public LongListIterator iterator() {
         return this.listIterator();
      }

      public LongListIterator listIterator(int var1) {
         return this.list.listIterator(var1);
      }

      public LongList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new LongLists.SynchronizedList(this.list.subList(var1, var2), this.sync);
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.sync) {
               return this.collection.equals(var1);
            }
         }
      }

      public int hashCode() {
         synchronized(this.sync) {
            return this.collection.hashCode();
         }
      }

      public int compareTo(List<? extends Long> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(int var1, LongCollection var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(int var1, LongList var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(LongList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long get(int var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Long var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long set(int var1, Long var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long remove(int var1) {
         synchronized(this.sync) {
            return this.list.remove(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public int indexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public int lastIndexOf(Object var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized(this.sync) {
            var1.defaultWriteObject();
         }
      }
   }

   public static class Singleton extends AbstractLongList implements RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final long element;

      protected Singleton(long var1) {
         super();
         this.element = var1;
      }

      public long getLong(int var1) {
         if (var1 == 0) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(long var1) {
         throw new UnsupportedOperationException();
      }

      public long removeLong(int var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(long var1) {
         return var1 == this.element;
      }

      public long[] toLongArray() {
         long[] var1 = new long[]{this.element};
         return var1;
      }

      public LongListIterator listIterator() {
         return LongIterators.singleton(this.element);
      }

      public LongListIterator iterator() {
         return this.listIterator();
      }

      public LongListIterator listIterator(int var1) {
         if (var1 <= 1 && var1 >= 0) {
            LongListIterator var2 = this.listIterator();
            if (var1 == 1) {
               var2.nextLong();
            }

            return var2;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public LongList subList(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         if (var1 > var2) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
         } else {
            return (LongList)(var1 == 0 && var2 == 1 ? this : LongLists.EMPTY_LIST);
         }
      }

      public boolean addAll(int var1, Collection<? extends Long> var2) {
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

      public boolean addAll(LongList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, LongList var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, LongCollection var2) {
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

      public int size() {
         return 1;
      }

      public void size(int var1) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public Object clone() {
         return this;
      }
   }

   public static class EmptyList extends LongCollections.EmptyCollection implements LongList, RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyList() {
         super();
      }

      public long getLong(int var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(long var1) {
         throw new UnsupportedOperationException();
      }

      public long removeLong(int var1) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public long set(int var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(long var1) {
         return -1;
      }

      public int lastIndexOf(long var1) {
         return -1;
      }

      public boolean addAll(int var1, Collection<? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(LongList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, LongCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, LongList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long get(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Long var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long set(int var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long remove(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public int indexOf(Object var1) {
         return -1;
      }

      /** @deprecated */
      @Deprecated
      public int lastIndexOf(Object var1) {
         return -1;
      }

      public LongListIterator listIterator() {
         return LongIterators.EMPTY_ITERATOR;
      }

      public LongListIterator iterator() {
         return LongIterators.EMPTY_ITERATOR;
      }

      public LongListIterator listIterator(int var1) {
         if (var1 == 0) {
            return LongIterators.EMPTY_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public LongList subList(int var1, int var2) {
         if (var1 == 0 && var2 == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(int var1, long[] var2, int var3, int var4) {
         if (var1 != 0 || var4 != 0 || var3 < 0 || var3 > var2.length) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, long[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, long[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(List<? extends Long> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return LongLists.EMPTY_LIST;
      }

      public int hashCode() {
         return 1;
      }

      public boolean equals(Object var1) {
         return var1 instanceof List && ((List)var1).isEmpty();
      }

      public String toString() {
         return "[]";
      }

      private Object readResolve() {
         return LongLists.EMPTY_LIST;
      }
   }
}
