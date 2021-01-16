package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Short2ShortArrayMap extends AbstractShort2ShortMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient short[] key;
   private transient short[] value;
   private int size;

   public Short2ShortArrayMap(short[] var1, short[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Short2ShortArrayMap() {
      super();
      this.key = ShortArrays.EMPTY_ARRAY;
      this.value = ShortArrays.EMPTY_ARRAY;
   }

   public Short2ShortArrayMap(int var1) {
      super();
      this.key = new short[var1];
      this.value = new short[var1];
   }

   public Short2ShortArrayMap(Short2ShortMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Short2ShortArrayMap(Map<? extends Short, ? extends Short> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Short2ShortArrayMap(short[] var1, short[] var2, int var3) {
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

   public Short2ShortMap.FastEntrySet short2ShortEntrySet() {
      return new Short2ShortArrayMap.EntrySet();
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

   public short get(short var1) {
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

   public short put(short var1, short var2) {
      int var3 = this.findKey(var1);
      if (var3 != -1) {
         short var7 = this.value[var3];
         this.value[var3] = var2;
         return var7;
      } else {
         if (this.size == this.key.length) {
            short[] var4 = new short[this.size == 0 ? 2 : this.size * 2];
            short[] var5 = new short[this.size == 0 ? 2 : this.size * 2];

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

   public short remove(short var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return this.defRetValue;
      } else {
         short var3 = this.value[var2];
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
            return Short2ShortArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(short var1) {
            int var2 = Short2ShortArrayMap.this.findKey(var1);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = Short2ShortArrayMap.this.size - var2 - 1;
               System.arraycopy(Short2ShortArrayMap.this.key, var2 + 1, Short2ShortArrayMap.this.key, var2, var3);
               System.arraycopy(Short2ShortArrayMap.this.value, var2 + 1, Short2ShortArrayMap.this.value, var2, var3);
               Short2ShortArrayMap.this.size--;
               return true;
            }
         }

         public ShortIterator iterator() {
            return new ShortIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Short2ShortArrayMap.this.size;
               }

               public short nextShort() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Short2ShortArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Short2ShortArrayMap.this.size - this.pos;
                     System.arraycopy(Short2ShortArrayMap.this.key, this.pos, Short2ShortArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Short2ShortArrayMap.this.value, this.pos, Short2ShortArrayMap.this.value, this.pos - 1, var1);
                     Short2ShortArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Short2ShortArrayMap.this.size;
         }

         public void clear() {
            Short2ShortArrayMap.this.clear();
         }
      };
   }

   public ShortCollection values() {
      return new AbstractShortCollection() {
         public boolean contains(short var1) {
            return Short2ShortArrayMap.this.containsValue(var1);
         }

         public ShortIterator iterator() {
            return new ShortIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Short2ShortArrayMap.this.size;
               }

               public short nextShort() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Short2ShortArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Short2ShortArrayMap.this.size - this.pos;
                     System.arraycopy(Short2ShortArrayMap.this.key, this.pos, Short2ShortArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Short2ShortArrayMap.this.value, this.pos, Short2ShortArrayMap.this.value, this.pos - 1, var1);
                     Short2ShortArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Short2ShortArrayMap.this.size;
         }

         public void clear() {
            Short2ShortArrayMap.this.clear();
         }
      };
   }

   public Short2ShortArrayMap clone() {
      Short2ShortArrayMap var1;
      try {
         var1 = (Short2ShortArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (short[])this.key.clone();
      var1.value = (short[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeShort(this.key[var2]);
         var1.writeShort(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new short[this.size];
      this.value = new short[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readShort();
         this.value[var2] = var1.readShort();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Short2ShortMap.Entry> implements Short2ShortMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Short2ShortMap.Entry> iterator() {
         return new ObjectIterator<Short2ShortMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Short2ShortArrayMap.this.size;
            }

            public Short2ShortMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractShort2ShortMap.BasicEntry(Short2ShortArrayMap.this.key[this.curr = this.next], Short2ShortArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Short2ShortArrayMap.this.size-- - this.next--;
                  System.arraycopy(Short2ShortArrayMap.this.key, this.next + 1, Short2ShortArrayMap.this.key, this.next, var1);
                  System.arraycopy(Short2ShortArrayMap.this.value, this.next + 1, Short2ShortArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Short2ShortMap.Entry> fastIterator() {
         return new ObjectIterator<Short2ShortMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractShort2ShortMap.BasicEntry entry = new AbstractShort2ShortMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Short2ShortArrayMap.this.size;
            }

            public Short2ShortMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Short2ShortArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Short2ShortArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Short2ShortArrayMap.this.size-- - this.next--;
                  System.arraycopy(Short2ShortArrayMap.this.key, this.next + 1, Short2ShortArrayMap.this.key, this.next, var1);
                  System.arraycopy(Short2ShortArrayMap.this.value, this.next + 1, Short2ShortArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Short2ShortArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Short) {
               if (var2.getValue() != null && var2.getValue() instanceof Short) {
                  short var3 = (Short)var2.getKey();
                  return Short2ShortArrayMap.this.containsKey(var3) && Short2ShortArrayMap.this.get(var3) == (Short)var2.getValue();
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
               if (var2.getValue() != null && var2.getValue() instanceof Short) {
                  short var3 = (Short)var2.getKey();
                  short var4 = (Short)var2.getValue();
                  int var5 = Short2ShortArrayMap.this.findKey(var3);
                  if (var5 != -1 && var4 == Short2ShortArrayMap.this.value[var5]) {
                     int var6 = Short2ShortArrayMap.this.size - var5 - 1;
                     System.arraycopy(Short2ShortArrayMap.this.key, var5 + 1, Short2ShortArrayMap.this.key, var5, var6);
                     System.arraycopy(Short2ShortArrayMap.this.value, var5 + 1, Short2ShortArrayMap.this.value, var5, var6);
                     Short2ShortArrayMap.this.size--;
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
