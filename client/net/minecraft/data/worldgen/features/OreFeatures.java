package net.minecraft.data.worldgen.features;

import java.util.List;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

public class OreFeatures {
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_MAGMA = FeatureUtils.createKey("ore_magma");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_SOUL_SAND = FeatureUtils.createKey("ore_soul_sand");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_NETHER_GOLD = FeatureUtils.createKey("ore_nether_gold");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_QUARTZ = FeatureUtils.createKey("ore_quartz");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_GRAVEL_NETHER = FeatureUtils.createKey("ore_gravel_nether");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_BLACKSTONE = FeatureUtils.createKey("ore_blackstone");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIRT = FeatureUtils.createKey("ore_dirt");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_GRAVEL = FeatureUtils.createKey("ore_gravel");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_GRANITE = FeatureUtils.createKey("ore_granite");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIORITE = FeatureUtils.createKey("ore_diorite");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ANDESITE = FeatureUtils.createKey("ore_andesite");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_TUFF = FeatureUtils.createKey("ore_tuff");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_COAL = FeatureUtils.createKey("ore_coal");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_COAL_BURIED = FeatureUtils.createKey("ore_coal_buried");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_IRON = FeatureUtils.createKey("ore_iron");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_IRON_SMALL = FeatureUtils.createKey("ore_iron_small");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_GOLD = FeatureUtils.createKey("ore_gold");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_GOLD_BURIED = FeatureUtils.createKey("ore_gold_buried");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_REDSTONE = FeatureUtils.createKey("ore_redstone");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIAMOND_SMALL = FeatureUtils.createKey("ore_diamond_small");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIAMOND_MEDIUM = FeatureUtils.createKey("ore_diamond_medium");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIAMOND_LARGE = FeatureUtils.createKey("ore_diamond_large");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIAMOND_BURIED = FeatureUtils.createKey("ore_diamond_buried");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_LAPIS = FeatureUtils.createKey("ore_lapis");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_LAPIS_BURIED = FeatureUtils.createKey("ore_lapis_buried");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_INFESTED = FeatureUtils.createKey("ore_infested");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_EMERALD = FeatureUtils.createKey("ore_emerald");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ANCIENT_DEBRIS_LARGE = FeatureUtils.createKey("ore_ancient_debris_large");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ANCIENT_DEBRIS_SMALL = FeatureUtils.createKey("ore_ancient_debris_small");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_COPPPER_SMALL = FeatureUtils.createKey("ore_copper_small");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_COPPER_LARGE = FeatureUtils.createKey("ore_copper_large");
   public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_CLAY = FeatureUtils.createKey("ore_clay");

   public OreFeatures() {
      super();
   }

   public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> var0) {
      TagMatchTest var1 = new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD);
      TagMatchTest var2 = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
      TagMatchTest var3 = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
      BlockMatchTest var4 = new BlockMatchTest(Blocks.NETHERRACK);
      TagMatchTest var5 = new TagMatchTest(BlockTags.BASE_STONE_NETHER);
      List var6 = List.of(OreConfiguration.target(var2, Blocks.IRON_ORE.defaultBlockState()), OreConfiguration.target(var3, Blocks.DEEPSLATE_IRON_ORE.defaultBlockState()));
      List var7 = List.of(OreConfiguration.target(var2, Blocks.GOLD_ORE.defaultBlockState()), OreConfiguration.target(var3, Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState()));
      List var8 = List.of(OreConfiguration.target(var2, Blocks.DIAMOND_ORE.defaultBlockState()), OreConfiguration.target(var3, Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState()));
      List var9 = List.of(OreConfiguration.target(var2, Blocks.LAPIS_ORE.defaultBlockState()), OreConfiguration.target(var3, Blocks.DEEPSLATE_LAPIS_ORE.defaultBlockState()));
      List var10 = List.of(OreConfiguration.target(var2, Blocks.COPPER_ORE.defaultBlockState()), OreConfiguration.target(var3, Blocks.DEEPSLATE_COPPER_ORE.defaultBlockState()));
      List var11 = List.of(OreConfiguration.target(var2, Blocks.COAL_ORE.defaultBlockState()), OreConfiguration.target(var3, Blocks.DEEPSLATE_COAL_ORE.defaultBlockState()));
      FeatureUtils.register(var0, ORE_MAGMA, Feature.ORE, new OreConfiguration(var4, Blocks.MAGMA_BLOCK.defaultBlockState(), 33));
      FeatureUtils.register(var0, ORE_SOUL_SAND, Feature.ORE, new OreConfiguration(var4, Blocks.SOUL_SAND.defaultBlockState(), 12));
      FeatureUtils.register(var0, ORE_NETHER_GOLD, Feature.ORE, new OreConfiguration(var4, Blocks.NETHER_GOLD_ORE.defaultBlockState(), 10));
      FeatureUtils.register(var0, ORE_QUARTZ, Feature.ORE, new OreConfiguration(var4, Blocks.NETHER_QUARTZ_ORE.defaultBlockState(), 14));
      FeatureUtils.register(var0, ORE_GRAVEL_NETHER, Feature.ORE, new OreConfiguration(var4, Blocks.GRAVEL.defaultBlockState(), 33));
      FeatureUtils.register(var0, ORE_BLACKSTONE, Feature.ORE, new OreConfiguration(var4, Blocks.BLACKSTONE.defaultBlockState(), 33));
      FeatureUtils.register(var0, ORE_DIRT, Feature.ORE, new OreConfiguration(var1, Blocks.DIRT.defaultBlockState(), 33));
      FeatureUtils.register(var0, ORE_GRAVEL, Feature.ORE, new OreConfiguration(var1, Blocks.GRAVEL.defaultBlockState(), 33));
      FeatureUtils.register(var0, ORE_GRANITE, Feature.ORE, new OreConfiguration(var1, Blocks.GRANITE.defaultBlockState(), 64));
      FeatureUtils.register(var0, ORE_DIORITE, Feature.ORE, new OreConfiguration(var1, Blocks.DIORITE.defaultBlockState(), 64));
      FeatureUtils.register(var0, ORE_ANDESITE, Feature.ORE, new OreConfiguration(var1, Blocks.ANDESITE.defaultBlockState(), 64));
      FeatureUtils.register(var0, ORE_TUFF, Feature.ORE, new OreConfiguration(var1, Blocks.TUFF.defaultBlockState(), 64));
      FeatureUtils.register(var0, ORE_COAL, Feature.ORE, new OreConfiguration(var11, 17));
      FeatureUtils.register(var0, ORE_COAL_BURIED, Feature.ORE, new OreConfiguration(var11, 17, 0.5F));
      FeatureUtils.register(var0, ORE_IRON, Feature.ORE, new OreConfiguration(var6, 9));
      FeatureUtils.register(var0, ORE_IRON_SMALL, Feature.ORE, new OreConfiguration(var6, 4));
      FeatureUtils.register(var0, ORE_GOLD, Feature.ORE, new OreConfiguration(var7, 9));
      FeatureUtils.register(var0, ORE_GOLD_BURIED, Feature.ORE, new OreConfiguration(var7, 9, 0.5F));
      FeatureUtils.register(var0, ORE_REDSTONE, Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(var2, Blocks.REDSTONE_ORE.defaultBlockState()), OreConfiguration.target(var3, Blocks.DEEPSLATE_REDSTONE_ORE.defaultBlockState())), 8));
      FeatureUtils.register(var0, ORE_DIAMOND_SMALL, Feature.ORE, new OreConfiguration(var8, 4, 0.5F));
      FeatureUtils.register(var0, ORE_DIAMOND_LARGE, Feature.ORE, new OreConfiguration(var8, 12, 0.7F));
      FeatureUtils.register(var0, ORE_DIAMOND_BURIED, Feature.ORE, new OreConfiguration(var8, 8, 1.0F));
      FeatureUtils.register(var0, ORE_DIAMOND_MEDIUM, Feature.ORE, new OreConfiguration(var8, 8, 0.5F));
      FeatureUtils.register(var0, ORE_LAPIS, Feature.ORE, new OreConfiguration(var9, 7));
      FeatureUtils.register(var0, ORE_LAPIS_BURIED, Feature.ORE, new OreConfiguration(var9, 7, 1.0F));
      FeatureUtils.register(var0, ORE_INFESTED, Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(var2, Blocks.INFESTED_STONE.defaultBlockState()), OreConfiguration.target(var3, Blocks.INFESTED_DEEPSLATE.defaultBlockState())), 9));
      FeatureUtils.register(var0, ORE_EMERALD, Feature.ORE, new OreConfiguration(List.of(OreConfiguration.target(var2, Blocks.EMERALD_ORE.defaultBlockState()), OreConfiguration.target(var3, Blocks.DEEPSLATE_EMERALD_ORE.defaultBlockState())), 3));
      FeatureUtils.register(var0, ORE_ANCIENT_DEBRIS_LARGE, Feature.SCATTERED_ORE, new OreConfiguration(var5, Blocks.ANCIENT_DEBRIS.defaultBlockState(), 3, 1.0F));
      FeatureUtils.register(var0, ORE_ANCIENT_DEBRIS_SMALL, Feature.SCATTERED_ORE, new OreConfiguration(var5, Blocks.ANCIENT_DEBRIS.defaultBlockState(), 2, 1.0F));
      FeatureUtils.register(var0, ORE_COPPPER_SMALL, Feature.ORE, new OreConfiguration(var10, 10));
      FeatureUtils.register(var0, ORE_COPPER_LARGE, Feature.ORE, new OreConfiguration(var10, 20));
      FeatureUtils.register(var0, ORE_CLAY, Feature.ORE, new OreConfiguration(var1, Blocks.CLAY.defaultBlockState(), 33));
   }
}
