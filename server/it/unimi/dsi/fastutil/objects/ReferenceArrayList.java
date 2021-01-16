package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ReferenceArrayList<K> extends AbstractReferenceList<K> implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353131L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected final boolean wrapped;
   protected transient K[] a;
   protected int size;

   protected ReferenceArrayList(K[] var1, boolean var2) {
      super();
      this.a = var1;
      this.wrapped = true;
   }

   public ReferenceArrayList(int var1) {
      super();
      if (var1 < 0) {
         throw new IllegalArgumentException("Initial capacity (" + var1 + ") is negative");
      } else {
         if (var1 == 0) {
            this.a = ObjectArrays.EMPTY_ARRAY;
         } else {
            this.a = new Object[var1];
         }

         this.wrapped = false;
      }
   }

   public ReferenceArrayList() {
      super();
      this.a = ObjectArrays.DEFAULT_EMPTY_ARRAY;
      this.wrapped = false;
   }

   public ReferenceArrayList(Collection<? extends K> var1) {
      this(var1.size());
      this.size = ObjectIterators.unwrap(var1.iterator(), this.a);
   }

   public ReferenceArrayList(ReferenceCollection<? extends K> var1) {
      this(var1.size());
      this.size = ObjectIterators.unwrap(var1.iterator(), (Object[])this.a);
   }

   public ReferenceArrayList(ReferenceList<? extends K> var1) {
      this(var1.size());
      var1.getElements(0, this.a, 0, this.size = var1.size());
   }

   public ReferenceArrayList(K[] var1) {
      this(var1, 0, var1.length);
   }

   public ReferenceArrayList(K[] var1, int var2, int var3) {
      this(var3);
      System.arraycopy(var1, var2, this.a, 0, var3);
      this.size = var3;
   }

   public ReferenceArrayList(Iterator<? extends K> var1) {
      this();

      while(var1.hasNext()) {
         this.add(var1.next());
      }

   }

   public ReferenceArrayList(ObjectIterator<? extends K> var1) {
      this();

      while(var1.hasNext()) {
         this.add(var1.next());
      }

   }

   public K[] elements() {
      return this.a;
   }

   public static <K> ReferenceArrayList<K> wrap(K[] var0, int var1) {
      if (var1 > var0.length) {
         throw new IllegalArgumentException("The specified length (" + var1 + ") is greater than the array size (" + var0.length + ")");
      } else {
         ReferenceArrayList var2 = new ReferenceArrayList(var0, false);
         var2.size = var1;
         return var2;
      }
   }

   public static <K> ReferenceArrayList<K> wrap(K[] var0) {
      return wrap(var0, var0.length);
   }

   public void ensureCapacity(int var1) {
      if (var1 > this.a.length && this.a != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
         if (this.wrapped) {
            this.a = ObjectArrays.ensureCapacity(this.a, var1, this.size);
         } else if (var1 > this.a.length) {
            Object[] var2 = new Object[var1];
            System.arraycopy(this.a, 0, var2, 0, this.size);
            this.a = var2;
         }

         assert this.size <= this.a.length;

      }
   }

   private void grow(int var1) {
      if (var1 > this.a.length) {
         if (this.a != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
            var1 = (int)Math.max(Math.min((long)this.a.length + (long)(this.a.length >> 1), 2147483639L), (long)var1);
         } else if (var1 < 10) {
            var1 = 10;
         }

         if (this.wrapped) {
            this.a = ObjectArrays.forceCapacity(this.a, var1, this.size);
         } else {
            Object[] var2 = new Object[var1];
            System.arraycopy(this.a, 0, var2, 0, this.size);
            this.a = var2;
         }

         assert this.size <= this.a.length;

      }
   }

   public void add(int var1, K var2) {
      this.ensureIndex(var1);
      this.grow(this.size + 1);
      if (var1 != this.size) {
         System.arraycopy(this.a, var1, this.a, var1 + 1, this.size - var1);
      }

      this.a[var1] = var2;
      ++this.size;

      assert this.size <= this.a.length;

   }

   public boolean add(K var1) {
      this.grow(this.size + 1);
      this.a[this.size++] = var1;

      assert this.size <= this.a.length;

      return true;
   }

   public K get(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return this.a[var1];
      }
   }

   public int indexOf(Object var1) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         if (var1 == this.a[var2]) {
            return var2;
         }
      }

      return -1;
   }

   public int lastIndexOf(Object var1) {
      int var2 = this.size;

      do {
         if (var2-- == 0) {
            return -1;
         }
      } while(var1 != this.a[var2]);

      return var2;
   }

   public K remove(int var1) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         Object var2 = this.a[var1];
         --this.size;
         if (var1 != this.size) {
            System.arraycopy(this.a, var1 + 1, this.a, var1, this.size - var1);
         }

         this.a[this.size] = null;

         assert this.size <= this.a.length;

         return var2;
      }
   }

   public boolean remove(Object var1) {
      int var2 = this.indexOf(var1);
      if (var2 == -1) {
         return false;
      } else {
         this.remove(var2);

         assert this.size <= this.a.length;

         return true;
      }
   }

   public K set(int var1, K var2) {
      if (var1 >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + var1 + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         Object var3 = this.a[var1];
         this.a[var1] = var2;
         return var3;
      }
   }

   public void clear() {
      Arrays.fill(this.a, 0, this.size, (Object)null);
      this.size = 0;

      assert this.size <= this.a.length;

   }

   public int size() {
      return this.size;
   }

   public void size(int var1) {
      if (var1 > this.a.length) {
         this.ensureCapacity(var1);
      }

      if (var1 > this.size) {
         Arrays.fill(this.a, this.size, var1, (Object)null);
      } else {
         Arrays.fill(this.a, var1, this.size, (Object)null);
      }

      this.size = var1;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public void trim() {
      this.trim(0);
   }

   public void trim(int var1) {
      if (var1 < this.a.length && this.size != this.a.length) {
         Object[] var2 = new Object[Math.max(var1, this.size)];
         System.arraycopy(this.a, 0, var2, 0, this.size);
         this.a = var2;

         assert this.size <= this.a.length;

      }
   }

   public void getElements(int var1, Object[] var2, int var3, int var4) {
      ObjectArrays.ensureOffsetLength(var2, var3, var4);
      System.arraycopy(this.a, var1, var2, var3, var4);
   }

   public void removeElements(int var1, int var2) {
      it.unimi.dsi.fastutil.Arrays.ensureFromTo(this.size, var1, var2);
      System.arraycopy(this.a, var2, this.a, var1, this.size - var2);
      this.size -= var2 - var1;

      for(int var3 = var2 - var1; var3-- != 0; this.a[this.size + var3] = null) {
      }

   }

   public void addElements(int var1, K[] var2, int var3, int var4) {
      this.ensureIndex(var1);
      ObjectArrays.ensureOffsetLength(var2, var3, var4);
      this.grow(this.size + var4);
      System.arraycopy(this.a, var1, this.a, var1 + var4, this.size - var1);
      System.arraycopy(var2, var3, this.a, var1, var4);
      this.size += var4;
   }

   public boolean removeAll(Collection<?> var1) {
      Object[] var2 = this.a;
      int var3 = 0;

      for(int var4 = 0; var4 < this.size; ++var4) {
         if (!var1.contains(var2[var4])) {
            var2[var3++] = var2[var4];
         }
      }

      Arrays.fill(var2, var3, this.size, (Object)null);
      boolean var5 = this.size != var3;
      this.size = var3;
      return var5;
   }

   public ObjectListIterator<K> listIterator(final int var1) {
      this.ensureIndex(var1);
      return new ObjectListIterator<K>() {
         int pos = var1;
         int last = -1;

         public boolean hasNext() {
            return this.pos < ReferenceArrayList.this.size;
         }

         public boolean hasPrevious() {
            return this.pos > 0;
         }

         public K next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return ReferenceArrayList.this.a[this.last = this.pos++];
            }
         }

         public K previous() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return ReferenceArrayList.this.a[this.last = --this.pos];
            }
         }

         public int nextIndex() {
            return this.pos;
         }

         public int previousIndex() {
            return this.pos - 1;
         }

         public void add(K var1x) {
            ReferenceArrayList.this.add(this.pos++, var1x);
            this.last = -1;
         }

         public void set(K var1x) {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               ReferenceArrayList.this.set(this.last, var1x);
            }
         }

         public void remove() {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               ReferenceArrayList.this.remove(this.last);
               if (this.last < this.pos) {
                  --this.pos;
               }

               this.last = -1;
            }
         }
      };
   }

   public ReferenceArrayList<K> clone() {
      ReferenceArrayList var1 = new ReferenceArrayList(this.size);
      System.arraycopy(this.a, 0, var1.a, 0, this.size);
      var1.size = this.size;
      return var1;
   }

   public boolean equals(ReferenceArrayList<K> var1) {
      if (var1 == this) {
         return true;
      } else {
         int var2 = this.size();
         if (var2 != var1.size()) {
            return false;
         } else {
            Object[] var3 = this.a;
            Object[] var4 = var1.a;

            do {
               if (var2-- == 0) {
                  return true;
               }
            } while(var3[var2] == var4[var2]);

            return false;
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeObject(this.a[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.a = new Object[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.a[var2] = var1.readObject();
      }

   }
}
