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
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public abstract class ChunkGenerator<C extends ChunkGeneratorSettings> {
   protected final LevelAccessor level;
   protected final long seed;
   protected final BiomeSource biomeSource;
   protected final C settings;

   public ChunkGenerator(LevelAccessor var1, BiomeSource var2, C var3) {
      super();
      this.level = var1;
      this.seed = var1.getSeed();
      this.biomeSource = var2;
      this.settings = var3;
   }

   public void createBiomes(ChunkAccess var1) {
      ChunkPos var2 = var1.getPos();
      int var3 = var2.x;
      int var4 = var2.z;
      Biome[] var5 = this.biomeSource.getBiomeBlock(var3 * 16, var4 * 16, 16, 16);
      var1.setBiomes(var5);
   }

   protected Biome getCarvingBiome(ChunkAccess var1) {
      return var1.getBiome(BlockPos.ZERO);
   }

   protected Biome getDecorationBiome(WorldGenRegion var1, BlockPos var2) {
      return this.biomeSource.getBiome(var2);
   }

   public void applyCarvers(ChunkAccess var1, GenerationStep.Carving var2) {
      WorldgenRandom var3 = new WorldgenRandom();
      boolean var4 = true;
      ChunkPos var5 = var1.getPos();
      int var6 = var5.x;
      int var7 = var5.z;
      BitSet var8 = var1.getCarvingMask(var2);

      for(int var9 = var6 - 8; var9 <= var6 + 8; ++var9) {
         for(int var10 = var7 - 8; var10 <= var7 + 8; ++var10) {
            List var11 = this.getCarvingBiome(var1).getCarvers(var2);
            ListIterator var12 = var11.listIterator();

            while(var12.hasNext()) {
               int var13 = var12.nextIndex();
               ConfiguredWorldCarver var14 = (ConfiguredWorldCarver)var12.next();
               var3.setLargeFeatureSeed(this.seed + (long)var13, var9, var10);
               if (var14.isStartChunk(var3, var9, var10)) {
                  var14.carve(var1, var3, this.getSeaLevel(), var9, var10, var6, var7, var8);
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
      Biome var7 = this.getDecorationBiome(var1, var6.offset(8, 8, 8));
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

   public abstract void buildSurfaceAndBedrock(ChunkAccess var1);

   public void spawnOriginalMobs(WorldGenRegion var1) {
   }

   public C getSettings() {
      return this.settings;
   }

   public abstract int getSpawnHeight();

   public void tickCustomSpawners(ServerLevel var1, boolean var2, boolean var3) {
   }

   public boolean isBiomeValidStartForStructure(Biome var1, StructureFeature<? extends FeatureConfiguration> var2) {
      return var1.isValidStart(var2);
   }

   @Nullable
   public <C extends FeatureConfiguration> C getStructureConfiguration(Biome var1, StructureFeature<C> var2) {
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

   public List<Biome.SpawnerData> getMobsAt(MobCategory var1, BlockPos var2) {
      return this.level.getBiome(var2).getMobs(var1);
   }

   public void createStructures(ChunkAccess var1, ChunkGenerator<?> var2, StructureManager var3) {
      Iterator var4 = Feature.STRUCTURES_REGISTRY.values().iterator();

      while(var4.hasNext()) {
         StructureFeature var5 = (StructureFeature)var4.next();
         if (var2.getBiomeSource().canGenerateStructure(var5)) {
            WorldgenRandom var6 = new WorldgenRandom();
            ChunkPos var7 = var1.getPos();
            StructureStart var8 = StructureStart.INVALID_START;
            if (var5.isFeatureChunk(var2, var6, var7.x, var7.z)) {
               Biome var9 = this.getBiomeSource().getBiome(new BlockPos(var7.getMinBlockX() + 9, 0, var7.getMinBlockZ() + 9));
               StructureStart var10 = var5.getStartFactory().create(var5, var7.x, var7.z, var9, BoundingBox.getUnknownBox(), 0, var2.getSeed());
               var10.generatePieces(this, var3, var7.x, var7.z, var9);
               var8 = var10.isValid() ? var10 : StructureStart.INVALID_START;
            }

            var1.setStartForFeature(var5.getFeatureName(), var8);
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
