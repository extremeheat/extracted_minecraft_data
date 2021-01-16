package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Byte2ReferenceArrayMap<V> extends AbstractByte2ReferenceMap<V> implements Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private transient byte[] key;
   private transient Object[] value;
   private int size;

   public Byte2ReferenceArrayMap(byte[] var1, Object[] var2) {
      super();
      this.key = var1;
      this.value = var2;
      this.size = var1.length;
      if (var1.length != var2.length) {
         throw new IllegalArgumentException("Keys and values have different lengths (" + var1.length + ", " + var2.length + ")");
      }
   }

   public Byte2ReferenceArrayMap() {
      super();
      this.key = ByteArrays.EMPTY_ARRAY;
      this.value = ObjectArrays.EMPTY_ARRAY;
   }

   public Byte2ReferenceArrayMap(int var1) {
      super();
      this.key = new byte[var1];
      this.value = new Object[var1];
   }

   public Byte2ReferenceArrayMap(Byte2ReferenceMap<V> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Byte2ReferenceArrayMap(Map<? extends Byte, ? extends V> var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public Byte2ReferenceArrayMap(byte[] var1, Object[] var2, int var3) {
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

   public Byte2ReferenceMap.FastEntrySet<V> byte2ReferenceEntrySet() {
      return new Byte2ReferenceArrayMap.EntrySet();
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

   public V get(byte var1) {
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
      for(int var1 = this.size; var1-- != 0; this.value[var1] = null) {
      }

      this.size = 0;
   }

   public boolean containsKey(byte var1) {
      return this.findKey(var1) != -1;
   }

   public boolean containsValue(Object var1) {
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

   public V put(byte var1, V var2) {
      int var3 = this.findKey(var1);
      if (var3 != -1) {
         Object var7 = this.value[var3];
         this.value[var3] = var2;
         return var7;
      } else {
         if (this.size == this.key.length) {
            byte[] var4 = new byte[this.size == 0 ? 2 : this.size * 2];
            Object[] var5 = new Object[this.size == 0 ? 2 : this.size * 2];

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

   public V remove(byte var1) {
      int var2 = this.findKey(var1);
      if (var2 == -1) {
         return this.defRetValue;
      } else {
         Object var3 = this.value[var2];
         int var4 = this.size - var2 - 1;
         System.arraycopy(this.key, var2 + 1, this.key, var2, var4);
         System.arraycopy(this.value, var2 + 1, this.value, var2, var4);
         --this.size;
         this.value[this.size] = null;
         return var3;
      }
   }

   public ByteSet keySet() {
      return new AbstractByteSet() {
         public boolean contains(byte var1) {
            return Byte2ReferenceArrayMap.this.findKey(var1) != -1;
         }

         public boolean remove(byte var1) {
            int var2 = Byte2ReferenceArrayMap.this.findKey(var1);
            if (var2 == -1) {
               return false;
            } else {
               int var3 = Byte2ReferenceArrayMap.this.size - var2 - 1;
               System.arraycopy(Byte2ReferenceArrayMap.this.key, var2 + 1, Byte2ReferenceArrayMap.this.key, var2, var3);
               System.arraycopy(Byte2ReferenceArrayMap.this.value, var2 + 1, Byte2ReferenceArrayMap.this.value, var2, var3);
               Byte2ReferenceArrayMap.this.size--;
               return true;
            }
         }

         public ByteIterator iterator() {
            return new ByteIterator() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Byte2ReferenceArrayMap.this.size;
               }

               public byte nextByte() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Byte2ReferenceArrayMap.this.key[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Byte2ReferenceArrayMap.this.size - this.pos;
                     System.arraycopy(Byte2ReferenceArrayMap.this.key, this.pos, Byte2ReferenceArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Byte2ReferenceArrayMap.this.value, this.pos, Byte2ReferenceArrayMap.this.value, this.pos - 1, var1);
                     Byte2ReferenceArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Byte2ReferenceArrayMap.this.size;
         }

         public void clear() {
            Byte2ReferenceArrayMap.this.clear();
         }
      };
   }

   public ReferenceCollection<V> values() {
      return new AbstractReferenceCollection<V>() {
         public boolean contains(Object var1) {
            return Byte2ReferenceArrayMap.this.containsValue(var1);
         }

         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               int pos = 0;

               public boolean hasNext() {
                  return this.pos < Byte2ReferenceArrayMap.this.size;
               }

               public V next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     return Byte2ReferenceArrayMap.this.value[this.pos++];
                  }
               }

               public void remove() {
                  if (this.pos == 0) {
                     throw new IllegalStateException();
                  } else {
                     int var1 = Byte2ReferenceArrayMap.this.size - this.pos;
                     System.arraycopy(Byte2ReferenceArrayMap.this.key, this.pos, Byte2ReferenceArrayMap.this.key, this.pos - 1, var1);
                     System.arraycopy(Byte2ReferenceArrayMap.this.value, this.pos, Byte2ReferenceArrayMap.this.value, this.pos - 1, var1);
                     Byte2ReferenceArrayMap.this.size--;
                  }
               }
            };
         }

         public int size() {
            return Byte2ReferenceArrayMap.this.size;
         }

         public void clear() {
            Byte2ReferenceArrayMap.this.clear();
         }
      };
   }

   public Byte2ReferenceArrayMap<V> clone() {
      Byte2ReferenceArrayMap var1;
      try {
         var1 = (Byte2ReferenceArrayMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      var1.key = (byte[])this.key.clone();
      var1.value = (Object[])this.value.clone();
      return var1;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeByte(this.key[var2]);
         var1.writeObject(this.value[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.key = new byte[this.size];
      this.value = new Object[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.key[var2] = var1.readByte();
         this.value[var2] = var1.readObject();
      }

   }

   private final class EntrySet extends AbstractObjectSet<Byte2ReferenceMap.Entry<V>> implements Byte2ReferenceMap.FastEntrySet<V> {
      private EntrySet() {
         super();
      }

      public ObjectIterator<Byte2ReferenceMap.Entry<V>> iterator() {
         return new ObjectIterator<Byte2ReferenceMap.Entry<V>>() {
            int curr = -1;
            int next = 0;

            public boolean hasNext() {
               return this.next < Byte2ReferenceArrayMap.this.size;
            }

            public Byte2ReferenceMap.Entry<V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  return new AbstractByte2ReferenceMap.BasicEntry(Byte2ReferenceArrayMap.this.key[this.curr = this.next], Byte2ReferenceArrayMap.this.value[this.next++]);
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Byte2ReferenceArrayMap.this.size-- - this.next--;
                  System.arraycopy(Byte2ReferenceArrayMap.this.key, this.next + 1, Byte2ReferenceArrayMap.this.key, this.next, var1);
                  System.arraycopy(Byte2ReferenceArrayMap.this.value, this.next + 1, Byte2ReferenceArrayMap.this.value, this.next, var1);
                  Byte2ReferenceArrayMap.this.value[Byte2ReferenceArrayMap.this.size] = null;
               }
            }
         };
      }

      public ObjectIterator<Byte2ReferenceMap.Entry<V>> fastIterator() {
         return new ObjectIterator<Byte2ReferenceMap.Entry<V>>() {
            int next = 0;
            int curr = -1;
            final AbstractByte2ReferenceMap.BasicEntry<V> entry = new AbstractByte2ReferenceMap.BasicEntry();

            public boolean hasNext() {
               return this.next < Byte2ReferenceArrayMap.this.size;
            }

            public Byte2ReferenceMap.Entry<V> next() {
               if (!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  this.entry.key = Byte2ReferenceArrayMap.this.key[this.curr = this.next];
                  this.entry.value = Byte2ReferenceArrayMap.this.value[this.next++];
                  return this.entry;
               }
            }

            public void remove() {
               if (this.curr == -1) {
                  throw new IllegalStateException();
               } else {
                  this.curr = -1;
                  int var1 = Byte2ReferenceArrayMap.this.size-- - this.next--;
                  System.arraycopy(Byte2ReferenceArrayMap.this.key, this.next + 1, Byte2ReferenceArrayMap.this.key, this.next, var1);
                  System.arraycopy(Byte2ReferenceArrayMap.this.value, this.next + 1, Byte2ReferenceArrayMap.this.value, this.next, var1);
                  Byte2ReferenceArrayMap.this.value[Byte2ReferenceArrayMap.this.size] = null;
               }
            }
         };
      }

      public int size() {
         return Byte2ReferenceArrayMap.this.size;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            if (var2.getKey() != null && var2.getKey() instanceof Byte) {
               byte var3 = (Byte)var2.getKey();
               return Byte2ReferenceArrayMap.this.containsKey(var3) && Byte2ReferenceArrayMap.this.get(var3) == var2.getValue();
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
               byte var3 = (Byte)var2.getKey();
               Object var4 = var2.getValue();
               int var5 = Byte2ReferenceArrayMap.this.findKey(var3);
               if (var5 != -1 && var4 == Byte2ReferenceArrayMap.this.value[var5]) {
                  int var6 = Byte2ReferenceArrayMap.this.size - var5 - 1;
                  System.arraycopy(Byte2ReferenceArrayMap.this.key, var5 + 1, Byte2ReferenceArrayMap.this.key, var5, var6);
                  System.arraycopy(Byte2ReferenceArrayMap.this.value, var5 + 1, Byte2ReferenceArrayMap.this.value, var5, var6);
                  Byte2ReferenceArrayMap.this.size--;
                  Byte2ReferenceArrayMap.this.value[Byte2ReferenceArrayMap.this.size] = null;
                  return true;
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
