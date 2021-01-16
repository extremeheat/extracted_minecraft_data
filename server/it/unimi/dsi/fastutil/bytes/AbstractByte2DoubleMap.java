package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractByte2DoubleMap extends AbstractByte2DoubleFunction implements Byte2DoubleMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractByte2DoubleMap() {
      super();
   }

   public boolean containsValue(double var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(byte var1) {
      ObjectIterator var2 = this.byte2DoubleEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Byte2DoubleMap.Entry)var2.next()).getByteKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ByteSet keySet() {
      return new AbstractByteSet() {
         public boolean contains(byte var1) {
            return AbstractByte2DoubleMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractByte2DoubleMap.this.size();
         }

         public void clear() {
            AbstractByte2DoubleMap.this.clear();
         }

         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Byte2DoubleMap.Entry> i = Byte2DoubleMaps.fastIterator(AbstractByte2DoubleMap.this);

               public byte nextByte() {
                  return ((Byte2DoubleMap.Entry)this.i.next()).getByteKey();
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

   public DoubleCollection values() {
      return new AbstractDoubleCollection() {
         public boolean contains(double var1) {
            return AbstractByte2DoubleMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractByte2DoubleMap.this.size();
         }

         public void clear() {
            AbstractByte2DoubleMap.this.clear();
         }

         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Byte2DoubleMap.Entry> i = Byte2DoubleMaps.fastIterator(AbstractByte2DoubleMap.this);

               public double nextDouble() {
                  return ((Byte2DoubleMap.Entry)this.i.next()).getDoubleValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Byte, ? extends Double> var1) {
      if (var1 instanceof Byte2DoubleMap) {
         ObjectIterator var2 = Byte2DoubleMaps.fastIterator((Byte2DoubleMap)var1);

         while(var2.hasNext()) {
            Byte2DoubleMap.Entry var3 = (Byte2DoubleMap.Entry)var2.next();
            this.put(var3.getByteKey(), var3.getDoubleValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Byte)var4.getKey(), (Double)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Byte2DoubleMaps.fastIterator(this); var2-- != 0; var1 += ((Byte2DoubleMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.byte2DoubleEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Byte2DoubleMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Byte2DoubleMap.Entry var4 = (Byte2DoubleMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getByteKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getDoubleValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Byte2DoubleMap.Entry> {
      protected final Byte2DoubleMap map;

      public BasicEntrySet(Byte2DoubleMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Byte2DoubleMap.Entry) {
            Byte2DoubleMap.Entry var6 = (Byte2DoubleMap.Entry)var1;
            byte var7 = var6.getByteKey();
            return this.map.containsKey(var7) && Double.doubleToLongBits(this.map.get(var7)) == Double.doubleToLongBits(var6.getDoubleValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Byte) {
               byte var4 = (Byte)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Double) {
                  return this.map.containsKey(var4) && Double.doubleToLongBits(this.map.get(var4)) == Double.doubleToLongBits((Double)var5);
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
         } else if (var1 instanceof Byte2DoubleMap.Entry) {
            Byte2DoubleMap.Entry var8 = (Byte2DoubleMap.Entry)var1;
            return this.map.remove(var8.getByteKey(), var8.getDoubleValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Byte) {
               byte var4 = (Byte)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Double) {
                  double var6 = (Double)var5;
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

   public static class BasicEntry implements Byte2DoubleMap.Entry {
      protected byte key;
      protected double value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Byte var1, Double var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(byte var1, double var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public byte getByteKey() {
         return this.key;
      }

      public double getDoubleValue() {
         return this.value;
      }

      public double setValue(double var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Byte2DoubleMap.Entry) {
            Byte2DoubleMap.Entry var5 = (Byte2DoubleMap.Entry)var1;
            return this.key == var5.getByteKey() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(var5.getDoubleValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Byte) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Double) {
                  return this.key == (Byte)var3 && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)var4);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.key ^ HashCommon.double2int(this.value);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
