package net.minecraft.world.level.levelgen;

import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseRouterData {
   public static final float GLOBAL_OFFSET = -0.50375F;
   private static final float ORE_THICKNESS = 0.08F;
   private static final double VEININESS_FREQUENCY = 1.5;
   private static final double NOODLE_SPACING_AND_STRAIGHTNESS = 1.5;
   private static final double SURFACE_DENSITY_THRESHOLD = 1.5625;
   private static final double CHEESE_NOISE_TARGET = -0.703125;
   public static final int ISLAND_CHUNK_DISTANCE = 64;
   public static final long ISLAND_CHUNK_DISTANCE_SQR = 4096L;
   private static final DensityFunction BLENDING_FACTOR = DensityFunctions.constant(10.0);
   private static final DensityFunction BLENDING_JAGGEDNESS = DensityFunctions.zero();
   private static final ResourceKey<DensityFunction> ZERO = createKey("zero");
   private static final ResourceKey<DensityFunction> Y = createKey("y");
   private static final ResourceKey<DensityFunction> SHIFT_X = createKey("shift_x");
   private static final ResourceKey<DensityFunction> SHIFT_Z = createKey("shift_z");
   private static final ResourceKey<DensityFunction> BASE_3D_NOISE_OVERWORLD = createKey("overworld/base_3d_noise");
   private static final ResourceKey<DensityFunction> BASE_3D_NOISE_NETHER = createKey("nether/base_3d_noise");
   private static final ResourceKey<DensityFunction> BASE_3D_NOISE_END = createKey("end/base_3d_noise");
   public static final ResourceKey<DensityFunction> CONTINENTS = createKey("overworld/continents");
   public static final ResourceKey<DensityFunction> EROSION = createKey("overworld/erosion");
   public static final ResourceKey<DensityFunction> RIDGES = createKey("overworld/ridges");
   public static final ResourceKey<DensityFunction> RIDGES_FOLDED = createKey("overworld/ridges_folded");
   public static final ResourceKey<DensityFunction> OFFSET = createKey("overworld/offset");
   public static final ResourceKey<DensityFunction> FACTOR = createKey("overworld/factor");
   public static final ResourceKey<DensityFunction> JAGGEDNESS = createKey("overworld/jaggedness");
   public static final ResourceKey<DensityFunction> DEPTH = createKey("overworld/depth");
   private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("overworld/sloped_cheese");
   public static final ResourceKey<DensityFunction> CONTINENTS_LARGE = createKey("overworld_large_biomes/continents");
   public static final ResourceKey<DensityFunction> EROSION_LARGE = createKey("overworld_large_biomes/erosion");
   private static final ResourceKey<DensityFunction> OFFSET_LARGE = createKey("overworld_large_biomes/offset");
   private static final ResourceKey<DensityFunction> FACTOR_LARGE = createKey("overworld_large_biomes/factor");
   private static final ResourceKey<DensityFunction> JAGGEDNESS_LARGE = createKey("overworld_large_biomes/jaggedness");
   private static final ResourceKey<DensityFunction> DEPTH_LARGE = createKey("overworld_large_biomes/depth");
   private static final ResourceKey<DensityFunction> SLOPED_CHEESE_LARGE = createKey("overworld_large_biomes/sloped_cheese");
   private static final ResourceKey<DensityFunction> OFFSET_AMPLIFIED = createKey("overworld_amplified/offset");
   private static final ResourceKey<DensityFunction> FACTOR_AMPLIFIED = createKey("overworld_amplified/factor");
   private static final ResourceKey<DensityFunction> JAGGEDNESS_AMPLIFIED = createKey("overworld_amplified/jaggedness");
   private static final ResourceKey<DensityFunction> DEPTH_AMPLIFIED = createKey("overworld_amplified/depth");
   private static final ResourceKey<DensityFunction> SLOPED_CHEESE_AMPLIFIED = createKey("overworld_amplified/sloped_cheese");
   private static final ResourceKey<DensityFunction> SLOPED_CHEESE_END = createKey("end/sloped_cheese");
   private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("overworld/caves/spaghetti_roughness_function");
   private static final ResourceKey<DensityFunction> ENTRANCES = createKey("overworld/caves/entrances");
   private static final ResourceKey<DensityFunction> NOODLE = createKey("overworld/caves/noodle");
   private static final ResourceKey<DensityFunction> PILLARS = createKey("overworld/caves/pillars");
   private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = createKey("overworld/caves/spaghetti_2d_thickness_modulator");
   private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("overworld/caves/spaghetti_2d");

   public NoiseRouterData() {
      super();
   }

   private static ResourceKey<DensityFunction> createKey(String var0) {
      return ResourceKey.create(Registry.DENSITY_FUNCTION_REGISTRY, new ResourceLocation(var0));
   }

   public static Holder<? extends DensityFunction> bootstrap(Registry<DensityFunction> var0) {
      register(var0, ZERO, DensityFunctions.zero());
      int var1 = DimensionType.MIN_Y * 2;
      int var2 = DimensionType.MAX_Y * 2;
      register(var0, Y, DensityFunctions.yClampedGradient(var1, var2, (double)var1, (double)var2));
      DensityFunction var3 = registerAndWrap(var0, SHIFT_X, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftA(getNoise(Noises.SHIFT)))));
      DensityFunction var4 = registerAndWrap(var0, SHIFT_Z, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftB(getNoise(Noises.SHIFT)))));
      register(var0, BASE_3D_NOISE_OVERWORLD, BlendedNoise.createUnseeded(0.25, 0.125, 80.0, 160.0, 8.0));
      register(var0, BASE_3D_NOISE_NETHER, BlendedNoise.createUnseeded(0.25, 0.375, 80.0, 60.0, 8.0));
      register(var0, BASE_3D_NOISE_END, BlendedNoise.createUnseeded(0.25, 0.25, 80.0, 160.0, 4.0));
      Holder var5 = register(var0, CONTINENTS, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(var3, var4, 0.25, getNoise(Noises.CONTINENTALNESS))));
      Holder var6 = register(var0, EROSION, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(var3, var4, 0.25, getNoise(Noises.EROSION))));
      DensityFunction var7 = registerAndWrap(var0, RIDGES, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(var3, var4, 0.25, getNoise(Noises.RIDGE))));
      register(var0, RIDGES_FOLDED, peaksAndValleys(var7));
      DensityFunction var8 = DensityFunctions.noise(getNoise(Noises.JAGGED), 1500.0, 0.0);
      registerTerrainNoises(var0, var8, var5, var6, OFFSET, FACTOR, JAGGEDNESS, DEPTH, SLOPED_CHEESE, false);
      Holder var9 = register(var0, CONTINENTS_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(var3, var4, 0.25, getNoise(Noises.CONTINENTALNESS_LARGE))));
      Holder var10 = register(var0, EROSION_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(var3, var4, 0.25, getNoise(Noises.EROSION_LARGE))));
      registerTerrainNoises(var0, var8, var9, var10, OFFSET_LARGE, FACTOR_LARGE, JAGGEDNESS_LARGE, DEPTH_LARGE, SLOPED_CHEESE_LARGE, false);
      registerTerrainNoises(var0, var8, var5, var6, OFFSET_AMPLIFIED, FACTOR_AMPLIFIED, JAGGEDNESS_AMPLIFIED, DEPTH_AMPLIFIED, SLOPED_CHEESE_AMPLIFIED, true);
      register(var0, SLOPED_CHEESE_END, DensityFunctions.add(DensityFunctions.endIslands(0L), getFunction(var0, BASE_3D_NOISE_END)));
      register(var0, SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction());
      register(var0, SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3)));
      register(var0, SPAGHETTI_2D, spaghetti2D(var0));
      register(var0, ENTRANCES, entrances(var0));
      register(var0, NOODLE, noodle(var0));
      return register(var0, PILLARS, pillars());
   }

   private static void registerTerrainNoises(Registry<DensityFunction> var0, DensityFunction var1, Holder<DensityFunction> var2, Holder<DensityFunction> var3, ResourceKey<DensityFunction> var4, ResourceKey<DensityFunction> var5, ResourceKey<DensityFunction> var6, ResourceKey<DensityFunction> var7, ResourceKey<DensityFunction> var8, boolean var9) {
      DensityFunctions.Spline.Coordinate var10 = new DensityFunctions.Spline.Coordinate(var2);
      DensityFunctions.Spline.Coordinate var11 = new DensityFunctions.Spline.Coordinate(var3);
      DensityFunctions.Spline.Coordinate var12 = new DensityFunctions.Spline.Coordinate(var0.getHolderOrThrow(RIDGES));
      DensityFunctions.Spline.Coordinate var13 = new DensityFunctions.Spline.Coordinate(var0.getHolderOrThrow(RIDGES_FOLDED));
      DensityFunction var14 = registerAndWrap(var0, var4, splineWithBlending(DensityFunctions.add(DensityFunctions.constant(-0.5037500262260437), DensityFunctions.spline(TerrainProvider.overworldOffset(var10, var11, var13, var9))), DensityFunctions.blendOffset()));
      DensityFunction var15 = registerAndWrap(var0, var5, splineWithBlending(DensityFunctions.spline(TerrainProvider.overworldFactor(var10, var11, var12, var13, var9)), BLENDING_FACTOR));
      DensityFunction var16 = registerAndWrap(var0, var7, DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 320, 1.5, -1.5), var14));
      DensityFunction var17 = registerAndWrap(var0, var6, splineWithBlending(DensityFunctions.spline(TerrainProvider.overworldJaggedness(var10, var11, var12, var13, var9)), BLENDING_JAGGEDNESS));
      DensityFunction var18 = DensityFunctions.mul(var17, var1.halfNegative());
      DensityFunction var19 = noiseGradientDensity(var15, DensityFunctions.add(var16, var18));
      register(var0, var8, DensityFunctions.add(var19, getFunction(var0, BASE_3D_NOISE_OVERWORLD)));
   }

   private static DensityFunction registerAndWrap(Registry<DensityFunction> var0, ResourceKey<DensityFunction> var1, DensityFunction var2) {
      return new DensityFunctions.HolderHolder(BuiltinRegistries.register(var0, (ResourceKey)var1, var2));
   }

   private static Holder<DensityFunction> register(Registry<DensityFunction> var0, ResourceKey<DensityFunction> var1, DensityFunction var2) {
      return BuiltinRegistries.register(var0, (ResourceKey)var1, var2);
   }

   private static Holder<NormalNoise.NoiseParameters> getNoise(ResourceKey<NormalNoise.NoiseParameters> var0) {
      return BuiltinRegistries.NOISE.getHolderOrThrow(var0);
   }

   private static DensityFunction getFunction(Registry<DensityFunction> var0, ResourceKey<DensityFunction> var1) {
      return new DensityFunctions.HolderHolder(var0.getHolderOrThrow(var1));
   }

   private static DensityFunction peaksAndValleys(DensityFunction var0) {
      return DensityFunctions.mul(DensityFunctions.add(DensityFunctions.add(var0.abs(), DensityFunctions.constant(-0.6666666666666666)).abs(), DensityFunctions.constant(-0.3333333333333333)), DensityFunctions.constant(-3.0));
   }

   public static float peaksAndValleys(float var0) {
      return -(Math.abs(Math.abs(var0) - 0.6666667F) - 0.33333334F) * 3.0F;
   }

   private static DensityFunction spaghettiRoughnessFunction() {
      DensityFunction var0 = DensityFunctions.noise(getNoise(Noises.SPAGHETTI_ROUGHNESS));
      DensityFunction var1 = DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
      return DensityFunctions.cacheOnce(DensityFunctions.mul(var1, DensityFunctions.add(var0.abs(), DensityFunctions.constant(-0.4))));
   }

   private static DensityFunction entrances(Registry<DensityFunction> var0) {
      DensityFunction var1 = DensityFunctions.cacheOnce(DensityFunctions.noise(getNoise(Noises.SPAGHETTI_3D_RARITY), 2.0, 1.0));
      DensityFunction var2 = DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_3D_THICKNESS), -0.065, -0.088);
      DensityFunction var3 = DensityFunctions.weirdScaledSampler(var1, getNoise(Noises.SPAGHETTI_3D_1), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
      DensityFunction var4 = DensityFunctions.weirdScaledSampler(var1, getNoise(Noises.SPAGHETTI_3D_2), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
      DensityFunction var5 = DensityFunctions.add(DensityFunctions.max(var3, var4), var2).clamp(-1.0, 1.0);
      DensityFunction var6 = getFunction(var0, SPAGHETTI_ROUGHNESS_FUNCTION);
      DensityFunction var7 = DensityFunctions.noise(getNoise(Noises.CAVE_ENTRANCE), 0.75, 0.5);
      DensityFunction var8 = DensityFunctions.add(DensityFunctions.add(var7, DensityFunctions.constant(0.37)), DensityFunctions.yClampedGradient(-10, 30, 0.3, 0.0));
      return DensityFunctions.cacheOnce(DensityFunctions.min(var8, DensityFunctions.add(var6, var5)));
   }

   private static DensityFunction noodle(Registry<DensityFunction> var0) {
      DensityFunction var1 = getFunction(var0, Y);
      boolean var2 = true;
      boolean var3 = true;
      boolean var4 = true;
      DensityFunction var5 = yLimitedInterpolatable(var1, DensityFunctions.noise(getNoise(Noises.NOODLE), 1.0, 1.0), -60, 320, -1);
      DensityFunction var6 = yLimitedInterpolatable(var1, DensityFunctions.mappedNoise(getNoise(Noises.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -60, 320, 0);
      double var7 = 2.6666666666666665;
      DensityFunction var9 = yLimitedInterpolatable(var1, DensityFunctions.noise(getNoise(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
      DensityFunction var10 = yLimitedInterpolatable(var1, DensityFunctions.noise(getNoise(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
      DensityFunction var11 = DensityFunctions.mul(DensityFunctions.constant(1.5), DensityFunctions.max(var9.abs(), var10.abs()));
      return DensityFunctions.rangeChoice(var5, -1000000.0, 0.0, DensityFunctions.constant(64.0), DensityFunctions.add(var6, var11));
   }

   private static DensityFunction pillars() {
      double var0 = 25.0;
      double var2 = 0.3;
      DensityFunction var4 = DensityFunctions.noise(getNoise(Noises.PILLAR), 25.0, 0.3);
      DensityFunction var5 = DensityFunctions.mappedNoise(getNoise(Noises.PILLAR_RARENESS), 0.0, -2.0);
      DensityFunction var6 = DensityFunctions.mappedNoise(getNoise(Noises.PILLAR_THICKNESS), 0.0, 1.1);
      DensityFunction var7 = DensityFunctions.add(DensityFunctions.mul(var4, DensityFunctions.constant(2.0)), var5);
      return DensityFunctions.cacheOnce(DensityFunctions.mul(var7, var6.cube()));
   }

   private static DensityFunction spaghetti2D(Registry<DensityFunction> var0) {
      DensityFunction var1 = DensityFunctions.noise(getNoise(Noises.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
      DensityFunction var2 = DensityFunctions.weirdScaledSampler(var1, getNoise(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2);
      DensityFunction var3 = DensityFunctions.mappedNoise(getNoise(Noises.SPAGHETTI_2D_ELEVATION), 0.0, (double)Math.floorDiv(-64, 8), 8.0);
      DensityFunction var4 = getFunction(var0, SPAGHETTI_2D_THICKNESS_MODULATOR);
      DensityFunction var5 = DensityFunctions.add(var3, DensityFunctions.yClampedGradient(-64, 320, 8.0, -40.0)).abs();
      DensityFunction var6 = DensityFunctions.add(var5, var4).cube();
      double var7 = 0.083;
      DensityFunction var9 = DensityFunctions.add(var2, DensityFunctions.mul(DensityFunctions.constant(0.083), var4));
      return DensityFunctions.max(var9, var6).clamp(-1.0, 1.0);
   }

   private static DensityFunction underground(Registry<DensityFunction> var0, DensityFunction var1) {
      DensityFunction var2 = getFunction(var0, SPAGHETTI_2D);
      DensityFunction var3 = getFunction(var0, SPAGHETTI_ROUGHNESS_FUNCTION);
      DensityFunction var4 = DensityFunctions.noise(getNoise(Noises.CAVE_LAYER), 8.0);
      DensityFunction var5 = DensityFunctions.mul(DensityFunctions.constant(4.0), var4.square());
      DensityFunction var6 = DensityFunctions.noise(getNoise(Noises.CAVE_CHEESE), 0.6666666666666666);
      DensityFunction var7 = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27), var6).clamp(-1.0, 1.0), DensityFunctions.add(DensityFunctions.constant(1.5), DensityFunctions.mul(DensityFunctions.constant(-0.64), var1)).clamp(0.0, 0.5));
      DensityFunction var8 = DensityFunctions.add(var5, var7);
      DensityFunction var9 = DensityFunctions.min(DensityFunctions.min(var8, getFunction(var0, ENTRANCES)), DensityFunctions.add(var2, var3));
      DensityFunction var10 = getFunction(var0, PILLARS);
      DensityFunction var11 = DensityFunctions.rangeChoice(var10, -1000000.0, 0.03, DensityFunctions.constant(-1000000.0), var10);
      return DensityFunctions.max(var9, var11);
   }

   private static DensityFunction postProcess(DensityFunction var0) {
      DensityFunction var1 = DensityFunctions.blendDensity(var0);
      return DensityFunctions.mul(DensityFunctions.interpolated(var1), DensityFunctions.constant(0.64)).squeeze();
   }

   protected static NoiseRouter overworld(Registry<DensityFunction> var0, boolean var1, boolean var2) {
      DensityFunction var3 = DensityFunctions.noise(getNoise(Noises.AQUIFER_BARRIER), 0.5);
      DensityFunction var4 = DensityFunctions.noise(getNoise(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
      DensityFunction var5 = DensityFunctions.noise(getNoise(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
      DensityFunction var6 = DensityFunctions.noise(getNoise(Noises.AQUIFER_LAVA));
      DensityFunction var7 = getFunction(var0, SHIFT_X);
      DensityFunction var8 = getFunction(var0, SHIFT_Z);
      DensityFunction var9 = DensityFunctions.shiftedNoise2d(var7, var8, 0.25, getNoise(var1 ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE));
      DensityFunction var10 = DensityFunctions.shiftedNoise2d(var7, var8, 0.25, getNoise(var1 ? Noises.VEGETATION_LARGE : Noises.VEGETATION));
      DensityFunction var11 = getFunction(var0, var1 ? FACTOR_LARGE : (var2 ? FACTOR_AMPLIFIED : FACTOR));
      DensityFunction var12 = getFunction(var0, var1 ? DEPTH_LARGE : (var2 ? DEPTH_AMPLIFIED : DEPTH));
      DensityFunction var13 = noiseGradientDensity(DensityFunctions.cache2d(var11), var12);
      DensityFunction var14 = getFunction(var0, var1 ? SLOPED_CHEESE_LARGE : (var2 ? SLOPED_CHEESE_AMPLIFIED : SLOPED_CHEESE));
      DensityFunction var15 = DensityFunctions.min(var14, DensityFunctions.mul(DensityFunctions.constant(5.0), getFunction(var0, ENTRANCES)));
      DensityFunction var16 = DensityFunctions.rangeChoice(var14, -1000000.0, 1.5625, var15, underground(var0, var14));
      DensityFunction var17 = DensityFunctions.min(postProcess(slideOverworld(var2, var16)), getFunction(var0, NOODLE));
      DensityFunction var18 = getFunction(var0, Y);
      int var19 = Stream.of(OreVeinifier.VeinType.values()).mapToInt((var0x) -> {
         return var0x.minY;
      }).min().orElse(-DimensionType.MIN_Y * 2);
      int var20 = Stream.of(OreVeinifier.VeinType.values()).mapToInt((var0x) -> {
         return var0x.maxY;
      }).max().orElse(-DimensionType.MIN_Y * 2);
      DensityFunction var21 = yLimitedInterpolatable(var18, DensityFunctions.noise(getNoise(Noises.ORE_VEININESS), 1.5, 1.5), var19, var20, 0);
      float var22 = 4.0F;
      DensityFunction var23 = yLimitedInterpolatable(var18, DensityFunctions.noise(getNoise(Noises.ORE_VEIN_A), 4.0, 4.0), var19, var20, 0).abs();
      DensityFunction var24 = yLimitedInterpolatable(var18, DensityFunctions.noise(getNoise(Noises.ORE_VEIN_B), 4.0, 4.0), var19, var20, 0).abs();
      DensityFunction var25 = DensityFunctions.add(DensityFunctions.constant(-0.07999999821186066), DensityFunctions.max(var23, var24));
      DensityFunction var26 = DensityFunctions.noise(getNoise(Noises.ORE_GAP));
      return new NoiseRouter(var3, var4, var5, var6, var9, var10, getFunction(var0, var1 ? CONTINENTS_LARGE : CONTINENTS), getFunction(var0, var1 ? EROSION_LARGE : EROSION), var12, getFunction(var0, RIDGES), slideOverworld(var2, DensityFunctions.add(var13, DensityFunctions.constant(-0.703125)).clamp(-64.0, 64.0)), var17, var21, var25, var26);
   }

   private static NoiseRouter noNewCaves(Registry<DensityFunction> var0, DensityFunction var1) {
      DensityFunction var2 = getFunction(var0, SHIFT_X);
      DensityFunction var3 = getFunction(var0, SHIFT_Z);
      DensityFunction var4 = DensityFunctions.shiftedNoise2d(var2, var3, 0.25, getNoise(Noises.TEMPERATURE));
      DensityFunction var5 = DensityFunctions.shiftedNoise2d(var2, var3, 0.25, getNoise(Noises.VEGETATION));
      DensityFunction var6 = postProcess(var1);
      return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), var4, var5, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), var6, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
   }

   private static DensityFunction slideOverworld(boolean var0, DensityFunction var1) {
      return slide(var1, -64, 384, var0 ? 16 : 80, var0 ? 0 : 64, -0.078125, 0, 24, var0 ? 0.4 : 0.1171875);
   }

   private static DensityFunction slideNetherLike(Registry<DensityFunction> var0, int var1, int var2) {
      return slide(getFunction(var0, BASE_3D_NOISE_NETHER), var1, var2, 24, 0, 0.9375, -8, 24, 2.5);
   }

   private static DensityFunction slideEndLike(DensityFunction var0, int var1, int var2) {
      return slide(var0, var1, var2, 72, -184, -23.4375, 4, 32, -0.234375);
   }

   protected static NoiseRouter nether(Registry<DensityFunction> var0) {
      return noNewCaves(var0, slideNetherLike(var0, 0, 128));
   }

   protected static NoiseRouter caves(Registry<DensityFunction> var0) {
      return noNewCaves(var0, slideNetherLike(var0, -64, 192));
   }

   protected static NoiseRouter floatingIslands(Registry<DensityFunction> var0) {
      return noNewCaves(var0, slideEndLike(getFunction(var0, BASE_3D_NOISE_END), 0, 256));
   }

   private static DensityFunction slideEnd(DensityFunction var0) {
      return slideEndLike(var0, 0, 128);
   }

   protected static NoiseRouter end(Registry<DensityFunction> var0) {
      DensityFunction var1 = DensityFunctions.cache2d(DensityFunctions.endIslands(0L));
      DensityFunction var2 = postProcess(slideEnd(getFunction(var0, SLOPED_CHEESE_END)));
      return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), var1, DensityFunctions.zero(), DensityFunctions.zero(), slideEnd(DensityFunctions.add(var1, DensityFunctions.constant(-0.703125))), var2, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
   }

   protected static NoiseRouter none() {
      return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
   }

   private static DensityFunction splineWithBlending(DensityFunction var0, DensityFunction var1) {
      DensityFunction var2 = DensityFunctions.lerp(DensityFunctions.blendAlpha(), var1, var0);
      return DensityFunctions.flatCache(DensityFunctions.cache2d(var2));
   }

   private static DensityFunction noiseGradientDensity(DensityFunction var0, DensityFunction var1) {
      DensityFunction var2 = DensityFunctions.mul(var1, var0);
      return DensityFunctions.mul(DensityFunctions.constant(4.0), var2.quarterNegative());
   }

   private static DensityFunction yLimitedInterpolatable(DensityFunction var0, DensityFunction var1, int var2, int var3, int var4) {
      return DensityFunctions.interpolated(DensityFunctions.rangeChoice(var0, (double)var2, (double)(var3 + 1), var1, DensityFunctions.constant((double)var4)));
   }

   private static DensityFunction slide(DensityFunction var0, int var1, int var2, int var3, int var4, double var5, int var7, int var8, double var9) {
      DensityFunction var12 = DensityFunctions.yClampedGradient(var1 + var2 - var3, var1 + var2 - var4, 1.0, 0.0);
      DensityFunction var11 = DensityFunctions.lerp(var12, var5, var0);
      DensityFunction var13 = DensityFunctions.yClampedGradient(var1 + var7, var1 + var8, 0.0, 1.0);
      var11 = DensityFunctions.lerp(var13, var9, var11);
      return var11;
   }

   protected static final class QuantizedSpaghettiRarity {
      protected QuantizedSpaghettiRarity() {
         super();
      }

      protected static double getSphaghettiRarity2D(double var0) {
         if (var0 < -0.75) {
            return 0.5;
         } else if (var0 < -0.5) {
            return 0.75;
         } else if (var0 < 0.5) {
            return 1.0;
         } else {
            return var0 < 0.75 ? 2.0 : 3.0;
         }
      }

      protected static double getSpaghettiRarity3D(double var0) {
         if (var0 < -0.5) {
            return 0.75;
         } else if (var0 < 0.0) {
            return 1.0;
         } else {
            return var0 < 0.5 ? 1.5 : 2.0;
         }
      }
   }
}
