package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CactusBlock extends Block {
   public static final IntegerProperty AGE;
   public static final int MAX_AGE = 15;
   private static final VoxelShape SHAPE;
   private static final VoxelShape SHAPE_COLLISION;
   private static final int MAX_CACTUS_GROWING_HEIGHT = 3;
   private static final int ATTEMPT_GROW_CACTUS_FLOWER_AGE = 8;
   private static final double ATTEMPT_GROW_CACTUS_FLOWER_SMALL_CACTUS_CHANCE = 0.1;
   private static final double ATTEMPT_GROW_CACTUS_FLOWER_TALL_CACTUS_CHANCE = 0.25;

   protected CactusBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (!state.canSurvive(level, pos)) {
         level.destroyBlock(pos, true);
      }

   }

   protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      BlockPos above = pos.above();
      if (level.isEmptyBlock(above)) {
         int height = 1;
         int age = (Integer)state.getValue(AGE);

         while(level.getBlockState(pos.below(height)).is(this)) {
            ++height;
            if (height == 3 && age == 15) {
               return;
            }
         }

         if (age == 8 && this.canSurvive(this.defaultBlockState(), level, pos.above())) {
            double chanceToGrowFlower = height >= 3 ? 0.25 : 0.1;
            if (random.nextDouble() <= chanceToGrowFlower) {
               level.setBlockAndUpdate(above, Blocks.CACTUS_FLOWER.defaultBlockState());
            }
         } else if (age == 15 && height < 3) {
            level.setBlockAndUpdate(above, this.defaultBlockState());
            BlockState aboveBlock = (BlockState)state.setValue(AGE, 0);
            level.setBlock(pos, aboveBlock, 260);
            level.neighborChanged(aboveBlock, above, this, (Orientation)null, false);
         }

         if (age < 15) {
            level.setBlock(pos, (BlockState)state.setValue(AGE, age + 1), 260);
         }

      }
   }

   protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE_COLLISION;
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (!state.canSurvive(level, pos)) {
         ticks.scheduleTick(pos, (Block)this, 1);
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockState neighbor = level.getBlockState(pos.relative(direction));
         if (neighbor.isSolid() || level.getFluidState(pos.relative(direction)).is(FluidTags.LAVA)) {
            return false;
         }
      }

      BlockState belowState = level.getBlockState(pos.below());
      return (belowState.is(this) || belowState.is(BlockTags.SUPPORTS_CACTUS)) && !level.getBlockState(pos.above()).liquid();
   }

   protected void entityInside(final BlockState state, final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier, final boolean isPrecise) {
      entity.hurt(level.damageSources().cactus(), 1.0F);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(AGE);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   static {
      AGE = BlockStateProperties.AGE_15;
      SHAPE = Block.column(14.0, 0.0, 16.0);
      SHAPE_COLLISION = Block.column(14.0, 0.0, 15.0);
   }
}
