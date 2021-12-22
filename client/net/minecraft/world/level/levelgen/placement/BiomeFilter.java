package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;

public class BiomeFilter extends PlacementFilter {
   private static final BiomeFilter INSTANCE = new BiomeFilter();
   public static Codec<BiomeFilter> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });

   private BiomeFilter() {
      super();
   }

   public static BiomeFilter biome() {
      return INSTANCE;
   }

   protected boolean shouldPlace(PlacementContext var1, Random var2, BlockPos var3) {
      PlacedFeature var4 = (PlacedFeature)var1.topFeature().orElseThrow(() -> {
         return new IllegalStateException("Tried to biome check an unregistered feature");
      });
      Biome var5 = var1.getLevel().getBiome(var3);
      return var5.getGenerationSettings().hasFeature(var4);
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.BIOME_FILTER;
   }
}
