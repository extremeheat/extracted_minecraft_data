package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

public class StatType<T> implements Iterable<Stat<T>> {
   private final Registry<T> registry;
   private final Map<T, Stat<T>> map = new IdentityHashMap<>();
   @Nullable
   private Component displayName;

   public StatType(Registry<T> var1) {
      super();
      this.registry = var1;
   }

   public boolean contains(T var1) {
      return this.map.containsKey(var1);
   }

   public Stat<T> get(T var1, StatFormatter var2) {
      return this.map.computeIfAbsent((T)var1, var2x -> new Stat<>(this, var2x, var2));
   }

   public Registry<T> getRegistry() {
      return this.registry;
   }

   @Override
   public Iterator<Stat<T>> iterator() {
      return this.map.values().iterator();
   }

   public Stat<T> get(T var1) {
      return this.get((T)var1, StatFormatter.DEFAULT);
   }

   public String getTranslationKey() {
      return "stat_type." + BuiltInRegistries.STAT_TYPE.getKey(this).toString().replace(':', '.');
   }

   public Component getDisplayName() {
      if (this.displayName == null) {
         this.displayName = Component.translatable(this.getTranslationKey());
      }

      return this.displayName;
   }
}
