package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public abstract class SpeleothemBlock extends Block implements SimpleWaterloggedBlock, Fallable {
   public static final EnumProperty<Direction> TIP_DIRECTION;
   public static final EnumProperty<SpeleothemThickness> THICKNESS;
   public static final BooleanProperty WATERLOGGED;
   private static final int DELAY_BEFORE_FALLING = 2;
   private static final double MIN_TRIDENT_VELOCITY_TO_BREAK_SPELEOTHEM = 0.6;
   private static final float STALACTITE_DAMAGE_PER_FALL_DISTANCE_AND_SIZE = 1.0F;
   private static final int STALACTITE_MAX_DAMAGE = 40;
   private static final int MAX_STALACTITE_HEIGHT_FOR_DAMAGE_CALCULATION = 6;
   private static final VoxelShape SHAPE_TIP_MERGE;
   private static final VoxelShape SHAPE_TIP_UP;
   protected static final VoxelShape SHAPE_TIP_DOWN;
   private static final VoxelShape SHAPE_FRUSTUM;
   private static final VoxelShape SHAPE_MIDDLE;
   private static final VoxelShape SHAPE_BASE;
   private static final float MAX_HORIZONTAL_OFFSET;
   private static final float AVERAGE_DAYS_PER_GROWTH = 5.0F;
   private static final float GROWTH_PROBABILITY_PER_RANDOM_TICK = 0.011377778F;
   private static final int MAX_GROWTH_LENGTH = 7;
   private static final int MAX_STALAGMITE_SEARCH_RANGE_WHEN_GROWING = 10;
   protected final BlockState blockToGrowOn;

   public abstract MapCodec<? extends SpeleothemBlock> codec();

   public SpeleothemBlock(final BlockState blockToGrowOn, final BlockBehaviour.Properties properties) {
      super(properties);
      this.blockToGrowOn = blockToGrowOn;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(TIP_DIRECTION, Direction.UP)).setValue(THICKNESS, SpeleothemThickness.TIP)).setValue(WATERLOGGED, false));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(TIP_DIRECTION, THICKNESS, WATERLOGGED);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return this.isValidSpeleothemPlacement(level, pos, (Direction)state.getValue(TIP_DIRECTION));
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      if (directionToNeighbour != Direction.UP && directionToNeighbour != Direction.DOWN) {
         return state;
      } else {
         Direction tipDirection = (Direction)state.getValue(TIP_DIRECTION);
         if (tipDirection == Direction.DOWN && ticks.getBlockTicks().hasScheduledTick(pos, this)) {
            return state;
         } else if (directionToNeighbour == tipDirection.getOpposite() && !this.canSurvive(state, level, pos)) {
            if (tipDirection == Direction.DOWN) {
               ticks.scheduleTick(pos, (Block)this, 2);
            } else {
               ticks.scheduleTick(pos, (Block)this, 1);
            }

            return state;
         } else {
            boolean mergeOpposingTips = state.getValue(THICKNESS) == SpeleothemThickness.TIP_MERGE;
            SpeleothemThickness newThickness = this.calculateSpeleothemThickness(level, pos, tipDirection, mergeOpposingTips);
            return (BlockState)state.setValue(THICKNESS, newThickness);
         }
      }
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      LevelAccessor level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      Direction defaultTipDirection = context.getNearestLookingVerticalDirection().getOpposite();
      Direction tipDirection = this.calculateTipDirection(level, pos, defaultTipDirection);
      if (tipDirection == null) {
         return null;
      } else {
         boolean mergeOpposingTips = !context.isSecondaryUseActive();
         SpeleothemThickness thickness = this.calculateSpeleothemThickness(level, pos, tipDirection, mergeOpposingTips);
         return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(TIP_DIRECTION, tipDirection)).setValue(THICKNESS, thickness)).setValue(WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER));
      }
   }

   private @Nullable Direction calculateTipDirection(final LevelReader level, final BlockPos pos, final Direction defaultTipDirection) {
      Direction tipDirection;
      if (this.isValidSpeleothemPlacement(level, pos, defaultTipDirection)) {
         tipDirection = defaultTipDirection;
      } else {
         if (!this.isValidSpeleothemPlacement(level, pos, defaultTipDirection.getOpposite())) {
            return null;
         }

         tipDirection = defaultTipDirection.getOpposite();
      }

      return tipDirection;
   }

   private SpeleothemThickness calculateSpeleothemThickness(final LevelReader level, final BlockPos pos, final Direction tipDirection, final boolean mergeOpposingTips) {
      Direction baseDirection = tipDirection.getOpposite();
      BlockState inFrontState = level.getBlockState(pos.relative(tipDirection));
      if (isSpeleothemWithDirection(inFrontState, baseDirection) && inFrontState.is(this)) {
         return !mergeOpposingTips && inFrontState.getValue(THICKNESS) != SpeleothemThickness.TIP_MERGE ? SpeleothemThickness.TIP : SpeleothemThickness.TIP_MERGE;
      } else if (!isSpeleothemWithDirection(inFrontState, tipDirection)) {
         return SpeleothemThickness.TIP;
      } else {
         SpeleothemThickness inFrontThickness = (SpeleothemThickness)inFrontState.getValue(THICKNESS);
         if (inFrontThickness != SpeleothemThickness.TIP && inFrontThickness != SpeleothemThickness.TIP_MERGE) {
            BlockState behindState = level.getBlockState(pos.relative(baseDirection));
            return !isSpeleothemWithDirection(behindState, tipDirection) ? SpeleothemThickness.BASE : SpeleothemThickness.MIDDLE;
         } else {
            return SpeleothemThickness.FRUSTUM;
         }
      }
   }

   private boolean isValidSpeleothemPlacement(final LevelReader level, final BlockPos pos, final Direction tipDirection) {
      BlockPos behindPos = pos.relative(tipDirection.getOpposite());
      BlockState behindState = level.getBlockState(behindPos);
      return behindState.isFaceSturdy(level, behindPos, tipDirection) || isSpeleothemWithDirection(behindState, tipDirection) && behindState.is(this);
   }

   private static boolean isSpeleothemWithDirection(final BlockState blockState, final Direction tipDirection) {
      return blockState.is(BlockTags.SPELEOTHEMS) && blockState.getValue(TIP_DIRECTION) == tipDirection;
   }

   protected void onProjectileHit(final Level level, final BlockState state, final BlockHitResult blockHit, final Projectile projectile) {
      if (!level.isClientSide()) {
         BlockPos blockPos = blockHit.getBlockPos();
         if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            if (projectile.mayInteract(serverLevel, blockPos) && projectile.mayBreak(serverLevel) && projectile instanceof ThrownTrident && projectile.getDeltaMovement().length() > 0.6) {
               level.destroyBlock(blockPos, true);
            }
         }

      }
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (isStalagmite(state) && !this.canSurvive(state, level, pos)) {
         level.destroyBlock(pos, true);
      } else {
         spawnFallingStalactite(state, level, pos);
      }

   }

   private static void spawnFallingStalactite(final BlockState state, final ServerLevel level, final BlockPos pos) {
      BlockPos.MutableBlockPos fallPos = pos.mutable();

      for(BlockState fallState = state; isStalactite(fallState); fallState = level.getBlockState(fallPos)) {
         FallingBlockEntity entity = FallingBlockEntity.fall(level, fallPos, fallState);
         if (isTip(fallState, true)) {
            int size = Math.max(1 + pos.getY() - fallPos.getY(), 6);
            float damagePerFallDistance = 1.0F * (float)size;
            entity.setHurtsEntities(damagePerFallDistance, 40);
            break;
         }

         fallPos.move(Direction.DOWN);
      }

   }

   private static boolean isStalagmite(final BlockState state) {
      return isSpeleothemWithDirection(state, Direction.UP);
   }

   protected static boolean isStalactite(final BlockState state) {
      return isSpeleothemWithDirection(state, Direction.DOWN);
   }

   private static boolean isTip(final BlockState state, final boolean includeMergedTip) {
      if (!state.is(BlockTags.SPELEOTHEMS)) {
         return false;
      } else {
         SpeleothemThickness thickness = (SpeleothemThickness)state.getValue(THICKNESS);
         return thickness == SpeleothemThickness.TIP || includeMergedTip && thickness == SpeleothemThickness.TIP_MERGE;
      }
   }

   public void onBrokenAfterFall(final Level level, final BlockPos pos, final FallingBlockEntity entity) {
      if (!entity.isSilent()) {
         level.levelEvent(this.getStalactiteLandingSound(), pos, 0);
      }

   }

   protected abstract int getStalactiteLandingSound();

   public DamageSource getFallDamageSource(final Entity entity) {
      return entity.damageSources().fallingStalactite(entity);
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      VoxelShape var10000;
      switch ((SpeleothemThickness)state.getValue(THICKNESS)) {
         case TIP_MERGE -> var10000 = SHAPE_TIP_MERGE;
         case TIP -> var10000 = state.getValue(TIP_DIRECTION) == Direction.DOWN ? SHAPE_TIP_DOWN : SHAPE_TIP_UP;
         case FRUSTUM -> var10000 = SHAPE_FRUSTUM;
         case MIDDLE -> var10000 = SHAPE_MIDDLE;
         case BASE -> var10000 = SHAPE_BASE;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      VoxelShape shape = var10000;
      return shape.move(state.getOffset(pos));
   }

   protected boolean isCollisionShapeFullBlock(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return false;
   }

   protected float getMaxHorizontalOffset() {
      return MAX_HORIZONTAL_OFFSET;
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (random.nextFloat() < 0.011377778F && isStalactiteStartPos(state, level, pos)) {
         this.growStalactiteOrStalagmiteIfPossible(state, level, pos, random);
      }

   }

   protected static boolean isStalactiteStartPos(final BlockState state, final LevelReader level, final BlockPos pos) {
      return isStalactite(state) && !level.getBlockState(pos.above()).is(state.getBlock());
   }

   @VisibleForTesting
   public void growStalactiteOrStalagmiteIfPossible(final BlockState stalactiteStartState, final ServerLevel level, final BlockPos stalactiteStartPos, final RandomSource random) {
      if (this.canGrow(level, stalactiteStartPos)) {
         BlockPos stalactiteTipPos = findTip(stalactiteStartState, level, stalactiteStartPos, 7, false);
         if (stalactiteTipPos != null) {
            BlockState stalactiteTipState = level.getBlockState(stalactiteTipPos);
            if (isFreeHangingStalactite(stalactiteTipState) && this.canTipGrow(stalactiteTipState, level, stalactiteTipPos)) {
               if (random.nextBoolean()) {
                  this.grow(level, stalactiteTipPos, Direction.DOWN);
               } else {
                  this.growStalagmiteBelow(level, stalactiteTipPos);
               }

            }
         }
      }
   }

   protected boolean canGrow(final LevelReader level, final BlockPos pos) {
      return level.getBlockState(pos.above()).is(this.blockToGrowOn.getBlock());
   }

   protected static @Nullable BlockPos findTip(final BlockState speleothemState, final LevelAccessor level, final BlockPos speleothemPos, final int maxSearchLength, final boolean includeMergedTip) {
      if (isTip(speleothemState, includeMergedTip)) {
         return speleothemPos;
      } else {
         Direction searchDirection = (Direction)speleothemState.getValue(TIP_DIRECTION);
         BiPredicate<BlockPos, BlockState> pathPredicate = (pos, state) -> state.is(speleothemState.getBlock()) && state.getValue(TIP_DIRECTION) == searchDirection;
         return (BlockPos)findBlockVertical(level, speleothemPos, searchDirection.getAxisDirection(), pathPredicate, (speleothem) -> isTip(speleothem, includeMergedTip), maxSearchLength).orElse((Object)null);
      }
   }

   protected static Optional<BlockPos> findBlockVertical(final LevelAccessor level, final BlockPos pos, final Direction.AxisDirection axisDirection, final BiPredicate<BlockPos, BlockState> pathPredicate, final Predicate<BlockState> targetPredicate, final int maxSteps) {
      Direction direction = Direction.get(axisDirection, Direction.Axis.Y);
      BlockPos.MutableBlockPos mutablePos = pos.mutable();

      for(int i = 1; i < maxSteps; ++i) {
         mutablePos.move(direction);
         BlockState state = level.getBlockState(mutablePos);
         if (targetPredicate.test(state)) {
            return Optional.of(mutablePos.immutable());
         }

         if (level.isOutsideBuildHeight(mutablePos.getY()) || !pathPredicate.test(mutablePos, state)) {
            return Optional.empty();
         }
      }

      return Optional.empty();
   }

   private boolean canTipGrow(final BlockState tipState, final ServerLevel level, final BlockPos tipPos) {
      Direction growDirection = (Direction)tipState.getValue(TIP_DIRECTION);
      BlockPos growPos = tipPos.relative(growDirection);
      BlockState stateAtGrowPos = level.getBlockState(growPos);
      if (!stateAtGrowPos.getFluidState().isEmpty()) {
         return false;
      } else {
         return stateAtGrowPos.isAir() ? true : this.isUnmergedTipWithDirection(stateAtGrowPos, growDirection.getOpposite());
      }
   }

   private boolean isUnmergedTipWithDirection(final BlockState state, final Direction tipDirection) {
      return isTip(state, false) && state.getValue(TIP_DIRECTION) == tipDirection && state.is(this);
   }

   private void grow(final ServerLevel level, final BlockPos growFromPos, final Direction growToDirection) {
      BlockPos targetPos = growFromPos.relative(growToDirection);
      BlockState existingStateAtTargetPos = level.getBlockState(targetPos);
      if (this.isUnmergedTipWithDirection(existingStateAtTargetPos, growToDirection.getOpposite())) {
         this.createMergedTips(existingStateAtTargetPos, level, targetPos);
      } else if (existingStateAtTargetPos.isAir() || existingStateAtTargetPos.is(Blocks.WATER)) {
         this.createSpeleothem(level, targetPos, growToDirection, SpeleothemThickness.TIP);
      }

   }

   private void createSpeleothem(final LevelAccessor level, final BlockPos pos, final Direction direction, final SpeleothemThickness thickness) {
      BlockState state = (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(TIP_DIRECTION, direction)).setValue(THICKNESS, thickness)).setValue(WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER));
      level.setBlock(pos, state, 3);
   }

   private void createMergedTips(final BlockState tipState, final LevelAccessor level, final BlockPos tipPos) {
      BlockPos stalactitePos;
      BlockPos stalagmitePos;
      if (tipState.getValue(TIP_DIRECTION) == Direction.UP) {
         stalagmitePos = tipPos;
         stalactitePos = tipPos.above();
      } else {
         stalactitePos = tipPos;
         stalagmitePos = tipPos.below();
      }

      this.createSpeleothem(level, stalactitePos, Direction.DOWN, SpeleothemThickness.TIP_MERGE);
      this.createSpeleothem(level, stalagmitePos, Direction.UP, SpeleothemThickness.TIP_MERGE);
   }

   private void growStalagmiteBelow(final ServerLevel level, final BlockPos posAboveStalagmite) {
      BlockPos.MutableBlockPos pos = posAboveStalagmite.mutable();

      for(int i = 0; i < 10; ++i) {
         pos.move(Direction.DOWN);
         BlockState state = level.getBlockState(pos);
         if (!state.getFluidState().isEmpty()) {
            return;
         }

         if (this.isUnmergedTipWithDirection(state, Direction.UP) && this.canTipGrow(state, level, pos)) {
            this.grow(level, pos, Direction.UP);
            return;
         }

         if (this.isValidSpeleothemPlacement(level, pos, Direction.UP) && !level.isWaterAt(pos.below())) {
            this.grow(level, pos.below(), Direction.UP);
            return;
         }

         if (this.blocksStalagmiteScan(level, pos, state)) {
            return;
         }
      }

   }

   protected boolean blocksStalagmiteScan(final LevelReader level, final BlockPos pos, final BlockState state) {
      return false;
   }

   protected static boolean isFreeHangingStalactite(final BlockState state) {
      return isStalactite(state) && state.getValue(THICKNESS) == SpeleothemThickness.TIP && !(Boolean)state.getValue(WATERLOGGED);
   }

   static {
      TIP_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
      THICKNESS = BlockStateProperties.SPELEOTHEM_THICKNESS;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE_TIP_MERGE = Block.column(6.0, 0.0, 16.0);
      SHAPE_TIP_UP = Block.column(6.0, 0.0, 11.0);
      SHAPE_TIP_DOWN = Block.column(6.0, 5.0, 16.0);
      SHAPE_FRUSTUM = Block.column(8.0, 0.0, 16.0);
      SHAPE_MIDDLE = Block.column(10.0, 0.0, 16.0);
      SHAPE_BASE = Block.column(12.0, 0.0, 16.0);
      MAX_HORIZONTAL_OFFSET = (float)SHAPE_BASE.min(Direction.Axis.X);
   }
}
