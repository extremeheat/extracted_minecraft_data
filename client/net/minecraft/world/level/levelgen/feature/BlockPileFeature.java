package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public abstract class BlockPileFeature extends Feature<NoneFeatureConfiguration> {
   public BlockPileFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      if (var4.getY() < 5) {
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
               this.tryPlaceBlock(var1, var9, var3);
            } else if ((double)var3.nextFloat() < 0.031D) {
               this.tryPlaceBlock(var1, var9, var3);
            }
         }

         return true;
      }
   }

   private boolean mayPlaceOn(LevelAccessor var1, BlockPos var2, Random var3) {
      BlockPos var4 = var2.below();
      BlockState var5 = var1.getBlockState(var4);
      return var5.getBlock() == Blocks.GRASS_PATH ? var3.nextBoolean() : var5.isFaceSturdy(var1, var4, Direction.UP);
   }

   private void tryPlaceBlock(LevelAccessor var1, BlockPos var2, Random var3) {
      if (var1.isEmptyBlock(var2) && this.mayPlaceOn(var1, var2, var3)) {
         var1.setBlock(var2, this.getBlockState(var1), 4);
      }

   }

   protected abstract BlockState getBlockState(LevelAccessor var1);
}
