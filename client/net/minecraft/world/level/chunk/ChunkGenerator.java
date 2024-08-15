package net.minecraft.world.level.chunk;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
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
import net.minecraft.world.level.chunk.status.ChunkStatus;
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

public abstract class ChunkGenerator {
   public static final Codec<ChunkGenerator> CODEC = BuiltInRegistries.CHUNK_GENERATOR.byNameCodec().dispatchStable(ChunkGenerator::codec, Function.identity());
   protected final BiomeSource biomeSource;
   private final Supplier<List<FeatureSorter.StepFeatureData>> featuresPerStep;
   private final Function<Holder<Biome>, BiomeGenerationSettings> generationSettingsGetter;

   public ChunkGenerator(BiomeSource var1) {
      this(var1, var0 -> var0.value().getGenerationSettings());
   }

   public ChunkGenerator(BiomeSource var1, Function<Holder<Biome>, BiomeGenerationSettings> var2) {
      super();
      this.biomeSource = var1;
      this.generationSettingsGetter = var2;
      this.featuresPerStep = Suppliers.memoize(
         () -> FeatureSorter.buildFeaturesPerStep(List.copyOf(var1.possibleBiomes()), var1xx -> ((BiomeGenerationSettings)var2.apply(var1xx)).features(), true)
      );
   }

   public void validate() {
      this.featuresPerStep.get();
   }

   protected abstract MapCodec<? extends ChunkGenerator> codec();

   public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> var1, RandomState var2, long var3) {
      return ChunkGeneratorStructureState.createForNormal(var2, var3, this.biomeSource, var1);
   }

   public Optional<ResourceKey<MapCodec<? extends ChunkGenerator>>> getTypeNameForDataFixer() {
      return BuiltInRegistries.CHUNK_GENERATOR.getResourceKey(this.codec());
   }

   public CompletableFuture<ChunkAccess> createBiomes(RandomState var1, Blender var2, StructureManager var3, ChunkAccess var4) {
      return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
         var4.fillBiomesFromNoise(this.biomeSource, var1.sampler());
         return var4;
      }), Util.backgroundExecutor());
   }

   public abstract void applyCarvers(WorldGenRegion var1, long var2, RandomState var4, BiomeManager var5, StructureManager var6, ChunkAccess var7);

   @Nullable
   public Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel var1, HolderSet<Structure> var2, BlockPos var3, int var4, boolean var5) {
      ChunkGeneratorStructureState var6 = var1.getChunkSource().getGeneratorState();
      Object2ObjectArrayMap var7 = new Object2ObjectArrayMap();

      for (Holder var9 : var2) {
         for (StructurePlacement var11 : var6.getPlacementsForStructure(var9)) {
            var7.computeIfAbsent(var11, var0 -> new ObjectArraySet()).add(var9);
         }
      }

      if (var7.isEmpty()) {
         return null;
      } else {
         Pair var23 = null;
         double var24 = 1.7976931348623157E308;
         StructureManager var25 = var1.structureManager();
         ArrayList var12 = new ArrayList(var7.size());

         for (Entry var14 : var7.entrySet()) {
            StructurePlacement var15 = (StructurePlacement)var14.getKey();
            if (var15 instanceof ConcentricRingsStructurePlacement) {
               ConcentricRingsStructurePlacement var16 = (ConcentricRingsStructurePlacement)var15;
               Pair var17 = this.getNearestGeneratedStructure((Set<Holder<Structure>>)var14.getValue(), var1, var25, var3, var5, var16);
               if (var17 != null) {
                  BlockPos var18 = (BlockPos)var17.getFirst();
                  double var19 = var3.distSqr(var18);
                  if (var19 < var24) {
                     var24 = var19;
                     var23 = var17;
                  }
               }
            } else if (var15 instanceof RandomSpreadStructurePlacement) {
               var12.add(var14);
            }
         }

         if (!var12.isEmpty()) {
            int var26 = SectionPos.blockToSectionCoord(var3.getX());
            int var27 = SectionPos.blockToSectionCoord(var3.getZ());

            for (int var28 = 0; var28 <= var4; var28++) {
               boolean var29 = false;

               for (Entry var31 : var12) {
                  RandomSpreadStructurePlacement var32 = (RandomSpreadStructurePlacement)var31.getKey();
                  Pair var20 = getNearestGeneratedStructure(
                     (Set<Holder<Structure>>)var31.getValue(), var1, var25, var26, var27, var28, var5, var6.getLevelSeed(), var32
                  );
                  if (var20 != null) {
                     var29 = true;
                     double var21 = var3.distSqr((Vec3i)var20.getFirst());
                     if (var21 < var24) {
                        var24 = var21;
                        var23 = var20;
                     }
                  }
               }

               if (var29) {
                  return var23;
               }
            }
         }

         return var23;
      }
   }

   @Nullable
   private Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(
      Set<Holder<Structure>> var1, ServerLevel var2, StructureManager var3, BlockPos var4, boolean var5, ConcentricRingsStructurePlacement var6
   ) {
      List var7 = var2.getChunkSource().getGeneratorState().getRingPositionsFor(var6);
      if (var7 == null) {
         throw new IllegalStateException("Somehow tried to find structures for a placement that doesn't exist");
      } else {
         Pair var8 = null;
         double var9 = 1.7976931348623157E308;
         BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

         for (ChunkPos var13 : var7) {
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
   private static Pair<BlockPos, Holder<Structure>> getNearestGeneratedStructure(
      Set<Holder<Structure>> var0,
      LevelReader var1,
      StructureManager var2,
      int var3,
      int var4,
      int var5,
      boolean var6,
      long var7,
      RandomSpreadStructurePlacement var9
   ) {
      int var10 = var9.spacing();

      for (int var11 = -var5; var11 <= var5; var11++) {
         boolean var12 = var11 == -var5 || var11 == var5;

         for (int var13 = -var5; var13 <= var5; var13++) {
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
   private static Pair<BlockPos, Holder<Structure>> getStructureGeneratingAt(
      Set<Holder<Structure>> var0, LevelReader var1, StructureManager var2, boolean var3, StructurePlacement var4, ChunkPos var5
   ) {
      for (Holder var7 : var0) {
         StructureCheckResult var8 = var2.checkStructurePresence(var5, (Structure)var7.value(), var4, var3);
         if (var8 != StructureCheckResult.START_NOT_PRESENT) {
            if (!var3 && var8 == StructureCheckResult.START_PRESENT) {
               return Pair.of(var4.getLocatePos(var5), var7);
            }

            ChunkAccess var9 = var1.getChunk(var5.x, var5.z, ChunkStatus.STRUCTURE_STARTS);
            StructureStart var10 = var2.getStartForStructure(SectionPos.bottomOf(var9), (Structure)var7.value(), var9);
            if (var10 != null && var10.isValid() && (!var3 || tryAddReference(var2, var10))) {
               return Pair.of(var4.getLocatePos(var10.getChunkPos()), var7);
            }
         }
      }

      return null;
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
         Registry var7 = var1.registryAccess().registryOrThrow(Registries.STRUCTURE);
         Map var8 = var7.stream().collect(Collectors.groupingBy(var0 -> var0.step().ordinal()));
         List var9 = this.featuresPerStep.get();
         WorldgenRandom var10 = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.generateUniqueSeed()));
         long var11 = var10.setDecorationSeed(var1.getSeed(), var6.getX(), var6.getZ());
         ObjectArraySet var13 = new ObjectArraySet();
         ChunkPos.rangeClosed(var5.chunk(), 1).forEach(var2x -> {
            ChunkAccess var3x = var1.getChunk(var2x.x, var2x.z);

            for (LevelChunkSection var7x : var3x.getSections()) {
               var7x.getBiomes().getAll(var13::add);
            }
         });
         var13.retainAll(this.biomeSource.possibleBiomes());
         int var14 = var9.size();

         try {
            Registry var15 = var1.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
            int var32 = Math.max(GenerationStep.Decoration.values().length, var14);

            for (int var17 = 0; var17 < var32; var17++) {
               int var18 = 0;
               if (var3.shouldGenerateStructures()) {
                  for (Structure var21 : var8.getOrDefault(var17, Collections.emptyList())) {
                     var10.setFeatureSeed(var11, var18, var17);
                     Supplier var22 = () -> var7.getResourceKey(var21).map(Object::toString).orElseGet(var21::toString);

                     try {
                        var1.setCurrentlyGenerating(var22);
                        var3.startsForStructure(var5, var21).forEach(var6x -> var6x.placeInChunk(var1, var3, this, var10, getWritableArea(var2), var4));
                     } catch (Exception var29) {
                        CrashReport var24 = CrashReport.forThrowable(var29, "Feature placement");
                        var24.addCategory("Feature").setDetail("Description", var22::get);
                        throw new ReportedException(var24);
                     }

                     var18++;
                  }
               }

               if (var17 < var14) {
                  IntArraySet var33 = new IntArraySet();

                  for (Holder var36 : var13) {
                     List var38 = this.generationSettingsGetter.apply(var36).features();
                     if (var17 < var38.size()) {
                        HolderSet var23 = (HolderSet)var38.get(var17);
                        FeatureSorter.StepFeatureData var41 = (FeatureSorter.StepFeatureData)var9.get(var17);
                        var23.stream().map(Holder::value).forEach(var2x -> var33.add(var41.indexMapping().applyAsInt(var2x)));
                     }
                  }

                  int var35 = var33.size();
                  int[] var37 = var33.toIntArray();
                  Arrays.sort(var37);
                  FeatureSorter.StepFeatureData var39 = (FeatureSorter.StepFeatureData)var9.get(var17);

                  for (int var40 = 0; var40 < var35; var40++) {
                     int var42 = var37[var40];
                     PlacedFeature var25 = var39.features().get(var42);
                     Supplier var26 = () -> var15.getResourceKey(var25).map(Object::toString).orElseGet(var25::toString);
                     var10.setFeatureSeed(var11, var42, var17);

                     try {
                        var1.setCurrentlyGenerating(var26);
                        var25.placeWithBiomeCheck(var1, this, var10, var6);
                     } catch (Exception var30) {
                        CrashReport var28 = CrashReport.forThrowable(var30, "Feature placement");
                        var28.addCategory("Feature").setDetail("Description", var26::get);
                        throw new ReportedException(var28);
                     }
                  }
               }
            }

            var1.setCurrentlyGenerating(null);
         } catch (Exception var31) {
            CrashReport var16 = CrashReport.forThrowable(var31, "Biome decoration");
            var16.addCategory("Generation").setDetail("CenterX", var4.x).setDetail("CenterZ", var4.z).setDetail("Decoration Seed", var11);
            throw new ReportedException(var16);
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

      for (Entry var7 : var5.entrySet()) {
         Structure var8 = (Structure)var7.getKey();
         StructureSpawnOverride var9 = var8.spawnOverrides().get(var3);
         if (var9 != null) {
            MutableBoolean var10 = new MutableBoolean(false);
            Predicate var11 = var9.boundingBox() == StructureSpawnOverride.BoundingBoxType.PIECE
               ? var2x -> var2.structureHasPieceAt(var4, var2x)
               : var1x -> var1x.getBoundingBox().isInside(var4);
            var2.fillStartsForStructure(var8, (LongSet)var7.getValue(), var2x -> {
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

   public void createStructures(RegistryAccess var1, ChunkGeneratorStructureState var2, StructureManager var3, ChunkAccess var4, StructureTemplateManager var5) {
      ChunkPos var6 = var4.getPos();
      SectionPos var7 = SectionPos.bottomOf(var4);
      RandomState var8 = var2.randomState();
      var2.possibleStructureSets().forEach(var9 -> {
         StructurePlacement var10 = var9.value().placement();
         List var11 = var9.value().structures();

         for (StructureSet.StructureSelectionEntry var13 : var11) {
            StructureStart var14 = var3.getStartForStructure(var7, var13.structure().value(), var4);
            if (var14 != null && var14.isValid()) {
               return;
            }
         }

         if (var10.isStructureChunk(var2, var6.x, var6.z)) {
            if (var11.size() == 1) {
               this.tryGenerateStructure((StructureSet.StructureSelectionEntry)var11.get(0), var3, var1, var8, var5, var2.getLevelSeed(), var4, var6, var7);
            } else {
               ArrayList var19 = new ArrayList(var11.size());
               var19.addAll(var11);
               WorldgenRandom var20 = new WorldgenRandom(new LegacyRandomSource(0L));
               var20.setLargeFeatureSeed(var2.getLevelSeed(), var6.x, var6.z);
               int var21 = 0;

               for (StructureSet.StructureSelectionEntry var16 : var19) {
                  var21 += var16.weight();
               }

               while (!var19.isEmpty()) {
                  int var22 = var20.nextInt(var21);
                  int var23 = 0;

                  for (StructureSet.StructureSelectionEntry var18 : var19) {
                     var22 -= var18.weight();
                     if (var22 < 0) {
                        break;
                     }

                     var23++;
                  }

                  StructureSet.StructureSelectionEntry var24 = (StructureSet.StructureSelectionEntry)var19.get(var23);
                  if (this.tryGenerateStructure(var24, var3, var1, var8, var5, var2.getLevelSeed(), var4, var6, var7)) {
                     return;
                  }

                  var19.remove(var23);
                  var21 -= var24.weight();
               }
            }
         }
      });
   }

   private boolean tryGenerateStructure(
      StructureSet.StructureSelectionEntry var1,
      StructureManager var2,
      RegistryAccess var3,
      RandomState var4,
      StructureTemplateManager var5,
      long var6,
      ChunkAccess var8,
      ChunkPos var9,
      SectionPos var10
   ) {
      Structure var11 = var1.structure().value();
      int var12 = fetchReferences(var2, var8, var10, var11);
      HolderSet var13 = var11.biomes();
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
      byte var4 = 8;
      ChunkPos var5 = var3.getPos();
      int var6 = var5.x;
      int var7 = var5.z;
      int var8 = var5.getMinBlockX();
      int var9 = var5.getMinBlockZ();
      SectionPos var10 = SectionPos.bottomOf(var3);

      for (int var11 = var6 - 8; var11 <= var6 + 8; var11++) {
         for (int var12 = var7 - 8; var12 <= var7 + 8; var12++) {
            long var13 = ChunkPos.asLong(var11, var12);

            for (StructureStart var16 : var1.getChunk(var11, var12).getAllStarts().values()) {
               try {
                  if (var16.isValid() && var16.getBoundingBox().intersects(var8, var9, var8 + 15, var9 + 15)) {
                     var2.addReferenceForStructure(var10, var16.getStructure(), var13, var3);
                     DebugPackets.sendStructurePacket(var1, var16);
                  }
               } catch (Exception var21) {
                  CrashReport var18 = CrashReport.forThrowable(var21, "Generating structure reference");
                  CrashReportCategory var19 = var18.addCategory("Structure");
                  Optional var20 = var1.registryAccess().registry(Registries.STRUCTURE);
                  var19.setDetail("Id", () -> var20.<String>map(var1xx -> var1xx.getKey(var16.getStructure()).toString()).orElse("UNKNOWN"));
                  var19.setDetail("Name", () -> BuiltInRegistries.STRUCTURE_TYPE.getKey(var16.getStructure().type()).toString());
                  var19.setDetail("Class", () -> var16.getStructure().getClass().getCanonicalName());
                  throw new ReportedException(var18);
               }
            }
         }
      }
   }

   public abstract CompletableFuture<ChunkAccess> fillFromNoise(Blender var1, RandomState var2, StructureManager var3, ChunkAccess var4);

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

   public abstract void addDebugScreenInfo(List<String> var1, RandomState var2, BlockPos var3);

   @Deprecated
   public BiomeGenerationSettings getBiomeGenerationSettings(Holder<Biome> var1) {
      return this.generationSettingsGetter.apply(var1);
   }
}
