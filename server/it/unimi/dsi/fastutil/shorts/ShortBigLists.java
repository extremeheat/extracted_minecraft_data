package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class ShortBigLists {
   public static final ShortBigLists.EmptyBigList EMPTY_BIG_LIST = new ShortBigLists.EmptyBigList();

   private ShortBigLists() {
      super();
   }

   public static ShortBigList shuffle(ShortBigList var0, Random var1) {
      long var2 = var0.size64();

      while(var2-- != 0L) {
         long var4 = (var1.nextLong() & 9223372036854775807L) % (var2 + 1L);
         short var6 = var0.getShort(var2);
         var0.set(var2, var0.getShort(var4));
         var0.set(var4, var6);
      }

      return var0;
   }

   public static ShortBigList singleton(short var0) {
      return new ShortBigLists.Singleton(var0);
   }

   public static ShortBigList singleton(Object var0) {
      return new ShortBigLists.Singleton((Short)var0);
   }

   public static ShortBigList synchronize(ShortBigList var0) {
      return new ShortBigLists.SynchronizedBigList(var0);
   }

   public static ShortBigList synchronize(ShortBigList var0, Object var1) {
      return new ShortBigLists.SynchronizedBigList(var0, var1);
   }

   public static ShortBigList unmodifiable(ShortBigList var0) {
      return new ShortBigLists.UnmodifiableBigList(var0);
   }

   public static ShortBigList asBigList(ShortList var0) {
      return new ShortBigLists.ListBigList(var0);
   }

   public static class ListBigList extends AbstractShortBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final ShortList list;

      protected ListBigList(ShortList var1) {
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

      public ShortBigListIterator iterator() {
         return ShortBigListIterators.asBigListIterator(this.list.iterator());
      }

      public ShortBigListIterator listIterator() {
         return ShortBigListIterators.asBigListIterator(this.list.listIterator());
      }

      public ShortBigListIterator listIterator(long var1) {
         return ShortBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(var1)));
      }

      public boolean addAll(long var1, Collection<? extends Short> var3) {
         return this.list.addAll(this.intIndex(var1), (Collection)var3);
      }

      public ShortBigList subList(long var1, long var3) {
         return new ShortBigLists.ListBigList(this.list.subList(this.intIndex(var1), this.intIndex(var3)));
      }

      public boolean contains(short var1) {
         return this.list.contains(var1);
      }

      public short[] toShortArray() {
         return this.list.toShortArray();
      }

      public void removeElements(long var1, long var3) {
         this.list.removeElements(this.intIndex(var1), this.intIndex(var3));
      }

      /** @deprecated */
      @Deprecated
      public short[] toShortArray(short[] var1) {
         return this.list.toArray(var1);
      }

      public boolean addAll(long var1, ShortCollection var3) {
         return this.list.addAll(this.intIndex(var1), var3);
      }

      public boolean addAll(ShortCollection var1) {
         return this.list.addAll(var1);
      }

      public boolean addAll(long var1, ShortBigList var3) {
         return this.list.addAll(this.intIndex(var1), (ShortCollection)var3);
      }

      public boolean addAll(ShortBigList var1) {
         return this.list.addAll(var1);
      }

      public boolean containsAll(ShortCollection var1) {
         return this.list.containsAll(var1);
      }

      public boolean removeAll(ShortCollection var1) {
         return this.list.removeAll(var1);
      }

      public boolean retainAll(ShortCollection var1) {
         return this.list.retainAll(var1);
      }

      public void add(long var1, short var3) {
         this.list.add(this.intIndex(var1), var3);
      }

      public boolean add(short var1) {
         return this.list.add(var1);
      }

      public short getShort(long var1) {
         return this.list.getShort(this.intIndex(var1));
      }

      public long indexOf(short var1) {
         return (long)this.list.indexOf(var1);
      }

      public long lastIndexOf(short var1) {
         return (long)this.list.lastIndexOf(var1);
      }

      public short removeShort(long var1) {
         return this.list.removeShort(this.intIndex(var1));
      }

      public short set(long var1, short var3) {
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

      public boolean addAll(Collection<? extends Short> var1) {
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

   public static class UnmodifiableBigList extends ShortCollections.UnmodifiableCollection implements ShortBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortBigList list;

      protected UnmodifiableBigList(ShortBigList var1) {
         super(var1);
         this.list = var1;
      }

      public short getShort(long var1) {
         return this.list.getShort(var1);
      }

      public short set(long var1, short var3) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, short var3) {
         throw new UnsupportedOperationException();
      }

      public short removeShort(long var1) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(short var1) {
         return this.list.indexOf(var1);
      }

      public long lastIndexOf(short var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(long var1, Collection<? extends Short> var3) {
         throw new UnsupportedOperationException();
      }

      public void getElements(long var1, short[][] var3, long var4, long var6) {
         this.list.getElements(var1, var3, var4, var6);
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, short[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, short[][] var3) {
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

      public ShortBigListIterator iterator() {
         return this.listIterator();
      }

      public ShortBigListIterator listIterator() {
         return ShortBigListIterators.unmodifiable(this.list.listIterator());
      }

      public ShortBigListIterator listIterator(long var1) {
         return ShortBigListIterators.unmodifiable(this.list.listIterator(var1));
      }

      public ShortBigList subList(long var1, long var3) {
         return ShortBigLists.unmodifiable(this.list.subList(var1, var3));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.list.equals(var1);
      }

      public int hashCode() {
         return this.list.hashCode();
      }

      public int compareTo(BigList<? extends Short> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(long var1, ShortCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ShortBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, ShortBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short get(long var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Short var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short set(long var1, Short var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short remove(long var1) {
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

   public static class SynchronizedBigList extends ShortCollections.SynchronizedCollection implements ShortBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortBigList list;

      protected SynchronizedBigList(ShortBigList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedBigList(ShortBigList var1) {
         super(var1);
         this.list = var1;
      }

      public short getShort(long var1) {
         synchronized(this.sync) {
            return this.list.getShort(var1);
         }
      }

      public short set(long var1, short var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      public void add(long var1, short var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      public short removeShort(long var1) {
         synchronized(this.sync) {
            return this.list.removeShort(var1);
         }
      }

      public long indexOf(short var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public long lastIndexOf(short var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(long var1, Collection<? extends Short> var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var3);
         }
      }

      public void getElements(long var1, short[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.getElements(var1, var3, var4, var6);
         }
      }

      public void removeElements(long var1, long var3) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var3);
         }
      }

      public void addElements(long var1, short[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.addElements(var1, var3, var4, var6);
         }
      }

      public void addElements(long var1, short[][] var3) {
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

      public ShortBigListIterator iterator() {
         return this.list.listIterator();
      }

      public ShortBigListIterator listIterator() {
         return this.list.listIterator();
      }

      public ShortBigListIterator listIterator(long var1) {
         return this.list.listIterator(var1);
      }

      public ShortBigList subList(long var1, long var3) {
         synchronized(this.sync) {
            return ShortBigLists.synchronize(this.list.subList(var1, var3), this.sync);
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

      public int compareTo(BigList<? extends Short> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(long var1, ShortCollection var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(long var1, ShortBigList var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(ShortBigList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Short var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short get(long var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short set(long var1, Short var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short remove(long var1) {
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

   public static class Singleton extends AbstractShortBigList implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final short element;

      protected Singleton(short var1) {
         super();
         this.element = var1;
      }

      public short getShort(long var1) {
         if (var1 == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(short var1) {
         throw new UnsupportedOperationException();
      }

      public short removeShort(long var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(short var1) {
         return var1 == this.element;
      }

      public short[] toShortArray() {
         short[] var1 = new short[]{this.element};
         return var1;
      }

      public ShortBigListIterator listIterator() {
         return ShortBigListIterators.singleton(this.element);
      }

      public ShortBigListIterator listIterator(long var1) {
         if (var1 <= 1L && var1 >= 0L) {
            ShortBigListIterator var3 = this.listIterator();
            if (var1 == 1L) {
               var3.nextShort();
            }

            return var3;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public ShortBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return (ShortBigList)(var1 == 0L && var3 == 1L ? this : ShortBigLists.EMPTY_BIG_LIST);
         }
      }

      public boolean addAll(long var1, Collection<? extends Short> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(Collection<? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ShortBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, ShortBigList var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, ShortCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ShortCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(ShortCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(ShortCollection var1) {
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

   public static class EmptyBigList extends ShortCollections.EmptyCollection implements ShortBigList, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
         super();
      }

      public short getShort(long var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(short var1) {
         throw new UnsupportedOperationException();
      }

      public short removeShort(long var1) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, short var3) {
         throw new UnsupportedOperationException();
      }

      public short set(long var1, short var3) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(short var1) {
         return -1L;
      }

      public long lastIndexOf(short var1) {
         return -1L;
      }

      public boolean addAll(long var1, Collection<? extends Short> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ShortCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ShortBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, ShortCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, ShortBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Short var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Short var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short get(long var1) {
         throw new IndexOutOfBoundsException();
      }

      /** @deprecated */
      @Deprecated
      public Short set(long var1, Short var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short remove(long var1) {
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

      public ShortBigListIterator listIterator() {
         return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public ShortBigListIterator iterator() {
         return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public ShortBigListIterator listIterator(long var1) {
         if (var1 == 0L) {
            return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public ShortBigList subList(long var1, long var3) {
         if (var1 == 0L && var3 == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(long var1, short[][] var3, long var4, long var6) {
         ShortBigArrays.ensureOffsetLength(var3, var4, var6);
         if (var1 != 0L) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, short[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, short[][] var3) {
         throw new UnsupportedOperationException();
      }

      public void size(long var1) {
         throw new UnsupportedOperationException();
      }

      public long size64() {
         return 0L;
      }

      public int compareTo(BigList<? extends Short> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return ShortBigLists.EMPTY_BIG_LIST;
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
         return ShortBigLists.EMPTY_BIG_LIST;
      }
   }
}
