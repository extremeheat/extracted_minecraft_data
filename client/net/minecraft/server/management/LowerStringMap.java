package net.minecraft.server.management;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class LowerStringMap implements Map {
   private final Map field_76117_a = new LinkedHashMap();

   public LowerStringMap() {
      super();
   }

   @Override
   public int size() {
      return this.field_76117_a.size();
   }

   @Override
   public boolean isEmpty() {
      return this.field_76117_a.isEmpty();
   }

   @Override
   public boolean containsKey(Object var1) {
      return this.field_76117_a.containsKey(var1.toString().toLowerCase());
   }

   @Override
   public boolean containsValue(Object var1) {
      return this.field_76117_a.containsKey(var1);
   }

   @Override
   public Object get(Object var1) {
      return this.field_76117_a.get(var1.toString().toLowerCase());
   }

   public Object put(String var1, Object var2) {
      return this.field_76117_a.put(var1.toLowerCase(), var2);
   }

   @Override
   public Object remove(Object var1) {
      return this.field_76117_a.remove(var1.toString().toLowerCase());
   }

   @Override
   public void putAll(Map var1) {
      for(Entry var3 : var1.entrySet()) {
         this.put((String)var3.getKey(), var3.getValue());
      }
   }

   @Override
   public void clear() {
      this.field_76117_a.clear();
   }

   @Override
   public Set keySet() {
      return this.field_76117_a.keySet();
   }

   @Override
   public Collection values() {
      return this.field_76117_a.values();
   }

   @Override
   public Set entrySet() {
      return this.field_76117_a.entrySet();
   }
}
