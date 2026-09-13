package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.AggregateProvider;

public record Average(HolderSet<ContextFloatProvider> inputs) implements ContextFloatProvider, AggregateProvider<ContextFloatProvider> {
   public static final MapCodec<Average> MAP_CODEC;

   public Average {
      super();
   }

   public MapCodec<Average> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      float sum = 0.0F;
      int count = 0;

      for(Holder<ContextFloatProvider> input : this.inputs()) {
         sum += ((ContextFloatProvider)input.value()).getFloatUnsafe(context);
         ++count;
      }

      return sum / (float)count;
   }

   static {
      MAP_CODEC = AggregateProvider.mapCodec(ContextFloatProviders.LIST_CODEC, Average::new);
   }
}
