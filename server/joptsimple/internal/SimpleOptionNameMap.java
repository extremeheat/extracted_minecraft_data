package joptsimple.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SimpleOptionNameMap<V> implements OptionNameMap<V> {
   private final Map<String, V> map = new HashMap();

   public SimpleOptionNameMap() {
      super();
   }

   public boolean contains(String var1) {
      return this.map.containsKey(var1);
   }

   public V get(String var1) {
      return this.map.get(var1);
   }

   public void put(String var1, V var2) {
      this.map.put(var1, var2);
   }

   public void putAll(Iterable<String> var1, V var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         this.map.put(var4, var2);
      }

   }

   public void remove(String var1) {
      this.map.remove(var1);
   }

   public Map<String, V> toJavaUtilMap() {
      return new HashMap(this.map);
   }
}
