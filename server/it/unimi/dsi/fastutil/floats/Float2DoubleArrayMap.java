package it.unimi.dsi.fastutil.floats;

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

public class Float2DoubleArrayMap extends AbstractFloat2DoubleMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient float[] key;
   private transient double[] value;
   private int size;

   public Float2DoubleArrayMap(float[] var1, double[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Float2DoubleArrayMap() {
      super();
      this.key = FloatArrays.EMPTY_ARRAY;
      this.value = DoubleArrays.EMPTY_ARRAY;
   }

   public Float2DoubleArrayMap(int var1) {
      super();
      this.key = new float[var1];
      this.value = new double[var1];
   }

   public Float2DoubleArrayMap(Float2DoubleMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Float2DoubleArrayMap(Map<? extends Float, ? extends Double> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Float2DoubleArrayMap(float[] var1, double[] var2, int var3) {
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

   public Float2DoubleMap.FastEntrySet float2DoubleEntrySet() {
      return new Float2DoubleArrayMap.EntrySet();
   }

   private int findKey(float var1) {
      float[] var2 = this.key;
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return -1;
         }
      } while(Float.floatToIntBits(var2[var3]) != Float.floatToIntBits(var1));

      return var3;
   }

   public double get(float var1) {
      float[] var2 = this.key;
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return this.defRetValue;
         }
      } while(Float.floatToIntBits(var2[var3]) != Float.floatToIntBits(var1));

      return this.value[var3];
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      this.size = 0;
   }

   public boolean containsKey(float var1) {
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

   public double put(float var1, double var2) {
      int var4 = this.findKey(var1);
      if (var4 != -1) {
         double var8 = this.value[var4];
         this.value[var4] = var2;
         return var8;
      } else {
         if (this.size == this.key.length) {
            float[] var5 = new float[this.size == 0 ? 2 : this.size * 2];
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

   public double remove(float var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return this.defRetValue;
      } else {
         double var3 = this.value[var2];
         int var5 = this.size - var2 - 1;
         System.arraycopy(this.key, var2 + 1, this.key, var2, var5);
         System.arraycopy(this.value, var2 + 1, this.value, var2, var5);
         --this.size;
         return var3;
      }
   }

   public FloatSet keySet() {
      return new AbstractFloatSet() {
         public boolean contains(float var1) {
            return Float2DoubleArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(float var1) {
            int var2 = Float2DoubleArrayMap.this.findKey(var1);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = Float2DoubleArrayMap.this.size - var2 - 1;
               System.arraycopy(Float2DoubleArrayMap.this.key, var2 + 1, Float2DoubleArrayMap.this.key, var2, var3);
               System.arraycopy(Float2DoubleArrayMap.this.value, var2 + 1, Float2DoubleArrayMap.this.value, var2, var3);
               Float2DoubleArrayMap.this.size--;
               return true;
            }
         }

         public FloatIterator iterator() {
            return new FloatIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Float2DoubleArrayMap.this.size;
               }

               public float nextFloat() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Float2DoubleArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Float2DoubleArrayMap.this.size - this.pos;
                     System.arraycopy(Float2DoubleArrayMap.this.key, this.pos, Float2DoubleArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Float2DoubleArrayMap.this.value, this.pos, Float2DoubleArrayMap.this.value, this.pos - 1, var1);
                     Float2DoubleArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Float2DoubleArrayMap.this.size;
         }

         public void clear() {
            Float2DoubleArrayMap.this.clear();
         }
      };
   }

   public DoubleCollection values() {
      return new AbstractDoubleCollection() {
         public boolean contains(double var1) {
            return Float2DoubleArrayMap.this.containsValue(var1);
         }

         public DoubleIterator iterator() {
            return new DoubleIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Float2DoubleArrayMap.this.size;
               }

               public double nextDouble() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Float2DoubleArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Float2DoubleArrayMap.this.size - this.pos;
                     System.arraycopy(Float2DoubleArrayMap.this.key, this.pos, Float2DoubleArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Float2DoubleArrayMap.this.value, this.pos, Float2DoubleArrayMap.this.value, this.pos - 1, var1);
                     Float2DoubleArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Float2DoubleArrayMap.this.size;
         }

         public void clear() {
            Float2DoubleArrayMap.this.clear();
         }
      };
   }

   public Float2DoubleArrayMap clone() {
      Float2DoubleArrayMap var1;
      try {
         var1 = (Float2DoubleArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (float[])this.key.clone();
      var1.value = (double[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeFloat(this.key[var2]);
         var1.writeDouble(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new float[this.size];
      this.value = new double[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readFloat();
         this.value[var2] = var1.readDouble();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Float2DoubleMap.Entry> implements Float2DoubleMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Float2DoubleMap.Entry> iterator() {
         return new ObjectIterator<Float2DoubleMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Float2DoubleArrayMap.this.size;
            }

            public Float2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractFloat2DoubleMap.BasicEntry(Float2DoubleArrayMap.this.key[this.curr = this.next], Float2DoubleArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Float2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2DoubleArrayMap.this.key, this.next + 1, Float2DoubleArrayMap.this.key, this.next, var1);
                  System.arraycopy(Float2DoubleArrayMap.this.value, this.next + 1, Float2DoubleArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Float2DoubleMap.Entry> fastIterator() {
         return new ObjectIterator<Float2DoubleMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractFloat2DoubleMap.BasicEntry entry = new AbstractFloat2DoubleMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Float2DoubleArrayMap.this.size;
            }

            public Float2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Float2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Float2DoubleArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Float2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2DoubleArrayMap.this.key, this.next + 1, Float2DoubleArrayMap.this.key, this.next, var1);
                  System.arraycopy(Float2DoubleArrayMap.this.value, this.next + 1, Float2DoubleArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Float2DoubleArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Float) {
               if (var2.getValue() != null && var2.getValue() instanceof Double) {
                  float var3 = (Float)var2.getKey();
                  return Float2DoubleArrayMap.this.containsKey(var3) && Double.doubleToLongBits(Float2DoubleArrayMap.this.get(var3)) == Double.doubleToLongBits((Double)var2.getValue());
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
            if (var2.getKey() != null && var2.getKey() instanceof Float) {
               if (var2.getValue() != null && var2.getValue() instanceof Double) {
                  float var3 = (Float)var2.getKey();
                  double var4 = (Double)var2.getValue();
                  int var6 = Float2DoubleArrayMap.this.findKey(var3);
                  if (var6 != -1 && Double.doubleToLongBits(var4) == Double.doubleToLongBits(Float2DoubleArrayMap.this.value[var6])) {
                     int var7 = Float2DoubleArrayMap.this.size - var6 - 1;
                     System.arraycopy(Float2DoubleArrayMap.this.key, var6 + 1, Float2DoubleArrayMap.this.key, var6, var7);
                     System.arraycopy(Float2DoubleArrayMap.this.value, var6 + 1, Float2DoubleArrayMap.this.value, var6, var7);
                     Float2DoubleArrayMap.this.size--;
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
