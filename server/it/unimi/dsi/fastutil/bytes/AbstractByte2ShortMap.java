package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractByte2ShortMap extends AbstractByte2ShortFunction implements Byte2ShortMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractByte2ShortMap() {
      super();
   }

   public boolean containsValue(short var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(byte var1) {
      ObjectIterator var2 = this.byte2ShortEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Byte2ShortMap.Entry)var2.next()).getByteKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ByteSet keySet() {
      return new AbstractByteSet() {
         public boolean contains(byte var1) {
            return AbstractByte2ShortMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractByte2ShortMap.this.size();
         }

         public void clear() {
            AbstractByte2ShortMap.this.clear();
         }

         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Byte2ShortMap.Entry> i = Byte2ShortMaps.fastIterator(AbstractByte2ShortMap.this);

               public byte nextByte() {
                  return ((Byte2ShortMap.Entry)this.i.next()).getByteKey();
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

   public ShortCollection values() {
      return new AbstractShortCollection() {
         public boolean contains(short var1) {
            return AbstractByte2ShortMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractByte2ShortMap.this.size();
         }

         public void clear() {
            AbstractByte2ShortMap.this.clear();
         }

         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Byte2ShortMap.Entry> i = Byte2ShortMaps.fastIterator(AbstractByte2ShortMap.this);

               public short nextShort() {
                  return ((Byte2ShortMap.Entry)this.i.next()).getShortValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Byte, ? extends Short> var1) {
      if (var1 instanceof Byte2ShortMap) {
         ObjectIterator var2 = Byte2ShortMaps.fastIterator((Byte2ShortMap)var1);

         while(var2.hasNext()) {
            Byte2ShortMap.Entry var3 = (Byte2ShortMap.Entry)var2.next();
            this.put(var3.getByteKey(), var3.getShortValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Byte)var4.getKey(), (Short)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Byte2ShortMaps.fastIterator(this); var2-- != 0; var1 += ((Byte2ShortMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.byte2ShortEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Byte2ShortMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Byte2ShortMap.Entry var4 = (Byte2ShortMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getByteKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getShortValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Byte2ShortMap.Entry> {
      protected final Byte2ShortMap map;

      public BasicEntrySet(Byte2ShortMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Byte2ShortMap.Entry) {
            Byte2ShortMap.Entry var6 = (Byte2ShortMap.Entry)var1;
            byte var7 = var6.getByteKey();
            return this.map.containsKey(var7) && this.map.get(var7) == var6.getShortValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Byte) {
               byte var4 = (Byte)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Short) {
                  return this.map.containsKey(var4) && this.map.get(var4) == (Short)var5;
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
         } else if (var1 instanceof Byte2ShortMap.Entry) {
            Byte2ShortMap.Entry var7 = (Byte2ShortMap.Entry)var1;
            return this.map.remove(var7.getByteKey(), var7.getShortValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Byte) {
               byte var4 = (Byte)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Short) {
                  short var6 = (Short)var5;
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

   public static class BasicEntry implements Byte2ShortMap.Entry {
      protected byte key;
      protected short value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Byte var1, Short var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(byte var1, short var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public byte getByteKey() {
         return this.key;
      }

      public short getShortValue() {
         return this.value;
      }

      public short setValue(short var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Byte2ShortMap.Entry) {
            Byte2ShortMap.Entry var5 = (Byte2ShortMap.Entry)var1;
            return this.key == var5.getByteKey() && this.value == var5.getShortValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Byte) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Short) {
                  return this.key == (Byte)var3 && this.value == (Short)var4;
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
