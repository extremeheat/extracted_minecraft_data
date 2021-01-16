package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;

public final class ByteBigLists {
   public static final ByteBigLists.EmptyBigList EMPTY_BIG_LIST = new ByteBigLists.EmptyBigList();

   private ByteBigLists() {
      super();
   }

   public static ByteBigList shuffle(ByteBigList var0, Random var1) {
      long var2 = var0.size64();

      while(var2-- != 0L) {
         long var4 = (var1.nextLong() & 9223372036854775807L) % (var2 + 1L);
         byte var6 = var0.getByte(var2);
         var0.set(var2, var0.getByte(var4));
         var0.set(var4, var6);
      }

      return var0;
   }

   public static ByteBigList singleton(byte var0) {
      return new ByteBigLists.Singleton(var0);
   }

   public static ByteBigList singleton(Object var0) {
      return new ByteBigLists.Singleton((Byte)var0);
   }

   public static ByteBigList synchronize(ByteBigList var0) {
      return new ByteBigLists.SynchronizedBigList(var0);
   }

   public static ByteBigList synchronize(ByteBigList var0, Object var1) {
      return new ByteBigLists.SynchronizedBigList(var0, var1);
   }

   public static ByteBigList unmodifiable(ByteBigList var0) {
      return new ByteBigLists.UnmodifiableBigList(var0);
   }

   public static ByteBigList asBigList(ByteList var0) {
      return new ByteBigLists.ListBigList(var0);
   }

   public static class ListBigList extends AbstractByteBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final ByteList list;

      protected ListBigList(ByteList var1) {
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

      public ByteBigListIterator iterator() {
         return ByteBigListIterators.asBigListIterator(this.list.iterator());
      }

      public ByteBigListIterator listIterator() {
         return ByteBigListIterators.asBigListIterator(this.list.listIterator());
      }

      public ByteBigListIterator listIterator(long var1) {
         return ByteBigListIterators.asBigListIterator(this.list.listIterator(this.intIndex(var1)));
      }

      public boolean addAll(long var1, Collection<? extends Byte> var3) {
         return this.list.addAll(this.intIndex(var1), (Collection)var3);
      }

      public ByteBigList subList(long var1, long var3) {
         return new ByteBigLists.ListBigList(this.list.subList(this.intIndex(var1), this.intIndex(var3)));
      }

      public boolean contains(byte var1) {
         return this.list.contains(var1);
      }

      public byte[] toByteArray() {
         return this.list.toByteArray();
      }

      public void removeElements(long var1, long var3) {
         this.list.removeElements(this.intIndex(var1), this.intIndex(var3));
      }

      /** @deprecated */
      @Deprecated
      public byte[] toByteArray(byte[] var1) {
         return this.list.toArray(var1);
      }

      public boolean addAll(long var1, ByteCollection var3) {
         return this.list.addAll(this.intIndex(var1), var3);
      }

      public boolean addAll(ByteCollection var1) {
         return this.list.addAll(var1);
      }

      public boolean addAll(long var1, ByteBigList var3) {
         return this.list.addAll(this.intIndex(var1), (ByteCollection)var3);
      }

      public boolean addAll(ByteBigList var1) {
         return this.list.addAll(var1);
      }

      public boolean containsAll(ByteCollection var1) {
         return this.list.containsAll(var1);
      }

      public boolean removeAll(ByteCollection var1) {
         return this.list.removeAll(var1);
      }

      public boolean retainAll(ByteCollection var1) {
         return this.list.retainAll(var1);
      }

      public void add(long var1, byte var3) {
         this.list.add(this.intIndex(var1), var3);
      }

      public boolean add(byte var1) {
         return this.list.add(var1);
      }

      public byte getByte(long var1) {
         return this.list.getByte(this.intIndex(var1));
      }

      public long indexOf(byte var1) {
         return (long)this.list.indexOf(var1);
      }

      public long lastIndexOf(byte var1) {
         return (long)this.list.lastIndexOf(var1);
      }

      public byte removeByte(long var1) {
         return this.list.removeByte(this.intIndex(var1));
      }

      public byte set(long var1, byte var3) {
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

      public boolean addAll(Collection<? extends Byte> var1) {
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

   public static class UnmodifiableBigList extends ByteCollections.UnmodifiableCollection implements ByteBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteBigList list;

      protected UnmodifiableBigList(ByteBigList var1) {
         super(var1);
         this.list = var1;
      }

      public byte getByte(long var1) {
         return this.list.getByte(var1);
      }

      public byte set(long var1, byte var3) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, byte var3) {
         throw new UnsupportedOperationException();
      }

      public byte removeByte(long var1) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(byte var1) {
         return this.list.indexOf(var1);
      }

      public long lastIndexOf(byte var1) {
         return this.list.lastIndexOf(var1);
      }

      public boolean addAll(long var1, Collection<? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }

      public void getElements(long var1, byte[][] var3, long var4, long var6) {
         this.list.getElements(var1, var3, var4, var6);
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, byte[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, byte[][] var3) {
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

      public ByteBigListIterator iterator() {
         return this.listIterator();
      }

      public ByteBigListIterator listIterator() {
         return ByteBigListIterators.unmodifiable(this.list.listIterator());
      }

      public ByteBigListIterator listIterator(long var1) {
         return ByteBigListIterators.unmodifiable(this.list.listIterator(var1));
      }

      public ByteBigList subList(long var1, long var3) {
         return ByteBigLists.unmodifiable(this.list.subList(var1, var3));
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.list.equals(var1);
      }

      public int hashCode() {
         return this.list.hashCode();
      }

      public int compareTo(BigList<? extends Byte> var1) {
         return this.list.compareTo(var1);
      }

      public boolean addAll(long var1, ByteCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ByteBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, ByteBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte get(long var1) {
         return this.list.get(var1);
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Byte var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte set(long var1, Byte var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte remove(long var1) {
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

   public static class SynchronizedBigList extends ByteCollections.SynchronizedCollection implements ByteBigList, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteBigList list;

      protected SynchronizedBigList(ByteBigList var1, Object var2) {
         super(var1, var2);
         this.list = var1;
      }

      protected SynchronizedBigList(ByteBigList var1) {
         super(var1);
         this.list = var1;
      }

      public byte getByte(long var1) {
         synchronized(this.sync) {
            return this.list.getByte(var1);
         }
      }

      public byte set(long var1, byte var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      public void add(long var1, byte var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      public byte removeByte(long var1) {
         synchronized(this.sync) {
            return this.list.removeByte(var1);
         }
      }

      public long indexOf(byte var1) {
         synchronized(this.sync) {
            return this.list.indexOf(var1);
         }
      }

      public long lastIndexOf(byte var1) {
         synchronized(this.sync) {
            return this.list.lastIndexOf(var1);
         }
      }

      public boolean addAll(long var1, Collection<? extends Byte> var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, (Collection)var3);
         }
      }

      public void getElements(long var1, byte[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.getElements(var1, var3, var4, var6);
         }
      }

      public void removeElements(long var1, long var3) {
         synchronized(this.sync) {
            this.list.removeElements(var1, var3);
         }
      }

      public void addElements(long var1, byte[][] var3, long var4, long var6) {
         synchronized(this.sync) {
            this.list.addElements(var1, var3, var4, var6);
         }
      }

      public void addElements(long var1, byte[][] var3) {
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

      public ByteBigListIterator iterator() {
         return this.list.listIterator();
      }

      public ByteBigListIterator listIterator() {
         return this.list.listIterator();
      }

      public ByteBigListIterator listIterator(long var1) {
         return this.list.listIterator(var1);
      }

      public ByteBigList subList(long var1, long var3) {
         synchronized(this.sync) {
            return ByteBigLists.synchronize(this.list.subList(var1, var3), this.sync);
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

      public int compareTo(BigList<? extends Byte> var1) {
         synchronized(this.sync) {
            return this.list.compareTo(var1);
         }
      }

      public boolean addAll(long var1, ByteCollection var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(long var1, ByteBigList var3) {
         synchronized(this.sync) {
            return this.list.addAll(var1, var3);
         }
      }

      public boolean addAll(ByteBigList var1) {
         synchronized(this.sync) {
            return this.list.addAll(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Byte var3) {
         synchronized(this.sync) {
            this.list.add(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte get(long var1) {
         synchronized(this.sync) {
            return this.list.get(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte set(long var1, Byte var3) {
         synchronized(this.sync) {
            return this.list.set(var1, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte remove(long var1) {
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

   public static class Singleton extends AbstractByteBigList implements Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      private final byte element;

      protected Singleton(byte var1) {
         super();
         this.element = var1;
      }

      public byte getByte(long var1) {
         if (var1 == 0L) {
            return this.element;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public boolean rem(byte var1) {
         throw new UnsupportedOperationException();
      }

      public byte removeByte(long var1) {
         throw new UnsupportedOperationException();
      }

      public boolean contains(byte var1) {
         return var1 == this.element;
      }

      public byte[] toByteArray() {
         byte[] var1 = new byte[]{this.element};
         return var1;
      }

      public ByteBigListIterator listIterator() {
         return ByteBigListIterators.singleton(this.element);
      }

      public ByteBigListIterator listIterator(long var1) {
         if (var1 <= 1L && var1 >= 0L) {
            ByteBigListIterator var3 = this.listIterator();
            if (var1 == 1L) {
               var3.nextByte();
            }

            return var3;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public ByteBigList subList(long var1, long var3) {
         this.ensureIndex(var1);
         this.ensureIndex(var3);
         if (var1 > var3) {
            throw new IndexOutOfBoundsException("Start index (" + var1 + ") is greater than end index (" + var3 + ")");
         } else {
            return (ByteBigList)(var1 == 0L && var3 == 1L ? this : ByteBigLists.EMPTY_BIG_LIST);
         }
      }

      public boolean addAll(long var1, Collection<? extends Byte> var3) {
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

      public boolean addAll(ByteBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, ByteBigList var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, ByteCollection var3) {
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

   public static class EmptyBigList extends ByteCollections.EmptyCollection implements ByteBigList, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigList() {
         super();
      }

      public byte getByte(long var1) {
         throw new IndexOutOfBoundsException();
      }

      public boolean rem(byte var1) {
         throw new UnsupportedOperationException();
      }

      public byte removeByte(long var1) {
         throw new UnsupportedOperationException();
      }

      public void add(long var1, byte var3) {
         throw new UnsupportedOperationException();
      }

      public byte set(long var1, byte var3) {
         throw new UnsupportedOperationException();
      }

      public long indexOf(byte var1) {
         return -1L;
      }

      public long lastIndexOf(byte var1) {
         return -1L;
      }

      public boolean addAll(long var1, Collection<? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ByteCollection var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(ByteBigList var1) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, ByteCollection var3) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(long var1, ByteBigList var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public void add(long var1, Byte var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean add(Byte var1) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte get(long var1) {
         throw new IndexOutOfBoundsException();
      }

      /** @deprecated */
      @Deprecated
      public Byte set(long var1, Byte var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte remove(long var1) {
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

      public ByteBigListIterator listIterator() {
         return ByteBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public ByteBigListIterator iterator() {
         return ByteBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      public ByteBigListIterator listIterator(long var1) {
         if (var1 == 0L) {
            return ByteBigListIterators.EMPTY_BIG_LIST_ITERATOR;
         } else {
            throw new IndexOutOfBoundsException(String.valueOf(var1));
         }
      }

      public ByteBigList subList(long var1, long var3) {
         if (var1 == 0L && var3 == 0L) {
            return this;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void getElements(long var1, byte[][] var3, long var4, long var6) {
         ByteBigArrays.ensureOffsetLength(var3, var4, var6);
         if (var1 != 0L) {
            throw new IndexOutOfBoundsException();
         }
      }

      public void removeElements(long var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, byte[][] var3, long var4, long var6) {
         throw new UnsupportedOperationException();
      }

      public void addElements(long var1, byte[][] var3) {
         throw new UnsupportedOperationException();
      }

      public void size(long var1) {
         throw new UnsupportedOperationException();
      }

      public long size64() {
         return 0L;
      }

      public int compareTo(BigList<? extends Byte> var1) {
         if (var1 == this) {
            return 0;
         } else {
            return var1.isEmpty() ? 0 : -1;
         }
      }

      public Object clone() {
         return ByteBigLists.EMPTY_BIG_LIST;
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
         return ByteBigLists.EMPTY_BIG_LIST;
      }
   }
}
