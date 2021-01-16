package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractByte2ByteMap extends AbstractByte2ByteFunction implements Byte2ByteMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractByte2ByteMap() {
      super();
   }

   public boolean containsValue(byte var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(byte var1) {
      ObjectIterator var2 = this.byte2ByteEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Byte2ByteMap.Entry)var2.next()).getByteKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ByteSet keySet() {
      return new AbstractByteSet() {
         public boolean contains(byte var1) {
            return AbstractByte2ByteMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractByte2ByteMap.this.size();
         }

         public void clear() {
            AbstractByte2ByteMap.this.clear();
         }

         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Byte2ByteMap.Entry> i = Byte2ByteMaps.fastIterator(AbstractByte2ByteMap.this);

               public byte nextByte() {
                  return ((Byte2ByteMap.Entry)this.i.next()).getByteKey();
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
            return AbstractByte2ByteMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractByte2ByteMap.this.size();
         }

         public void clear() {
            AbstractByte2ByteMap.this.clear();
         }

         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Byte2ByteMap.Entry> i = Byte2ByteMaps.fastIterator(AbstractByte2ByteMap.this);

               public byte nextByte() {
                  return ((Byte2ByteMap.Entry)this.i.next()).getByteValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Byte, ? extends Byte> var1) {
      if (var1 instanceof Byte2ByteMap) {
         ObjectIterator var2 = Byte2ByteMaps.fastIterator((Byte2ByteMap)var1);

         while(var2.hasNext()) {
            Byte2ByteMap.Entry var3 = (Byte2ByteMap.Entry)var2.next();
            this.put(var3.getByteKey(), var3.getByteValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Byte)var4.getKey(), (Byte)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Byte2ByteMaps.fastIterator(this); var2-- != 0; var1 += ((Byte2ByteMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.byte2ByteEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Byte2ByteMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Byte2ByteMap.Entry var4 = (Byte2ByteMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getByteKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getByteValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Byte2ByteMap.Entry> {
      protected final Byte2ByteMap map;

      public BasicEntrySet(Byte2ByteMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Byte2ByteMap.Entry) {
            Byte2ByteMap.Entry var6 = (Byte2ByteMap.Entry)var1;
            byte var7 = var6.getByteKey();
            return this.map.containsKey(var7) && this.map.get(var7) == var6.getByteValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Byte) {
               byte var4 = (Byte)var3;
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
         } else if (var1 instanceof Byte2ByteMap.Entry) {
            Byte2ByteMap.Entry var7 = (Byte2ByteMap.Entry)var1;
            return this.map.remove(var7.getByteKey(), var7.getByteValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Byte) {
               byte var4 = (Byte)var3;
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

   public static class BasicEntry implements Byte2ByteMap.Entry {
      protected byte key;
      protected byte value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Byte var1, Byte var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(byte var1, byte var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public byte getByteKey() {
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
         } else if (var1 instanceof Byte2ByteMap.Entry) {
            Byte2ByteMap.Entry var5 = (Byte2ByteMap.Entry)var1;
            return this.key == var5.getByteKey() && this.value == var5.getByteValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Byte) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Byte) {
                  return this.key == (Byte)var3 && this.value == (Byte)var4;
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
