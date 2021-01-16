package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractFloat2DoubleMap extends AbstractFloat2DoubleFunction implements Float2DoubleMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractFloat2DoubleMap() {
      super();
   }

   public boolean containsValue(double var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(float var1) {
      ObjectIterator var2 = this.float2DoubleEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Float2DoubleMap.Entry)var2.next()).getFloatKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public FloatSet keySet() {
      return new AbstractFloatSet() {
         public boolean contains(float var1) {
            return AbstractFloat2DoubleMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractFloat2DoubleMap.this.size();
         }

         public void clear() {
            AbstractFloat2DoubleMap.this.clear();
         }

         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Float2DoubleMap.Entry> i = Float2DoubleMaps.fastIterator(AbstractFloat2DoubleMap.this);

               public float nextFloat() {
                  return ((Float2DoubleMap.Entry)this.i.next()).getFloatKey();
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
            return AbstractFloat2DoubleMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractFloat2DoubleMap.this.size();
         }

         public void clear() {
            AbstractFloat2DoubleMap.this.clear();
         }

         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Float2DoubleMap.Entry> i = Float2DoubleMaps.fastIterator(AbstractFloat2DoubleMap.this);

               public double nextDouble() {
                  return ((Float2DoubleMap.Entry)this.i.next()).getDoubleValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Float, ? extends Double> var1) {
      if (var1 instanceof Float2DoubleMap) {
         ObjectIterator var2 = Float2DoubleMaps.fastIterator((Float2DoubleMap)var1);

         while(var2.hasNext()) {
            Float2DoubleMap.Entry var3 = (Float2DoubleMap.Entry)var2.next();
            this.put(var3.getFloatKey(), var3.getDoubleValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Float)var4.getKey(), (Double)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Float2DoubleMaps.fastIterator(this); var2-- != 0; var1 += ((Float2DoubleMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.float2DoubleEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Float2DoubleMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Float2DoubleMap.Entry var4 = (Float2DoubleMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getFloatKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getDoubleValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Float2DoubleMap.Entry> {
      protected final Float2DoubleMap map;

      public BasicEntrySet(Float2DoubleMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Float2DoubleMap.Entry) {
            Float2DoubleMap.Entry var6 = (Float2DoubleMap.Entry)var1;
            float var7 = var6.getFloatKey();
            return this.map.containsKey(var7) && Double.doubleToLongBits(this.map.get(var7)) == Double.doubleToLongBits(var6.getDoubleValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               float var4 = (Float)var3;
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
         } else if (var1 instanceof Float2DoubleMap.Entry) {
            Float2DoubleMap.Entry var8 = (Float2DoubleMap.Entry)var1;
            return this.map.remove(var8.getFloatKey(), var8.getDoubleValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               float var4 = (Float)var3;
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

   public static class BasicEntry implements Float2DoubleMap.Entry {
      protected float key;
      protected double value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Float var1, Double var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(float var1, double var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public float getFloatKey() {
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
         } else if (var1 instanceof Float2DoubleMap.Entry) {
            Float2DoubleMap.Entry var5 = (Float2DoubleMap.Entry)var1;
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(var5.getFloatKey()) && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(var5.getDoubleValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Double) {
                  return Float.floatToIntBits(this.key) == Float.floatToIntBits((Float)var3) && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)var4);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return HashCommon.float2int(this.key) ^ HashCommon.double2int(this.value);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
