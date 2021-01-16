package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractReference2DoubleMap<K> extends AbstractReference2DoubleFunction<K> implements Reference2DoubleMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2DoubleMap() {
      super();
   }

   public boolean containsValue(double var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(Object var1) {
      ObjectIterator var2 = this.reference2DoubleEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Reference2DoubleMap.Entry)var2.next()).getKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ReferenceSet<K> keySet() {
      return new AbstractReferenceSet<K>() {
         public boolean contains(Object var1) {
            return AbstractReference2DoubleMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractReference2DoubleMap.this.size();
         }

         public void clear() {
            AbstractReference2DoubleMap.this.clear();
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2DoubleMap.Entry<K>> i = Reference2DoubleMaps.fastIterator(AbstractReference2DoubleMap.this);

               public K next() {
                  return ((Reference2DoubleMap.Entry)this.i.next()).getKey();
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
            return AbstractReference2DoubleMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractReference2DoubleMap.this.size();
         }

         public void clear() {
            AbstractReference2DoubleMap.this.clear();
         }

         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Reference2DoubleMap.Entry<K>> i = Reference2DoubleMaps.fastIterator(AbstractReference2DoubleMap.this);

               public double nextDouble() {
                  return ((Reference2DoubleMap.Entry)this.i.next()).getDoubleValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends K, ? extends Double> var1) {
      if (var1 instanceof Reference2DoubleMap) {
         ObjectIterator var2 = Reference2DoubleMaps.fastIterator((Reference2DoubleMap)var1);

         while(var2.hasNext()) {
            Reference2DoubleMap.Entry var3 = (Reference2DoubleMap.Entry)var2.next();
            this.put(var3.getKey(), var3.getDoubleValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put(var4.getKey(), (Double)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Reference2DoubleMaps.fastIterator(this); var2-- != 0; var1 += ((Reference2DoubleMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.reference2DoubleEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Reference2DoubleMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Reference2DoubleMap.Entry var4 = (Reference2DoubleMap.Entry)var2.next();
         if (this == var4.getKey()) {
            var1.append("(this map)");
         } else {
            var1.append(String.valueOf(var4.getKey()));
         }

         var1.append("=>");
         var1.append(String.valueOf(var4.getDoubleValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Reference2DoubleMap.Entry<K>> {
      protected final Reference2DoubleMap<K> map;

      public BasicEntrySet(Reference2DoubleMap<K> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            Object var3;
            if (var1 instanceof Reference2DoubleMap.Entry) {
               Reference2DoubleMap.Entry var5 = (Reference2DoubleMap.Entry)var1;
               var3 = var5.getKey();
               return this.map.containsKey(var3) && Double.doubleToLongBits(this.map.getDouble(var3)) == Double.doubleToLongBits(var5.getDoubleValue());
            } else {
               java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
               var3 = var2.getKey();
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Double) {
                  return this.map.containsKey(var3) && Double.doubleToLongBits(this.map.getDouble(var3)) == Double.doubleToLongBits((Double)var4);
               } else {
                  return false;
               }
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Reference2DoubleMap.Entry) {
            Reference2DoubleMap.Entry var7 = (Reference2DoubleMap.Entry)var1;
            return this.map.remove(var7.getKey(), var7.getDoubleValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Double) {
               double var5 = (Double)var4;
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

   public static class BasicEntry<K> implements Reference2DoubleMap.Entry<K> {
      protected K key;
      protected double value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(K var1, Double var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(K var1, double var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public K getKey() {
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
         } else if (var1 instanceof Reference2DoubleMap.Entry) {
            Reference2DoubleMap.Entry var5 = (Reference2DoubleMap.Entry)var1;
            return this.key == var5.getKey() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(var5.getDoubleValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Double) {
               return this.key == var3 && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)var4);
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return System.identityHashCode(this.key) ^ HashCommon.double2int(this.value);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
