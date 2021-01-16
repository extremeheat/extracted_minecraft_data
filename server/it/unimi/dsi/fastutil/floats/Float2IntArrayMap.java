package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Float2IntArrayMap extends AbstractFloat2IntMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient float[] key;
   private transient int[] value;
   private int size;

   public Float2IntArrayMap(float[] var1, int[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Float2IntArrayMap() {
      super();
      this.key = FloatArrays.EMPTY_ARRAY;
      this.value = IntArrays.EMPTY_ARRAY;
   }

   public Float2IntArrayMap(int var1) {
      super();
      this.key = new float[var1];
      this.value = new int[var1];
   }

   public Float2IntArrayMap(Float2IntMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Float2IntArrayMap(Map<? extends Float, ? extends Integer> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Float2IntArrayMap(float[] var1, int[] var2, int var3) {
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

   public Float2IntMap.FastEntrySet float2IntEntrySet() {
      return new Float2IntArrayMap.EntrySet();
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

   public int get(float var1) {
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

   public int put(float var1, int var2) {
      int var3 = this.findKey(var1);
      if (var3 != -1) {
         int var7 = this.value[var3];
         this.value[var3] = var2;
         return var7;
      } else {
         if (this.size == this.key.length) {
            float[] var4 = new float[this.size == 0 ? 2 : this.size * 2];
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

   public int remove(float var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return this.defRetValue;
      } else {
         int var3 = this.value[var2];
         int var4 = this.size - var2 - 1;
         System.arraycopy(this.key, var2 + 1, this.key, var2, var4);
         System.arraycopy(this.value, var2 + 1, this.value, var2, var4);
         --this.size;
         return var3;
      }
   }

   public FloatSet keySet() {
      return new AbstractFloatSet() {
         public boolean contains(float var1) {
            return Float2IntArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(float var1) {
            int var2 = Float2IntArrayMap.this.findKey(var1);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = Float2IntArrayMap.this.size - var2 - 1;
               System.arraycopy(Float2IntArrayMap.this.key, var2 + 1, Float2IntArrayMap.this.key, var2, var3);
               System.arraycopy(Float2IntArrayMap.this.value, var2 + 1, Float2IntArrayMap.this.value, var2, var3);
               Float2IntArrayMap.this.size--;
               return true;
            }
         }

         public FloatIterator iterator() {
            return new FloatIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Float2IntArrayMap.this.size;
               }

               public float nextFloat() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Float2IntArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Float2IntArrayMap.this.size - this.pos;
                     System.arraycopy(Float2IntArrayMap.this.key, this.pos, Float2IntArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Float2IntArrayMap.this.value, this.pos, Float2IntArrayMap.this.value, this.pos - 1, var1);
                     Float2IntArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Float2IntArrayMap.this.size;
         }

         public void clear() {
            Float2IntArrayMap.this.clear();
         }
      };
   }

   public IntCollection values() {
      return new AbstractIntCollection() {
         public boolean contains(int var1) {
            return Float2IntArrayMap.this.containsValue(var1);
         }

         public IntIterator iterator() {
            return new IntIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Float2IntArrayMap.this.size;
               }

               public int nextInt() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Float2IntArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Float2IntArrayMap.this.size - this.pos;
                     System.arraycopy(Float2IntArrayMap.this.key, this.pos, Float2IntArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Float2IntArrayMap.this.value, this.pos, Float2IntArrayMap.this.value, this.pos - 1, var1);
                     Float2IntArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Float2IntArrayMap.this.size;
         }

         public void clear() {
            Float2IntArrayMap.this.clear();
         }
      };
   }

   public Float2IntArrayMap clone() {
      Float2IntArrayMap var1;
      try {
         var1 = (Float2IntArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (float[])this.key.clone();
      var1.value = (int[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeFloat(this.key[var2]);
         var1.writeInt(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new float[this.size];
      this.value = new int[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readFloat();
         this.value[var2] = var1.readInt();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Float2IntMap.Entry> implements Float2IntMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Float2IntMap.Entry> iterator() {
         return new ObjectIterator<Float2IntMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Float2IntArrayMap.this.size;
            }

            public Float2IntMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractFloat2IntMap.BasicEntry(Float2IntArrayMap.this.key[this.curr = this.next], Float2IntArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Float2IntArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2IntArrayMap.this.key, this.next + 1, Float2IntArrayMap.this.key, this.next, var1);
                  System.arraycopy(Float2IntArrayMap.this.value, this.next + 1, Float2IntArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Float2IntMap.Entry> fastIterator() {
         return new ObjectIterator<Float2IntMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractFloat2IntMap.BasicEntry entry = new AbstractFloat2IntMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Float2IntArrayMap.this.size;
            }

            public Float2IntMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Float2IntArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Float2IntArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Float2IntArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2IntArrayMap.this.key, this.next + 1, Float2IntArrayMap.this.key, this.next, var1);
                  System.arraycopy(Float2IntArrayMap.this.value, this.next + 1, Float2IntArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Float2IntArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Float) {
               if (var2.getValue() != null && var2.getValue() instanceof Integer) {
                  float var3 = (Float)var2.getKey();
                  return Float2IntArrayMap.this.containsKey(var3) && Float2IntArrayMap.this.get(var3) == (Integer)var2.getValue();
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
               if (var2.getValue() != null && var2.getValue() instanceof Integer) {
                  float var3 = (Float)var2.getKey();
                  int var4 = (Integer)var2.getValue();
                  int var5 = Float2IntArrayMap.this.findKey(var3);
                  if (var5 != -1 && var4 == Float2IntArrayMap.this.value[var5]) {
                     int var6 = Float2IntArrayMap.this.size - var5 - 1;
                     System.arraycopy(Float2IntArrayMap.this.key, var5 + 1, Float2IntArrayMap.this.key, var5, var6);
                     System.arraycopy(Float2IntArrayMap.this.value, var5 + 1, Float2IntArrayMap.this.value, var5, var6);
                     Float2IntArrayMap.this.size--;
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
