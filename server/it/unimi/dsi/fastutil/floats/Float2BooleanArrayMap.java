package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Float2BooleanArrayMap extends AbstractFloat2BooleanMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient float[] key;
   private transient boolean[] value;
   private int size;

   public Float2BooleanArrayMap(float[] var1, boolean[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Float2BooleanArrayMap() {
      super();
      this.key = FloatArrays.EMPTY_ARRAY;
      this.value = BooleanArrays.EMPTY_ARRAY;
   }

   public Float2BooleanArrayMap(int var1) {
      super();
      this.key = new float[var1];
      this.value = new boolean[var1];
   }

   public Float2BooleanArrayMap(Float2BooleanMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Float2BooleanArrayMap(Map<? extends Float, ? extends Boolean> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Float2BooleanArrayMap(float[] var1, boolean[] var2, int var3) {
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

   public Float2BooleanMap.FastEntrySet float2BooleanEntrySet() {
      return new Float2BooleanArrayMap.EntrySet();
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

   public boolean get(float var1) {
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

   public boolean containsValue(boolean var1) {
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

   public boolean put(float var1, boolean var2) {
      int var3 = this.findKey(var1);
      if (var3 != -1) {
         boolean var7 = this.value[var3];
         this.value[var3] = var2;
         return var7;
      } else {
         if (this.size == this.key.length) {
            float[] var4 = new float[this.size == 0 ? 2 : this.size * 2];
            boolean[] var5 = new boolean[this.size == 0 ? 2 : this.size * 2];

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

   public boolean remove(float var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return this.defRetValue;
      } else {
         boolean var3 = this.value[var2];
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
            return Float2BooleanArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(float var1) {
            int var2 = Float2BooleanArrayMap.this.findKey(var1);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = Float2BooleanArrayMap.this.size - var2 - 1;
               System.arraycopy(Float2BooleanArrayMap.this.key, var2 + 1, Float2BooleanArrayMap.this.key, var2, var3);
               System.arraycopy(Float2BooleanArrayMap.this.value, var2 + 1, Float2BooleanArrayMap.this.value, var2, var3);
               Float2BooleanArrayMap.this.size--;
               return true;
            }
         }

         public FloatIterator iterator() {
            return new FloatIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Float2BooleanArrayMap.this.size;
               }

               public float nextFloat() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Float2BooleanArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Float2BooleanArrayMap.this.size - this.pos;
                     System.arraycopy(Float2BooleanArrayMap.this.key, this.pos, Float2BooleanArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Float2BooleanArrayMap.this.value, this.pos, Float2BooleanArrayMap.this.value, this.pos - 1, var1);
                     Float2BooleanArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Float2BooleanArrayMap.this.size;
         }

         public void clear() {
            Float2BooleanArrayMap.this.clear();
         }
      };
   }

   public BooleanCollection values() {
      return new AbstractBooleanCollection() {
         public boolean contains(boolean var1) {
            return Float2BooleanArrayMap.this.containsValue(var1);
         }

         public BooleanIterator iterator() {
            return new BooleanIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Float2BooleanArrayMap.this.size;
               }

               public boolean nextBoolean() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Float2BooleanArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Float2BooleanArrayMap.this.size - this.pos;
                     System.arraycopy(Float2BooleanArrayMap.this.key, this.pos, Float2BooleanArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Float2BooleanArrayMap.this.value, this.pos, Float2BooleanArrayMap.this.value, this.pos - 1, var1);
                     Float2BooleanArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Float2BooleanArrayMap.this.size;
         }

         public void clear() {
            Float2BooleanArrayMap.this.clear();
         }
      };
   }

   public Float2BooleanArrayMap clone() {
      Float2BooleanArrayMap var1;
      try {
         var1 = (Float2BooleanArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (float[])this.key.clone();
      var1.value = (boolean[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeFloat(this.key[var2]);
         var1.writeBoolean(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new float[this.size];
      this.value = new boolean[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readFloat();
         this.value[var2] = var1.readBoolean();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Float2BooleanMap.Entry> implements Float2BooleanMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Float2BooleanMap.Entry> iterator() {
         return new ObjectIterator<Float2BooleanMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Float2BooleanArrayMap.this.size;
            }

            public Float2BooleanMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractFloat2BooleanMap.BasicEntry(Float2BooleanArrayMap.this.key[this.curr = this.next], Float2BooleanArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Float2BooleanArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2BooleanArrayMap.this.key, this.next + 1, Float2BooleanArrayMap.this.key, this.next, var1);
                  System.arraycopy(Float2BooleanArrayMap.this.value, this.next + 1, Float2BooleanArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Float2BooleanMap.Entry> fastIterator() {
         return new ObjectIterator<Float2BooleanMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractFloat2BooleanMap.BasicEntry entry = new AbstractFloat2BooleanMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Float2BooleanArrayMap.this.size;
            }

            public Float2BooleanMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Float2BooleanArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Float2BooleanArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Float2BooleanArrayMap.this.size-- - this.next--;
                  System.arraycopy(Float2BooleanArrayMap.this.key, this.next + 1, Float2BooleanArrayMap.this.key, this.next, var1);
                  System.arraycopy(Float2BooleanArrayMap.this.value, this.next + 1, Float2BooleanArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Float2BooleanArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Float) {
               if (var2.getValue() != null && var2.getValue() instanceof Boolean) {
                  float var3 = (Float)var2.getKey();
                  return Float2BooleanArrayMap.this.containsKey(var3) && Float2BooleanArrayMap.this.get(var3) == (Boolean)var2.getValue();
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
               if (var2.getValue() != null && var2.getValue() instanceof Boolean) {
                  float var3 = (Float)var2.getKey();
                  boolean var4 = (Boolean)var2.getValue();
                  int var5 = Float2BooleanArrayMap.this.findKey(var3);
                  if (var5 != -1 && var4 == Float2BooleanArrayMap.this.value[var5]) {
                     int var6 = Float2BooleanArrayMap.this.size - var5 - 1;
                     System.arraycopy(Float2BooleanArrayMap.this.key, var5 + 1, Float2BooleanArrayMap.this.key, var5, var6);
                     System.arraycopy(Float2BooleanArrayMap.this.value, var5 + 1, Float2BooleanArrayMap.this.value, var5, var6);
                     Float2BooleanArrayMap.this.size--;
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
