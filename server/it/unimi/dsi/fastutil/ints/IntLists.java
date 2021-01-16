package it.unimi.dsi.fastutil.ints;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

public final class IntLists {
   public static final IntLists.EmptyList EMPTY_LIST = new IntLists.EmptyList();

   private IntLists() {
      super();
   }

   public static IntList shuffle(IntList var0, Random var1) {
      int var2 = var0.size();

      while(var2-- != 0) {
         int var3 = var1.nextInt(var2 + 1);
         int var4 = var0.getInt(var2);
         var0.set(var2, var0.getInt(var3));
         var0.set(var3, var4);
      }

      return var0;
   }

   public static IntList singleton(int var0) {
      return new IntLists.Singleton(var0);
   }

   public static IntList singleton(Object var0) {
      return new IntLists.Singleton((Integer)var0);
   }

   public static IntList synchronize(IntList var0) {
      return (IntList)(var0 instanceof RandomAccess ? new IntLists.SynchronizedRandomAccessList(var0) : new IntLists.SynchronizedList(var0));
   }

   public static IntList synchronize(IntList var0, Object var1) {
      return (IntList)(var0 instanceof RandomAccess ? new IntLists.SynchronizedRandomAccessList(var0, var1) : new IntLists.SynchronizedList(var0, var1));
   }

   public static IntList unmodifiable(IntList var0) {
      return (IntList)(var0 instanceof RandomAccess ? new IntLists.UnmodifiableRandomAccessList(var0) : new IntLists.UnmodifiableList(var0));
   }

   public static class UnmodifiableRandomAccessList extends IntLists.UnmodifiableList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected UnmodifiableRandomAccessList(IntList var1) {
         super(var1);
      }

      public IntList subList(int var1, int var2) {
         return new IntLists.UnmodifiableRandomAccessList(this.list.subList(var1, var2));
      }
   }

   public static class UnmodifiableList extends IntCollections.UnmodifiableCollection implements IntList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntList list;

      protected UnmodifiableList(IntList var1) {
         super(var1);
         this.list = var1;
      }

      public int getInt(int var1) {
         return this.list.getInt(var1);
      }

      public int set(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public int removeInt(int var1) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(int var1) {
         return this.list.indexOf(var1);
      }

      public int lastIndexOf(int var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(int var1, Collection<? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public void getElements(int var1, int[] var2, int var3, int var4) {
         this.list.getElements(var1, var2, var3, var4);
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, int[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, int[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         this.list.size(var1);
      }

      public IntListIterator listIterator() {
         return IntIterators.unmodifiable(this.list.listIterator());
      }

      public IntListIterator iterator() {
         return this.listIterator();
      }

      public IntListIterator listIterator(int var1) {
         return IntIterators.unmodifiable(this.list.listIterator(var1));
      }

      public IntList subList(int var1, int var2) {
         return new IntLists.UnmodifiableList(this.list.subList(var1, var2));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.collection.equals(var1);
      }

      public int hashCode() {
         return this.collection.hashCode();
      }

      public int compareTo(List<? extends Integer> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(int var1, IntCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(IntList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, IntList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer get(int var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer set(int var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer remove(int var1) {
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

   public static class SynchronizedRandomAccessList extends IntLists.SynchronizedList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected SynchronizedRandomAccessList(IntList var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedRandomAccessList(IntList var1) {
         super(var1);
      }

      public IntList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new IntLists.SynchronizedRandomAccessList(this.list.subList(var1, var2), this.sync);
         }
      }
   }

   public static class SynchronizedList extends IntCollections.SynchronizedCollection implements IntList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntList list;

      protected SynchronizedList(IntList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedList(IntList var1) {
         super(var1);
         this.list = var1;
      }

      public int getInt(int var1) {
         synchronized(this.sync) {
            return this.list.getInt(var1);
         }
      }

      public int set(int var1, int var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      public void add(int var1, int var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      public int removeInt(int var1) {
         synchronized(this.sync) {
            return this.list.removeInt(var1);
         }
      }

      public int indexOf(int var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public int lastIndexOf(int var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(int var1, Collection<? extends Integer> var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var2);
         }
      }

      public void getElements(int var1, int[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.getElements(var1, var2, var3, var4);
         }
      }

      public void removeElements(int var1, int var2) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var2);
         }
      }

      public void addElements(int var1, int[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2, var3, var4);
         }
      }

      public void addElements(int var1, int[] var2) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2);
         }
      }

      public void size(int var1) {
         synchronized(this.sync) {
            this.list.size(var1);
         }
      }

      public IntListIterator listIterator() {
         return this.list.listIterator();
      }

      public IntListIterator iterator() {
         return this.listIterator();
      }

      public IntListIterator listIterator(int var1) {
         return this.list.listIterator(var1);
      }

      public IntList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new IntLists.SynchronizedList(this.list.subList(var1, var2), this.sync);
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

      public int compareTo(List<? extends Integer> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(int var1, IntCollection var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(int var1, IntList var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(IntList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer get(int var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Integer var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer set(int var1, Integer var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer remove(int var1) {
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

   public static class Singleton extends AbstractIntList implements RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final int element;

      protected Singleton(int var1) {
         super();
         this.element = var1;
      }

      public int getInt(int var1) {
         if (var1 == 0) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(int var1) {
         throw new UnsupportedOperationException();
      }

      public int removeInt(int var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(int var1) {
         return var1 == this.element;
      }

      public int[] toIntArray() {
         int[] var1 = new int[]{this.element};
         return var1;
      }

      public IntListIterator listIterator() {
         return IntIterators.singleton(this.element);
      }

      public IntListIterator iterator() {
         return this.listIterator();
      }

      public IntListIterator listIterator(int var1) {
         if (var1 <= 1 && var1 >= 0) {
            IntListIterator var2 = this.listIterator();
            if (var1 == 1) {
               var2.nextInt();
            }

            return var2;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public IntList subList(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         if (var1 > var2) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
         } else {
            return (IntList)(var1 == 0 && var2 == 1 ? this : IntLists.EMPTY_LIST);
         }
      }

      public boolean addAll(int var1, Collection<? extends Integer> var2) {
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

      public boolean addAll(IntList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, IntList var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, IntCollection var2) {
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

   public static class EmptyList extends IntCollections.EmptyCollection implements IntList, RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyList() {
         super();
      }

      public int getInt(int var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(int var1) {
         throw new UnsupportedOperationException();
      }

      public int removeInt(int var1) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public int set(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(int var1) {
         return -1;
      }

      public int lastIndexOf(int var1) {
         return -1;
      }

      public boolean addAll(int var1, Collection<? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(IntList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, IntCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, IntList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer get(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Integer var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer set(int var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer remove(int var1) {
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

      public IntListIterator listIterator() {
         return IntIterators.EMPTY_ITERATOR;
      }

      public IntListIterator iterator() {
         return IntIterators.EMPTY_ITERATOR;
      }

      public IntListIterator listIterator(int var1) {
         if (var1 == 0) {
            return IntIterators.EMPTY_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public IntList subList(int var1, int var2) {
         if (var1 == 0 && var2 == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(int var1, int[] var2, int var3, int var4) {
         if (var1 != 0 || var4 != 0 || var3 < 0 || var3 > var2.length) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, int[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, int[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(List<? extends Integer> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return IntLists.EMPTY_LIST;
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
         return IntLists.EMPTY_LIST;
      }
   }
}
