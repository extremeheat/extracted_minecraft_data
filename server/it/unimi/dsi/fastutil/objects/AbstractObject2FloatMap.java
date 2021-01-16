package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractObject2FloatMap<K> extends AbstractObject2FloatFunction<K> implements Object2FloatMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractObject2FloatMap() {
      super();
   }

   public boolean containsValue(float var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(Object var1) {
      ObjectIterator var2 = this.object2FloatEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Object2FloatMap.Entry)var2.next()).getKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ObjectSet<K> keySet() {
      return new AbstractObjectSet<K>() {
         public boolean contains(Object var1) {
            return AbstractObject2FloatMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractObject2FloatMap.this.size();
         }

         public void clear() {
            AbstractObject2FloatMap.this.clear();
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Object2FloatMap.Entry<K>> i = Object2FloatMaps.fastIterator(AbstractObject2FloatMap.this);

               public K next() {
                  return ((Object2FloatMap.Entry)this.i.next()).getKey();
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
            return AbstractObject2FloatMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractObject2FloatMap.this.size();
         }

         public void clear() {
            AbstractObject2FloatMap.this.clear();
         }

         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Object2FloatMap.Entry<K>> i = Object2FloatMaps.fastIterator(AbstractObject2FloatMap.this);

               public float nextFloat() {
                  return ((Object2FloatMap.Entry)this.i.next()).getFloatValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends K, ? extends Float> var1) {
      if (var1 instanceof Object2FloatMap) {
         ObjectIterator var2 = Object2FloatMaps.fastIterator((Object2FloatMap)var1);

         while(var2.hasNext()) {
            Object2FloatMap.Entry var3 = (Object2FloatMap.Entry)var2.next();
            this.put(var3.getKey(), var3.getFloatValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put(var4.getKey(), (Float)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Object2FloatMaps.fastIterator(this); var2-- != 0; var1 += ((Object2FloatMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.object2FloatEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Object2FloatMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Object2FloatMap.Entry var4 = (Object2FloatMap.Entry)var2.next();
         if (this == var4.getKey()) {
            var1.append("(this map)");
         } else {
            var1.append(String.valueOf(var4.getKey()));
         }

         var1.append("=>");
         var1.append(String.valueOf(var4.getFloatValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Object2FloatMap.Entry<K>> {
      protected final Object2FloatMap<K> map;

      public BasicEntrySet(Object2FloatMap<K> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            Object var3;
            if (var1 instanceof Object2FloatMap.Entry) {
               Object2FloatMap.Entry var5 = (Object2FloatMap.Entry)var1;
               var3 = var5.getKey();
               return this.map.containsKey(var3) && Float.floatToIntBits(this.map.getFloat(var3)) == Float.floatToIntBits(var5.getFloatValue());
            } else {
               java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
               var3 = var2.getKey();
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Float) {
                  return this.map.containsKey(var3) && Float.floatToIntBits(this.map.getFloat(var3)) == Float.floatToIntBits((Float)var4);
               } else {
                  return false;
               }
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Object2FloatMap.Entry) {
            Object2FloatMap.Entry var6 = (Object2FloatMap.Entry)var1;
            return this.map.remove(var6.getKey(), var6.getFloatValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Float) {
               float var5 = (Float)var4;
               return this.map.remove(var3, var5);
            } else {
               return false;
            }
         }
      }

      public int size() {
         return this.map.size();
      }
   }

   public static class BasicEntry<K> implements Object2FloatMap.Entry<K> {
      protected K key;
      protected float value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(K var1, Float var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(K var1, float var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public K getKey() {
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
         } else if (var1 instanceof Object2FloatMap.Entry) {
            Object2FloatMap.Entry var5 = (Object2FloatMap.Entry)var1;
            return Objects.equals(this.key, var5.getKey()) && Float.floatToIntBits(this.value) == Float.floatToIntBits(var5.getFloatValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Float) {
               return Objects.equals(this.key, var3) && Float.floatToIntBits(this.value) == Float.floatToIntBits((Float)var4);
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ HashCommon.float2int(this.value);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
