package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractReference2CharMap<K> extends AbstractReference2CharFunction<K> implements Reference2CharMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractReference2CharMap() {
      super();
   }

   public boolean containsValue(char var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(Object var1) {
      ObjectIterator var2 = this.reference2CharEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Reference2CharMap.Entry)var2.next()).getKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ReferenceSet<K> keySet() {
      return new AbstractReferenceSet<K>() {
         public boolean contains(Object var1) {
            return AbstractReference2CharMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractReference2CharMap.this.size();
         }

         public void clear() {
            AbstractReference2CharMap.this.clear();
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Reference2CharMap.Entry<K>> i = Reference2CharMaps.fastIterator(AbstractReference2CharMap.this);

               public K next() {
                  return ((Reference2CharMap.Entry)this.i.next()).getKey();
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
            return AbstractReference2CharMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractReference2CharMap.this.size();
         }

         public void clear() {
            AbstractReference2CharMap.this.clear();
         }

         public CharIterator iterator() {
            return new CharIterator() {
               private final ObjectIterator<Reference2CharMap.Entry<K>> i = Reference2CharMaps.fastIterator(AbstractReference2CharMap.this);

               public char nextChar() {
                  return ((Reference2CharMap.Entry)this.i.next()).getCharValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends K, ? extends Character> var1) {
      if (var1 instanceof Reference2CharMap) {
         ObjectIterator var2 = Reference2CharMaps.fastIterator((Reference2CharMap)var1);

         while(var2.hasNext()) {
            Reference2CharMap.Entry var3 = (Reference2CharMap.Entry)var2.next();
            this.put(var3.getKey(), var3.getCharValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put(var4.getKey(), (Character)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Reference2CharMaps.fastIterator(this); var2-- != 0; var1 += ((Reference2CharMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.reference2CharEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Reference2CharMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Reference2CharMap.Entry var4 = (Reference2CharMap.Entry)var2.next();
         if (this == var4.getKey()) {
            var1.append("(this map)");
         } else {
            var1.append(String.valueOf(var4.getKey()));
         }

         var1.append("=>");
         var1.append(String.valueOf(var4.getCharValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Reference2CharMap.Entry<K>> {
      protected final Reference2CharMap<K> map;

      public BasicEntrySet(Reference2CharMap<K> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            Object var3;
            if (var1 instanceof Reference2CharMap.Entry) {
               Reference2CharMap.Entry var5 = (Reference2CharMap.Entry)var1;
               var3 = var5.getKey();
               return this.map.containsKey(var3) && this.map.getChar(var3) == var5.getCharValue();
            } else {
               java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
               var3 = var2.getKey();
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Character) {
                  return this.map.containsKey(var3) && this.map.getChar(var3) == (Character)var4;
               } else {
                  return false;
               }
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Reference2CharMap.Entry) {
            Reference2CharMap.Entry var6 = (Reference2CharMap.Entry)var1;
            return this.map.remove(var6.getKey(), var6.getCharValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Character) {
               char var5 = (Character)var4;
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

   public static class BasicEntry<K> implements Reference2CharMap.Entry<K> {
      protected K key;
      protected char value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(K var1, Character var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(K var1, char var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public K getKey() {
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
         } else if (var1 instanceof Reference2CharMap.Entry) {
            Reference2CharMap.Entry var5 = (Reference2CharMap.Entry)var1;
            return this.key == var5.getKey() && this.value == var5.getCharValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Character) {
               return this.key == var3 && this.value == (Character)var4;
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return System.identityHashCode(this.key) ^ this.value;
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
