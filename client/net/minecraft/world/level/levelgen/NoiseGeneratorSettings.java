package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.material.EndMaterialRules;
import net.minecraft.data.worldgen.material.NetherMaterialRules;
import net.minecraft.data.worldgen.material.OverworldMaterialRules;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public record NoiseGeneratorSettings(NoiseSettings noiseSettings, BlockState defaultBlock, BlockState defaultFluid, NoiseRouter noiseRouter, Holder<MaterialRule> materialRule, List<SpawnTargetPoint> spawnTarget, int seaLevel, boolean disableMobGeneration, Optional<Aquifer.Config> aquifers, boolean useLegacyRandomSource, DebugFunctions debugFunctions) {
   public static final Codec<NoiseGeneratorSettings> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(NoiseSettings.CODEC.fieldOf("noise").forGetter(NoiseGeneratorSettings::noiseSettings), BlockState.CODEC.fieldOf("default_block").forGetter(NoiseGeneratorSettings::defaultBlock), BlockState.CODEC.fieldOf("default_fluid").forGetter(NoiseGeneratorSettings::defaultFluid), NoiseRouter.CODEC.fieldOf("noise_router").forGetter(NoiseGeneratorSettings::noiseRouter), MaterialRule.HOLDER_CODEC.fieldOf("material_rule").forGetter(NoiseGeneratorSettings::materialRule), SpawnTargetPoint.CODEC.listOf().fieldOf("spawn_target").forGetter(NoiseGeneratorSettings::spawnTarget), Codec.INT.fieldOf("sea_level").forGetter(NoiseGeneratorSettings::seaLevel), Codec.BOOL.fieldOf("disable_mob_generation").forGetter(NoiseGeneratorSettings::disableMobGeneration), Aquifer.Config.CODEC.optionalFieldOf("aquifers").forGetter(NoiseGeneratorSettings::aquifers), Codec.BOOL.fieldOf("legacy_random_source").forGetter(NoiseGeneratorSettings::useLegacyRandomSource), NoiseGeneratorSettings.DebugFunctions.CODEC.optionalFieldOf("debug_functions", NoiseGeneratorSettings.DebugFunctions.EMPTY).forGetter(NoiseGeneratorSettings::debugFunctions)).apply(i, NoiseGeneratorSettings::new));
   public static final Codec<Holder<NoiseGeneratorSettings>> CODEC;
   public static final ResourceKey<NoiseGeneratorSettings> OVERWORLD;
   public static final ResourceKey<NoiseGeneratorSettings> LARGE_BIOMES;
   public static final ResourceKey<NoiseGeneratorSettings> AMPLIFIED;
   public static final ResourceKey<NoiseGeneratorSettings> NETHER;
   public static final ResourceKey<NoiseGeneratorSettings> END;
   public static final ResourceKey<NoiseGeneratorSettings> CAVES;
   public static final ResourceKey<NoiseGeneratorSettings> FLOATING_ISLANDS;

   public NoiseGeneratorSettings {
      super();
   }

   /** @deprecated */
   @Deprecated
   public boolean disableMobGeneration() {
      return this.disableMobGeneration;
   }

   public WorldgenRandom.Algorithm getRandomSource() {
      return this.useLegacyRandomSource ? WorldgenRandom.Algorithm.LEGACY : WorldgenRandom.Algorithm.XOROSHIRO;
   }

   public static void bootstrap(final BootstrapContext<NoiseGeneratorSettings> context) {
      context.register(OVERWORLD, overworld(context, false, false));
      context.register(LARGE_BIOMES, overworld(context, false, true));
      context.register(AMPLIFIED, overworld(context, true, false));
      context.register(NETHER, nether(context));
      context.register(END, end(context));
      context.register(CAVES, caves(context));
      context.register(FLOATING_ISLANDS, floatingIslands(context));
   }

   private static NoiseGeneratorSettings end(final BootstrapContext<?> context) {
      NoiseRouter router = NoiseRouterData.end(context.lookup(Registries.DENSITY_FUNCTION));
      return new NoiseGeneratorSettings(NoiseSettings.END_NOISE_SETTINGS, Blocks.END_STONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), router, context.lookup(Registries.MATERIAL_RULE).getOrThrow(EndMaterialRules.END), List.of(), 0, true, Optional.empty(), true, new DebugFunctions(List.of(new DebugFunctionEntry("N", router.finalDensity()), new DebugFunctionEntry("IS", router.erosion()))));
   }

   private static NoiseGeneratorSettings nether(final BootstrapContext<?> context) {
      NoiseRouter router = NoiseRouterData.nether(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE));
      return new NoiseGeneratorSettings(NoiseSettings.NETHER_NOISE_SETTINGS, Blocks.NETHERRACK.defaultBlockState(), Blocks.LAVA.defaultBlockState(), router, context.lookup(Registries.MATERIAL_RULE).getOrThrow(NetherMaterialRules.NETHER), List.of(), 32, false, Optional.empty(), true, new DebugFunctions(List.of(new DebugFunctionEntry("N", router.finalDensity()), new DebugFunctionEntry("T", router.temperature()), new DebugFunctionEntry("V", router.vegetation()))));
   }

   private static NoiseGeneratorSettings overworld(final BootstrapContext<?> context, final boolean isAmplified, final boolean largeBiomes) {
      HolderGetter<DensityFunction> functions = context.lookup(Registries.DENSITY_FUNCTION);
      HolderGetter<NormalNoise> noises = context.lookup(Registries.NOISE);
      OverworldFunctionSet<ResourceKey<DensityFunction>> functionNames;
      if (isAmplified) {
         functionNames = NoiseRouterData.AMPLIFIED_OVERWORLD_FUNCTIONS;
      } else if (largeBiomes) {
         functionNames = NoiseRouterData.LARGE_OVERWORLD_FUNCTIONS;
      } else {
         functionNames = NoiseRouterData.OVERWORLD_FUNCTIONS;
      }

      Holder<DensityFunction> weirdness = functions.getOrThrow(NoiseRouterData.RIDGES);
      OverworldBiomeBuilder var10000 = new OverworldBiomeBuilder();
      Objects.requireNonNull(functions);
      List<SpawnTargetPoint> spawnTarget = var10000.spawnTarget(functionNames.map(functions::getOrThrow), weirdness);
      NoiseRouter router = NoiseRouterData.overworld(functions, functionNames);
      return new NoiseGeneratorSettings(NoiseSettings.OVERWORLD_NOISE_SETTINGS, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), router, context.lookup(Registries.MATERIAL_RULE).getOrThrow(OverworldMaterialRules.OVERWORLD), spawnTarget, 63, false, Optional.of(NoiseRouterData.overworldAquifers(functions, noises, functionNames)), false, new DebugFunctions(List.of(new DebugFunctionEntry("N", router.finalDensity()), new DebugFunctionEntry("T", router.temperature()), new DebugFunctionEntry("V", router.vegetation()), new DebugFunctionEntry("C", router.continents()), new DebugFunctionEntry("E", router.erosion()), new DebugFunctionEntry("D", router.depth()), new DebugFunctionEntry("W", router.ridges()), new DebugFunctionEntry("PV", NoiseRouterData.peaksAndValleys(router.ridges())), new DebugFunctionEntry("PS", NoiseRouterData.getFunction(functions, functionNames.preliminarySurfaceLevel())))));
   }

   private static NoiseGeneratorSettings caves(final BootstrapContext<?> context) {
      NoiseRouter router = NoiseRouterData.caves(context.lookup(Registries.DENSITY_FUNCTION));
      return new NoiseGeneratorSettings(NoiseSettings.CAVES_NOISE_SETTINGS, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), router, context.lookup(Registries.MATERIAL_RULE).getOrThrow(OverworldMaterialRules.OVERWORLD_CAVES), List.of(), 32, false, Optional.empty(), true, new DebugFunctions(List.of(new DebugFunctionEntry("N", router.finalDensity()))));
   }

   private static NoiseGeneratorSettings floatingIslands(final BootstrapContext<?> context) {
      NoiseRouter router = NoiseRouterData.floatingIslands(context.lookup(Registries.DENSITY_FUNCTION), context.lookup(Registries.NOISE));
      return new NoiseGeneratorSettings(NoiseSettings.FLOATING_ISLANDS_NOISE_SETTINGS, Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), router, context.lookup(Registries.MATERIAL_RULE).getOrThrow(OverworldMaterialRules.OVERWORLD_FLOATING_ISLANDS), List.of(), -64, false, Optional.empty(), true, new DebugFunctions(List.of(new DebugFunctionEntry("N", router.finalDensity()))));
   }

   static {
      CODEC = RegistryCodecs.holder(Registries.NOISE_SETTINGS, DIRECT_CODEC);
      OVERWORLD = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("overworld"));
      LARGE_BIOMES = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("large_biomes"));
      AMPLIFIED = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("amplified"));
      NETHER = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("nether"));
      END = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("end"));
      CAVES = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("caves"));
      FLOATING_ISLANDS = ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.withDefaultNamespace("floating_islands"));
   }

   public static record DebugFunctions(List<DebugFunctionEntry> functions) {
      public static final DebugFunctions EMPTY = new DebugFunctions(List.of());
      public static final Codec<DebugFunctions> CODEC;

      public DebugFunctions {
         super();
      }

      static {
         CODEC = NoiseGeneratorSettings.DebugFunctionEntry.CODEC.listOf().xmap(DebugFunctions::new, DebugFunctions::functions);
      }
   }

   public static record DebugFunctionEntry(String label, DensityFunction function) {
      public static final Codec<DebugFunctionEntry> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.STRING.fieldOf("label").forGetter(DebugFunctionEntry::label), DensityFunction.CODEC.fieldOf("function").forGetter(DebugFunctionEntry::function)).apply(i, DebugFunctionEntry::new));

      public DebugFunctionEntry {
         super();
      }
   }
}
