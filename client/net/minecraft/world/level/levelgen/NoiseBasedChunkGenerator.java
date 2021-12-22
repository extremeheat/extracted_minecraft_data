package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.NetherFortressFeature;
import net.minecraft.world.level.levelgen.feature.OceanMonumentFeature;
import net.minecraft.world.level.levelgen.feature.PillagerOutpostFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.SwamplandHutFeature;
import net.minecraft.world.level.levelgen.material.MaterialRuleList;
import net.minecraft.world.level.levelgen.material.WorldGenMaterialRule;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public final class NoiseBasedChunkGenerator extends ChunkGenerator {
   public static final Codec<NoiseBasedChunkGenerator> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(RegistryLookupCodec.create(Registry.NOISE_REGISTRY).forGetter((var0x) -> {
         return var0x.noises;
      }), BiomeSource.CODEC.fieldOf("biome_source").forGetter((var0x) -> {
         return var0x.biomeSource;
      }), Codec.LONG.fieldOf("seed").stable().forGetter((var0x) -> {
         return var0x.seed;
      }), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((var0x) -> {
         return var0x.settings;
      })).apply(var0, var0.stable(NoiseBasedChunkGenerator::new));
   });
   private static final BlockState AIR;
   private static final BlockState[] EMPTY_COLUMN;
   protected final BlockState defaultBlock;
   private final Registry<NormalNoise.NoiseParameters> noises;
   private final long seed;
   protected final Supplier<NoiseGeneratorSettings> settings;
   private final NoiseSampler sampler;
   private final SurfaceSystem surfaceSystem;
   private final WorldGenMaterialRule materialRule;
   private final Aquifer.FluidPicker globalFluidPicker;

   public NoiseBasedChunkGenerator(Registry<NormalNoise.NoiseParameters> var1, BiomeSource var2, long var3, Supplier<NoiseGeneratorSettings> var5) {
      this(var1, var2, var2, var3, var5);
   }

   private NoiseBasedChunkGenerator(Registry<NormalNoise.NoiseParameters> var1, BiomeSource var2, BiomeSource var3, long var4, Supplier<NoiseGeneratorSettings> var6) {
      super(var2, var3, ((NoiseGeneratorSettings)var6.get()).structureSettings(), var4);
      this.noises = var1;
      this.seed = var4;
      this.settings = var6;
      NoiseGeneratorSettings var7 = (NoiseGeneratorSettings)this.settings.get();
      this.defaultBlock = var7.getDefaultBlock();
      NoiseSettings var8 = var7.noiseSettings();
      this.sampler = new NoiseSampler(var8, var7.isNoiseCavesEnabled(), var4, var1, var7.getRandomSource());
      Builder var9 = ImmutableList.builder();
      var9.add(NoiseChunk::updateNoiseAndGenerateBaseState);
      var9.add(NoiseChunk::oreVeinify);
      this.materialRule = new MaterialRuleList(var9.build());
      Aquifer.FluidStatus var10 = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
      int var11 = var7.seaLevel();
      Aquifer.FluidStatus var12 = new Aquifer.FluidStatus(var11, var7.getDefaultFluid());
      Aquifer.FluidStatus var13 = new Aquifer.FluidStatus(var8.minY() - 1, Blocks.AIR.defaultBlockState());
      this.globalFluidPicker = (var4x, var5, var6x) -> {
         return var5 < Math.min(-54, var11) ? var10 : var12;
      };
      this.surfaceSystem = new SurfaceSystem(var1, this.defaultBlock, var11, var4, var7.getRandomSource());
   }

   public CompletableFuture<ChunkAccess> createBiomes(Registry<Biome> var1, Executor var2, Blender var3, StructureFeatureManager var4, ChunkAccess var5) {
      return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
         this.doCreateBiomes(var1, var3, var4, var5);
         return var5;
      }), Util.backgroundExecutor());
   }

   private void doCreateBiomes(Registry<Biome> var1, Blender var2, StructureFeatureManager var3, ChunkAccess var4) {
      NoiseChunk var5 = var4.getOrCreateNoiseChunk(this.sampler, () -> {
         return new Beardifier(var3, var4);
      }, (NoiseGeneratorSettings)this.settings.get(), this.globalFluidPicker, var2);
      BiomeResolver var6 = BelowZeroRetrogen.getBiomeResolver(var2.getBiomeResolver(this.runtimeBiomeSource), var1, var4);
      var4.fillBiomesFromNoise(var6, (var2x, var3x, var4x) -> {
         return this.sampler.target(var2x, var3x, var4x, var5.noiseData(var2x, var4x));
      });
   }

   public Climate.Sampler climateSampler() {
      return this.sampler;
   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public ChunkGenerator withSeed(long var1) {
      return new NoiseBasedChunkGenerator(this.noises, this.biomeSource.withSeed(var1), var1, this.settings);
   }

   public boolean stable(long var1, ResourceKey<NoiseGeneratorSettings> var3) {
      return this.seed == var1 && ((NoiseGeneratorSettings)this.settings.get()).stable(var3);
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4) {
      NoiseSettings var5 = ((NoiseGeneratorSettings)this.settings.get()).noiseSettings();
      int var6 = Math.max(var5.minY(), var4.getMinBuildHeight());
      int var7 = Math.min(var5.minY() + var5.height(), var4.getMaxBuildHeight());
      int var8 = Mth.intFloorDiv(var6, var5.getCellHeight());
      int var9 = Mth.intFloorDiv(var7 - var6, var5.getCellHeight());
      return var9 <= 0 ? var4.getMinBuildHeight() : this.iterateNoiseColumn(var1, var2, (BlockState[])null, var3.isOpaque(), var8, var9).orElse(var4.getMinBuildHeight());
   }

   public NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3) {
      NoiseSettings var4 = ((NoiseGeneratorSettings)this.settings.get()).noiseSettings();
      int var5 = Math.max(var4.minY(), var3.getMinBuildHeight());
      int var6 = Math.min(var4.minY() + var4.height(), var3.getMaxBuildHeight());
      int var7 = Mth.intFloorDiv(var5, var4.getCellHeight());
      int var8 = Mth.intFloorDiv(var6 - var5, var4.getCellHeight());
      if (var8 <= 0) {
         return new NoiseColumn(var5, EMPTY_COLUMN);
      } else {
         BlockState[] var9 = new BlockState[var8 * var4.getCellHeight()];
         this.iterateNoiseColumn(var1, var2, var9, (Predicate)null, var7, var8);
         return new NoiseColumn(var5, var9);
      }
   }

   private OptionalInt iterateNoiseColumn(int var1, int var2, @Nullable BlockState[] var3, @Nullable Predicate<BlockState> var4, int var5, int var6) {
      NoiseSettings var7 = ((NoiseGeneratorSettings)this.settings.get()).noiseSettings();
      int var8 = var7.getCellWidth();
      int var9 = var7.getCellHeight();
      int var10 = Math.floorDiv(var1, var8);
      int var11 = Math.floorDiv(var2, var8);
      int var12 = Math.floorMod(var1, var8);
      int var13 = Math.floorMod(var2, var8);
      int var14 = var10 * var8;
      int var15 = var11 * var8;
      double var16 = (double)var12 / (double)var8;
      double var18 = (double)var13 / (double)var8;
      NoiseChunk var20 = NoiseChunk.forColumn(var14, var15, var5, var6, this.sampler, (NoiseGeneratorSettings)this.settings.get(), this.globalFluidPicker);
      var20.initializeForFirstCellX();
      var20.advanceCellX(0);

      for(int var21 = var6 - 1; var21 >= 0; --var21) {
         var20.selectCellYZ(var21, 0);

         for(int var22 = var9 - 1; var22 >= 0; --var22) {
            int var23 = (var5 + var21) * var9 + var22;
            double var24 = (double)var22 / (double)var9;
            var20.updateForY(var24);
            var20.updateForX(var16);
            var20.updateForZ(var18);
            BlockState var26 = this.materialRule.apply(var20, var1, var23, var2);
            BlockState var27 = var26 == null ? this.defaultBlock : var26;
            if (var3 != null) {
               int var28 = var21 * var9 + var22;
               var3[var28] = var27;
            }

            if (var4 != null && var4.test(var27)) {
               return OptionalInt.of(var23 + 1);
            }
         }
      }

      return OptionalInt.empty();
   }

   public void buildSurface(WorldGenRegion var1, StructureFeatureManager var2, ChunkAccess var3) {
      if (!SharedConstants.debugVoidTerrain(var3.getPos())) {
         WorldGenerationContext var4 = new WorldGenerationContext(this, var1);
         NoiseGeneratorSettings var5 = (NoiseGeneratorSettings)this.settings.get();
         NoiseChunk var6 = var3.getOrCreateNoiseChunk(this.sampler, () -> {
            return new Beardifier(var2, var3);
         }, var5, this.globalFluidPicker, Blender.method_124(var1));
         this.surfaceSystem.buildSurface(var1.getBiomeManager(), var1.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), var5.useLegacyRandomSource(), var4, var3, var6, var5.surfaceRule());
      }
   }

   public void applyCarvers(WorldGenRegion var1, long var2, BiomeManager var4, StructureFeatureManager var5, ChunkAccess var6, GenerationStep.Carving var7) {
      BiomeManager var8 = var4.withDifferentSource((var1x, var2x, var3) -> {
         return this.biomeSource.getNoiseBiome(var1x, var2x, var3, this.climateSampler());
      });
      WorldgenRandom var9 = new WorldgenRandom(new LegacyRandomSource(RandomSupport.seedUniquifier()));
      boolean var10 = true;
      ChunkPos var11 = var6.getPos();
      NoiseChunk var12 = var6.getOrCreateNoiseChunk(this.sampler, () -> {
         return new Beardifier(var5, var6);
      }, (NoiseGeneratorSettings)this.settings.get(), this.globalFluidPicker, Blender.method_124(var1));
      Aquifer var13 = var12.aquifer();
      CarvingContext var14 = new CarvingContext(this, var1.registryAccess(), var6.getHeightAccessorForGeneration(), var12);
      CarvingMask var15 = ((ProtoChunk)var6).getOrCreateCarvingMask(var7);

      for(int var16 = -8; var16 <= 8; ++var16) {
         for(int var17 = -8; var17 <= 8; ++var17) {
            ChunkPos var18 = new ChunkPos(var11.field_504 + var16, var11.field_505 + var17);
            ChunkAccess var19 = var1.getChunk(var18.field_504, var18.field_505);
            BiomeGenerationSettings var20 = var19.carverBiome(() -> {
               return this.biomeSource.getNoiseBiome(QuartPos.fromBlock(var18.getMinBlockX()), 0, QuartPos.fromBlock(var18.getMinBlockZ()), this.climateSampler());
            }).getGenerationSettings();
            List var21 = var20.getCarvers(var7);
            ListIterator var22 = var21.listIterator();

            while(var22.hasNext()) {
               int var23 = var22.nextIndex();
               ConfiguredWorldCarver var24 = (ConfiguredWorldCarver)((Supplier)var22.next()).get();
               var9.setLargeFeatureSeed(var2 + (long)var23, var18.field_504, var18.field_505);
               if (var24.isStartChunk(var9)) {
                  Objects.requireNonNull(var8);
                  var24.carve(var14, var6, var8::getBiome, var9, var13, var18, var15);
               }
            }
         }
      }

   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Executor var1, Blender var2, StructureFeatureManager var3, ChunkAccess var4) {
      NoiseSettings var5 = ((NoiseGeneratorSettings)this.settings.get()).noiseSettings();
      LevelHeightAccessor var6 = var4.getHeightAccessorForGeneration();
      int var7 = Math.max(var5.minY(), var6.getMinBuildHeight());
      int var8 = Math.min(var5.minY() + var5.height(), var6.getMaxBuildHeight());
      int var9 = Mth.intFloorDiv(var7, var5.getCellHeight());
      int var10 = Mth.intFloorDiv(var8 - var7, var5.getCellHeight());
      if (var10 <= 0) {
         return CompletableFuture.completedFuture(var4);
      } else {
         int var11 = var4.getSectionIndex(var10 * var5.getCellHeight() - 1 + var7);
         int var12 = var4.getSectionIndex(var7);
         HashSet var13 = Sets.newHashSet();

         for(int var14 = var11; var14 >= var12; --var14) {
            LevelChunkSection var15 = var4.getSection(var14);
            var15.acquire();
            var13.add(var15);
         }

         return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("wgen_fill_noise", () -> {
            return this.doFill(var2, var3, var4, var9, var10);
         }), Util.backgroundExecutor()).whenCompleteAsync((var1x, var2x) -> {
            Iterator var3 = var13.iterator();

            while(var3.hasNext()) {
               LevelChunkSection var4 = (LevelChunkSection)var3.next();
               var4.release();
            }

         }, var1);
      }
   }

   private ChunkAccess doFill(Blender var1, StructureFeatureManager var2, ChunkAccess var3, int var4, int var5) {
      NoiseGeneratorSettings var6 = (NoiseGeneratorSettings)this.settings.get();
      NoiseChunk var7 = var3.getOrCreateNoiseChunk(this.sampler, () -> {
         return new Beardifier(var2, var3);
      }, var6, this.globalFluidPicker, var1);
      Heightmap var8 = var3.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap var9 = var3.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
      ChunkPos var10 = var3.getPos();
      int var11 = var10.getMinBlockX();
      int var12 = var10.getMinBlockZ();
      Aquifer var13 = var7.aquifer();
      var7.initializeForFirstCellX();
      BlockPos.MutableBlockPos var14 = new BlockPos.MutableBlockPos();
      NoiseSettings var15 = var6.noiseSettings();
      int var16 = var15.getCellWidth();
      int var17 = var15.getCellHeight();
      int var18 = 16 / var16;
      int var19 = 16 / var16;

      for(int var20 = 0; var20 < var18; ++var20) {
         var7.advanceCellX(var20);

         for(int var21 = 0; var21 < var19; ++var21) {
            LevelChunkSection var22 = var3.getSection(var3.getSectionsCount() - 1);

            for(int var23 = var5 - 1; var23 >= 0; --var23) {
               var7.selectCellYZ(var23, var21);

               for(int var24 = var17 - 1; var24 >= 0; --var24) {
                  int var25 = (var4 + var23) * var17 + var24;
                  int var26 = var25 & 15;
                  int var27 = var3.getSectionIndex(var25);
                  if (var3.getSectionIndex(var22.bottomBlockY()) != var27) {
                     var22 = var3.getSection(var27);
                  }

                  double var28 = (double)var24 / (double)var17;
                  var7.updateForY(var28);

                  for(int var30 = 0; var30 < var16; ++var30) {
                     int var31 = var11 + var20 * var16 + var30;
                     int var32 = var31 & 15;
                     double var33 = (double)var30 / (double)var16;
                     var7.updateForX(var33);

                     for(int var35 = 0; var35 < var16; ++var35) {
                        int var36 = var12 + var21 * var16 + var35;
                        int var37 = var36 & 15;
                        double var38 = (double)var35 / (double)var16;
                        var7.updateForZ(var38);
                        BlockState var40 = this.materialRule.apply(var7, var31, var25, var36);
                        if (var40 == null) {
                           var40 = this.defaultBlock;
                        }

                        var40 = this.debugPreliminarySurfaceLevel(var7, var31, var25, var36, var40);
                        if (var40 != AIR && !SharedConstants.debugVoidTerrain(var3.getPos())) {
                           if (var40.getLightEmission() != 0 && var3 instanceof ProtoChunk) {
                              var14.set(var31, var25, var36);
                              ((ProtoChunk)var3).addLight(var14);
                           }

                           var22.setBlockState(var32, var26, var37, var40, false);
                           var8.update(var32, var25, var37, var40);
                           var9.update(var32, var25, var37, var40);
                           if (var13.shouldScheduleFluidUpdate() && !var40.getFluidState().isEmpty()) {
                              var14.set(var31, var25, var36);
                              var3.markPosForPostprocessing(var14);
                           }
                        }
                     }
                  }
               }
            }
         }

         var7.swapSlices();
      }

      return var3;
   }

   private BlockState debugPreliminarySurfaceLevel(NoiseChunk var1, int var2, int var3, int var4, BlockState var5) {
      return var5;
   }

   public int getGenDepth() {
      return ((NoiseGeneratorSettings)this.settings.get()).noiseSettings().height();
   }

   public int getSeaLevel() {
      return ((NoiseGeneratorSettings)this.settings.get()).seaLevel();
   }

   public int getMinY() {
      return ((NoiseGeneratorSettings)this.settings.get()).noiseSettings().minY();
   }

   public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Biome var1, StructureFeatureManager var2, MobCategory var3, BlockPos var4) {
      if (!var2.hasAnyStructureAt(var4)) {
         return super.getMobsAt(var1, var2, var3, var4);
      } else {
         if (var2.getStructureWithPieceAt(var4, StructureFeature.SWAMP_HUT).isValid()) {
            if (var3 == MobCategory.MONSTER) {
               return SwamplandHutFeature.SWAMPHUT_ENEMIES;
            }

            if (var3 == MobCategory.CREATURE) {
               return SwamplandHutFeature.SWAMPHUT_ANIMALS;
            }
         }

         if (var3 == MobCategory.MONSTER) {
            if (var2.getStructureAt(var4, StructureFeature.PILLAGER_OUTPOST).isValid()) {
               return PillagerOutpostFeature.OUTPOST_ENEMIES;
            }

            if (var2.getStructureAt(var4, StructureFeature.OCEAN_MONUMENT).isValid()) {
               return OceanMonumentFeature.MONUMENT_ENEMIES;
            }

            if (var2.getStructureWithPieceAt(var4, StructureFeature.NETHER_BRIDGE).isValid()) {
               return NetherFortressFeature.FORTRESS_ENEMIES;
            }
         }

         return (var3 == MobCategory.UNDERGROUND_WATER_CREATURE || var3 == MobCategory.AXOLOTLS) && var2.getStructureAt(var4, StructureFeature.OCEAN_MONUMENT).isValid() ? MobSpawnSettings.EMPTY_MOB_LIST : super.getMobsAt(var1, var2, var3, var4);
      }
   }

   public void spawnOriginalMobs(WorldGenRegion var1) {
      if (!((NoiseGeneratorSettings)this.settings.get()).disableMobGeneration()) {
         ChunkPos var2 = var1.getCenter();
         Biome var3 = var1.getBiome(var2.getWorldPosition().atY(var1.getMaxBuildHeight() - 1));
         WorldgenRandom var4 = new WorldgenRandom(new LegacyRandomSource(RandomSupport.seedUniquifier()));
         var4.setDecorationSeed(var1.getSeed(), var2.getMinBlockX(), var2.getMinBlockZ());
         NaturalSpawner.spawnMobsForChunkGeneration(var1, var3, var2, var4);
      }
   }

   /** @deprecated */
   @Deprecated
   public Optional<BlockState> topMaterial(CarvingContext var1, Function<BlockPos, Biome> var2, ChunkAccess var3, NoiseChunk var4, BlockPos var5, boolean var6) {
      return this.surfaceSystem.topMaterial(((NoiseGeneratorSettings)this.settings.get()).surfaceRule(), var1, var2, var3, var4, var5, var6);
   }

   static {
      AIR = Blocks.AIR.defaultBlockState();
      EMPTY_COLUMN = new BlockState[0];
   }
}
