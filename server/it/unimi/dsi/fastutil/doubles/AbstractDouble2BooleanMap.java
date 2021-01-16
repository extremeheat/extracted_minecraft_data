package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractDouble2BooleanMap extends AbstractDouble2BooleanFunction implements Double2BooleanMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractDouble2BooleanMap() {
      super();
   }

   public boolean containsValue(boolean var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(double var1) {
      ObjectIterator var3 = this.double2BooleanEntrySet().iterator();

      do {
         if (!var3.hasNext()) {
            return false;
         }
      } while(((Double2BooleanMap.Entry)var3.next()).getDoubleKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public DoubleSet keySet() {
      return new AbstractDoubleSet() {
         public boolean contains(double var1) {
            return AbstractDouble2BooleanMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractDouble2BooleanMap.this.size();
         }

         public void clear() {
            AbstractDouble2BooleanMap.this.clear();
         }

         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Double2BooleanMap.Entry> i = Double2BooleanMaps.fastIterator(AbstractDouble2BooleanMap.this);

               public double nextDouble() {
                  return ((Double2BooleanMap.Entry)this.i.next()).getDoubleKey();
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
            return AbstractDouble2BooleanMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractDouble2BooleanMap.this.size();
         }

         public void clear() {
            AbstractDouble2BooleanMap.this.clear();
         }

         public BooleanIterator iterator() {
            return new BooleanIterator() {
               private final ObjectIterator<Double2BooleanMap.Entry> i = Double2BooleanMaps.fastIterator(AbstractDouble2BooleanMap.this);

               public boolean nextBoolean() {
                  return ((Double2BooleanMap.Entry)this.i.next()).getBooleanValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Double, ? extends Boolean> var1) {
      if (var1 instanceof Double2BooleanMap) {
         ObjectIterator var2 = Double2BooleanMaps.fastIterator((Double2BooleanMap)var1);

         while(var2.hasNext()) {
            Double2BooleanMap.Entry var3 = (Double2BooleanMap.Entry)var2.next();
            this.put(var3.getDoubleKey(), var3.getBooleanValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Double)var4.getKey(), (Boolean)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Double2BooleanMaps.fastIterator(this); var2-- != 0; var1 += ((Double2BooleanMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.double2BooleanEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Double2BooleanMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Double2BooleanMap.Entry var4 = (Double2BooleanMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getDoubleKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getBooleanValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Double2BooleanMap.Entry> {
      protected final Double2BooleanMap map;

      public BasicEntrySet(Double2BooleanMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Double2BooleanMap.Entry) {
            Double2BooleanMap.Entry var7 = (Double2BooleanMap.Entry)var1;
            double var8 = var7.getDoubleKey();
            return this.map.containsKey(var8) && this.map.get(var8) == var7.getBooleanValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Double) {
               double var4 = (Double)var3;
               Object var6 = var2.getValue();
               if (var6 != null && var6 instanceof Boolean) {
                  return this.map.containsKey(var4) && this.map.get(var4) == (Boolean)var6;
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
         } else if (var1 instanceof Double2BooleanMap.Entry) {
            Double2BooleanMap.Entry var8 = (Double2BooleanMap.Entry)var1;
            return this.map.remove(var8.getDoubleKey(), var8.getBooleanValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Double) {
               double var4 = (Double)var3;
               Object var6 = var2.getValue();
               if (var6 != null && var6 instanceof Boolean) {
                  boolean var7 = (Boolean)var6;
                  return this.map.remove(var4, var7);
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

   public static class BasicEntry implements Double2BooleanMap.Entry {
      protected double key;
      protected boolean value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Double var1, Boolean var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(double var1, boolean var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public double getDoubleKey() {
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
         } else if (var1 instanceof Double2BooleanMap.Entry) {
            Double2BooleanMap.Entry var5 = (Double2BooleanMap.Entry)var1;
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(var5.getDoubleKey()) && this.value == var5.getBooleanValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Double) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Boolean) {
                  return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)var3) && this.value == (Boolean)var4;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return HashCommon.double2int(this.key) ^ (this.value ? 1231 : 1237);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
