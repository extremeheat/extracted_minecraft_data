package it.unimi.dsi.fastutil.shorts;

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

public class Short2FloatArrayMap extends AbstractShort2FloatMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient short[] key;
   private transient float[] value;
   private int size;

   public Short2FloatArrayMap(short[] var1, float[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Short2FloatArrayMap() {
      super();
      this.key = ShortArrays.EMPTY_ARRAY;
      this.value = FloatArrays.EMPTY_ARRAY;
   }

   public Short2FloatArrayMap(int var1) {
      super();
      this.key = new short[var1];
      this.value = new float[var1];
   }

   public Short2FloatArrayMap(Short2FloatMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Short2FloatArrayMap(Map<? extends Short, ? extends Float> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Short2FloatArrayMap(short[] var1, float[] var2, int var3) {
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

   public Short2FloatMap.FastEntrySet short2FloatEntrySet() {
      return new Short2FloatArrayMap.EntrySet();
   }

   private int findKey(short var1) {
      short[] var2 = this.key;
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return -1;
         }
      } while(var2[var3] != var1);

      return var3;
   }

   public float get(short var1) {
      short[] var2 = this.key;
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

   public boolean containsKey(short var1) {
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

   public float put(short var1, float var2) {
      int var3 = this.findKey(var1);
      if (var3 != -1) {
         float var7 = this.value[var3];
         this.value[var3] = var2;
         return var7;
      } else {
         if (this.size == this.key.length) {
            short[] var4 = new short[this.size == 0 ? 2 : this.size * 2];
            float[] var5 = new float[this.size == 0 ? 2 : this.size * 2];

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

   public float remove(short var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return this.defRetValue;
      } else {
         float var3 = this.value[var2];
         int var4 = this.size - var2 - 1;
         System.arraycopy(this.key, var2 + 1, this.key, var2, var4);
         System.arraycopy(this.value, var2 + 1, this.value, var2, var4);
         --this.size;
         return var3;
      }
   }

   public ShortSet keySet() {
      return new AbstractShortSet() {
         public boolean contains(short var1) {
            return Short2FloatArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(short var1) {
            int var2 = Short2FloatArrayMap.this.findKey(var1);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = Short2FloatArrayMap.this.size - var2 - 1;
               System.arraycopy(Short2FloatArrayMap.this.key, var2 + 1, Short2FloatArrayMap.this.key, var2, var3);
               System.arraycopy(Short2FloatArrayMap.this.value, var2 + 1, Short2FloatArrayMap.this.value, var2, var3);
               Short2FloatArrayMap.this.size--;
               return true;
            }
         }

         public ShortIterator iterator() {
            return new ShortIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Short2FloatArrayMap.this.size;
               }

               public short nextShort() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Short2FloatArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Short2FloatArrayMap.this.size - this.pos;
                     System.arraycopy(Short2FloatArrayMap.this.key, this.pos, Short2FloatArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Short2FloatArrayMap.this.value, this.pos, Short2FloatArrayMap.this.value, this.pos - 1, var1);
                     Short2FloatArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Short2FloatArrayMap.this.size;
         }

         public void clear() {
            Short2FloatArrayMap.this.clear();
         }
      };
   }

   public FloatCollection values() {
      return new AbstractFloatCollection() {
         public boolean contains(float var1) {
            return Short2FloatArrayMap.this.containsValue(var1);
         }

         public FloatIterator iterator() {
            return new FloatIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Short2FloatArrayMap.this.size;
               }

               public float nextFloat() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Short2FloatArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Short2FloatArrayMap.this.size - this.pos;
                     System.arraycopy(Short2FloatArrayMap.this.key, this.pos, Short2FloatArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Short2FloatArrayMap.this.value, this.pos, Short2FloatArrayMap.this.value, this.pos - 1, var1);
                     Short2FloatArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Short2FloatArrayMap.this.size;
         }

         public void clear() {
            Short2FloatArrayMap.this.clear();
         }
      };
   }

   public Short2FloatArrayMap clone() {
      Short2FloatArrayMap var1;
      try {
         var1 = (Short2FloatArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (short[])this.key.clone();
      var1.value = (float[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeShort(this.key[var2]);
         var1.writeFloat(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new short[this.size];
      this.value = new float[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readShort();
         this.value[var2] = var1.readFloat();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Short2FloatMap.Entry> implements Short2FloatMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Short2FloatMap.Entry> iterator() {
         return new ObjectIterator<Short2FloatMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Short2FloatArrayMap.this.size;
            }

            public Short2FloatMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractShort2FloatMap.BasicEntry(Short2FloatArrayMap.this.key[this.curr = this.next], Short2FloatArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Short2FloatArrayMap.this.size-- - this.next--;
                  System.arraycopy(Short2FloatArrayMap.this.key, this.next + 1, Short2FloatArrayMap.this.key, this.next, var1);
                  System.arraycopy(Short2FloatArrayMap.this.value, this.next + 1, Short2FloatArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Short2FloatMap.Entry> fastIterator() {
         return new ObjectIterator<Short2FloatMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractShort2FloatMap.BasicEntry entry = new AbstractShort2FloatMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Short2FloatArrayMap.this.size;
            }

            public Short2FloatMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Short2FloatArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Short2FloatArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Short2FloatArrayMap.this.size-- - this.next--;
                  System.arraycopy(Short2FloatArrayMap.this.key, this.next + 1, Short2FloatArrayMap.this.key, this.next, var1);
                  System.arraycopy(Short2FloatArrayMap.this.value, this.next + 1, Short2FloatArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Short2FloatArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Short) {
               if (var2.getValue() != null && var2.getValue() instanceof Float) {
                  short var3 = (Short)var2.getKey();
                  return Short2FloatArrayMap.this.containsKey(var3) && Float.floatToIntBits(Short2FloatArrayMap.this.get(var3)) == Float.floatToIntBits((Float)var2.getValue());
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
            if (var2.getKey() != null && var2.getKey() instanceof Short) {
               if (var2.getValue() != null && var2.getValue() instanceof Float) {
                  short var3 = (Short)var2.getKey();
                  float var4 = (Float)var2.getValue();
                  int var5 = Short2FloatArrayMap.this.findKey(var3);
                  if (var5 != -1 && Float.floatToIntBits(var4) == Float.floatToIntBits(Short2FloatArrayMap.this.value[var5])) {
                     int var6 = Short2FloatArrayMap.this.size - var5 - 1;
                     System.arraycopy(Short2FloatArrayMap.this.key, var5 + 1, Short2FloatArrayMap.this.key, var5, var6);
                     System.arraycopy(Short2FloatArrayMap.this.value, var5 + 1, Short2FloatArrayMap.this.value, var5, var6);
                     Short2FloatArrayMap.this.size--;
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
