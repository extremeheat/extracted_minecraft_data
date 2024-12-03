package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LanternBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<LanternBlock> CODEC = simpleCodec(LanternBlock::new);
   public static final BooleanProperty HANGING;
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape AABB;
   protected static final VoxelShape HANGING_AABB;

   public MapCodec<LanternBlock> codec() {
      return CODEC;
   }

   public LanternBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HANGING, false)).setValue(WATERLOGGED, false));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());

      for(Direction var6 : var1.getNearestLookingDirections()) {
         if (var6.getAxis() == Direction.Axis.Y) {
            BlockState var7 = (BlockState)this.defaultBlockState().setValue(HANGING, var6 == Direction.UP);
            if (var7.canSurvive(var1.getLevel(), var1.getClickedPos())) {
               return (BlockState)var7.setValue(WATERLOGGED, var2.getType() == Fluids.WATER);
            }
         }
      }

      return null;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (Boolean)var1.getValue(HANGING) ? HANGING_AABB : AABB;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HANGING, WATERLOGGED);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      Direction var4 = getConnectedDirection(var1).getOpposite();
      return Block.canSupportCenter(var2, var3.relative(var4), var4.getOpposite());
   }

   protected static Direction getConnectedDirection(BlockState var0) {
      return (Boolean)var0.getValue(HANGING) ? Direction.DOWN : Direction.UP;
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return getConnectedDirection(var1).getOpposite() == var5 && !var1.canSurvive(var2, var4) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   static {
      HANGING = BlockStateProperties.HANGING;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      AABB = Shapes.or(Block.box(5.0, 0.0, 5.0, 11.0, 7.0, 11.0), Block.box(6.0, 7.0, 6.0, 10.0, 9.0, 10.0));
      HANGING_AABB = Shapes.or(Block.box(5.0, 1.0, 5.0, 11.0, 8.0, 11.0), Block.box(6.0, 8.0, 6.0, 10.0, 10.0, 10.0));
   }
}
