package net.minecraft.world.biome;

import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BushConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HellLavaConfig;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.LiquidsConfig;
import net.minecraft.world.gen.feature.MinableConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.structure.FortressConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.ChanceRangeConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

public final class NetherBiome extends Biome {
   protected NetherBiome() {
      super((new Biome.BiomeBuilder()).func_205416_a(new CompositeSurfaceBuilder(field_205404_aE, field_205409_as)).func_205415_a(Biome.RainType.NONE).func_205419_a(Biome.Category.NETHER).func_205421_a(0.1F).func_205420_b(0.2F).func_205414_c(2.0F).func_205417_d(0.0F).func_205412_a(4159204).func_205413_b(329011).func_205418_a((String)null));
      this.func_201865_a(Feature.field_202337_o, new FortressConfig());
      this.func_203609_a(GenerationStage.Carving.AIR, func_203606_a(field_201908_c, new ProbabilityConfig(0.2F)));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, func_201864_a(Feature.field_202295_ao, new LiquidsConfig(Fluids.field_204547_b), field_201925_t, new CountRangeConfig(20, 8, 16, 256)));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, func_201864_a(Feature.field_202284_ad, new BushConfig(Blocks.field_150338_P), field_201920_o, new ChanceConfig(4)));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, func_201864_a(Feature.field_202284_ad, new BushConfig(Blocks.field_150337_Q), field_201920_o, new ChanceConfig(8)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, func_201864_a(Feature.field_202337_o, new FortressConfig(), field_201917_l, IPlacementConfig.field_202468_e));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, func_201864_a(Feature.field_202287_ag, new HellLavaConfig(false), field_201923_r, new CountRangeConfig(8, 4, 8, 128)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, func_201864_a(Feature.field_202317_Q, IFeatureConfig.field_202429_e, field_201931_z, new FrequencyConfig(10)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, func_201864_a(Feature.field_202321_U, IFeatureConfig.field_202429_e, field_201887_G, new FrequencyConfig(10)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, func_201864_a(Feature.field_202321_U, IFeatureConfig.field_202429_e, field_201923_r, new CountRangeConfig(10, 0, 0, 128)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, func_201864_a(Feature.field_202284_ad, new BushConfig(Blocks.field_150338_P), field_201926_u, new ChanceRangeConfig(0.5F, 0, 0, 128)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, func_201864_a(Feature.field_202284_ad, new BushConfig(Blocks.field_150337_Q), field_201926_u, new ChanceRangeConfig(0.5F, 0, 0, 128)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, func_201864_a(Feature.field_202290_aj, new MinableConfig(BlockMatcher.func_177642_a(Blocks.field_150424_aL), Blocks.field_196766_fg.func_176223_P(), 14), field_201923_r, new CountRangeConfig(16, 10, 20, 128)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, func_201864_a(Feature.field_202290_aj, new MinableConfig(BlockMatcher.func_177642_a(Blocks.field_150424_aL), Blocks.field_196814_hQ.func_176223_P(), 33), field_201881_A, new FrequencyConfig(4)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_DECORATION, func_201864_a(Feature.field_202287_ag, new HellLavaConfig(true), field_201923_r, new CountRangeConfig(16, 10, 20, 128)));
      this.func_201866_a(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.field_200811_y, 50, 4, 4));
      this.func_201866_a(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.field_200785_Y, 100, 4, 4));
      this.func_201866_a(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.field_200771_K, 2, 4, 4));
      this.func_201866_a(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.field_200803_q, 1, 4, 4));
   }
}
