package net.minecraft.world.level.levelgen;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NoiseUtils;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class NoiseSampler implements Climate.Sampler {
   private static final float ORE_VEIN_RARITY = 1.0F;
   private static final float ORE_THICKNESS = 0.08F;
   private static final float VEININESS_THRESHOLD = 0.4F;
   private static final double VEININESS_FREQUENCY = 1.5D;
   private static final int EDGE_ROUNDOFF_BEGIN = 20;
   private static final double MAX_EDGE_ROUNDOFF = 0.2D;
   private static final float VEIN_SOLIDNESS = 0.7F;
   private static final float MIN_RICHNESS = 0.1F;
   private static final float MAX_RICHNESS = 0.3F;
   private static final float MAX_RICHNESS_THRESHOLD = 0.6F;
   private static final float CHANCE_OF_RAW_ORE_BLOCK = 0.02F;
   private static final float SKIP_ORE_IF_GAP_NOISE_IS_BELOW = -0.3F;
   private static final double NOODLE_SPACING_AND_STRAIGHTNESS = 1.5D;
   private final NoiseSettings noiseSettings;
   private final boolean isNoiseCavesEnabled;
   private final NoiseChunk.InterpolatableNoise baseNoise;
   private final BlendedNoise blendedNoise;
   @Nullable
   private final SimplexNoise islandNoise;
   private final NormalNoise jaggedNoise;
   private final NormalNoise barrierNoise;
   private final NormalNoise fluidLevelFloodednessNoise;
   private final NormalNoise fluidLevelSpreadNoise;
   private final NormalNoise lavaNoise;
   private final NormalNoise layerNoiseSource;
   private final NormalNoise pillarNoiseSource;
   private final NormalNoise pillarRarenessModulator;
   private final NormalNoise pillarThicknessModulator;
   private final NormalNoise spaghetti2DNoiseSource;
   private final NormalNoise spaghetti2DElevationModulator;
   private final NormalNoise spaghetti2DRarityModulator;
   private final NormalNoise spaghetti2DThicknessModulator;
   private final NormalNoise spaghetti3DNoiseSource1;
   private final NormalNoise spaghetti3DNoiseSource2;
   private final NormalNoise spaghetti3DRarityModulator;
   private final NormalNoise spaghetti3DThicknessModulator;
   private final NormalNoise spaghettiRoughnessNoise;
   private final NormalNoise spaghettiRoughnessModulator;
   private final NormalNoise bigEntranceNoiseSource;
   private final NormalNoise cheeseNoiseSource;
   private final NormalNoise temperatureNoise;
   private final NormalNoise humidityNoise;
   private final NormalNoise continentalnessNoise;
   private final NormalNoise erosionNoise;
   private final NormalNoise weirdnessNoise;
   private final NormalNoise offsetNoise;
   private final NormalNoise gapNoise;
   private final NoiseChunk.InterpolatableNoise veininess;
   private final NoiseChunk.InterpolatableNoise veinA;
   private final NoiseChunk.InterpolatableNoise veinB;
   private final NoiseChunk.InterpolatableNoise noodleToggle;
   private final NoiseChunk.InterpolatableNoise noodleThickness;
   private final NoiseChunk.InterpolatableNoise noodleRidgeA;
   private final NoiseChunk.InterpolatableNoise noodleRidgeB;
   private final PositionalRandomFactory aquiferPositionalRandomFactory;
   private final PositionalRandomFactory oreVeinsPositionalRandomFactory;
   private final PositionalRandomFactory depthBasedLayerPositionalRandomFactory;
   private final List<Climate.ParameterPoint> spawnTarget = (new OverworldBiomeBuilder()).spawnTarget();
   private final boolean amplified;

   public NoiseSampler(NoiseSettings var1, boolean var2, long var3, Registry<NormalNoise.NoiseParameters> var5, WorldgenRandom.Algorithm var6) {
      super();
      this.noiseSettings = var1;
      this.isNoiseCavesEnabled = var2;
      this.baseNoise = (var1x) -> {
         return var1x.createNoiseInterpolator((var2, var3, var4) -> {
            return this.calculateBaseNoise(var2, var3, var4, var1x.noiseData(QuartPos.fromBlock(var2), QuartPos.fromBlock(var4)).terrainInfo(), var1x.getBlender());
         });
      };
      if (var1.islandNoiseOverride()) {
         RandomSource var7 = var6.newInstance(var3);
         var7.consumeCount(17292);
         this.islandNoise = new SimplexNoise(var7);
      } else {
         this.islandNoise = null;
      }

      this.amplified = var1.isAmplified();
      int var17 = var1.minY();
      int var8 = Stream.of(NoiseSampler.VeinType.values()).mapToInt((var0) -> {
         return var0.minY;
      }).min().orElse(var17);
      int var9 = Stream.of(NoiseSampler.VeinType.values()).mapToInt((var0) -> {
         return var0.maxY;
      }).max().orElse(var17);
      float var10 = 4.0F;
      double var11 = 2.6666666666666665D;
      int var13 = var17 + 4;
      int var14 = var17 + var1.height();
      boolean var15 = var1.largeBiomes();
      PositionalRandomFactory var16 = var6.newInstance(var3).forkPositional();
      if (var6 != WorldgenRandom.Algorithm.LEGACY) {
         this.blendedNoise = new BlendedNoise(var16.fromHashOf(new ResourceLocation("terrain")), var1.noiseSamplingSettings(), var1.getCellWidth(), var1.getCellHeight());
         this.temperatureNoise = Noises.instantiate(var5, var16, var15 ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE);
         this.humidityNoise = Noises.instantiate(var5, var16, var15 ? Noises.VEGETATION_LARGE : Noises.VEGETATION);
         this.offsetNoise = Noises.instantiate(var5, var16, Noises.SHIFT);
      } else {
         this.blendedNoise = new BlendedNoise(var6.newInstance(var3), var1.noiseSamplingSettings(), var1.getCellWidth(), var1.getCellHeight());
         this.temperatureNoise = NormalNoise.createLegacyNetherBiome(var6.newInstance(var3), new NormalNoise.NoiseParameters(-7, 1.0D, new double[]{1.0D}));
         this.humidityNoise = NormalNoise.createLegacyNetherBiome(var6.newInstance(var3 + 1L), new NormalNoise.NoiseParameters(-7, 1.0D, new double[]{1.0D}));
         this.offsetNoise = NormalNoise.create(var16.fromHashOf(Noises.SHIFT.location()), new NormalNoise.NoiseParameters(0, 0.0D, new double[0]));
      }

      this.aquiferPositionalRandomFactory = var16.fromHashOf(new ResourceLocation("aquifer")).forkPositional();
      this.oreVeinsPositionalRandomFactory = var16.fromHashOf(new ResourceLocation("ore")).forkPositional();
      this.depthBasedLayerPositionalRandomFactory = var16.fromHashOf(new ResourceLocation("depth_based_layer")).forkPositional();
      this.barrierNoise = Noises.instantiate(var5, var16, Noises.AQUIFER_BARRIER);
      this.fluidLevelFloodednessNoise = Noises.instantiate(var5, var16, Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS);
      this.lavaNoise = Noises.instantiate(var5, var16, Noises.AQUIFER_LAVA);
      this.fluidLevelSpreadNoise = Noises.instantiate(var5, var16, Noises.AQUIFER_FLUID_LEVEL_SPREAD);
      this.pillarNoiseSource = Noises.instantiate(var5, var16, Noises.PILLAR);
      this.pillarRarenessModulator = Noises.instantiate(var5, var16, Noises.PILLAR_RARENESS);
      this.pillarThicknessModulator = Noises.instantiate(var5, var16, Noises.PILLAR_THICKNESS);
      this.spaghetti2DNoiseSource = Noises.instantiate(var5, var16, Noises.SPAGHETTI_2D);
      this.spaghetti2DElevationModulator = Noises.instantiate(var5, var16, Noises.SPAGHETTI_2D_ELEVATION);
      this.spaghetti2DRarityModulator = Noises.instantiate(var5, var16, Noises.SPAGHETTI_2D_MODULATOR);
      this.spaghetti2DThicknessModulator = Noises.instantiate(var5, var16, Noises.SPAGHETTI_2D_THICKNESS);
      this.spaghetti3DNoiseSource1 = Noises.instantiate(var5, var16, Noises.SPAGHETTI_3D_1);
      this.spaghetti3DNoiseSource2 = Noises.instantiate(var5, var16, Noises.SPAGHETTI_3D_2);
      this.spaghetti3DRarityModulator = Noises.instantiate(var5, var16, Noises.SPAGHETTI_3D_RARITY);
      this.spaghetti3DThicknessModulator = Noises.instantiate(var5, var16, Noises.SPAGHETTI_3D_THICKNESS);
      this.spaghettiRoughnessNoise = Noises.instantiate(var5, var16, Noises.SPAGHETTI_ROUGHNESS);
      this.spaghettiRoughnessModulator = Noises.instantiate(var5, var16, Noises.SPAGHETTI_ROUGHNESS_MODULATOR);
      this.bigEntranceNoiseSource = Noises.instantiate(var5, var16, Noises.CAVE_ENTRANCE);
      this.layerNoiseSource = Noises.instantiate(var5, var16, Noises.CAVE_LAYER);
      this.cheeseNoiseSource = Noises.instantiate(var5, var16, Noises.CAVE_CHEESE);
      this.continentalnessNoise = Noises.instantiate(var5, var16, var15 ? Noises.CONTINENTALNESS_LARGE : Noises.CONTINENTALNESS);
      this.erosionNoise = Noises.instantiate(var5, var16, var15 ? Noises.EROSION_LARGE : Noises.EROSION);
      this.weirdnessNoise = Noises.instantiate(var5, var16, Noises.RIDGE);
      this.veininess = yLimitedInterpolatableNoise(Noises.instantiate(var5, var16, Noises.ORE_VEININESS), var8, var9, 0, 1.5D);
      this.veinA = yLimitedInterpolatableNoise(Noises.instantiate(var5, var16, Noises.ORE_VEIN_A), var8, var9, 0, 4.0D);
      this.veinB = yLimitedInterpolatableNoise(Noises.instantiate(var5, var16, Noises.ORE_VEIN_B), var8, var9, 0, 4.0D);
      this.gapNoise = Noises.instantiate(var5, var16, Noises.ORE_GAP);
      this.noodleToggle = yLimitedInterpolatableNoise(Noises.instantiate(var5, var16, Noises.NOODLE), var13, var14, -1, 1.0D);
      this.noodleThickness = yLimitedInterpolatableNoise(Noises.instantiate(var5, var16, Noises.NOODLE_THICKNESS), var13, var14, 0, 1.0D);
      this.noodleRidgeA = yLimitedInterpolatableNoise(Noises.instantiate(var5, var16, Noises.NOODLE_RIDGE_A), var13, var14, 0, 2.6666666666666665D);
      this.noodleRidgeB = yLimitedInterpolatableNoise(Noises.instantiate(var5, var16, Noises.NOODLE_RIDGE_B), var13, var14, 0, 2.6666666666666665D);
      this.jaggedNoise = Noises.instantiate(var5, var16, Noises.JAGGED);
   }

   private static NoiseChunk.InterpolatableNoise yLimitedInterpolatableNoise(NormalNoise var0, int var1, int var2, int var3, double var4) {
      NoiseChunk.NoiseFiller var6 = (var6x, var7, var8) -> {
         return var7 <= var2 && var7 >= var1 ? var0.getValue((double)var6x * var4, (double)var7 * var4, (double)var8 * var4) : (double)var3;
      };
      return (var1x) -> {
         return var1x.createNoiseInterpolator(var6);
      };
   }

   private double calculateBaseNoise(int var1, int var2, int var3, TerrainInfo var4, Blender var5) {
      double var6 = this.blendedNoise.calculateNoise(var1, var2, var3);
      boolean var8 = !this.isNoiseCavesEnabled;
      return this.calculateBaseNoise(var1, var2, var3, var4, var6, var8, true, var5);
   }

   private double calculateBaseNoise(int var1, int var2, int var3, TerrainInfo var4, double var5, boolean var7, boolean var8, Blender var9) {
      double var10;
      double var12;
      double var14;
      if (this.islandNoise != null) {
         var10 = ((double)TheEndBiomeSource.getHeightValue(this.islandNoise, var1 / 8, var3 / 8) - 8.0D) / 128.0D;
      } else {
         var12 = var8 ? this.sampleJaggedNoise(var4.jaggedness(), (double)var1, (double)var3) : 0.0D;
         var14 = (this.computeBaseDensity(var2, var4) + var12) * var4.factor();
         var10 = var14 * (double)(var14 > 0.0D ? 4 : 1);
      }

      var12 = var10 + var5;
      var14 = 1.5625D;
      double var16;
      double var18;
      double var20;
      double var22;
      if (!var7 && !(var12 < -64.0D)) {
         var22 = var12 - 1.5625D;
         boolean var24 = var22 < 0.0D;
         double var25 = this.getBigEntrances(var1, var2, var3);
         double var27 = this.spaghettiRoughness(var1, var2, var3);
         double var29 = this.getSpaghetti3D(var1, var2, var3);
         double var31 = Math.min(var25, var29 + var27);
         if (var24) {
            var16 = var12;
            var18 = var31 * 5.0D;
            var20 = -64.0D;
         } else {
            double var33 = this.getLayerizedCaverns(var1, var2, var3);
            double var35;
            if (var33 > 64.0D) {
               var16 = 64.0D;
            } else {
               var35 = this.cheeseNoiseSource.getValue((double)var1, (double)var2 / 1.5D, (double)var3);
               double var37 = Mth.clamp(var35 + 0.27D, -1.0D, 1.0D);
               double var39 = var22 * 1.28D;
               double var41 = var37 + Mth.clampedLerp(0.5D, 0.0D, var39);
               var16 = var41 + var33;
            }

            var35 = this.getSpaghetti2D(var1, var2, var3);
            var18 = Math.min(var31, var35 + var27);
            var20 = this.getPillars(var1, var2, var3);
         }
      } else {
         var16 = var12;
         var18 = 64.0D;
         var20 = -64.0D;
      }

      var22 = Math.max(Math.min(var16, var18), var20);
      var22 = this.applySlide(var22, var2 / this.noiseSettings.getCellHeight());
      var22 = var9.blendDensity(var1, var2, var3, var22);
      var22 = Mth.clamp(var22, -64.0D, 64.0D);
      return var22;
   }

   private double sampleJaggedNoise(double var1, double var3, double var5) {
      if (var1 == 0.0D) {
         return 0.0D;
      } else {
         float var7 = 1500.0F;
         double var8 = this.jaggedNoise.getValue(var3 * 1500.0D, 0.0D, var5 * 1500.0D);
         return var8 > 0.0D ? var1 * var8 : var1 / 2.0D * var8;
      }
   }

   private double computeBaseDensity(int var1, TerrainInfo var2) {
      double var3 = 1.0D - (double)var1 / 128.0D;
      return var3 + var2.offset();
   }

   private double applySlide(double var1, int var3) {
      int var4 = var3 - this.noiseSettings.getMinCellY();
      var1 = this.noiseSettings.topSlideSettings().applySlide(var1, this.noiseSettings.getCellCountY() - var4);
      var1 = this.noiseSettings.bottomSlideSettings().applySlide(var1, var4);
      return var1;
   }

   protected NoiseChunk.BlockStateFiller makeBaseNoiseFiller(NoiseChunk var1, NoiseChunk.NoiseFiller var2, boolean var3) {
      NoiseChunk.Sampler var4 = this.baseNoise.instantiate(var1);
      NoiseChunk.Sampler var5 = var3 ? this.noodleToggle.instantiate(var1) : () -> {
         return -1.0D;
      };
      NoiseChunk.Sampler var6 = var3 ? this.noodleThickness.instantiate(var1) : () -> {
         return 0.0D;
      };
      NoiseChunk.Sampler var7 = var3 ? this.noodleRidgeA.instantiate(var1) : () -> {
         return 0.0D;
      };
      NoiseChunk.Sampler var8 = var3 ? this.noodleRidgeB.instantiate(var1) : () -> {
         return 0.0D;
      };
      return (var7x, var8x, var9) -> {
         double var10 = var4.sample();
         double var12 = Mth.clamp(var10 * 0.64D, -1.0D, 1.0D);
         var12 = var12 / 2.0D - var12 * var12 * var12 / 24.0D;
         if (var5.sample() >= 0.0D) {
            double var14 = 0.05D;
            double var16 = 0.1D;
            double var18 = Mth.clampedMap(var6.sample(), -1.0D, 1.0D, 0.05D, 0.1D);
            double var20 = Math.abs(1.5D * var7.sample()) - var18;
            double var22 = Math.abs(1.5D * var8.sample()) - var18;
            var12 = Math.min(var12, Math.max(var20, var22));
         }

         var12 += var2.calculateNoise(var7x, var8x, var9);
         return var1.aquifer().computeSubstance(var7x, var8x, var9, var10, var12);
      };
   }

   protected NoiseChunk.BlockStateFiller makeOreVeinifier(NoiseChunk var1, boolean var2) {
      if (!var2) {
         return (var0, var1x, var2x) -> {
            return null;
         };
      } else {
         NoiseChunk.Sampler var3 = this.veininess.instantiate(var1);
         NoiseChunk.Sampler var4 = this.veinA.instantiate(var1);
         NoiseChunk.Sampler var5 = this.veinB.instantiate(var1);
         Object var6 = null;
         return (var5x, var6x, var7) -> {
            RandomSource var8 = this.oreVeinsPositionalRandomFactory.method_6(var5x, var6x, var7);
            double var9 = var3.sample();
            NoiseSampler.VeinType var11 = this.getVeinType(var9, var6x);
            if (var11 == null) {
               return var6;
            } else if (var8.nextFloat() > 0.7F) {
               return var6;
            } else if (this.isVein(var4.sample(), var5.sample())) {
               double var12 = Mth.clampedMap(Math.abs(var9), 0.4000000059604645D, 0.6000000238418579D, 0.10000000149011612D, 0.30000001192092896D);
               if ((double)var8.nextFloat() < var12 && this.gapNoise.getValue((double)var5x, (double)var6x, (double)var7) > -0.30000001192092896D) {
                  return var8.nextFloat() < 0.02F ? var11.rawOreBlock : var11.ore;
               } else {
                  return var11.filler;
               }
            } else {
               return var6;
            }
         };
      }
   }

   protected int getPreliminarySurfaceLevel(int var1, int var2, TerrainInfo var3) {
      for(int var4 = this.noiseSettings.getMinCellY() + this.noiseSettings.getCellCountY(); var4 >= this.noiseSettings.getMinCellY(); --var4) {
         int var5 = var4 * this.noiseSettings.getCellHeight();
         double var6 = -0.703125D;
         double var8 = this.calculateBaseNoise(var1, var5, var2, var3, -0.703125D, true, false, Blender.empty());
         if (var8 > 0.390625D) {
            return var5;
         }
      }

      return 2147483647;
   }

   protected Aquifer createAquifer(NoiseChunk var1, int var2, int var3, int var4, int var5, Aquifer.FluidPicker var6, boolean var7) {
      if (!var7) {
         return Aquifer.createDisabled(var6);
      } else {
         int var8 = SectionPos.blockToSectionCoord(var2);
         int var9 = SectionPos.blockToSectionCoord(var3);
         return Aquifer.create(var1, new ChunkPos(var8, var9), this.barrierNoise, this.fluidLevelFloodednessNoise, this.fluidLevelSpreadNoise, this.lavaNoise, this.aquiferPositionalRandomFactory, var4 * this.noiseSettings.getCellHeight(), var5 * this.noiseSettings.getCellHeight(), var6);
      }
   }

   @VisibleForDebug
   public NoiseSampler.FlatNoiseData noiseData(int var1, int var2, Blender var3) {
      double var4 = (double)var1 + this.getOffset(var1, 0, var2);
      double var6 = (double)var2 + this.getOffset(var2, var1, 0);
      double var8 = this.getContinentalness(var4, 0.0D, var6);
      double var10 = this.getWeirdness(var4, 0.0D, var6);
      double var12 = this.getErosion(var4, 0.0D, var6);
      TerrainInfo var14 = this.terrainInfo(QuartPos.toBlock(var1), QuartPos.toBlock(var2), (float)var8, (float)var10, (float)var12, var3);
      return new NoiseSampler.FlatNoiseData(var4, var6, var8, var10, var12, var14);
   }

   public Climate.TargetPoint sample(int var1, int var2, int var3) {
      return this.target(var1, var2, var3, this.noiseData(var1, var3, Blender.empty()));
   }

   @VisibleForDebug
   public Climate.TargetPoint target(int var1, int var2, int var3, NoiseSampler.FlatNoiseData var4) {
      double var5 = var4.shiftedX();
      double var7 = (double)var2 + this.getOffset(var2, var3, var1);
      double var9 = var4.shiftedZ();
      double var11 = this.computeBaseDensity(QuartPos.toBlock(var2), var4.terrainInfo());
      return Climate.target((float)this.getTemperature(var5, var7, var9), (float)this.getHumidity(var5, var7, var9), (float)var4.continentalness(), (float)var4.erosion(), (float)var11, (float)var4.weirdness());
   }

   public TerrainInfo terrainInfo(int var1, int var2, float var3, float var4, float var5, Blender var6) {
      TerrainShaper var7 = this.noiseSettings.terrainShaper();
      TerrainShaper.Point var8 = var7.makePoint(var3, var5, var4);
      float var9 = var7.offset(var8);
      float var10 = var7.factor(var8);
      float var11 = var7.jaggedness(var8);
      TerrainInfo var12 = new TerrainInfo((double)var9, (double)var10, (double)var11);
      return var6.blendOffsetAndFactor(var1, var2, var12);
   }

   public BlockPos findSpawnPosition() {
      return Climate.findSpawnPosition(this.spawnTarget, this);
   }

   @VisibleForDebug
   public double getOffset(int var1, int var2, int var3) {
      return this.offsetNoise.getValue((double)var1, (double)var2, (double)var3) * 4.0D;
   }

   private double getTemperature(double var1, double var3, double var5) {
      return this.temperatureNoise.getValue(var1, 0.0D, var5);
   }

   private double getHumidity(double var1, double var3, double var5) {
      return this.humidityNoise.getValue(var1, 0.0D, var5);
   }

   @VisibleForDebug
   public double getContinentalness(double var1, double var3, double var5) {
      double var7;
      if (SharedConstants.debugGenerateSquareTerrainWithoutNoise) {
         if (SharedConstants.debugVoidTerrain(new ChunkPos(QuartPos.toSection(Mth.floor(var1)), QuartPos.toSection(Mth.floor(var5))))) {
            return -1.0D;
         } else {
            var7 = Mth.frac(var1 / 2048.0D) * 2.0D - 1.0D;
            return var7 * var7 * (double)(var7 < 0.0D ? -1 : 1);
         }
      } else if (SharedConstants.debugGenerateStripedTerrainWithoutNoise) {
         var7 = var1 * 0.005D;
         return Math.sin(var7 + 0.5D * Math.sin(var7));
      } else {
         return this.continentalnessNoise.getValue(var1, var3, var5);
      }
   }

   @VisibleForDebug
   public double getErosion(double var1, double var3, double var5) {
      double var7;
      if (SharedConstants.debugGenerateSquareTerrainWithoutNoise) {
         if (SharedConstants.debugVoidTerrain(new ChunkPos(QuartPos.toSection(Mth.floor(var1)), QuartPos.toSection(Mth.floor(var5))))) {
            return -1.0D;
         } else {
            var7 = Mth.frac(var5 / 256.0D) * 2.0D - 1.0D;
            return var7 * var7 * (double)(var7 < 0.0D ? -1 : 1);
         }
      } else if (SharedConstants.debugGenerateStripedTerrainWithoutNoise) {
         var7 = var5 * 0.005D;
         return Math.sin(var7 + 0.5D * Math.sin(var7));
      } else {
         return this.erosionNoise.getValue(var1, var3, var5);
      }
   }

   @VisibleForDebug
   public double getWeirdness(double var1, double var3, double var5) {
      return this.weirdnessNoise.getValue(var1, var3, var5);
   }

   private double getBigEntrances(int var1, int var2, int var3) {
      double var4 = 0.75D;
      double var6 = 0.5D;
      double var8 = 0.37D;
      double var10 = this.bigEntranceNoiseSource.getValue((double)var1 * 0.75D, (double)var2 * 0.5D, (double)var3 * 0.75D) + 0.37D;
      boolean var12 = true;
      double var13 = (double)(var2 - -10) / 40.0D;
      double var15 = 0.3D;
      return var10 + Mth.clampedLerp(0.3D, 0.0D, var13);
   }

   private double getPillars(int var1, int var2, int var3) {
      double var4 = 0.0D;
      double var6 = 2.0D;
      double var8 = NoiseUtils.sampleNoiseAndMapToRange(this.pillarRarenessModulator, (double)var1, (double)var2, (double)var3, 0.0D, 2.0D);
      double var10 = 0.0D;
      double var12 = 1.1D;
      double var14 = NoiseUtils.sampleNoiseAndMapToRange(this.pillarThicknessModulator, (double)var1, (double)var2, (double)var3, 0.0D, 1.1D);
      var14 = Math.pow(var14, 3.0D);
      double var16 = 25.0D;
      double var18 = 0.3D;
      double var20 = this.pillarNoiseSource.getValue((double)var1 * 25.0D, (double)var2 * 0.3D, (double)var3 * 25.0D);
      var20 = var14 * (var20 * 2.0D - var8);
      return var20 > 0.03D ? var20 : -1.0D / 0.0;
   }

   private double getLayerizedCaverns(int var1, int var2, int var3) {
      double var4 = this.layerNoiseSource.getValue((double)var1, (double)(var2 * 8), (double)var3);
      return Mth.square(var4) * 4.0D;
   }

   private double getSpaghetti3D(int var1, int var2, int var3) {
      double var4 = this.spaghetti3DRarityModulator.getValue((double)(var1 * 2), (double)var2, (double)(var3 * 2));
      double var6 = NoiseSampler.QuantizedSpaghettiRarity.getSpaghettiRarity3D(var4);
      double var8 = 0.065D;
      double var10 = 0.088D;
      double var12 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghetti3DThicknessModulator, (double)var1, (double)var2, (double)var3, 0.065D, 0.088D);
      double var14 = sampleWithRarity(this.spaghetti3DNoiseSource1, (double)var1, (double)var2, (double)var3, var6);
      double var16 = Math.abs(var6 * var14) - var12;
      double var18 = sampleWithRarity(this.spaghetti3DNoiseSource2, (double)var1, (double)var2, (double)var3, var6);
      double var20 = Math.abs(var6 * var18) - var12;
      return clampToUnit(Math.max(var16, var20));
   }

   private double getSpaghetti2D(int var1, int var2, int var3) {
      double var4 = this.spaghetti2DRarityModulator.getValue((double)(var1 * 2), (double)var2, (double)(var3 * 2));
      double var6 = NoiseSampler.QuantizedSpaghettiRarity.getSphaghettiRarity2D(var4);
      double var8 = 0.6D;
      double var10 = 1.3D;
      double var12 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghetti2DThicknessModulator, (double)(var1 * 2), (double)var2, (double)(var3 * 2), 0.6D, 1.3D);
      double var14 = sampleWithRarity(this.spaghetti2DNoiseSource, (double)var1, (double)var2, (double)var3, var6);
      double var16 = 0.083D;
      double var18 = Math.abs(var6 * var14) - 0.083D * var12;
      int var20 = this.noiseSettings.getMinCellY();
      boolean var21 = true;
      double var22 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghetti2DElevationModulator, (double)var1, 0.0D, (double)var3, (double)var20, 8.0D);
      double var24 = Math.abs(var22 - (double)var2 / 8.0D) - 1.0D * var12;
      var24 = var24 * var24 * var24;
      return clampToUnit(Math.max(var24, var18));
   }

   private double spaghettiRoughness(int var1, int var2, int var3) {
      double var4 = NoiseUtils.sampleNoiseAndMapToRange(this.spaghettiRoughnessModulator, (double)var1, (double)var2, (double)var3, 0.0D, 0.1D);
      return (0.4D - Math.abs(this.spaghettiRoughnessNoise.getValue((double)var1, (double)var2, (double)var3))) * var4;
   }

   public PositionalRandomFactory getDepthBasedLayerPositionalRandom() {
      return this.depthBasedLayerPositionalRandomFactory;
   }

   private static double clampToUnit(double var0) {
      return Mth.clamp(var0, -1.0D, 1.0D);
   }

   private static double sampleWithRarity(NormalNoise var0, double var1, double var3, double var5, double var7) {
      return var0.getValue(var1 / var7, var3 / var7, var5 / var7);
   }

   private boolean isVein(double var1, double var3) {
      double var5 = Math.abs(1.0D * var1) - 0.07999999821186066D;
      double var7 = Math.abs(1.0D * var3) - 0.07999999821186066D;
      return Math.max(var5, var7) < 0.0D;
   }

   @Nullable
   private NoiseSampler.VeinType getVeinType(double var1, int var3) {
      NoiseSampler.VeinType var4 = var1 > 0.0D ? NoiseSampler.VeinType.COPPER : NoiseSampler.VeinType.IRON;
      int var5 = var4.maxY - var3;
      int var6 = var3 - var4.minY;
      if (var6 >= 0 && var5 >= 0) {
         int var7 = Math.min(var5, var6);
         double var8 = Mth.clampedMap((double)var7, 0.0D, 20.0D, -0.2D, 0.0D);
         return Math.abs(var1) + var8 < 0.4000000059604645D ? null : var4;
      } else {
         return null;
      }
   }

   private static enum VeinType {
      COPPER(Blocks.COPPER_ORE.defaultBlockState(), Blocks.RAW_COPPER_BLOCK.defaultBlockState(), Blocks.GRANITE.defaultBlockState(), 0, 50),
      IRON(Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(), Blocks.RAW_IRON_BLOCK.defaultBlockState(), Blocks.TUFF.defaultBlockState(), -60, -8);

      final BlockState ore;
      final BlockState rawOreBlock;
      final BlockState filler;
      final int minY;
      final int maxY;

      private VeinType(BlockState var3, BlockState var4, BlockState var5, int var6, int var7) {
         this.ore = var3;
         this.rawOreBlock = var4;
         this.filler = var5;
         this.minY = var6;
         this.maxY = var7;
      }

      // $FF: synthetic method
      private static NoiseSampler.VeinType[] $values() {
         return new NoiseSampler.VeinType[]{COPPER, IRON};
      }
   }

   public static record FlatNoiseData(double a, double b, double c, double d, double e, TerrainInfo f) {
      private final double shiftedX;
      private final double shiftedZ;
      private final double continentalness;
      private final double weirdness;
      private final double erosion;
      private final TerrainInfo terrainInfo;

      public FlatNoiseData(double var1, double var3, double var5, double var7, double var9, TerrainInfo var11) {
         super();
         this.shiftedX = var1;
         this.shiftedZ = var3;
         this.continentalness = var5;
         this.weirdness = var7;
         this.erosion = var9;
         this.terrainInfo = var11;
      }

      public double shiftedX() {
         return this.shiftedX;
      }

      public double shiftedZ() {
         return this.shiftedZ;
      }

      public double continentalness() {
         return this.continentalness;
      }

      public double weirdness() {
         return this.weirdness;
      }

      public double erosion() {
         return this.erosion;
      }

      public TerrainInfo terrainInfo() {
         return this.terrainInfo;
      }
   }

   private static final class QuantizedSpaghettiRarity {
      private QuantizedSpaghettiRarity() {
         super();
      }

      static double getSphaghettiRarity2D(double var0) {
         if (var0 < -0.75D) {
            return 0.5D;
         } else if (var0 < -0.5D) {
            return 0.75D;
         } else if (var0 < 0.5D) {
            return 1.0D;
         } else {
            return var0 < 0.75D ? 2.0D : 3.0D;
         }
      }

      static double getSpaghettiRarity3D(double var0) {
         if (var0 < -0.5D) {
            return 0.75D;
         } else if (var0 < 0.0D) {
            return 1.0D;
         } else {
            return var0 < 0.5D ? 1.5D : 2.0D;
         }
      }
   }
}
