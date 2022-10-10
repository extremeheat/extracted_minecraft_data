package net.minecraft.world.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.EndCityConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

public class SmallEndIslandsBiome extends Biome {
   public SmallEndIslandsBiome() {
      super((new Biome.BiomeBuilder()).func_205416_a(new CompositeSurfaceBuilder(field_203955_aj, field_205410_at)).func_205415_a(Biome.RainType.NONE).func_205419_a(Biome.Category.THEEND).func_205421_a(0.1F).func_205420_b(0.2F).func_205414_c(0.5F).func_205417_d(0.5F).func_205412_a(4159204).func_205413_b(329011).func_205418_a((String)null));
      this.func_203611_a(GenerationStage.Decoration.RAW_GENERATION, func_201864_a(Feature.field_202297_aq, IFeatureConfig.field_202429_e, field_201889_I, IPlacementConfig.field_202468_e));
      this.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, func_201864_a(Feature.field_202338_p, new EndCityConfig(), field_201917_l, IPlacementConfig.field_202468_e));
      this.func_201866_a(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.field_200803_q, 10, 4, 4));
   }

   public int func_76731_a(float var1) {
      return 0;
   }
}
