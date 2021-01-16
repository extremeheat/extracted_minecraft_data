package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractByte2ObjectMap<V> extends AbstractByte2ObjectFunction<V> implements Byte2ObjectMap<V>, Serializable {
   private static final long serialVersionUID = -4940583368468432370L;

   protected AbstractByte2ObjectMap() {
      super();
   }

   public boolean containsValue(Object var1) {
      return this.values().contains(var1);
   }

   public boolean containsKey(byte var1) {
      ObjectIterator var2 = this.byte2ObjectEntrySet().iterator();

      do {
         if (!var2.hasNext()) {
            return false;
         }
      } while(((Byte2ObjectMap.Entry)var2.next()).getByteKey() != var1);

      return true;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public ByteSet keySet() {
      return new AbstractByteSet() {
         public boolean contains(byte var1) {
            return AbstractByte2ObjectMap.this.containsKey(var1);
         }

         public int size() {
            return AbstractByte2ObjectMap.this.size();
         }

         public void clear() {
            AbstractByte2ObjectMap.this.clear();
         }

         public ByteIterator iterator() {
            return new ByteIterator() {
               private final ObjectIterator<Byte2ObjectMap.Entry<V>> i = Byte2ObjectMaps.fastIterator(AbstractByte2ObjectMap.this);

               public byte nextByte() {
                  return ((Byte2ObjectMap.Entry)this.i.next()).getByteKey();
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

   public ObjectCollection<V> values() {
      return new AbstractObjectCollection<V>() {
         public boolean contains(Object var1) {
            return AbstractByte2ObjectMap.this.containsValue(var1);
         }

         public int size() {
            return AbstractByte2ObjectMap.this.size();
         }

         public void clear() {
            AbstractByte2ObjectMap.this.clear();
         }

         public ObjectIterator<V> iterator() {
            return new ObjectIterator<V>() {
               private final ObjectIterator<Byte2ObjectMap.Entry<V>> i = Byte2ObjectMaps.fastIterator(AbstractByte2ObjectMap.this);

               public V next() {
                  return ((Byte2ObjectMap.Entry)this.i.next()).getValue();
               }

               public boolean hasNext() {
                  return this.i.hasNext();
               }
            };
         }
      };
   }

   public void putAll(Map<? extends Byte, ? extends V> var1) {
      if (var1 instanceof Byte2ObjectMap) {
         ObjectIterator var2 = Byte2ObjectMaps.fastIterator((Byte2ObjectMap)var1);

         while(var2.hasNext()) {
            Byte2ObjectMap.Entry var3 = (Byte2ObjectMap.Entry)var2.next();
            this.put(var3.getByteKey(), var3.getValue());
         }
      } else {
         int var5 = var1.size();
         Iterator var6 = var1.entrySet().iterator();

         while(var5-- != 0) {
            java.util.Map.Entry var4 = (java.util.Map.Entry)var6.next();
            this.put((Byte)var4.getKey(), var4.getValue());
         }
      }

   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.size();

      for(ObjectIterator var3 = Byte2ObjectMaps.fastIterator(this); var2-- != 0; var1 += ((Byte2ObjectMap.Entry)var3.next()).hashCode()) {
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
         return var2.size() != this.size() ? false : this.byte2ObjectEntrySet().containsAll(var2.entrySet());
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      ObjectIterator var2 = Byte2ObjectMaps.fastIterator(this);
      int var3 = this.size();
      boolean var5 = true;
      var1.append("{");

      while(var3-- != 0) {
         if (var5) {
            var5 = false;
         } else {
            var1.append(", ");
         }

         Byte2ObjectMap.Entry var4 = (Byte2ObjectMap.Entry)var2.next();
         var1.append(String.valueOf(var4.getByteKey()));
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

   public abstract static class BasicEntrySet<V> extends AbstractObjectSet<Byte2ObjectMap.Entry<V>> {
      protected final Byte2ObjectMap<V> map;

      public BasicEntrySet(Byte2ObjectMap<V> var1) {
         super();
         this.map = var1;
      }

      public boolean contains(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Byte2ObjectMap.Entry) {
            Byte2ObjectMap.Entry var6 = (Byte2ObjectMap.Entry)var1;
            byte var7 = var6.getByteKey();
            return this.map.containsKey(var7) && Objects.equals(this.map.get(var7), var6.getValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Byte) {
               byte var4 = (Byte)var3;
               Object var5 = var2.getValue();
               return this.map.containsKey(var4) && Objects.equals(this.map.get(var4), var5);
            } else {
               return false;
            }
         }
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof java.util.Map.Entry)) {
            return false;
         } else if (var1 instanceof Byte2ObjectMap.Entry) {
            Byte2ObjectMap.Entry var6 = (Byte2ObjectMap.Entry)var1;
            return this.map.remove(var6.getByteKey(), var6.getValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Byte) {
               byte var4 = (Byte)var3;
               Object var5 = var2.getValue();
               return this.map.remove(var4, var5);
            } else {
               return false;
            }
         }
      }

      public int size() {
         return this.map.size();
      }
   }

   public static class BasicEntry<V> implements Byte2ObjectMap.Entry<V> {
      protected byte key;
      protected V value;

      public BasicEntry() {
         super();
      }

      public BasicEntry(Byte var1, V var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public BasicEntry(byte var1, V var2) {
         super();
         this.key = var1;
         this.value = var2;
      }

      public byte getByteKey() {
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
         } else if (var1 instanceof Byte2ObjectMap.Entry) {
            Byte2ObjectMap.Entry var5 = (Byte2ObjectMap.Entry)var1;
            return this.key == var5.getByteKey() && Objects.equals(this.value, var5.getValue());
         } else {
            java.util.Map.Entry var2 = (java.util.Map.Entry)var1;
            Object var3 = var2.getKey();
            if (var3 != null && var3 instanceof Byte) {
               Object var4 = var2.getValue();
               return this.key == (Byte)var3 && Objects.equals(this.value, var4);
            } else {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.key ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.key + "->" + this.value;
      }
   }
}
