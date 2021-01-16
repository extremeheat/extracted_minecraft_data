package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractChar2DoubleMap extends AbstractChar2DoubleFunction implements Char2DoubleMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractChar2DoubleMap() {
      super();
   }

   public boolean containsValue(double var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(char var1) {
      ObjectIterator var2 = this.char2DoubleEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Char2DoubleMap.Entry)var2.next()).getCharKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public CharSet keySet() {
      return new AbstractCharSet() {
         public boolean contains(char var1) {
            return AbstractChar2DoubleMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractChar2DoubleMap.this.size();
         }

         public void clear() {
            AbstractChar2DoubleMap.this.clear();
         }

         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Char2DoubleMap.Entry> i = Char2DoubleMaps.fastIterator(AbstractChar2DoubleMap.this);

               public char nextChar() {
                  return ((Char2DoubleMap.Entry)this.i.next()).getCharKey();
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
            return AbstractChar2DoubleMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractChar2DoubleMap.this.size();
         }

         public void clear() {
            AbstractChar2DoubleMap.this.clear();
         }

         public DoubleIterator iterator() {
            return new DoubleIterator() {
               private final ObjectIterator<Char2DoubleMap.Entry> i = Char2DoubleMaps.fastIterator(AbstractChar2DoubleMap.this);

               public double nextDouble() {
                  return ((Char2DoubleMap.Entry)this.i.next()).getDoubleValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Character, ? extends Double> var1) {
      if (var1 instanceof Char2DoubleMap) {
         ObjectIterator var2 = Char2DoubleMaps.fastIterator((Char2DoubleMap)var1);

         while(var2.hasNext()) {
            Char2DoubleMap.Entry var3 = (Char2DoubleMap.Entry)var2.next();
            this.put(var3.getCharKey(), var3.getDoubleValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Character)var4.getKey(), (Double)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Char2DoubleMaps.fastIterator(this); var2-- != 0; var1 += ((Char2DoubleMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.char2DoubleEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Char2DoubleMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Char2DoubleMap.Entry var4 = (Char2DoubleMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getCharKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getDoubleValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Char2DoubleMap.Entry> {
      protected final Char2DoubleMap map;

      public BasicEntrySet(Char2DoubleMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Char2DoubleMap.Entry) {
            Char2DoubleMap.Entry var6 = (Char2DoubleMap.Entry)var1;
            char var7 = var6.getCharKey();
            return this.map.containsKey(var7) && Double.doubleToLongBits(this.map.get(var7)) == Double.doubleToLongBits(var6.getDoubleValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Character) {
               char var4 = (Character)var3;
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
         } else if (var1 instanceof Char2DoubleMap.Entry) {
            Char2DoubleMap.Entry var8 = (Char2DoubleMap.Entry)var1;
            return this.map.remove(var8.getCharKey(), var8.getDoubleValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Character) {
               char var4 = (Character)var3;
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

   public static class BasicEntry implements Char2DoubleMap.Entry {
      protected char key;
      protected double value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Character var1, Double var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(char var1, double var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public char getCharKey() {
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
         } else if (var1 instanceof Char2DoubleMap.Entry) {
            Char2DoubleMap.Entry var5 = (Char2DoubleMap.Entry)var1;
            return this.key == var5.getCharKey() && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(var5.getDoubleValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Character) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Double) {
                  return this.key == (Character)var3 && Double.doubleToLongBits(this.value) == Double.doubleToLongBits((Double)var4);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.key ^ HashCommon.double2int(this.value);
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
