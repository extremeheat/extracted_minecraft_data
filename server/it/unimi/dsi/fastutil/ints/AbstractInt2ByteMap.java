package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractInt2ByteMap extends AbstractInt2ByteFunction implements Int2ByteMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractInt2ByteMap() {
      super();
   }

   public boolean containsValue(byte var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(int var1) {
      ObjectIterator var2 = this.int2ByteEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Int2ByteMap.Entry)var2.next()).getIntKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public IntSet keySet() {
      return new AbstractIntSet() {
         public boolean contains(int var1) {
            return AbstractInt2ByteMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractInt2ByteMap.this.size();
         }

         public void clear() {
            AbstractInt2ByteMap.this.clear();
         }

         public IntIterator iterator() {
            return new IntIterator() {
               private final ObjectIterator<Int2ByteMap.Entry> i = Int2ByteMaps.fastIterator(AbstractInt2ByteMap.this);

               public int nextInt() {
                  return ((Int2ByteMap.Entry)this.i.next()).getIntKey();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }

               public void remove() {
                  this.i.remove();
               }
            };
         }
      };
   }

   public ByteCollection values() {
      return new AbstractByteCollection() {
         public boolean contains(byte var1) {
            return AbstractInt2ByteMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractInt2ByteMap.this.size();
         }

         public void clear() {
            AbstractInt2ByteMap.this.clear();
         }

         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Int2ByteMap.Entry> i = Int2ByteMaps.fastIterator(AbstractInt2ByteMap.this);

               public byte nextByte() {
                  return ((Int2ByteMap.Entry)this.i.next()).getByteValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Integer, ? extends Byte> var1) {
      if (var1 instanceof Int2ByteMap) {
         ObjectIterator var2 = Int2ByteMaps.fastIterator((Int2ByteMap)var1);

         while(var2.hasNext()) {
            Int2ByteMap.Entry var3 = (Int2ByteMap.Entry)var2.next();
            this.put(var3.getIntKey(), var3.getByteValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Integer)var4.getKey(), (Byte)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Int2ByteMaps.fastIterator(this); var2-- != 0; var1 += ((Int2ByteMap.Entry)var3.next()).hashCode()) {
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Map)) {
         return false;
      } else {
         Map var2 = (Map)var1;
         return var2.size() != this.size() ? false : this.int2ByteEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Int2ByteMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Int2ByteMap.Entry var4 = (Int2ByteMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getIntKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getByteValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Int2ByteMap.Entry> {
      protected final Int2ByteMap map;

      public BasicEntrySet(Int2ByteMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Int2ByteMap.Entry) {
            Int2ByteMap.Entry var6 = (Int2ByteMap.Entry)var1;
            int var7 = var6.getIntKey();
            return this.map.containsKey(var7) && this.map.get(var7) == var6.getByteValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Integer) {
               int var4 = (Integer)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Byte) {
                  return this.map.containsKey(var4) && this.map.get(var4) == (Byte)var5;
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
         } else if (var1 instanceof Int2ByteMap.Entry) {
            Int2ByteMap.Entry var7 = (Int2ByteMap.Entry)var1;
            return this.map.remove(var7.getIntKey(), var7.getByteValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Integer) {
               int var4 = (Integer)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Byte) {
                  byte var6 = (Byte)var5;
                  return this.map.remove(var4, var6);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int size() {
         return this.map.size();
      }
   }

   public static class BasicEntry implements Int2ByteMap.Entry {
      protected int key;
      protected byte value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Integer var1, Byte var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(int var1, byte var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public int getIntKey() {
         return this.key;
      }

      public byte getByteValue() {
         return this.value;
      }

      public byte setValue(byte var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Int2ByteMap.Entry) {
            Int2ByteMap.Entry var5 = (Int2ByteMap.Entry)var1;
            return this.key == var5.getIntKey() && this.value == var5.getByteValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Integer) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Byte) {
                  return this.key == (Integer)var3 && this.value == (Byte)var4;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.key ^ this.value;
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
