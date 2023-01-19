package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IronBarsBlock extends CrossCollisionBlock {
   protected IronBarsBlock(BlockBehaviour.Properties var1) {
      super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(NORTH, Boolean.valueOf(false))
            .setValue(EAST, Boolean.valueOf(false))
            .setValue(SOUTH, Boolean.valueOf(false))
            .setValue(WEST, Boolean.valueOf(false))
            .setValue(WATERLOGGED, Boolean.valueOf(false))
      );
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      FluidState var4 = var1.getLevel().getFluidState(var1.getClickedPos());
      BlockPos var5 = var3.north();
      BlockPos var6 = var3.south();
      BlockPos var7 = var3.west();
      BlockPos var8 = var3.east();
      BlockState var9 = var2.getBlockState(var5);
      BlockState var10 = var2.getBlockState(var6);
      BlockState var11 = var2.getBlockState(var7);
      BlockState var12 = var2.getBlockState(var8);
      return this.defaultBlockState()
         .setValue(NORTH, Boolean.valueOf(this.attachsTo(var9, var9.isFaceSturdy(var2, var5, Direction.SOUTH))))
         .setValue(SOUTH, Boolean.valueOf(this.attachsTo(var10, var10.isFaceSturdy(var2, var6, Direction.NORTH))))
         .setValue(WEST, Boolean.valueOf(this.attachsTo(var11, var11.isFaceSturdy(var2, var7, Direction.EAST))))
         .setValue(EAST, Boolean.valueOf(this.attachsTo(var12, var12.isFaceSturdy(var2, var8, Direction.WEST))))
         .setValue(WATERLOGGED, Boolean.valueOf(var4.getType() == Fluids.WATER));
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return var2.getAxis().isHorizontal()
         ? var1.setValue(PROPERTY_BY_DIRECTION.get(var2), Boolean.valueOf(this.attachsTo(var3, var3.isFaceSturdy(var4, var6, var2.getOpposite()))))
         : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public VoxelShape getVisualShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.empty();
   }

   @Override
   public boolean skipRendering(BlockState var1, BlockState var2, Direction var3) {
      if (var2.is(this)) {
         if (!var3.getAxis().isHorizontal()) {
            return true;
         }

         if (var1.getValue(PROPERTY_BY_DIRECTION.get(var3)) && var2.getValue(PROPERTY_BY_DIRECTION.get(var3.getOpposite()))) {
            return true;
         }
      }

      return super.skipRendering(var1, var2, var3);
   }

   public final boolean attachsTo(BlockState var1, boolean var2) {
      return !isExceptionForConnection(var1) && var2 || var1.getBlock() instanceof IronBarsBlock || var1.is(BlockTags.WALLS);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }
}
