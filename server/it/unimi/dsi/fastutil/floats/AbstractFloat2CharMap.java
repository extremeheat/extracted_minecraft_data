package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractFloat2CharMap extends AbstractFloat2CharFunction implements Float2CharMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractFloat2CharMap() {
      super();
   }

   public boolean containsValue(char var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(float var1) {
      ObjectIterator var2 = this.float2CharEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Float2CharMap.Entry)var2.next()).getFloatKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public FloatSet keySet() {
      return new AbstractFloatSet() {
         public boolean contains(float var1) {
            return AbstractFloat2CharMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractFloat2CharMap.this.size();
         }

         public void clear() {
            AbstractFloat2CharMap.this.clear();
         }

         public FloatIterator iterator() {
            return new FloatIterator() {
               private final ObjectIterator<Float2CharMap.Entry> i = Float2CharMaps.fastIterator(AbstractFloat2CharMap.this);

               public float nextFloat() {
                  return ((Float2CharMap.Entry)this.i.next()).getFloatKey();
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
            return AbstractFloat2CharMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractFloat2CharMap.this.size();
         }

         public void clear() {
            AbstractFloat2CharMap.this.clear();
         }

         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Float2CharMap.Entry> i = Float2CharMaps.fastIterator(AbstractFloat2CharMap.this);

               public char nextChar() {
                  return ((Float2CharMap.Entry)this.i.next()).getCharValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Float, ? extends Character> var1) {
      if (var1 instanceof Float2CharMap) {
         ObjectIterator var2 = Float2CharMaps.fastIterator((Float2CharMap)var1);

         while(var2.hasNext()) {
            Float2CharMap.Entry var3 = (Float2CharMap.Entry)var2.next();
            this.put(var3.getFloatKey(), var3.getCharValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Float)var4.getKey(), (Character)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Float2CharMaps.fastIterator(this); var2-- != 0; var1 += ((Float2CharMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.float2CharEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Float2CharMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Float2CharMap.Entry var4 = (Float2CharMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getFloatKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getCharValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Float2CharMap.Entry> {
      protected final Float2CharMap map;

      public BasicEntrySet(Float2CharMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Float2CharMap.Entry) {
            Float2CharMap.Entry var6 = (Float2CharMap.Entry)var1;
            float var7 = var6.getFloatKey();
            return this.map.containsKey(var7) && this.map.get(var7) == var6.getCharValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               float var4 = (Float)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Character) {
                  return this.map.containsKey(var4) && this.map.get(var4) == (Character)var5;
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
         } else if (var1 instanceof Float2CharMap.Entry) {
            Float2CharMap.Entry var7 = (Float2CharMap.Entry)var1;
            return this.map.remove(var7.getFloatKey(), var7.getCharValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               float var4 = (Float)var3;
               Object var5 = var2.getValue();
               if (var5 != null && var5 instanceof Character) {
                  char var6 = (Character)var5;
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

   public static class BasicEntry implements Float2CharMap.Entry {
      protected float key;
      protected char value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Float var1, Character var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(float var1, char var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public float getFloatKey() {
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
         } else if (var1 instanceof Float2CharMap.Entry) {
            Float2CharMap.Entry var5 = (Float2CharMap.Entry)var1;
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(var5.getFloatKey()) && this.value == var5.getCharValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Float) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Character) {
                  return Float.floatToIntBits(this.key) == Float.floatToIntBits((Float)var3) && this.value == (Character)var4;
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
