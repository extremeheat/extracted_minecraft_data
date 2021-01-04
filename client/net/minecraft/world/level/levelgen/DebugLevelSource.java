package net.minecraft.world.level.levelgen;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class DebugLevelSource extends ChunkGenerator<DebugGeneratorSettings> {
   private static final List<BlockState> ALL_BLOCKS;
   private static final int GRID_WIDTH;
   private static final int GRID_HEIGHT;
   protected static final BlockState AIR;
   protected static final BlockState BARRIER;

   public DebugLevelSource(LevelAccessor var1, BiomeSource var2, DebugGeneratorSettings var3) {
      super(var1, var2, var3);
   }

   public void buildSurfaceAndBedrock(ChunkAccess var1) {
   }

   public void applyCarvers(ChunkAccess var1, GenerationStep.Carving var2) {
   }

   public int getSpawnHeight() {
      return this.level.getSeaLevel() + 1;
   }

   public void applyBiomeDecoration(WorldGenRegion var1) {
      BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
      int var3 = var1.getCenterX();
      int var4 = var1.getCenterZ();

      for(int var5 = 0; var5 < 16; ++var5) {
         for(int var6 = 0; var6 < 16; ++var6) {
            int var7 = (var3 << 4) + var5;
            int var8 = (var4 << 4) + var6;
            var1.setBlock(var2.set(var7, 60, var8), BARRIER, 2);
            BlockState var9 = getBlockStateFor(var7, var8);
            if (var9 != null) {
               var1.setBlock(var2.set(var7, 70, var8), var9, 2);
            }
         }
      }

   }

   public void fillFromNoise(LevelAccessor var1, ChunkAccess var2) {
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types var3) {
      return 0;
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

   static {
      ALL_BLOCKS = (List)StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap((var0) -> {
         return var0.getStateDefinition().getPossibleStates().stream();
      }).collect(Collectors.toList());
      GRID_WIDTH = Mth.ceil(Mth.sqrt((float)ALL_BLOCKS.size()));
      GRID_HEIGHT = Mth.ceil((float)ALL_BLOCKS.size() / (float)GRID_WIDTH);
      AIR = Blocks.AIR.defaultBlockState();
      BARRIER = Blocks.BARRIER.defaultBlockState();
   }
}
