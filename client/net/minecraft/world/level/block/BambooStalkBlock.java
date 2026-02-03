package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BambooStalkBlock extends Block implements BonemealableBlock {
   public static final MapCodec<BambooStalkBlock> CODEC = simpleCodec(BambooStalkBlock::new);
   private static final VoxelShape SHAPE_SMALL = Block.column(6.0, 0.0, 16.0);
   private static final VoxelShape SHAPE_LARGE = Block.column(10.0, 0.0, 16.0);
   private static final VoxelShape SHAPE_COLLISION = Block.column(3.0, 0.0, 16.0);
   public static final IntegerProperty AGE;
   public static final EnumProperty<BambooLeaves> LEAVES;
   public static final IntegerProperty STAGE;
   public static final int MAX_HEIGHT = 16;
   public static final int STAGE_GROWING = 0;
   public static final int STAGE_DONE_GROWING = 1;
   public static final int AGE_THIN_BAMBOO = 0;
   public static final int AGE_THICK_BAMBOO = 1;

   public MapCodec<BambooStalkBlock> codec() {
      return CODEC;
   }

   public BambooStalkBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0)).setValue(LEAVES, BambooLeaves.NONE)).setValue(STAGE, 0));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(AGE, LEAVES, STAGE);
   }

   protected boolean propagatesSkylightDown(final BlockState state) {
      return true;
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      VoxelShape shape = state.getValue(LEAVES) == BambooLeaves.LARGE ? SHAPE_LARGE : SHAPE_SMALL;
      return shape.move(state.getOffset(pos));
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE_COLLISION.move(state.getOffset(pos));
   }

   protected boolean isCollisionShapeFullBlock(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return false;
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
      if (!fluidState.isEmpty()) {
         return null;
      } else {
         BlockState belowState = context.getLevel().getBlockState(context.getClickedPos().below());
         if (belowState.is(BlockTags.SUPPORTS_BAMBOO)) {
            if (belowState.is(Blocks.BAMBOO_SAPLING)) {
               return (BlockState)this.defaultBlockState().setValue(AGE, 0);
            } else if (belowState.is(Blocks.BAMBOO)) {
               int age = (Integer)belowState.getValue(AGE) > 0 ? 1 : 0;
               return (BlockState)this.defaultBlockState().setValue(AGE, age);
            } else {
               BlockState aboveState = context.getLevel().getBlockState(context.getClickedPos().above());
               return aboveState.is(Blocks.BAMBOO) ? (BlockState)this.defaultBlockState().setValue(AGE, (Integer)aboveState.getValue(AGE)) : Blocks.BAMBOO_SAPLING.defaultBlockState();
            }
         } else {
            return null;
         }
      }
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (!state.canSurvive(level, pos)) {
         level.destroyBlock(pos, true);
      }

   }

   protected boolean isRandomlyTicking(final BlockState state) {
      return (Integer)state.getValue(STAGE) == 0;
   }

   protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if ((Integer)state.getValue(STAGE) == 0) {
         if (random.nextInt(3) == 0 && level.isEmptyBlock(pos.above()) && level.getRawBrightness(pos.above(), 0) >= 9) {
            int height = this.getHeightBelowUpToMax(level, pos) + 1;
            if (height < 16) {
               this.growBamboo(state, level, pos, random, height);
            }
         }

      }
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return level.getBlockState(pos.below()).is(BlockTags.SUPPORTS_BAMBOO);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (!state.canSurvive(level, pos)) {
         ticks.scheduleTick(pos, (Block)this, 1);
      }

      return directionToNeighbour == Direction.UP && neighbourState.is(Blocks.BAMBOO) && (Integer)neighbourState.getValue(AGE) > (Integer)state.getValue(AGE) ? (BlockState)state.cycle(AGE) : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state) {
      int heightAbove = this.getHeightAboveUpToMax(level, pos);
      int heightBelow = this.getHeightBelowUpToMax(level, pos);
      return heightAbove + heightBelow + 1 < 16 && (Integer)level.getBlockState(pos.above(heightAbove)).getValue(STAGE) != 1;
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state) {
      return true;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state) {
      int heightAbove = this.getHeightAboveUpToMax(level, pos);
      int heightBelow = this.getHeightBelowUpToMax(level, pos);
      int totalHeight = heightAbove + heightBelow + 1;
      int newBamboo = 1 + random.nextInt(2);

      for(int i = 0; i < newBamboo; ++i) {
         BlockPos topPos = pos.above(heightAbove);
         BlockState topState = level.getBlockState(topPos);
         if (totalHeight >= 16 || (Integer)topState.getValue(STAGE) == 1 || !level.isEmptyBlock(topPos.above())) {
            return;
         }

         this.growBamboo(topState, level, topPos, random, totalHeight);
         ++heightAbove;
         ++totalHeight;
      }

   }

   protected void growBamboo(final BlockState state, final Level level, final BlockPos pos, final RandomSource random, final int height) {
      BlockState belowState = level.getBlockState(pos.below());
      BlockPos twoBelowPos = pos.below(2);
      BlockState twoBelowState = level.getBlockState(twoBelowPos);
      BambooLeaves leaves = BambooLeaves.NONE;
      if (height >= 1) {
         if (belowState.is(Blocks.BAMBOO) && belowState.getValue(LEAVES) != BambooLeaves.NONE) {
            if (belowState.is(Blocks.BAMBOO) && belowState.getValue(LEAVES) != BambooLeaves.NONE) {
               leaves = BambooLeaves.LARGE;
               if (twoBelowState.is(Blocks.BAMBOO)) {
                  level.setBlock(pos.below(), (BlockState)belowState.setValue(LEAVES, BambooLeaves.SMALL), 3);
                  level.setBlock(twoBelowPos, (BlockState)twoBelowState.setValue(LEAVES, BambooLeaves.NONE), 3);
               }
            }
         } else {
            leaves = BambooLeaves.SMALL;
         }
      }

      int age = (Integer)state.getValue(AGE) != 1 && !twoBelowState.is(Blocks.BAMBOO) ? 0 : 1;
      int stage = (height < 11 || !(random.nextFloat() < 0.25F)) && height != 15 ? 0 : 1;
      level.setBlock(pos.above(), (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(AGE, age)).setValue(LEAVES, leaves)).setValue(STAGE, stage), 3);
   }

   protected int getHeightAboveUpToMax(final BlockGetter level, final BlockPos pos) {
      int height;
      for(height = 0; height < 16 && level.getBlockState(pos.above(height + 1)).is(Blocks.BAMBOO); ++height) {
      }

      return height;
   }

   protected int getHeightBelowUpToMax(final BlockGetter level, final BlockPos pos) {
      int height;
      for(height = 0; height < 16 && level.getBlockState(pos.below(height + 1)).is(Blocks.BAMBOO); ++height) {
      }

      return height;
   }

   static {
      AGE = BlockStateProperties.AGE_1;
      LEAVES = BlockStateProperties.BAMBOO_LEAVES;
      STAGE = BlockStateProperties.STAGE;
   }
}
