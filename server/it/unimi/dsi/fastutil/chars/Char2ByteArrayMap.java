package it.unimi.dsi.fastutil.chars;

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

public class Char2ByteArrayMap extends AbstractChar2ByteMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient char[] key;
   private transient byte[] value;
   private int size;

   public Char2ByteArrayMap(char[] var1, byte[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Char2ByteArrayMap() {
      super();
      this.key = CharArrays.EMPTY_ARRAY;
      this.value = ByteArrays.EMPTY_ARRAY;
   }

   public Char2ByteArrayMap(int var1) {
      super();
      this.key = new char[var1];
      this.value = new byte[var1];
   }

   public Char2ByteArrayMap(Char2ByteMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Char2ByteArrayMap(Map<? extends Character, ? extends Byte> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Char2ByteArrayMap(char[] var1, byte[] var2, int var3) {
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

   public Char2ByteMap.FastEntrySet char2ByteEntrySet() {
      return new Char2ByteArrayMap.EntrySet();
   }

   private int findKey(char var1) {
      char[] var2 = this.key;
      int var3 = this.size;

      do {
         if (var3-- == 0) {
            return -1;
         }
      } while(var2[var3] != var1);

      return var3;
   }

   public byte get(char var1) {
      char[] var2 = this.key;
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

   public boolean containsKey(char var1) {
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

   public byte put(char var1, byte var2) {
      int var3 = this.findKey(var1);
      if (var3 != -1) {
         byte var7 = this.value[var3];
         this.value[var3] = var2;
         return var7;
      } else {
         if (this.size == this.key.length) {
            char[] var4 = new char[this.size == 0 ? 2 : this.size * 2];
            byte[] var5 = new byte[this.size == 0 ? 2 : this.size * 2];

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

   public byte remove(char var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return this.defRetValue;
      } else {
         byte var3 = this.value[var2];
         int var4 = this.size - var2 - 1;
         System.arraycopy(this.key, var2 + 1, this.key, var2, var4);
         System.arraycopy(this.value, var2 + 1, this.value, var2, var4);
         --this.size;
         return var3;
      }
   }

   public CharSet keySet() {
      return new AbstractCharSet() {
         public boolean contains(char var1) {
            return Char2ByteArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(char var1) {
            int var2 = Char2ByteArrayMap.this.findKey(var1);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = Char2ByteArrayMap.this.size - var2 - 1;
               System.arraycopy(Char2ByteArrayMap.this.key, var2 + 1, Char2ByteArrayMap.this.key, var2, var3);
               System.arraycopy(Char2ByteArrayMap.this.value, var2 + 1, Char2ByteArrayMap.this.value, var2, var3);
               Char2ByteArrayMap.this.size--;
               return true;
            }
         }

         public CharIterator iterator() {
            return new CharIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Char2ByteArrayMap.this.size;
               }

               public char nextChar() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Char2ByteArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Char2ByteArrayMap.this.size - this.pos;
                     System.arraycopy(Char2ByteArrayMap.this.key, this.pos, Char2ByteArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Char2ByteArrayMap.this.value, this.pos, Char2ByteArrayMap.this.value, this.pos - 1, var1);
                     Char2ByteArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Char2ByteArrayMap.this.size;
         }

         public void clear() {
            Char2ByteArrayMap.this.clear();
         }
      };
   }

   public ByteCollection values() {
      return new AbstractByteCollection() {
         public boolean contains(byte var1) {
            return Char2ByteArrayMap.this.containsValue(var1);
         }

         public ByteIterator iterator() {
            return new ByteIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Char2ByteArrayMap.this.size;
               }

               public byte nextByte() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Char2ByteArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Char2ByteArrayMap.this.size - this.pos;
                     System.arraycopy(Char2ByteArrayMap.this.key, this.pos, Char2ByteArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Char2ByteArrayMap.this.value, this.pos, Char2ByteArrayMap.this.value, this.pos - 1, var1);
                     Char2ByteArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Char2ByteArrayMap.this.size;
         }

         public void clear() {
            Char2ByteArrayMap.this.clear();
         }
      };
   }

   public Char2ByteArrayMap clone() {
      Char2ByteArrayMap var1;
      try {
         var1 = (Char2ByteArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (char[])this.key.clone();
      var1.value = (byte[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeChar(this.key[var2]);
         var1.writeByte(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new char[this.size];
      this.value = new byte[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readChar();
         this.value[var2] = var1.readByte();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Char2ByteMap.Entry> implements Char2ByteMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Char2ByteMap.Entry> iterator() {
         return new ObjectIterator<Char2ByteMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Char2ByteArrayMap.this.size;
            }

            public Char2ByteMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractChar2ByteMap.BasicEntry(Char2ByteArrayMap.this.key[this.curr = this.next], Char2ByteArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Char2ByteArrayMap.this.size-- - this.next--;
                  System.arraycopy(Char2ByteArrayMap.this.key, this.next + 1, Char2ByteArrayMap.this.key, this.next, var1);
                  System.arraycopy(Char2ByteArrayMap.this.value, this.next + 1, Char2ByteArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Char2ByteMap.Entry> fastIterator() {
         return new ObjectIterator<Char2ByteMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractChar2ByteMap.BasicEntry entry = new AbstractChar2ByteMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Char2ByteArrayMap.this.size;
            }

            public Char2ByteMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Char2ByteArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Char2ByteArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Char2ByteArrayMap.this.size-- - this.next--;
                  System.arraycopy(Char2ByteArrayMap.this.key, this.next + 1, Char2ByteArrayMap.this.key, this.next, var1);
                  System.arraycopy(Char2ByteArrayMap.this.value, this.next + 1, Char2ByteArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Char2ByteArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Character) {
               if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                  char var3 = (Character)var2.getKey();
                  return Char2ByteArrayMap.this.containsKey(var3) && Char2ByteArrayMap.this.get(var3) == (Byte)var2.getValue();
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
            if (var2.getKey() != null && var2.getKey() instanceof Character) {
               if (var2.getValue() != null && var2.getValue() instanceof Byte) {
                  char var3 = (Character)var2.getKey();
                  byte var4 = (Byte)var2.getValue();
                  int var5 = Char2ByteArrayMap.this.findKey(var3);
                  if (var5 != -1 && var4 == Char2ByteArrayMap.this.value[var5]) {
                     int var6 = Char2ByteArrayMap.this.size - var5 - 1;
                     System.arraycopy(Char2ByteArrayMap.this.key, var5 + 1, Char2ByteArrayMap.this.key, var5, var6);
                     System.arraycopy(Char2ByteArrayMap.this.value, var5 + 1, Char2ByteArrayMap.this.value, var5, var6);
                     Char2ByteArrayMap.this.size--;
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
