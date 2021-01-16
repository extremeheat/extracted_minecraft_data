package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractFloat2FloatMap extends AbstractFloat2FloatFunction implements Float2FloatMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractFloat2FloatMap() {
      super();
   }

   public boolean containsValue(float var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(float var1) {
      ObjectIterator var2 = this.float2FloatEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Float2FloatMap.Entry)var2.next()).getFloatKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public FloatSet keySet() {
      return new AbstractFloatSet() {
         public boolean contains(float var1) {
            return AbstractFloat2FloatMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractFloat2FloatMap.this.size();
         }

         public void clear() {
            AbstractFloat2FloatMap.this.clear();
         }

         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Float2FloatMap.Entry> i = Float2FloatMaps.fastIterator(AbstractFloat2FloatMap.this);

               public float nextFloat() {
                  return ((Float2FloatMap.Entry)this.i.next()).getFloatKey();
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

   public FloatCollection values() {
      return new AbstractFloatCollection() {
         public boolean contains(float var1) {
            return AbstractFloat2FloatMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractFloat2FloatMap.this.size();
         }

         public void clear() {
            AbstractFloat2FloatMap.this.clear();
         }

         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Float2FloatMap.Entry> i = Float2FloatMaps.fastIterator(AbstractFloat2FloatMap.this);

               public float nextFloat() {
                  return ((Float2FloatMap.Entry)this.i.next()).getFloatValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Float, ? extends Float> var1) {
      if (var1 instanceof Float2FloatMap) {
         ObjectIterator var2 = Float2FloatMaps.fastIterator((Float2FloatMap)var1);

         while(var2.hasNext()) {
            Float2FloatMap.Entry var3 = (Float2FloatMap.Entry)var2.next();
            this.put(var3.getFloatKey(), var3.getFloatValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Float)var4.getKey(), (Float)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Float2FloatMaps.fastIterator(this); var2-- != 0; var1 += ((Float2FloatMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.float2FloatEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Float2FloatMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Float2FloatMap.Entry var4 = (Float2FloatMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getFloatKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getFloatValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Float2FloatMap.Entry> {
      protected final Float2FloatMap map;

      public BasicEntrySet(Float2FloatMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Float2FloatMap.Entry) {
            Float2FloatMap.Entry var6 = (Float2FloatMap.Entry)var1;
            float var7 = var6.getFloatKey();
            return this.map.containsKey(var7) && Float.floatToIntBits(this.map.get(var7)) == Float.floatToIntBits(var6.getFloatValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               float var4 = (Float)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Float) {
                  return this.map.containsKey(var4) && Float.floatToIntBits(this.map.get(var4)) == Float.floatToIntBits((Float)var5);
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
         } else if (var1 instanceof Float2FloatMap.Entry) {
            Float2FloatMap.Entry var7 = (Float2FloatMap.Entry)var1;
            return this.map.remove(var7.getFloatKey(), var7.getFloatValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               float var4 = (Float)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Float) {
                  float var6 = (Float)var5;
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

   public static class BasicEntry implements Float2FloatMap.Entry {
      protected float key;
      protected float value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Float var1, Float var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(float var1, float var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public float getFloatKey() {
         return this.key;
      }

      public float getFloatValue() {
         return this.value;
      }

      public float setValue(float var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Float2FloatMap.Entry) {
            Float2FloatMap.Entry var5 = (Float2FloatMap.Entry)var1;
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(var5.getFloatKey()) && Float.floatToIntBits(this.value) == Float.floatToIntBits(var5.getFloatValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Float) {
                  return Float.floatToIntBits(this.key) == Float.floatToIntBits((Float)var3) && Float.floatToIntBits(this.value) == Float.floatToIntBits((Float)var4);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return HashCommon.float2int(this.key) ^ HashCommon.float2int(this.value);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
