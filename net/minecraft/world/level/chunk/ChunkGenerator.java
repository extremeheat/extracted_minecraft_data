package net.minecraft.world.level.chunk;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public abstract class ChunkGenerator {
   protected final LevelAccessor level;
   protected final long seed;
   protected final BiomeSource biomeSource;
   protected final ChunkGeneratorSettings settings;

   public ChunkGenerator(LevelAccessor var1, BiomeSource var2, ChunkGeneratorSettings var3) {
      this.level = var1;
      this.seed = var1.getSeed();
      this.biomeSource = var2;
      this.settings = var3;
   }

   public void createBiomes(ChunkAccess var1) {
      ChunkPos var2 = var1.getPos();
      ((ProtoChunk)var1).setBiomes(new ChunkBiomeContainer(var2, this.biomeSource));
   }

   protected Biome getCarvingOrDecorationBiome(BiomeManager var1, BlockPos var2) {
      return var1.getBiome(var2);
   }

   public void applyCarvers(BiomeManager var1, ChunkAccess var2, GenerationStep.Carving var3) {
      WorldgenRandom var4 = new WorldgenRandom();
      boolean var5 = true;
      ChunkPos var6 = var2.getPos();
      int var7 = var6.x;
      int var8 = var6.z;
      Biome var9 = this.getCarvingOrDecorationBiome(var1, var6.getWorldPosition());
      BitSet var10 = var2.getCarvingMask(var3);

      for(int var11 = var7 - 8; var11 <= var7 + 8; ++var11) {
         for(int var12 = var8 - 8; var12 <= var8 + 8; ++var12) {
            List var13 = var9.getCarvers(var3);
            ListIterator var14 = var13.listIterator();

            while(var14.hasNext()) {
               int var15 = var14.nextIndex();
               ConfiguredWorldCarver var16 = (ConfiguredWorldCarver)var14.next();
               var4.setLargeFeatureSeed(this.seed + (long)var15, var11, var12);
               if (var16.isStartChunk(var4, var11, var12)) {
                  var16.carve(var2, (var2x) -> {
                     return this.getCarvingOrDecorationBiome(var1, var2x);
                  }, var4, this.getSeaLevel(), var11, var12, var7, var8, var10);
               }
            }
         }
      }

   }

   @Nullable
   public BlockPos findNearestMapFeature(Level var1, String var2, BlockPos var3, int var4, boolean var5) {
      StructureFeature var6 = (StructureFeature)Feature.STRUCTURES_REGISTRY.get(var2.toLowerCase(Locale.ROOT));
      return var6 != null ? var6.getNearestGeneratedFeature(var1, this, var3, var4, var5) : null;
   }

   public void applyBiomeDecoration(WorldGenRegion var1) {
      int var2 = var1.getCenterX();
      int var3 = var1.getCenterZ();
      int var4 = var2 * 16;
      int var5 = var3 * 16;
      BlockPos var6 = new BlockPos(var4, 0, var5);
      Biome var7 = this.getCarvingOrDecorationBiome(var1.getBiomeManager(), var6.offset(8, 8, 8));
      WorldgenRandom var8 = new WorldgenRandom();
      long var9 = var8.setDecorationSeed(var1.getSeed(), var4, var5);
      GenerationStep.Decoration[] var11 = GenerationStep.Decoration.values();
      int var12 = var11.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         GenerationStep.Decoration var14 = var11[var13];

         try {
            var7.generate(var14, this, var1, var9, var8, var6);
         } catch (Exception var17) {
            CrashReport var16 = CrashReport.forThrowable(var17, "Biome decoration");
            var16.addCategory("Generation").setDetail("CenterX", (Object)var2).setDetail("CenterZ", (Object)var3).setDetail("Step", (Object)var14).setDetail("Seed", (Object)var9).setDetail("Biome", (Object)Registry.BIOME.getKey(var7));
            throw new ReportedException(var16);
         }
      }

   }

   public abstract void buildSurfaceAndBedrock(WorldGenRegion var1, ChunkAccess var2);

   public void spawnOriginalMobs(WorldGenRegion var1) {
   }

   public ChunkGeneratorSettings getSettings() {
      return this.settings;
   }

   public abstract int getSpawnHeight();

   public void tickCustomSpawners(ServerLevel var1, boolean var2, boolean var3) {
   }

   public boolean isBiomeValidStartForStructure(Biome var1, StructureFeature var2) {
      return var1.isValidStart(var2);
   }

   @Nullable
   public FeatureConfiguration getStructureConfiguration(Biome var1, StructureFeature var2) {
      return var1.getStructureConfiguration(var2);
   }

   public BiomeSource getBiomeSource() {
      return this.biomeSource;
   }

   public long getSeed() {
      return this.seed;
   }

   public int getGenDepth() {
      return 256;
   }

   public List getMobsAt(MobCategory var1, BlockPos var2) {
      return this.level.getBiome(var2).getMobs(var1);
   }

   public void createStructures(BiomeManager var1, ChunkAccess var2, ChunkGenerator var3, StructureManager var4) {
      Iterator var5 = Feature.STRUCTURES_REGISTRY.values().iterator();

      while(var5.hasNext()) {
         StructureFeature var6 = (StructureFeature)var5.next();
         if (var3.getBiomeSource().canGenerateStructure(var6)) {
            StructureStart var7 = var2.getStartForFeature(var6.getFeatureName());
            int var8 = var7 != null ? var7.getReferences() : 0;
            WorldgenRandom var9 = new WorldgenRandom();
            ChunkPos var10 = var2.getPos();
            StructureStart var11 = StructureStart.INVALID_START;
            Biome var12 = var1.getBiome(new BlockPos(var10.getMinBlockX() + 9, 0, var10.getMinBlockZ() + 9));
            if (var6.isFeatureChunk(var1, var3, var9, var10.x, var10.z, var12)) {
               StructureStart var13 = var6.getStartFactory().create(var6, var10.x, var10.z, BoundingBox.getUnknownBox(), var8, var3.getSeed());
               var13.generatePieces(this, var4, var10.x, var10.z, var12);
               var11 = var13.isValid() ? var13 : StructureStart.INVALID_START;
            }

            var2.setStartForFeature(var6.getFeatureName(), var11);
         }
      }

   }

   public void createReferences(LevelAccessor var1, ChunkAccess var2) {
      boolean var3 = true;
      int var4 = var2.getPos().x;
      int var5 = var2.getPos().z;
      int var6 = var4 << 4;
      int var7 = var5 << 4;

      for(int var8 = var4 - 8; var8 <= var4 + 8; ++var8) {
         for(int var9 = var5 - 8; var9 <= var5 + 8; ++var9) {
            long var10 = ChunkPos.asLong(var8, var9);
            Iterator var12 = var1.getChunk(var8, var9).getAllStarts().entrySet().iterator();

            while(var12.hasNext()) {
               Entry var13 = (Entry)var12.next();
               StructureStart var14 = (StructureStart)var13.getValue();
               if (var14 != StructureStart.INVALID_START && var14.getBoundingBox().intersects(var6, var7, var6 + 15, var7 + 15)) {
                  var2.addReferenceForFeature((String)var13.getKey(), var10);
                  DebugPackets.sendStructurePacket(var1, var14);
               }
            }
         }
      }

   }

   public abstract void fillFromNoise(LevelAccessor var1, ChunkAccess var2);

   public int getSeaLevel() {
      return 63;
   }

   public abstract int getBaseHeight(int var1, int var2, Heightmap.Types var3);

   public int getFirstFreeHeight(int var1, int var2, Heightmap.Types var3) {
      return this.getBaseHeight(var1, var2, var3);
   }

   public int getFirstOccupiedHeight(int var1, int var2, Heightmap.Types var3) {
      return this.getBaseHeight(var1, var2, var3) - 1;
   }
}
