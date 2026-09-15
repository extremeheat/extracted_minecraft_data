package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.AggregateProvider;

public record Sum(HolderSet<ContextFloatProvider> inputs) implements ContextFloatProvider, AggregateProvider<ContextFloatProvider> {
   public static final MapCodec<Sum> MAP_CODEC;

   public Sum {
      super();
   }

   public MapCodec<Sum> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      float value = 0.0F;

      for(Holder<ContextFloatProvider> input : this.inputs()) {
         value += ((ContextFloatProvider)input.value()).getFloatUnsafe(context);
      }

      return value;
   }

   static {
      MAP_CODEC = AggregateProvider.mapCodec(ContextFloatProviders.LIST_CODEC, Sum::new);
   }
}
