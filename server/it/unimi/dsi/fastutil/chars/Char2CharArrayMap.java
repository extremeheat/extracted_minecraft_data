package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Char2CharArrayMap extends AbstractChar2CharMap implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient char[] key;
   private transient char[] value;
   private int size;

   public Char2CharArrayMap(char[] var1, char[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Char2CharArrayMap() {
      super();
      this.key = CharArrays.EMPTY_ARRAY;
      this.value = CharArrays.EMPTY_ARRAY;
   }

   public Char2CharArrayMap(int var1) {
      super();
      this.key = new char[var1];
      this.value = new char[var1];
   }

   public Char2CharArrayMap(Char2CharMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Char2CharArrayMap(Map<? extends Character, ? extends Character> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Char2CharArrayMap(char[] var1, char[] var2, int var3) {
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

   public Char2CharMap.FastEntrySet char2CharEntrySet() {
      return new Char2CharArrayMap.EntrySet();
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

   public char get(char var1) {
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

   public char put(char var1, char var2) {
      int var3 = this.findKey(var1);
      if (var3 != -1) {
         char var7 = this.value[var3];
         this.value[var3] = var2;
         return var7;
      } else {
         if (this.size == this.key.length) {
            char[] var4 = new char[this.size == 0 ? 2 : this.size * 2];
            char[] var5 = new char[this.size == 0 ? 2 : this.size * 2];

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

   public char remove(char var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return this.defRetValue;
      } else {
         char var3 = this.value[var2];
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
            return Char2CharArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(char var1) {
            int var2 = Char2CharArrayMap.this.findKey(var1);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = Char2CharArrayMap.this.size - var2 - 1;
               System.arraycopy(Char2CharArrayMap.this.key, var2 + 1, Char2CharArrayMap.this.key, var2, var3);
               System.arraycopy(Char2CharArrayMap.this.value, var2 + 1, Char2CharArrayMap.this.value, var2, var3);
               Char2CharArrayMap.this.size--;
               return true;
            }
         }

         public CharIterator iterator() {
            return new CharIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Char2CharArrayMap.this.size;
               }

               public char nextChar() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Char2CharArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Char2CharArrayMap.this.size - this.pos;
                     System.arraycopy(Char2CharArrayMap.this.key, this.pos, Char2CharArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Char2CharArrayMap.this.value, this.pos, Char2CharArrayMap.this.value, this.pos - 1, var1);
                     Char2CharArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Char2CharArrayMap.this.size;
         }

         public void clear() {
            Char2CharArrayMap.this.clear();
         }
      };
   }

   public CharCollection values() {
      return new AbstractCharCollection() {
         public boolean contains(char var1) {
            return Char2CharArrayMap.this.containsValue(var1);
         }

         public CharIterator iterator() {
            return new CharIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Char2CharArrayMap.this.size;
               }

               public char nextChar() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Char2CharArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Char2CharArrayMap.this.size - this.pos;
                     System.arraycopy(Char2CharArrayMap.this.key, this.pos, Char2CharArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Char2CharArrayMap.this.value, this.pos, Char2CharArrayMap.this.value, this.pos - 1, var1);
                     Char2CharArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Char2CharArrayMap.this.size;
         }

         public void clear() {
            Char2CharArrayMap.this.clear();
         }
      };
   }

   public Char2CharArrayMap clone() {
      Char2CharArrayMap var1;
      try {
         var1 = (Char2CharArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (char[])this.key.clone();
      var1.value = (char[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeChar(this.key[var2]);
         var1.writeChar(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new char[this.size];
      this.value = new char[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readChar();
         this.value[var2] = var1.readChar();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Char2CharMap.Entry> implements Char2CharMap.FastEntrySet {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Char2CharMap.Entry> iterator() {
         return new ObjectIterator<Char2CharMap.Entry>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Char2CharArrayMap.this.size;
            }

            public Char2CharMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractChar2CharMap.BasicEntry(Char2CharArrayMap.this.key[this.curr = this.next], Char2CharArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Char2CharArrayMap.this.size-- - this.next--;
                  System.arraycopy(Char2CharArrayMap.this.key, this.next + 1, Char2CharArrayMap.this.key, this.next, var1);
                  System.arraycopy(Char2CharArrayMap.this.value, this.next + 1, Char2CharArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public ObjectIterator<Char2CharMap.Entry> fastIterator() {
         return new ObjectIterator<Char2CharMap.Entry>() {
            int next = 0;
            int curr = -1;
            final AbstractChar2CharMap.BasicEntry entry = new AbstractChar2CharMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Char2CharArrayMap.this.size;
            }

            public Char2CharMap.Entry next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Char2CharArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Char2CharArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Char2CharArrayMap.this.size-- - this.next--;
                  System.arraycopy(Char2CharArrayMap.this.key, this.next + 1, Char2CharArrayMap.this.key, this.next, var1);
                  System.arraycopy(Char2CharArrayMap.this.value, this.next + 1, Char2CharArrayMap.this.value, this.next, var1);
               }
            }
         };
      }

      public int size() {
         return Char2CharArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Character) {
               if (var2.getValue() != null && var2.getValue() instanceof Character) {
                  char var3 = (Character)var2.getKey();
                  return Char2CharArrayMap.this.containsKey(var3) && Char2CharArrayMap.this.get(var3) == (Character)var2.getValue();
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
               if (var2.getValue() != null && var2.getValue() instanceof Character) {
                  char var3 = (Character)var2.getKey();
                  char var4 = (Character)var2.getValue();
                  int var5 = Char2CharArrayMap.this.findKey(var3);
                  if (var5 != -1 && var4 == Char2CharArrayMap.this.value[var5]) {
                     int var6 = Char2CharArrayMap.this.size - var5 - 1;
                     System.arraycopy(Char2CharArrayMap.this.key, var5 + 1, Char2CharArrayMap.this.key, var5, var6);
                     System.arraycopy(Char2CharArrayMap.this.value, var5 + 1, Char2CharArrayMap.this.value, var5, var6);
                     Char2CharArrayMap.this.size--;
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
