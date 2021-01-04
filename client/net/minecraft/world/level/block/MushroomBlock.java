package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeMushroomFeatureConfig;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MushroomBlock extends BushBlock implements BonemealableBlock {
   protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

   public MushroomBlock(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public void tick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if (var4.nextInt(25) == 0) {
         int var5 = 5;
         boolean var6 = true;
         Iterator var7 = BlockPos.betweenClosed(var3.offset(-4, -1, -4), var3.offset(4, 1, 4)).iterator();

         while(var7.hasNext()) {
            BlockPos var8 = (BlockPos)var7.next();
            if (var2.getBlockState(var8).getBlock() == this) {
               --var5;
               if (var5 <= 0) {
                  return;
               }
            }
         }

         BlockPos var9 = var3.offset(var4.nextInt(3) - 1, var4.nextInt(2) - var4.nextInt(2), var4.nextInt(3) - 1);

         for(int var10 = 0; var10 < 4; ++var10) {
            if (var2.isEmptyBlock(var9) && var1.canSurvive(var2, var9)) {
               var3 = var9;
            }

            var9 = var3.offset(var4.nextInt(3) - 1, var4.nextInt(2) - var4.nextInt(2), var4.nextInt(3) - 1);
         }

         if (var2.isEmptyBlock(var9) && var1.canSurvive(var2, var9)) {
            var2.setBlock(var9, var1, 2);
         }
      }

   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.isSolidRender(var2, var3);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      BlockState var5 = var2.getBlockState(var4);
      Block var6 = var5.getBlock();
      if (var6 != Blocks.MYCELIUM && var6 != Blocks.PODZOL) {
         return var2.getRawBrightness(var3, 0) < 13 && this.mayPlaceOn(var5, var2, var4);
      } else {
         return true;
      }
   }

   public boolean growMushroom(LevelAccessor var1, BlockPos var2, BlockState var3, Random var4) {
      var1.removeBlock(var2, false);
      Feature var5 = null;
      if (this == Blocks.BROWN_MUSHROOM) {
         var5 = Feature.HUGE_BROWN_MUSHROOM;
      } else if (this == Blocks.RED_MUSHROOM) {
         var5 = Feature.HUGE_RED_MUSHROOM;
      }

      if (var5 != null && var5.place(var1, var1.getChunkSource().getGenerator(), var4, var2, new HugeMushroomFeatureConfig(true))) {
         return true;
      } else {
         var1.setBlock(var2, var3, 3);
         return false;
      }
   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      return true;
   }

   public boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4) {
      return (double)var2.nextFloat() < 0.4D;
   }

   public void performBonemeal(Level var1, Random var2, BlockPos var3, BlockState var4) {
      this.growMushroom(var1, var3, var4, var2);
   }

   public boolean hasPostProcess(BlockState var1, BlockGetter var2, BlockPos var3) {
      return true;
   }
}
