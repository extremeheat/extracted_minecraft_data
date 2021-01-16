package it.unimi.dsi.fastutil.longs;

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

public class Long2ByteArrayMap extends AbstractLong2ByteMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient long[] key;
   private transient byte[] value;
   private int size;

   public Long2ByteArrayMap(long[] var1, byte[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Long2ByteArrayMap() {
      super();
      this.key = LongArrays.EMPTY_ARRAY;
      this.value = ByteArrays.EMPTY_ARRAY;
   }

   public Long2ByteArrayMap(int var1) {
      super();
      this.key = new long[var1];
      this.value = new byte[var1];
   }

   public Long2ByteArrayMap(Long2ByteMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2ByteArrayMap(Map<? extends Long, ? extends Byte> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2ByteArrayMap(long[] var1, byte[] var2, int var3) {
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

   public Long2ByteMap.FastEntrySet long2ByteEntrySet() {
      return new Long2ByteArrayMap.EntrySet();
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

   public byte get(long var1) {
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

   public byte put(long var1, byte var3) {
      int var4 = this.findKey(var1);
      if (var4 != -1) {
         byte var8 = this.value[var4];
         this.value[var4] = var3;
         return var8;
      } else {
         if (this.size == this.key.length) {
            long[] var5 = new long[this.size == 0 ? 2 : this.size * 2];
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

   public byte remove(long var1) {
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

   public LongSet keySet() {
      return new AbstractLongSet() {
         public boolean contains(long var1) {
            return Long2ByteArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(long var1) {
            int var3 = Long2ByteArrayMap.this.findKey(var1);
            if (var3 == -1) {
               return false;
            } else {
               int var4 = Long2ByteArrayMap.this.size - var3 - 1;
               System.arraycopy(Long2ByteArrayMap.this.key, var3 + 1, Long2ByteArrayMap.this.key, var3, var4);
               System.arraycopy(Long2ByteArrayMap.this.value, var3 + 1, Long2ByteArrayMap.this.value, var3, var4);
               Long2ByteArrayMap.this.size--;
               return true;
            }
         }

         public LongIterator iterator() {
            return new LongIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2ByteArrayMap.this.size;
               }

               public long nextLong() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2ByteArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2ByteArrayMap.this.size - this.pos;
                     System.arraycopy(Long2ByteArrayMap.this.key, this.pos, Long2ByteArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2ByteArrayMap.this.value, this.pos, Long2ByteArrayMap.this.value, this.pos - 1, var1);
                     Long2ByteArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2ByteArrayMap.this.size;
         }

         public void clear() {
            Long2ByteArrayMap.this.clear();
         }
      };
   }

   public ByteCollection values() {
      return new AbstractByteCollection() {
         public boolean contains(byte var1) {
            return Long2ByteArrayMap.this.containsValue(var1);
         }

         public ByteIterator iterator() {
            return new ByteIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2ByteArrayMap.this.size;
               }

               public byte nextByte() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2ByteArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2ByteArrayMap.this.size - this.pos;
                     System.arraycopy(Long2ByteArrayMap.this.key, this.pos, Long2ByteArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2ByteArrayMap.this.value, this.pos, Long2ByteArrayMap.this.value, this.pos - 1, var1);
                     Long2ByteArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2ByteArrayMap.this.size;
         }

         public void clear() {
            Long2ByteArrayMap.this.clear();
         }
      };
   }

   public Long2ByteArrayMap clone() {
      Long2ByteArrayMap var1;
      try {
         var1 = (Long2ByteArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (long[])this.key.clone();
      var1.value = (byte[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeLong(this.key[var2]);
         var1.writeByte(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new long[this.size];
      this.value = new byte[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readLong();
         this.value[var2] = var1.readByte();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Long2ByteMap.Entry> implements Long2ByteMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Long2ByteMap.Entry> iterator() {
         return new ObjectIterator<Long2ByteMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Long2ByteArrayMap.this.size;
            }

            public Long2ByteMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractLong2ByteMap.BasicEntry(Long2ByteArrayMap.this.key[this.curr = this.next], Long2ByteArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2ByteArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2ByteArrayMap.this.key, this.next + 1, Long2ByteArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2ByteArrayMap.this.value, this.next + 1, Long2ByteArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Long2ByteMap.Entry> fastIterator() {
         return new ObjectIterator<Long2ByteMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractLong2ByteMap.BasicEntry entry = new AbstractLong2ByteMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Long2ByteArrayMap.this.size;
            }

            public Long2ByteMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Long2ByteArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Long2ByteArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2ByteArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2ByteArrayMap.this.key, this.next + 1, Long2ByteArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2ByteArrayMap.this.value, this.next + 1, Long2ByteArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Long2ByteArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Long) {
               if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                  long var3 = (Long)var2.getKey();
                  return Long2ByteArrayMap.this.containsKey(var3) && Long2ByteArrayMap.this.get(var3) == (Byte)var2.getValue();
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
               if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                  long var3 = (Long)var2.getKey();
                  byte var5 = (Byte)var2.getValue();
                  int var6 = Long2ByteArrayMap.this.findKey(var3);
                  if (var6 != -1 && var5 == Long2ByteArrayMap.this.value[var6]) {
                     int var7 = Long2ByteArrayMap.this.size - var6 - 1;
                     System.arraycopy(Long2ByteArrayMap.this.key, var6 + 1, Long2ByteArrayMap.this.key, var6, var7);
                     System.arraycopy(Long2ByteArrayMap.this.value, var6 + 1, Long2ByteArrayMap.this.value, var6, var7);
                     Long2ByteArrayMap.this.size--;
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
