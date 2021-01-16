package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Long2ShortArrayMap extends AbstractLong2ShortMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient long[] key;
   private transient short[] value;
   private int size;

   public Long2ShortArrayMap(long[] var1, short[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Long2ShortArrayMap() {
      super();
      this.key = LongArrays.EMPTY_ARRAY;
      this.value = ShortArrays.EMPTY_ARRAY;
   }

   public Long2ShortArrayMap(int var1) {
      super();
      this.key = new long[var1];
      this.value = new short[var1];
   }

   public Long2ShortArrayMap(Long2ShortMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2ShortArrayMap(Map<? extends Long, ? extends Short> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2ShortArrayMap(long[] var1, short[] var2, int var3) {
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

   public Long2ShortMap.FastEntrySet long2ShortEntrySet() {
      return new Long2ShortArrayMap.EntrySet();
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

   public short get(long var1) {
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

   public boolean containsValue(short var1) {
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

   public short put(long var1, short var3) {
      int var4 = this.findKey(var1);
      if (var4 != -1) {
         short var8 = this.value[var4];
         this.value[var4] = var3;
         return var8;
      } else {
         if (this.size == this.key.length) {
            long[] var5 = new long[this.size == 0 ? 2 : this.size * 2];
            short[] var6 = new short[this.size == 0 ? 2 : this.size * 2];

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

   public short remove(long var1) {
      int var3 = this.findKey(var1);
      if (var3 == -1) {
         return this.defRetValue;
      } else {
         short var4 = this.value[var3];
         int var5 = this.size - var3 - 1;
         System.arraycopy(this.key, var3 + 1, this.key, var3, var5);
         System.arraycopy(this.value, var3 + 1, this.value, var3, var5);
         --this.size;
         return var4;
      }
   }

   public LongSet keySet() {
      return new AbstractLongSet() {
         public boolean contains(long var1) {
            return Long2ShortArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(long var1) {
            int var3 = Long2ShortArrayMap.this.findKey(var1);
            if (var3 == -1) {
               return false;
            } else {
               int var4 = Long2ShortArrayMap.this.size - var3 - 1;
               System.arraycopy(Long2ShortArrayMap.this.key, var3 + 1, Long2ShortArrayMap.this.key, var3, var4);
               System.arraycopy(Long2ShortArrayMap.this.value, var3 + 1, Long2ShortArrayMap.this.value, var3, var4);
               Long2ShortArrayMap.this.size--;
               return true;
            }
         }

         public LongIterator iterator() {
            return new LongIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2ShortArrayMap.this.size;
               }

               public long nextLong() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2ShortArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2ShortArrayMap.this.size - this.pos;
                     System.arraycopy(Long2ShortArrayMap.this.key, this.pos, Long2ShortArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2ShortArrayMap.this.value, this.pos, Long2ShortArrayMap.this.value, this.pos - 1, var1);
                     Long2ShortArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2ShortArrayMap.this.size;
         }

         public void clear() {
            Long2ShortArrayMap.this.clear();
         }
      };
   }

   public ShortCollection values() {
      return new AbstractShortCollection() {
         public boolean contains(short var1) {
            return Long2ShortArrayMap.this.containsValue(var1);
         }

         public ShortIterator iterator() {
            return new ShortIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2ShortArrayMap.this.size;
               }

               public short nextShort() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2ShortArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2ShortArrayMap.this.size - this.pos;
                     System.arraycopy(Long2ShortArrayMap.this.key, this.pos, Long2ShortArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2ShortArrayMap.this.value, this.pos, Long2ShortArrayMap.this.value, this.pos - 1, var1);
                     Long2ShortArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2ShortArrayMap.this.size;
         }

         public void clear() {
            Long2ShortArrayMap.this.clear();
         }
      };
   }

   public Long2ShortArrayMap clone() {
      Long2ShortArrayMap var1;
      try {
         var1 = (Long2ShortArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (long[])this.key.clone();
      var1.value = (short[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeLong(this.key[var2]);
         var1.writeShort(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new long[this.size];
      this.value = new short[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readLong();
         this.value[var2] = var1.readShort();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Long2ShortMap.Entry> implements Long2ShortMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Long2ShortMap.Entry> iterator() {
         return new ObjectIterator<Long2ShortMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Long2ShortArrayMap.this.size;
            }

            public Long2ShortMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractLong2ShortMap.BasicEntry(Long2ShortArrayMap.this.key[this.curr = this.next], Long2ShortArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2ShortArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2ShortArrayMap.this.key, this.next + 1, Long2ShortArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2ShortArrayMap.this.value, this.next + 1, Long2ShortArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Long2ShortMap.Entry> fastIterator() {
         return new ObjectIterator<Long2ShortMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractLong2ShortMap.BasicEntry entry = new AbstractLong2ShortMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Long2ShortArrayMap.this.size;
            }

            public Long2ShortMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Long2ShortArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Long2ShortArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2ShortArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2ShortArrayMap.this.key, this.next + 1, Long2ShortArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2ShortArrayMap.this.value, this.next + 1, Long2ShortArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Long2ShortArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Long) {
               if (var2.getValue() != null && var2.getValue() instanceof Short) {
                  long var3 = (Long)var2.getKey();
                  return Long2ShortArrayMap.this.containsKey(var3) && Long2ShortArrayMap.this.get(var3) == (Short)var2.getValue();
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
               if (var2.getValue() != null && var2.getValue() instanceof Short) {
                  long var3 = (Long)var2.getKey();
                  short var5 = (Short)var2.getValue();
                  int var6 = Long2ShortArrayMap.this.findKey(var3);
                  if (var6 != -1 && var5 == Long2ShortArrayMap.this.value[var6]) {
                     int var7 = Long2ShortArrayMap.this.size - var6 - 1;
                     System.arraycopy(Long2ShortArrayMap.this.key, var6 + 1, Long2ShortArrayMap.this.key, var6, var7);
                     System.arraycopy(Long2ShortArrayMap.this.value, var6 + 1, Long2ShortArrayMap.this.value, var6, var7);
                     Long2ShortArrayMap.this.size--;
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
