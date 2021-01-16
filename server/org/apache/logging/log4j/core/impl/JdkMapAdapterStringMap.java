package org.apache.logging.log4j.core.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StringMap;
import org.apache.logging.log4j.util.TriConsumer;

class JdkMapAdapterStringMap implements StringMap {
   private static final long serialVersionUID = -7348247784983193612L;
   private static final String FROZEN = "Frozen collection cannot be modified";
   private static final Comparator<? super String> NULL_FIRST_COMPARATOR = new Comparator<String>() {
      public int compare(String var1, String var2) {
         if (var1 == null) {
            return -1;
         } else {
            return var2 == null ? 1 : var1.compareTo(var2);
         }
      }
   };
   private final Map<String, String> map;
   private boolean immutable;
   private transient String[] sortedKeys;
   private static TriConsumer<String, String, Map<String, String>> PUT_ALL = new TriConsumer<String, String, Map<String, String>>() {
      public void accept(String var1, String var2, Map<String, String> var3) {
         var3.put(var1, var2);
      }
   };

   public JdkMapAdapterStringMap() {
      this(new HashMap());
   }

   public JdkMapAdapterStringMap(Map<String, String> var1) {
      super();
      this.immutable = false;
      this.map = (Map)Objects.requireNonNull(var1, "map");
   }

   public Map<String, String> toMap() {
      return this.map;
   }

   private void assertNotFrozen() {
      if (this.immutable) {
         throw new UnsupportedOperationException("Frozen collection cannot be modified");
      }
   }

   public boolean containsKey(String var1) {
      return this.map.containsKey(var1);
   }

   public <V> void forEach(BiConsumer<String, ? super V> var1) {
      String[] var2 = this.getSortedKeys();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var1.accept(var2[var3], this.map.get(var2[var3]));
      }

   }

   public <V, S> void forEach(TriConsumer<String, ? super V, S> var1, S var2) {
      String[] var3 = this.getSortedKeys();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var1.accept(var3[var4], this.map.get(var3[var4]), var2);
      }

   }

   private String[] getSortedKeys() {
      if (this.sortedKeys == null) {
         this.sortedKeys = (String[])this.map.keySet().toArray(new String[this.map.size()]);
         Arrays.sort(this.sortedKeys, NULL_FIRST_COMPARATOR);
      }

      return this.sortedKeys;
   }

   public <V> V getValue(String var1) {
      return this.map.get(var1);
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public int size() {
      return this.map.size();
   }

   public void clear() {
      if (!this.map.isEmpty()) {
         this.assertNotFrozen();
         this.map.clear();
         this.sortedKeys = null;
      }
   }

   public void freeze() {
      this.immutable = true;
   }

   public boolean isFrozen() {
      return this.immutable;
   }

   public void putAll(ReadOnlyStringMap var1) {
      this.assertNotFrozen();
      var1.forEach(PUT_ALL, this.map);
      this.sortedKeys = null;
   }

   public void putValue(String var1, Object var2) {
      this.assertNotFrozen();
      this.map.put(var1, var2 == null ? null : String.valueOf(var2));
      this.sortedKeys = null;
   }

   public void remove(String var1) {
      if (this.map.containsKey(var1)) {
         this.assertNotFrozen();
         this.map.remove(var1);
         this.sortedKeys = null;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(this.map.size() * 13);
      var1.append('{');
      String[] var2 = this.getSortedKeys();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var3 > 0) {
            var1.append(", ");
         }

         var1.append(var2[var3]).append('=').append((String)this.map.get(var2[var3]));
      }

      var1.append('}');
      return var1.toString();
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof JdkMapAdapterStringMap)) {
         return false;
      } else {
         JdkMapAdapterStringMap var2 = (JdkMapAdapterStringMap)var1;
         return this.map.equals(var2.map) && this.immutable == var2.immutable;
      }
   }

   public int hashCode() {
      return this.map.hashCode() + (this.immutable ? 31 : 0);
   }
}
