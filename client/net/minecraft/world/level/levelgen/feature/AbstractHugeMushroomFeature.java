package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public abstract class AbstractHugeMushroomFeature extends Feature<HugeMushroomFeatureConfiguration> {
   public AbstractHugeMushroomFeature(Codec<HugeMushroomFeatureConfiguration> var1) {
      super(var1);
   }

   protected void placeTrunk(LevelAccessor var1, Random var2, BlockPos var3, HugeMushroomFeatureConfiguration var4, int var5, BlockPos.MutableBlockPos var6) {
      for(int var7 = 0; var7 < var5; ++var7) {
         var6.set(var3).move(Direction.UP, var7);
         if (!var1.getBlockState(var6).isSolidRender(var1, var6)) {
            this.setBlock(var1, var6, var4.stemProvider.getState(var2, var3));
         }
      }

   }

   protected int getTreeHeight(Random var1) {
      int var2 = var1.nextInt(3) + 4;
      if (var1.nextInt(12) == 0) {
         var2 *= 2;
      }

      return var2;
   }

   protected boolean isValidPosition(LevelAccessor var1, BlockPos var2, int var3, BlockPos.MutableBlockPos var4, HugeMushroomFeatureConfiguration var5) {
      int var6 = var2.getY();
      if (var6 >= 1 && var6 + var3 + 1 < 256) {
         Block var7 = var1.getBlockState(var2.below()).getBlock();
         if (!isDirt(var7) && !var7.is((Tag)BlockTags.MUSHROOM_GROW_BLOCK)) {
            return false;
         } else {
            for(int var8 = 0; var8 <= var3; ++var8) {
               int var9 = this.getTreeRadiusForHeight(-1, -1, var5.foliageRadius, var8);

               for(int var10 = -var9; var10 <= var9; ++var10) {
                  for(int var11 = -var9; var11 <= var9; ++var11) {
                     BlockState var12 = var1.getBlockState(var4.setWithOffset(var2, var10, var8, var11));
                     if (!var12.isAir() && !var12.is(BlockTags.LEAVES)) {
                        return false;
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, HugeMushroomFeatureConfiguration var5) {
      int var6 = this.getTreeHeight(var3);
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();
      if (!this.isValidPosition(var1, var4, var6, var7, var5)) {
         return false;
      } else {
         this.makeCap(var1, var3, var4, var6, var7, var5);
         this.placeTrunk(var1, var3, var4, var5, var6, var7);
         return true;
      }
   }

   protected abstract int getTreeRadiusForHeight(int var1, int var2, int var3, int var4);

   protected abstract void makeCap(LevelAccessor var1, Random var2, BlockPos var3, int var4, BlockPos.MutableBlockPos var5, HugeMushroomFeatureConfiguration var6);
}
