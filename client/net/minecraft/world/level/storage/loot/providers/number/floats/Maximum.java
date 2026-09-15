package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.AggregateProvider;

public record Maximum(HolderSet<ContextFloatProvider> inputs) implements ContextFloatProvider, AggregateProvider<ContextFloatProvider> {
   public static final MapCodec<Maximum> MAP_CODEC;

   public Maximum {
      super();
   }

   public MapCodec<Maximum> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      float value = -3.4028235E38F;

      for(Holder<ContextFloatProvider> input : this.inputs()) {
         value = Math.max(value, ((ContextFloatProvider)input.value()).getFloatUnsafe(context));
      }

      return value;
   }

   static {
      MAP_CODEC = AggregateProvider.mapCodec(ContextFloatProviders.LIST_CODEC, Maximum::new);
   }
}
