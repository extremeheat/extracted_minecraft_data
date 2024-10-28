package net.minecraft.world.level.levelgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blending.Blender;

public class DebugLevelSource extends ChunkGenerator {
   public static final MapCodec<DebugLevelSource> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(RegistryOps.retrieveElement(Biomes.PLAINS)).apply(var0, var0.stable(DebugLevelSource::new));
   });
   private static final int BLOCK_MARGIN = 2;
   private static final List<BlockState> ALL_BLOCKS;
   private static final int GRID_WIDTH;
   private static final int GRID_HEIGHT;
   protected static final BlockState AIR;
   protected static final BlockState BARRIER;
   public static final int HEIGHT = 70;
   public static final int BARRIER_HEIGHT = 60;

   public DebugLevelSource(Holder.Reference<Biome> var1) {
      super(new FixedBiomeSource(var1));
   }

   protected MapCodec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public void buildSurface(WorldGenRegion var1, StructureManager var2, RandomState var3, ChunkAccess var4) {
   }

   public void applyBiomeDecoration(WorldGenLevel var1, ChunkAccess var2, StructureManager var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      ChunkPos var5 = var2.getPos();
      int var6 = var5.x;
      int var7 = var5.z;

      for(int var8 = 0; var8 < 16; ++var8) {
         for(int var9 = 0; var9 < 16; ++var9) {
            int var10 = SectionPos.sectionToBlockCoord(var6, var8);
            int var11 = SectionPos.sectionToBlockCoord(var7, var9);
            var1.setBlock(var4.set(var10, 60, var11), BARRIER, 2);
            BlockState var12 = getBlockStateFor(var10, var11);
            var1.setBlock(var4.set(var10, 70, var11), var12, 2);
         }
      }

   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Blender var1, RandomState var2, StructureManager var3, ChunkAccess var4) {
      return CompletableFuture.completedFuture(var4);
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3, LevelHeightAccessor var4, RandomState var5) {
      return 0;
   }

   public NoiseColumn getBaseColumn(int var1, int var2, LevelHeightAccessor var3, RandomState var4) {
      return new NoiseColumn(0, new BlockState[0]);
   }

   public void addDebugScreenInfo(List<String> var1, RandomState var2, BlockPos var3) {
   }

   public static BlockState getBlockStateFor(int var0, int var1) {
      BlockState var2 = AIR;
      if (var0 > 0 && var1 > 0 && var0 % 2 != 0 && var1 % 2 != 0) {
         var0 /= 2;
         var1 /= 2;
         if (var0 <= GRID_WIDTH && var1 <= GRID_HEIGHT) {
            int var3 = Mth.abs(var0 * GRID_WIDTH + var1);
            if (var3 < ALL_BLOCKS.size()) {
               var2 = (BlockState)ALL_BLOCKS.get(var3);
            }
         }
      }

      return var2;
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
      return 63;
   }

   static {
      ALL_BLOCKS = (List)StreamSupport.stream(BuiltInRegistries.BLOCK.spliterator(), false).flatMap((var0) -> {
         return var0.getStateDefinition().getPossibleStates().stream();
      }).collect(Collectors.toList());
      GRID_WIDTH = Mth.ceil(Mth.sqrt((float)ALL_BLOCKS.size()));
      GRID_HEIGHT = Mth.ceil((float)ALL_BLOCKS.size() / (float)GRID_WIDTH);
      AIR = Blocks.AIR.defaultBlockState();
      BARRIER = Blocks.BARRIER.defaultBlockState();
   }
}
