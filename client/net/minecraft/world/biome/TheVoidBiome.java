package net.minecraft.world.biome;

import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

public final class TheVoidBiome extends Biome {
   public TheVoidBiome() {
      super((new Biome.BiomeBuilder()).func_205416_a(new CompositeSurfaceBuilder(field_201869_aa, field_203946_aa)).func_205415_a(Biome.RainType.NONE).func_205419_a(Biome.Category.NONE).func_205421_a(0.1F).func_205420_b(0.2F).func_205414_c(0.5F).func_205417_d(0.5F).func_205412_a(4159204).func_205413_b(329011).func_205418_a((String)null));
      this.func_203611_a(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, func_201864_a(Feature.field_202312_L, IFeatureConfig.field_202429_e, field_201917_l, IPlacementConfig.field_202468_e));
   }
}
