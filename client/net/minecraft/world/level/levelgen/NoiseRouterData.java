package net.minecraft.world.level.levelgen;

import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.densityfunction.DistanceMetric;
import net.minecraft.world.level.levelgen.densityfunction.op.SplineFunction;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseRouterData {
   public static final float GLOBAL_OFFSET = -0.50375F;
   private static final float ORE_THICKNESS = 0.08F;
   private static final float VEININESS_FREQUENCY = 1.5F;
   private static final float NOODLE_SPACING_AND_STRAIGHTNESS = 1.5F;
   private static final float SURFACE_DENSITY_THRESHOLD = 1.5625F;
   private static final float CHEESE_NOISE_TARGET = -0.703125F;
   public static final float NOISE_ZERO = 0.390625F;
   public static final int ISLAND_CHUNK_DISTANCE = 64;
   public static final long ISLAND_CHUNK_DISTANCE_SQR = 4096L;
   private static final int DENSITY_Y_ANCHOR_BOTTOM = -64;
   private static final int DENSITY_Y_ANCHOR_TOP = 320;
   private static final float DENSITY_Y_BOTTOM = 1.5F;
   private static final float DENSITY_Y_TOP = -1.5F;
   private static final int OVERWORLD_BOTTOM_SLIDE_HEIGHT = 24;
   private static final float BASE_DENSITY_MULTIPLIER = 4.0F;
   private static final DensityFunction BLENDING_FACTOR = DensityFunctions.constant(10.0F);
   private static final DensityFunction BLENDING_JAGGEDNESS = DensityFunctions.zero();
   private static final ResourceKey<DensityFunction> ZERO = createKey("zero");
   private static final ResourceKey<DensityFunction> Y = createKey("y");
   private static final ResourceKey<DensityFunction> SHIFT_X = createKey("shift_x");
   private static final ResourceKey<DensityFunction> SHIFT_Z = createKey("shift_z");
   private static final ResourceKey<DensityFunction> BASE_3D_NOISE_OVERWORLD = createKey("overworld/base_3d_noise");
   private static final ResourceKey<DensityFunction> BASE_3D_NOISE_NETHER = createKey("nether/base_3d_noise");
   private static final ResourceKey<DensityFunction> BASE_3D_NOISE_END = createKey("end/base_3d_noise");
   public static final ResourceKey<DensityFunction> RIDGES = createKey("overworld/ridges");
   public static final ResourceKey<DensityFunction> RIDGES_FOLDED = createKey("overworld/ridges_folded");
   public static final OverworldFunctionSet<ResourceKey<DensityFunction>> OVERWORLD_FUNCTIONS = new OverworldFunctionSet<ResourceKey<DensityFunction>>(createKey("overworld/temperature"), createKey("overworld/vegetation"), createKey("overworld/continents"), createKey("overworld/erosion"), createKey("overworld/offset"), createKey("overworld/factor"), createKey("overworld/jaggedness"), createKey("overworld/depth"), createKey("overworld/sloped_cheese"), createKey("overworld/preliminary_surface_level"), createKey("overworld/final_density"));
   public static final OverworldFunctionSet<ResourceKey<DensityFunction>> AMPLIFIED_OVERWORLD_FUNCTIONS;
   public static final OverworldFunctionSet<ResourceKey<DensityFunction>> LARGE_OVERWORLD_FUNCTIONS;
   private static final ResourceKey<DensityFunction> END_ISLANDS;
   private static final ResourceKey<DensityFunction> SLOPED_CHEESE_END;
   private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION;
   private static final ResourceKey<DensityFunction> ENTRANCES;
   private static final ResourceKey<DensityFunction> NOODLE;
   private static final ResourceKey<DensityFunction> PILLARS;
   private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR;
   private static final ResourceKey<DensityFunction> SPAGHETTI_2D;
   private static final ResourceKey<DensityFunction> ORE_VEIN_MASK;
   private static final ResourceKey<DensityFunction> ORE_VEIN_TOGGLE;
   private static final ResourceKey<DensityFunction> ORE_VEIN_RICHNESS;
   private static final ResourceKey<DensityFunction> ORE_VEIN_COPPER_DENSITY;
   private static final ResourceKey<DensityFunction> ORE_VEIN_IRON_DENSITY;
   private static final ResourceKey<DensityFunction> ORE_VEIN_GAP;

   public NoiseRouterData() {
      super();
   }

   private static ResourceKey<DensityFunction> createKey(final String name) {
      return ResourceKey.create(Registries.DENSITY_FUNCTION, Identifier.withDefaultNamespace(name));
   }

   public static Holder<? extends DensityFunction> bootstrap(final BootstrapContext<DensityFunction> context) {
      HolderGetter<NormalNoise> noises = context.lookup(Registries.NOISE);
      HolderGetter<DensityFunction> functions = context.lookup(Registries.DENSITY_FUNCTION);
      context.register(ZERO, DensityFunctions.zero());
      int belowBottom = DimensionType.MIN_Y * 2;
      int aboveTop = DimensionType.MAX_Y * 2;
      context.register(Y, DensityFunctions.yClampedGradient(belowBottom, aboveTop, (float)belowBottom, (float)aboveTop));
      registerOreVeins(context);
      DensityFunction shiftX = registerAndWrap(context, SHIFT_X, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftA(noises.getOrThrow(Noises.SHIFT)))));
      DensityFunction shiftZ = registerAndWrap(context, SHIFT_Z, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftB(noises.getOrThrow(Noises.SHIFT)))));
      context.register(BASE_3D_NOISE_OVERWORLD, new BlendedNoise(0.25, 0.125, 80.0, 160.0, 8.0));
      context.register(BASE_3D_NOISE_NETHER, new BlendedNoise(0.25, 0.375, 80.0, 60.0, 8.0));
      context.register(BASE_3D_NOISE_END, new BlendedNoise(0.25, 0.25, 80.0, 160.0, 4.0));
      registerAndWrap(context, OVERWORLD_FUNCTIONS.temperature(), DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noises.getOrThrow(Noises.TEMPERATURE)));
      registerAndWrap(context, LARGE_OVERWORLD_FUNCTIONS.temperature(), DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noises.getOrThrow(Noises.TEMPERATURE_LARGE)));
      registerAndWrap(context, OVERWORLD_FUNCTIONS.vegetation(), DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noises.getOrThrow(Noises.VEGETATION)));
      registerAndWrap(context, LARGE_OVERWORLD_FUNCTIONS.vegetation(), DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noises.getOrThrow(Noises.VEGETATION_LARGE)));
      DensityFunction continents = registerAndWrap(context, OVERWORLD_FUNCTIONS.continents(), DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noises.getOrThrow(Noises.CONTINENTALNESS))));
      DensityFunction erosion = registerAndWrap(context, OVERWORLD_FUNCTIONS.erosion(), DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noises.getOrThrow(Noises.EROSION))));
      DensityFunction ridge = registerAndWrap(context, RIDGES, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noises.getOrThrow(Noises.RIDGE))));
      context.register(RIDGES_FOLDED, peaksAndValleys(ridge));
      DensityFunction jaggedNoise = DensityFunctions.noise(noises.getOrThrow(Noises.JAGGED), 1500.0, 0.0);
      registerTerrainNoises(context, functions, noises, jaggedNoise, continents, erosion, OVERWORLD_FUNCTIONS, false);
      DensityFunction continentsLarge = registerAndWrap(context, LARGE_OVERWORLD_FUNCTIONS.continents(), DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noises.getOrThrow(Noises.CONTINENTALNESS_LARGE))));
      DensityFunction erosionLarge = registerAndWrap(context, LARGE_OVERWORLD_FUNCTIONS.erosion(), DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noises.getOrThrow(Noises.EROSION_LARGE))));
      registerTerrainNoises(context, functions, noises, jaggedNoise, continentsLarge, erosionLarge, LARGE_OVERWORLD_FUNCTIONS, false);
      registerTerrainNoises(context, functions, noises, jaggedNoise, continents, erosion, AMPLIFIED_OVERWORLD_FUNCTIONS, true);
      DensityFunction endIslands = registerAndWrap(context, END_ISLANDS, createEndIslands());
      context.register(SLOPED_CHEESE_END, DensityFunctions.add(endIslands, getFunction(functions, BASE_3D_NOISE_END)));
      context.register(SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction(noises));
      context.register(SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(noises.getOrThrow(Noises.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6F, -1.3F)));
      context.register(SPAGHETTI_2D, spaghetti2D(functions, noises));
      context.register(ENTRANCES, entrances(functions, noises));
      context.register(NOODLE, noodle(functions, noises));
      return context.register(PILLARS, pillars(noises));
   }

   private static DensityFunction createEndIslands() {
      DensityFunction distanceToMainIsland = DensityFunctions.distanceToPoint(Vec3i.ZERO, DistanceMetric.EUCLIDEAN);
      DensityFunction mainIsland = DensityFunctions.sliceY(DensityFunctions.constant(100.0F).sub(distanceToMainIsland).clamp(-100.0F, 80.0F).sub(8.0F).mul(0.0078125F), 0);
      DensityFunction outerIslands = DensityFunctions.endOuterIslands(0L);
      return DensityFunctions.cache2d(DensityFunctions.max(mainIsland, outerIslands));
   }

   private static void registerTerrainNoises(final BootstrapContext<DensityFunction> context, final HolderGetter<DensityFunction> functions, final HolderGetter<NormalNoise> noises, final DensityFunction jaggedNoise, final DensityFunction continentsFunction, final DensityFunction erosionFunction, final OverworldFunctionSet<ResourceKey<DensityFunction>> names, final boolean amplified) {
      SplineFunction.Coordinate continents = new SplineFunction.Coordinate(continentsFunction);
      SplineFunction.Coordinate erosion = new SplineFunction.Coordinate(erosionFunction);
      SplineFunction.Coordinate weirdness = new SplineFunction.Coordinate(getFunction(functions, RIDGES));
      SplineFunction.Coordinate ridges = new SplineFunction.Coordinate(getFunction(functions, RIDGES_FOLDED));
      DensityFunction offset = registerAndWrap(context, names.offset(), splineWithBlending(DensityFunctions.add(DensityFunctions.constant(-0.50375F), DensityFunctions.spline(TerrainProvider.overworldOffset(continents, erosion, ridges, amplified))), DensityFunctions.blendOffset()));
      DensityFunction factor = registerAndWrap(context, names.factor(), splineWithBlending(DensityFunctions.spline(TerrainProvider.overworldFactor(continents, erosion, weirdness, ridges, amplified)), BLENDING_FACTOR));
      DensityFunction depth = registerAndWrap(context, names.depth(), offsetToDepth(offset));
      DensityFunction unscaledJaggedness = registerAndWrap(context, names.jaggedness(), splineWithBlending(DensityFunctions.spline(TerrainProvider.overworldJaggedness(continents, erosion, weirdness, ridges, amplified)), BLENDING_JAGGEDNESS));
      DensityFunction jaggedness = DensityFunctions.flatCache(DensityFunctions.mul(unscaledJaggedness, jaggedNoise.halfNegative()));
      DensityFunction initialDensity = noiseGradientDensity(factor, DensityFunctions.add(depth, jaggedness));
      DensityFunction slopedCheese = registerAndWrap(context, names.slopedCheese(), DensityFunctions.cacheOnce(DensityFunctions.add(initialDensity, getFunction(functions, BASE_3D_NOISE_OVERWORLD))));
      context.register(names.preliminarySurfaceLevel(), preliminarySurfaceLevel(offset, factor, amplified));
      DensityFunction surfaceWithEntrances = DensityFunctions.min(slopedCheese, getFunction(functions, ENTRANCES).mul(5.0F));
      DensityFunction caves = DensityFunctions.rangeChoice(slopedCheese, -1000000.0F, 1.5625F, surfaceWithEntrances, underground(functions, noises, slopedCheese));
      context.register(names.finalDensity(), DensityFunctions.add(DensityFunctions.min(postProcess(slideOverworld(amplified, caves)), getFunction(functions, NOODLE)), DensityFunctions.beardifier()));
   }

   private static DensityFunction offsetToDepth(final DensityFunction offset) {
      return DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 320, 1.5F, -1.5F), offset);
   }

   private static DensityFunction registerAndWrap(final BootstrapContext<DensityFunction> context, final ResourceKey<DensityFunction> name, final DensityFunction value) {
      return new DensityFunctions.HolderHolder(context.register(name, value));
   }

   private static DensityFunction getFunction(final HolderGetter<DensityFunction> functions, final ResourceKey<DensityFunction> name) {
      return new DensityFunctions.HolderHolder(functions.getOrThrow(name));
   }

   private static DensityFunction peaksAndValleys(final DensityFunction weirdness) {
      return DensityFunctions.mul(DensityFunctions.add(DensityFunctions.add(weirdness.abs(), DensityFunctions.constant(-0.6666667F)).abs(), DensityFunctions.constant(-0.33333334F)), DensityFunctions.constant(-3.0F));
   }

   public static float peaksAndValleys(final float weirdness) {
      return TerrainProvider.peaksAndValleys(weirdness);
   }

   private static DensityFunction spaghettiRoughnessFunction(final HolderGetter<NormalNoise> noises) {
      DensityFunction spaghettiRoughnessNoise = DensityFunctions.noise(noises.getOrThrow(Noises.SPAGHETTI_ROUGHNESS));
      DensityFunction spaghettiRoughnessModulator = DensityFunctions.mappedNoise(noises.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0F, -0.1F);
      return DensityFunctions.cacheOnce(DensityFunctions.mul(spaghettiRoughnessModulator, DensityFunctions.add(spaghettiRoughnessNoise.abs(), DensityFunctions.constant(-0.4F))));
   }

   private static DensityFunction entrances(final HolderGetter<DensityFunction> functions, final HolderGetter<NormalNoise> noises) {
      DensityFunction spaghetti3DRarityModulator = DensityFunctions.cacheOnce(DensityFunctions.noise(noises.getOrThrow(Noises.SPAGHETTI_3D_RARITY), 2.0, 1.0));
      DensityFunction spaghetti3DThicknessModulator = DensityFunctions.mappedNoise(noises.getOrThrow(Noises.SPAGHETTI_3D_THICKNESS), -0.065F, -0.088F);
      DensityFunction spaghetti3DCave1 = NoiseRouterData.QuantizedSpaghettiRarity.wrapRarity3d(spaghetti3DRarityModulator, noises.getOrThrow(Noises.SPAGHETTI_3D_1));
      DensityFunction spaghetti3DCave2 = NoiseRouterData.QuantizedSpaghettiRarity.wrapRarity3d(spaghetti3DRarityModulator, noises.getOrThrow(Noises.SPAGHETTI_3D_2));
      DensityFunction spaghetti3DFunction = DensityFunctions.add(DensityFunctions.max(spaghetti3DCave1, spaghetti3DCave2), spaghetti3DThicknessModulator).clamp(-1.0F, 1.0F);
      DensityFunction spaghettiRoughnessFunction = getFunction(functions, SPAGHETTI_ROUGHNESS_FUNCTION);
      DensityFunction bigEntranceNoiseSource = DensityFunctions.noise(noises.getOrThrow(Noises.CAVE_ENTRANCE), 0.75, 0.5);
      DensityFunction bigEntrancesFunction = DensityFunctions.add(bigEntranceNoiseSource.add(0.37F), DensityFunctions.yClampedGradient(-10, 30, 0.3F, 0.0F));
      return DensityFunctions.cacheOnce(DensityFunctions.min(bigEntrancesFunction, DensityFunctions.add(spaghettiRoughnessFunction, spaghetti3DFunction)));
   }

   private static DensityFunction noodle(final HolderGetter<DensityFunction> functions, final HolderGetter<NormalNoise> noises) {
      DensityFunction y = getFunction(functions, Y);
      int minBlockY = -64;
      int noodleMinY = -60;
      int noodleMaxY = 320;
      DensityFunction noodleToggle = yLimitedInterpolatable(y, DensityFunctions.noise(noises.getOrThrow(Noises.NOODLE), 1.0, 1.0), -60, 320, -1);
      DensityFunction noodleThickness = yLimitedInterpolatable(y, DensityFunctions.mappedNoise(noises.getOrThrow(Noises.NOODLE_THICKNESS), 1.0, 1.0, -0.05F, -0.1F), -60, 320, 0);
      double noodleRidgeFrequency = 2.6666666666666665;
      DensityFunction noodleRidgeA = yLimitedInterpolatable(y, DensityFunctions.noise(noises.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
      DensityFunction noodleRidgeB = yLimitedInterpolatable(y, DensityFunctions.noise(noises.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
      DensityFunction noodleRidged = DensityFunctions.max(noodleRidgeA.abs(), noodleRidgeB.abs()).mul(1.5F);
      return DensityFunctions.rangeChoice(noodleToggle, -1000000.0F, 0.0F, DensityFunctions.constant(64.0F), DensityFunctions.add(noodleThickness, noodleRidged));
   }

   private static DensityFunction pillars(final HolderGetter<NormalNoise> noises) {
      double xzFrequency = 25.0;
      double yFrequency = 0.3;
      DensityFunction pillarNoiseSource = DensityFunctions.noise(noises.getOrThrow(Noises.PILLAR), 25.0, 0.3);
      DensityFunction pillarRarenessModulator = DensityFunctions.mappedNoise(noises.getOrThrow(Noises.PILLAR_RARENESS), 0.0F, -2.0F);
      DensityFunction pillarThicknessModulator = DensityFunctions.mappedNoise(noises.getOrThrow(Noises.PILLAR_THICKNESS), 0.0F, 1.1F);
      DensityFunction pillarsWithRareness = DensityFunctions.add(pillarNoiseSource.mul(2.0F), pillarRarenessModulator);
      return DensityFunctions.cacheOnce(DensityFunctions.mul(pillarsWithRareness, pillarThicknessModulator.cube()));
   }

   private static DensityFunction spaghetti2D(final HolderGetter<DensityFunction> functions, final HolderGetter<NormalNoise> noises) {
      DensityFunction spaghetti2DRarityModulator = DensityFunctions.noise(noises.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
      DensityFunction spaghetti2DCave = NoiseRouterData.QuantizedSpaghettiRarity.wrapRarity2d(spaghetti2DRarityModulator, noises.getOrThrow(Noises.SPAGHETTI_2D));
      DensityFunction spaghetti2DElevationModulator = DensityFunctions.mappedNoise(noises.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), 0.0, (float)Math.floorDiv(-64, 8), 8.0F);
      DensityFunction spaghetti2DThicknessModulator = getFunction(functions, SPAGHETTI_2D_THICKNESS_MODULATOR);
      DensityFunction slopedSpaghetti = DensityFunctions.add(DensityFunctions.flatCache(spaghetti2DElevationModulator), DensityFunctions.yClampedGradient(-64, 320, 8.0F, -40.0F)).abs();
      DensityFunction layerRidged = DensityFunctions.add(slopedSpaghetti, spaghetti2DThicknessModulator).cube();
      float ridgeOffset = 0.083F;
      DensityFunction caveNoise = DensityFunctions.add(spaghetti2DCave, spaghetti2DThicknessModulator.mul(0.083F));
      return DensityFunctions.max(caveNoise, layerRidged).clamp(-1.0F, 1.0F);
   }

   private static DensityFunction underground(final HolderGetter<DensityFunction> functions, final HolderGetter<NormalNoise> noises, final DensityFunction slopedCheese) {
      DensityFunction spaghetti2DFunction = getFunction(functions, SPAGHETTI_2D);
      DensityFunction spaghettiRoughnessFunction = getFunction(functions, SPAGHETTI_ROUGHNESS_FUNCTION);
      DensityFunction layerNoiseSource = DensityFunctions.noise(noises.getOrThrow(Noises.CAVE_LAYER), 8.0);
      DensityFunction layerizedCavernsFunction = layerNoiseSource.square().mul(4.0F);
      DensityFunction cheese = DensityFunctions.noise(noises.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
      DensityFunction solidifedCheeseWithTopSlide = DensityFunctions.add(cheese.add(0.27F).clamp(-1.0F, 1.0F), slopedCheese.mul(-0.64F).add(1.5F).clamp(0.0F, 0.5F));
      DensityFunction baseCaveDensity = DensityFunctions.add(layerizedCavernsFunction, solidifedCheeseWithTopSlide);
      DensityFunction undergroundSubtractions = DensityFunctions.min(DensityFunctions.min(baseCaveDensity, getFunction(functions, ENTRANCES)), DensityFunctions.add(spaghetti2DFunction, spaghettiRoughnessFunction));
      DensityFunction pillarsWithoutCutoff = getFunction(functions, PILLARS);
      DensityFunction pillars = DensityFunctions.rangeChoice(pillarsWithoutCutoff, -1000000.0F, 0.03F, DensityFunctions.constant(-1000000.0F), pillarsWithoutCutoff);
      return DensityFunctions.max(undergroundSubtractions, pillars);
   }

   private static DensityFunction postProcess(final DensityFunction slide) {
      DensityFunction blended = DensityFunctions.blendDensity(slide);
      return DensityFunctions.interpolated(DensityFunctions.mul(blended, DensityFunctions.constant(0.64F))).squeeze();
   }

   protected static NoiseRouter overworld(final HolderGetter<DensityFunction> functions, final OverworldFunctionSet<ResourceKey<DensityFunction>> functionNames) {
      OverworldFunctionSet<DensityFunction> functionSet = functionNames.<DensityFunction>map((key) -> getFunction(functions, key));
      return new NoiseRouter(functionSet.temperature(), functionSet.vegetation(), functionSet.continents(), functionSet.erosion(), functionSet.depth(), getFunction(functions, RIDGES), functionSet.preliminarySurfaceLevel(), functionSet.finalDensity());
   }

   protected static Aquifer.Config overworldAquifers(final HolderGetter<DensityFunction> functions, final HolderGetter<NormalNoise> noises, final OverworldFunctionSet<ResourceKey<DensityFunction>> names) {
      DensityFunction barrierNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_BARRIER), 0.5);
      DensityFunction fluidLevelFloodednessNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
      DensityFunction fluidLevelSpreadNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
      DensityFunction lavaNoise = DensityFunctions.noise(noises.getOrThrow(Noises.AQUIFER_LAVA));
      DensityFunction exclusion = OverworldBiomeBuilder.deepDarkRegion(getFunction(functions, names.erosion()), getFunction(functions, names.depth()));
      return new Aquifer.Config(barrierNoise, fluidLevelFloodednessNoise, fluidLevelSpreadNoise, lavaNoise, exclusion, getFunction(functions, names.preliminarySurfaceLevel()));
   }

   private static void registerOreVeins(final BootstrapContext<DensityFunction> context) {
      HolderGetter<DensityFunction> functions = context.lookup(Registries.DENSITY_FUNCTION);
      HolderGetter<NormalNoise> noises = context.lookup(Registries.NOISE);
      DensityFunction y = getFunction(functions, Y);
      int veinMinY = Stream.of(OreVeinifier.VeinType.values()).mapToInt((t) -> t.minY).min().orElseThrow();
      int veinMaxY = Stream.of(OreVeinifier.VeinType.values()).mapToInt((t) -> t.maxY).max().orElseThrow();
      int interpolatedCellHeight = NoiseSettings.OVERWORLD_NOISE_SETTINGS.getCellHeight();
      int interpolatedVeinMinY = Mth.floorDiv(veinMinY, interpolatedCellHeight) * interpolatedCellHeight;
      int interpolatedVeinMaxY = (Mth.floorDiv(veinMaxY, interpolatedCellHeight) + 1) * interpolatedCellHeight;
      DensityFunction veinToggle = registerAndWrap(context, ORE_VEIN_TOGGLE, yLimitedInterpolatable(y, DensityFunctions.noise(noises.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5), interpolatedVeinMinY, interpolatedVeinMaxY, 0));
      context.register(ORE_VEIN_RICHNESS, DensityFunctions.clampedMap(veinToggle.abs(), 0.4F, 0.6F, 0.1F, 0.3F));
      DensityFunction veinMask = registerAndWrap(context, ORE_VEIN_MASK, DensityFunctions.cacheOnce(createBaseOreVeinMask(noises, y, veinToggle, interpolatedVeinMinY, interpolatedVeinMaxY)));
      context.register(ORE_VEIN_COPPER_DENSITY, createOreVeinDensity(OreVeinifier.VeinType.COPPER, y, veinToggle, veinMask, true));
      context.register(ORE_VEIN_IRON_DENSITY, createOreVeinDensity(OreVeinifier.VeinType.IRON, y, veinToggle, veinMask, false));
      context.register(ORE_VEIN_GAP, DensityFunctions.constant(-0.3F).sub(DensityFunctions.noise(noises.getOrThrow(Noises.ORE_GAP))));
   }

   protected static List<OreVeinifier> overworldOreVeins(final HolderGetter<DensityFunction> functions) {
      DensityFunction richness = getFunction(functions, ORE_VEIN_RICHNESS);
      DensityFunction gap = getFunction(functions, ORE_VEIN_GAP);
      return List.of(OreVeinifier.VeinType.COPPER.create(getFunction(functions, ORE_VEIN_COPPER_DENSITY), richness, gap), OreVeinifier.VeinType.IRON.create(getFunction(functions, ORE_VEIN_IRON_DENSITY), richness, gap));
   }

   private static DensityFunction createBaseOreVeinMask(final HolderGetter<NormalNoise> noises, final DensityFunction y, final DensityFunction toggle, final int minY, final int maxY) {
      float oreRidgeFrequency = 4.0F;
      int noVein = 1;
      DensityFunction veinA = yLimitedInterpolatable(y, DensityFunctions.noise(noises.getOrThrow(Noises.ORE_VEIN_A), 4.0, 4.0), minY, maxY, 1).abs();
      DensityFunction veinB = yLimitedInterpolatable(y, DensityFunctions.noise(noises.getOrThrow(Noises.ORE_VEIN_B), 4.0, 4.0), minY, maxY, 1).abs();
      return DensityFunctions.rangeChoice(toggle, -0.4F, 0.4F, DensityFunctions.constant(-1.0F), DensityFunctions.constant(0.08F).sub(DensityFunctions.max(veinA, veinB)));
   }

   private static DensityFunction createOreVeinDensity(final OreVeinifier.VeinType type, final DensityFunction y, final DensityFunction toggle, final DensityFunction baseVeinMask, final boolean whenTogglePositive) {
      DensityFunction noVein = DensityFunctions.constant(-1.0F);
      DensityFunction distanceFromEdge = DensityFunctions.cacheOnce(DensityFunctions.min(DensityFunctions.constant((float)type.maxY).sub(y), y.sub((float)type.minY)));
      DensityFunction edgeRoundoff = DensityFunctions.clampedMap(distanceFromEdge, 0.0F, 20.0F, -0.2F, 0.0F);
      DensityFunction veininess = whenTogglePositive ? toggle : toggle.negate();
      return DensityFunctions.rangeChoice(y, (float)type.minY, (float)type.maxY, DensityFunctions.rangeChoice(baseVeinMask, 0.0F, 1000000.0F, DensityFunctions.rangeChoice(veininess.sub(0.4F).add(edgeRoundoff), 0.0F, 1000000.0F, DensityFunctions.constant(0.7F), noVein), noVein), noVein);
   }

   private static DensityFunction slideOverworld(final boolean isAmplified, final DensityFunction caves) {
      return slide(caves, -64, 384, isAmplified ? 16 : 80, isAmplified ? 0 : 64, -0.078125F, 0, 24, isAmplified ? 0.4F : 0.1171875F);
   }

   private static DensityFunction slideNetherLike(final HolderGetter<DensityFunction> functions, final int minY, final int height) {
      return slide(getFunction(functions, BASE_3D_NOISE_NETHER), minY, height, 24, 0, 0.9375F, -8, 24, 2.5F);
   }

   private static DensityFunction slideEndLike(final DensityFunction caves, final int minY, final int height) {
      return slide(caves, minY, height, 72, -184, -23.4375F, 4, 32, -0.234375F);
   }

   protected static NoiseRouter nether(final HolderGetter<DensityFunction> functions, final HolderGetter<NormalNoise> noises) {
      DensityFunction temperature = DensityFunctions.shiftedNoise2d(DensityFunctions.zero(), DensityFunctions.zero(), 0.25, noises.getOrThrow(Noises.TEMPERATURE_NETHER));
      DensityFunction vegetation = DensityFunctions.shiftedNoise2d(DensityFunctions.zero(), DensityFunctions.zero(), 0.25, noises.getOrThrow(Noises.VEGETATION_NETHER));
      DensityFunction slide = slideNetherLike(functions, 0, 128);
      DensityFunction fullNoise = DensityFunctions.add(postProcess(slide), DensityFunctions.beardifier());
      return new NoiseRouter(temperature, vegetation, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), fullNoise);
   }

   protected static NoiseRouter caves(final HolderGetter<DensityFunction> functions) {
      DensityFunction slide = slideNetherLike(functions, -64, 192);
      return simpleRouter(DensityFunctions.add(postProcess(slide), DensityFunctions.beardifier()));
   }

   protected static NoiseRouter floatingIslands(final HolderGetter<DensityFunction> functions, final HolderGetter<NormalNoise> noises) {
      DensityFunction slide = slideEndLike(getFunction(functions, BASE_3D_NOISE_END), 0, 256);
      return simpleRouter(DensityFunctions.add(postProcess(slide), DensityFunctions.beardifier()));
   }

   private static DensityFunction slideEnd(final DensityFunction caves) {
      return slideEndLike(caves, 0, 128);
   }

   protected static NoiseRouter end(final HolderGetter<DensityFunction> functions) {
      DensityFunction islands = getFunction(functions, END_ISLANDS);
      DensityFunction fullNoise = DensityFunctions.add(postProcess(slideEnd(getFunction(functions, SLOPED_CHEESE_END))), DensityFunctions.beardifier());
      return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), islands, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), fullNoise);
   }

   private static NoiseRouter simpleRouter(final DensityFunction fullNoise) {
      return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), fullNoise);
   }

   public static NoiseRouter none() {
      return simpleRouter(DensityFunctions.zero());
   }

   private static DensityFunction splineWithBlending(final DensityFunction spline, final DensityFunction blendingTarget) {
      DensityFunction blendedSpline = DensityFunctions.lerp(DensityFunctions.blendAlpha(), blendingTarget, spline);
      return DensityFunctions.flatCache(DensityFunctions.cache2d(blendedSpline));
   }

   private static DensityFunction noiseGradientDensity(final DensityFunction factor, final DensityFunction depthWithJaggedness) {
      DensityFunction gradientUnscaled = DensityFunctions.mul(depthWithJaggedness, factor);
      return gradientUnscaled.quarterNegative().mul(4.0F);
   }

   private static DensityFunction preliminarySurfaceLevel(final DensityFunction offset, final DensityFunction factor, final boolean amplified) {
      DensityFunction cachedFactor = DensityFunctions.cache2d(factor);
      DensityFunction cachedOffset = DensityFunctions.cache2d(offset);
      DensityFunction upperBound = DensityFunctions.remap(DensityFunctions.sub(DensityFunctions.div(DensityFunctions.constant(0.2734375F), cachedFactor), cachedOffset), 1.5F, -1.5F, -64.0F, 320.0F);
      upperBound = upperBound.clamp(-40.0F, 320.0F);
      DensityFunction density = DensityFunctions.add(slideOverworld(amplified, DensityFunctions.add(noiseGradientDensity(cachedFactor, offsetToDepth(cachedOffset)), DensityFunctions.constant(-0.703125F)).clamp(-64.0F, 64.0F)), DensityFunctions.constant(-0.390625F));
      return DensityFunctions.findTopSurface(density, upperBound, -64, NoiseSettings.OVERWORLD_NOISE_SETTINGS.getCellHeight());
   }

   private static DensityFunction yLimitedInterpolatable(final DensityFunction y, final DensityFunction whenInRange, final int minYInclusive, final int maxYInclusive, final int whenOutOfRange) {
      return DensityFunctions.interpolated(DensityFunctions.rangeChoice(y, (float)minYInclusive, (float)(maxYInclusive + 1), whenInRange, DensityFunctions.constant((float)whenOutOfRange)));
   }

   private static DensityFunction slide(final DensityFunction caves, final int minY, final int height, final int topStartY, final int topEndY, final float topTarget, final int bottomStartY, final int bottomEndY, final float bottomTarget) {
      DensityFunction topFactor = DensityFunctions.yClampedGradient(minY + height - topStartY, minY + height - topEndY, 1.0F, 0.0F);
      DensityFunction noiseValue = DensityFunctions.lerp(topFactor, topTarget, caves);
      DensityFunction bottomFactor = DensityFunctions.yClampedGradient(minY + bottomStartY, minY + bottomEndY, 0.0F, 1.0F);
      noiseValue = DensityFunctions.lerp(bottomFactor, bottomTarget, noiseValue);
      return noiseValue;
   }

   static {
      AMPLIFIED_OVERWORLD_FUNCTIONS = new OverworldFunctionSet<ResourceKey<DensityFunction>>(OVERWORLD_FUNCTIONS.temperature(), OVERWORLD_FUNCTIONS.vegetation(), OVERWORLD_FUNCTIONS.continents(), OVERWORLD_FUNCTIONS.erosion(), createKey("overworld_amplified/offset"), createKey("overworld_amplified/factor"), createKey("overworld_amplified/jaggedness"), createKey("overworld_amplified/depth"), createKey("overworld_amplified/sloped_cheese"), createKey("overworld_amplified/preliminary_surface_level"), createKey("overworld_amplified/final_density"));
      LARGE_OVERWORLD_FUNCTIONS = new OverworldFunctionSet<ResourceKey<DensityFunction>>(createKey("overworld_large_biomes/temperature"), createKey("overworld_large_biomes/vegetation"), createKey("overworld_large_biomes/continents"), createKey("overworld_large_biomes/erosion"), createKey("overworld_large_biomes/offset"), createKey("overworld_large_biomes/factor"), createKey("overworld_large_biomes/jaggedness"), createKey("overworld_large_biomes/depth"), createKey("overworld_large_biomes/sloped_cheese"), createKey("overworld_large_biomes/preliminary_surface_level"), createKey("overworld_large_biomes/final_density"));
      END_ISLANDS = createKey("end/islands");
      SLOPED_CHEESE_END = createKey("end/sloped_cheese");
      SPAGHETTI_ROUGHNESS_FUNCTION = createKey("overworld/caves/spaghetti_roughness_function");
      ENTRANCES = createKey("overworld/caves/entrances");
      NOODLE = createKey("overworld/caves/noodle");
      PILLARS = createKey("overworld/caves/pillars");
      SPAGHETTI_2D_THICKNESS_MODULATOR = createKey("overworld/caves/spaghetti_2d_thickness_modulator");
      SPAGHETTI_2D = createKey("overworld/caves/spaghetti_2d");
      ORE_VEIN_MASK = createKey("overworld/ore_vein/mask");
      ORE_VEIN_TOGGLE = createKey("overworld/ore_vein/toggle");
      ORE_VEIN_RICHNESS = createKey("overworld/ore_vein/richness");
      ORE_VEIN_COPPER_DENSITY = createKey("overworld/ore_vein/copper_density");
      ORE_VEIN_IRON_DENSITY = createKey("overworld/ore_vein/iron_density");
      ORE_VEIN_GAP = createKey("overworld/ore_vein/gap");
   }

   protected static final class QuantizedSpaghettiRarity {
      protected QuantizedSpaghettiRarity() {
         super();
      }

      public static DensityFunction wrapRarity2d(final DensityFunction input, final Holder<NormalNoise> noise) {
         return DensityFunctions.intervalSelect(input, FloatList.of(new float[]{-0.75F, -0.5F, 0.5F, 0.75F}), List.of(noiseFunctionForRarity(noise, 0.5F), noiseFunctionForRarity(noise, 0.75F), noiseFunctionForRarity(noise, 1.0F), noiseFunctionForRarity(noise, 2.0F), noiseFunctionForRarity(noise, 3.0F))).abs();
      }

      public static DensityFunction wrapRarity3d(final DensityFunction input, final Holder<NormalNoise> noise) {
         return DensityFunctions.intervalSelect(input, FloatList.of(-0.5F, 0.0F, 0.5F), List.of(noiseFunctionForRarity(noise, 0.75F), noiseFunctionForRarity(noise, 1.0F), noiseFunctionForRarity(noise, 1.5F), noiseFunctionForRarity(noise, 2.0F))).abs();
      }

      private static DensityFunction noiseFunctionForRarity(final Holder<NormalNoise> noise, final float rarity) {
         return DensityFunctions.noise(noise, 1.0 / (double)rarity, 1.0 / (double)rarity).mul(rarity);
      }
   }
}
