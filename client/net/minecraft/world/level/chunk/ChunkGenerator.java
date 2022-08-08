package net.minecraft.world.level.chunk;

import com.google.common.base.Stopwatch;
import com.google.common.base.Suppliers;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

public abstract class ChunkGenerator {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<ChunkGenerator> CODEC;
   protected final Registry<StructureSet> structureSets;
   protected final BiomeSource biomeSource;
   private final Supplier<List<FeatureSorter.StepFeatureData>> featuresPerStep;
   protected final Optional<HolderSet<StructureSet>> structureOverrides;
   private final Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter;
   private final Map<Structure, List<StructurePlacement>> placementsForStructure;
   private final Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>> ringPositions;
   private boolean hasGeneratedPositions;

   protected static <T extends ChunkGenerator> Products.P1<RecordCodecBuilder.Mu<T>, Registry<StructureSet>> commonCodec(RecordCodecBuilder.Instance<T> var0) {
      return var0.group(RegistryOps.retrieveRegistry(Registry.STRUCTURE_SET_REGISTRY).forGetter((var0x) -> {
         return var0x.structureSets;
      }));
   }

   public ChunkGenerator(Registry<StructureSet> var1, Optional<HolderSet<StructureSet>> var2, BiomeSource var3) {
      this(var1, var2, var3, (var0) -> {
         return ((Biome)var0.value()).getGenerationSettings();
      });
   }

   public ChunkGenerator(Registry<StructureSet> var1, Optional<HolderSet<StructureSet>> var2, BiomeSource var3, Function<Holder<Biome>, BiomeGenerationSettings> var4) {
      super();
      this.placementsForStructure = new Object2ObjectOpenHashMap();
      this.ringPositions = new Object2ObjectArrayMap();
      this.structureSets = var1;
      this.biomeSource = var3;
      this.generationSettingsGetter = var4;
      this.structureOverrides = var2;
      this.featuresPerStep = Suppliers.memoize(() -> {
         return FeatureSorter.buildFeaturesPerStep(List.copyOf(var3.possibleBiomes()), (var1) -> {
            return ((BiomeGenerationSettings)var4.apply(var1)).features();
         }, true);
      });
   }

   public Stream<Holder<StructureSet>> possibleStructureSets() {
      return this.structureOverrides.isPresent() ? ((HolderSet)this.structureOverrides.get()).stream() : this.structureSets.holders().map(Holder::hackyErase);
   }

   private void generatePositions(RandomState var1) {
      Set var2 = this.biomeSource.possibleBiomes();
      this.possibleStructureSets().forEach((var3) -> {
         StructureSet var4 = (StructureSet)var3.value();
         boolean var5 = false;
         Iterator var6 = var4.structures().iterator();

         while(var6.hasNext()) {
            StructureSet.StructureSelectionEntry var7 = (StructureSet.StructureSelectionEntry)var6.next();
            Structure var8 = (Structure)var7.structure().value();
            Stream var10000 = var8.biomes().stream();
            Objects.requireNonNull(var2);
            if (var10000.anyMatch(var2::contains)) {
               ((List)this.placementsForStructure.computeIfAbsent(var8, (var0) -> {
                  return new ArrayList();
               })).add(var4.placement());
               var5 = true;
            }
         }

         if (var5) {
            StructurePlacement var10 = var4.placement();
            if (var10 instanceof ConcentricRingsStructurePlacement) {
               ConcentricRingsStructurePlacement var9 = (ConcentricRingsStructurePlacement)var10;
               this.ringPositions.put(var9, this.generateRingPositions(var3, var1, var9));
            }
         }

      });
   }

   private CompletableFuture<List<ChunkPos>> generateRingPositions(Holder<StructureSet> var1, RandomState var2, ConcentricRingsStructurePlacement var3) {
      return var3.count() == 0 ? CompletableFuture.completedFuture(List.of()) : CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("placement calculation", () -> {
         Stopwatch var4 = Stopwatch.createStarted(Util.TICKER);
         ArrayList var5 = new ArrayList();
         int var6 = var3.distance();
         int var7 = var3.count();
         int var8 = var3.spread();
         HolderSet var9 = var3.preferredBiomes();
         RandomSource var10 = RandomSource.create();
         var10.setSeed(this instanceof FlatLevelSource ? 0L : var2.legacyLevelSeed());
         double var11 = var10.nextDouble() * 3.141592653589793 * 2.0;
         int var13 = 0;
         int var14 = 0;

         for(int var15 = 0; var15 < var7; ++var15) {
            double var16 = (double)(4 * var6 + var6 * var14 * 6) + (var10.nextDouble() - 0.5) * (double)var6 * 2.5;
            int var18 = (int)Math.round(Math.cos(var11) * var16);
            int var19 = (int)Math.round(Math.sin(var11) * var16);
            BiomeSource var10000 = this.biomeSource;
            int var10001 = SectionPos.sectionToBlockCoord(var18, 8);
            int var10003 = SectionPos.sectionToBlockCoord(var19, 8);
            Objects.requireNonNull(var9);
            Pair var20 = var10000.findBiomeHorizontal(var10001, 0, var10003, 112, var9::contains, var10, var2.sampler());
            if (var20 != null) {
               BlockPos var21 = (BlockPos)var20.getFirst();
               var18 = SectionPos.blockToSectionCoord(var21.getX());
               var19 = SectionPos.blockToSectionCoord(var21.getZ());
            }

            var5.add(new ChunkPos(var18, var19));
            var11 += 6.283185307179586 / (double)var8;
            ++var13;
            if (var13 == var8) {
               ++var14;
               var13 = 0;
               var8 += 2 * var8 / (var14 + 1);
               var8 = Math.min(var8, var7 - var15);
               var11 += var10.nextDouble() * 3.141592653589793 * 2.0;
            }
         }

         double var22 = (double)var4.stop().elapsed(TimeUnit.MILLISECONDS) / 1000.0;
         LOGGER.debug("Calculation for {} took {}s", var1, var22);
         return var5;
      }), Util.backgroundExecutor());
   }

   protected abstract Codec<? extends ChunkGenerator> codec();

   public Optional<ResourceKey<Codec<? extends ChunkGenerator>>> getTypeNameForDataFixer() {
      return Registry.CHUNK_GENERATOR.getResourceKey(this.codec());
   }

   public CompletableFuture<ChunkAccess> createBiomes(Registry<Biome> var1, Executor var2, RandomState var3, Blender var4, StructureManager var5, ChunkAccess var6) {
      return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
         var6.fillBiomesFromNoise(this.biomeSource, var3.sampler());
         return var6;
      }), Util.backgroundExecutor());
   }

   public abstract void applyCarvers(WorldGenRegion var1, long var2, RandomState var4, BiomeManager var5, StructureManager var6, ChunkAccess var7, GenerationStep.Carving var8);

   @Nullable
   public Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel var1, HolderSet<Structure> var2, BlockPos var3, int var4, boolean var5) {
      Object2ObjectArrayMap var6 = new Object2ObjectArrayMap();
      Iterator var7 = var2.iterator();

      while(var7.hasNext()) {
         Holder var8 = (Holder)var7.next();
         Iterator var9 = this.getPlacementsForStructure(var8, var1.getChunkSource().randomState()).iterator();

         while(var9.hasNext()) {
            StructurePlacement var10 = (StructurePlacement)var9.next();
            ((Set)var6.computeIfAbsent(var10, (var0) -> {
               return new ObjectArraySet();
            })).add(var8);
         }
      }

      if (var6.isEmpty()) {
         return null;
      } else {
         Pair var22 = null;
         double var23 = 1.7976931348623157E308;
         StructureManager var24 = var1.structureManager();
         ArrayList var11 = new ArrayList(var6.size());
         Iterator var12 = var6.entrySet().iterator();

         while(var12.hasNext()) {
            Map.Entry var13 = (Map.Entry)var12.next();
            StructurePlacement var14 = (StructurePlacement)var13.getKey();
            if (var14 instanceof ConcentricRingsStructurePlacement) {
               ConcentricRingsStructurePlacement var15 = (ConcentricRingsStructurePlacement)var14;
               Pair var16 = this.getNearestGeneratedStructure((Set)var13.getValue(), var1, var24, var3, var5, var15);
               if (var16 != null) {
                  BlockPos var17 = (BlockPos)var16.getFirst();
                  double var18 = var3.distSqr(var17);
                  if (var18 < var23) {
                     var23 = var18;
                     var22 = var16;
                  }
               }
            } else if (var14 instanceof RandomSpreadStructurePlacement) {
               var11.add(var13);
            }
         }

         if (!var11.isEmpty()) {
            int var25 = SectionPos.blockToSectionCoord(var3.getX());
            int var26 = SectionPos.blockToSectionCoord(var3.getZ());

            for(int var27 = 0; var27 <= var4; ++var27) {
               boolean var28 = false;
               Iterator var29 = var11.iterator();

               while(var29.hasNext()) {
                  Map.Entry var30 = (Map.Entry)var29.next();
                  RandomSpreadStructurePlacement var31 = (RandomSpreadStructurePlacement)var30.getKey();
                  Pair var19 = getNearestGeneratedStructure((Set)var30.getValue(), var1, var24, var25, var26, var27, var5, var1.getSeed(), var31);
                  if (var19 != null) {
                     var28 = true;
                     double var20 = var3.distSqr((Vec3i)var19.getFirst());
                     if (var20 < var23) {
                        var23 = var20;
                        var22 = var19;
                     }
                  }
               }

               if (var28) {
                  return var22;
               }
            }
         }

         return var22;
      }
   }

   @Nullable
   private Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(Set<Holder<Structure>> var1, ServerLevel var2, StructureManager var3, BlockPos var4, boolean var5, ConcentricRingsStructurePlacement var6) {
      List var7 = this.getRingPositionsFor(var6, var2.getChunkSource().randomState());
      if (var7 == null) {
         throw new IllegalStateException("Somehow tried to find structures for a placement that doesn't exist");
      } else {
         Pair var8 = null;
         double var9 = 1.7976931348623157E308;
         BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
         Iterator var12 = var7.iterator();

         while(var12.hasNext()) {
            ChunkPos var13 = (ChunkPos)var12.next();
            var11.set(SectionPos.sectionToBlockCoord(var13.x, 8), 32, SectionPos.sectionToBlockCoord(var13.z, 8));
            double var14 = var11.distSqr(var4);
            boolean var16 = var8 == null || var14 < var9;
            if (var16) {
               Pair var17 = getStructureGeneratingAt(var1, var2, var3, var5, var6, var13);
               if (var17 != null) {
                  var8 = var17;
                  var9 = var14;
               }
            }
         }

         return var8;
      }
   }

   @Nullable
   private static Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(Set<Holder<Structure>> var0, LevelReader var1, StructureManager var2, int var3, int var4, int var5, boolean var6, long var7, RandomSpreadStructurePlacement var9) {
      int var10 = var9.spacing();

      for(int var11 = -var5; var11 <= var5; ++var11) {
         boolean var12 = var11 == -var5 || var11 == var5;

         for(int var13 = -var5; var13 <= var5; ++var13) {
            boolean var14 = var13 == -var5 || var13 == var5;
            if (var12 || var14) {
               int var15 = var3 + var10 * var11;
               int var16 = var4 + var10 * var13;
               ChunkPos var17 = var9.getPotentialStructureChunk(var7, var15, var16);
               Pair var18 = getStructureGeneratingAt(var0, var1, var2, var6, var9, var17);
               if (var18 != null) {
                  return var18;
               }
            }
         }
      }

      return null;
   }

   @Nullable
   private static Pair<BlockPos, Holder<Structure>> getStructureGeneratingAt(Set<Holder<Structure>> var0, LevelReader var1, StructureManager var2, boolean var3, StructurePlacement var4, ChunkPos var5) {
      Iterator var6 = var0.iterator();

      Holder var7;
      StructureStart var10;
      do {
         do {
            do {
               StructureCheckResult var8;
               do {
                  if (!var6.hasNext()) {
                     return null;
                  }

                  var7 = (Holder)var6.next();
                  var8 = var2.checkStructurePresence(var5, (Structure)var7.value(), var3);
               } while(var8 == StructureCheckResult.START_NOT_PRESENT);

               if (!var3 && var8 == StructureCheckResult.START_PRESENT) {
                  return Pair.of(var4.getLocatePos(var5), var7);
               }

               ChunkAccess var9 = var1.getChunk(var5.x, var5.z, ChunkStatus.STRUCTURE_STARTS);
               var10 = var2.getStartForStructure(SectionPos.bottomOf(var9), (Structure)var7.value(), var9);
            } while(var10 == null);
         } while(!var10.isValid());
      } while(var3 && !tryAddReference(var2, var10));

      return Pair.of(var4.getLocatePos(var10.getChunkPos()), var7);
   }

   private static boolean tryAddReference(StructureManager var0, StructureStart var1) {
      if (var1.canBeReferenced()) {
         var0.addReference(var1);
         return true;
      } else {
         return false;
      }
   }

   public void applyBiomeDecoration(WorldGenLevel var1, ChunkAccess var2, StructureManager var3) {
      ChunkPos var4 = var2.getPos();
      if (!SharedConstants.debugVoidTerrain(var4)) {
         SectionPos var5 = SectionPos.of(var4, var1.getMinSection());
         BlockPos var6 = var5.origin();
         Registry var7 = var1.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);
         Map var8 = (Map)var7.stream().collect(Collectors.groupingBy((var0) -> {
            return var0.step().ordinal();
         }));
         List var9 = (List)this.featuresPerStep.get();
         WorldgenRandom var10 = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
         long var11 = var10.setDecorationSeed(var1.getSeed(), var6.getX(), var6.getZ());
         ObjectArraySet var13 = new ObjectArraySet();
         ChunkPos.rangeClosed(var5.chunk(), 1).forEach((var2x) -> {
            ChunkAccess var3 = var1.getChunk(var2x.x, var2x.z);
            LevelChunkSection[] var4 = var3.getSections();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               LevelChunkSection var7 = var4[var6];
               PalettedContainerRO var10000 = var7.getBiomes();
               Objects.requireNonNull(var13);
               var10000.getAll(var13::add);
            }

         });
         var13.retainAll(this.biomeSource.possibleBiomes());
         int var14 = var9.size();

         try {
            Registry var15 = var1.registryAccess().registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
            int var32 = Math.max(GenerationStep.Decoration.values().length, var14);

            for(int var17 = 0; var17 < var32; ++var17) {
               int var18 = 0;
               CrashReportCategory var10000;
               Iterator var20;
               if (var3.shouldGenerateStructures()) {
                  List var19 = (List)var8.getOrDefault(var17, Collections.emptyList());

                  for(var20 = var19.iterator(); var20.hasNext(); ++var18) {
                     Structure var21 = (Structure)var20.next();
                     var10.setFeatureSeed(var11, var18, var17);
                     Supplier var22 = () -> {
                        Optional var10000 = var7.getResourceKey(var21).map(Object::toString);
                        Objects.requireNonNull(var21);
                        return (String)var10000.orElseGet(var21::toString);
                     };

                     try {
                        var1.setCurrentlyGenerating(var22);
                        var3.startsForStructure(var5, var21).forEach((var6x) -> {
                           var6x.placeInChunk(var1, var3, this, var10, getWritableArea(var2), var4);
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

               if (var17 < var14) {
                  IntArraySet var33 = new IntArraySet();
                  var20 = var13.iterator();

                  while(var20.hasNext()) {
                     Holder var35 = (Holder)var20.next();
                     List var37 = ((BiomeGenerationSettings)this.generationSettingsGetter.apply(var35)).features();
                     if (var17 < var37.size()) {
                        HolderSet var23 = (HolderSet)var37.get(var17);
                        FeatureSorter.StepFeatureData var40 = (FeatureSorter.StepFeatureData)var9.get(var17);
                        var23.stream().map(Holder::value).forEach((var2x) -> {
                           var33.add(var40.indexMapping().applyAsInt(var2x));
                        });
                     }
                  }

                  int var34 = var33.size();
                  int[] var36 = var33.toIntArray();
                  Arrays.sort(var36);
                  FeatureSorter.StepFeatureData var38 = (FeatureSorter.StepFeatureData)var9.get(var17);

                  for(int var39 = 0; var39 < var34; ++var39) {
                     int var41 = var36[var39];
                     PlacedFeature var25 = (PlacedFeature)var38.features().get(var41);
                     Supplier var26 = () -> {
                        Optional var10000 = var15.getResourceKey(var25).map(Object::toString);
                        Objects.requireNonNull(var25);
                        return (String)var10000.orElseGet(var25::toString);
                     };
                     var10.setFeatureSeed(var11, var41, var17);

                     try {
                        var1.setCurrentlyGenerating(var26);
                        var25.placeWithBiomeCheck(var1, this, var10, var6);
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
            CrashReport var16 = CrashReport.forThrowable(var31, "Biome decoration");
            var16.addCategory("Generation").setDetail("CenterX", (Object)var4.x).setDetail("CenterZ", (Object)var4.z).setDetail("Seed", (Object)var11);
            throw new ReportedException(var16);
         }
      }
   }

   public boolean hasStructureChunkInRange(Holder<StructureSet> var1, RandomState var2, long var3, int var5, int var6, int var7) {
      StructureSet var8 = (StructureSet)var1.value();
      if (var8 == null) {
         return false;
      } else {
         StructurePlacement var9 = var8.placement();

         for(int var10 = var5 - var7; var10 <= var5 + var7; ++var10) {
            for(int var11 = var6 - var7; var11 <= var6 + var7; ++var11) {
               if (var9.isStructureChunk(this, var2, var3, var10, var11)) {
                  return true;
               }
            }
         }

         return false;
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

   public abstract void buildSurface(WorldGenRegion var1, StructureManager var2, RandomState var3, ChunkAccess var4);

   public abstract void spawnOriginalMobs(WorldGenRegion var1);

   public int getSpawnHeight(LevelHeightAccessor var1) {
      return 64;
   }

   public BiomeSource getBiomeSource() {
      return this.biomeSource;
   }

   public abstract int getGenDepth();

   public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Holder<Biome> var1, StructureManager var2, MobCategory var3, BlockPos var4) {
      Map var5 = var2.getAllStructuresAt(var4);
      Iterator var6 = var5.entrySet().iterator();

      while(var6.hasNext()) {
         Map.Entry var7 = (Map.Entry)var6.next();
         Structure var8 = (Structure)var7.getKey();
         StructureSpawnOverride var9 = (StructureSpawnOverride)var8.spawnOverrides().get(var3);
         if (var9 != null) {
            MutableBoolean var10 = new MutableBoolean(false);
            Predicate var11 = var9.boundingBox() == StructureSpawnOverride.BoundingBoxType.PIECE ? (var2x) -> {
               return var2.structureHasPieceAt(var4, var2x);
            } : (var1x) -> {
               return var1x.getBoundingBox().isInside(var4);
            };
            var2.fillStartsForStructure(var8, (LongSet)var7.getValue(), (var2x) -> {
               if (var10.isFalse() && var11.test(var2x)) {
                  var10.setTrue();
               }

            });
            if (var10.isTrue()) {
               return var9.spawns();
            }
         }
      }

      return ((Biome)var1.value()).getMobSettings().getMobs(var3);
   }

   public void createStructures(RegistryAccess var1, RandomState var2, StructureManager var3, ChunkAccess var4, StructureTemplateManager var5, long var6) {
      ChunkPos var8 = var4.getPos();
      SectionPos var9 = SectionPos.bottomOf(var4);
      this.possibleStructureSets().forEach((var10) -> {
         StructurePlacement var11 = ((StructureSet)var10.value()).placement();
         List var12 = ((StructureSet)var10.value()).structures();
         Iterator var13 = var12.iterator();

         while(var13.hasNext()) {
            StructureSet.StructureSelectionEntry var14 = (StructureSet.StructureSelectionEntry)var13.next();
            StructureStart var15 = var3.getStartForStructure(var9, (Structure)var14.structure().value(), var4);
            if (var15 != null && var15.isValid()) {
               return;
            }
         }

         if (var11.isStructureChunk(this, var2, var6, var8.x, var8.z)) {
            if (var12.size() == 1) {
               this.tryGenerateStructure((StructureSet.StructureSelectionEntry)var12.get(0), var3, var1, var2, var5, var6, var4, var8, var9);
            } else {
               ArrayList var20 = new ArrayList(var12.size());
               var20.addAll(var12);
               WorldgenRandom var21 = new WorldgenRandom(new LegacyRandomSource(0L));
               var21.setLargeFeatureSeed(var6, var8.x, var8.z);
               int var22 = 0;

               StructureSet.StructureSelectionEntry var17;
               for(Iterator var16 = var20.iterator(); var16.hasNext(); var22 += var17.weight()) {
                  var17 = (StructureSet.StructureSelectionEntry)var16.next();
               }

               while(!var20.isEmpty()) {
                  int var23 = var21.nextInt(var22);
                  int var24 = 0;

                  for(Iterator var18 = var20.iterator(); var18.hasNext(); ++var24) {
                     StructureSet.StructureSelectionEntry var19 = (StructureSet.StructureSelectionEntry)var18.next();
                     var23 -= var19.weight();
                     if (var23 < 0) {
                        break;
                     }
                  }

                  StructureSet.StructureSelectionEntry var25 = (StructureSet.StructureSelectionEntry)var20.get(var24);
                  if (this.tryGenerateStructure(var25, var3, var1, var2, var5, var6, var4, var8, var9)) {
                     return;
                  }

                  var20.remove(var24);
                  var22 -= var25.weight();
               }

            }
         }
      });
   }

   private boolean tryGenerateStructure(StructureSet.StructureSelectionEntry var1, StructureManager var2, RegistryAccess var3, RandomState var4, StructureTemplateManager var5, long var6, ChunkAccess var8, ChunkPos var9, SectionPos var10) {
      Structure var11 = (Structure)var1.structure().value();
      int var12 = fetchReferences(var2, var8, var10, var11);
      HolderSet var13 = var11.biomes();
      Objects.requireNonNull(var13);
      Predicate var14 = var13::contains;
      StructureStart var15 = var11.generate(var3, this, this.biomeSource, var4, var5, var6, var9, var12, var8, var14);
      if (var15.isValid()) {
         var2.setStartForStructure(var10, var11, var15, var8);
         return true;
      } else {
         return false;
      }
   }

   private static int fetchReferences(StructureManager var0, ChunkAccess var1, SectionPos var2, Structure var3) {
      StructureStart var4 = var0.getStartForStructure(var2, var3, var1);
      return var4 != null ? var4.getReferences() : 0;
   }

   public void createReferences(WorldGenLevel var1, StructureManager var2, ChunkAccess var3) {
      boolean var4 = true;
      ChunkPos var5 = var3.getPos();
      int var6 = var5.x;
      int var7 = var5.z;
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
                     var2.addReferenceForStructure(var10, var16.getStructure(), var13, var3);
                     DebugPackets.sendStructurePacket(var1, var16);
                  }
               } catch (Exception var21) {
                  CrashReport var18 = CrashReport.forThrowable(var21, "Generating structure reference");
                  CrashReportCategory var19 = var18.addCategory("Structure");
                  Optional var20 = var1.registryAccess().registry(Registry.STRUCTURE_REGISTRY);
                  var19.setDetail("Id", () -> {
                     return (String)var20.map((var1) -> {
                        return var1.getKey(var16.getStructure()).toString();
                     }).orElse("UNKNOWN");
                  });
                  var19.setDetail("Name", () -> {
                     return Registry.STRUCTURE_TYPES.getKey(var16.getStructure().type()).toString();
                  });
                  var19.setDetail("Class", () -> {
                     return var16.getStructure().getClass().getCanonicalName();
                  });
                  throw new ReportedException(var18);
               }
            }
         }
      }

   }

   public abstract CompletableFuture<ChunkAccess> fillFromNoise(Executor var1, Blender var2, RandomState var3, StructureManager var4, ChunkAccess var5);

   public abstract int getSeaLevel();

   public abstract int getMinY();

   public abstract int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4, RandomState var5);

   public abstract NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3, RandomState var4);

   public int getFirstFreeHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4, RandomState var5) {
      return this.getBaseHeight(var1, var2, var3, var4, var5);
   }

   public int getFirstOccupiedHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4, RandomState var5) {
      return this.getBaseHeight(var1, var2, var3, var4, var5) - 1;
   }

   public void ensureStructuresGenerated(RandomState var1) {
      if (!this.hasGeneratedPositions) {
         this.generatePositions(var1);
         this.hasGeneratedPositions = true;
      }

   }

   @Nullable
   public List<ChunkPos> getRingPositionsFor(ConcentricRingsStructurePlacement var1, RandomState var2) {
      this.ensureStructuresGenerated(var2);
      CompletableFuture var3 = (CompletableFuture)this.ringPositions.get(var1);
      return var3 != null ? (List)var3.join() : null;
   }

   private List<StructurePlacement> getPlacementsForStructure(Holder<Structure> var1, RandomState var2) {
      this.ensureStructuresGenerated(var2);
      return (List)this.placementsForStructure.getOrDefault(var1.value(), List.of());
   }

   public abstract void addDebugScreenInfo(List<String> var1, RandomState var2, BlockPos var3);

   /** @deprecated */
   @Deprecated
   public BiomeGenerationSettings getBiomeGenerationSettings(Holder<Biome> var1) {
      return (BiomeGenerationSettings)this.generationSettingsGetter.apply(var1);
   }

   static {
      CODEC = Registry.CHUNK_GENERATOR.byNameCodec().dispatchStable(ChunkGenerator::codec, Function.identity());
   }
}
