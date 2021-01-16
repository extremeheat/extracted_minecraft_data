package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Object2DoubleArrayMap<K> extends AbstractObject2DoubleMap<K> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient Object[] key;
   private transient double[] value;
   private int size;

   public Object2DoubleArrayMap(Object[] var1, double[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Object2DoubleArrayMap() {
      super();
      this.key = ObjectArrays.EMPTY_ARRAY;
      this.value = DoubleArrays.EMPTY_ARRAY;
   }

   public Object2DoubleArrayMap(int var1) {
      super();
      this.key = new Object[var1];
      this.value = new double[var1];
   }

   public Object2DoubleArrayMap(Object2DoubleMap<K> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Object2DoubleArrayMap(Map<? extends K, ? extends Double> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Object2DoubleArrayMap(Object[] var1, double[] var2, int var3) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var3;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      } else if (var3 > var1.length) {
         throw new IllegalArgumentException("The provided size (" + var3 + ") is larger than or equal to the backing-arrays size (" + var1.length + ")");
      }
   }

   public Object2DoubleMap.FastEntrySet<K> object2DoubleEntrySet() {
      return new Object2DoubleArrayMap.EntrySet();
   }

   private int findKey(Object var1) {
      Object[] var2 = this.key;
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return -1;
         }
      } while(!Objects.equals(var2[var3], var1));

      return var3;
   }

   public double getDouble(Object var1) {
      Object[] var2 = this.key;
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return this.defRetValue;
         }
      } while(!Objects.equals(var2[var3], var1));

      return this.value[var3];
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      for(int var1 = this.size; var1-- != 0; this.key[var1] = null) {
      }

      this.size = 0;
   }

   public boolean containsKey(Object var1) {
      return this.findKey(var1) != -1;
   }

   public boolean containsValue(double var1) {
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return false;
         }
      } while(Double.doubleToLongBits(this.value[var3]) != Double.doubleToLongBits(var1));

      return true;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public double put(K var1, double var2) {
      int var4 = this.findKey(var1);
      if (var4 != -1) {
         double var8 = this.value[var4];
         this.value[var4] = var2;
         return var8;
      } else {
         if (this.size == this.key.length) {
            Object[] var5 = new Object[this.size == 0 ? 2 : this.size * 2];
            double[] var6 = new double[this.size == 0 ? 2 : this.size * 2];

            for(int var7 = this.size; var7-- != 0; var6[var7] = this.value[var7]) {
               var5[var7] = this.key[var7];
            }

            this.key = var5;
            this.value = var6;
         }

         this.key[this.size] = var1;
         this.value[this.size] = var2;
         ++this.size;
         return this.defRetValue;
      }
   }

   public double removeDouble(Object var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return this.defRetValue;
      } else {
         double var3 = this.value[var2];
         int var5 = this.size - var2 - 1;
         System.arraycopy(this.key, var2 + 1, this.key, var2, var5);
         System.arraycopy(this.value, var2 + 1, this.value, var2, var5);
         --this.size;
         this.key[this.size] = null;
         return var3;
      }
   }

   public ObjectSet<K> keySet() {
      return new AbstractObjectSet<K>() {
         public boolean contains(Object var1) {
            return Object2DoubleArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(Object var1) {
            int var2 = Object2DoubleArrayMap.this.findKey(var1);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = Object2DoubleArrayMap.this.size - var2 - 1;
               System.arraycopy(Object2DoubleArrayMap.this.key, var2 + 1, Object2DoubleArrayMap.this.key, var2, var3);
               System.arraycopy(Object2DoubleArrayMap.this.value, var2 + 1, Object2DoubleArrayMap.this.value, var2, var3);
               Object2DoubleArrayMap.this.size--;
               return true;
            }
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Object2DoubleArrayMap.this.size;
               }

               public K next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Object2DoubleArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Object2DoubleArrayMap.this.size - this.pos;
                     System.arraycopy(Object2DoubleArrayMap.this.key, this.pos, Object2DoubleArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Object2DoubleArrayMap.this.value, this.pos, Object2DoubleArrayMap.this.value, this.pos - 1, var1);
                     Object2DoubleArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Object2DoubleArrayMap.this.size;
         }

         public void clear() {
            Object2DoubleArrayMap.this.clear();
         }
      };
   }

   public DoubleCollection values() {
      return new AbstractDoubleCollection() {
         public boolean contains(double var1) {
            return Object2DoubleArrayMap.this.containsValue(var1);
         }

         public DoubleIterator iterator() {
            return new DoubleIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Object2DoubleArrayMap.this.size;
               }

               public double nextDouble() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Object2DoubleArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Object2DoubleArrayMap.this.size - this.pos;
                     System.arraycopy(Object2DoubleArrayMap.this.key, this.pos, Object2DoubleArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Object2DoubleArrayMap.this.value, this.pos, Object2DoubleArrayMap.this.value, this.pos - 1, var1);
                     Object2DoubleArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Object2DoubleArrayMap.this.size;
         }

         public void clear() {
            Object2DoubleArrayMap.this.clear();
         }
      };
   }

   public Object2DoubleArrayMap<K> clone() {
      Object2DoubleArrayMap var1;
      try {
         var1 = (Object2DoubleArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (Object[])this.key.clone();
      var1.value = (double[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeObject(this.key[var2]);
         var1.writeDouble(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new Object[this.size];
      this.value = new double[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readObject();
         this.value[var2] = var1.readDouble();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Object2DoubleMap.Entry<K>> implements Object2DoubleMap.FastEntrySet<K> {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Object2DoubleMap.Entry<K>> iterator() {
         return new ObjectIterator<Object2DoubleMap.Entry<K>>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Object2DoubleArrayMap.this.size;
            }

            public Object2DoubleMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractObject2DoubleMap.BasicEntry(Object2DoubleArrayMap.this.key[this.curr = this.next], Object2DoubleArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Object2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Object2DoubleArrayMap.this.key, this.next + 1, Object2DoubleArrayMap.this.key, this.next, var1);
                  System.arraycopy(Object2DoubleArrayMap.this.value, this.next + 1, Object2DoubleArrayMap.this.value, this.next, var1);
                  Object2DoubleArrayMap.this.key[Object2DoubleArrayMap.this.size] = null;
               }
            }
         };
      }

      public ObjectIterator<Object2DoubleMap.Entry<K>> fastIterator() {
         return new ObjectIterator<Object2DoubleMap.Entry<K>>() {
            int next = 0;
            int curr = -1;
            final AbstractObject2DoubleMap.BasicEntry<K> entry = new AbstractObject2DoubleMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Object2DoubleArrayMap.this.size;
            }

            public Object2DoubleMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Object2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Object2DoubleArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Object2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Object2DoubleArrayMap.this.key, this.next + 1, Object2DoubleArrayMap.this.key, this.next, var1);
                  System.arraycopy(Object2DoubleArrayMap.this.value, this.next + 1, Object2DoubleArrayMap.this.value, this.next, var1);
                  Object2DoubleArrayMap.this.key[Object2DoubleArrayMap.this.size] = null;
               }
            }
         };
      }

      public int size() {
         return Object2DoubleArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getValue() != null && var2.getValue() instanceof Double) {
               Object var3 = var2.getKey();
               return Object2DoubleArrayMap.this.containsKey(var3) && Double.doubleToLongBits(Object2DoubleArrayMap.this.getDouble(var3)) == Double.doubleToLongBits((Double)var2.getValue());
            } else {
               return false;
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getValue() != null && var2.getValue() instanceof Double) {
               Object var3 = var2.getKey();
               double var4 = (Double)var2.getValue();
               int var6 = Object2DoubleArrayMap.this.findKey(var3);
               if (var6 != -1 && Double.doubleToLongBits(var4) == Double.doubleToLongBits(Object2DoubleArrayMap.this.value[var6])) {
                  int var7 = Object2DoubleArrayMap.this.size - var6 - 1;
                  System.arraycopy(Object2DoubleArrayMap.this.key, var6 + 1, Object2DoubleArrayMap.this.key, var6, var7);
                  System.arraycopy(Object2DoubleArrayMap.this.value, var6 + 1, Object2DoubleArrayMap.this.value, var6, var7);
                  Object2DoubleArrayMap.this.size--;
                  Object2DoubleArrayMap.this.key[Object2DoubleArrayMap.this.size] = null;
                  return true;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      // $FF: synthetic method
      EntrySet(Object var2) {
         this();
      }
   }
}
