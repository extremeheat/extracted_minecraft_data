package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
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
import org.jspecify.annotations.Nullable;

public class LanternBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<LanternBlock> CODEC = simpleCodec(LanternBlock::new);
   public static final BooleanProperty HANGING;
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape SHAPE_STANDING;
   private static final VoxelShape SHAPE_HANGING;

   public MapCodec<? extends LanternBlock> codec() {
      return CODEC;
   }

   public LanternBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HANGING, false)).setValue(WATERLOGGED, false));
   }

   public @Nullable BlockState getStateForPlacement(BlockPlaceContext var1) {
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
      return (Boolean)var1.getValue(HANGING) ? SHAPE_HANGING : SHAPE_STANDING;
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
      SHAPE_STANDING = Shapes.or(Block.column(4.0, 7.0, 9.0), Block.column(6.0, 0.0, 7.0));
      SHAPE_HANGING = SHAPE_STANDING.move(0.0, 0.0625, 0.0).optimize();
   }
}
