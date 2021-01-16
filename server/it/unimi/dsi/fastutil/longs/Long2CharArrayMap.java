package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Long2CharArrayMap extends AbstractLong2CharMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient long[] key;
   private transient char[] value;
   private int size;

   public Long2CharArrayMap(long[] var1, char[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Long2CharArrayMap() {
      super();
      this.key = LongArrays.EMPTY_ARRAY;
      this.value = CharArrays.EMPTY_ARRAY;
   }

   public Long2CharArrayMap(int var1) {
      super();
      this.key = new long[var1];
      this.value = new char[var1];
   }

   public Long2CharArrayMap(Long2CharMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2CharArrayMap(Map<? extends Long, ? extends Character> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Long2CharArrayMap(long[] var1, char[] var2, int var3) {
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

   public Long2CharMap.FastEntrySet long2CharEntrySet() {
      return new Long2CharArrayMap.EntrySet();
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

   public char get(long var1) {
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

   public boolean containsValue(char var1) {
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

   public char put(long var1, char var3) {
      int var4 = this.findKey(var1);
      if (var4 != -1) {
         char var8 = this.value[var4];
         this.value[var4] = var3;
         return var8;
      } else {
         if (this.size == this.key.length) {
            long[] var5 = new long[this.size == 0 ? 2 : this.size * 2];
            char[] var6 = new char[this.size == 0 ? 2 : this.size * 2];

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

   public char remove(long var1) {
      int var3 = this.findKey(var1);
      if (var3 == -1) {
         return this.defRetValue;
      } else {
         char var4 = this.value[var3];
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
            return Long2CharArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(long var1) {
            int var3 = Long2CharArrayMap.this.findKey(var1);
            if (var3 == -1) {
               return false;
            } else {
               int var4 = Long2CharArrayMap.this.size - var3 - 1;
               System.arraycopy(Long2CharArrayMap.this.key, var3 + 1, Long2CharArrayMap.this.key, var3, var4);
               System.arraycopy(Long2CharArrayMap.this.value, var3 + 1, Long2CharArrayMap.this.value, var3, var4);
               Long2CharArrayMap.this.size--;
               return true;
            }
         }

         public LongIterator iterator() {
            return new LongIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2CharArrayMap.this.size;
               }

               public long nextLong() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2CharArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2CharArrayMap.this.size - this.pos;
                     System.arraycopy(Long2CharArrayMap.this.key, this.pos, Long2CharArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2CharArrayMap.this.value, this.pos, Long2CharArrayMap.this.value, this.pos - 1, var1);
                     Long2CharArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2CharArrayMap.this.size;
         }

         public void clear() {
            Long2CharArrayMap.this.clear();
         }
      };
   }

   public CharCollection values() {
      return new AbstractCharCollection() {
         public boolean contains(char var1) {
            return Long2CharArrayMap.this.containsValue(var1);
         }

         public CharIterator iterator() {
            return new CharIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Long2CharArrayMap.this.size;
               }

               public char nextChar() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Long2CharArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Long2CharArrayMap.this.size - this.pos;
                     System.arraycopy(Long2CharArrayMap.this.key, this.pos, Long2CharArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Long2CharArrayMap.this.value, this.pos, Long2CharArrayMap.this.value, this.pos - 1, var1);
                     Long2CharArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Long2CharArrayMap.this.size;
         }

         public void clear() {
            Long2CharArrayMap.this.clear();
         }
      };
   }

   public Long2CharArrayMap clone() {
      Long2CharArrayMap var1;
      try {
         var1 = (Long2CharArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (long[])this.key.clone();
      var1.value = (char[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeLong(this.key[var2]);
         var1.writeChar(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new long[this.size];
      this.value = new char[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readLong();
         this.value[var2] = var1.readChar();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Long2CharMap.Entry> implements Long2CharMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Long2CharMap.Entry> iterator() {
         return new ObjectIterator<Long2CharMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Long2CharArrayMap.this.size;
            }

            public Long2CharMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractLong2CharMap.BasicEntry(Long2CharArrayMap.this.key[this.curr = this.next], Long2CharArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2CharArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2CharArrayMap.this.key, this.next + 1, Long2CharArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2CharArrayMap.this.value, this.next + 1, Long2CharArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Long2CharMap.Entry> fastIterator() {
         return new ObjectIterator<Long2CharMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractLong2CharMap.BasicEntry entry = new AbstractLong2CharMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Long2CharArrayMap.this.size;
            }

            public Long2CharMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Long2CharArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Long2CharArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Long2CharArrayMap.this.size-- - this.next--;
                  System.arraycopy(Long2CharArrayMap.this.key, this.next + 1, Long2CharArrayMap.this.key, this.next, var1);
                  System.arraycopy(Long2CharArrayMap.this.value, this.next + 1, Long2CharArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Long2CharArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Long) {
               if (var2.getValue() != null && var2.getValue() instanceof Character) {
                  long var3 = (Long)var2.getKey();
                  return Long2CharArrayMap.this.containsKey(var3) && Long2CharArrayMap.this.get(var3) == (Character)var2.getValue();
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
               if (var2.getValue() != null && var2.getValue() instanceof Character) {
                  long var3 = (Long)var2.getKey();
                  char var5 = (Character)var2.getValue();
                  int var6 = Long2CharArrayMap.this.findKey(var3);
                  if (var6 != -1 && var5 == Long2CharArrayMap.this.value[var6]) {
                     int var7 = Long2CharArrayMap.this.size - var6 - 1;
                     System.arraycopy(Long2CharArrayMap.this.key, var6 + 1, Long2CharArrayMap.this.key, var6, var7);
                     System.arraycopy(Long2CharArrayMap.this.value, var6 + 1, Long2CharArrayMap.this.value, var6, var7);
                     Long2CharArrayMap.this.size--;
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
