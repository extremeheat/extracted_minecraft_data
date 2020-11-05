package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LanternBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty HANGING;
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape AABB;
   protected static final VoxelShape HANGING_AABB;

   public LanternBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HANGING, false)).setValue(WATERLOGGED, false));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      Direction[] var3 = var1.getNearestLookingDirections();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction var6 = var3[var5];
         if (var6.getAxis() == Direction.Axis.Y) {
            BlockState var7 = (BlockState)this.defaultBlockState().setValue(HANGING, var6 == Direction.UP);
            if (var7.canSurvive(var1.getLevel(), var1.getClickedPos())) {
               return (BlockState)var7.setValue(WATERLOGGED, var2.getType() == Fluids.WATER);
            }
         }
      }

      return null;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (Boolean)var1.getValue(HANGING) ? HANGING_AABB : AABB;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HANGING, WATERLOGGED);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      Direction var4 = getConnectedDirection(var1).getOpposite();
      return Block.canSupportCenter(var2, var3.relative(var4), var4.getOpposite());
   }

   protected static Direction getConnectedDirection(BlockState var0) {
      return (Boolean)var0.getValue(HANGING) ? Direction.DOWN : Direction.UP;
   }

   public PushReaction getPistonPushReaction(BlockState var1) {
      return PushReaction.DESTROY;
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return getConnectedDirection(var1).getOpposite() == var2 && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      HANGING = BlockStateProperties.HANGING;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      AABB = Shapes.or(Block.box(5.0D, 0.0D, 5.0D, 11.0D, 7.0D, 11.0D), Block.box(6.0D, 7.0D, 6.0D, 10.0D, 9.0D, 10.0D));
      HANGING_AABB = Shapes.or(Block.box(5.0D, 1.0D, 5.0D, 11.0D, 8.0D, 11.0D), Block.box(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D));
   }
}
