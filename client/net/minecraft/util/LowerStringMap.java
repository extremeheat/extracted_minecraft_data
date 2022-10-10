package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class LowerStringMap<V> implements Map<String, V> {
   private final Map<String, V> field_76117_a = Maps.newLinkedHashMap();

   public LowerStringMap() {
      super();
   }

   public int size() {
      return this.field_76117_a.size();
   }

   public boolean isEmpty() {
      return this.field_76117_a.isEmpty();
   }

   public boolean containsKey(Object var1) {
      return this.field_76117_a.containsKey(var1.toString().toLowerCase(Locale.ROOT));
   }

   public boolean containsValue(Object var1) {
      return this.field_76117_a.containsValue(var1);
   }

   public V get(Object var1) {
      return this.field_76117_a.get(var1.toString().toLowerCase(Locale.ROOT));
   }

   public V put(String var1, V var2) {
      return this.field_76117_a.put(var1.toLowerCase(Locale.ROOT), var2);
   }

   public V remove(Object var1) {
      return this.field_76117_a.remove(var1.toString().toLowerCase(Locale.ROOT));
   }

   public void putAll(Map<? extends String, ? extends V> var1) {
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.put((String)var3.getKey(), var3.getValue());
      }

   }

   public void clear() {
      this.field_76117_a.clear();
   }

   public Set<String> keySet() {
      return this.field_76117_a.keySet();
   }

   public Collection<V> values() {
      return this.field_76117_a.values();
   }

   public Set<Entry<String, V>> entrySet() {
      return this.field_76117_a.entrySet();
   }

   // $FF: synthetic method
   public Object put(Object var1, Object var2) {
      return this.put((String)var1, var2);
   }
}
