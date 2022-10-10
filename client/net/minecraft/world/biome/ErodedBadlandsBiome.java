package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.BushConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.LakesConfig;
import net.minecraft.world.gen.feature.LiquidsConfig;
import net.minecraft.world.gen.feature.MinableConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.SphereReplaceConfig;
import net.minecraft.world.gen.feature.TallGrassConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.StrongholdConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.DepthAverageConfig;
import net.minecraft.world.gen.placement.DungeonRoomConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.LakeChanceConfig;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;

public final class ErodedBadlandsBiome extends Biome {
   public ErodedBadlandsBiome() {
      super((new Biome.BiomeBuilder()).func_205416_a(new CompositeSurfaceBuilder(field_201906_Z, field_203953_ah)).func_205415_a(Biome.RainType.NONE).func_205419_a(Biome.Category.MESA).func_205421_a(0.1F).func_205420_b(0.2F).func_205414_c(2.0F).func_205417_d(0.0F).func_205412_a(4159204).func_205413_b(329011).func_205418_a("badlands"));
      this.func_201865_a(Feature.field_202329_g, new MineshaftConfig(0.004D, MineshaftStructure.Type.MESA));
      this.func_201865_a(Feature.field_202335_m, new StrongholdConfig());
      this.func_203609_a(GenerationStage.Carving.AIR, func_203606_a(field_201907_b, new ProbabilityConfig(0.14285715F)));
      this.func_203609_a(GenerationStage.Carving.AIR, func_203606_a(field_201909_d, new ProbabilityConfig(0.02F)));
      this.func_203605_a();
      this.func_203611_a(GenerationStage.Decoration.LOCAL_MODIFICATIONS, func_201864_a(Feature.field_202289_ai, new LakesConfig(Blocks.field_150355_j), field_201884_D, new LakeChanceConfig(4)));
      this.func_203611_a(GenerationStage.Decoration.LOCAL_MODIFICATIONS, func_201864_a(Feature.field_202289_ai, new LakesConfig(Blocks.field_150353_l), field_201883_C, new LakeChanceConfig(80)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, func_201864_a(Feature.field_202282_ab, IFeatureConfig.field_202429_e, field_201885_E, new DungeonRoomConfig(8)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202290_aj, new MinableConfig(MinableConfig.field_202441_a, Blocks.field_150346_d.func_176223_P(), 33), field_201923_r, new CountRangeConfig(10, 0, 0, 256)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202290_aj, new MinableConfig(MinableConfig.field_202441_a, Blocks.field_150351_n.func_176223_P(), 33), field_201923_r, new CountRangeConfig(8, 0, 0, 256)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202290_aj, new MinableConfig(MinableConfig.field_202441_a, Blocks.field_196650_c.func_176223_P(), 33), field_201923_r, new CountRangeConfig(10, 0, 0, 80)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202290_aj, new MinableConfig(MinableConfig.field_202441_a, Blocks.field_196654_e.func_176223_P(), 33), field_201923_r, new CountRangeConfig(10, 0, 0, 80)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202290_aj, new MinableConfig(MinableConfig.field_202441_a, Blocks.field_196656_g.func_176223_P(), 33), field_201923_r, new CountRangeConfig(10, 0, 0, 80)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202290_aj, new MinableConfig(MinableConfig.field_202441_a, Blocks.field_150365_q.func_176223_P(), 17), field_201923_r, new CountRangeConfig(20, 0, 0, 128)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202290_aj, new MinableConfig(MinableConfig.field_202441_a, Blocks.field_150366_p.func_176223_P(), 9), field_201923_r, new CountRangeConfig(20, 0, 0, 64)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202290_aj, new MinableConfig(MinableConfig.field_202441_a, Blocks.field_150352_o.func_176223_P(), 9), field_201923_r, new CountRangeConfig(2, 0, 0, 32)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202290_aj, new MinableConfig(MinableConfig.field_202441_a, Blocks.field_150450_ax.func_176223_P(), 8), field_201923_r, new CountRangeConfig(8, 0, 0, 16)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202290_aj, new MinableConfig(MinableConfig.field_202441_a, Blocks.field_150482_ag.func_176223_P(), 8), field_201923_r, new CountRangeConfig(1, 0, 0, 16)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202290_aj, new MinableConfig(MinableConfig.field_202441_a, Blocks.field_150369_x.func_176223_P(), 7), field_201929_x, new DepthAverageConfig(1, 16, 16)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202290_aj, new MinableConfig(MinableConfig.field_202441_a, Blocks.field_150352_o.func_176223_P(), 9), field_201923_r, new CountRangeConfig(20, 32, 32, 80)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202285_ae, new SphereReplaceConfig(Blocks.field_150354_m, 7, 2, Lists.newArrayList(new Block[]{Blocks.field_150346_d, Blocks.field_196658_i})), field_201911_f, new FrequencyConfig(3)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202285_ae, new SphereReplaceConfig(Blocks.field_150435_aG, 4, 1, Lists.newArrayList(new Block[]{Blocks.field_150346_d, Blocks.field_150435_aG})), field_201911_f, new FrequencyConfig(1)));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_ORES, func_201864_a(Feature.field_202285_ae, new SphereReplaceConfig(Blocks.field_150351_n, 6, 2, Lists.newArrayList(new Block[]{Blocks.field_150346_d, Blocks.field_196658_i})), field_201911_f, new FrequencyConfig(1)));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, func_201864_a(Feature.field_202311_K, new TallGrassConfig(Blocks.field_150349_c.func_176223_P()), field_201913_h, new FrequencyConfig(1)));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, func_201864_a(Feature.field_202314_N, IFeatureConfig.field_202429_e, field_201913_h, new FrequencyConfig(20)));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, func_201864_a(Feature.field_202284_ad, new BushConfig(Blocks.field_150338_P), field_201920_o, new ChanceConfig(4)));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, func_201864_a(Feature.field_202284_ad, new BushConfig(Blocks.field_150337_Q), field_201920_o, new ChanceConfig(8)));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, func_201864_a(Feature.field_202324_X, IFeatureConfig.field_202429_e, field_201913_h, new FrequencyConfig(13)));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, func_201864_a(Feature.field_202323_W, IFeatureConfig.field_202429_e, field_201920_o, new ChanceConfig(32)));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, func_201864_a(Feature.field_202313_M, IFeatureConfig.field_202429_e, field_201913_h, new FrequencyConfig(5)));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, func_201864_a(Feature.field_202295_ao, new LiquidsConfig(Fluids.field_204546_a), field_201924_s, new CountRangeConfig(50, 8, 8, 256)));
      this.func_203611_a(GenerationStage.Decoration.VEGETAL_DECORATION, func_201864_a(Feature.field_202295_ao, new LiquidsConfig(Fluids.field_204547_b), field_201925_t, new CountRangeConfig(20, 8, 16, 256)));
      this.func_203611_a(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, func_201864_a(Feature.field_202325_Y, IFeatureConfig.field_202429_e, field_201917_l, IPlacementConfig.field_202468_e));
      this.func_201866_a(EnumCreatureType.AMBIENT, new Biome.SpawnListEntry(EntityType.field_200791_e, 10, 8, 8));
      this.func_201866_a(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.field_200748_an, 100, 4, 4));
      this.func_201866_a(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.field_200725_aD, 95, 4, 4));
      this.func_201866_a(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.field_200727_aF, 5, 1, 1));
      this.func_201866_a(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.field_200741_ag, 100, 4, 4));
      this.func_201866_a(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.field_200797_k, 100, 4, 4));
      this.func_201866_a(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.field_200743_ai, 100, 4, 4));
      this.func_201866_a(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.field_200803_q, 10, 1, 4));
      this.func_201866_a(EnumCreatureType.MONSTER, new Biome.SpawnListEntry(EntityType.field_200759_ay, 5, 1, 1));
   }

   public int func_180625_c(BlockPos var1) {
      return 10387789;
   }

   public int func_180627_b(BlockPos var1) {
      return 9470285;
   }
}
