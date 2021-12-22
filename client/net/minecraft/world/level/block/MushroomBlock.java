package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MushroomBlock extends BushBlock implements BonemealableBlock {
   protected static final float AABB_OFFSET = 3.0F;
   protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
   private final Supplier<ConfiguredFeature<?, ?>> featureSupplier;

   public MushroomBlock(BlockBehaviour.Properties var1, Supplier<ConfiguredFeature<?, ?>> var2) {
      super(var1);
      this.featureSupplier = var2;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (var4.nextInt(25) == 0) {
         int var5 = 5;
         boolean var6 = true;
         Iterator var7 = BlockPos.betweenClosed(var3.offset(-4, -1, -4), var3.offset(4, 1, 4)).iterator();

         while(var7.hasNext()) {
            BlockPos var8 = (BlockPos)var7.next();
            if (var2.getBlockState(var8).is(this)) {
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
      if (var5.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
         return true;
      } else {
         return var2.getRawBrightness(var3, 0) < 13 && this.mayPlaceOn(var5, var2, var4);
      }
   }

   public boolean growMushroom(ServerLevel var1, BlockPos var2, BlockState var3, Random var4) {
      var1.removeBlock(var2, false);
      if (((ConfiguredFeature)this.featureSupplier.get()).place(var1, var1.getChunkSource().getGenerator(), var4, var2)) {
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

   public void performBonemeal(ServerLevel var1, Random var2, BlockPos var3, BlockState var4) {
      this.growMushroom(var1, var3, var4, var2);
   }
}
