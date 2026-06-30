package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public record RarityFilter(int chance) implements PlacementFilter {
   public static final MapCodec<RarityFilter> CODEC;

   public RarityFilter {
      super();
   }

   public static RarityFilter onAverageOnceEvery(final int chance) {
      return new RarityFilter(chance);
   }

   public boolean shouldPlace(final PlacementContext context, final RandomSource random, final BlockPos origin) {
      return random.nextFloat() < 1.0F / (float)this.chance;
   }

   public MapCodec<RarityFilter> codec() {
      return CODEC;
   }

   static {
      CODEC = ExtraCodecs.POSITIVE_INT.fieldOf("chance").xmap(RarityFilter::new, RarityFilter::chance);
   }
}
