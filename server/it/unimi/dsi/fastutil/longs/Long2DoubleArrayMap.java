package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Long2DoubleArrayMap extends AbstractLong2DoubleMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient long[] key;
   private transient double[] value;
   private int size;

   public Long2DoubleArrayMap(long[] var1, double[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Long2DoubleArrayMap() {
      super();
      this.key = LongArrays.EMPTY_ARRAY;
      this.value = DoubleArrays.EMPTY_ARRAY;
   }

   public Long2DoubleArrayMap(int var1) {
      super();
      this.key = new long[var1];
      this.value = new double[var1];
   }

   public Long2DoubleArrayMap(Long2DoubleMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2DoubleArrayMap(Map<? extends Long, ? extends Double> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2DoubleArrayMap(long[] var1, double[] var2, int var3) {
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

   public Long2DoubleMap.FastEntrySet long2DoubleEntrySet() {
      return new Long2DoubleArrayMap.EntrySet();
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

   public double get(long var1) {
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

   public double put(long var1, double var3) {
      int var5 = this.findKey(var1);
      if (var5 != -1) {
         double var9 = this.value[var5];
         this.value[var5] = var3;
         return var9;
      } else {
         if (this.size == this.key.length) {
            long[] var6 = new long[this.size == 0 ? 2 : this.size * 2];
            double[] var7 = new double[this.size == 0 ? 2 : this.size * 2];

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

   public double remove(long var1) {
      int var3 = this.findKey(var1);
      if (var3 == -1) {
         return this.defRetValue;
      } else {
         double var4 = this.value[var3];
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
            return Long2DoubleArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(long var1) {
            int var3 = Long2DoubleArrayMap.this.findKey(var1);
            if (var3 == -1) {
               return false;
            } else {
               int var4 = Long2DoubleArrayMap.this.size - var3 - 1;
               System.arraycopy(Long2DoubleArrayMap.this.key, var3 + 1, Long2DoubleArrayMap.this.key, var3, var4);
               System.arraycopy(Long2DoubleArrayMap.this.value, var3 + 1, Long2DoubleArrayMap.this.value, var3, var4);
               Long2DoubleArrayMap.this.size--;
               return true;
            }
         }

         public LongIterator iterator() {
            return new LongIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2DoubleArrayMap.this.size;
               }

               public long nextLong() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2DoubleArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2DoubleArrayMap.this.size - this.pos;
                     System.arraycopy(Long2DoubleArrayMap.this.key, this.pos, Long2DoubleArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2DoubleArrayMap.this.value, this.pos, Long2DoubleArrayMap.this.value, this.pos - 1, var1);
                     Long2DoubleArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2DoubleArrayMap.this.size;
         }

         public void clear() {
            Long2DoubleArrayMap.this.clear();
         }
      };
   }

   public DoubleCollection values() {
      return new AbstractDoubleCollection() {
         public boolean contains(double var1) {
            return Long2DoubleArrayMap.this.containsValue(var1);
         }

         public DoubleIterator iterator() {
            return new DoubleIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2DoubleArrayMap.this.size;
               }

               public double nextDouble() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2DoubleArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2DoubleArrayMap.this.size - this.pos;
                     System.arraycopy(Long2DoubleArrayMap.this.key, this.pos, Long2DoubleArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2DoubleArrayMap.this.value, this.pos, Long2DoubleArrayMap.this.value, this.pos - 1, var1);
                     Long2DoubleArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2DoubleArrayMap.this.size;
         }

         public void clear() {
            Long2DoubleArrayMap.this.clear();
         }
      };
   }

   public Long2DoubleArrayMap clone() {
      Long2DoubleArrayMap var1;
      try {
         var1 = (Long2DoubleArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (long[])this.key.clone();
      var1.value = (double[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeLong(this.key[var2]);
         var1.writeDouble(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new long[this.size];
      this.value = new double[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readLong();
         this.value[var2] = var1.readDouble();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Long2DoubleMap.Entry> implements Long2DoubleMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Long2DoubleMap.Entry> iterator() {
         return new ObjectIterator<Long2DoubleMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Long2DoubleArrayMap.this.size;
            }

            public Long2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractLong2DoubleMap.BasicEntry(Long2DoubleArrayMap.this.key[this.curr = this.next], Long2DoubleArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2DoubleArrayMap.this.key, this.next + 1, Long2DoubleArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2DoubleArrayMap.this.value, this.next + 1, Long2DoubleArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Long2DoubleMap.Entry> fastIterator() {
         return new ObjectIterator<Long2DoubleMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractLong2DoubleMap.BasicEntry entry = new AbstractLong2DoubleMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Long2DoubleArrayMap.this.size;
            }

            public Long2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Long2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Long2DoubleArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2DoubleArrayMap.this.key, this.next + 1, Long2DoubleArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2DoubleArrayMap.this.value, this.next + 1, Long2DoubleArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Long2DoubleArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Long) {
               if (var2.getValue() != null && var2.getValue() instanceof Double) {
                  long var3 = (Long)var2.getKey();
                  return Long2DoubleArrayMap.this.containsKey(var3) && Double.doubleToLongBits(Long2DoubleArrayMap.this.get(var3)) == Double.doubleToLongBits((Double)var2.getValue());
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
               if (var2.getValue() != null && var2.getValue() instanceof Double) {
                  long var3 = (Long)var2.getKey();
                  double var5 = (Double)var2.getValue();
                  int var7 = Long2DoubleArrayMap.this.findKey(var3);
                  if (var7 != -1 && Double.doubleToLongBits(var5) == Double.doubleToLongBits(Long2DoubleArrayMap.this.value[var7])) {
                     int var8 = Long2DoubleArrayMap.this.size - var7 - 1;
                     System.arraycopy(Long2DoubleArrayMap.this.key, var7 + 1, Long2DoubleArrayMap.this.key, var7, var8);
                     System.arraycopy(Long2DoubleArrayMap.this.value, var7 + 1, Long2DoubleArrayMap.this.value, var7, var8);
                     Long2DoubleArrayMap.this.size--;
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
