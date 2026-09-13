package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.AggregateProvider;

public record Sum(HolderSet<ContextIntProvider> inputs) implements ContextIntProvider, AggregateProvider<ContextIntProvider> {
   public static final MapCodec<Sum> MAP_CODEC;

   public Sum {
      super();
   }

   public MapCodec<Sum> codec() {
      return MAP_CODEC;
   }

   public int getIntUnsafe(final LootContext context) {
      long value = 0L;

      for(Holder<ContextIntProvider> input : this.inputs()) {
         value += (long)((ContextIntProvider)input.value()).getIntUnsafe(context);
      }

      return ContextIntProvider.longToIntSafe(value);
   }

   static {
      MAP_CODEC = AggregateProvider.mapCodec(ContextIntProviders.LIST_CODEC, Sum::new);
   }
}
