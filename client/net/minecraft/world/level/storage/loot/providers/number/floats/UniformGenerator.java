package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.RangeProvider;

public record UniformGenerator(Holder<ContextFloatProvider> min, Holder<ContextFloatProvider> max) implements ContextFloatProvider, RangeProvider<ContextFloatProvider> {
   public static final MapCodec<UniformGenerator> MAP_CODEC;

   public UniformGenerator {
      super();
   }

   public MapCodec<UniformGenerator> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      return Mth.nextFloat(context.getRandom(), ((ContextFloatProvider)this.min().value()).getFloatUnsafe(context), ((ContextFloatProvider)this.max().value()).getFloatUnsafe(context));
   }

   static {
      MAP_CODEC = RangeProvider.mapCodec(ContextFloatProviders.CODEC, UniformGenerator::new);
   }
}
