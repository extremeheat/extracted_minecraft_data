package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Object2ObjectArrayMap<K, V> extends AbstractObject2ObjectMap<K, V> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient Object[] key;
   private transient Object[] value;
   private int size;

   public Object2ObjectArrayMap(Object[] var1, Object[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Object2ObjectArrayMap() {
      super();
      this.key = ObjectArrays.EMPTY_ARRAY;
      this.value = ObjectArrays.EMPTY_ARRAY;
   }

   public Object2ObjectArrayMap(int var1) {
      super();
      this.key = new Object[var1];
      this.value = new Object[var1];
   }

   public Object2ObjectArrayMap(Object2ObjectMap<K, V> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Object2ObjectArrayMap(Map<? extends K, ? extends V> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Object2ObjectArrayMap(Object[] var1, Object[] var2, int var3) {
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

   public Object2ObjectMap.FastEntrySet<K, V> object2ObjectEntrySet() {
      return new Object2ObjectArrayMap.EntrySet();
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

   public V get(Object var1) {
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
      for(int var1 = this.size; var1-- != 0; this.value[var1] = null) {
         this.key[var1] = null;
      }

      this.size = 0;
   }

   public boolean containsKey(Object var1) {
      return this.findKey(var1) != -1;
   }

   public boolean containsValue(Object var1) {
      int var2 = this.size;

      do {
         if (var2-- == 0) {
            return false;
         }
      } while(!Objects.equals(this.value[var2], var1));

      return true;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public V put(K var1, V var2) {
      int var3 = this.findKey(var1);
      if (var3 != -1) {
         Object var7 = this.value[var3];
         this.value[var3] = var2;
         return var7;
      } else {
         if (this.size == this.key.length) {
            Object[] var4 = new Object[this.size == 0 ? 2 : this.size * 2];
            Object[] var5 = new Object[this.size == 0 ? 2 : this.size * 2];

            for(int var6 = this.size; var6-- != 0; var5[var6] = this.value[var6]) {
               var4[var6] = this.key[var6];
            }

            this.key = var4;
            this.value = var5;
         }

         this.key[this.size] = var1;
         this.value[this.size] = var2;
         ++this.size;
         return this.defRetValue;
      }
   }

   public V remove(Object var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return this.defRetValue;
      } else {
         Object var3 = this.value[var2];
         int var4 = this.size - var2 - 1;
         System.arraycopy(this.key, var2 + 1, this.key, var2, var4);
         System.arraycopy(this.value, var2 + 1, this.value, var2, var4);
         --this.size;
         this.key[this.size] = null;
         this.value[this.size] = null;
         return var3;
      }
   }

   public ObjectSet<K> keySet() {
      return new AbstractObjectSet<K>() {
         public boolean contains(Object var1) {
            return Object2ObjectArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(Object var1) {
            int var2 = Object2ObjectArrayMap.this.findKey(var1);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = Object2ObjectArrayMap.this.size - var2 - 1;
               System.arraycopy(Object2ObjectArrayMap.this.key, var2 + 1, Object2ObjectArrayMap.this.key, var2, var3);
               System.arraycopy(Object2ObjectArrayMap.this.value, var2 + 1, Object2ObjectArrayMap.this.value, var2, var3);
               Object2ObjectArrayMap.this.size--;
               return true;
            }
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Object2ObjectArrayMap.this.size;
               }

               public K next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Object2ObjectArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Object2ObjectArrayMap.this.size - this.pos;
                     System.arraycopy(Object2ObjectArrayMap.this.key, this.pos, Object2ObjectArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Object2ObjectArrayMap.this.value, this.pos, Object2ObjectArrayMap.this.value, this.pos - 1, var1);
                     Object2ObjectArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Object2ObjectArrayMap.this.size;
         }

         public void clear() {
            Object2ObjectArrayMap.this.clear();
         }
      };
   }

   public ObjectCollection<V> values() {
      return new AbstractObjectCollection<V>() {
         public boolean contains(Object var1) {
            return Object2ObjectArrayMap.this.containsValue(var1);
         }

         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Object2ObjectArrayMap.this.size;
               }

               public V next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Object2ObjectArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Object2ObjectArrayMap.this.size - this.pos;
                     System.arraycopy(Object2ObjectArrayMap.this.key, this.pos, Object2ObjectArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Object2ObjectArrayMap.this.value, this.pos, Object2ObjectArrayMap.this.value, this.pos - 1, var1);
                     Object2ObjectArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Object2ObjectArrayMap.this.size;
         }

         public void clear() {
            Object2ObjectArrayMap.this.clear();
         }
      };
   }

   public Object2ObjectArrayMap<K, V> clone() {
      Object2ObjectArrayMap var1;
      try {
         var1 = (Object2ObjectArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (Object[])this.key.clone();
      var1.value = (Object[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeObject(this.key[var2]);
         var1.writeObject(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new Object[this.size];
      this.value = new Object[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readObject();
         this.value[var2] = var1.readObject();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Object2ObjectMap.Entry<K, V>> implements Object2ObjectMap.FastEntrySet<K, V> {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Object2ObjectMap.Entry<K, V>> iterator() {
         return new ObjectIterator<Object2ObjectMap.Entry<K, V>>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Object2ObjectArrayMap.this.size;
            }

            public Object2ObjectMap.Entry<K, V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractObject2ObjectMap.BasicEntry(Object2ObjectArrayMap.this.key[this.curr = this.next], Object2ObjectArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Object2ObjectArrayMap.this.size-- - this.next--;
                  System.arraycopy(Object2ObjectArrayMap.this.key, this.next + 1, Object2ObjectArrayMap.this.key, this.next, var1);
                  System.arraycopy(Object2ObjectArrayMap.this.value, this.next + 1, Object2ObjectArrayMap.this.value, this.next, var1);
                  Object2ObjectArrayMap.this.key[Object2ObjectArrayMap.this.size] = null;
                  Object2ObjectArrayMap.this.value[Object2ObjectArrayMap.this.size] = null;
               }
            }
         };
      }

      public ObjectIterator<Object2ObjectMap.Entry<K, V>> fastIterator() {
         return new ObjectIterator<Object2ObjectMap.Entry<K, V>>() {
            int next = 0;
            int curr = -1;
            final AbstractObject2ObjectMap.BasicEntry<K, V> entry = new AbstractObject2ObjectMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Object2ObjectArrayMap.this.size;
            }

            public Object2ObjectMap.Entry<K, V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Object2ObjectArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Object2ObjectArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Object2ObjectArrayMap.this.size-- - this.next--;
                  System.arraycopy(Object2ObjectArrayMap.this.key, this.next + 1, Object2ObjectArrayMap.this.key, this.next, var1);
                  System.arraycopy(Object2ObjectArrayMap.this.value, this.next + 1, Object2ObjectArrayMap.this.value, this.next, var1);
                  Object2ObjectArrayMap.this.key[Object2ObjectArrayMap.this.size] = null;
                  Object2ObjectArrayMap.this.value[Object2ObjectArrayMap.this.size] = null;
               }
            }
         };
      }

      public int size() {
         return Object2ObjectArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            return Object2ObjectArrayMap.this.containsKey(var3) && Objects.equals(Object2ObjectArrayMap.this.get(var3), var2.getValue());
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            int var5 = Object2ObjectArrayMap.this.findKey(var3);
            if (var5 != -1 && Objects.equals(var4, Object2ObjectArrayMap.this.value[var5])) {
               int var6 = Object2ObjectArrayMap.this.size - var5 - 1;
               System.arraycopy(Object2ObjectArrayMap.this.key, var5 + 1, Object2ObjectArrayMap.this.key, var5, var6);
               System.arraycopy(Object2ObjectArrayMap.this.value, var5 + 1, Object2ObjectArrayMap.this.value, var5, var6);
               Object2ObjectArrayMap.this.size--;
               Object2ObjectArrayMap.this.key[Object2ObjectArrayMap.this.size] = null;
               Object2ObjectArrayMap.this.value[Object2ObjectArrayMap.this.size] = null;
               return true;
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
