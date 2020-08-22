package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class InsensitiveStringMap implements Map {
   private final Map map = Maps.newLinkedHashMap();

   public int size() {
      return this.map.size();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public boolean containsKey(Object var1) {
      return this.map.containsKey(var1.toString().toLowerCase(Locale.ROOT));
   }

   public boolean containsValue(Object var1) {
      return this.map.containsValue(var1);
   }

   public Object get(Object var1) {
      return this.map.get(var1.toString().toLowerCase(Locale.ROOT));
   }

   public Object put(String var1, Object var2) {
      return this.map.put(var1.toLowerCase(Locale.ROOT), var2);
   }

   public Object remove(Object var1) {
      return this.map.remove(var1.toString().toLowerCase(Locale.ROOT));
   }

   public void putAll(Map var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.put((String)var3.getKey(), var3.getValue());
      }

   }

   public void clear() {
      this.map.clear();
   }

   public Set keySet() {
      return this.map.keySet();
   }

   public Collection values() {
      return this.map.values();
   }

   public Set entrySet() {
      return this.map.entrySet();
   }

   // $FF: synthetic method
   public Object put(Object var1, Object var2) {
      return this.put((String)var1, var2);
   }
}
