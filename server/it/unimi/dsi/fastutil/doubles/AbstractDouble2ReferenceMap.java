package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractDouble2ReferenceMap<V> extends AbstractDouble2ReferenceFunction<V> implements Double2ReferenceMap<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractDouble2ReferenceMap() {
      super();
   }

   public boolean containsValue(Object var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(double var1) {
      ObjectIterator var3 = this.double2ReferenceEntrySet().iterator();

      do {
         if (!var3.hasNext()) {
            return false;
         }
      } while(((Double2ReferenceMap.Entry)var3.next()).getDoubleKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public DoubleSet keySet() {
      return new AbstractDoubleSet() {
         public boolean contains(double var1) {
            return AbstractDouble2ReferenceMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractDouble2ReferenceMap.this.size();
         }

         public void clear() {
            AbstractDouble2ReferenceMap.this.clear();
         }

         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Double2ReferenceMap.Entry<V>> i = Double2ReferenceMaps.fastIterator(AbstractDouble2ReferenceMap.this);

               public double nextDouble() {
                  return ((Double2ReferenceMap.Entry)this.i.next()).getDoubleKey();
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

   public ReferenceCollection<V> values() {
      return new AbstractReferenceCollection<V>() {
         public boolean contains(Object var1) {
            return AbstractDouble2ReferenceMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractDouble2ReferenceMap.this.size();
         }

         public void clear() {
            AbstractDouble2ReferenceMap.this.clear();
         }

         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Double2ReferenceMap.Entry<V>> i = Double2ReferenceMaps.fastIterator(AbstractDouble2ReferenceMap.this);

               public V next() {
                  return ((Double2ReferenceMap.Entry)this.i.next()).getValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Double, ? extends V> var1) {
      if (var1 instanceof Double2ReferenceMap) {
         ObjectIterator var2 = Double2ReferenceMaps.fastIterator((Double2ReferenceMap)var1);

         while(var2.hasNext()) {
            Double2ReferenceMap.Entry var3 = (Double2ReferenceMap.Entry)var2.next();
            this.put(var3.getDoubleKey(), var3.getValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Double)var4.getKey(), var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Double2ReferenceMaps.fastIterator(this); var2-- != 0; var1 += ((Double2ReferenceMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.double2ReferenceEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Double2ReferenceMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Double2ReferenceMap.Entry var4 = (Double2ReferenceMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getDoubleKey()));
         var1.append("=>");
         if (this == var4.getValue()) {
            var1.append("(this map)");
         } else {
            var1.append(String.valueOf(var4.getValue()));
         }
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet<V> extends AbstractObjectSet<Double2ReferenceMap.Entry<V>> {
      protected final Double2ReferenceMap<V> map;

      public BasicEntrySet(Double2ReferenceMap<V> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Double2ReferenceMap.Entry) {
            Double2ReferenceMap.Entry var7 = (Double2ReferenceMap.Entry)var1;
            double var8 = var7.getDoubleKey();
            return this.map.containsKey(var8) && this.map.get(var8) == var7.getValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Double) {
               double var4 = (Double)var3;
               Object var6 = var2.getValue();
               return this.map.containsKey(var4) && this.map.get(var4) == var6;
            } else {
               return false;
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Double2ReferenceMap.Entry) {
            Double2ReferenceMap.Entry var7 = (Double2ReferenceMap.Entry)var1;
            return this.map.remove(var7.getDoubleKey(), var7.getValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Double) {
               double var4 = (Double)var3;
               Object var6 = var2.getValue();
               return this.map.remove(var4, var6);
            } else {
               return false;
            }
         }
      }

      public int size() {
         return this.map.size();
      }
   }

   public static class BasicEntry<V> implements Double2ReferenceMap.Entry<V> {
      protected double key;
      protected V value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Double var1, V var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(double var1, V var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public double getDoubleKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Double2ReferenceMap.Entry) {
            Double2ReferenceMap.Entry var5 = (Double2ReferenceMap.Entry)var1;
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(var5.getDoubleKey()) && this.value == var5.getValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Double) {
               Object var4 = var2.getValue();
               return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)var3) && this.value == var4;
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return HashCommon.double2int(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
