package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractFloat2ShortMap extends AbstractFloat2ShortFunction implements Float2ShortMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractFloat2ShortMap() {
      super();
   }

   public boolean containsValue(short var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(float var1) {
      ObjectIterator var2 = this.float2ShortEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Float2ShortMap.Entry)var2.next()).getFloatKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public FloatSet keySet() {
      return new AbstractFloatSet() {
         public boolean contains(float var1) {
            return AbstractFloat2ShortMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractFloat2ShortMap.this.size();
         }

         public void clear() {
            AbstractFloat2ShortMap.this.clear();
         }

         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Float2ShortMap.Entry> i = Float2ShortMaps.fastIterator(AbstractFloat2ShortMap.this);

               public float nextFloat() {
                  return ((Float2ShortMap.Entry)this.i.next()).getFloatKey();
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
            return AbstractFloat2ShortMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractFloat2ShortMap.this.size();
         }

         public void clear() {
            AbstractFloat2ShortMap.this.clear();
         }

         public ShortIterator iterator() {
            return new ShortIterator() {
               private final ObjectIterator<Float2ShortMap.Entry> i = Float2ShortMaps.fastIterator(AbstractFloat2ShortMap.this);

               public short nextShort() {
                  return ((Float2ShortMap.Entry)this.i.next()).getShortValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Float, ? extends Short> var1) {
      if (var1 instanceof Float2ShortMap) {
         ObjectIterator var2 = Float2ShortMaps.fastIterator((Float2ShortMap)var1);

         while(var2.hasNext()) {
            Float2ShortMap.Entry var3 = (Float2ShortMap.Entry)var2.next();
            this.put(var3.getFloatKey(), var3.getShortValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Float)var4.getKey(), (Short)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Float2ShortMaps.fastIterator(this); var2-- != 0; var1 += ((Float2ShortMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.float2ShortEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Float2ShortMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Float2ShortMap.Entry var4 = (Float2ShortMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getFloatKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getShortValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Float2ShortMap.Entry> {
      protected final Float2ShortMap map;

      public BasicEntrySet(Float2ShortMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Float2ShortMap.Entry) {
            Float2ShortMap.Entry var6 = (Float2ShortMap.Entry)var1;
            float var7 = var6.getFloatKey();
            return this.map.containsKey(var7) && this.map.get(var7) == var6.getShortValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               float var4 = (Float)var3;
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
         } else if (var1 instanceof Float2ShortMap.Entry) {
            Float2ShortMap.Entry var7 = (Float2ShortMap.Entry)var1;
            return this.map.remove(var7.getFloatKey(), var7.getShortValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               float var4 = (Float)var3;
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

   public static class BasicEntry implements Float2ShortMap.Entry {
      protected float key;
      protected short value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Float var1, Short var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(float var1, short var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public float getFloatKey() {
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
         } else if (var1 instanceof Float2ShortMap.Entry) {
            Float2ShortMap.Entry var5 = (Float2ShortMap.Entry)var1;
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(var5.getFloatKey()) && this.value == var5.getShortValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Short) {
                  return Float.floatToIntBits(this.key) == Float.floatToIntBits((Float)var3) && this.value == (Short)var4;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return HashCommon.float2int(this.key) ^ this.value;
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
