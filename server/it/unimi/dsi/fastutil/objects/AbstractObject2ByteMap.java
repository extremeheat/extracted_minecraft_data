package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractObject2ByteMap<K> extends AbstractObject2ByteFunction<K> implements Object2ByteMap<K>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractObject2ByteMap() {
      super();
   }

   public boolean containsValue(byte var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(Object var1) {
      ObjectIterator var2 = this.object2ByteEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Object2ByteMap.Entry)var2.next()).getKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ObjectSet<K> keySet() {
      return new AbstractObjectSet<K>() {
         public boolean contains(Object var1) {
            return AbstractObject2ByteMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractObject2ByteMap.this.size();
         }

         public void clear() {
            AbstractObject2ByteMap.this.clear();
         }

         public ObjectIterator<K> iterator() {
            return new ObjectIterator<K>() {
               private final ObjectIterator<Object2ByteMap.Entry<K>> i = Object2ByteMaps.fastIterator(AbstractObject2ByteMap.this);

               public K next() {
                  return ((Object2ByteMap.Entry)this.i.next()).getKey();
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

   public ByteCollection values() {
      return new AbstractByteCollection() {
         public boolean contains(byte var1) {
            return AbstractObject2ByteMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractObject2ByteMap.this.size();
         }

         public void clear() {
            AbstractObject2ByteMap.this.clear();
         }

         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Object2ByteMap.Entry<K>> i = Object2ByteMaps.fastIterator(AbstractObject2ByteMap.this);

               public byte nextByte() {
                  return ((Object2ByteMap.Entry)this.i.next()).getByteValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends K, ? extends Byte> var1) {
      if (var1 instanceof Object2ByteMap) {
         ObjectIterator var2 = Object2ByteMaps.fastIterator((Object2ByteMap)var1);

         while(var2.hasNext()) {
            Object2ByteMap.Entry var3 = (Object2ByteMap.Entry)var2.next();
            this.put(var3.getKey(), var3.getByteValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put(var4.getKey(), (Byte)var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Object2ByteMaps.fastIterator(this); var2-- != 0; var1 += ((Object2ByteMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.object2ByteEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Object2ByteMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Object2ByteMap.Entry var4 = (Object2ByteMap.Entry)var2.next();
         if (this == var4.getKey()) {
            var1.append("(this map)");
         } else {
            var1.append(String.valueOf(var4.getKey()));
         }

         var1.append("=>");
         var1.append(String.valueOf(var4.getByteValue()));
      }

      var1.append("}");
      return var1.toString();
   }

   public abstract static class BasicEntrySet<K> extends AbstractObjectSet<Object2ByteMap.Entry<K>> {
      protected final Object2ByteMap<K> map;

      public BasicEntrySet(Object2ByteMap<K> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else {
            Object var3;
            if (var1 instanceof Object2ByteMap.Entry) {
               Object2ByteMap.Entry var5 = (Object2ByteMap.Entry)var1;
               var3 = var5.getKey();
               return this.map.containsKey(var3) && this.map.getByte(var3) == var5.getByteValue();
            } else {
               java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
               var3 = var2.getKey();
               Object var4 = var2.getValue();
               if (var4 != null && var4 instanceof Byte) {
                  return this.map.containsKey(var3) && this.map.getByte(var3) == (Byte)var4;
               } else {
                  return false;
               }
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Object2ByteMap.Entry) {
            Object2ByteMap.Entry var6 = (Object2ByteMap.Entry)var1;
            return this.map.remove(var6.getKey(), var6.getByteValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Byte) {
               byte var5 = (Byte)var4;
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

   public static class BasicEntry<K> implements Object2ByteMap.Entry<K> {
      protected K key;
      protected byte value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(K var1, Byte var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(K var1, byte var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public K getKey() {
         return this.key;
      }

      public byte getByteValue() {
         return this.value;
      }

      public byte setValue(byte var1) {
         throw new UnsupportedOperationException();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Object2ByteMap.Entry) {
            Object2ByteMap.Entry var5 = (Object2ByteMap.Entry)var1;
            return Objects.equals(this.key, var5.getKey()) && this.value == var5.getByteValue();
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            Object var4 = var2.getValue();
            if (var4 != null && var4 instanceof Byte) {
               return Objects.equals(this.key, var3) && this.value == (Byte)var4;
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ this.value;
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
