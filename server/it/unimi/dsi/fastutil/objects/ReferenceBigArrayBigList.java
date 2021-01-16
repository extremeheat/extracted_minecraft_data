package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigArrays;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ReferenceBigArrayBigList<K> extends AbstractReferenceBigList<K> implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353131L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected final boolean wrapped;
   protected transient K[][] a;
   protected long size;

   protected ReferenceBigArrayBigList(K[][] var1, boolean var2) {
      super();
      this.a = var1;
      this.wrapped = true;
   }

   public ReferenceBigArrayBigList(long var1) {
      super();
      if (var1 < 0L) {
         throw new IllegalArgumentException("Initial capacity (" + var1 + ") is negative");
      } else {
         if (var1 == 0L) {
            this.a = ObjectBigArrays.EMPTY_BIG_ARRAY;
         } else {
            this.a = ObjectBigArrays.newBigArray(var1);
         }

         this.wrapped = false;
      }
   }

   public ReferenceBigArrayBigList() {
      super();
      this.a = ObjectBigArrays.DEFAULT_EMPTY_BIG_ARRAY;
      this.wrapped = false;
   }

   public ReferenceBigArrayBigList(ReferenceCollection<? extends K> var1) {
      this((long)var1.size());
      ObjectIterator var2 = var1.iterator();

      while(var2.hasNext()) {
         this.add(var2.next());
      }

   }

   public ReferenceBigArrayBigList(ReferenceBigList<? extends K> var1) {
      this(var1.size64());
      var1.getElements(0L, this.a, 0L, this.size = var1.size64());
   }

   public ReferenceBigArrayBigList(K[][] var1) {
      this(var1, 0L, ObjectBigArrays.length(var1));
   }

   public ReferenceBigArrayBigList(K[][] var1, long var2, long var4) {
      this(var4);
      ObjectBigArrays.copy(var1, var2, this.a, 0L, var4);
      this.size = var4;
   }

   public ReferenceBigArrayBigList(Iterator<? extends K> var1) {
      this();

      while(var1.hasNext()) {
         this.add(var1.next());
      }

   }

   public ReferenceBigArrayBigList(ObjectIterator<? extends K> var1) {
      this();

      while(var1.hasNext()) {
         this.add(var1.next());
      }

   }

   public K[][] elements() {
      return this.a;
   }

   public static <K> ReferenceBigArrayBigList<K> wrap(K[][] var0, long var1) {
      if (var1 > ObjectBigArrays.length(var0)) {
         throw new IllegalArgumentException("The specified length (" + var1 + ") is greater than the array size (" + ObjectBigArrays.length(var0) + ")");
      } else {
         ReferenceBigArrayBigList var3 = new ReferenceBigArrayBigList(var0, false);
         var3.size = var1;
         return var3;
      }
   }

   public static <K> ReferenceBigArrayBigList<K> wrap(K[][] var0) {
      return wrap(var0, ObjectBigArrays.length(var0));
   }

   public void ensureCapacity(long var1) {
      if (var1 > (long)this.a.length && this.a != ObjectBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
         if (this.wrapped) {
            this.a = ObjectBigArrays.forceCapacity(this.a, var1, this.size);
         } else if (var1 > ObjectBigArrays.length(this.a)) {
            Object[][] var3 = ObjectBigArrays.newBigArray(var1);
            ObjectBigArrays.copy(this.a, 0L, var3, 0L, this.size);
            this.a = var3;
         }

         assert this.size <= ObjectBigArrays.length(this.a);

      }
   }

   private void grow(long var1) {
      long var3 = ObjectBigArrays.length(this.a);
      if (var1 > var3) {
         if (this.a != ObjectBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
            var1 = Math.max(var3 + (var3 >> 1), var1);
         } else if (var1 < 10L) {
            var1 = 10L;
         }

         if (this.wrapped) {
            this.a = ObjectBigArrays.forceCapacity(this.a, var1, this.size);
         } else {
            Object[][] var5 = ObjectBigArrays.newBigArray(var1);
            ObjectBigArrays.copy(this.a, 0L, var5, 0L, this.size);
            this.a = var5;
         }

         assert this.size <= ObjectBigArrays.length(this.a);

      }
   }

   public void add(long var1, K var3) {
      this.ensureIndex(var1);
      this.grow(this.size + 1L);
      if (var1 != this.size) {
         ObjectBigArrays.copy(this.a, var1, this.a, var1 + 1L, this.size - var1);
      }

      ObjectBigArrays.set(this.a, var1, var3);
      ++this.size;

      assert this.size <= ObjectBigArrays.length(this.a);

   }

   public boolean add(K var1) {
      this.grow(this.size + 1L);
      ObjectBigArrays.set(this.a, (long)(this.size++), var1);

      assert this.size <= ObjectBigArrays.length(this.a);

      return true;
   }

   public K get(long var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return ObjectBigArrays.get(this.a, var1);
      }
   }

   public long indexOf(Object var1) {
      for(long var2 = 0L; var2 < this.size; ++var2) {
         if (var1 == ObjectBigArrays.get(this.a, var2)) {
            return var2;
         }
      }

      return -1L;
   }

   public long lastIndexOf(Object var1) {
      long var2 = this.size;

      do {
         if (var2-- == 0L) {
            return -1L;
         }
      } while(var1 != ObjectBigArrays.get(this.a, var2));

      return var2;
   }

   public K remove(long var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         Object var3 = ObjectBigArrays.get(this.a, var1);
         --this.size;
         if (var1 != this.size) {
            ObjectBigArrays.copy(this.a, var1 + 1L, this.a, var1, this.size - var1);
         }

         ObjectBigArrays.set(this.a, this.size, (Object)null);

         assert this.size <= ObjectBigArrays.length(this.a);

         return var3;
      }
   }

   public boolean remove(Object var1) {
      long var2 = this.indexOf(var1);
      if (var2 == -1L) {
         return false;
      } else {
         this.remove(var2);

         assert this.size <= ObjectBigArrays.length(this.a);

         return true;
      }
   }

   public K set(long var1, K var3) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         Object var4 = ObjectBigArrays.get(this.a, var1);
         ObjectBigArrays.set(this.a, var1, var3);
         return var4;
      }
   }

   public boolean removeAll(Collection<?> var1) {
      Object[] var2 = null;
      Object[] var3 = null;
      int var4 = -1;
      int var5 = 134217728;
      int var6 = -1;
      int var7 = 134217728;

      long var8;
      for(var8 = 0L; var8 < this.size; ++var8) {
         if (var5 == 134217728) {
            var5 = 0;
            ++var4;
            var2 = this.a[var4];
         }

         if (!var1.contains(var2[var5])) {
            if (var7 == 134217728) {
               ++var6;
               var3 = this.a[var6];
               var7 = 0;
            }

            var3[var7++] = var2[var5];
         }

         ++var5;
      }

      var8 = BigArrays.index(var6, var7);
      ObjectBigArrays.fill(this.a, var8, this.size, (Object)null);
      boolean var10 = this.size != var8;
      this.size = var8;
      return var10;
   }

   public void clear() {
      ObjectBigArrays.fill(this.a, 0L, this.size, (Object)null);
      this.size = 0L;

      assert this.size <= ObjectBigArrays.length(this.a);

   }

   public long size64() {
      return this.size;
   }

   public void size(long var1) {
      if (var1 > ObjectBigArrays.length(this.a)) {
         this.ensureCapacity(var1);
      }

      if (var1 > this.size) {
         ObjectBigArrays.fill(this.a, this.size, var1, (Object)null);
      } else {
         ObjectBigArrays.fill(this.a, var1, this.size, (Object)null);
      }

      this.size = var1;
   }

   public boolean isEmpty() {
      return this.size == 0L;
   }

   public void trim() {
      this.trim(0L);
   }

   public void trim(long var1) {
      long var3 = ObjectBigArrays.length(this.a);
      if (var1 < var3 && this.size != var3) {
         this.a = ObjectBigArrays.trim(this.a, Math.max(var1, this.size));

         assert this.size <= ObjectBigArrays.length(this.a);

      }
   }

   public void getElements(long var1, Object[][] var3, long var4, long var6) {
      ObjectBigArrays.copy(this.a, var1, var3, var4, var6);
   }

   public void removeElements(long var1, long var3) {
      BigArrays.ensureFromTo(this.size, var1, var3);
      ObjectBigArrays.copy(this.a, var3, this.a, var1, this.size - var3);
      this.size -= var3 - var1;
      ObjectBigArrays.fill(this.a, this.size, this.size + var3 - var1, (Object)null);
   }

   public void addElements(long var1, K[][] var3, long var4, long var6) {
      this.ensureIndex(var1);
      ObjectBigArrays.ensureOffsetLength(var3, var4, var6);
      this.grow(this.size + var6);
      ObjectBigArrays.copy(this.a, var1, this.a, var1 + var6, this.size - var1);
      ObjectBigArrays.copy(var3, var4, this.a, var1, var6);
      this.size += var6;
   }

   public ObjectBigListIterator<K> listIterator(final long var1) {
      this.ensureIndex(var1);
      return new ObjectBigListIterator<K>() {
         long pos = var1;
         long last = -1L;

         public boolean hasNext() {
            return this.pos < ReferenceBigArrayBigList.this.size;
         }

         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         public K next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return ObjectBigArrays.get(ReferenceBigArrayBigList.this.a, this.last = (long)(this.pos++));
            }
         }

         public K previous() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return ObjectBigArrays.get(ReferenceBigArrayBigList.this.a, this.last = --this.pos);
            }
         }

         public long nextIndex() {
            return this.pos;
         }

         public long previousIndex() {
            return this.pos - 1L;
         }

         public void add(K var1x) {
            ReferenceBigArrayBigList.this.add((long)(this.pos++), var1x);
            this.last = -1L;
         }

         public void set(K var1x) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               ReferenceBigArrayBigList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               ReferenceBigArrayBigList.this.remove(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1L;
            }
         }
      };
   }

   public ReferenceBigArrayBigList<K> clone() {
      ReferenceBigArrayBigList var1 = new ReferenceBigArrayBigList(this.size);
      ObjectBigArrays.copy(this.a, 0L, var1.a, 0L, this.size);
      var1.size = this.size;
      return var1;
   }

   public boolean equals(ReferenceBigArrayBigList<K> var1) {
      if (var1 == this) {
         return true;
      } else {
         long var2 = this.size64();
         if (var2 != var1.size64()) {
            return false;
         } else {
            Object[][] var4 = this.a;
            Object[][] var5 = var1.a;

            do {
               if (var2-- == 0L) {
                  return true;
               }
            } while(ObjectBigArrays.get(var4, var2) == ObjectBigArrays.get(var5, var2));

            return false;
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; (long)var2 < this.size; ++var2) {
         var1.writeObject(ObjectBigArrays.get(this.a, (long)var2));
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.a = ObjectBigArrays.newBigArray(this.size);

      for(int var2 = 0; (long)var2 < this.size; ++var2) {
         ObjectBigArrays.set(this.a, (long)var2, var1.readObject());
      }

   }
}
