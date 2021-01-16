package it.unimi.dsi.fastutil.shorts;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

public final class ShortLists {
   public static final ShortLists.EmptyList EMPTY_LIST = new ShortLists.EmptyList();

   private ShortLists() {
      super();
   }

   public static ShortList shuffle(ShortList var0, Random var1) {
      int var2 = var0.size();

      while(var2-- != 0) {
         int var3 = var1.nextInt(var2 + 1);
         short var4 = var0.getShort(var2);
         var0.set(var2, var0.getShort(var3));
         var0.set(var3, var4);
      }

      return var0;
   }

   public static ShortList singleton(short var0) {
      return new ShortLists.Singleton(var0);
   }

   public static ShortList singleton(Object var0) {
      return new ShortLists.Singleton((Short)var0);
   }

   public static ShortList synchronize(ShortList var0) {
      return (ShortList)(var0 instanceof RandomAccess ? new ShortLists.SynchronizedRandomAccessList(var0) : new ShortLists.SynchronizedList(var0));
   }

   public static ShortList synchronize(ShortList var0, Object var1) {
      return (ShortList)(var0 instanceof RandomAccess ? new ShortLists.SynchronizedRandomAccessList(var0, var1) : new ShortLists.SynchronizedList(var0, var1));
   }

   public static ShortList unmodifiable(ShortList var0) {
      return (ShortList)(var0 instanceof RandomAccess ? new ShortLists.UnmodifiableRandomAccessList(var0) : new ShortLists.UnmodifiableList(var0));
   }

   public static class UnmodifiableRandomAccessList extends ShortLists.UnmodifiableList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected UnmodifiableRandomAccessList(ShortList var1) {
         super(var1);
      }

      public ShortList subList(int var1, int var2) {
         return new ShortLists.UnmodifiableRandomAccessList(this.list.subList(var1, var2));
      }
   }

   public static class UnmodifiableList extends ShortCollections.UnmodifiableCollection implements ShortList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortList list;

      protected UnmodifiableList(ShortList var1) {
         super(var1);
         this.list = var1;
      }

      public short getShort(int var1) {
         return this.list.getShort(var1);
      }

      public short set(int var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public short removeShort(int var1) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(short var1) {
         return this.list.indexOf(var1);
      }

      public int lastIndexOf(short var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(int var1, Collection<? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      public void getElements(int var1, short[] var2, int var3, int var4) {
         this.list.getElements(var1, var2, var3, var4);
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, short[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, short[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         this.list.size(var1);
      }

      public ShortListIterator listIterator() {
         return ShortIterators.unmodifiable(this.list.listIterator());
      }

      public ShortListIterator iterator() {
         return this.listIterator();
      }

      public ShortListIterator listIterator(int var1) {
         return ShortIterators.unmodifiable(this.list.listIterator(var1));
      }

      public ShortList subList(int var1, int var2) {
         return new ShortLists.UnmodifiableList(this.list.subList(var1, var2));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.collection.equals(var1);
      }

      public int hashCode() {
         return this.collection.hashCode();
      }

      public int compareTo(List<? extends Short> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(int var1, ShortCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ShortList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, ShortList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short get(int var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Short var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short set(int var1, Short var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short remove(int var1) {
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

   public static class SynchronizedRandomAccessList extends ShortLists.SynchronizedList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected SynchronizedRandomAccessList(ShortList var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedRandomAccessList(ShortList var1) {
         super(var1);
      }

      public ShortList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new ShortLists.SynchronizedRandomAccessList(this.list.subList(var1, var2), this.sync);
         }
      }
   }

   public static class SynchronizedList extends ShortCollections.SynchronizedCollection implements ShortList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortList list;

      protected SynchronizedList(ShortList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedList(ShortList var1) {
         super(var1);
         this.list = var1;
      }

      public short getShort(int var1) {
         synchronized(this.sync) {
            return this.list.getShort(var1);
         }
      }

      public short set(int var1, short var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      public void add(int var1, short var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      public short removeShort(int var1) {
         synchronized(this.sync) {
            return this.list.removeShort(var1);
         }
      }

      public int indexOf(short var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public int lastIndexOf(short var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(int var1, Collection<? extends Short> var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var2);
         }
      }

      public void getElements(int var1, short[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.getElements(var1, var2, var3, var4);
         }
      }

      public void removeElements(int var1, int var2) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var2);
         }
      }

      public void addElements(int var1, short[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2, var3, var4);
         }
      }

      public void addElements(int var1, short[] var2) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2);
         }
      }

      public void size(int var1) {
         synchronized(this.sync) {
            this.list.size(var1);
         }
      }

      public ShortListIterator listIterator() {
         return this.list.listIterator();
      }

      public ShortListIterator iterator() {
         return this.listIterator();
      }

      public ShortListIterator listIterator(int var1) {
         return this.list.listIterator(var1);
      }

      public ShortList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new ShortLists.SynchronizedList(this.list.subList(var1, var2), this.sync);
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

      public int compareTo(List<? extends Short> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(int var1, ShortCollection var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(int var1, ShortList var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(ShortList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short get(int var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Short var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short set(int var1, Short var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short remove(int var1) {
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

   public static class Singleton extends AbstractShortList implements RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final short element;

      protected Singleton(short var1) {
         super();
         this.element = var1;
      }

      public short getShort(int var1) {
         if (var1 == 0) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(short var1) {
         throw new UnsupportedOperationException();
      }

      public short removeShort(int var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(short var1) {
         return var1 == this.element;
      }

      public short[] toShortArray() {
         short[] var1 = new short[]{this.element};
         return var1;
      }

      public ShortListIterator listIterator() {
         return ShortIterators.singleton(this.element);
      }

      public ShortListIterator iterator() {
         return this.listIterator();
      }

      public ShortListIterator listIterator(int var1) {
         if (var1 <= 1 && var1 >= 0) {
            ShortListIterator var2 = this.listIterator();
            if (var1 == 1) {
               var2.nextShort();
            }

            return var2;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public ShortList subList(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         if (var1 > var2) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
         } else {
            return (ShortList)(var1 == 0 && var2 == 1 ? this : ShortLists.EMPTY_LIST);
         }
      }

      public boolean addAll(int var1, Collection<? extends Short> var2) {
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

      public boolean addAll(ShortList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, ShortList var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, ShortCollection var2) {
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

   public static class EmptyList extends ShortCollections.EmptyCollection implements ShortList, RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyList() {
         super();
      }

      public short getShort(int var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(short var1) {
         throw new UnsupportedOperationException();
      }

      public short removeShort(int var1) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public short set(int var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(short var1) {
         return -1;
      }

      public int lastIndexOf(short var1) {
         return -1;
      }

      public boolean addAll(int var1, Collection<? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ShortList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, ShortCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, ShortList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Short var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short get(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Short var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short set(int var1, Short var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short remove(int var1) {
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

      public ShortListIterator listIterator() {
         return ShortIterators.EMPTY_ITERATOR;
      }

      public ShortListIterator iterator() {
         return ShortIterators.EMPTY_ITERATOR;
      }

      public ShortListIterator listIterator(int var1) {
         if (var1 == 0) {
            return ShortIterators.EMPTY_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public ShortList subList(int var1, int var2) {
         if (var1 == 0 && var2 == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(int var1, short[] var2, int var3, int var4) {
         if (var1 != 0 || var4 != 0 || var3 < 0 || var3 > var2.length) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, short[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, short[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(List<? extends Short> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return ShortLists.EMPTY_LIST;
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
         return ShortLists.EMPTY_LIST;
      }
   }
}
