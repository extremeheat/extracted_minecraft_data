package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractChar2CharMap extends AbstractChar2CharFunction implements Char2CharMap, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractChar2CharMap() {
      super();
   }

   public boolean containsValue(char var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(char var1) {
      ObjectIterator var2 = this.char2CharEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Char2CharMap.Entry)var2.next()).getCharKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public CharSet keySet() {
      return new AbstractCharSet() {
         public boolean contains(char var1) {
            return AbstractChar2CharMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractChar2CharMap.this.size();
         }

         public void clear() {
            AbstractChar2CharMap.this.clear();
         }

         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Char2CharMap.Entry> i = Char2CharMaps.fastIterator(AbstractChar2CharMap.this);

               public char nextChar() {
                  return ((Char2CharMap.Entry)this.i.next()).getCharKey();
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
            return AbstractChar2CharMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractChar2CharMap.this.size();
         }

         public void clear() {
            AbstractChar2CharMap.this.clear();
         }

         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Char2CharMap.Entry> i = Char2CharMaps.fastIterator(AbstractChar2CharMap.this);

               public char nextChar() {
                  return ((Char2CharMap.Entry)this.i.next()).getCharValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Character, ? extends Character> var1) {
      if (var1 instanceof Char2CharMap) {
         ObjectIterator var2 = Char2CharMaps.fastIterator((Char2CharMap)var1);

         while(var2.hasNext()) {
            Char2CharMap.Entry var3 = (Char2CharMap.Entry)var2.next();
            this.put(var3.getCharKey(), var3.getCharValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Character)var4.getKey(), (Character)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Char2CharMaps.fastIterator(this); var2-- != 0; var1 += ((Char2CharMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.char2CharEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Char2CharMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Char2CharMap.Entry var4 = (Char2CharMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getCharKey()));
         var1.append("=>");
         var1.append(String.valueOf(var4.getCharValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet extends AbstractObjectSet<Char2CharMap.Entry> {
      protected final Char2CharMap map;

      public BasicEntrySet(Char2CharMap var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Char2CharMap.Entry) {
            Char2CharMap.Entry var6 = (Char2CharMap.Entry)var1;
            char var7 = var6.getCharKey();
            return this.map.containsKey(var7) && this.map.get(var7) == var6.getCharValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Character) {
               char var4 = (Character)var3;
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
         } else if (var1 instanceof Char2CharMap.Entry) {
            Char2CharMap.Entry var7 = (Char2CharMap.Entry)var1;
            return this.map.remove(var7.getCharKey(), var7.getCharValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Character) {
               char var4 = (Character)var3;
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

   public static class BasicEntry implements Char2CharMap.Entry {
      protected char key;
      protected char value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Character var1, Character var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(char var1, char var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public char getCharKey() {
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
         } else if (var1 instanceof Char2CharMap.Entry) {
            Char2CharMap.Entry var5 = (Char2CharMap.Entry)var1;
            return this.key == var5.getCharKey() && this.value == var5.getCharValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Character) {
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Character) {
                  return this.key == (Character)var3 && this.value == (Character)var4;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.key ^ this.value;
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
