package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public class RarityFilter extends PlacementFilter {
   public static final MapCodec<RarityFilter> CODEC;
   private final int chance;

   private RarityFilter(final int chance) {
      super();
      this.chance = chance;
   }

   public static RarityFilter onAverageOnceEvery(final int chance) {
      return new RarityFilter(chance);
   }

   protected boolean shouldPlace(final PlacementContext context, final RandomSource random, final BlockPos origin) {
      return random.nextFloat() < 1.0F / (float)this.chance;
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.RARITY_FILTER;
   }

   static {
      CODEC = ExtraCodecs.POSITIVE_INT.fieldOf("chance").xmap(RarityFilter::new, (c) -> c.chance);
   }
}
