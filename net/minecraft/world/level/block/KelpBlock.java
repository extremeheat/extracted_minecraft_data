package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KelpBlock extends Block implements LiquidBlockContainer {
   public static final IntegerProperty AGE;
   protected static final VoxelShape SHAPE;

   protected KelpBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      return var2.is(FluidTags.WATER) && var2.getAmount() == 8 ? this.getStateForPlacement((LevelAccessor)var1.getLevel()) : null;
   }

   public BlockState getStateForPlacement(LevelAccessor var1) {
      return (BlockState)this.defaultBlockState().setValue(AGE, var1.getRandom().nextInt(25));
   }

   public FluidState getFluidState(BlockState var1) {
      return Fluids.WATER.getSource(false);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (!var1.canSurvive(var2, var3)) {
         var2.destroyBlock(var3, true);
      } else {
         BlockPos var5 = var3.above();
         BlockState var6 = var2.getBlockState(var5);
         if (var6.getBlock() == Blocks.WATER && (Integer)var1.getValue(AGE) < 25 && var4.nextDouble() < 0.14D) {
            var2.setBlockAndUpdate(var5, (BlockState)var1.cycle(AGE));
         }

      }
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      BlockState var5 = var2.getBlockState(var4);
      Block var6 = var5.getBlock();
      if (var6 == Blocks.MAGMA_BLOCK) {
         return false;
      } else {
         return var6 == this || var6 == Blocks.KELP_PLANT || var5.isFaceSturdy(var2, var4, Direction.UP);
      }
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (!var1.canSurvive(var4, var5)) {
         if (var2 == Direction.DOWN) {
            return Blocks.AIR.defaultBlockState();
         }

         var4.getBlockTicks().scheduleTick(var5, this, 1);
      }

      if (var2 == Direction.UP && var3.getBlock() == this) {
         return Blocks.KELP_PLANT.defaultBlockState();
      } else {
         var4.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder var1) {
      var1.add(AGE);
   }

   public boolean canPlaceLiquid(BlockGetter var1, BlockPos var2, BlockState var3, Fluid var4) {
      return false;
   }

   public boolean placeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3, FluidState var4) {
      return false;
   }

   static {
      AGE = BlockStateProperties.AGE_25;
      SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
   }
}
