package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.AggregateProvider;

public record Minimum(HolderSet<ContextIntProvider> inputs) implements ContextIntProvider, AggregateProvider<ContextIntProvider> {
   public static final MapCodec<Minimum> MAP_CODEC;

   public Minimum {
      super();
   }

   public MapCodec<Minimum> codec() {
      return MAP_CODEC;
   }

   public int getIntUnsafe(final LootContext context) {
      int value = 2147483647;

      for(Holder<ContextIntProvider> input : this.inputs()) {
         value = Math.min(value, ((ContextIntProvider)input.value()).getIntUnsafe(context));
      }

      return value;
   }

   static {
      MAP_CODEC = AggregateProvider.mapCodec(ContextIntProviders.LIST_CODEC, Minimum::new);
   }
}
