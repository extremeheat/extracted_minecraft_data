package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractReference2FloatMap<K> extends AbstractReference2FloatFunction<K> implements Reference2FloatMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2FloatMap() {
      super();
   }

   public boolean containsValue(float var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(Object var1) {
      ObjectIterator var2 = this.reference2FloatEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Reference2FloatMap.Entry)var2.next()).getKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ReferenceSet<K> keySet() {
      return new AbstractReferenceSet<K>() {
         public boolean contains(Object var1) {
            return AbstractReference2FloatMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractReference2FloatMap.this.size();
         }

         public void clear() {
            AbstractReference2FloatMap.this.clear();
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2FloatMap.Entry<K>> i = Reference2FloatMaps.fastIterator(AbstractReference2FloatMap.this);

               public K next() {
                  return ((Reference2FloatMap.Entry)this.i.next()).getKey();
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
            return AbstractReference2FloatMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractReference2FloatMap.this.size();
         }

         public void clear() {
            AbstractReference2FloatMap.this.clear();
         }

         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Reference2FloatMap.Entry<K>> i = Reference2FloatMaps.fastIterator(AbstractReference2FloatMap.this);

               public float nextFloat() {
                  return ((Reference2FloatMap.Entry)this.i.next()).getFloatValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends K, ? extends Float> var1) {
      if (var1 instanceof Reference2FloatMap) {
         ObjectIterator var2 = Reference2FloatMaps.fastIterator((Reference2FloatMap)var1);

         while(var2.hasNext()) {
            Reference2FloatMap.Entry var3 = (Reference2FloatMap.Entry)var2.next();
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

      for(ObjectIterator var3 = Reference2FloatMaps.fastIterator(this); var2-- != 0; var1 += ((Reference2FloatMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.reference2FloatEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Reference2FloatMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Reference2FloatMap.Entry var4 = (Reference2FloatMap.Entry)var2.next();
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

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Reference2FloatMap.Entry<K>> {
      protected final Reference2FloatMap<K> map;

      public BasicEntrySet(Reference2FloatMap<K> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            Object var3;
            if (var1 instanceof Reference2FloatMap.Entry) {
               Reference2FloatMap.Entry var5 = (Reference2FloatMap.Entry)var1;
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
         } else if (var1 instanceof Reference2FloatMap.Entry) {
            Reference2FloatMap.Entry var6 = (Reference2FloatMap.Entry)var1;
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

   public static class BasicEntry<K> implements Reference2FloatMap.Entry<K> {
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
         } else if (var1 instanceof Reference2FloatMap.Entry) {
            Reference2FloatMap.Entry var5 = (Reference2FloatMap.Entry)var1;
            return this.key == var5.getKey() && Float.floatToIntBits(this.value) == Float.floatToIntBits(var5.getFloatValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Float) {
               return this.key == var3 && Float.floatToIntBits(this.value) == Float.floatToIntBits((Float)var4);
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return System.identityHashCode(this.key) ^ HashCommon.float2int(this.value);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
