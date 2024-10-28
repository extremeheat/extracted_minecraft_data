package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

public class RarityFilter extends PlacementFilter {
   public static final MapCodec<RarityFilter> CODEC;
   private final int chance;

   private RarityFilter(int var1) {
      super();
      this.chance = var1;
   }

   public static RarityFilter onAverageOnceEvery(int var0) {
      return new RarityFilter(var0);
   }

   protected boolean shouldPlace(PlacementContext var1, RandomSource var2, BlockPos var3) {
      return var2.nextFloat() < 1.0F / (float)this.chance;
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.RARITY_FILTER;
   }

   static {
      CODEC = ExtraCodecs.POSITIVE_INT.fieldOf("chance").xmap(RarityFilter::new, (var0) -> {
         return var0.chance;
      });
   }
}
