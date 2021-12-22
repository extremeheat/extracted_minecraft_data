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
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;

public class BlockPileFeature extends Feature<BlockPileConfiguration> {
   public BlockPileFeature(Codec<BlockPileConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<BlockPileConfiguration> var1) {
      BlockPos var2 = var1.origin();
      WorldGenLevel var3 = var1.level();
      Random var4 = var1.random();
      BlockPileConfiguration var5 = (BlockPileConfiguration)var1.config();
      if (var2.getY() < var3.getMinBuildHeight() + 5) {
         return false;
      } else {
         int var6 = 2 + var4.nextInt(2);
         int var7 = 2 + var4.nextInt(2);
         Iterator var8 = BlockPos.betweenClosed(var2.offset(-var6, 0, -var7), var2.offset(var6, 1, var7)).iterator();

         while(var8.hasNext()) {
            BlockPos var9 = (BlockPos)var8.next();
            int var10 = var2.getX() - var9.getX();
            int var11 = var2.getZ() - var9.getZ();
            if ((float)(var10 * var10 + var11 * var11) <= var4.nextFloat() * 10.0F - var4.nextFloat() * 6.0F) {
               this.tryPlaceBlock(var3, var9, var4, var5);
            } else if ((double)var4.nextFloat() < 0.031D) {
               this.tryPlaceBlock(var3, var9, var4, var5);
            }
         }

         return true;
      }
   }

   private boolean mayPlaceOn(LevelAccessor var1, BlockPos var2, Random var3) {
      BlockPos var4 = var2.below();
      BlockState var5 = var1.getBlockState(var4);
      return var5.is(Blocks.DIRT_PATH) ? var3.nextBoolean() : var5.isFaceSturdy(var1, var4, Direction.field_526);
   }

   private void tryPlaceBlock(LevelAccessor var1, BlockPos var2, Random var3, BlockPileConfiguration var4) {
      if (var1.isEmptyBlock(var2) && this.mayPlaceOn(var1, var2, var3)) {
         var1.setBlock(var2, var4.stateProvider.getState(var3, var2), 4);
      }

   }
}
