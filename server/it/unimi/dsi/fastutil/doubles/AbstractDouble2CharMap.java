package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractDouble2CharMap extends AbstractDouble2CharFunction implements Double2CharMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractDouble2CharMap() {
      super();
   }

   public boolean containsValue(char var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(double var1) {
      ObjectIterator var3 = this.double2CharEntrySet().iterator();

      do {
         if (!var3.hasNext()) {
            return false;
         }
      } while(((Double2CharMap.Entry)var3.next()).getDoubleKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public DoubleSet keySet() {
      return new AbstractDoubleSet() {
         public boolean contains(double var1) {
            return AbstractDouble2CharMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractDouble2CharMap.this.size();
         }

         public void clear() {
            AbstractDouble2CharMap.this.clear();
         }

         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Double2CharMap.Entry> i = Double2CharMaps.fastIterator(AbstractDouble2CharMap.this);

               public double nextDouble() {
                  return ((Double2CharMap.Entry)this.i.next()).getDoubleKey();
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

   public CharCollection values() {
      return new AbstractCharCollection() {
         public boolean contains(char var1) {
            return AbstractDouble2CharMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractDouble2CharMap.this.size();
         }

         public void clear() {
            AbstractDouble2CharMap.this.clear();
         }

         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Double2CharMap.Entry> i = Double2CharMaps.fastIterator(AbstractDouble2CharMap.this);

               public char nextChar() {
                  return ((Double2CharMap.Entry)this.i.next()).getCharValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Double, ? extends Character> var1) {
      if (var1 instanceof Double2CharMap) {
         ObjectIterator var2 = Double2CharMaps.fastIterator((Double2CharMap)var1);

         while(var2.hasNext()) {
            Double2CharMap.Entry var3 = (Double2CharMap.Entry)var2.next();
            this.put(var3.getDoubleKey(), var3.getCharValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Double)var4.getKey(), (Character)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Double2CharMaps.fastIterator(this); var2-- != 0; var1 += ((Double2CharMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.double2CharEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Double2CharMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Double2CharMap.Entry var4 = (Double2CharMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getDoubleKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getCharValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Double2CharMap.Entry> {
      protected final Double2CharMap map;

      public BasicEntrySet(Double2CharMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Double2CharMap.Entry) {
            Double2CharMap.Entry var7 = (Double2CharMap.Entry)var1;
            double var8 = var7.getDoubleKey();
            return this.map.containsKey(var8) && this.map.get(var8) == var7.getCharValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Double) {
               double var4 = (Double)var3;
               Object var6 = var2.getValue();
               if (var6 != null && var6 instanceof Character) {
                  return this.map.containsKey(var4) && this.map.get(var4) == (Character)var6;
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
         } else if (var1 instanceof Double2CharMap.Entry) {
            Double2CharMap.Entry var8 = (Double2CharMap.Entry)var1;
            return this.map.remove(var8.getDoubleKey(), var8.getCharValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Double) {
               double var4 = (Double)var3;
               Object var6 = var2.getValue();
               if (var6 != null && var6 instanceof Character) {
                  char var7 = (Character)var6;
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

   public static class BasicEntry implements Double2CharMap.Entry {
      protected double key;
      protected char value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Double var1, Character var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(double var1, char var3) {
         super();
         this.key = var1;
         this.value = var3;
      }

      public double getDoubleKey() {
         return this.key;
      }

      public char getCharValue() {
         return this.value;
      }

      public char setValue(char var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Double2CharMap.Entry) {
            Double2CharMap.Entry var5 = (Double2CharMap.Entry)var1;
            return Double.doubleToLongBits(this.key) == Double.doubleToLongBits(var5.getDoubleKey()) && this.value == var5.getCharValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Double) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Character) {
                  return Double.doubleToLongBits(this.key) == Double.doubleToLongBits((Double)var3) && this.value == (Character)var4;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return HashCommon.double2int(this.key) ^ this.value;
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
