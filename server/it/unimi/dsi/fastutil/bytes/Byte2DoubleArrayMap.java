package it.unimi.dsi.fastutil.bytes;

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

public class Byte2DoubleArrayMap extends AbstractByte2DoubleMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient byte[] key;
   private transient double[] value;
   private int size;

   public Byte2DoubleArrayMap(byte[] var1, double[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Byte2DoubleArrayMap() {
      super();
      this.key = ByteArrays.EMPTY_ARRAY;
      this.value = DoubleArrays.EMPTY_ARRAY;
   }

   public Byte2DoubleArrayMap(int var1) {
      super();
      this.key = new byte[var1];
      this.value = new double[var1];
   }

   public Byte2DoubleArrayMap(Byte2DoubleMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Byte2DoubleArrayMap(Map<? extends Byte, ? extends Double> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Byte2DoubleArrayMap(byte[] var1, double[] var2, int var3) {
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

   public Byte2DoubleMap.FastEntrySet byte2DoubleEntrySet() {
      return new Byte2DoubleArrayMap.EntrySet();
   }

   private int findKey(byte var1) {
      byte[] var2 = this.key;
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return -1;
         }
      } while(var2[var3] != var1);

      return var3;
   }

   public double get(byte var1) {
      byte[] var2 = this.key;
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return this.defRetValue;
         }
      } while(var2[var3] != var1);

      return this.value[var3];
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      this.size = 0;
   }

   public boolean containsKey(byte var1) {
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

   public double put(byte var1, double var2) {
      int var4 = this.findKey(var1);
      if (var4 != -1) {
         double var8 = this.value[var4];
         this.value[var4] = var2;
         return var8;
      } else {
         if (this.size == this.key.length) {
            byte[] var5 = new byte[this.size == 0 ? 2 : this.size * 2];
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

   public double remove(byte var1) {
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

   public ByteSet keySet() {
      return new AbstractByteSet() {
         public boolean contains(byte var1) {
            return Byte2DoubleArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(byte var1) {
            int var2 = Byte2DoubleArrayMap.this.findKey(var1);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = Byte2DoubleArrayMap.this.size - var2 - 1;
               System.arraycopy(Byte2DoubleArrayMap.this.key, var2 + 1, Byte2DoubleArrayMap.this.key, var2, var3);
               System.arraycopy(Byte2DoubleArrayMap.this.value, var2 + 1, Byte2DoubleArrayMap.this.value, var2, var3);
               Byte2DoubleArrayMap.this.size--;
               return true;
            }
         }

         public ByteIterator iterator() {
            return new ByteIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Byte2DoubleArrayMap.this.size;
               }

               public byte nextByte() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Byte2DoubleArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Byte2DoubleArrayMap.this.size - this.pos;
                     System.arraycopy(Byte2DoubleArrayMap.this.key, this.pos, Byte2DoubleArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Byte2DoubleArrayMap.this.value, this.pos, Byte2DoubleArrayMap.this.value, this.pos - 1, var1);
                     Byte2DoubleArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Byte2DoubleArrayMap.this.size;
         }

         public void clear() {
            Byte2DoubleArrayMap.this.clear();
         }
      };
   }

   public DoubleCollection values() {
      return new AbstractDoubleCollection() {
         public boolean contains(double var1) {
            return Byte2DoubleArrayMap.this.containsValue(var1);
         }

         public DoubleIterator iterator() {
            return new DoubleIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Byte2DoubleArrayMap.this.size;
               }

               public double nextDouble() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Byte2DoubleArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Byte2DoubleArrayMap.this.size - this.pos;
                     System.arraycopy(Byte2DoubleArrayMap.this.key, this.pos, Byte2DoubleArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Byte2DoubleArrayMap.this.value, this.pos, Byte2DoubleArrayMap.this.value, this.pos - 1, var1);
                     Byte2DoubleArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Byte2DoubleArrayMap.this.size;
         }

         public void clear() {
            Byte2DoubleArrayMap.this.clear();
         }
      };
   }

   public Byte2DoubleArrayMap clone() {
      Byte2DoubleArrayMap var1;
      try {
         var1 = (Byte2DoubleArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (byte[])this.key.clone();
      var1.value = (double[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeByte(this.key[var2]);
         var1.writeDouble(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new byte[this.size];
      this.value = new double[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readByte();
         this.value[var2] = var1.readDouble();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Byte2DoubleMap.Entry> implements Byte2DoubleMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Byte2DoubleMap.Entry> iterator() {
         return new ObjectIterator<Byte2DoubleMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Byte2DoubleArrayMap.this.size;
            }

            public Byte2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractByte2DoubleMap.BasicEntry(Byte2DoubleArrayMap.this.key[this.curr = this.next], Byte2DoubleArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Byte2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Byte2DoubleArrayMap.this.key, this.next + 1, Byte2DoubleArrayMap.this.key, this.next, var1);
                  System.arraycopy(Byte2DoubleArrayMap.this.value, this.next + 1, Byte2DoubleArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Byte2DoubleMap.Entry> fastIterator() {
         return new ObjectIterator<Byte2DoubleMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractByte2DoubleMap.BasicEntry entry = new AbstractByte2DoubleMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Byte2DoubleArrayMap.this.size;
            }

            public Byte2DoubleMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Byte2DoubleArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Byte2DoubleArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Byte2DoubleArrayMap.this.size-- - this.next--;
                  System.arraycopy(Byte2DoubleArrayMap.this.key, this.next + 1, Byte2DoubleArrayMap.this.key, this.next, var1);
                  System.arraycopy(Byte2DoubleArrayMap.this.value, this.next + 1, Byte2DoubleArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Byte2DoubleArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Byte) {
               if (var2.getValue() != null && var2.getValue() instanceof Double) {
                  byte var3 = (Byte)var2.getKey();
                  return Byte2DoubleArrayMap.this.containsKey(var3) && Double.doubleToLongBits(Byte2DoubleArrayMap.this.get(var3)) == Double.doubleToLongBits((Double)var2.getValue());
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
            if (var2.getKey() != null && var2.getKey() instanceof Byte) {
               if (var2.getValue() != null && var2.getValue() instanceof Double) {
                  byte var3 = (Byte)var2.getKey();
                  double var4 = (Double)var2.getValue();
                  int var6 = Byte2DoubleArrayMap.this.findKey(var3);
                  if (var6 != -1 && Double.doubleToLongBits(var4) == Double.doubleToLongBits(Byte2DoubleArrayMap.this.value[var6])) {
                     int var7 = Byte2DoubleArrayMap.this.size - var6 - 1;
                     System.arraycopy(Byte2DoubleArrayMap.this.key, var6 + 1, Byte2DoubleArrayMap.this.key, var6, var7);
                     System.arraycopy(Byte2DoubleArrayMap.this.value, var6 + 1, Byte2DoubleArrayMap.this.value, var6, var7);
                     Byte2DoubleArrayMap.this.size--;
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
