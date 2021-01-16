package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Double2ByteArrayMap extends AbstractDouble2ByteMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient double[] key;
   private transient byte[] value;
   private int size;

   public Double2ByteArrayMap(double[] var1, byte[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Double2ByteArrayMap() {
      super();
      this.key = DoubleArrays.EMPTY_ARRAY;
      this.value = ByteArrays.EMPTY_ARRAY;
   }

   public Double2ByteArrayMap(int var1) {
      super();
      this.key = new double[var1];
      this.value = new byte[var1];
   }

   public Double2ByteArrayMap(Double2ByteMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Double2ByteArrayMap(Map<? extends Double, ? extends Byte> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Double2ByteArrayMap(double[] var1, byte[] var2, int var3) {
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

   public Double2ByteMap.FastEntrySet double2ByteEntrySet() {
      return new Double2ByteArrayMap.EntrySet();
   }

   private int findKey(double var1) {
      double[] var3 = this.key;
      int var4 = this.size;

      do {
         if (var4-- == 0) {
            return -1;
         }
      } while(Double.doubleToLongBits(var3[var4]) != Double.doubleToLongBits(var1));

      return var4;
   }

   public byte get(double var1) {
      double[] var3 = this.key;
      int var4 = this.size;

      do {
         if (var4-- == 0) {
            return this.defRetValue;
         }
      } while(Double.doubleToLongBits(var3[var4]) != Double.doubleToLongBits(var1));

      return this.value[var4];
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      this.size = 0;
   }

   public boolean containsKey(double var1) {
      return this.findKey(var1) != -1;
   }

   public boolean containsValue(byte var1) {
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

   public byte put(double var1, byte var3) {
      int var4 = this.findKey(var1);
      if (var4 != -1) {
         byte var8 = this.value[var4];
         this.value[var4] = var3;
         return var8;
      } else {
         if (this.size == this.key.length) {
            double[] var5 = new double[this.size == 0 ? 2 : this.size * 2];
            byte[] var6 = new byte[this.size == 0 ? 2 : this.size * 2];

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

   public byte remove(double var1) {
      int var3 = this.findKey(var1);
      if (var3 == -1) {
         return this.defRetValue;
      } else {
         byte var4 = this.value[var3];
         int var5 = this.size - var3 - 1;
         System.arraycopy(this.key, var3 + 1, this.key, var3, var5);
         System.arraycopy(this.value, var3 + 1, this.value, var3, var5);
         --this.size;
         return var4;
      }
   }

   public DoubleSet keySet() {
      return new AbstractDoubleSet() {
         public boolean contains(double var1) {
            return Double2ByteArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(double var1) {
            int var3 = Double2ByteArrayMap.this.findKey(var1);
            if (var3 == -1) {
               return false;
            } else {
               int var4 = Double2ByteArrayMap.this.size - var3 - 1;
               System.arraycopy(Double2ByteArrayMap.this.key, var3 + 1, Double2ByteArrayMap.this.key, var3, var4);
               System.arraycopy(Double2ByteArrayMap.this.value, var3 + 1, Double2ByteArrayMap.this.value, var3, var4);
               Double2ByteArrayMap.this.size--;
               return true;
            }
         }

         public DoubleIterator iterator() {
            return new DoubleIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Double2ByteArrayMap.this.size;
               }

               public double nextDouble() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Double2ByteArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Double2ByteArrayMap.this.size - this.pos;
                     System.arraycopy(Double2ByteArrayMap.this.key, this.pos, Double2ByteArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Double2ByteArrayMap.this.value, this.pos, Double2ByteArrayMap.this.value, this.pos - 1, var1);
                     Double2ByteArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Double2ByteArrayMap.this.size;
         }

         public void clear() {
            Double2ByteArrayMap.this.clear();
         }
      };
   }

   public ByteCollection values() {
      return new AbstractByteCollection() {
         public boolean contains(byte var1) {
            return Double2ByteArrayMap.this.containsValue(var1);
         }

         public ByteIterator iterator() {
            return new ByteIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Double2ByteArrayMap.this.size;
               }

               public byte nextByte() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Double2ByteArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Double2ByteArrayMap.this.size - this.pos;
                     System.arraycopy(Double2ByteArrayMap.this.key, this.pos, Double2ByteArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Double2ByteArrayMap.this.value, this.pos, Double2ByteArrayMap.this.value, this.pos - 1, var1);
                     Double2ByteArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Double2ByteArrayMap.this.size;
         }

         public void clear() {
            Double2ByteArrayMap.this.clear();
         }
      };
   }

   public Double2ByteArrayMap clone() {
      Double2ByteArrayMap var1;
      try {
         var1 = (Double2ByteArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (double[])this.key.clone();
      var1.value = (byte[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeDouble(this.key[var2]);
         var1.writeByte(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new double[this.size];
      this.value = new byte[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readDouble();
         this.value[var2] = var1.readByte();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Double2ByteMap.Entry> implements Double2ByteMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Double2ByteMap.Entry> iterator() {
         return new ObjectIterator<Double2ByteMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Double2ByteArrayMap.this.size;
            }

            public Double2ByteMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractDouble2ByteMap.BasicEntry(Double2ByteArrayMap.this.key[this.curr = this.next], Double2ByteArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Double2ByteArrayMap.this.size-- - this.next--;
                  System.arraycopy(Double2ByteArrayMap.this.key, this.next + 1, Double2ByteArrayMap.this.key, this.next, var1);
                  System.arraycopy(Double2ByteArrayMap.this.value, this.next + 1, Double2ByteArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Double2ByteMap.Entry> fastIterator() {
         return new ObjectIterator<Double2ByteMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractDouble2ByteMap.BasicEntry entry = new AbstractDouble2ByteMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Double2ByteArrayMap.this.size;
            }

            public Double2ByteMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Double2ByteArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Double2ByteArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Double2ByteArrayMap.this.size-- - this.next--;
                  System.arraycopy(Double2ByteArrayMap.this.key, this.next + 1, Double2ByteArrayMap.this.key, this.next, var1);
                  System.arraycopy(Double2ByteArrayMap.this.value, this.next + 1, Double2ByteArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Double2ByteArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Double) {
               if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                  double var3 = (Double)var2.getKey();
                  return Double2ByteArrayMap.this.containsKey(var3) && Double2ByteArrayMap.this.get(var3) == (Byte)var2.getValue();
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
            if (var2.getKey() != null && var2.getKey() instanceof Double) {
               if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                  double var3 = (Double)var2.getKey();
                  byte var5 = (Byte)var2.getValue();
                  int var6 = Double2ByteArrayMap.this.findKey(var3);
                  if (var6 != -1 && var5 == Double2ByteArrayMap.this.value[var6]) {
                     int var7 = Double2ByteArrayMap.this.size - var6 - 1;
                     System.arraycopy(Double2ByteArrayMap.this.key, var6 + 1, Double2ByteArrayMap.this.key, var6, var7);
                     System.arraycopy(Double2ByteArrayMap.this.value, var6 + 1, Double2ByteArrayMap.this.value, var6, var7);
                     Double2ByteArrayMap.this.size--;
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
