package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Long2FloatArrayMap extends AbstractLong2FloatMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient long[] key;
   private transient float[] value;
   private int size;

   public Long2FloatArrayMap(long[] var1, float[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Long2FloatArrayMap() {
      super();
      this.key = LongArrays.EMPTY_ARRAY;
      this.value = FloatArrays.EMPTY_ARRAY;
   }

   public Long2FloatArrayMap(int var1) {
      super();
      this.key = new long[var1];
      this.value = new float[var1];
   }

   public Long2FloatArrayMap(Long2FloatMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2FloatArrayMap(Map<? extends Long, ? extends Float> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2FloatArrayMap(long[] var1, float[] var2, int var3) {
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

   public Long2FloatMap.FastEntrySet long2FloatEntrySet() {
      return new Long2FloatArrayMap.EntrySet();
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

   public float get(long var1) {
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

   public boolean containsValue(float var1) {
      int var2 = this.size;

      do {
         if (var2-- == 0) {
            return false;
         }
      } while(Float.floatToIntBits(this.value[var2]) != Float.floatToIntBits(var1));

      return true;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public float put(long var1, float var3) {
      int var4 = this.findKey(var1);
      if (var4 != -1) {
         float var8 = this.value[var4];
         this.value[var4] = var3;
         return var8;
      } else {
         if (this.size == this.key.length) {
            long[] var5 = new long[this.size == 0 ? 2 : this.size * 2];
            float[] var6 = new float[this.size == 0 ? 2 : this.size * 2];

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

   public float remove(long var1) {
      int var3 = this.findKey(var1);
      if (var3 == -1) {
         return this.defRetValue;
      } else {
         float var4 = this.value[var3];
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
            return Long2FloatArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(long var1) {
            int var3 = Long2FloatArrayMap.this.findKey(var1);
            if (var3 == -1) {
               return false;
            } else {
               int var4 = Long2FloatArrayMap.this.size - var3 - 1;
               System.arraycopy(Long2FloatArrayMap.this.key, var3 + 1, Long2FloatArrayMap.this.key, var3, var4);
               System.arraycopy(Long2FloatArrayMap.this.value, var3 + 1, Long2FloatArrayMap.this.value, var3, var4);
               Long2FloatArrayMap.this.size--;
               return true;
            }
         }

         public LongIterator iterator() {
            return new LongIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2FloatArrayMap.this.size;
               }

               public long nextLong() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2FloatArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2FloatArrayMap.this.size - this.pos;
                     System.arraycopy(Long2FloatArrayMap.this.key, this.pos, Long2FloatArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2FloatArrayMap.this.value, this.pos, Long2FloatArrayMap.this.value, this.pos - 1, var1);
                     Long2FloatArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2FloatArrayMap.this.size;
         }

         public void clear() {
            Long2FloatArrayMap.this.clear();
         }
      };
   }

   public FloatCollection values() {
      return new AbstractFloatCollection() {
         public boolean contains(float var1) {
            return Long2FloatArrayMap.this.containsValue(var1);
         }

         public FloatIterator iterator() {
            return new FloatIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2FloatArrayMap.this.size;
               }

               public float nextFloat() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2FloatArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2FloatArrayMap.this.size - this.pos;
                     System.arraycopy(Long2FloatArrayMap.this.key, this.pos, Long2FloatArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2FloatArrayMap.this.value, this.pos, Long2FloatArrayMap.this.value, this.pos - 1, var1);
                     Long2FloatArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2FloatArrayMap.this.size;
         }

         public void clear() {
            Long2FloatArrayMap.this.clear();
         }
      };
   }

   public Long2FloatArrayMap clone() {
      Long2FloatArrayMap var1;
      try {
         var1 = (Long2FloatArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (long[])this.key.clone();
      var1.value = (float[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeLong(this.key[var2]);
         var1.writeFloat(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new long[this.size];
      this.value = new float[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readLong();
         this.value[var2] = var1.readFloat();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Long2FloatMap.Entry> implements Long2FloatMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Long2FloatMap.Entry> iterator() {
         return new ObjectIterator<Long2FloatMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Long2FloatArrayMap.this.size;
            }

            public Long2FloatMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractLong2FloatMap.BasicEntry(Long2FloatArrayMap.this.key[this.curr = this.next], Long2FloatArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2FloatArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2FloatArrayMap.this.key, this.next + 1, Long2FloatArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2FloatArrayMap.this.value, this.next + 1, Long2FloatArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Long2FloatMap.Entry> fastIterator() {
         return new ObjectIterator<Long2FloatMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractLong2FloatMap.BasicEntry entry = new AbstractLong2FloatMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Long2FloatArrayMap.this.size;
            }

            public Long2FloatMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Long2FloatArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Long2FloatArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2FloatArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2FloatArrayMap.this.key, this.next + 1, Long2FloatArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2FloatArrayMap.this.value, this.next + 1, Long2FloatArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Long2FloatArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Long) {
               if (var2.getValue() != null && var2.getValue() instanceof Float) {
                  long var3 = (Long)var2.getKey();
                  return Long2FloatArrayMap.this.containsKey(var3) && Float.floatToIntBits(Long2FloatArrayMap.this.get(var3)) == Float.floatToIntBits((Float)var2.getValue());
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
               if (var2.getValue() != null && var2.getValue() instanceof Float) {
                  long var3 = (Long)var2.getKey();
                  float var5 = (Float)var2.getValue();
                  int var6 = Long2FloatArrayMap.this.findKey(var3);
                  if (var6 != -1 && Float.floatToIntBits(var5) == Float.floatToIntBits(Long2FloatArrayMap.this.value[var6])) {
                     int var7 = Long2FloatArrayMap.this.size - var6 - 1;
                     System.arraycopy(Long2FloatArrayMap.this.key, var6 + 1, Long2FloatArrayMap.this.key, var6, var7);
                     System.arraycopy(Long2FloatArrayMap.this.value, var6 + 1, Long2FloatArrayMap.this.value, var6, var7);
                     Long2FloatArrayMap.this.size--;
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
