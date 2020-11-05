package net.minecraft.world.level.chunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StrongholdConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public abstract class ChunkGenerator {
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
               if (var4.getGenerationSettings().isValidStart(StructureFeature.STRONGHOLD)) {
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
               BlockPos var16 = this.biomeSource.findBiomeHorizontal(SectionPos.sectionToBlockCoord(var14, 8), 0, SectionPos.sectionToBlockCoord(var15, 8), 112, var2::contains, var6);
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

   protected abstract Codec<? extends ChunkGenerator> codec();

   public abstract ChunkGenerator withSeed(long var1);

   public void createBiomes(Registry<Biome> var1, ChunkAccess var2) {
      ChunkPos var3 = var2.getPos();
      ((ProtoChunk)var2).setBiomes(new ChunkBiomeContainer(var1, var3, this.runtimeBiomeSource));
   }

   public void applyCarvers(long var1, BiomeManager var3, ChunkAccess var4, GenerationStep.Carving var5) {
      BiomeManager var6 = var3.withDifferentSource(this.biomeSource);
      WorldgenRandom var7 = new WorldgenRandom();
      boolean var8 = true;
      ChunkPos var9 = var4.getPos();
      int var10 = var9.x;
      int var11 = var9.z;
      BiomeGenerationSettings var12 = this.biomeSource.getNoiseBiome(var9.x << 2, 0, var9.z << 2).getGenerationSettings();
      BitSet var13 = ((ProtoChunk)var4).getOrCreateCarvingMask(var5);

      for(int var14 = var10 - 8; var14 <= var10 + 8; ++var14) {
         for(int var15 = var11 - 8; var15 <= var11 + 8; ++var15) {
            List var16 = var12.getCarvers(var5);
            ListIterator var17 = var16.listIterator();

            while(var17.hasNext()) {
               int var18 = var17.nextIndex();
               ConfiguredWorldCarver var19 = (ConfiguredWorldCarver)((Supplier)var17.next()).get();
               var7.setLargeFeatureSeed(var1 + (long)var18, var14, var15);
               if (var19.isStartChunk(var7, var14, var15)) {
                  var19.carve(var4, var6::getBiome, var7, this.getSeaLevel(), var14, var15, var10, var11, var13);
               }
            }
         }
      }

   }

   @Nullable
   public BlockPos findNearestMapFeature(ServerLevel var1, StructureFeature<?> var2, BlockPos var3, int var4, boolean var5) {
      if (!this.biomeSource.canGenerateStructure(var2)) {
         return null;
      } else if (var2 == StructureFeature.STRONGHOLD) {
         this.generateStrongholds();
         BlockPos var14 = null;
         double var7 = 1.7976931348623157E308D;
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();
         Iterator var10 = this.strongholdPositions.iterator();

         while(var10.hasNext()) {
            ChunkPos var11 = (ChunkPos)var10.next();
            var9.set(SectionPos.sectionToBlockCoord(var11.x, 8), 32, SectionPos.sectionToBlockCoord(var11.z, 8));
            double var12 = var9.distSqr(var3);
            if (var14 == null) {
               var14 = new BlockPos(var9);
               var7 = var12;
            } else if (var12 < var7) {
               var14 = new BlockPos(var9);
               var7 = var12;
            }
         }

         return var14;
      } else {
         StructureFeatureConfiguration var6 = this.settings.getConfig(var2);
         return var6 == null ? null : var2.getNearestGeneratedFeature(var1, var1.structureFeatureManager(), var3, var4, var5, var1.getSeed(), var6);
      }
   }

   public void applyBiomeDecoration(WorldGenRegion var1, StructureFeatureManager var2) {
      int var3 = var1.getCenterX();
      int var4 = var1.getCenterZ();
      int var5 = SectionPos.sectionToBlockCoord(var3);
      int var6 = SectionPos.sectionToBlockCoord(var4);
      BlockPos var7 = new BlockPos(var5, 0, var6);
      Biome var8 = this.biomeSource.getPrimaryBiome(var3, var4);
      WorldgenRandom var9 = new WorldgenRandom();
      long var10 = var9.setDecorationSeed(var1.getSeed(), var5, var6);

      try {
         var8.generate(var2, this, var1, var10, var9, var7);
      } catch (Exception var14) {
         CrashReport var13 = CrashReport.forThrowable(var14, "Biome decoration");
         var13.addCategory("Generation").setDetail("CenterX", (Object)var3).setDetail("CenterZ", (Object)var4).setDetail("Seed", (Object)var10).setDetail("Biome", (Object)var8);
         throw new ReportedException(var13);
      }
   }

   public abstract void buildSurfaceAndBedrock(WorldGenRegion var1, ChunkAccess var2);

   public void spawnOriginalMobs(WorldGenRegion var1) {
   }

   public StructureSettings getSettings() {
      return this.settings;
   }

   public int getSpawnHeight() {
      return 64;
   }

   public BiomeSource getBiomeSource() {
      return this.runtimeBiomeSource;
   }

   public int getGenDepth() {
      return 256;
   }

   public List<MobSpawnSettings.SpawnerData> getMobsAt(Biome var1, StructureFeatureManager var2, MobCategory var3, BlockPos var4) {
      return var1.getMobSettings().getMobs(var3);
   }

   public void createStructures(RegistryAccess var1, StructureFeatureManager var2, ChunkAccess var3, StructureManager var4, long var5) {
      ChunkPos var7 = var3.getPos();
      Biome var8 = this.biomeSource.getPrimaryBiome(var7.x, var7.z);
      this.createStructure(StructureFeatures.STRONGHOLD, var1, var2, var3, var4, var5, var7, var8);
      Iterator var9 = var8.getGenerationSettings().structures().iterator();

      while(var9.hasNext()) {
         Supplier var10 = (Supplier)var9.next();
         this.createStructure((ConfiguredStructureFeature)var10.get(), var1, var2, var3, var4, var5, var7, var8);
      }

   }

   private void createStructure(ConfiguredStructureFeature<?, ?> var1, RegistryAccess var2, StructureFeatureManager var3, ChunkAccess var4, StructureManager var5, long var6, ChunkPos var8, Biome var9) {
      StructureStart var10 = var3.getStartForFeature(SectionPos.of(var4.getPos(), 0), var1.feature, var4);
      int var11 = var10 != null ? var10.getReferences() : 0;
      StructureFeatureConfiguration var12 = this.settings.getConfig(var1.feature);
      if (var12 != null) {
         StructureStart var13 = var1.generate(var2, this, this.biomeSource, var5, var6, var8, var9, var11, var12);
         var3.setStartForFeature(SectionPos.of(var4.getPos(), 0), var1.feature, var13, var4);
      }

   }

   public void createReferences(WorldGenLevel var1, StructureFeatureManager var2, ChunkAccess var3) {
      boolean var4 = true;
      int var5 = var3.getPos().x;
      int var6 = var3.getPos().z;
      int var7 = SectionPos.sectionToBlockCoord(var5);
      int var8 = SectionPos.sectionToBlockCoord(var6);
      SectionPos var9 = SectionPos.of(var3.getPos(), 0);

      for(int var10 = var5 - 8; var10 <= var5 + 8; ++var10) {
         for(int var11 = var6 - 8; var11 <= var6 + 8; ++var11) {
            long var12 = ChunkPos.asLong(var10, var11);
            Iterator var14 = var1.getChunk(var10, var11).getAllStarts().values().iterator();

            while(var14.hasNext()) {
               StructureStart var15 = (StructureStart)var14.next();

               try {
                  if (var15 != StructureStart.INVALID_START && var15.getBoundingBox().intersects(var7, var8, var7 + 15, var8 + 15)) {
                     var2.addReferenceForFeature(var9, var15.getFeature(), var12, var3);
                     DebugPackets.sendStructurePacket(var1, var15);
                  }
               } catch (Exception var19) {
                  CrashReport var17 = CrashReport.forThrowable(var19, "Generating structure reference");
                  CrashReportCategory var18 = var17.addCategory("Structure");
                  var18.setDetail("Id", () -> {
                     return Registry.STRUCTURE_FEATURE.getKey(var15.getFeature()).toString();
                  });
                  var18.setDetail("Name", () -> {
                     return var15.getFeature().getFeatureName();
                  });
                  var18.setDetail("Class", () -> {
                     return var15.getFeature().getClass().getCanonicalName();
                  });
                  throw new ReportedException(var17);
               }
            }
         }
      }

   }

   public abstract void fillFromNoise(LevelAccessor var1, StructureFeatureManager var2, ChunkAccess var3);

   public int getSeaLevel() {
      return 63;
   }

   public abstract int getBaseHeight(int var1, int var2, Heightmap.Types var3);

   public abstract BlockGetter getBaseColumn(int var1, int var2);

   public int getFirstFreeHeight(int var1, int var2, Heightmap.Types var3) {
      return this.getBaseHeight(var1, var2, var3);
   }

   public int getFirstOccupiedHeight(int var1, int var2, Heightmap.Types var3) {
      return this.getBaseHeight(var1, var2, var3) - 1;
   }

   public boolean hasStronghold(ChunkPos var1) {
      this.generateStrongholds();
      return this.strongholdPositions.contains(var1);
   }

   static {
      Registry.register(Registry.CHUNK_GENERATOR, (String)"noise", NoiseBasedChunkGenerator.CODEC);
      Registry.register(Registry.CHUNK_GENERATOR, (String)"flat", FlatLevelSource.CODEC);
      Registry.register(Registry.CHUNK_GENERATOR, (String)"debug", DebugLevelSource.CODEC);
      CODEC = Registry.CHUNK_GENERATOR.dispatchStable(ChunkGenerator::codec, Function.identity());
   }
}
