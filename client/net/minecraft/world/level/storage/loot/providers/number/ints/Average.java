package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.AggregateProvider;

public record Average(HolderSet<ContextIntProvider> inputs) implements ContextIntProvider, AggregateProvider<ContextIntProvider> {
   public static final MapCodec<Average> MAP_CODEC;

   public Average {
      super();
   }

   public MapCodec<Average> codec() {
      return MAP_CODEC;
   }

   public int getIntUnsafe(final LootContext context) throws ArithmeticException {
      long sum = 0L;
      long count = 0L;

      for(Holder<ContextIntProvider> input : this.inputs()) {
         sum += (long)((ContextIntProvider)input.value()).getIntUnsafe(context);
         ++count;
      }

      return ContextIntProvider.longToIntSafe(sum / count);
   }

   static {
      MAP_CODEC = AggregateProvider.mapCodec(ContextIntProviders.LIST_CODEC, Average::new);
   }
}
