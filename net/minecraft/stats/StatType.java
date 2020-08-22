package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.Registry;

public class StatType implements Iterable {
   private final Registry registry;
   private final Map map = new IdentityHashMap();

   public StatType(Registry var1) {
      this.registry = var1;
   }

   public boolean contains(Object var1) {
      return this.map.containsKey(var1);
   }

   public Stat get(Object var1, StatFormatter var2) {
      return (Stat)this.map.computeIfAbsent(var1, (var2x) -> {
         return new Stat(this, var2x, var2);
      });
   }

   public Registry getRegistry() {
      return this.registry;
   }

   public Iterator iterator() {
      return this.map.values().iterator();
   }

   public Stat get(Object var1) {
      return this.get(var1, StatFormatter.DEFAULT);
   }

   public String getTranslationKey() {
      return "stat_type." + Registry.STAT_TYPE.getKey(this).toString().replace(':', '.');
   }
}
