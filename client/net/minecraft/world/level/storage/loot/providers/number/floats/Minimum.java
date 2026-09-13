package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.AggregateProvider;

public record Minimum(HolderSet<ContextFloatProvider> inputs) implements ContextFloatProvider, AggregateProvider<ContextFloatProvider> {
   public static final MapCodec<Minimum> MAP_CODEC;

   public Minimum {
      super();
   }

   public MapCodec<Minimum> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      float value = 3.4028235E38F;

      for(Holder<ContextFloatProvider> input : this.inputs()) {
         value = Math.min(value, ((ContextFloatProvider)input.value()).getFloatUnsafe(context));
      }

      return value;
   }

   static {
      MAP_CODEC = AggregateProvider.mapCodec(ContextFloatProviders.LIST_CODEC, Minimum::new);
   }
}
