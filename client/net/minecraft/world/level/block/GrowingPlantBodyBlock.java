package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class GrowingPlantBodyBlock extends GrowingPlantBlock implements BonemealableBlock {
   protected GrowingPlantBodyBlock(BlockBehaviour.Properties var1, Direction var2, VoxelShape var3, boolean var4) {
      super(var1, var2, var3, var4);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == this.growthDirection.getOpposite() && !var1.canSurvive(var4, var5)) {
         var4.getBlockTicks().scheduleTick(var5, this, 1);
      }

      GrowingPlantHeadBlock var7 = this.getHeadBlock();
      if (var2 == this.growthDirection) {
         Block var8 = var3.getBlock();
         if (var8 != this && var8 != var7) {
            return var7.getStateForPlacement(var4);
         }
      }

      if (this.scheduleFluidTicks) {
         var4.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return new ItemStack(this.getHeadBlock());
   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      Optional var5 = this.getHeadPos(var1, var2, var3);
      return var5.isPresent() && this.getHeadBlock().canGrowInto(var1.getBlockState(((BlockPos)var5.get()).relative(this.growthDirection)));
   }

   public boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, Random var2, BlockPos var3, BlockState var4) {
      Optional var5 = this.getHeadPos(var1, var3, var4);
      if (var5.isPresent()) {
         BlockState var6 = var1.getBlockState((BlockPos)var5.get());
         ((GrowingPlantHeadBlock)var6.getBlock()).performBonemeal(var1, var2, (BlockPos)var5.get(), var6);
      }

   }

   private Optional<BlockPos> getHeadPos(BlockGetter var1, BlockPos var2, BlockState var3) {
      BlockPos var4 = var2;

      Block var5;
      do {
         var4 = var4.relative(this.growthDirection);
         var5 = var1.getBlockState(var4).getBlock();
      } while(var5 == var3.getBlock());

      return var5 == this.getHeadBlock() ? Optional.of(var4) : Optional.empty();
   }

   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      boolean var3 = super.canBeReplaced(var1, var2);
      return var3 && var2.getItemInHand().getItem() == this.getHeadBlock().asItem() ? false : var3;
   }

   protected Block getBodyBlock() {
      return this;
   }
}
