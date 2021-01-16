package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Long2LongArrayMap extends AbstractLong2LongMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient long[] key;
   private transient long[] value;
   private int size;

   public Long2LongArrayMap(long[] var1, long[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Long2LongArrayMap() {
      super();
      this.key = LongArrays.EMPTY_ARRAY;
      this.value = LongArrays.EMPTY_ARRAY;
   }

   public Long2LongArrayMap(int var1) {
      super();
      this.key = new long[var1];
      this.value = new long[var1];
   }

   public Long2LongArrayMap(Long2LongMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2LongArrayMap(Map<? extends Long, ? extends Long> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2LongArrayMap(long[] var1, long[] var2, int var3) {
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

   public Long2LongMap.FastEntrySet long2LongEntrySet() {
      return new Long2LongArrayMap.EntrySet();
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

   public long get(long var1) {
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
      this.size = 0;
   }

   public boolean containsKey(long var1) {
      return this.findKey(var1) != -1;
   }

   public boolean containsValue(long var1) {
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return false;
         }
      } while(this.value[var3] != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public long put(long var1, long var3) {
      int var5 = this.findKey(var1);
      if (var5 != -1) {
         long var9 = this.value[var5];
         this.value[var5] = var3;
         return var9;
      } else {
         if (this.size == this.key.length) {
            long[] var6 = new long[this.size == 0 ? 2 : this.size * 2];
            long[] var7 = new long[this.size == 0 ? 2 : this.size * 2];

            for(int var8 = this.size; var8-- != 0; var7[var8] = this.value[var8]) {
               var6[var8] = this.key[var8];
            }

            this.key = var6;
            this.value = var7;
         }

         this.key[this.size] = var1;
         this.value[this.size] = var3;
         ++this.size;
         return this.defRetValue;
      }
   }

   public long remove(long var1) {
      int var3 = this.findKey(var1);
      if (var3 == -1) {
         return this.defRetValue;
      } else {
         long var4 = this.value[var3];
         int var6 = this.size - var3 - 1;
         System.arraycopy(this.key, var3 + 1, this.key, var3, var6);
         System.arraycopy(this.value, var3 + 1, this.value, var3, var6);
         --this.size;
         return var4;
      }
   }

   public LongSet keySet() {
      return new AbstractLongSet() {
         public boolean contains(long var1) {
            return Long2LongArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(long var1) {
            int var3 = Long2LongArrayMap.this.findKey(var1);
            if (var3 == -1) {
               return false;
            } else {
               int var4 = Long2LongArrayMap.this.size - var3 - 1;
               System.arraycopy(Long2LongArrayMap.this.key, var3 + 1, Long2LongArrayMap.this.key, var3, var4);
               System.arraycopy(Long2LongArrayMap.this.value, var3 + 1, Long2LongArrayMap.this.value, var3, var4);
               Long2LongArrayMap.this.size--;
               return true;
            }
         }

         public LongIterator iterator() {
            return new LongIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2LongArrayMap.this.size;
               }

               public long nextLong() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2LongArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2LongArrayMap.this.size - this.pos;
                     System.arraycopy(Long2LongArrayMap.this.key, this.pos, Long2LongArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2LongArrayMap.this.value, this.pos, Long2LongArrayMap.this.value, this.pos - 1, var1);
                     Long2LongArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2LongArrayMap.this.size;
         }

         public void clear() {
            Long2LongArrayMap.this.clear();
         }
      };
   }

   public LongCollection values() {
      return new AbstractLongCollection() {
         public boolean contains(long var1) {
            return Long2LongArrayMap.this.containsValue(var1);
         }

         public LongIterator iterator() {
            return new LongIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2LongArrayMap.this.size;
               }

               public long nextLong() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2LongArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2LongArrayMap.this.size - this.pos;
                     System.arraycopy(Long2LongArrayMap.this.key, this.pos, Long2LongArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2LongArrayMap.this.value, this.pos, Long2LongArrayMap.this.value, this.pos - 1, var1);
                     Long2LongArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2LongArrayMap.this.size;
         }

         public void clear() {
            Long2LongArrayMap.this.clear();
         }
      };
   }

   public Long2LongArrayMap clone() {
      Long2LongArrayMap var1;
      try {
         var1 = (Long2LongArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (long[])this.key.clone();
      var1.value = (long[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeLong(this.key[var2]);
         var1.writeLong(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new long[this.size];
      this.value = new long[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readLong();
         this.value[var2] = var1.readLong();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Long2LongMap.Entry> implements Long2LongMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Long2LongMap.Entry> iterator() {
         return new ObjectIterator<Long2LongMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Long2LongArrayMap.this.size;
            }

            public Long2LongMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractLong2LongMap.BasicEntry(Long2LongArrayMap.this.key[this.curr = this.next], Long2LongArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2LongArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2LongArrayMap.this.key, this.next + 1, Long2LongArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2LongArrayMap.this.value, this.next + 1, Long2LongArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Long2LongMap.Entry> fastIterator() {
         return new ObjectIterator<Long2LongMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractLong2LongMap.BasicEntry entry = new AbstractLong2LongMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Long2LongArrayMap.this.size;
            }

            public Long2LongMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Long2LongArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Long2LongArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2LongArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2LongArrayMap.this.key, this.next + 1, Long2LongArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2LongArrayMap.this.value, this.next + 1, Long2LongArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Long2LongArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Long) {
               if (var2.getValue() != null && var2.getValue() instanceof Long) {
                  long var3 = (Long)var2.getKey();
                  return Long2LongArrayMap.this.containsKey(var3) && Long2LongArrayMap.this.get(var3) == (Long)var2.getValue();
               } else {
                  return false;
               }
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
               if (var2.getValue() != null && var2.getValue() instanceof Long) {
                  long var3 = (Long)var2.getKey();
                  long var5 = (Long)var2.getValue();
                  int var7 = Long2LongArrayMap.this.findKey(var3);
                  if (var7 != -1 && var5 == Long2LongArrayMap.this.value[var7]) {
                     int var8 = Long2LongArrayMap.this.size - var7 - 1;
                     System.arraycopy(Long2LongArrayMap.this.key, var7 + 1, Long2LongArrayMap.this.key, var7, var8);
                     System.arraycopy(Long2LongArrayMap.this.value, var7 + 1, Long2LongArrayMap.this.value, var7, var8);
                     Long2LongArrayMap.this.size--;
                     return true;
                  } else {
                     return false;
                  }
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
