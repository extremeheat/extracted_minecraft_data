package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class StatType<T> implements Iterable<Stat<T>> {
   private final Registry<T> registry;
   private final Map<T, Stat<T>> map = new IdentityHashMap();
   private final Component displayName;
   private final StreamCodec<RegistryFriendlyByteBuf, Stat<T>> streamCodec;

   public StatType(Registry<T> var1, Component var2) {
      super();
      this.registry = var1;
      this.displayName = var2;
      this.streamCodec = ByteBufCodecs.registry(var1.key()).map(this::get, Stat::getValue);
   }

   public StreamCodec<RegistryFriendlyByteBuf, Stat<T>> streamCodec() {
      return this.streamCodec;
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

   public Component getDisplayName() {
      return this.displayName;
   }
}
