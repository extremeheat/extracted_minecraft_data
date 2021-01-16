package it.unimi.dsi.fastutil.bytes;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

public final class ByteLists {
   public static final ByteLists.EmptyList EMPTY_LIST = new ByteLists.EmptyList();

   private ByteLists() {
      super();
   }

   public static ByteList shuffle(ByteList var0, Random var1) {
      int var2 = var0.size();

      while(var2-- != 0) {
         int var3 = var1.nextInt(var2 + 1);
         byte var4 = var0.getByte(var2);
         var0.set(var2, var0.getByte(var3));
         var0.set(var3, var4);
      }

      return var0;
   }

   public static ByteList singleton(byte var0) {
      return new ByteLists.Singleton(var0);
   }

   public static ByteList singleton(Object var0) {
      return new ByteLists.Singleton((Byte)var0);
   }

   public static ByteList synchronize(ByteList var0) {
      return (ByteList)(var0 instanceof RandomAccess ? new ByteLists.SynchronizedRandomAccessList(var0) : new ByteLists.SynchronizedList(var0));
   }

   public static ByteList synchronize(ByteList var0, Object var1) {
      return (ByteList)(var0 instanceof RandomAccess ? new ByteLists.SynchronizedRandomAccessList(var0, var1) : new ByteLists.SynchronizedList(var0, var1));
   }

   public static ByteList unmodifiable(ByteList var0) {
      return (ByteList)(var0 instanceof RandomAccess ? new ByteLists.UnmodifiableRandomAccessList(var0) : new ByteLists.UnmodifiableList(var0));
   }

   public static class UnmodifiableRandomAccessList extends ByteLists.UnmodifiableList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected UnmodifiableRandomAccessList(ByteList var1) {
         super(var1);
      }

      public ByteList subList(int var1, int var2) {
         return new ByteLists.UnmodifiableRandomAccessList(this.list.subList(var1, var2));
      }
   }

   public static class UnmodifiableList extends ByteCollections.UnmodifiableCollection implements ByteList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteList list;

      protected UnmodifiableList(ByteList var1) {
         super(var1);
         this.list = var1;
      }

      public byte getByte(int var1) {
         return this.list.getByte(var1);
      }

      public byte set(int var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public byte removeByte(int var1) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(byte var1) {
         return this.list.indexOf(var1);
      }

      public int lastIndexOf(byte var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(int var1, Collection<? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public void getElements(int var1, byte[] var2, int var3, int var4) {
         this.list.getElements(var1, var2, var3, var4);
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, byte[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, byte[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         this.list.size(var1);
      }

      public ByteListIterator listIterator() {
         return ByteIterators.unmodifiable(this.list.listIterator());
      }

      public ByteListIterator iterator() {
         return this.listIterator();
      }

      public ByteListIterator listIterator(int var1) {
         return ByteIterators.unmodifiable(this.list.listIterator(var1));
      }

      public ByteList subList(int var1, int var2) {
         return new ByteLists.UnmodifiableList(this.list.subList(var1, var2));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.collection.equals(var1);
      }

      public int hashCode() {
         return this.collection.hashCode();
      }

      public int compareTo(List<? extends Byte> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(int var1, ByteCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ByteList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, ByteList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte get(int var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte set(int var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte remove(int var1) {
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

   public static class SynchronizedRandomAccessList extends ByteLists.SynchronizedList implements RandomAccess, Serializable {
      private static final long serialVersionUID = 0L;

      protected SynchronizedRandomAccessList(ByteList var1, Object var2) {
         super(var1, var2);
      }

      protected SynchronizedRandomAccessList(ByteList var1) {
         super(var1);
      }

      public ByteList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new ByteLists.SynchronizedRandomAccessList(this.list.subList(var1, var2), this.sync);
         }
      }
   }

   public static class SynchronizedList extends ByteCollections.SynchronizedCollection implements ByteList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteList list;

      protected SynchronizedList(ByteList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedList(ByteList var1) {
         super(var1);
         this.list = var1;
      }

      public byte getByte(int var1) {
         synchronized(this.sync) {
            return this.list.getByte(var1);
         }
      }

      public byte set(int var1, byte var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      public void add(int var1, byte var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      public byte removeByte(int var1) {
         synchronized(this.sync) {
            return this.list.removeByte(var1);
         }
      }

      public int indexOf(byte var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public int lastIndexOf(byte var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(int var1, Collection<? extends Byte> var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var2);
         }
      }

      public void getElements(int var1, byte[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.getElements(var1, var2, var3, var4);
         }
      }

      public void removeElements(int var1, int var2) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var2);
         }
      }

      public void addElements(int var1, byte[] var2, int var3, int var4) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2, var3, var4);
         }
      }

      public void addElements(int var1, byte[] var2) {
         synchronized(this.sync) {
            this.list.addElements(var1, var2);
         }
      }

      public void size(int var1) {
         synchronized(this.sync) {
            this.list.size(var1);
         }
      }

      public ByteListIterator listIterator() {
         return this.list.listIterator();
      }

      public ByteListIterator iterator() {
         return this.listIterator();
      }

      public ByteListIterator listIterator(int var1) {
         return this.list.listIterator(var1);
      }

      public ByteList subList(int var1, int var2) {
         synchronized(this.sync) {
            return new ByteLists.SynchronizedList(this.list.subList(var1, var2), this.sync);
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

      public int compareTo(List<? extends Byte> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(int var1, ByteCollection var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(int var1, ByteList var2) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var2);
         }
      }

      public boolean addAll(ByteList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte get(int var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Byte var2) {
         synchronized(this.sync) {
            this.list.add(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte set(int var1, Byte var2) {
         synchronized(this.sync) {
            return this.list.set(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte remove(int var1) {
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

   public static class Singleton extends AbstractByteList implements RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final byte element;

      protected Singleton(byte var1) {
         super();
         this.element = var1;
      }

      public byte getByte(int var1) {
         if (var1 == 0) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(byte var1) {
         throw new UnsupportedOperationException();
      }

      public byte removeByte(int var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(byte var1) {
         return var1 == this.element;
      }

      public byte[] toByteArray() {
         byte[] var1 = new byte[]{this.element};
         return var1;
      }

      public ByteListIterator listIterator() {
         return ByteIterators.singleton(this.element);
      }

      public ByteListIterator iterator() {
         return this.listIterator();
      }

      public ByteListIterator listIterator(int var1) {
         if (var1 <= 1 && var1 >= 0) {
            ByteListIterator var2 = this.listIterator();
            if (var1 == 1) {
               var2.nextByte();
            }

            return var2;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public ByteList subList(int var1, int var2) {
         this.ensureIndex(var1);
         this.ensureIndex(var2);
         if (var1 > var2) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var2 + ")");
         } else {
            return (ByteList)(var1 == 0 && var2 == 1 ? this : ByteLists.EMPTY_LIST);
         }
      }

      public boolean addAll(int var1, Collection<? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(Collection<? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ByteList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, ByteList var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, ByteCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ByteCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(ByteCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(ByteCollection var1) {
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

   public static class EmptyList extends ByteCollections.EmptyCollection implements ByteList, RandomAccess, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyList() {
         super();
      }

      public byte getByte(int var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(byte var1) {
         throw new UnsupportedOperationException();
      }

      public byte removeByte(int var1) {
         throw new UnsupportedOperationException();
      }

      public void add(int var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public byte set(int var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public int indexOf(byte var1) {
         return -1;
      }

      public int lastIndexOf(byte var1) {
         return -1;
      }

      public boolean addAll(int var1, Collection<? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ByteList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, ByteCollection var2) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(int var1, ByteList var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(int var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte get(int var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Byte var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte set(int var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte remove(int var1) {
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

      public ByteListIterator listIterator() {
         return ByteIterators.EMPTY_ITERATOR;
      }

      public ByteListIterator iterator() {
         return ByteIterators.EMPTY_ITERATOR;
      }

      public ByteListIterator listIterator(int var1) {
         if (var1 == 0) {
            return ByteIterators.EMPTY_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public ByteList subList(int var1, int var2) {
         if (var1 == 0 && var2 == 0) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(int var1, byte[] var2, int var3, int var4) {
         if (var1 != 0 || var4 != 0 || var3 < 0 || var3 > var2.length) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(int var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, byte[] var2, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public void addElements(int var1, byte[] var2) {
         throw new UnsupportedOperationException();
      }

      public void size(int var1) {
         throw new UnsupportedOperationException();
      }

      public int compareTo(List<? extends Byte> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return ByteLists.EMPTY_LIST;
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
         return ByteLists.EMPTY_LIST;
      }
   }
}
