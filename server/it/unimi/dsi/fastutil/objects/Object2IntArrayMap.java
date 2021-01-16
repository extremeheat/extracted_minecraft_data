package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Object2IntArrayMap<K> extends AbstractObject2IntMap<K> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient Object[] key;
   private transient int[] value;
   private int size;

   public Object2IntArrayMap(Object[] var1, int[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Object2IntArrayMap() {
      super();
      this.key = ObjectArrays.EMPTY_ARRAY;
      this.value = IntArrays.EMPTY_ARRAY;
   }

   public Object2IntArrayMap(int var1) {
      super();
      this.key = new Object[var1];
      this.value = new int[var1];
   }

   public Object2IntArrayMap(Object2IntMap<K> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Object2IntArrayMap(Map<? extends K, ? extends Integer> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Object2IntArrayMap(Object[] var1, int[] var2, int var3) {
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

   public Object2IntMap.FastEntrySet<K> object2IntEntrySet() {
      return new Object2IntArrayMap.EntrySet();
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

   public int getInt(Object var1) {
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

   public boolean containsValue(int var1) {
      int var2 = this.size;

      do {
         if (var2-- == 0) {
            return false;
         }
      } while(this.value[var2] != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public int put(K var1, int var2) {
      int var3 = this.findKey(var1);
      if (var3 != -1) {
         int var7 = this.value[var3];
         this.value[var3] = var2;
         return var7;
      } else {
         if (this.size == this.key.length) {
            Object[] var4 = new Object[this.size == 0 ? 2 : this.size * 2];
            int[] var5 = new int[this.size == 0 ? 2 : this.size * 2];

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

   public int removeInt(Object var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return this.defRetValue;
      } else {
         int var3 = this.value[var2];
         int var4 = this.size - var2 - 1;
         System.arraycopy(this.key, var2 + 1, this.key, var2, var4);
         System.arraycopy(this.value, var2 + 1, this.value, var2, var4);
         --this.size;
         this.key[this.size] = null;
         return var3;
      }
   }

   public ObjectSet<K> keySet() {
      return new AbstractObjectSet<K>() {
         public boolean contains(Object var1) {
            return Object2IntArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(Object var1) {
            int var2 = Object2IntArrayMap.this.findKey(var1);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = Object2IntArrayMap.this.size - var2 - 1;
               System.arraycopy(Object2IntArrayMap.this.key, var2 + 1, Object2IntArrayMap.this.key, var2, var3);
               System.arraycopy(Object2IntArrayMap.this.value, var2 + 1, Object2IntArrayMap.this.value, var2, var3);
               Object2IntArrayMap.this.size--;
               return true;
            }
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Object2IntArrayMap.this.size;
               }

               public K next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Object2IntArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Object2IntArrayMap.this.size - this.pos;
                     System.arraycopy(Object2IntArrayMap.this.key, this.pos, Object2IntArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Object2IntArrayMap.this.value, this.pos, Object2IntArrayMap.this.value, this.pos - 1, var1);
                     Object2IntArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Object2IntArrayMap.this.size;
         }

         public void clear() {
            Object2IntArrayMap.this.clear();
         }
      };
   }

   public IntCollection values() {
      return new AbstractIntCollection() {
         public boolean contains(int var1) {
            return Object2IntArrayMap.this.containsValue(var1);
         }

         public IntIterator iterator() {
            return new IntIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Object2IntArrayMap.this.size;
               }

               public int nextInt() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Object2IntArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Object2IntArrayMap.this.size - this.pos;
                     System.arraycopy(Object2IntArrayMap.this.key, this.pos, Object2IntArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Object2IntArrayMap.this.value, this.pos, Object2IntArrayMap.this.value, this.pos - 1, var1);
                     Object2IntArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Object2IntArrayMap.this.size;
         }

         public void clear() {
            Object2IntArrayMap.this.clear();
         }
      };
   }

   public Object2IntArrayMap<K> clone() {
      Object2IntArrayMap var1;
      try {
         var1 = (Object2IntArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (Object[])this.key.clone();
      var1.value = (int[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeObject(this.key[var2]);
         var1.writeInt(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new Object[this.size];
      this.value = new int[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readObject();
         this.value[var2] = var1.readInt();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Object2IntMap.Entry<K>> implements Object2IntMap.FastEntrySet<K> {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Object2IntMap.Entry<K>> iterator() {
         return new ObjectIterator<Object2IntMap.Entry<K>>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Object2IntArrayMap.this.size;
            }

            public Object2IntMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractObject2IntMap.BasicEntry(Object2IntArrayMap.this.key[this.curr = this.next], Object2IntArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Object2IntArrayMap.this.size-- - this.next--;
                  System.arraycopy(Object2IntArrayMap.this.key, this.next + 1, Object2IntArrayMap.this.key, this.next, var1);
                  System.arraycopy(Object2IntArrayMap.this.value, this.next + 1, Object2IntArrayMap.this.value, this.next, var1);
                  Object2IntArrayMap.this.key[Object2IntArrayMap.this.size] = null;
               }
            }
         };
      }

      public ObjectIterator<Object2IntMap.Entry<K>> fastIterator() {
         return new ObjectIterator<Object2IntMap.Entry<K>>() {
            int next = 0;
            int curr = -1;
            final AbstractObject2IntMap.BasicEntry<K> entry = new AbstractObject2IntMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Object2IntArrayMap.this.size;
            }

            public Object2IntMap.Entry<K> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Object2IntArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Object2IntArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Object2IntArrayMap.this.size-- - this.next--;
                  System.arraycopy(Object2IntArrayMap.this.key, this.next + 1, Object2IntArrayMap.this.key, this.next, var1);
                  System.arraycopy(Object2IntArrayMap.this.value, this.next + 1, Object2IntArrayMap.this.value, this.next, var1);
                  Object2IntArrayMap.this.key[Object2IntArrayMap.this.size] = null;
               }
            }
         };
      }

      public int size() {
         return Object2IntArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getValue() != null && var2.getValue() instanceof Integer) {
               Object var3 = var2.getKey();
               return Object2IntArrayMap.this.containsKey(var3) && Object2IntArrayMap.this.getInt(var3) == (Integer)var2.getValue();
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
            if (var2.getValue() != null && var2.getValue() instanceof Integer) {
               Object var3 = var2.getKey();
               int var4 = (Integer)var2.getValue();
               int var5 = Object2IntArrayMap.this.findKey(var3);
               if (var5 != -1 && var4 == Object2IntArrayMap.this.value[var5]) {
                  int var6 = Object2IntArrayMap.this.size - var5 - 1;
                  System.arraycopy(Object2IntArrayMap.this.key, var5 + 1, Object2IntArrayMap.this.key, var5, var6);
                  System.arraycopy(Object2IntArrayMap.this.value, var5 + 1, Object2IntArrayMap.this.value, var5, var6);
                  Object2IntArrayMap.this.size--;
                  Object2IntArrayMap.this.key[Object2IntArrayMap.this.size] = null;
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
