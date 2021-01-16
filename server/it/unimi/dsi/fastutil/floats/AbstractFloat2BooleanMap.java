package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractFloat2BooleanMap extends AbstractFloat2BooleanFunction implements Float2BooleanMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractFloat2BooleanMap() {
      super();
   }

   public boolean containsValue(boolean var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(float var1) {
      ObjectIterator var2 = this.float2BooleanEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Float2BooleanMap.Entry)var2.next()).getFloatKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public FloatSet keySet() {
      return new AbstractFloatSet() {
         public boolean contains(float var1) {
            return AbstractFloat2BooleanMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractFloat2BooleanMap.this.size();
         }

         public void clear() {
            AbstractFloat2BooleanMap.this.clear();
         }

         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Float2BooleanMap.Entry> i = Float2BooleanMaps.fastIterator(AbstractFloat2BooleanMap.this);

               public float nextFloat() {
                  return ((Float2BooleanMap.Entry)this.i.next()).getFloatKey();
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

   public BooleanCollection values() {
      return new AbstractBooleanCollection() {
         public boolean contains(boolean var1) {
            return AbstractFloat2BooleanMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractFloat2BooleanMap.this.size();
         }

         public void clear() {
            AbstractFloat2BooleanMap.this.clear();
         }

         public BooleanIterator iterator() {
            return new BooleanIterator() {
               private final ObjectIterator<Float2BooleanMap.Entry> i = Float2BooleanMaps.fastIterator(AbstractFloat2BooleanMap.this);

               public boolean nextBoolean() {
                  return ((Float2BooleanMap.Entry)this.i.next()).getBooleanValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Float, ? extends Boolean> var1) {
      if (var1 instanceof Float2BooleanMap) {
         ObjectIterator var2 = Float2BooleanMaps.fastIterator((Float2BooleanMap)var1);

         while(var2.hasNext()) {
            Float2BooleanMap.Entry var3 = (Float2BooleanMap.Entry)var2.next();
            this.put(var3.getFloatKey(), var3.getBooleanValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Float)var4.getKey(), (Boolean)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Float2BooleanMaps.fastIterator(this); var2-- != 0; var1 += ((Float2BooleanMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.float2BooleanEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Float2BooleanMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Float2BooleanMap.Entry var4 = (Float2BooleanMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getFloatKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getBooleanValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Float2BooleanMap.Entry> {
      protected final Float2BooleanMap map;

      public BasicEntrySet(Float2BooleanMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Float2BooleanMap.Entry) {
            Float2BooleanMap.Entry var6 = (Float2BooleanMap.Entry)var1;
            float var7 = var6.getFloatKey();
            return this.map.containsKey(var7) && this.map.get(var7) == var6.getBooleanValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               float var4 = (Float)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Boolean) {
                  return this.map.containsKey(var4) && this.map.get(var4) == (Boolean)var5;
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
         } else if (var1 instanceof Float2BooleanMap.Entry) {
            Float2BooleanMap.Entry var7 = (Float2BooleanMap.Entry)var1;
            return this.map.remove(var7.getFloatKey(), var7.getBooleanValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               float var4 = (Float)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Boolean) {
                  boolean var6 = (Boolean)var5;
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

   public static class BasicEntry implements Float2BooleanMap.Entry {
      protected float key;
      protected boolean value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Float var1, Boolean var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(float var1, boolean var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public float getFloatKey() {
         return this.key;
      }

      public boolean getBooleanValue() {
         return this.value;
      }

      public boolean setValue(boolean var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Float2BooleanMap.Entry) {
            Float2BooleanMap.Entry var5 = (Float2BooleanMap.Entry)var1;
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(var5.getFloatKey()) && this.value == var5.getBooleanValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Boolean) {
                  return Float.floatToIntBits(this.key) == Float.floatToIntBits((Float)var3) && this.value == (Boolean)var4;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return HashCommon.float2int(this.key) ^ (this.value ? 1231 : 1237);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
