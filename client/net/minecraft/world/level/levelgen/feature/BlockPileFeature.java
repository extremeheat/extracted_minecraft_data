package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;

public class BlockPileFeature extends Feature<BlockPileConfiguration> {
   public BlockPileFeature(Codec<BlockPileConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, BlockPileConfiguration var5) {
      if (var4.getY() < var1.getMinBuildHeight() + 5) {
         return false;
      } else {
         int var6 = 2 + var3.nextInt(2);
         int var7 = 2 + var3.nextInt(2);
         Iterator var8 = BlockPos.betweenClosed(var4.offset(-var6, 0, -var7), var4.offset(var6, 1, var7)).iterator();

         while(var8.hasNext()) {
            BlockPos var9 = (BlockPos)var8.next();
            int var10 = var4.getX() - var9.getX();
            int var11 = var4.getZ() - var9.getZ();
            if ((float)(var10 * var10 + var11 * var11) <= var3.nextFloat() * 10.0F - var3.nextFloat() * 6.0F) {
               this.tryPlaceBlock(var1, var9, var3, var5);
            } else if ((double)var3.nextFloat() < 0.031D) {
               this.tryPlaceBlock(var1, var9, var3, var5);
            }
         }

         return true;
      }
   }

   private boolean mayPlaceOn(LevelAccessor var1, BlockPos var2, Random var3) {
      BlockPos var4 = var2.below();
      BlockState var5 = var1.getBlockState(var4);
      return var5.is(Blocks.DIRT_PATH) ? var3.nextBoolean() : var5.isFaceSturdy(var1, var4, Direction.UP);
   }

   private void tryPlaceBlock(LevelAccessor var1, BlockPos var2, Random var3, BlockPileConfiguration var4) {
      if (var1.isEmptyBlock(var2) && this.mayPlaceOn(var1, var2, var3)) {
         var1.setBlock(var2, var4.stateProvider.getState(var3, var2), 4);
      }

   }
}
