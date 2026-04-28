package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class SlabBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<SlabBlock> CODEC = simpleCodec(SlabBlock::new);
   public static final EnumProperty<SlabType> TYPE;
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape SHAPE_BOTTOM;
   private static final VoxelShape SHAPE_TOP;

   public MapCodec<? extends SlabBlock> codec() {
      return CODEC;
   }

   public SlabBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM)).setValue(WATERLOGGED, false));
   }

   protected boolean useShapeForLightOcclusion(final BlockState state) {
      return state.getValue(TYPE) != SlabType.DOUBLE;
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(TYPE, WATERLOGGED);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      VoxelShape var10000;
      switch ((SlabType)state.getValue(TYPE)) {
         case TOP -> var10000 = SHAPE_TOP;
         case BOTTOM -> var10000 = SHAPE_BOTTOM;
         case DOUBLE -> var10000 = Shapes.block();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockPos pos = context.getClickedPos();
      BlockState replacedBlockState = context.getLevel().getBlockState(pos);
      if (replacedBlockState.is(this)) {
         return (BlockState)((BlockState)replacedBlockState.setValue(TYPE, SlabType.DOUBLE)).setValue(WATERLOGGED, false);
      } else {
         FluidState replacedFluidState = context.getLevel().getFluidState(pos);
         BlockState result = (BlockState)((BlockState)this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM)).setValue(WATERLOGGED, replacedFluidState.is(Fluids.WATER));
         Direction clickedFace = context.getClickedFace();
         return clickedFace != Direction.DOWN && (clickedFace == Direction.UP || !(context.getClickLocation().y - (double)pos.getY() > 0.5)) ? result : (BlockState)result.setValue(TYPE, SlabType.TOP);
      }
   }

   protected boolean canBeReplaced(final BlockState state, final BlockPlaceContext context) {
      ItemStack itemStack = context.getItemInHand();
      SlabType type = (SlabType)state.getValue(TYPE);
      if (type != SlabType.DOUBLE && itemStack.is(this.asItem())) {
         if (context.replacingClickedOnBlock()) {
            boolean above = context.getClickLocation().y - (double)context.getClickedPos().getY() > 0.5;
            Direction clickedFace = context.getClickedFace();
            if (type == SlabType.BOTTOM) {
               return clickedFace == Direction.UP || above && clickedFace.getAxis().isHorizontal();
            } else {
               return clickedFace == Direction.DOWN || !above && clickedFace.getAxis().isHorizontal();
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public boolean placeLiquid(final LevelAccessor level, final BlockPos pos, final BlockState state, final FluidState fluidState) {
      return state.getValue(TYPE) != SlabType.DOUBLE ? SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState) : false;
   }

   public boolean canPlaceLiquid(final @Nullable LivingEntity user, final BlockGetter level, final BlockPos pos, final BlockState state, final Fluid type) {
      return state.getValue(TYPE) != SlabType.DOUBLE ? SimpleWaterloggedBlock.super.canPlaceLiquid(user, level, pos, state, type) : false;
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      boolean var10000;
      switch (type) {
         case LAND -> var10000 = false;
         case WATER -> var10000 = state.getFluidState().is(FluidTags.WATER);
         case AIR -> var10000 = false;
         default -> var10000 = false;
      }

      return var10000;
   }

   static {
      TYPE = BlockStateProperties.SLAB_TYPE;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE_BOTTOM = Block.column(16.0, 0.0, 8.0);
      SHAPE_TOP = Block.column(16.0, 8.0, 16.0);
   }
}
