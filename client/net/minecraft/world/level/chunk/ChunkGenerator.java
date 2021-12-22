package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StrongholdConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public abstract class ChunkGenerator implements BiomeManager.NoiseBiomeSource {
   public static final Codec<ChunkGenerator> CODEC;
   protected final BiomeSource biomeSource;
   protected final BiomeSource runtimeBiomeSource;
   private final StructureSettings settings;
   private final long strongholdSeed;
   private final List<ChunkPos> strongholdPositions;

   public ChunkGenerator(BiomeSource var1, StructureSettings var2) {
      this(var1, var1, var2, 0L);
   }

   public ChunkGenerator(BiomeSource var1, BiomeSource var2, StructureSettings var3, long var4) {
      super();
      this.strongholdPositions = Lists.newArrayList();
      this.biomeSource = var1;
      this.runtimeBiomeSource = var2;
      this.settings = var3;
      this.strongholdSeed = var4;
   }

   private void generateStrongholds() {
      if (this.strongholdPositions.isEmpty()) {
         StrongholdConfiguration var1 = this.settings.stronghold();
         if (var1 != null && var1.count() != 0) {
            ArrayList var2 = Lists.newArrayList();
            Iterator var3 = this.biomeSource.possibleBiomes().iterator();

            while(var3.hasNext()) {
               Biome var4 = (Biome)var3.next();
               if (validStrongholdBiome(var4)) {
                  var2.add(var4);
               }
            }

            int var17 = var1.distance();
            int var18 = var1.count();
            int var5 = var1.spread();
            Random var6 = new Random();
            var6.setSeed(this.strongholdSeed);
            double var7 = var6.nextDouble() * 3.141592653589793D * 2.0D;
            int var9 = 0;
            int var10 = 0;

            for(int var11 = 0; var11 < var18; ++var11) {
               double var12 = (double)(4 * var17 + var17 * var10 * 6) + (var6.nextDouble() - 0.5D) * (double)var17 * 2.5D;
               int var14 = (int)Math.round(Math.cos(var7) * var12);
               int var15 = (int)Math.round(Math.sin(var7) * var12);
               BiomeSource var10000 = this.biomeSource;
               int var10001 = SectionPos.sectionToBlockCoord(var14, 8);
               int var10003 = SectionPos.sectionToBlockCoord(var15, 8);
               Objects.requireNonNull(var2);
               BlockPos var16 = var10000.findBiomeHorizontal(var10001, 0, var10003, 112, var2::contains, var6, this.climateSampler());
               if (var16 != null) {
                  var14 = SectionPos.blockToSectionCoord(var16.getX());
                  var15 = SectionPos.blockToSectionCoord(var16.getZ());
               }

               this.strongholdPositions.add(new ChunkPos(var14, var15));
               var7 += 6.283185307179586D / (double)var5;
               ++var9;
               if (var9 == var5) {
                  ++var10;
                  var9 = 0;
                  var5 += 2 * var5 / (var10 + 1);
                  var5 = Math.min(var5, var18 - var11);
                  var7 += var6.nextDouble() * 3.141592653589793D * 2.0D;
               }
            }

         }
      }
   }

   private static boolean validStrongholdBiome(Biome var0) {
      Biome.BiomeCategory var1 = var0.getBiomeCategory();
      return var1 != Biome.BiomeCategory.OCEAN && var1 != Biome.BiomeCategory.RIVER && var1 != Biome.BiomeCategory.BEACH && var1 != Biome.BiomeCategory.SWAMP && var1 != Biome.BiomeCategory.NETHER && var1 != Biome.BiomeCategory.THEEND;
   }

   protected abstract Codec<? extends ChunkGenerator> codec();

   public Optional<ResourceKey<Codec<? extends ChunkGenerator>>> getTypeNameForDataFixer() {
      return Registry.CHUNK_GENERATOR.getResourceKey(this.codec());
   }

   public abstract ChunkGenerator withSeed(long var1);

   public CompletableFuture<ChunkAccess> createBiomes(Registry<Biome> var1, Executor var2, Blender var3, StructureFeatureManager var4, ChunkAccess var5) {
      return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
         BiomeSource var10001 = this.runtimeBiomeSource;
         Objects.requireNonNull(var10001);
         var5.fillBiomesFromNoise(var10001::getNoiseBiome, this.climateSampler());
         return var5;
      }), Util.backgroundExecutor());
   }

   public abstract Climate.Sampler climateSampler();

   public Biome getNoiseBiome(int var1, int var2, int var3) {
      return this.getBiomeSource().getNoiseBiome(var1, var2, var3, this.climateSampler());
   }

   public abstract void applyCarvers(WorldGenRegion var1, long var2, BiomeManager var4, StructureFeatureManager var5, ChunkAccess var6, GenerationStep.Carving var7);

   @Nullable
   public BlockPos findNearestMapFeature(ServerLevel var1, StructureFeature<?> var2, BlockPos var3, int var4, boolean var5) {
      if (var2 == StructureFeature.STRONGHOLD) {
         this.generateStrongholds();
         BlockPos var14 = null;
         double var15 = 1.7976931348623157E308D;
         BlockPos.MutableBlockPos var16 = new BlockPos.MutableBlockPos();
         Iterator var10 = this.strongholdPositions.iterator();

         while(var10.hasNext()) {
            ChunkPos var11 = (ChunkPos)var10.next();
            var16.set(SectionPos.sectionToBlockCoord(var11.field_504, 8), 32, SectionPos.sectionToBlockCoord(var11.field_505, 8));
            double var12 = var16.distSqr(var3);
            if (var14 == null) {
               var14 = new BlockPos(var16);
               var15 = var12;
            } else if (var12 < var15) {
               var14 = new BlockPos(var16);
               var15 = var12;
            }
         }

         return var14;
      } else {
         StructureFeatureConfiguration var6 = this.settings.getConfig(var2);
         ImmutableMultimap var7 = this.settings.structures(var2);
         if (var6 != null && !var7.isEmpty()) {
            Registry var8 = var1.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
            Set var9 = (Set)this.runtimeBiomeSource.possibleBiomes().stream().flatMap((var1x) -> {
               return var8.getResourceKey(var1x).stream();
            }).collect(Collectors.toSet());
            Stream var10000 = var7.values().stream();
            Objects.requireNonNull(var9);
            return var10000.noneMatch(var9::contains) ? null : var2.getNearestGeneratedFeature(var1, var1.structureFeatureManager(), var3, var4, var5, var1.getSeed(), var6);
         } else {
            return null;
         }
      }
   }

   public void applyBiomeDecoration(WorldGenLevel var1, ChunkAccess var2, StructureFeatureManager var3) {
      ChunkPos var4 = var2.getPos();
      if (!SharedConstants.debugVoidTerrain(var4)) {
         SectionPos var5 = SectionPos.method_72(var4, var1.getMinSection());
         BlockPos var6 = var5.origin();
         Map var7 = (Map)Registry.STRUCTURE_FEATURE.stream().collect(Collectors.groupingBy((var0) -> {
            return var0.step().ordinal();
         }));
         List var8 = this.biomeSource.featuresPerStep();
         WorldgenRandom var9 = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.seedUniquifier()));
         long var10 = var9.setDecorationSeed(var1.getSeed(), var6.getX(), var6.getZ());
         ObjectArraySet var12 = new ObjectArraySet();
         if (this instanceof FlatLevelSource) {
            var12.addAll(this.biomeSource.possibleBiomes());
         } else {
            ChunkPos.rangeClosed(var5.chunk(), 1).forEach((var2x) -> {
               ChunkAccess var3 = var1.getChunk(var2x.field_504, var2x.field_505);
               LevelChunkSection[] var4 = var3.getSections();
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  LevelChunkSection var7 = var4[var6];
                  PalettedContainer var10000 = var7.getBiomes();
                  Objects.requireNonNull(var12);
                  var10000.getAll(var12::add);
               }

            });
            var12.retainAll(this.biomeSource.possibleBiomes());
         }

         int var13 = var8.size();

         try {
            Registry var14 = var1.registryAccess().registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
            Registry var32 = var1.registryAccess().registryOrThrow(Registry.STRUCTURE_FEATURE_REGISTRY);
            int var16 = Math.max(GenerationStep.Decoration.values().length, var13);

            for(int var17 = 0; var17 < var16; ++var17) {
               int var18 = 0;
               CrashReportCategory var10000;
               Iterator var20;
               if (var3.shouldGenerateFeatures()) {
                  List var19 = (List)var7.getOrDefault(var17, Collections.emptyList());

                  for(var20 = var19.iterator(); var20.hasNext(); ++var18) {
                     StructureFeature var21 = (StructureFeature)var20.next();
                     var9.setFeatureSeed(var10, var18, var17);
                     Supplier var22 = () -> {
                        Optional var10000 = var32.getResourceKey(var21).map(Object::toString);
                        Objects.requireNonNull(var21);
                        return (String)var10000.orElseGet(var21::toString);
                     };

                     try {
                        var1.setCurrentlyGenerating(var22);
                        var3.startsForFeature(var5, var21).forEach((var6x) -> {
                           var6x.placeInChunk(var1, var3, this, var9, getWritableArea(var2), var4);
                        });
                     } catch (Exception var29) {
                        CrashReport var24 = CrashReport.forThrowable(var29, "Feature placement");
                        var10000 = var24.addCategory("Feature");
                        Objects.requireNonNull(var22);
                        var10000.setDetail("Description", var22::get);
                        throw new ReportedException(var24);
                     }
                  }
               }

               if (var17 < var13) {
                  IntArraySet var33 = new IntArraySet();
                  var20 = var12.iterator();

                  while(var20.hasNext()) {
                     Biome var35 = (Biome)var20.next();
                     List var37 = var35.getGenerationSettings().features();
                     if (var17 < var37.size()) {
                        List var23 = (List)var37.get(var17);
                        BiomeSource.StepFeatureData var40 = (BiomeSource.StepFeatureData)var8.get(var17);
                        var23.stream().map(Supplier::get).forEach((var2x) -> {
                           var33.add(var40.indexMapping().applyAsInt(var2x));
                        });
                     }
                  }

                  int var34 = var33.size();
                  int[] var36 = var33.toIntArray();
                  Arrays.sort(var36);
                  BiomeSource.StepFeatureData var38 = (BiomeSource.StepFeatureData)var8.get(var17);

                  for(int var39 = 0; var39 < var34; ++var39) {
                     int var41 = var36[var39];
                     PlacedFeature var25 = (PlacedFeature)var38.features().get(var41);
                     Supplier var26 = () -> {
                        Optional var10000 = var14.getResourceKey(var25).map(Object::toString);
                        Objects.requireNonNull(var25);
                        return (String)var10000.orElseGet(var25::toString);
                     };
                     var9.setFeatureSeed(var10, var41, var17);

                     try {
                        var1.setCurrentlyGenerating(var26);
                        var25.placeWithBiomeCheck(var1, this, var9, var6);
                     } catch (Exception var30) {
                        CrashReport var28 = CrashReport.forThrowable(var30, "Feature placement");
                        var10000 = var28.addCategory("Feature");
                        Objects.requireNonNull(var26);
                        var10000.setDetail("Description", var26::get);
                        throw new ReportedException(var28);
                     }
                  }
               }
            }

            var1.setCurrentlyGenerating((Supplier)null);
         } catch (Exception var31) {
            CrashReport var15 = CrashReport.forThrowable(var31, "Biome decoration");
            var15.addCategory("Generation").setDetail("CenterX", (Object)var4.field_504).setDetail("CenterZ", (Object)var4.field_505).setDetail("Seed", (Object)var10);
            throw new ReportedException(var15);
         }
      }
   }

   private static BoundingBox getWritableArea(ChunkAccess var0) {
      ChunkPos var1 = var0.getPos();
      int var2 = var1.getMinBlockX();
      int var3 = var1.getMinBlockZ();
      LevelHeightAccessor var4 = var0.getHeightAccessorForGeneration();
      int var5 = var4.getMinBuildHeight() + 1;
      int var6 = var4.getMaxBuildHeight() - 1;
      return new BoundingBox(var2, var5, var3, var2 + 15, var6, var3 + 15);
   }

   public abstract void buildSurface(WorldGenRegion var1, StructureFeatureManager var2, ChunkAccess var3);

   public abstract void spawnOriginalMobs(WorldGenRegion var1);

   public StructureSettings getSettings() {
      return this.settings;
   }

   public int getSpawnHeight(LevelHeightAccessor var1) {
      return 64;
   }

   public BiomeSource getBiomeSource() {
      return this.runtimeBiomeSource;
   }

   public abstract int getGenDepth();

   public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Biome var1, StructureFeatureManager var2, MobCategory var3, BlockPos var4) {
      return var1.getMobSettings().getMobs(var3);
   }

   public void createStructures(RegistryAccess var1, StructureFeatureManager var2, ChunkAccess var3, StructureManager var4, long var5) {
      ChunkPos var7 = var3.getPos();
      SectionPos var8 = SectionPos.bottomOf(var3);
      StructureFeatureConfiguration var9 = this.settings.getConfig(StructureFeature.STRONGHOLD);
      if (var9 != null) {
         StructureStart var10 = var2.getStartForFeature(var8, StructureFeature.STRONGHOLD, var3);
         if (var10 == null || !var10.isValid()) {
            StructureStart var11 = StructureFeatures.STRONGHOLD.generate(var1, this, this.biomeSource, var4, var5, var7, fetchReferences(var2, var3, var8, StructureFeature.STRONGHOLD), var9, var3, ChunkGenerator::validStrongholdBiome);
            var2.setStartForFeature(var8, StructureFeature.STRONGHOLD, var11, var3);
         }
      }

      Registry var19 = var1.registryOrThrow(Registry.BIOME_REGISTRY);
      Iterator var20 = Registry.STRUCTURE_FEATURE.iterator();

      while(true) {
         label46:
         while(true) {
            StructureFeature var12;
            StructureFeatureConfiguration var13;
            StructureStart var14;
            do {
               do {
                  do {
                     if (!var20.hasNext()) {
                        return;
                     }

                     var12 = (StructureFeature)var20.next();
                  } while(var12 == StructureFeature.STRONGHOLD);

                  var13 = this.settings.getConfig(var12);
               } while(var13 == null);

               var14 = var2.getStartForFeature(var8, var12, var3);
            } while(var14 != null && var14.isValid());

            int var15 = fetchReferences(var2, var3, var8, var12);
            UnmodifiableIterator var16 = this.settings.structures(var12).asMap().entrySet().iterator();

            while(var16.hasNext()) {
               Entry var17 = (Entry)var16.next();
               StructureStart var18 = ((ConfiguredStructureFeature)var17.getKey()).generate(var1, this, this.biomeSource, var4, var5, var7, var15, var13, var3, (var3x) -> {
                  Collection var10002 = (Collection)var17.getValue();
                  Objects.requireNonNull(var10002);
                  return this.validBiome(var19, var10002::contains, var3x);
               });
               if (var18.isValid()) {
                  var2.setStartForFeature(var8, var12, var18, var3);
                  continue label46;
               }
            }

            var2.setStartForFeature(var8, var12, StructureStart.INVALID_START, var3);
         }
      }
   }

   private static int fetchReferences(StructureFeatureManager var0, ChunkAccess var1, SectionPos var2, StructureFeature<?> var3) {
      StructureStart var4 = var0.getStartForFeature(var2, var3, var1);
      return var4 != null ? var4.getReferences() : 0;
   }

   protected boolean validBiome(Registry<Biome> var1, Predicate<ResourceKey<Biome>> var2, Biome var3) {
      return var1.getResourceKey(var3).filter(var2).isPresent();
   }

   public void createReferences(WorldGenLevel var1, StructureFeatureManager var2, ChunkAccess var3) {
      boolean var4 = true;
      ChunkPos var5 = var3.getPos();
      int var6 = var5.field_504;
      int var7 = var5.field_505;
      int var8 = var5.getMinBlockX();
      int var9 = var5.getMinBlockZ();
      SectionPos var10 = SectionPos.bottomOf(var3);

      for(int var11 = var6 - 8; var11 <= var6 + 8; ++var11) {
         for(int var12 = var7 - 8; var12 <= var7 + 8; ++var12) {
            long var13 = ChunkPos.asLong(var11, var12);
            Iterator var15 = var1.getChunk(var11, var12).getAllStarts().values().iterator();

            while(var15.hasNext()) {
               StructureStart var16 = (StructureStart)var15.next();

               try {
                  if (var16.isValid() && var16.getBoundingBox().intersects(var8, var9, var8 + 15, var9 + 15)) {
                     var2.addReferenceForFeature(var10, var16.getFeature(), var13, var3);
                     DebugPackets.sendStructurePacket(var1, var16);
                  }
               } catch (Exception var20) {
                  CrashReport var18 = CrashReport.forThrowable(var20, "Generating structure reference");
                  CrashReportCategory var19 = var18.addCategory("Structure");
                  var19.setDetail("Id", () -> {
                     return Registry.STRUCTURE_FEATURE.getKey(var16.getFeature()).toString();
                  });
                  var19.setDetail("Name", () -> {
                     return var16.getFeature().getFeatureName();
                  });
                  var19.setDetail("Class", () -> {
                     return var16.getFeature().getClass().getCanonicalName();
                  });
                  throw new ReportedException(var18);
               }
            }
         }
      }

   }

   public abstract CompletableFuture<ChunkAccess> fillFromNoise(Executor var1, Blender var2, StructureFeatureManager var3, ChunkAccess var4);

   public abstract int getSeaLevel();

   public abstract int getMinY();

   public abstract int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4);

   public abstract NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3);

   public int getFirstFreeHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4) {
      return this.getBaseHeight(var1, var2, var3, var4);
   }

   public int getFirstOccupiedHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4) {
      return this.getBaseHeight(var1, var2, var3, var4) - 1;
   }

   public boolean hasStronghold(ChunkPos var1) {
      this.generateStrongholds();
      return this.strongholdPositions.contains(var1);
   }

   static {
      Registry.register(Registry.CHUNK_GENERATOR, (String)"noise", NoiseBasedChunkGenerator.CODEC);
      Registry.register(Registry.CHUNK_GENERATOR, (String)"flat", FlatLevelSource.CODEC);
      Registry.register(Registry.CHUNK_GENERATOR, (String)"debug", DebugLevelSource.CODEC);
      CODEC = Registry.CHUNK_GENERATOR.byNameCodec().dispatchStable(ChunkGenerator::codec, Function.identity());
   }
}
