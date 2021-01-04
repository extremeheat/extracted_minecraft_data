package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class DoublePlantBlock extends BushBlock {
   public static final EnumProperty<DoubleBlockHalf> HALF;

   public DoublePlantBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER));
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      DoubleBlockHalf var7 = (DoubleBlockHalf)var1.getValue(HALF);
      if (var2.getAxis() == Direction.Axis.Y && var7 == DoubleBlockHalf.LOWER == (var2 == Direction.UP) && (var3.getBlock() != this || var3.getValue(HALF) == var7)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         return var7 == DoubleBlockHalf.LOWER && var2 == Direction.DOWN && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockPos var2 = var1.getClickedPos();
      return var2.getY() < 255 && var1.getLevel().getBlockState(var2.above()).canBeReplaced(var1) ? super.getStateForPlacement(var1) : null;
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      var1.setBlock(var2.above(), (BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER), 3);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      if (var1.getValue(HALF) != DoubleBlockHalf.UPPER) {
         return super.canSurvive(var1, var2, var3);
      } else {
         BlockState var4 = var2.getBlockState(var3.below());
         return var4.getBlock() == this && var4.getValue(HALF) == DoubleBlockHalf.LOWER;
      }
   }

   public void placeAt(LevelAccessor var1, BlockPos var2, int var3) {
      var1.setBlock(var2, (BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER), var3);
      var1.setBlock(var2.above(), (BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER), var3);
   }

   public void playerDestroy(Level var1, Player var2, BlockPos var3, BlockState var4, @Nullable BlockEntity var5, ItemStack var6) {
      super.playerDestroy(var1, var2, var3, Blocks.AIR.defaultBlockState(), var5, var6);
   }

   public void playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      DoubleBlockHalf var5 = (DoubleBlockHalf)var3.getValue(HALF);
      BlockPos var6 = var5 == DoubleBlockHalf.LOWER ? var2.above() : var2.below();
      BlockState var7 = var1.getBlockState(var6);
      if (var7.getBlock() == this && var7.getValue(HALF) != var5) {
         var1.setBlock(var6, Blocks.AIR.defaultBlockState(), 35);
         var1.levelEvent(var4, 2001, var6, Block.getId(var7));
         if (!var1.isClientSide && !var4.isCreative()) {
            dropResources(var3, var1, var2, (BlockEntity)null, var4, var4.getMainHandItem());
            dropResources(var7, var1, var6, (BlockEntity)null, var4, var4.getMainHandItem());
         }
      }

      super.playerWillDestroy(var1, var2, var3, var4);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HALF);
   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XZ;
   }

   public long getSeed(BlockState var1, BlockPos var2) {
      return Mth.getSeed(var2.getX(), var2.below(var1.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), var2.getZ());
   }

   static {
      HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   }
}
