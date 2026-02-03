package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
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
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class PointedDripstoneBlock extends Block implements SimpleWaterloggedBlock, Fallable {
   public static final MapCodec<PointedDripstoneBlock> CODEC = simpleCodec(PointedDripstoneBlock::new);
   public static final EnumProperty<Direction> TIP_DIRECTION;
   public static final EnumProperty<DripstoneThickness> THICKNESS;
   public static final BooleanProperty WATERLOGGED;
   private static final int MAX_SEARCH_LENGTH_WHEN_CHECKING_DRIP_TYPE = 11;
   private static final int DELAY_BEFORE_FALLING = 2;
   private static final float DRIP_PROBABILITY_PER_ANIMATE_TICK = 0.02F;
   private static final float DRIP_PROBABILITY_PER_ANIMATE_TICK_IF_UNDER_LIQUID_SOURCE = 0.12F;
   private static final int MAX_SEARCH_LENGTH_BETWEEN_STALACTITE_TIP_AND_CAULDRON = 11;
   private static final float WATER_TRANSFER_PROBABILITY_PER_RANDOM_TICK = 0.17578125F;
   private static final float LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK = 0.05859375F;
   private static final double MIN_TRIDENT_VELOCITY_TO_BREAK_DRIPSTONE = 0.6;
   private static final float STALACTITE_DAMAGE_PER_FALL_DISTANCE_AND_SIZE = 1.0F;
   private static final int STALACTITE_MAX_DAMAGE = 40;
   private static final int MAX_STALACTITE_HEIGHT_FOR_DAMAGE_CALCULATION = 6;
   private static final float STALAGMITE_FALL_DISTANCE_OFFSET = 2.5F;
   private static final int STALAGMITE_FALL_DAMAGE_MODIFIER = 2;
   private static final float AVERAGE_DAYS_PER_GROWTH = 5.0F;
   private static final float GROWTH_PROBABILITY_PER_RANDOM_TICK = 0.011377778F;
   private static final int MAX_GROWTH_LENGTH = 7;
   private static final int MAX_STALAGMITE_SEARCH_RANGE_WHEN_GROWING = 10;
   private static final VoxelShape SHAPE_TIP_MERGE;
   private static final VoxelShape SHAPE_TIP_UP;
   private static final VoxelShape SHAPE_TIP_DOWN;
   private static final VoxelShape SHAPE_FRUSTUM;
   private static final VoxelShape SHAPE_MIDDLE;
   private static final VoxelShape SHAPE_BASE;
   private static final double STALACTITE_DRIP_START_PIXEL;
   private static final float MAX_HORIZONTAL_OFFSET;
   private static final VoxelShape REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK;

   public MapCodec<PointedDripstoneBlock> codec() {
      return CODEC;
   }

   public PointedDripstoneBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(TIP_DIRECTION, Direction.UP)).setValue(THICKNESS, DripstoneThickness.TIP)).setValue(WATERLOGGED, false));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(TIP_DIRECTION, THICKNESS, WATERLOGGED);
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return isValidPointedDripstonePlacement(level, pos, (Direction)state.getValue(TIP_DIRECTION));
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
            boolean mergeOpposingTips = state.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE;
            DripstoneThickness newThickness = calculateDripstoneThickness(level, pos, tipDirection, mergeOpposingTips);
            return (BlockState)state.setValue(THICKNESS, newThickness);
         }
      }
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

   public void fallOn(final Level level, final BlockState state, final BlockPos pos, final Entity entity, final double fallDistance) {
      if (state.getValue(TIP_DIRECTION) == Direction.UP && state.getValue(THICKNESS) == DripstoneThickness.TIP) {
         entity.causeFallDamage(fallDistance + 2.5, 2.0F, level.damageSources().stalagmite());
      } else {
         super.fallOn(level, state, pos, entity, fallDistance);
      }

   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      if (canDrip(state)) {
         float randomValue = random.nextFloat();
         if (!(randomValue > 0.12F)) {
            getFluidAboveStalactite(level, pos, state).filter((fluidAbove) -> randomValue < 0.02F || canFillCauldron(fluidAbove.fluid)).ifPresent((fluidAbove) -> spawnDripParticle(level, pos, state, fluidAbove.fluid, fluidAbove.pos));
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

   protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      maybeTransferFluid(state, level, pos, random.nextFloat());
      if (random.nextFloat() < 0.011377778F && isStalactiteStartPos(state, level, pos)) {
         growStalactiteOrStalagmiteIfPossible(state, level, pos, random);
      }

   }

   @VisibleForTesting
   public static void maybeTransferFluid(final BlockState state, final ServerLevel level, final BlockPos pos, final float randomValue) {
      if (!(randomValue > 0.17578125F) || !(randomValue > 0.05859375F)) {
         if (isStalactiteStartPos(state, level, pos)) {
            Optional<FluidInfo> fluidInfo = getFluidAboveStalactite(level, pos, state);
            if (!fluidInfo.isEmpty()) {
               Fluid fluid = ((FluidInfo)fluidInfo.get()).fluid;
               float transferProbability;
               if (fluid == Fluids.WATER) {
                  transferProbability = 0.17578125F;
               } else {
                  if (fluid != Fluids.LAVA) {
                     return;
                  }

                  transferProbability = 0.05859375F;
               }

               if (!(randomValue >= transferProbability)) {
                  BlockPos stalactiteTipPos = findTip(state, level, pos, 11, false);
                  if (stalactiteTipPos != null) {
                     if (((FluidInfo)fluidInfo.get()).sourceState.is(Blocks.MUD) && fluid == Fluids.WATER) {
                        BlockState newState = Blocks.CLAY.defaultBlockState();
                        level.setBlockAndUpdate(((FluidInfo)fluidInfo.get()).pos, newState);
                        Block.pushEntitiesUp(((FluidInfo)fluidInfo.get()).sourceState, newState, level, ((FluidInfo)fluidInfo.get()).pos);
                        level.gameEvent(GameEvent.BLOCK_CHANGE, ((FluidInfo)fluidInfo.get()).pos, GameEvent.Context.of(newState));
                        level.levelEvent(1504, stalactiteTipPos, 0);
                     } else {
                        BlockPos cauldronPos = findFillableCauldronBelowStalactiteTip(level, stalactiteTipPos, fluid);
                        if (cauldronPos != null) {
                           level.levelEvent(1504, stalactiteTipPos, 0);
                           int fallDistance = stalactiteTipPos.getY() - cauldronPos.getY();
                           int delay = 50 + fallDistance;
                           BlockState cauldronState = level.getBlockState(cauldronPos);
                           level.scheduleTick(cauldronPos, cauldronState.getBlock(), delay);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      LevelAccessor level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      Direction defaultTipDirection = context.getNearestLookingVerticalDirection().getOpposite();
      Direction tipDirection = calculateTipDirection(level, pos, defaultTipDirection);
      if (tipDirection == null) {
         return null;
      } else {
         boolean mergeOpposingTips = !context.isSecondaryUseActive();
         DripstoneThickness thickness = calculateDripstoneThickness(level, pos, tipDirection, mergeOpposingTips);
         return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(TIP_DIRECTION, tipDirection)).setValue(THICKNESS, thickness)).setValue(WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER));
      }
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      VoxelShape var10000;
      switch ((DripstoneThickness)state.getValue(THICKNESS)) {
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

   public void onBrokenAfterFall(final Level level, final BlockPos pos, final FallingBlockEntity entity) {
      if (!entity.isSilent()) {
         level.levelEvent(1045, pos, 0);
      }

   }

   public DamageSource getFallDamageSource(final Entity entity) {
      return entity.damageSources().fallingStalactite(entity);
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

   @VisibleForTesting
   public static void growStalactiteOrStalagmiteIfPossible(final BlockState stalactiteStartState, final ServerLevel level, final BlockPos stalactiteStartPos, final RandomSource random) {
      BlockState rootState = level.getBlockState(stalactiteStartPos.above(1));
      BlockState stateAbove = level.getBlockState(stalactiteStartPos.above(2));
      if (canGrow(rootState, stateAbove)) {
         BlockPos stalactiteTipPos = findTip(stalactiteStartState, level, stalactiteStartPos, 7, false);
         if (stalactiteTipPos != null) {
            BlockState stalactiteTipState = level.getBlockState(stalactiteTipPos);
            if (canDrip(stalactiteTipState) && canTipGrow(stalactiteTipState, level, stalactiteTipPos)) {
               if (random.nextBoolean()) {
                  grow(level, stalactiteTipPos, Direction.DOWN);
               } else {
                  growStalagmiteBelow(level, stalactiteTipPos);
               }

            }
         }
      }
   }

   private static void growStalagmiteBelow(final ServerLevel level, final BlockPos posAboveStalagmite) {
      BlockPos.MutableBlockPos pos = posAboveStalagmite.mutable();

      for(int i = 0; i < 10; ++i) {
         pos.move(Direction.DOWN);
         BlockState state = level.getBlockState(pos);
         if (!state.getFluidState().isEmpty()) {
            return;
         }

         if (isUnmergedTipWithDirection(state, Direction.UP) && canTipGrow(state, level, pos)) {
            grow(level, pos, Direction.UP);
            return;
         }

         if (isValidPointedDripstonePlacement(level, pos, Direction.UP) && !level.isWaterAt(pos.below())) {
            grow(level, pos.below(), Direction.UP);
            return;
         }

         if (!canDripThrough(level, pos, state)) {
            return;
         }
      }

   }

   private static void grow(final ServerLevel level, final BlockPos growFromPos, final Direction growToDirection) {
      BlockPos targetPos = growFromPos.relative(growToDirection);
      BlockState existingStateAtTargetPos = level.getBlockState(targetPos);
      if (isUnmergedTipWithDirection(existingStateAtTargetPos, growToDirection.getOpposite())) {
         createMergedTips(existingStateAtTargetPos, level, targetPos);
      } else if (existingStateAtTargetPos.isAir() || existingStateAtTargetPos.is(Blocks.WATER)) {
         createDripstone(level, targetPos, growToDirection, DripstoneThickness.TIP);
      }

   }

   private static void createDripstone(final LevelAccessor level, final BlockPos pos, final Direction direction, final DripstoneThickness thickness) {
      BlockState state = (BlockState)((BlockState)((BlockState)Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(TIP_DIRECTION, direction)).setValue(THICKNESS, thickness)).setValue(WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER));
      level.setBlock(pos, state, 3);
   }

   private static void createMergedTips(final BlockState tipState, final LevelAccessor level, final BlockPos tipPos) {
      BlockPos stalactitePos;
      BlockPos stalagmitePos;
      if (tipState.getValue(TIP_DIRECTION) == Direction.UP) {
         stalagmitePos = tipPos;
         stalactitePos = tipPos.above();
      } else {
         stalactitePos = tipPos;
         stalagmitePos = tipPos.below();
      }

      createDripstone(level, stalactitePos, Direction.DOWN, DripstoneThickness.TIP_MERGE);
      createDripstone(level, stalagmitePos, Direction.UP, DripstoneThickness.TIP_MERGE);
   }

   public static void spawnDripParticle(final Level level, final BlockPos stalactiteTipPos, final BlockState stalactiteTipState) {
      getFluidAboveStalactite(level, stalactiteTipPos, stalactiteTipState).ifPresent((fluidAbove) -> spawnDripParticle(level, stalactiteTipPos, stalactiteTipState, fluidAbove.fluid, fluidAbove.pos));
   }

   private static void spawnDripParticle(final Level level, final BlockPos stalactiteTipPos, final BlockState stalactiteTipState, final Fluid fluidAbove, final BlockPos posAbove) {
      Vec3 offset = stalactiteTipState.getOffset(stalactiteTipPos);
      double PIXEL_SIZE = 0.0625;
      double x = (double)stalactiteTipPos.getX() + 0.5 + offset.x;
      double y = (double)stalactiteTipPos.getY() + STALACTITE_DRIP_START_PIXEL - 0.0625;
      double z = (double)stalactiteTipPos.getZ() + 0.5 + offset.z;
      ParticleOptions dripParticle = getDripParticle(level, fluidAbove, posAbove);
      level.addParticle(dripParticle, x, y, z, 0.0, 0.0, 0.0);
   }

   private static @Nullable BlockPos findTip(final BlockState dripstoneState, final LevelAccessor level, final BlockPos dripstonePos, final int maxSearchLength, final boolean includeMergedTip) {
      if (isTip(dripstoneState, includeMergedTip)) {
         return dripstonePos;
      } else {
         Direction searchDirection = (Direction)dripstoneState.getValue(TIP_DIRECTION);
         BiPredicate<BlockPos, BlockState> pathPredicate = (pos, state) -> state.is(Blocks.POINTED_DRIPSTONE) && state.getValue(TIP_DIRECTION) == searchDirection;
         return (BlockPos)findBlockVertical(level, dripstonePos, searchDirection.getAxisDirection(), pathPredicate, (dripstone) -> isTip(dripstone, includeMergedTip), maxSearchLength).orElse((Object)null);
      }
   }

   private static @Nullable Direction calculateTipDirection(final LevelReader level, final BlockPos pos, final Direction defaultTipDirection) {
      Direction tipDirection;
      if (isValidPointedDripstonePlacement(level, pos, defaultTipDirection)) {
         tipDirection = defaultTipDirection;
      } else {
         if (!isValidPointedDripstonePlacement(level, pos, defaultTipDirection.getOpposite())) {
            return null;
         }

         tipDirection = defaultTipDirection.getOpposite();
      }

      return tipDirection;
   }

   private static DripstoneThickness calculateDripstoneThickness(final LevelReader level, final BlockPos pos, final Direction tipDirection, final boolean mergeOpposingTips) {
      Direction baseDirection = tipDirection.getOpposite();
      BlockState inFrontState = level.getBlockState(pos.relative(tipDirection));
      if (isPointedDripstoneWithDirection(inFrontState, baseDirection)) {
         return !mergeOpposingTips && inFrontState.getValue(THICKNESS) != DripstoneThickness.TIP_MERGE ? DripstoneThickness.TIP : DripstoneThickness.TIP_MERGE;
      } else if (!isPointedDripstoneWithDirection(inFrontState, tipDirection)) {
         return DripstoneThickness.TIP;
      } else {
         DripstoneThickness inFrontThickness = (DripstoneThickness)inFrontState.getValue(THICKNESS);
         if (inFrontThickness != DripstoneThickness.TIP && inFrontThickness != DripstoneThickness.TIP_MERGE) {
            BlockState behindState = level.getBlockState(pos.relative(baseDirection));
            return !isPointedDripstoneWithDirection(behindState, tipDirection) ? DripstoneThickness.BASE : DripstoneThickness.MIDDLE;
         } else {
            return DripstoneThickness.FRUSTUM;
         }
      }
   }

   public static boolean canDrip(final BlockState state) {
      return isStalactite(state) && state.getValue(THICKNESS) == DripstoneThickness.TIP && !(Boolean)state.getValue(WATERLOGGED);
   }

   private static boolean canTipGrow(final BlockState tipState, final ServerLevel level, final BlockPos tipPos) {
      Direction growDirection = (Direction)tipState.getValue(TIP_DIRECTION);
      BlockPos growPos = tipPos.relative(growDirection);
      BlockState stateAtGrowPos = level.getBlockState(growPos);
      if (!stateAtGrowPos.getFluidState().isEmpty()) {
         return false;
      } else {
         return stateAtGrowPos.isAir() ? true : isUnmergedTipWithDirection(stateAtGrowPos, growDirection.getOpposite());
      }
   }

   private static Optional<BlockPos> findRootBlock(final Level level, final BlockPos pos, final BlockState dripStoneState, final int maxSearchLength) {
      Direction tipDirection = (Direction)dripStoneState.getValue(TIP_DIRECTION);
      BiPredicate<BlockPos, BlockState> pathPredicate = (pathPos, state) -> state.is(Blocks.POINTED_DRIPSTONE) && state.getValue(TIP_DIRECTION) == tipDirection;
      return findBlockVertical(level, pos, tipDirection.getOpposite().getAxisDirection(), pathPredicate, (state) -> !state.is(Blocks.POINTED_DRIPSTONE), maxSearchLength);
   }

   private static boolean isValidPointedDripstonePlacement(final LevelReader level, final BlockPos pos, final Direction tipDirection) {
      BlockPos behindPos = pos.relative(tipDirection.getOpposite());
      BlockState behindState = level.getBlockState(behindPos);
      return behindState.isFaceSturdy(level, behindPos, tipDirection) || isPointedDripstoneWithDirection(behindState, tipDirection);
   }

   private static boolean isTip(final BlockState state, final boolean includeMergedTip) {
      if (!state.is(Blocks.POINTED_DRIPSTONE)) {
         return false;
      } else {
         DripstoneThickness thickness = (DripstoneThickness)state.getValue(THICKNESS);
         return thickness == DripstoneThickness.TIP || includeMergedTip && thickness == DripstoneThickness.TIP_MERGE;
      }
   }

   private static boolean isUnmergedTipWithDirection(final BlockState state, final Direction tipDirection) {
      return isTip(state, false) && state.getValue(TIP_DIRECTION) == tipDirection;
   }

   private static boolean isStalactite(final BlockState state) {
      return isPointedDripstoneWithDirection(state, Direction.DOWN);
   }

   private static boolean isStalagmite(final BlockState state) {
      return isPointedDripstoneWithDirection(state, Direction.UP);
   }

   private static boolean isStalactiteStartPos(final BlockState state, final LevelReader level, final BlockPos pos) {
      return isStalactite(state) && !level.getBlockState(pos.above()).is(Blocks.POINTED_DRIPSTONE);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   private static boolean isPointedDripstoneWithDirection(final BlockState blockState, final Direction tipDirection) {
      return blockState.is(Blocks.POINTED_DRIPSTONE) && blockState.getValue(TIP_DIRECTION) == tipDirection;
   }

   private static @Nullable BlockPos findFillableCauldronBelowStalactiteTip(final Level level, final BlockPos stalactiteTipPos, final Fluid fluid) {
      Predicate<BlockState> cauldronPredicate = (state) -> state.getBlock() instanceof AbstractCauldronBlock && ((AbstractCauldronBlock)state.getBlock()).canReceiveStalactiteDrip(fluid);
      BiPredicate<BlockPos, BlockState> pathPredicate = (pos, state) -> canDripThrough(level, pos, state);
      return (BlockPos)findBlockVertical(level, stalactiteTipPos, Direction.DOWN.getAxisDirection(), pathPredicate, cauldronPredicate, 11).orElse((Object)null);
   }

   public static @Nullable BlockPos findStalactiteTipAboveCauldron(final Level level, final BlockPos cauldronPos) {
      BiPredicate<BlockPos, BlockState> pathPredicate = (pos, state) -> canDripThrough(level, pos, state);
      return (BlockPos)findBlockVertical(level, cauldronPos, Direction.UP.getAxisDirection(), pathPredicate, PointedDripstoneBlock::canDrip, 11).orElse((Object)null);
   }

   public static Fluid getCauldronFillFluidType(final ServerLevel level, final BlockPos stalactitePos) {
      return (Fluid)getFluidAboveStalactite(level, stalactitePos, level.getBlockState(stalactitePos)).map((fluidSource) -> fluidSource.fluid).filter(PointedDripstoneBlock::canFillCauldron).orElse(Fluids.EMPTY);
   }

   private static Optional<FluidInfo> getFluidAboveStalactite(final Level level, final BlockPos stalactitePos, final BlockState stalactiteState) {
      return !isStalactite(stalactiteState) ? Optional.empty() : findRootBlock(level, stalactitePos, stalactiteState, 11).map((rootPos) -> {
         BlockPos abovePos = rootPos.above();
         BlockState aboveState = level.getBlockState(abovePos);
         Fluid fluid;
         if (aboveState.is(Blocks.MUD) && !(Boolean)level.environmentAttributes().getValue(EnvironmentAttributes.WATER_EVAPORATES, abovePos)) {
            fluid = Fluids.WATER;
         } else {
            fluid = level.getFluidState(abovePos).getType();
         }

         return new FluidInfo(abovePos, fluid, aboveState);
      });
   }

   private static boolean canFillCauldron(final Fluid fluidAbove) {
      return fluidAbove == Fluids.LAVA || fluidAbove == Fluids.WATER;
   }

   private static boolean canGrow(final BlockState rootState, final BlockState aboveState) {
      return rootState.is(Blocks.DRIPSTONE_BLOCK) && aboveState.is(Blocks.WATER) && aboveState.getFluidState().isSource();
   }

   private static ParticleOptions getDripParticle(final Level level, final Fluid fluidAbove, final BlockPos posAbove) {
      if (fluidAbove.isSame(Fluids.EMPTY)) {
         return (ParticleOptions)level.environmentAttributes().getValue(EnvironmentAttributes.DEFAULT_DRIPSTONE_PARTICLE, posAbove);
      } else {
         return fluidAbove.is(FluidTags.LAVA) ? ParticleTypes.DRIPPING_DRIPSTONE_LAVA : ParticleTypes.DRIPPING_DRIPSTONE_WATER;
      }
   }

   private static Optional<BlockPos> findBlockVertical(final LevelAccessor level, final BlockPos pos, final Direction.AxisDirection axisDirection, final BiPredicate<BlockPos, BlockState> pathPredicate, final Predicate<BlockState> targetPredicate, final int maxSteps) {
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

   private static boolean canDripThrough(final BlockGetter level, final BlockPos pos, final BlockState state) {
      if (state.isAir()) {
         return true;
      } else if (state.isSolidRender()) {
         return false;
      } else if (!state.getFluidState().isEmpty()) {
         return false;
      } else {
         VoxelShape collisionShape = state.getCollisionShape(level, pos);
         return !Shapes.joinIsNotEmpty(REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK, collisionShape, BooleanOp.AND);
      }
   }

   static {
      TIP_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
      THICKNESS = BlockStateProperties.DRIPSTONE_THICKNESS;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE_TIP_MERGE = Block.column(6.0, 0.0, 16.0);
      SHAPE_TIP_UP = Block.column(6.0, 0.0, 11.0);
      SHAPE_TIP_DOWN = Block.column(6.0, 5.0, 16.0);
      SHAPE_FRUSTUM = Block.column(8.0, 0.0, 16.0);
      SHAPE_MIDDLE = Block.column(10.0, 0.0, 16.0);
      SHAPE_BASE = Block.column(12.0, 0.0, 16.0);
      STALACTITE_DRIP_START_PIXEL = SHAPE_TIP_DOWN.min(Direction.Axis.Y);
      MAX_HORIZONTAL_OFFSET = (float)SHAPE_BASE.min(Direction.Axis.X);
      REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK = Block.column(4.0, 0.0, 16.0);
   }

   static record FluidInfo(BlockPos pos, Fluid fluid, BlockState sourceState) {
      FluidInfo {
         super();
      }
   }
}
