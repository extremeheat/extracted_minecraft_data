package net.minecraft.world.level.levelgen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import org.apache.commons.lang3.mutable.MutableObject;

public final class NoiseBasedChunkGenerator extends ChunkGenerator {
   public static final MapCodec<NoiseBasedChunkGenerator> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((var0x) -> {
         return var0x.biomeSource;
      }), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((var0x) -> {
         return var0x.settings;
      })).apply(var0, var0.stable(NoiseBasedChunkGenerator::new));
   });
   private static final BlockState AIR;
   private final Holder<NoiseGeneratorSettings> settings;
   private final Supplier<Aquifer.FluidPicker> globalFluidPicker;

   public NoiseBasedChunkGenerator(BiomeSource var1, Holder<NoiseGeneratorSettings> var2) {
      super(var1);
      this.settings = var2;
      this.globalFluidPicker = Suppliers.memoize(() -> {
         return createFluidPicker((NoiseGeneratorSettings)var2.value());
      });
   }

   private static Aquifer.FluidPicker createFluidPicker(NoiseGeneratorSettings var0) {
      Aquifer.FluidStatus var1 = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
      int var2 = var0.seaLevel();
      Aquifer.FluidStatus var3 = new Aquifer.FluidStatus(var2, var0.defaultFluid());
      Aquifer.FluidStatus var4 = new Aquifer.FluidStatus(DimensionType.MIN_Y * 2, Blocks.AIR.defaultBlockState());
      return (var4x, var5, var6) -> {
         return var5 < Math.min(-54, var2) ? var1 : var3;
      };
   }

   public CompletableFuture<ChunkAccess> createBiomes(RandomState var1, Blender var2, StructureManager var3, ChunkAccess var4) {
      return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
         this.doCreateBiomes(var2, var1, var3, var4);
         return var4;
      }), Util.backgroundExecutor());
   }

   private void doCreateBiomes(Blender var1, RandomState var2, StructureManager var3, ChunkAccess var4) {
      NoiseChunk var5 = var4.getOrCreateNoiseChunk((var4x) -> {
         return this.createNoiseChunk(var4x, var3, var1, var2);
      });
      BiomeResolver var6 = BelowZeroRetrogen.getBiomeResolver(var1.getBiomeResolver(this.biomeSource), var4);
      var4.fillBiomesFromNoise(var6, var5.cachedClimateSampler(var2.router(), ((NoiseGeneratorSettings)this.settings.value()).spawnTarget()));
   }

   private NoiseChunk createNoiseChunk(ChunkAccess var1, StructureManager var2, Blender var3, RandomState var4) {
      return NoiseChunk.forChunk(var1, var4, Beardifier.forStructuresInChunk(var2, var1.getPos()), (NoiseGeneratorSettings)this.settings.value(), (Aquifer.FluidPicker)this.globalFluidPicker.get(), var3);
   }

   protected MapCodec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public Holder<NoiseGeneratorSettings> generatorSettings() {
      return this.settings;
   }

   public boolean stable(ResourceKey<NoiseGeneratorSettings> var1) {
      return this.settings.is(var1);
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4, RandomState var5) {
      return this.iterateNoiseColumn(var4, var5, var1, var2, (MutableObject)null, var3.isOpaque()).orElse(var4.getMinBuildHeight());
   }

   public NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3, RandomState var4) {
      MutableObject var5 = new MutableObject();
      this.iterateNoiseColumn(var3, var4, var1, var2, var5, (Predicate)null);
      return (NoiseColumn)var5.getValue();
   }

   public void addDebugScreenInfo(List<String> var1, RandomState var2, BlockPos var3) {
      DecimalFormat var4 = new DecimalFormat("0.000");
      NoiseRouter var5 = var2.router();
      DensityFunction.SinglePointContext var6 = new DensityFunction.SinglePointContext(var3.getX(), var3.getY(), var3.getZ());
      double var7 = var5.ridges().compute(var6);
      String var10001 = var4.format(var5.temperature().compute(var6));
      var1.add("NoiseRouter T: " + var10001 + " V: " + var4.format(var5.vegetation().compute(var6)) + " C: " + var4.format(var5.continents().compute(var6)) + " E: " + var4.format(var5.erosion().compute(var6)) + " D: " + var4.format(var5.depth().compute(var6)) + " W: " + var4.format(var7) + " PV: " + var4.format((double)NoiseRouterData.peaksAndValleys((float)var7)) + " AS: " + var4.format(var5.initialDensityWithoutJaggedness().compute(var6)) + " N: " + var4.format(var5.finalDensity().compute(var6)));
   }

   private OptionalInt iterateNoiseColumn(LevelHeightAccessor var1, RandomState var2, int var3, int var4, @Nullable MutableObject<NoiseColumn> var5, @Nullable Predicate<BlockState> var6) {
      NoiseSettings var7 = ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().clampToHeightAccessor(var1);
      int var8 = var7.getCellHeight();
      int var9 = var7.minY();
      int var10 = Mth.floorDiv(var9, var8);
      int var11 = Mth.floorDiv(var7.height(), var8);
      if (var11 <= 0) {
         return OptionalInt.empty();
      } else {
         BlockState[] var12;
         if (var5 == null) {
            var12 = null;
         } else {
            var12 = new BlockState[var7.height()];
            var5.setValue(new NoiseColumn(var9, var12));
         }

         int var13 = var7.getCellWidth();
         int var14 = Math.floorDiv(var3, var13);
         int var15 = Math.floorDiv(var4, var13);
         int var16 = Math.floorMod(var3, var13);
         int var17 = Math.floorMod(var4, var13);
         int var18 = var14 * var13;
         int var19 = var15 * var13;
         double var20 = (double)var16 / (double)var13;
         double var22 = (double)var17 / (double)var13;
         NoiseChunk var24 = new NoiseChunk(1, var2, var18, var19, var7, DensityFunctions.BeardifierMarker.INSTANCE, (NoiseGeneratorSettings)this.settings.value(), (Aquifer.FluidPicker)this.globalFluidPicker.get(), Blender.empty());
         var24.initializeForFirstCellX();
         var24.advanceCellX(0);

         for(int var25 = var11 - 1; var25 >= 0; --var25) {
            var24.selectCellYZ(var25, 0);

            for(int var26 = var8 - 1; var26 >= 0; --var26) {
               int var27 = (var10 + var25) * var8 + var26;
               double var28 = (double)var26 / (double)var8;
               var24.updateForY(var27, var28);
               var24.updateForX(var3, var20);
               var24.updateForZ(var4, var22);
               BlockState var30 = var24.getInterpolatedState();
               BlockState var31 = var30 == null ? ((NoiseGeneratorSettings)this.settings.value()).defaultBlock() : var30;
               if (var12 != null) {
                  int var32 = var25 * var8 + var26;
                  var12[var32] = var31;
               }

               if (var6 != null && var6.test(var31)) {
                  var24.stopInterpolation();
                  return OptionalInt.of(var27 + 1);
               }
            }
         }

         var24.stopInterpolation();
         return OptionalInt.empty();
      }
   }

   public void buildSurface(WorldGenRegion var1, StructureManager var2, RandomState var3, ChunkAccess var4) {
      if (!SharedConstants.debugVoidTerrain(var4.getPos())) {
         WorldGenerationContext var5 = new WorldGenerationContext(this, var1);
         this.buildSurface(var4, var5, var3, var2, var1.getBiomeManager(), var1.registryAccess().registryOrThrow(Registries.BIOME), Blender.of(var1));
      }
   }

   @VisibleForTesting
   public void buildSurface(ChunkAccess var1, WorldGenerationContext var2, RandomState var3, StructureManager var4, BiomeManager var5, Registry<Biome> var6, Blender var7) {
      NoiseChunk var8 = var1.getOrCreateNoiseChunk((var4x) -> {
         return this.createNoiseChunk(var4x, var4, var7, var3);
      });
      NoiseGeneratorSettings var9 = (NoiseGeneratorSettings)this.settings.value();
      var3.surfaceSystem().buildSurface(var3, var5, var6, var9.useLegacyRandomSource(), var2, var1, var8, var9.surfaceRule());
   }

   public void applyCarvers(WorldGenRegion var1, long var2, RandomState var4, BiomeManager var5, StructureManager var6, ChunkAccess var7, GenerationStep.Carving var8) {
      BiomeManager var9 = var5.withDifferentSource((var2x, var3, var4x) -> {
         return this.biomeSource.getNoiseBiome(var2x, var3, var4x, var4.sampler());
      });
      WorldgenRandom var10 = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
      boolean var11 = true;
      ChunkPos var12 = var7.getPos();
      NoiseChunk var13 = var7.getOrCreateNoiseChunk((var4x) -> {
         return this.createNoiseChunk(var4x, var6, Blender.of(var1), var4);
      });
      Aquifer var14 = var13.aquifer();
      CarvingContext var15 = new CarvingContext(this, var1.registryAccess(), var7.getHeightAccessorForGeneration(), var13, var4, ((NoiseGeneratorSettings)this.settings.value()).surfaceRule());
      CarvingMask var16 = ((ProtoChunk)var7).getOrCreateCarvingMask(var8);

      for(int var17 = -8; var17 <= 8; ++var17) {
         for(int var18 = -8; var18 <= 8; ++var18) {
            ChunkPos var19 = new ChunkPos(var12.x + var17, var12.z + var18);
            ChunkAccess var20 = var1.getChunk(var19.x, var19.z);
            BiomeGenerationSettings var21 = var20.carverBiome(() -> {
               return this.getBiomeGenerationSettings(this.biomeSource.getNoiseBiome(QuartPos.fromBlock(var19.getMinBlockX()), 0, QuartPos.fromBlock(var19.getMinBlockZ()), var4.sampler()));
            });
            Iterable var22 = var21.getCarvers(var8);
            int var23 = 0;

            for(Iterator var24 = var22.iterator(); var24.hasNext(); ++var23) {
               Holder var25 = (Holder)var24.next();
               ConfiguredWorldCarver var26 = (ConfiguredWorldCarver)var25.value();
               var10.setLargeFeatureSeed(var2 + (long)var23, var19.x, var19.z);
               if (var26.isStartChunk(var10)) {
                  Objects.requireNonNull(var9);
                  var26.carve(var15, var7, var9::getBiome, var10, var14, var19, var16);
               }
            }
         }
      }

   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Blender var1, RandomState var2, StructureManager var3, ChunkAccess var4) {
      NoiseSettings var5 = ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().clampToHeightAccessor(var4.getHeightAccessorForGeneration());
      int var6 = var5.minY();
      int var7 = Mth.floorDiv(var6, var5.getCellHeight());
      int var8 = Mth.floorDiv(var5.height(), var5.getCellHeight());
      return var8 <= 0 ? CompletableFuture.completedFuture(var4) : CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("wgen_fill_noise", () -> {
         int var9 = var4.getSectionIndex(var8 * var5.getCellHeight() - 1 + var6);
         int var10 = var4.getSectionIndex(var6);
         HashSet var11 = Sets.newHashSet();

         for(int var12 = var9; var12 >= var10; --var12) {
            LevelChunkSection var13 = var4.getSection(var12);
            var13.acquire();
            var11.add(var13);
         }

         boolean var19 = false;

         ChunkAccess var21;
         try {
            var19 = true;
            var21 = this.doFill(var1, var3, var2, var4, var7, var8);
            var19 = false;
         } finally {
            if (var19) {
               Iterator var16 = var11.iterator();

               while(var16.hasNext()) {
                  LevelChunkSection var17 = (LevelChunkSection)var16.next();
                  var17.release();
               }

            }
         }

         Iterator var22 = var11.iterator();

         while(var22.hasNext()) {
            LevelChunkSection var14 = (LevelChunkSection)var22.next();
            var14.release();
         }

         return var21;
      }), Util.backgroundExecutor());
   }

   private ChunkAccess doFill(Blender var1, StructureManager var2, RandomState var3, ChunkAccess var4, int var5, int var6) {
      NoiseChunk var7 = var4.getOrCreateNoiseChunk((var4x) -> {
         return this.createNoiseChunk(var4x, var2, var1, var3);
      });
      Heightmap var8 = var4.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap var9 = var4.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
      ChunkPos var10 = var4.getPos();
      int var11 = var10.getMinBlockX();
      int var12 = var10.getMinBlockZ();
      Aquifer var13 = var7.aquifer();
      var7.initializeForFirstCellX();
      BlockPos.MutableBlockPos var14 = new BlockPos.MutableBlockPos();
      int var15 = var7.cellWidth();
      int var16 = var7.cellHeight();
      int var17 = 16 / var15;
      int var18 = 16 / var15;

      for(int var19 = 0; var19 < var17; ++var19) {
         var7.advanceCellX(var19);

         for(int var20 = 0; var20 < var18; ++var20) {
            int var21 = var4.getSectionsCount() - 1;
            LevelChunkSection var22 = var4.getSection(var21);

            for(int var23 = var6 - 1; var23 >= 0; --var23) {
               var7.selectCellYZ(var23, var20);

               for(int var24 = var16 - 1; var24 >= 0; --var24) {
                  int var25 = (var5 + var23) * var16 + var24;
                  int var26 = var25 & 15;
                  int var27 = var4.getSectionIndex(var25);
                  if (var21 != var27) {
                     var21 = var27;
                     var22 = var4.getSection(var27);
                  }

                  double var28 = (double)var24 / (double)var16;
                  var7.updateForY(var25, var28);

                  for(int var30 = 0; var30 < var15; ++var30) {
                     int var31 = var11 + var19 * var15 + var30;
                     int var32 = var31 & 15;
                     double var33 = (double)var30 / (double)var15;
                     var7.updateForX(var31, var33);

                     for(int var35 = 0; var35 < var15; ++var35) {
                        int var36 = var12 + var20 * var15 + var35;
                        int var37 = var36 & 15;
                        double var38 = (double)var35 / (double)var15;
                        var7.updateForZ(var36, var38);
                        BlockState var40 = var7.getInterpolatedState();
                        if (var40 == null) {
                           var40 = ((NoiseGeneratorSettings)this.settings.value()).defaultBlock();
                        }

                        var40 = this.debugPreliminarySurfaceLevel(var7, var31, var25, var36, var40);
                        if (var40 != AIR && !SharedConstants.debugVoidTerrain(var4.getPos())) {
                           var22.setBlockState(var32, var26, var37, var40, false);
                           var8.update(var32, var25, var37, var40);
                           var9.update(var32, var25, var37, var40);
                           if (var13.shouldScheduleFluidUpdate() && !var40.getFluidState().isEmpty()) {
                              var14.set(var31, var25, var36);
                              var4.markPosForPostprocessing(var14);
                           }
                        }
                     }
                  }
               }
            }
         }

         var7.swapSlices();
      }

      var7.stopInterpolation();
      return var4;
   }

   private BlockState debugPreliminarySurfaceLevel(NoiseChunk var1, int var2, int var3, int var4, BlockState var5) {
      return var5;
   }

   public int getGenDepth() {
      return ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().height();
   }

   public int getSeaLevel() {
      return ((NoiseGeneratorSettings)this.settings.value()).seaLevel();
   }

   public int getMinY() {
      return ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().minY();
   }

   public void spawnOriginalMobs(WorldGenRegion var1) {
      if (!((NoiseGeneratorSettings)this.settings.value()).disableMobGeneration()) {
         ChunkPos var2 = var1.getCenter();
         Holder var3 = var1.getBiome(var2.getWorldPosition().atY(var1.getMaxBuildHeight() - 1));
         WorldgenRandom var4 = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
         var4.setDecorationSeed(var1.getSeed(), var2.getMinBlockX(), var2.getMinBlockZ());
         NaturalSpawner.spawnMobsForChunkGeneration(var1, var3, var2, var4);
      }
   }

   static {
      AIR = Blocks.AIR.defaultBlockState();
   }
}
