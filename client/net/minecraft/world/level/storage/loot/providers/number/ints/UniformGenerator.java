package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.RangeProvider;

public record UniformGenerator(Holder<ContextIntProvider> min, Holder<ContextIntProvider> max) implements ContextIntProvider, RangeProvider<ContextIntProvider> {
   public static final MapCodec<UniformGenerator> MAP_CODEC;

   public UniformGenerator {
      super();
   }

   public MapCodec<UniformGenerator> codec() {
      return MAP_CODEC;
   }

   public int getIntUnsafe(final LootContext context) {
      return Mth.nextInt(context.getRandom(), ((ContextIntProvider)this.min().value()).getIntUnsafe(context), ((ContextIntProvider)this.max().value()).getIntUnsafe(context));
   }

   static {
      MAP_CODEC = RangeProvider.mapCodec(ContextIntProviders.CODEC, UniformGenerator::new);
   }
}
