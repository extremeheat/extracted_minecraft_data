package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Long2ReferenceArrayMap<V> extends AbstractLong2ReferenceMap<V> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient long[] key;
   private transient Object[] value;
   private int size;

   public Long2ReferenceArrayMap(long[] var1, Object[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Long2ReferenceArrayMap() {
      super();
      this.key = LongArrays.EMPTY_ARRAY;
      this.value = ObjectArrays.EMPTY_ARRAY;
   }

   public Long2ReferenceArrayMap(int var1) {
      super();
      this.key = new long[var1];
      this.value = new Object[var1];
   }

   public Long2ReferenceArrayMap(Long2ReferenceMap<V> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2ReferenceArrayMap(Map<? extends Long, ? extends V> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2ReferenceArrayMap(long[] var1, Object[] var2, int var3) {
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

   public Long2ReferenceMap.FastEntrySet<V> long2ReferenceEntrySet() {
      return new Long2ReferenceArrayMap.EntrySet();
   }

   private int findKey(long var1) {
      long[] var3 = this.key;
      int var4 = this.size;

      do {
         if (var4-- == 0) {
            return -1;
         }
      } while(var3[var4] != var1);

      return var4;
   }

   public V get(long var1) {
      long[] var3 = this.key;
      int var4 = this.size;

      do {
         if (var4-- == 0) {
            return this.defRetValue;
         }
      } while(var3[var4] != var1);

      return this.value[var4];
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      for(int var1 = this.size; var1-- != 0; this.value[var1] = null) {
      }

      this.size = 0;
   }

   public boolean containsKey(long var1) {
      return this.findKey(var1) != -1;
   }

   public boolean containsValue(Object var1) {
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

   public V put(long var1, V var3) {
      int var4 = this.findKey(var1);
      if (var4 != -1) {
         Object var8 = this.value[var4];
         this.value[var4] = var3;
         return var8;
      } else {
         if (this.size == this.key.length) {
            long[] var5 = new long[this.size == 0 ? 2 : this.size * 2];
            Object[] var6 = new Object[this.size == 0 ? 2 : this.size * 2];

            for(int var7 = this.size; var7-- != 0; var6[var7] = this.value[var7]) {
               var5[var7] = this.key[var7];
            }

            this.key = var5;
            this.value = var6;
         }

         this.key[this.size] = var1;
         this.value[this.size] = var3;
         ++this.size;
         return this.defRetValue;
      }
   }

   public V remove(long var1) {
      int var3 = this.findKey(var1);
      if (var3 == -1) {
         return this.defRetValue;
      } else {
         Object var4 = this.value[var3];
         int var5 = this.size - var3 - 1;
         System.arraycopy(this.key, var3 + 1, this.key, var3, var5);
         System.arraycopy(this.value, var3 + 1, this.value, var3, var5);
         --this.size;
         this.value[this.size] = null;
         return var4;
      }
   }

   public LongSet keySet() {
      return new AbstractLongSet() {
         public boolean contains(long var1) {
            return Long2ReferenceArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(long var1) {
            int var3 = Long2ReferenceArrayMap.this.findKey(var1);
            if (var3 == -1) {
               return false;
            } else {
               int var4 = Long2ReferenceArrayMap.this.size - var3 - 1;
               System.arraycopy(Long2ReferenceArrayMap.this.key, var3 + 1, Long2ReferenceArrayMap.this.key, var3, var4);
               System.arraycopy(Long2ReferenceArrayMap.this.value, var3 + 1, Long2ReferenceArrayMap.this.value, var3, var4);
               Long2ReferenceArrayMap.this.size--;
               return true;
            }
         }

         public LongIterator iterator() {
            return new LongIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2ReferenceArrayMap.this.size;
               }

               public long nextLong() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2ReferenceArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2ReferenceArrayMap.this.size - this.pos;
                     System.arraycopy(Long2ReferenceArrayMap.this.key, this.pos, Long2ReferenceArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2ReferenceArrayMap.this.value, this.pos, Long2ReferenceArrayMap.this.value, this.pos - 1, var1);
                     Long2ReferenceArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2ReferenceArrayMap.this.size;
         }

         public void clear() {
            Long2ReferenceArrayMap.this.clear();
         }
      };
   }

   public ReferenceCollection<V> values() {
      return new AbstractReferenceCollection<V>() {
         public boolean contains(Object var1) {
            return Long2ReferenceArrayMap.this.containsValue(var1);
         }

         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2ReferenceArrayMap.this.size;
               }

               public V next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2ReferenceArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2ReferenceArrayMap.this.size - this.pos;
                     System.arraycopy(Long2ReferenceArrayMap.this.key, this.pos, Long2ReferenceArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2ReferenceArrayMap.this.value, this.pos, Long2ReferenceArrayMap.this.value, this.pos - 1, var1);
                     Long2ReferenceArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2ReferenceArrayMap.this.size;
         }

         public void clear() {
            Long2ReferenceArrayMap.this.clear();
         }
      };
   }

   public Long2ReferenceArrayMap<V> clone() {
      Long2ReferenceArrayMap var1;
      try {
         var1 = (Long2ReferenceArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (long[])this.key.clone();
      var1.value = (Object[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeLong(this.key[var2]);
         var1.writeObject(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new long[this.size];
      this.value = new Object[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readLong();
         this.value[var2] = var1.readObject();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Long2ReferenceMap.Entry<V>> implements Long2ReferenceMap.FastEntrySet<V> {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Long2ReferenceMap.Entry<V>> iterator() {
         return new ObjectIterator<Long2ReferenceMap.Entry<V>>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Long2ReferenceArrayMap.this.size;
            }

            public Long2ReferenceMap.Entry<V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractLong2ReferenceMap.BasicEntry(Long2ReferenceArrayMap.this.key[this.curr = this.next], Long2ReferenceArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2ReferenceArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2ReferenceArrayMap.this.key, this.next + 1, Long2ReferenceArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2ReferenceArrayMap.this.value, this.next + 1, Long2ReferenceArrayMap.this.value, this.next, var1);
                  Long2ReferenceArrayMap.this.value[Long2ReferenceArrayMap.this.size] = null;
               }
            }
         };
      }

      public ObjectIterator<Long2ReferenceMap.Entry<V>> fastIterator() {
         return new ObjectIterator<Long2ReferenceMap.Entry<V>>() {
            int next = 0;
            int curr = -1;
            final AbstractLong2ReferenceMap.BasicEntry<V> entry = new AbstractLong2ReferenceMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Long2ReferenceArrayMap.this.size;
            }

            public Long2ReferenceMap.Entry<V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Long2ReferenceArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Long2ReferenceArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2ReferenceArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2ReferenceArrayMap.this.key, this.next + 1, Long2ReferenceArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2ReferenceArrayMap.this.value, this.next + 1, Long2ReferenceArrayMap.this.value, this.next, var1);
                  Long2ReferenceArrayMap.this.value[Long2ReferenceArrayMap.this.size] = null;
               }
            }
         };
      }

      public int size() {
         return Long2ReferenceArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Long) {
               long var3 = (Long)var2.getKey();
               return Long2ReferenceArrayMap.this.containsKey(var3) && Long2ReferenceArrayMap.this.get(var3) == var2.getValue();
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
            if (var2.getKey() != null && var2.getKey() instanceof Long) {
               long var3 = (Long)var2.getKey();
               Object var5 = var2.getValue();
               int var6 = Long2ReferenceArrayMap.this.findKey(var3);
               if (var6 != -1 && var5 == Long2ReferenceArrayMap.this.value[var6]) {
                  int var7 = Long2ReferenceArrayMap.this.size - var6 - 1;
                  System.arraycopy(Long2ReferenceArrayMap.this.key, var6 + 1, Long2ReferenceArrayMap.this.key, var6, var7);
                  System.arraycopy(Long2ReferenceArrayMap.this.value, var6 + 1, Long2ReferenceArrayMap.this.value, var6, var7);
                  Long2ReferenceArrayMap.this.size--;
                  Long2ReferenceArrayMap.this.value[Long2ReferenceArrayMap.this.size] = null;
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
