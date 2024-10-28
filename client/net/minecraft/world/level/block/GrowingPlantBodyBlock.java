package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class GrowingPlantBodyBlock extends GrowingPlantBlock implements BonemealableBlock {
   protected GrowingPlantBodyBlock(BlockBehaviour.Properties var1, Direction var2, VoxelShape var3, boolean var4) {
      super(var1, var2, var3, var4);
   }

   protected abstract MapCodec<? extends GrowingPlantBodyBlock> codec();

   protected BlockState updateHeadAfterConvertedFromBody(BlockState var1, BlockState var2) {
      return var2;
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if (var5 == this.growthDirection.getOpposite() && !var1.canSurvive(var2, var4)) {
         var3.scheduleTick(var4, (Block)this, 1);
      }

      GrowingPlantHeadBlock var9 = this.getHeadBlock();
      if (var5 == this.growthDirection && !var7.is(this) && !var7.is(var9)) {
         return this.updateHeadAfterConvertedFromBody(var1, var9.getStateForPlacement(var8));
      } else {
         if (this.scheduleFluidTicks) {
            var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
         }

         return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return new ItemStack(this.getHeadBlock());
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      Optional var4 = this.getHeadPos(var1, var2, var3.getBlock());
      return var4.isPresent() && this.getHeadBlock().canGrowInto(var1.getBlockState(((BlockPos)var4.get()).relative(this.growthDirection)));
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      Optional var5 = this.getHeadPos(var1, var3, var4.getBlock());
      if (var5.isPresent()) {
         BlockState var6 = var1.getBlockState((BlockPos)var5.get());
         ((GrowingPlantHeadBlock)var6.getBlock()).performBonemeal(var1, var2, (BlockPos)var5.get(), var6);
      }

   }

   private Optional<BlockPos> getHeadPos(BlockGetter var1, BlockPos var2, Block var3) {
      return BlockUtil.getTopConnectedBlock(var1, var2, var3, this.growthDirection, this.getHeadBlock());
   }

   protected boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      boolean var3 = super.canBeReplaced(var1, var2);
      return var3 && var2.getItemInHand().is(this.getHeadBlock().asItem()) ? false : var3;
   }

   protected Block getBodyBlock() {
      return this;
   }
}
