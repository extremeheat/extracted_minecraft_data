package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Byte2LongArrayMap extends AbstractByte2LongMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient byte[] key;
   private transient long[] value;
   private int size;

   public Byte2LongArrayMap(byte[] var1, long[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Byte2LongArrayMap() {
      super();
      this.key = ByteArrays.EMPTY_ARRAY;
      this.value = LongArrays.EMPTY_ARRAY;
   }

   public Byte2LongArrayMap(int var1) {
      super();
      this.key = new byte[var1];
      this.value = new long[var1];
   }

   public Byte2LongArrayMap(Byte2LongMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Byte2LongArrayMap(Map<? extends Byte, ? extends Long> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Byte2LongArrayMap(byte[] var1, long[] var2, int var3) {
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

   public Byte2LongMap.FastEntrySet byte2LongEntrySet() {
      return new Byte2LongArrayMap.EntrySet();
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

   public long get(byte var1) {
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

   public boolean containsValue(long var1) {
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return false;
         }
      } while(this.value[var3] != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public long put(byte var1, long var2) {
      int var4 = this.findKey(var1);
      if (var4 != -1) {
         long var8 = this.value[var4];
         this.value[var4] = var2;
         return var8;
      } else {
         if (this.size == this.key.length) {
            byte[] var5 = new byte[this.size == 0 ? 2 : this.size * 2];
            long[] var6 = new long[this.size == 0 ? 2 : this.size * 2];

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

   public long remove(byte var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return this.defRetValue;
      } else {
         long var3 = this.value[var2];
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
            return Byte2LongArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(byte var1) {
            int var2 = Byte2LongArrayMap.this.findKey(var1);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = Byte2LongArrayMap.this.size - var2 - 1;
               System.arraycopy(Byte2LongArrayMap.this.key, var2 + 1, Byte2LongArrayMap.this.key, var2, var3);
               System.arraycopy(Byte2LongArrayMap.this.value, var2 + 1, Byte2LongArrayMap.this.value, var2, var3);
               Byte2LongArrayMap.this.size--;
               return true;
            }
         }

         public ByteIterator iterator() {
            return new ByteIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Byte2LongArrayMap.this.size;
               }

               public byte nextByte() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Byte2LongArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Byte2LongArrayMap.this.size - this.pos;
                     System.arraycopy(Byte2LongArrayMap.this.key, this.pos, Byte2LongArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Byte2LongArrayMap.this.value, this.pos, Byte2LongArrayMap.this.value, this.pos - 1, var1);
                     Byte2LongArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Byte2LongArrayMap.this.size;
         }

         public void clear() {
            Byte2LongArrayMap.this.clear();
         }
      };
   }

   public LongCollection values() {
      return new AbstractLongCollection() {
         public boolean contains(long var1) {
            return Byte2LongArrayMap.this.containsValue(var1);
         }

         public LongIterator iterator() {
            return new LongIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Byte2LongArrayMap.this.size;
               }

               public long nextLong() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Byte2LongArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Byte2LongArrayMap.this.size - this.pos;
                     System.arraycopy(Byte2LongArrayMap.this.key, this.pos, Byte2LongArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Byte2LongArrayMap.this.value, this.pos, Byte2LongArrayMap.this.value, this.pos - 1, var1);
                     Byte2LongArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Byte2LongArrayMap.this.size;
         }

         public void clear() {
            Byte2LongArrayMap.this.clear();
         }
      };
   }

   public Byte2LongArrayMap clone() {
      Byte2LongArrayMap var1;
      try {
         var1 = (Byte2LongArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (byte[])this.key.clone();
      var1.value = (long[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeByte(this.key[var2]);
         var1.writeLong(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new byte[this.size];
      this.value = new long[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readByte();
         this.value[var2] = var1.readLong();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Byte2LongMap.Entry> implements Byte2LongMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Byte2LongMap.Entry> iterator() {
         return new ObjectIterator<Byte2LongMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Byte2LongArrayMap.this.size;
            }

            public Byte2LongMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractByte2LongMap.BasicEntry(Byte2LongArrayMap.this.key[this.curr = this.next], Byte2LongArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Byte2LongArrayMap.this.size-- - this.next--;
                  System.arraycopy(Byte2LongArrayMap.this.key, this.next + 1, Byte2LongArrayMap.this.key, this.next, var1);
                  System.arraycopy(Byte2LongArrayMap.this.value, this.next + 1, Byte2LongArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Byte2LongMap.Entry> fastIterator() {
         return new ObjectIterator<Byte2LongMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractByte2LongMap.BasicEntry entry = new AbstractByte2LongMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Byte2LongArrayMap.this.size;
            }

            public Byte2LongMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Byte2LongArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Byte2LongArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Byte2LongArrayMap.this.size-- - this.next--;
                  System.arraycopy(Byte2LongArrayMap.this.key, this.next + 1, Byte2LongArrayMap.this.key, this.next, var1);
                  System.arraycopy(Byte2LongArrayMap.this.value, this.next + 1, Byte2LongArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Byte2LongArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Byte) {
               if (var2.getValue() != null && var2.getValue() instanceof Long) {
                  byte var3 = (Byte)var2.getKey();
                  return Byte2LongArrayMap.this.containsKey(var3) && Byte2LongArrayMap.this.get(var3) == (Long)var2.getValue();
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
               if (var2.getValue() != null && var2.getValue() instanceof Long) {
                  byte var3 = (Byte)var2.getKey();
                  long var4 = (Long)var2.getValue();
                  int var6 = Byte2LongArrayMap.this.findKey(var3);
                  if (var6 != -1 && var4 == Byte2LongArrayMap.this.value[var6]) {
                     int var7 = Byte2LongArrayMap.this.size - var6 - 1;
                     System.arraycopy(Byte2LongArrayMap.this.key, var6 + 1, Byte2LongArrayMap.this.key, var6, var7);
                     System.arraycopy(Byte2LongArrayMap.this.value, var6 + 1, Byte2LongArrayMap.this.value, var6, var7);
                     Byte2LongArrayMap.this.size--;
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
