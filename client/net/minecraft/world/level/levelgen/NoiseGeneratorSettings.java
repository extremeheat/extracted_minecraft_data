package net.minecraft.world.level.levelgen;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;

public final class NoiseGeneratorSettings {
   public static final Codec<NoiseGeneratorSettings> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(StructureSettings.CODEC.fieldOf("structures").forGetter(NoiseGeneratorSettings::structureSettings), NoiseSettings.CODEC.fieldOf("noise").forGetter(NoiseGeneratorSettings::noiseSettings), BlockState.CODEC.fieldOf("default_block").forGetter(NoiseGeneratorSettings::getDefaultBlock), BlockState.CODEC.fieldOf("default_fluid").forGetter(NoiseGeneratorSettings::getDefaultFluid), SurfaceRules.RuleSource.CODEC.fieldOf("surface_rule").forGetter(NoiseGeneratorSettings::surfaceRule), Codec.INT.fieldOf("sea_level").forGetter(NoiseGeneratorSettings::seaLevel), Codec.BOOL.fieldOf("disable_mob_generation").forGetter(NoiseGeneratorSettings::disableMobGeneration), Codec.BOOL.fieldOf("aquifers_enabled").forGetter(NoiseGeneratorSettings::isAquifersEnabled), Codec.BOOL.fieldOf("noise_caves_enabled").forGetter(NoiseGeneratorSettings::isNoiseCavesEnabled), Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(NoiseGeneratorSettings::isOreVeinsEnabled), Codec.BOOL.fieldOf("noodle_caves_enabled").forGetter(NoiseGeneratorSettings::isNoodleCavesEnabled), Codec.BOOL.fieldOf("legacy_random_source").forGetter(NoiseGeneratorSettings::useLegacyRandomSource)).apply(var0, NoiseGeneratorSettings::new);
   });
   public static final Codec<Supplier<NoiseGeneratorSettings>> CODEC;
   private final WorldgenRandom.Algorithm randomSource;
   private final StructureSettings structureSettings;
   private final NoiseSettings noiseSettings;
   private final BlockState defaultBlock;
   private final BlockState defaultFluid;
   private final SurfaceRules.RuleSource surfaceRule;
   private final int seaLevel;
   private final boolean disableMobGeneration;
   private final boolean aquifersEnabled;
   private final boolean noiseCavesEnabled;
   private final boolean oreVeinsEnabled;
   private final boolean noodleCavesEnabled;
   public static final ResourceKey<NoiseGeneratorSettings> OVERWORLD;
   public static final ResourceKey<NoiseGeneratorSettings> LARGE_BIOMES;
   public static final ResourceKey<NoiseGeneratorSettings> AMPLIFIED;
   public static final ResourceKey<NoiseGeneratorSettings> NETHER;
   public static final ResourceKey<NoiseGeneratorSettings> END;
   public static final ResourceKey<NoiseGeneratorSettings> CAVES;
   public static final ResourceKey<NoiseGeneratorSettings> FLOATING_ISLANDS;

   private NoiseGeneratorSettings(StructureSettings var1, NoiseSettings var2, BlockState var3, BlockState var4, SurfaceRules.RuleSource var5, int var6, boolean var7, boolean var8, boolean var9, boolean var10, boolean var11, boolean var12) {
      super();
      this.structureSettings = var1;
      this.noiseSettings = var2;
      this.defaultBlock = var3;
      this.defaultFluid = var4;
      this.surfaceRule = var5;
      this.seaLevel = var6;
      this.disableMobGeneration = var7;
      this.aquifersEnabled = var8;
      this.noiseCavesEnabled = var9;
      this.oreVeinsEnabled = var10;
      this.noodleCavesEnabled = var11;
      this.randomSource = var12 ? WorldgenRandom.Algorithm.LEGACY : WorldgenRandom.Algorithm.XOROSHIRO;
   }

   public StructureSettings structureSettings() {
      return this.structureSettings;
   }

   public NoiseSettings noiseSettings() {
      return this.noiseSettings;
   }

   public BlockState getDefaultBlock() {
      return this.defaultBlock;
   }

   public BlockState getDefaultFluid() {
      return this.defaultFluid;
   }

   public SurfaceRules.RuleSource surfaceRule() {
      return this.surfaceRule;
   }

   public int seaLevel() {
      return this.seaLevel;
   }

   /** @deprecated */
   @Deprecated
   protected boolean disableMobGeneration() {
      return this.disableMobGeneration;
   }

   public boolean isAquifersEnabled() {
      return this.aquifersEnabled;
   }

   public boolean isNoiseCavesEnabled() {
      return this.noiseCavesEnabled;
   }

   public boolean isOreVeinsEnabled() {
      return this.oreVeinsEnabled;
   }

   public boolean isNoodleCavesEnabled() {
      return this.noodleCavesEnabled;
   }

   public boolean useLegacyRandomSource() {
      return this.randomSource == WorldgenRandom.Algorithm.LEGACY;
   }

   public RandomSource createRandomSource(long var1) {
      return this.getRandomSource().newInstance(var1);
   }

   public WorldgenRandom.Algorithm getRandomSource() {
      return this.randomSource;
   }

   public boolean stable(ResourceKey<NoiseGeneratorSettings> var1) {
      return Objects.equals(this, BuiltinRegistries.NOISE_GENERATOR_SETTINGS.get(var1));
   }

   private static void register(ResourceKey<NoiseGeneratorSettings> var0, NoiseGeneratorSettings var1) {
      BuiltinRegistries.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, (ResourceLocation)var0.location(), var1);
   }

   public static NoiseGeneratorSettings bootstrap() {
      return (NoiseGeneratorSettings)BuiltinRegistries.NOISE_GENERATOR_SETTINGS.iterator().next();
   }

   private static NoiseGeneratorSettings end() {
      return new NoiseGeneratorSettings(new StructureSettings(false), NoiseSettings.create(0, 128, new NoiseSamplingSettings(2.0D, 1.0D, 80.0D, 160.0D), new NoiseSlider(-23.4375D, 64, -46), new NoiseSlider(-0.234375D, 7, 1), 2, 1, true, false, false, TerrainProvider.end()), Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), SurfaceRuleData.end(), 0, true, false, false, false, false, true);
   }

   private static NoiseGeneratorSettings nether() {
      HashMap var0 = Maps.newHashMap(StructureSettings.DEFAULTS);
      var0.put(StructureFeature.RUINED_PORTAL, new StructureFeatureConfiguration(25, 10, 34222645));
      return new NoiseGeneratorSettings(new StructureSettings(Optional.empty(), var0), NoiseSettings.create(0, 128, new NoiseSamplingSettings(1.0D, 3.0D, 80.0D, 60.0D), new NoiseSlider(0.9375D, 3, 0), new NoiseSlider(2.5D, 4, -1), 1, 2, false, false, false, TerrainProvider.nether()), Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), SurfaceRuleData.nether(), 32, false, false, false, false, false, true);
   }

   private static NoiseGeneratorSettings overworld(boolean var0, boolean var1) {
      return new NoiseGeneratorSettings(new StructureSettings(true), NoiseSettings.create(-64, 384, new NoiseSamplingSettings(1.0D, 1.0D, 80.0D, 160.0D), new NoiseSlider(-0.078125D, 2, var0 ? 0 : 8), new NoiseSlider(var0 ? 0.4D : 0.1171875D, 3, 0), 1, 2, false, var0, var1, TerrainProvider.overworld(var0)), Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), SurfaceRuleData.overworld(), 63, false, true, true, true, true, false);
   }

   private static NoiseGeneratorSettings caves() {
      return new NoiseGeneratorSettings(new StructureSettings(false), NoiseSettings.create(-64, 192, new NoiseSamplingSettings(1.0D, 3.0D, 80.0D, 60.0D), new NoiseSlider(0.9375D, 3, 0), new NoiseSlider(2.5D, 4, -1), 1, 2, false, false, false, TerrainProvider.caves()), Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), SurfaceRuleData.overworldLike(false, true, true), 32, false, false, false, false, false, true);
   }

   private static NoiseGeneratorSettings floatingIslands() {
      return new NoiseGeneratorSettings(new StructureSettings(true), NoiseSettings.create(0, 256, new NoiseSamplingSettings(2.0D, 1.0D, 80.0D, 160.0D), new NoiseSlider(-23.4375D, 64, -46), new NoiseSlider(-0.234375D, 7, 1), 2, 1, false, false, false, TerrainProvider.floatingIslands()), Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), SurfaceRuleData.overworldLike(false, false, false), -64, false, false, false, false, false, true);
   }

   static {
      CODEC = RegistryFileCodec.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, DIRECT_CODEC);
      OVERWORLD = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("overworld"));
      LARGE_BIOMES = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("large_biomes"));
      AMPLIFIED = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("amplified"));
      NETHER = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("nether"));
      END = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("end"));
      CAVES = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("caves"));
      FLOATING_ISLANDS = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("floating_islands"));
      register(OVERWORLD, overworld(false, false));
      register(LARGE_BIOMES, overworld(false, true));
      register(AMPLIFIED, overworld(true, false));
      register(NETHER, nether());
      register(END, end());
      register(CAVES, caves());
      register(FLOATING_ISLANDS, floatingIslands());
   }
}
