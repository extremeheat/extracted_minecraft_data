package net.minecraft.world.level.biome;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public final class OverworldBiomeBuilder {
   private static final float VALLEY_SIZE = 0.05F;
   private static final float LOW_START = 0.26666668F;
   public static final float HIGH_START = 0.4F;
   private static final float HIGH_END = 0.93333334F;
   private static final float PEAK_SIZE = 0.1F;
   public static final float PEAK_START = 0.56666666F;
   private static final float PEAK_END = 0.7666667F;
   public static final float NEAR_INLAND_START = -0.11F;
   public static final float MID_INLAND_START = 0.03F;
   public static final float FAR_INLAND_START = 0.3F;
   public static final float EROSION_INDEX_1_START = -0.78F;
   public static final float EROSION_INDEX_2_START = -0.375F;
   private static final float EROSION_DEEP_DARK_DRYNESS_THRESHOLD = -0.225F;
   private static final float DEPTH_DEEP_DARK_DRYNESS_THRESHOLD = 0.9F;
   private final OverworldBiomeBuilder.Modifier modifier;
   private final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
   private final Climate.Parameter[] temperatures = new Climate.Parameter[]{
      Climate.Parameter.span(-1.0F, -0.45F),
      Climate.Parameter.span(-0.45F, -0.15F),
      Climate.Parameter.span(-0.15F, 0.2F),
      Climate.Parameter.span(0.2F, 0.55F),
      Climate.Parameter.span(0.55F, 1.0F)
   };
   private final Climate.Parameter[] humidities = new Climate.Parameter[]{
      Climate.Parameter.span(-1.0F, -0.35F),
      Climate.Parameter.span(-0.35F, -0.1F),
      Climate.Parameter.span(-0.1F, 0.1F),
      Climate.Parameter.span(0.1F, 0.3F),
      Climate.Parameter.span(0.3F, 1.0F)
   };
   private final Climate.Parameter[] erosions = new Climate.Parameter[]{
      Climate.Parameter.span(-1.0F, -0.78F),
      Climate.Parameter.span(-0.78F, -0.375F),
      Climate.Parameter.span(-0.375F, -0.2225F),
      Climate.Parameter.span(-0.2225F, 0.05F),
      Climate.Parameter.span(0.05F, 0.45F),
      Climate.Parameter.span(0.45F, 0.55F),
      Climate.Parameter.span(0.55F, 1.0F)
   };
   private final Climate.Parameter FROZEN_RANGE = this.temperatures[0];
   private final Climate.Parameter UNFROZEN_RANGE = Climate.Parameter.span(this.temperatures[1], this.temperatures[4]);
   private final Climate.Parameter mushroomFieldsContinentalness = Climate.Parameter.span(-1.2F, -1.05F);
   private final Climate.Parameter deepOceanContinentalness = Climate.Parameter.span(-1.05F, -0.455F);
   private final Climate.Parameter oceanContinentalness = Climate.Parameter.span(-0.455F, -0.19F);
   private final Climate.Parameter coastContinentalness = Climate.Parameter.span(-0.19F, -0.11F);
   private final Climate.Parameter inlandContinentalness = Climate.Parameter.span(-0.11F, 0.55F);
   private final Climate.Parameter nearInlandContinentalness = Climate.Parameter.span(-0.11F, 0.03F);
   private final Climate.Parameter midInlandContinentalness = Climate.Parameter.span(0.03F, 0.3F);
   private final Climate.Parameter farInlandContinentalness = Climate.Parameter.span(0.3F, 1.0F);
   private final ResourceKey<Biome>[][] OCEANS = new ResourceKey[][]{
      {Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.WARM_OCEAN},
      {Biomes.FROZEN_OCEAN, Biomes.COLD_OCEAN, Biomes.OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN}
   };
   private final ResourceKey<Biome>[][] MIDDLE_BIOMES = new ResourceKey[][]{
      {Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA, Biomes.TAIGA},
      {Biomes.PLAINS, Biomes.PLAINS, Biomes.FOREST, Biomes.TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA},
      {Biomes.FLOWER_FOREST, Biomes.PLAINS, Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.DARK_FOREST},
      {Biomes.SAVANNA, Biomes.SAVANNA, Biomes.FOREST, Biomes.JUNGLE, Biomes.JUNGLE},
      {Biomes.DESERT, Biomes.DESERT, Biomes.DESERT, Biomes.DESERT, Biomes.DESERT}
   };
   private final ResourceKey<Biome>[][] MIDDLE_BIOMES_VARIANT = new ResourceKey[][]{
      {Biomes.ICE_SPIKES, null, Biomes.SNOWY_TAIGA, null, null},
      {null, null, null, null, Biomes.OLD_GROWTH_PINE_TAIGA},
      {Biomes.SUNFLOWER_PLAINS, null, null, Biomes.OLD_GROWTH_BIRCH_FOREST, null},
      {null, null, Biomes.PLAINS, Biomes.SPARSE_JUNGLE, Biomes.BAMBOO_JUNGLE},
      {null, null, null, null, null}
   };
   private final ResourceKey<Biome>[][] PLATEAU_BIOMES = new ResourceKey[][]{
      {Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_PLAINS, Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA},
      {Biomes.MEADOW, Biomes.MEADOW, Biomes.FOREST, Biomes.TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA},
      {Biomes.MEADOW, Biomes.MEADOW, Biomes.MEADOW, Biomes.MEADOW, Biomes.DARK_FOREST},
      {Biomes.SAVANNA_PLATEAU, Biomes.SAVANNA_PLATEAU, Biomes.FOREST, Biomes.FOREST, Biomes.JUNGLE},
      {Biomes.BADLANDS, Biomes.BADLANDS, Biomes.BADLANDS, Biomes.WOODED_BADLANDS, Biomes.WOODED_BADLANDS}
   };
   private final ResourceKey<Biome>[][] PLATEAU_BIOMES_VARIANT = new ResourceKey[][]{
      {Biomes.ICE_SPIKES, null, null, null, null},
      {Biomes.CHERRY_GROVE, null, Biomes.MEADOW, Biomes.MEADOW, Biomes.OLD_GROWTH_PINE_TAIGA},
      {Biomes.CHERRY_GROVE, Biomes.CHERRY_GROVE, Biomes.FOREST, Biomes.BIRCH_FOREST, null},
      {null, null, null, null, null},
      {Biomes.ERODED_BADLANDS, Biomes.ERODED_BADLANDS, null, null, null}
   };
   private final ResourceKey<Biome>[][] SHATTERED_BIOMES = new ResourceKey[][]{
      {Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST},
      {Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST},
      {Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_FOREST},
      {null, null, null, null, null},
      {null, null, null, null, null}
   };

   public OverworldBiomeBuilder() {
      this(OverworldBiomeBuilder.Modifier.NONE);
   }

   public OverworldBiomeBuilder(OverworldBiomeBuilder.Modifier var1) {
      super();
      this.modifier = var1;
   }

   public List<Climate.ParameterPoint> spawnTarget() {
      Climate.Parameter var1 = Climate.Parameter.point(0.0F);
      float var2 = 0.16F;
      return List.of(
         new Climate.ParameterPoint(
            this.FULL_RANGE,
            this.FULL_RANGE,
            Climate.Parameter.span(this.inlandContinentalness, this.FULL_RANGE),
            this.FULL_RANGE,
            var1,
            Climate.Parameter.span(-1.0F, -0.16F),
            0L
         ),
         new Climate.ParameterPoint(
            this.FULL_RANGE,
            this.FULL_RANGE,
            Climate.Parameter.span(this.inlandContinentalness, this.FULL_RANGE),
            this.FULL_RANGE,
            var1,
            Climate.Parameter.span(0.16F, 1.0F),
            0L
         )
      );
   }

   protected void addBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1) {
      if (SharedConstants.debugGenerateSquareTerrainWithoutNoise) {
         this.addDebugBiomes(var1);
      } else {
         this.addOffCoastBiomes(var1);
         this.addInlandBiomes(var1);
         this.addUndergroundBiomes(var1);
      }
   }

   private void addDebugBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1) {
      HolderLookup.Provider var2 = VanillaRegistries.createLookup();
      HolderLookup.RegistryLookup var3 = var2.lookupOrThrow(Registries.DENSITY_FUNCTION);
      DensityFunctions.Spline.Coordinate var4 = new DensityFunctions.Spline.Coordinate(var3.getOrThrow(NoiseRouterData.CONTINENTS));
      DensityFunctions.Spline.Coordinate var5 = new DensityFunctions.Spline.Coordinate(var3.getOrThrow(NoiseRouterData.EROSION));
      DensityFunctions.Spline.Coordinate var6 = new DensityFunctions.Spline.Coordinate(var3.getOrThrow(NoiseRouterData.RIDGES_FOLDED));
      var1.accept(
         Pair.of(
            Climate.parameters(this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.point(0.0F), this.FULL_RANGE, 0.01F),
            Biomes.PLAINS
         )
      );
      CubicSpline var7 = TerrainProvider.buildErosionOffsetSpline(var5, var6, -0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, ToFloatFunction.IDENTITY);
      if (var7 instanceof CubicSpline.Multipoint var8) {
         ResourceKey var9 = Biomes.DESERT;

         for(float var13 : var8.locations()) {
            var1.accept(
               Pair.of(
                  Climate.parameters(
                     this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.point(var13), Climate.Parameter.point(0.0F), this.FULL_RANGE, 0.0F
                  ),
                  var9
               )
            );
            var9 = var9 == Biomes.DESERT ? Biomes.BADLANDS : Biomes.DESERT;
         }
      }

      CubicSpline var14 = TerrainProvider.overworldOffset(var4, var5, var6, false);
      if (var14 instanceof CubicSpline.Multipoint var15) {
         for(float var19 : var15.locations()) {
            var1.accept(
               Pair.of(
                  Climate.parameters(
                     this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.point(var19), this.FULL_RANGE, Climate.Parameter.point(0.0F), this.FULL_RANGE, 0.0F
                  ),
                  Biomes.SNOWY_TAIGA
               )
            );
         }
      }
   }

   private void addOffCoastBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1) {
      this.addSurfaceBiome(
         var1, this.FULL_RANGE, this.FULL_RANGE, this.mushroomFieldsContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0F, Biomes.MUSHROOM_FIELDS
      );

      for(int var2 = 0; var2 < this.temperatures.length; ++var2) {
         Climate.Parameter var3 = this.temperatures[var2];
         this.addSurfaceBiome(var1, var3, this.FULL_RANGE, this.deepOceanContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0F, this.OCEANS[0][var2]);
         this.addSurfaceBiome(var1, var3, this.FULL_RANGE, this.oceanContinentalness, this.FULL_RANGE, this.FULL_RANGE, 0.0F, this.OCEANS[1][var2]);
      }
   }

   private void addInlandBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1) {
      this.addMidSlice(var1, Climate.Parameter.span(-1.0F, -0.93333334F));
      this.addHighSlice(var1, Climate.Parameter.span(-0.93333334F, -0.7666667F));
      this.addPeaks(var1, Climate.Parameter.span(-0.7666667F, -0.56666666F));
      this.addHighSlice(var1, Climate.Parameter.span(-0.56666666F, -0.4F));
      this.addMidSlice(var1, Climate.Parameter.span(-0.4F, -0.26666668F));
      this.addLowSlice(var1, Climate.Parameter.span(-0.26666668F, -0.05F));
      this.addValleys(var1, Climate.Parameter.span(-0.05F, 0.05F));
      this.addLowSlice(var1, Climate.Parameter.span(0.05F, 0.26666668F));
      this.addMidSlice(var1, Climate.Parameter.span(0.26666668F, 0.4F));
      this.addHighSlice(var1, Climate.Parameter.span(0.4F, 0.56666666F));
      this.addPeaks(var1, Climate.Parameter.span(0.56666666F, 0.7666667F));
      this.addHighSlice(var1, Climate.Parameter.span(0.7666667F, 0.93333334F));
      this.addMidSlice(var1, Climate.Parameter.span(0.93333334F, 1.0F));
   }

   private void addPeaks(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1, Climate.Parameter var2) {
      for(int var3 = 0; var3 < this.temperatures.length; ++var3) {
         Climate.Parameter var4 = this.temperatures[var3];

         for(int var5 = 0; var5 < this.humidities.length; ++var5) {
            Climate.Parameter var6 = this.humidities[var5];
            ResourceKey var7 = this.pickMiddleBiome(var3, var5, var2);
            ResourceKey var8 = this.pickMiddleBiomeOrBadlandsIfHot(var3, var5, var2);
            ResourceKey var9 = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(var3, var5, var2);
            ResourceKey var10 = this.pickPlateauBiome(var3, var5, var2);
            ResourceKey var11 = this.pickShatteredBiome(var3, var5, var2);
            ResourceKey var12 = this.maybePickWindsweptSavannaBiome(var3, var5, var2, var11);
            ResourceKey var13 = this.pickPeakBiome(var3, var5, var2);
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[0], var2, 0.0F, var13
            );
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[1], var2, 0.0F, var9
            );
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[1], var2, 0.0F, var13
            );
            this.addSurfaceBiome(
               var1,
               var4,
               var6,
               Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness),
               Climate.Parameter.span(this.erosions[2], this.erosions[3]),
               var2,
               0.0F,
               var7
            );
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[2], var2, 0.0F, var10
            );
            this.addSurfaceBiome(var1, var4, var6, this.midInlandContinentalness, this.erosions[3], var2, 0.0F, var8);
            this.addSurfaceBiome(var1, var4, var6, this.farInlandContinentalness, this.erosions[3], var2, 0.0F, var10);
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], var2, 0.0F, var7
            );
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[5], var2, 0.0F, var12
            );
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], var2, 0.0F, var11
            );
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[6], var2, 0.0F, var7
            );
         }
      }
   }

   private void addHighSlice(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1, Climate.Parameter var2) {
      for(int var3 = 0; var3 < this.temperatures.length; ++var3) {
         Climate.Parameter var4 = this.temperatures[var3];

         for(int var5 = 0; var5 < this.humidities.length; ++var5) {
            Climate.Parameter var6 = this.humidities[var5];
            ResourceKey var7 = this.pickMiddleBiome(var3, var5, var2);
            ResourceKey var8 = this.pickMiddleBiomeOrBadlandsIfHot(var3, var5, var2);
            ResourceKey var9 = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(var3, var5, var2);
            ResourceKey var10 = this.pickPlateauBiome(var3, var5, var2);
            ResourceKey var11 = this.pickShatteredBiome(var3, var5, var2);
            ResourceKey var12 = this.maybePickWindsweptSavannaBiome(var3, var5, var2, var7);
            ResourceKey var13 = this.pickSlopeBiome(var3, var5, var2);
            ResourceKey var14 = this.pickPeakBiome(var3, var5, var2);
            this.addSurfaceBiome(var1, var4, var6, this.coastContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), var2, 0.0F, var7);
            this.addSurfaceBiome(var1, var4, var6, this.nearInlandContinentalness, this.erosions[0], var2, 0.0F, var13);
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[0], var2, 0.0F, var14
            );
            this.addSurfaceBiome(var1, var4, var6, this.nearInlandContinentalness, this.erosions[1], var2, 0.0F, var9);
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[1], var2, 0.0F, var13
            );
            this.addSurfaceBiome(
               var1,
               var4,
               var6,
               Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness),
               Climate.Parameter.span(this.erosions[2], this.erosions[3]),
               var2,
               0.0F,
               var7
            );
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[2], var2, 0.0F, var10
            );
            this.addSurfaceBiome(var1, var4, var6, this.midInlandContinentalness, this.erosions[3], var2, 0.0F, var8);
            this.addSurfaceBiome(var1, var4, var6, this.farInlandContinentalness, this.erosions[3], var2, 0.0F, var10);
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], var2, 0.0F, var7
            );
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[5], var2, 0.0F, var12
            );
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], var2, 0.0F, var11
            );
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[6], var2, 0.0F, var7
            );
         }
      }
   }

   private void addMidSlice(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1, Climate.Parameter var2) {
      this.addSurfaceBiome(
         var1,
         this.FULL_RANGE,
         this.FULL_RANGE,
         this.coastContinentalness,
         Climate.Parameter.span(this.erosions[0], this.erosions[2]),
         var2,
         0.0F,
         Biomes.STONY_SHORE
      );
      this.addSurfaceBiome(
         var1,
         Climate.Parameter.span(this.temperatures[1], this.temperatures[2]),
         this.FULL_RANGE,
         Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness),
         this.erosions[6],
         var2,
         0.0F,
         Biomes.SWAMP
      );
      this.addSurfaceBiome(
         var1,
         Climate.Parameter.span(this.temperatures[3], this.temperatures[4]),
         this.FULL_RANGE,
         Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness),
         this.erosions[6],
         var2,
         0.0F,
         Biomes.MANGROVE_SWAMP
      );

      for(int var3 = 0; var3 < this.temperatures.length; ++var3) {
         Climate.Parameter var4 = this.temperatures[var3];

         for(int var5 = 0; var5 < this.humidities.length; ++var5) {
            Climate.Parameter var6 = this.humidities[var5];
            ResourceKey var7 = this.pickMiddleBiome(var3, var5, var2);
            ResourceKey var8 = this.pickMiddleBiomeOrBadlandsIfHot(var3, var5, var2);
            ResourceKey var9 = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(var3, var5, var2);
            ResourceKey var10 = this.pickShatteredBiome(var3, var5, var2);
            ResourceKey var11 = this.pickPlateauBiome(var3, var5, var2);
            ResourceKey var12 = this.pickBeachBiome(var3, var5);
            ResourceKey var13 = this.maybePickWindsweptSavannaBiome(var3, var5, var2, var7);
            ResourceKey var14 = this.pickShatteredCoastBiome(var3, var5, var2);
            ResourceKey var15 = this.pickSlopeBiome(var3, var5, var2);
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[0], var2, 0.0F, var15
            );
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.nearInlandContinentalness, this.midInlandContinentalness), this.erosions[1], var2, 0.0F, var9
            );
            this.addSurfaceBiome(var1, var4, var6, this.farInlandContinentalness, this.erosions[1], var2, 0.0F, var3 == 0 ? var15 : var11);
            this.addSurfaceBiome(var1, var4, var6, this.nearInlandContinentalness, this.erosions[2], var2, 0.0F, var7);
            this.addSurfaceBiome(var1, var4, var6, this.midInlandContinentalness, this.erosions[2], var2, 0.0F, var8);
            this.addSurfaceBiome(var1, var4, var6, this.farInlandContinentalness, this.erosions[2], var2, 0.0F, var11);
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.coastContinentalness, this.nearInlandContinentalness), this.erosions[3], var2, 0.0F, var7
            );
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[3], var2, 0.0F, var8
            );
            if (var2.max() < 0L) {
               this.addSurfaceBiome(var1, var4, var6, this.coastContinentalness, this.erosions[4], var2, 0.0F, var12);
               this.addSurfaceBiome(
                  var1, var4, var6, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[4], var2, 0.0F, var7
               );
            } else {
               this.addSurfaceBiome(
                  var1, var4, var6, Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness), this.erosions[4], var2, 0.0F, var7
               );
            }

            this.addSurfaceBiome(var1, var4, var6, this.coastContinentalness, this.erosions[5], var2, 0.0F, var14);
            this.addSurfaceBiome(var1, var4, var6, this.nearInlandContinentalness, this.erosions[5], var2, 0.0F, var13);
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], var2, 0.0F, var10
            );
            if (var2.max() < 0L) {
               this.addSurfaceBiome(var1, var4, var6, this.coastContinentalness, this.erosions[6], var2, 0.0F, var12);
            } else {
               this.addSurfaceBiome(var1, var4, var6, this.coastContinentalness, this.erosions[6], var2, 0.0F, var7);
            }

            if (var3 == 0) {
               this.addSurfaceBiome(
                  var1, var4, var6, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], var2, 0.0F, var7
               );
            }
         }
      }
   }

   private void addLowSlice(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1, Climate.Parameter var2) {
      this.addSurfaceBiome(
         var1,
         this.FULL_RANGE,
         this.FULL_RANGE,
         this.coastContinentalness,
         Climate.Parameter.span(this.erosions[0], this.erosions[2]),
         var2,
         0.0F,
         Biomes.STONY_SHORE
      );
      this.addSurfaceBiome(
         var1,
         Climate.Parameter.span(this.temperatures[1], this.temperatures[2]),
         this.FULL_RANGE,
         Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness),
         this.erosions[6],
         var2,
         0.0F,
         Biomes.SWAMP
      );
      this.addSurfaceBiome(
         var1,
         Climate.Parameter.span(this.temperatures[3], this.temperatures[4]),
         this.FULL_RANGE,
         Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness),
         this.erosions[6],
         var2,
         0.0F,
         Biomes.MANGROVE_SWAMP
      );

      for(int var3 = 0; var3 < this.temperatures.length; ++var3) {
         Climate.Parameter var4 = this.temperatures[var3];

         for(int var5 = 0; var5 < this.humidities.length; ++var5) {
            Climate.Parameter var6 = this.humidities[var5];
            ResourceKey var7 = this.pickMiddleBiome(var3, var5, var2);
            ResourceKey var8 = this.pickMiddleBiomeOrBadlandsIfHot(var3, var5, var2);
            ResourceKey var9 = this.pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(var3, var5, var2);
            ResourceKey var10 = this.pickBeachBiome(var3, var5);
            ResourceKey var11 = this.maybePickWindsweptSavannaBiome(var3, var5, var2, var7);
            ResourceKey var12 = this.pickShatteredCoastBiome(var3, var5, var2);
            this.addSurfaceBiome(
               var1, var4, var6, this.nearInlandContinentalness, Climate.Parameter.span(this.erosions[0], this.erosions[1]), var2, 0.0F, var8
            );
            this.addSurfaceBiome(
               var1,
               var4,
               var6,
               Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness),
               Climate.Parameter.span(this.erosions[0], this.erosions[1]),
               var2,
               0.0F,
               var9
            );
            this.addSurfaceBiome(
               var1, var4, var6, this.nearInlandContinentalness, Climate.Parameter.span(this.erosions[2], this.erosions[3]), var2, 0.0F, var7
            );
            this.addSurfaceBiome(
               var1,
               var4,
               var6,
               Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness),
               Climate.Parameter.span(this.erosions[2], this.erosions[3]),
               var2,
               0.0F,
               var8
            );
            this.addSurfaceBiome(var1, var4, var6, this.coastContinentalness, Climate.Parameter.span(this.erosions[3], this.erosions[4]), var2, 0.0F, var10);
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[4], var2, 0.0F, var7
            );
            this.addSurfaceBiome(var1, var4, var6, this.coastContinentalness, this.erosions[5], var2, 0.0F, var12);
            this.addSurfaceBiome(var1, var4, var6, this.nearInlandContinentalness, this.erosions[5], var2, 0.0F, var11);
            this.addSurfaceBiome(
               var1, var4, var6, Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness), this.erosions[5], var2, 0.0F, var7
            );
            this.addSurfaceBiome(var1, var4, var6, this.coastContinentalness, this.erosions[6], var2, 0.0F, var10);
            if (var3 == 0) {
               this.addSurfaceBiome(
                  var1, var4, var6, Climate.Parameter.span(this.nearInlandContinentalness, this.farInlandContinentalness), this.erosions[6], var2, 0.0F, var7
               );
            }
         }
      }
   }

   private void addValleys(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1, Climate.Parameter var2) {
      this.addSurfaceBiome(
         var1,
         this.FROZEN_RANGE,
         this.FULL_RANGE,
         this.coastContinentalness,
         Climate.Parameter.span(this.erosions[0], this.erosions[1]),
         var2,
         0.0F,
         var2.max() < 0L ? Biomes.STONY_SHORE : Biomes.FROZEN_RIVER
      );
      this.addSurfaceBiome(
         var1,
         this.UNFROZEN_RANGE,
         this.FULL_RANGE,
         this.coastContinentalness,
         Climate.Parameter.span(this.erosions[0], this.erosions[1]),
         var2,
         0.0F,
         var2.max() < 0L ? Biomes.STONY_SHORE : Biomes.RIVER
      );
      this.addSurfaceBiome(
         var1,
         this.FROZEN_RANGE,
         this.FULL_RANGE,
         this.nearInlandContinentalness,
         Climate.Parameter.span(this.erosions[0], this.erosions[1]),
         var2,
         0.0F,
         Biomes.FROZEN_RIVER
      );
      this.addSurfaceBiome(
         var1,
         this.UNFROZEN_RANGE,
         this.FULL_RANGE,
         this.nearInlandContinentalness,
         Climate.Parameter.span(this.erosions[0], this.erosions[1]),
         var2,
         0.0F,
         Biomes.RIVER
      );
      this.addSurfaceBiome(
         var1,
         this.FROZEN_RANGE,
         this.FULL_RANGE,
         Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness),
         Climate.Parameter.span(this.erosions[2], this.erosions[5]),
         var2,
         0.0F,
         Biomes.FROZEN_RIVER
      );
      this.addSurfaceBiome(
         var1,
         this.UNFROZEN_RANGE,
         this.FULL_RANGE,
         Climate.Parameter.span(this.coastContinentalness, this.farInlandContinentalness),
         Climate.Parameter.span(this.erosions[2], this.erosions[5]),
         var2,
         0.0F,
         Biomes.RIVER
      );
      this.addSurfaceBiome(var1, this.FROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, this.erosions[6], var2, 0.0F, Biomes.FROZEN_RIVER);
      this.addSurfaceBiome(var1, this.UNFROZEN_RANGE, this.FULL_RANGE, this.coastContinentalness, this.erosions[6], var2, 0.0F, Biomes.RIVER);
      this.addSurfaceBiome(
         var1,
         Climate.Parameter.span(this.temperatures[1], this.temperatures[2]),
         this.FULL_RANGE,
         Climate.Parameter.span(this.inlandContinentalness, this.farInlandContinentalness),
         this.erosions[6],
         var2,
         0.0F,
         Biomes.SWAMP
      );
      this.addSurfaceBiome(
         var1,
         Climate.Parameter.span(this.temperatures[3], this.temperatures[4]),
         this.FULL_RANGE,
         Climate.Parameter.span(this.inlandContinentalness, this.farInlandContinentalness),
         this.erosions[6],
         var2,
         0.0F,
         Biomes.MANGROVE_SWAMP
      );
      this.addSurfaceBiome(
         var1,
         this.FROZEN_RANGE,
         this.FULL_RANGE,
         Climate.Parameter.span(this.inlandContinentalness, this.farInlandContinentalness),
         this.erosions[6],
         var2,
         0.0F,
         Biomes.FROZEN_RIVER
      );

      for(int var3 = 0; var3 < this.temperatures.length; ++var3) {
         Climate.Parameter var4 = this.temperatures[var3];

         for(int var5 = 0; var5 < this.humidities.length; ++var5) {
            Climate.Parameter var6 = this.humidities[var5];
            ResourceKey var7 = this.pickMiddleBiomeOrBadlandsIfHot(var3, var5, var2);
            this.addSurfaceBiome(
               var1,
               var4,
               var6,
               Climate.Parameter.span(this.midInlandContinentalness, this.farInlandContinentalness),
               Climate.Parameter.span(this.erosions[0], this.erosions[1]),
               var2,
               0.0F,
               var7
            );
         }
      }
   }

   private void addUndergroundBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1) {
      this.addUndergroundBiome(
         var1, this.FULL_RANGE, this.FULL_RANGE, Climate.Parameter.span(0.8F, 1.0F), this.FULL_RANGE, this.FULL_RANGE, 0.0F, Biomes.DRIPSTONE_CAVES
      );
      this.addUndergroundBiome(
         var1, this.FULL_RANGE, Climate.Parameter.span(0.7F, 1.0F), this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, 0.0F, Biomes.LUSH_CAVES
      );
      this.addBottomBiome(
         var1,
         this.FULL_RANGE,
         this.FULL_RANGE,
         this.FULL_RANGE,
         Climate.Parameter.span(this.erosions[0], this.erosions[1]),
         this.FULL_RANGE,
         0.0F,
         Biomes.DEEP_DARK
      );
   }

   private ResourceKey<Biome> pickMiddleBiome(int var1, int var2, Climate.Parameter var3) {
      if (var3.max() < 0L) {
         return this.MIDDLE_BIOMES[var1][var2];
      } else {
         ResourceKey var4 = this.MIDDLE_BIOMES_VARIANT[var1][var2];
         return var4 == null ? this.MIDDLE_BIOMES[var1][var2] : var4;
      }
   }

   private ResourceKey<Biome> pickMiddleBiomeOrBadlandsIfHot(int var1, int var2, Climate.Parameter var3) {
      return var1 == 4 ? this.pickBadlandsBiome(var2, var3) : this.pickMiddleBiome(var1, var2, var3);
   }

   private ResourceKey<Biome> pickMiddleBiomeOrBadlandsIfHotOrSlopeIfCold(int var1, int var2, Climate.Parameter var3) {
      return var1 == 0 ? this.pickSlopeBiome(var1, var2, var3) : this.pickMiddleBiomeOrBadlandsIfHot(var1, var2, var3);
   }

   private ResourceKey<Biome> maybePickWindsweptSavannaBiome(int var1, int var2, Climate.Parameter var3, ResourceKey<Biome> var4) {
      return var1 > 1 && var2 < 4 && var3.max() >= 0L ? Biomes.WINDSWEPT_SAVANNA : var4;
   }

   private ResourceKey<Biome> pickShatteredCoastBiome(int var1, int var2, Climate.Parameter var3) {
      ResourceKey var4 = var3.max() >= 0L ? this.pickMiddleBiome(var1, var2, var3) : this.pickBeachBiome(var1, var2);
      return this.maybePickWindsweptSavannaBiome(var1, var2, var3, var4);
   }

   private ResourceKey<Biome> pickBeachBiome(int var1, int var2) {
      if (var1 == 0) {
         return Biomes.SNOWY_BEACH;
      } else {
         return var1 == 4 ? Biomes.DESERT : Biomes.BEACH;
      }
   }

   private ResourceKey<Biome> pickBadlandsBiome(int var1, Climate.Parameter var2) {
      if (var1 < 2) {
         return var2.max() < 0L ? Biomes.BADLANDS : Biomes.ERODED_BADLANDS;
      } else {
         return var1 < 3 ? Biomes.BADLANDS : Biomes.WOODED_BADLANDS;
      }
   }

   private ResourceKey<Biome> pickPlateauBiome(int var1, int var2, Climate.Parameter var3) {
      ResourceKey var4 = this.PLATEAU_BIOMES_VARIANT[var1][var2];
      return var3.max() >= 0L && var4 != null && (var4 != Biomes.CHERRY_GROVE || this.modifier == OverworldBiomeBuilder.Modifier.UPDATE_1_20)
         ? var4
         : this.PLATEAU_BIOMES[var1][var2];
   }

   private ResourceKey<Biome> pickPeakBiome(int var1, int var2, Climate.Parameter var3) {
      if (var1 <= 2) {
         return var3.max() < 0L ? Biomes.JAGGED_PEAKS : Biomes.FROZEN_PEAKS;
      } else {
         return var1 == 3 ? Biomes.STONY_PEAKS : this.pickBadlandsBiome(var2, var3);
      }
   }

   private ResourceKey<Biome> pickSlopeBiome(int var1, int var2, Climate.Parameter var3) {
      if (var1 >= 3) {
         return this.pickPlateauBiome(var1, var2, var3);
      } else {
         return var2 <= 1 ? Biomes.SNOWY_SLOPES : Biomes.GROVE;
      }
   }

   private ResourceKey<Biome> pickShatteredBiome(int var1, int var2, Climate.Parameter var3) {
      ResourceKey var4 = this.SHATTERED_BIOMES[var1][var2];
      return var4 == null ? this.pickMiddleBiome(var1, var2, var3) : var4;
   }

   private void addSurfaceBiome(
      Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1,
      Climate.Parameter var2,
      Climate.Parameter var3,
      Climate.Parameter var4,
      Climate.Parameter var5,
      Climate.Parameter var6,
      float var7,
      ResourceKey<Biome> var8
   ) {
      var1.accept(Pair.of(Climate.parameters(var2, var3, var4, var5, Climate.Parameter.point(0.0F), var6, var7), var8));
      var1.accept(Pair.of(Climate.parameters(var2, var3, var4, var5, Climate.Parameter.point(1.0F), var6, var7), var8));
   }

   private void addUndergroundBiome(
      Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1,
      Climate.Parameter var2,
      Climate.Parameter var3,
      Climate.Parameter var4,
      Climate.Parameter var5,
      Climate.Parameter var6,
      float var7,
      ResourceKey<Biome> var8
   ) {
      var1.accept(Pair.of(Climate.parameters(var2, var3, var4, var5, Climate.Parameter.span(0.2F, 0.9F), var6, var7), var8));
   }

   private void addBottomBiome(
      Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1,
      Climate.Parameter var2,
      Climate.Parameter var3,
      Climate.Parameter var4,
      Climate.Parameter var5,
      Climate.Parameter var6,
      float var7,
      ResourceKey<Biome> var8
   ) {
      var1.accept(Pair.of(Climate.parameters(var2, var3, var4, var5, Climate.Parameter.point(1.1F), var6, var7), var8));
   }

   public static boolean isDeepDarkRegion(DensityFunction var0, DensityFunction var1, DensityFunction.FunctionContext var2) {
      return var0.compute(var2) < -0.22499999403953552 && var1.compute(var2) > 0.8999999761581421;
   }

   public static String getDebugStringForPeaksAndValleys(double var0) {
      if (var0 < (double)NoiseRouterData.peaksAndValleys(0.05F)) {
         return "Valley";
      } else if (var0 < (double)NoiseRouterData.peaksAndValleys(0.26666668F)) {
         return "Low";
      } else if (var0 < (double)NoiseRouterData.peaksAndValleys(0.4F)) {
         return "Mid";
      } else {
         return var0 < (double)NoiseRouterData.peaksAndValleys(0.56666666F) ? "High" : "Peak";
      }
   }

   public String getDebugStringForContinentalness(double var1) {
      double var3 = (double)Climate.quantizeCoord((float)var1);
      if (var3 < (double)this.mushroomFieldsContinentalness.max()) {
         return "Mushroom fields";
      } else if (var3 < (double)this.deepOceanContinentalness.max()) {
         return "Deep ocean";
      } else if (var3 < (double)this.oceanContinentalness.max()) {
         return "Ocean";
      } else if (var3 < (double)this.coastContinentalness.max()) {
         return "Coast";
      } else if (var3 < (double)this.nearInlandContinentalness.max()) {
         return "Near inland";
      } else {
         return var3 < (double)this.midInlandContinentalness.max() ? "Mid inland" : "Far inland";
      }
   }

   public String getDebugStringForErosion(double var1) {
      return getDebugStringForNoiseValue(var1, this.erosions);
   }

   public String getDebugStringForTemperature(double var1) {
      return getDebugStringForNoiseValue(var1, this.temperatures);
   }

   public String getDebugStringForHumidity(double var1) {
      return getDebugStringForNoiseValue(var1, this.humidities);
   }

   private static String getDebugStringForNoiseValue(double var0, Climate.Parameter[] var2) {
      double var3 = (double)Climate.quantizeCoord((float)var0);

      for(int var5 = 0; var5 < var2.length; ++var5) {
         if (var3 < (double)var2[var5].max()) {
            return var5 + "";
         }
      }

      return "?";
   }

   @VisibleForDebug
   public Climate.Parameter[] getTemperatureThresholds() {
      return this.temperatures;
   }

   @VisibleForDebug
   public Climate.Parameter[] getHumidityThresholds() {
      return this.humidities;
   }

   @VisibleForDebug
   public Climate.Parameter[] getErosionThresholds() {
      return this.erosions;
   }

   @VisibleForDebug
   public Climate.Parameter[] getContinentalnessThresholds() {
      return new Climate.Parameter[]{
         this.mushroomFieldsContinentalness,
         this.deepOceanContinentalness,
         this.oceanContinentalness,
         this.coastContinentalness,
         this.nearInlandContinentalness,
         this.midInlandContinentalness,
         this.farInlandContinentalness
      };
   }

   @VisibleForDebug
   public Climate.Parameter[] getPeaksAndValleysThresholds() {
      return new Climate.Parameter[]{
         Climate.Parameter.span(-2.0F, NoiseRouterData.peaksAndValleys(0.05F)),
         Climate.Parameter.span(NoiseRouterData.peaksAndValleys(0.05F), NoiseRouterData.peaksAndValleys(0.26666668F)),
         Climate.Parameter.span(NoiseRouterData.peaksAndValleys(0.26666668F), NoiseRouterData.peaksAndValleys(0.4F)),
         Climate.Parameter.span(NoiseRouterData.peaksAndValleys(0.4F), NoiseRouterData.peaksAndValleys(0.56666666F)),
         Climate.Parameter.span(NoiseRouterData.peaksAndValleys(0.56666666F), 2.0F)
      };
   }

   @VisibleForDebug
   public Climate.Parameter[] getWeirdnessThresholds() {
      return new Climate.Parameter[]{Climate.Parameter.span(-2.0F, 0.0F), Climate.Parameter.span(0.0F, 2.0F)};
   }

   public static enum Modifier {
      NONE,
      UPDATE_1_20;

      private Modifier() {
      }
   }
}
