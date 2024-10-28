package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;

public class BiomeFilter extends PlacementFilter {
   private static final BiomeFilter INSTANCE = new BiomeFilter();
   public static MapCodec<BiomeFilter> CODEC = MapCodec.unit(() -> {
      return INSTANCE;
   });

   private BiomeFilter() {
      super();
   }

   public static BiomeFilter biome() {
      return INSTANCE;
   }

   protected boolean shouldPlace(PlacementContext var1, RandomSource var2, BlockPos var3) {
      PlacedFeature var4 = (PlacedFeature)var1.topFeature().orElseThrow(() -> {
         return new IllegalStateException("Tried to biome check an unregistered feature, or a feature that should not restrict the biome");
      });
      Holder var5 = var1.getLevel().getBiome(var3);
      return var1.generator().getBiomeGenerationSettings(var5).hasFeature(var4);
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.BIOME_FILTER;
   }
}
