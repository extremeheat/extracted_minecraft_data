package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ObjectArraySet<K> extends AbstractObjectSet<K> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient Object[] a;
   private int size;

   public ObjectArraySet(Object[] var1) {
      super();
      this.a = var1;
      this.size = var1.length;
   }

   public ObjectArraySet() {
      super();
      this.a = ObjectArrays.EMPTY_ARRAY;
   }

   public ObjectArraySet(int var1) {
      super();
      this.a = new Object[var1];
   }

   public ObjectArraySet(ObjectCollection<K> var1) {
      this(var1.size());
      this.addAll(var1);
   }

   public ObjectArraySet(Collection<? extends K> var1) {
      this(var1.size());
      this.addAll(var1);
   }

   public ObjectArraySet(Object[] var1, int var2) {
      super();
      this.a = var1;
      this.size = var2;
      if (var2 > var1.length) {
         throw new IllegalArgumentException("The provided size (" + var2 + ") is larger than or equal to the array size (" + var1.length + ")");
      }
   }

   private int findKey(Object var1) {
      int var2 = this.size;

      do {
         if (var2-- == 0) {
            return -1;
         }
      } while(!Objects.equals(this.a[var2], var1));

      return var2;
   }

   public ObjectIterator<K> iterator() {
      return new ObjectIterator<K>() {
         int next = 0;

         public boolean hasNext() {
            return this.next < ObjectArraySet.this.size;
         }

         public K next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return ObjectArraySet.this.a[this.next++];
            }
         }

         public void remove() {
            int var1 = ObjectArraySet.this.size-- - this.next--;
            System.arraycopy(ObjectArraySet.this.a, this.next + 1, ObjectArraySet.this.a, this.next, var1);
            ObjectArraySet.this.a[ObjectArraySet.this.size] = null;
         }
      };
   }

   public boolean contains(Object var1) {
      return this.findKey(var1) != -1;
   }

   public int size() {
      return this.size;
   }

   public boolean remove(Object var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return false;
      } else {
         int var3 = this.size - var2 - 1;

         for(int var4 = 0; var4 < var3; ++var4) {
            this.a[var2 + var4] = this.a[var2 + var4 + 1];
         }

         --this.size;
         this.a[this.size] = null;
         return true;
      }
   }

   public boolean add(K var1) {
      int var2 = this.findKey(var1);
      if (var2 != -1) {
         return false;
      } else {
         if (this.size == this.a.length) {
            Object[] var3 = new Object[this.size == 0 ? 2 : this.size * 2];

            for(int var4 = this.size; var4-- != 0; var3[var4] = this.a[var4]) {
            }

            this.a = var3;
         }

         this.a[this.size++] = var1;
         return true;
      }
   }

   public void clear() {
      Arrays.fill(this.a, 0, this.size, (Object)null);
      this.size = 0;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public ObjectArraySet<K> clone() {
      ObjectArraySet var1;
      try {
         var1 = (ObjectArraySet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.a = (Object[])this.a.clone();
      return var1;
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
