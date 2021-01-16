package it.unimi.dsi.fastutil.booleans;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

public final class BooleanLists {
   public static final BooleanLists.EmptyList EMPTY_LIST = new BooleanLists.EmptyList();

   private BooleanLists() {
      super();
   }

   public static BooleanList shuffle(BooleanList var0, Random var1) {
      int var2 = var0.size();

      while(var2-- != 0) {
         int var3 = var1.nextInt(var2 + 1);
         boolean var4 = var0.getBoolean(var2);
         var0.set(var2, var0.getBoolean(var3));
         var0.set(var3, var4);
      }

      return var0;
   }

   public static BooleanList singleton(boolean var0) {
      return new BooleanLists.Singleton(var0);
   }

   public static BooleanList singleton(Object var0) {
      return new BooleanLists.Singleton((Boolean)var0);
   }

   public static BooleanList synchronize(BooleanList var0) {
      return (BooleanList)(var0 instanceof RandomAccess ? new BooleanLists.SynchronizedRandomAccessList(var0) : new BooleanLists.SynchronizedList(var0));
   }

   public static BooleanList synchronize(BooleanList var0, Object var1) {
      return (BooleanList)(var0 instanceof RandomAccess ? new BooleanLists.SynchronizedRandomAccessList(var0, var1) : new BooleanLists.SynchronizedList(var0, var1));
   }

   public static BooleanList unmodifiable(BooleanList var0) {
      return (BooleanList)(var0 instanceof RandomAccess ? new BooleanLists.UnmodifiableRandomAccessList(var0) : new BooleanLists.UnmodifiableList(var0));
   }

   public static class UnmodifiableRandomAccessList extends BooleanLists.UnmodifiableList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected UnmodifiableRandomAccessList(BooleanList var1) {
         super(var1);
      }

      public BooleanList subList(int var1, int var2) {
         return new BooleanLists.UnmodifiableRandomAccessList(this.list.subList(var1, var2));
      }
   }

   public static class UnmodifiableList extends BooleanCollections.UnmodifiableCollection implements BooleanList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final BooleanList list;

      protected UnmodifiableList(BooleanList var1) {
         super(var1);
         this.list = var1;
      }

      public boolean getBoolean(int var1) {
         return this.list.getBoolean(var1);
      }

      public boolean set(int var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean removeBoolean(int var1) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(boolean var1) {
         return this.list.indexOf(var1);
      }

      public int lastIndexOf(boolean var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(int var1, Collection<? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public void getElements(int var1, boolean[] var2, int var3, int var4) {
         this.list.getElements(var1, var2, var3, var4);
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, boolean[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, boolean[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         this.list.size(var1);
      }

      public BooleanListIterator listIterator() {
         return BooleanIterators.unmodifiable(this.list.listIterator());
      }

      public BooleanListIterator iterator() {
         return this.listIterator();
      }

      public BooleanListIterator listIterator(int var1) {
         return BooleanIterators.unmodifiable(this.list.listIterator(var1));
      }

      public BooleanList subList(int var1, int var2) {
         return new BooleanLists.UnmodifiableList(this.list.subList(var1, var2));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.collection.equals(var1);
      }

      public int hashCode() {
         return this.collection.hashCode();
      }

      public int compareTo(List<? extends Boolean> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(int var1, BooleanCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(BooleanList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, BooleanList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean get(int var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean set(int var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean remove(int var1) {
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

   public static class SynchronizedRandomAccessList extends BooleanLists.SynchronizedList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected SynchronizedRandomAccessList(BooleanList var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedRandomAccessList(BooleanList var1) {
         super(var1);
      }

      public BooleanList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new BooleanLists.SynchronizedRandomAccessList(this.list.subList(var1, var2), this.sync);
         }
      }
   }

   public static class SynchronizedList extends BooleanCollections.SynchronizedCollection implements BooleanList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final BooleanList list;

      protected SynchronizedList(BooleanList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedList(BooleanList var1) {
         super(var1);
         this.list = var1;
      }

      public boolean getBoolean(int var1) {
         synchronized(this.sync) {
            return this.list.getBoolean(var1);
         }
      }

      public boolean set(int var1, boolean var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      public void add(int var1, boolean var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      public boolean removeBoolean(int var1) {
         synchronized(this.sync) {
            return this.list.removeBoolean(var1);
         }
      }

      public int indexOf(boolean var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public int lastIndexOf(boolean var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(int var1, Collection<? extends Boolean> var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var2);
         }
      }

      public void getElements(int var1, boolean[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.getElements(var1, var2, var3, var4);
         }
      }

      public void removeElements(int var1, int var2) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var2);
         }
      }

      public void addElements(int var1, boolean[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2, var3, var4);
         }
      }

      public void addElements(int var1, boolean[] var2) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2);
         }
      }

      public void size(int var1) {
         synchronized(this.sync) {
            this.list.size(var1);
         }
      }

      public BooleanListIterator listIterator() {
         return this.list.listIterator();
      }

      public BooleanListIterator iterator() {
         return this.listIterator();
      }

      public BooleanListIterator listIterator(int var1) {
         return this.list.listIterator(var1);
      }

      public BooleanList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new BooleanLists.SynchronizedList(this.list.subList(var1, var2), this.sync);
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

      public int compareTo(List<? extends Boolean> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(int var1, BooleanCollection var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(int var1, BooleanList var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(BooleanList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean get(int var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Boolean var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean set(int var1, Boolean var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Boolean remove(int var1) {
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

   public static class Singleton extends AbstractBooleanList implements RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final boolean element;

      protected Singleton(boolean var1) {
         super();
         this.element = var1;
      }

      public boolean getBoolean(int var1) {
         if (var1 == 0) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeBoolean(int var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(boolean var1) {
         return var1 == this.element;
      }

      public boolean[] toBooleanArray() {
         boolean[] var1 = new boolean[]{this.element};
         return var1;
      }

      public BooleanListIterator listIterator() {
         return BooleanIterators.singleton(this.element);
      }

      public BooleanListIterator iterator() {
         return this.listIterator();
      }

      public BooleanListIterator listIterator(int var1) {
         if (var1 <= 1 && var1 >= 0) {
            BooleanListIterator var2 = this.listIterator();
            if (var1 == 1) {
               var2.nextBoolean();
            }

            return var2;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public BooleanList subList(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         if (var1 > var2) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
         } else {
            return (BooleanList)(var1 == 0 && var2 == 1 ? this : BooleanLists.EMPTY_LIST);
         }
      }

      public boolean addAll(int var1, Collection<? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(Collection<? extends Boolean> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(BooleanList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, BooleanList var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, BooleanCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(BooleanCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(BooleanCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(BooleanCollection var1) {
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

   public static class EmptyList extends BooleanCollections.EmptyCollection implements BooleanList, RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyList() {
         super();
      }

      public boolean getBoolean(int var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeBoolean(int var1) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public boolean set(int var1, boolean var2) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(boolean var1) {
         return -1;
      }

      public int lastIndexOf(boolean var1) {
         return -1;
      }

      public boolean addAll(int var1, Collection<? extends Boolean> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(BooleanList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, BooleanCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, BooleanList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean get(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Boolean var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean set(int var1, Boolean var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Boolean remove(int var1) {
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

      public BooleanListIterator listIterator() {
         return BooleanIterators.EMPTY_ITERATOR;
      }

      public BooleanListIterator iterator() {
         return BooleanIterators.EMPTY_ITERATOR;
      }

      public BooleanListIterator listIterator(int var1) {
         if (var1 == 0) {
            return BooleanIterators.EMPTY_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public BooleanList subList(int var1, int var2) {
         if (var1 == 0 && var2 == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(int var1, boolean[] var2, int var3, int var4) {
         if (var1 != 0 || var4 != 0 || var3 < 0 || var3 > var2.length) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, boolean[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, boolean[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(List<? extends Boolean> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return BooleanLists.EMPTY_LIST;
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
         return BooleanLists.EMPTY_LIST;
      }
   }
}
