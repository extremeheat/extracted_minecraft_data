package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrapDoorBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final int AABB_THICKNESS = 3;
   protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
   protected static final VoxelShape WEST_OPEN_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
   protected static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
   protected static final VoxelShape TOP_AABB = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);

   protected TrapDoorBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(OPEN, Boolean.valueOf(false))
            .setValue(HALF, Half.BOTTOM)
            .setValue(POWERED, Boolean.valueOf(false))
            .setValue(WATERLOGGED, Boolean.valueOf(false))
      );
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (!var1.getValue(OPEN)) {
         return var1.getValue(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
      } else {
         switch((Direction)var1.getValue(FACING)) {
            case NORTH:
            default:
               return NORTH_OPEN_AABB;
            case SOUTH:
               return SOUTH_OPEN_AABB;
            case WEST:
               return WEST_OPEN_AABB;
            case EAST:
               return EAST_OPEN_AABB;
         }
      }
   }

   @Override
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      switch(var4) {
         case LAND:
            return var1.getValue(OPEN);
         case WATER:
            return var1.getValue(WATERLOGGED);
         case AIR:
            return var1.getValue(OPEN);
         default:
            return false;
      }
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (this.material == Material.METAL) {
         return InteractionResult.PASS;
      } else {
         var1 = var1.cycle(OPEN);
         var2.setBlock(var3, var1, 2);
         if (var1.getValue(WATERLOGGED)) {
            var2.scheduleTick(var3, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
         }

         this.playSound(var4, var2, var3, var1.getValue(OPEN));
         return InteractionResult.sidedSuccess(var2.isClientSide);
      }
   }

   protected void playSound(@Nullable Player var1, Level var2, BlockPos var3, boolean var4) {
      if (var4) {
         int var5 = this.material == Material.METAL ? 1037 : 1007;
         var2.levelEvent(var1, var5, var3, 0);
      } else {
         int var6 = this.material == Material.METAL ? 1036 : 1013;
         var2.levelEvent(var1, var6, var3, 0);
      }

      var2.gameEvent(var1, var4 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, var3);
   }

   @Override
   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         boolean var7 = var2.hasNeighborSignal(var3);
         if (var7 != var1.getValue(POWERED)) {
            if (var1.getValue(OPEN) != var7) {
               var1 = var1.setValue(OPEN, Boolean.valueOf(var7));
               this.playSound(null, var2, var3, var7);
            }

            var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(var7)), 2);
            if (var1.getValue(WATERLOGGED)) {
               var2.scheduleTick(var3, Fluids.WATER, Fluids.WATER.getTickDelay(var2));
            }
         }
      }
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = this.defaultBlockState();
      FluidState var3 = var1.getLevel().getFluidState(var1.getClickedPos());
      Direction var4 = var1.getClickedFace();
      if (!var1.replacingClickedOnBlock() && var4.getAxis().isHorizontal()) {
         var2 = var2.setValue(FACING, var4).setValue(HALF, var1.getClickLocation().y - (double)var1.getClickedPos().getY() > 0.5 ? Half.TOP : Half.BOTTOM);
      } else {
         var2 = var2.setValue(FACING, var1.getHorizontalDirection().getOpposite()).setValue(HALF, var4 == Direction.UP ? Half.BOTTOM : Half.TOP);
      }

      if (var1.getLevel().hasNeighborSignal(var1.getClickedPos())) {
         var2 = var2.setValue(OPEN, Boolean.valueOf(true)).setValue(POWERED, Boolean.valueOf(true));
      }

      return var2.setValue(WATERLOGGED, Boolean.valueOf(var3.getType() == Fluids.WATER));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, OPEN, HALF, POWERED, WATERLOGGED);
   }

   @Override
   public FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }
}
