package net.minecraft.world.level.levelgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class FlatLevelSource extends ChunkGenerator {
   public static final MapCodec<FlatLevelSource> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(FlatLevelGeneratorSettings.CODEC.fieldOf("settings").forGetter(FlatLevelSource::settings)).apply(var0, var0.stable(FlatLevelSource::new));
   });
   private final FlatLevelGeneratorSettings settings;

   public FlatLevelSource(FlatLevelGeneratorSettings var1) {
      FixedBiomeSource var10001 = new FixedBiomeSource(var1.getBiome());
      Objects.requireNonNull(var1);
      super(var10001, Util.memoize(var1::adjustGenerationSettings));
      this.settings = var1;
   }

   public ChunkGeneratorStructureState createState(HolderLookup<StructureSet> var1, RandomState var2, long var3) {
      Stream var5 = (Stream)this.settings.structureOverrides().map(HolderSet::stream).orElseGet(() -> {
         return var1.listElements().map((var0) -> {
            return var0;
         });
      });
      return ChunkGeneratorStructureState.createForFlat(var2, var3, this.biomeSource, var5);
   }

   protected MapCodec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public FlatLevelGeneratorSettings settings() {
      return this.settings;
   }

   public void buildSurface(WorldGenRegion var1, StructureManager var2, RandomState var3, ChunkAccess var4) {
   }

   public int getSpawnHeight(LevelHeightAccessor var1) {
      return var1.getMinBuildHeight() + Math.min(var1.getHeight(), this.settings.getLayers().size());
   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Executor var1, Blender var2, RandomState var3, StructureManager var4, ChunkAccess var5) {
      List var6 = this.settings.getLayers();
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();
      Heightmap var8 = var5.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap var9 = var5.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);

      for(int var10 = 0; var10 < Math.min(var5.getHeight(), var6.size()); ++var10) {
         BlockState var11 = (BlockState)var6.get(var10);
         if (var11 != null) {
            int var12 = var5.getMinBuildHeight() + var10;

            for(int var13 = 0; var13 < 16; ++var13) {
               for(int var14 = 0; var14 < 16; ++var14) {
                  var5.setBlockState(var7.set(var13, var12, var14), var11, false);
                  var8.update(var13, var12, var14, var11);
                  var9.update(var13, var12, var14, var11);
               }
            }
         }
      }

      return CompletableFuture.completedFuture(var5);
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4, RandomState var5) {
      List var6 = this.settings.getLayers();

      for(int var7 = Math.min(var6.size(), var4.getMaxBuildHeight()) - 1; var7 >= 0; --var7) {
         BlockState var8 = (BlockState)var6.get(var7);
         if (var8 != null && var3.isOpaque().test(var8)) {
            return var4.getMinBuildHeight() + var7 + 1;
         }
      }

      return var4.getMinBuildHeight();
   }

   public NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3, RandomState var4) {
      return new NoiseColumn(var3.getMinBuildHeight(), (BlockState[])this.settings.getLayers().stream().limit((long)var3.getHeight()).map((var0) -> {
         return var0 == null ? Blocks.AIR.defaultBlockState() : var0;
      }).toArray((var0) -> {
         return new BlockState[var0];
      }));
   }

   public void addDebugScreenInfo(List<String> var1, RandomState var2, BlockPos var3) {
   }

   public void applyCarvers(WorldGenRegion var1, long var2, RandomState var4, BiomeManager var5, StructureManager var6, ChunkAccess var7, GenerationStep.Carving var8) {
   }

   public void spawnOriginalMobs(WorldGenRegion var1) {
   }

   public int getMinY() {
      return 0;
   }

   public int getGenDepth() {
      return 384;
   }

   public int getSeaLevel() {
      return -63;
   }
}
