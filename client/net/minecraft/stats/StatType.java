package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.Registry;

public class StatType<T> implements Iterable<Stat<T>> {
   private final Registry<T> registry;
   private final Map<T, Stat<T>> map = new IdentityHashMap();

   public StatType(Registry<T> var1) {
      super();
      this.registry = var1;
   }

   public boolean contains(T var1) {
      return this.map.containsKey(var1);
   }

   public Stat<T> get(T var1, StatFormatter var2) {
      return (Stat)this.map.computeIfAbsent(var1, (var2x) -> {
         return new Stat(this, var2x, var2);
      });
   }

   public Registry<T> getRegistry() {
      return this.registry;
   }

   public Iterator<Stat<T>> iterator() {
      return this.map.values().iterator();
   }

   public Stat<T> get(T var1) {
      return this.get(var1, StatFormatter.DEFAULT);
   }

   public String getTranslationKey() {
      return "stat_type." + Registry.STAT_TYPE.getKey(this).toString().replace(':', '.');
   }
}
