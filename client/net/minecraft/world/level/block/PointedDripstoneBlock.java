package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
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

public class PointedDripstoneBlock extends Block implements Fallable, SimpleWaterloggedBlock {
   public static final MapCodec<PointedDripstoneBlock> CODEC = simpleCodec(PointedDripstoneBlock::new);
   public static final DirectionProperty TIP_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
   public static final EnumProperty<DripstoneThickness> THICKNESS = BlockStateProperties.DRIPSTONE_THICKNESS;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
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
   private static final float STALAGMITE_FALL_DISTANCE_OFFSET = 2.0F;
   private static final int STALAGMITE_FALL_DAMAGE_MODIFIER = 2;
   private static final float AVERAGE_DAYS_PER_GROWTH = 5.0F;
   private static final float GROWTH_PROBABILITY_PER_RANDOM_TICK = 0.011377778F;
   private static final int MAX_GROWTH_LENGTH = 7;
   private static final int MAX_STALAGMITE_SEARCH_RANGE_WHEN_GROWING = 10;
   private static final float STALACTITE_DRIP_START_PIXEL = 0.6875F;
   private static final VoxelShape TIP_MERGE_SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
   private static final VoxelShape TIP_SHAPE_UP = Block.box(5.0, 0.0, 5.0, 11.0, 11.0, 11.0);
   private static final VoxelShape TIP_SHAPE_DOWN = Block.box(5.0, 5.0, 5.0, 11.0, 16.0, 11.0);
   private static final VoxelShape FRUSTUM_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
   private static final VoxelShape MIDDLE_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
   private static final VoxelShape BASE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
   private static final float MAX_HORIZONTAL_OFFSET = 0.125F;
   private static final VoxelShape REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);

   @Override
   public MapCodec<PointedDripstoneBlock> codec() {
      return CODEC;
   }

   public PointedDripstoneBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(TIP_DIRECTION, Direction.UP)
            .setValue(THICKNESS, DripstoneThickness.TIP)
            .setValue(WATERLOGGED, Boolean.valueOf(false))
      );
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(TIP_DIRECTION, THICKNESS, WATERLOGGED);
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return isValidPointedDripstonePlacement(var2, var3, var1.getValue(TIP_DIRECTION));
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      if (var2 != Direction.UP && var2 != Direction.DOWN) {
         return var1;
      } else {
         Direction var7 = var1.getValue(TIP_DIRECTION);
         if (var7 == Direction.DOWN && var4.getBlockTicks().hasScheduledTick(var5, this)) {
            return var1;
         } else if (var2 == var7.getOpposite() && !this.canSurvive(var1, var4, var5)) {
            if (var7 == Direction.DOWN) {
               var4.scheduleTick(var5, this, 2);
            } else {
               var4.scheduleTick(var5, this, 1);
            }

            return var1;
         } else {
            boolean var8 = var1.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE;
            DripstoneThickness var9 = calculateDripstoneThickness(var4, var5, var7, var8);
            return var1.setValue(THICKNESS, var9);
         }
      }
   }

   @Override
   protected void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      if (!var1.isClientSide) {
         BlockPos var5 = var3.getBlockPos();
         if (var4.mayInteract(var1, var5) && var4.mayBreak(var1) && var4 instanceof ThrownTrident && var4.getDeltaMovement().length() > 0.6) {
            var1.destroyBlock(var5, true);
         }
      }
   }

   @Override
   public void fallOn(Level var1, BlockState var2, BlockPos var3, Entity var4, float var5) {
      if (var2.getValue(TIP_DIRECTION) == Direction.UP && var2.getValue(THICKNESS) == DripstoneThickness.TIP) {
         var4.causeFallDamage(var5 + 2.0F, 2.0F, var1.damageSources().stalagmite());
      } else {
         super.fallOn(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (canDrip(var1)) {
         float var5 = var4.nextFloat();
         if (!(var5 > 0.12F)) {
            getFluidAboveStalactite(var2, var3, var1)
               .filter(var1x -> var5 < 0.02F || canFillCauldron(var1x.fluid))
               .ifPresent(var3x -> spawnDripParticle(var2, var3, var1, var3x.fluid));
         }
      }
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (isStalagmite(var1) && !this.canSurvive(var1, var2, var3)) {
         var2.destroyBlock(var3, true);
      } else {
         spawnFallingStalactite(var1, var2, var3);
      }
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      maybeTransferFluid(var1, var2, var3, var4.nextFloat());
      if (var4.nextFloat() < 0.011377778F && isStalactiteStartPos(var1, var2, var3)) {
         growStalactiteOrStalagmiteIfPossible(var1, var2, var3, var4);
      }
   }

   @VisibleForTesting
   public static void maybeTransferFluid(BlockState var0, ServerLevel var1, BlockPos var2, float var3) {
      if (!(var3 > 0.17578125F) || !(var3 > 0.05859375F)) {
         if (isStalactiteStartPos(var0, var1, var2)) {
            Optional var4 = getFluidAboveStalactite(var1, var2, var0);
            if (!var4.isEmpty()) {
               Fluid var5 = ((PointedDripstoneBlock.FluidInfo)var4.get()).fluid;
               float var6;
               if (var5 == Fluids.WATER) {
                  var6 = 0.17578125F;
               } else {
                  if (var5 != Fluids.LAVA) {
                     return;
                  }

                  var6 = 0.05859375F;
               }

               if (!(var3 >= var6)) {
                  BlockPos var7 = findTip(var0, var1, var2, 11, false);
                  if (var7 != null) {
                     if (((PointedDripstoneBlock.FluidInfo)var4.get()).sourceState.is(Blocks.MUD) && var5 == Fluids.WATER) {
                        BlockState var12 = Blocks.CLAY.defaultBlockState();
                        var1.setBlockAndUpdate(((PointedDripstoneBlock.FluidInfo)var4.get()).pos, var12);
                        Block.pushEntitiesUp(
                           ((PointedDripstoneBlock.FluidInfo)var4.get()).sourceState, var12, var1, ((PointedDripstoneBlock.FluidInfo)var4.get()).pos
                        );
                        var1.gameEvent(GameEvent.BLOCK_CHANGE, ((PointedDripstoneBlock.FluidInfo)var4.get()).pos, GameEvent.Context.of(var12));
                        var1.levelEvent(1504, var7, 0);
                     } else {
                        BlockPos var8 = findFillableCauldronBelowStalactiteTip(var1, var7, var5);
                        if (var8 != null) {
                           var1.levelEvent(1504, var7, 0);
                           int var9 = var7.getY() - var8.getY();
                           int var10 = 50 + var9;
                           BlockState var11 = var1.getBlockState(var8);
                           var1.scheduleTick(var8, var11.getBlock(), var10);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      Direction var4 = var1.getNearestLookingVerticalDirection().getOpposite();
      Direction var5 = calculateTipDirection(var2, var3, var4);
      if (var5 == null) {
         return null;
      } else {
         boolean var6 = !var1.isSecondaryUseActive();
         DripstoneThickness var7 = calculateDripstoneThickness(var2, var3, var5, var6);
         return var7 == null
            ? null
            : this.defaultBlockState()
               .setValue(TIP_DIRECTION, var5)
               .setValue(THICKNESS, var7)
               .setValue(WATERLOGGED, Boolean.valueOf(var2.getFluidState(var3).getType() == Fluids.WATER));
      }
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   protected VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      DripstoneThickness var6 = var1.getValue(THICKNESS);
      VoxelShape var5;
      if (var6 == DripstoneThickness.TIP_MERGE) {
         var5 = TIP_MERGE_SHAPE;
      } else if (var6 == DripstoneThickness.TIP) {
         if (var1.getValue(TIP_DIRECTION) == Direction.DOWN) {
            var5 = TIP_SHAPE_DOWN;
         } else {
            var5 = TIP_SHAPE_UP;
         }
      } else if (var6 == DripstoneThickness.FRUSTUM) {
         var5 = FRUSTUM_SHAPE;
      } else if (var6 == DripstoneThickness.MIDDLE) {
         var5 = MIDDLE_SHAPE;
      } else {
         var5 = BASE_SHAPE;
      }

      Vec3 var7 = var1.getOffset(var2, var3);
      return var5.move(var7.x, 0.0, var7.z);
   }

   @Override
   protected boolean isCollisionShapeFullBlock(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   @Override
   protected float getMaxHorizontalOffset() {
      return 0.125F;
   }

   @Override
   public void onBrokenAfterFall(Level var1, BlockPos var2, FallingBlockEntity var3) {
      if (!var3.isSilent()) {
         var1.levelEvent(1045, var2, 0);
      }
   }

   @Override
   public DamageSource getFallDamageSource(Entity var1) {
      return var1.damageSources().fallingStalactite(var1);
   }

   private static void spawnFallingStalactite(BlockState var0, ServerLevel var1, BlockPos var2) {
      BlockPos.MutableBlockPos var3 = var2.mutable();
      BlockState var4 = var0;

      while (isStalactite(var4)) {
         FallingBlockEntity var5 = FallingBlockEntity.fall(var1, var3, var4);
         if (isTip(var4, true)) {
            int var6 = Math.max(1 + var2.getY() - var3.getY(), 6);
            float var7 = 1.0F * (float)var6;
            var5.setHurtsEntities(var7, 40);
            break;
         }

         var3.move(Direction.DOWN);
         var4 = var1.getBlockState(var3);
      }
   }

   @VisibleForTesting
   public static void growStalactiteOrStalagmiteIfPossible(BlockState var0, ServerLevel var1, BlockPos var2, RandomSource var3) {
      BlockState var4 = var1.getBlockState(var2.above(1));
      BlockState var5 = var1.getBlockState(var2.above(2));
      if (canGrow(var4, var5)) {
         BlockPos var6 = findTip(var0, var1, var2, 7, false);
         if (var6 != null) {
            BlockState var7 = var1.getBlockState(var6);
            if (canDrip(var7) && canTipGrow(var7, var1, var6)) {
               if (var3.nextBoolean()) {
                  grow(var1, var6, Direction.DOWN);
               } else {
                  growStalagmiteBelow(var1, var6);
               }
            }
         }
      }
   }

   private static void growStalagmiteBelow(ServerLevel var0, BlockPos var1) {
      BlockPos.MutableBlockPos var2 = var1.mutable();

      for (int var3 = 0; var3 < 10; var3++) {
         var2.move(Direction.DOWN);
         BlockState var4 = var0.getBlockState(var2);
         if (!var4.getFluidState().isEmpty()) {
            return;
         }

         if (isUnmergedTipWithDirection(var4, Direction.UP) && canTipGrow(var4, var0, var2)) {
            grow(var0, var2, Direction.UP);
            return;
         }

         if (isValidPointedDripstonePlacement(var0, var2, Direction.UP) && !var0.isWaterAt(var2.below())) {
            grow(var0, var2.below(), Direction.UP);
            return;
         }

         if (!canDripThrough(var0, var2, var4)) {
            return;
         }
      }
   }

   private static void grow(ServerLevel var0, BlockPos var1, Direction var2) {
      BlockPos var3 = var1.relative(var2);
      BlockState var4 = var0.getBlockState(var3);
      if (isUnmergedTipWithDirection(var4, var2.getOpposite())) {
         createMergedTips(var4, var0, var3);
      } else if (var4.isAir() || var4.is(Blocks.WATER)) {
         createDripstone(var0, var3, var2, DripstoneThickness.TIP);
      }
   }

   private static void createDripstone(LevelAccessor var0, BlockPos var1, Direction var2, DripstoneThickness var3) {
      BlockState var4 = Blocks.POINTED_DRIPSTONE
         .defaultBlockState()
         .setValue(TIP_DIRECTION, var2)
         .setValue(THICKNESS, var3)
         .setValue(WATERLOGGED, Boolean.valueOf(var0.getFluidState(var1).getType() == Fluids.WATER));
      var0.setBlock(var1, var4, 3);
   }

   private static void createMergedTips(BlockState var0, LevelAccessor var1, BlockPos var2) {
      BlockPos var3;
      BlockPos var4;
      if (var0.getValue(TIP_DIRECTION) == Direction.UP) {
         var4 = var2;
         var3 = var2.above();
      } else {
         var3 = var2;
         var4 = var2.below();
      }

      createDripstone(var1, var3, Direction.DOWN, DripstoneThickness.TIP_MERGE);
      createDripstone(var1, var4, Direction.UP, DripstoneThickness.TIP_MERGE);
   }

   public static void spawnDripParticle(Level var0, BlockPos var1, BlockState var2) {
      getFluidAboveStalactite(var0, var1, var2).ifPresent(var3 -> spawnDripParticle(var0, var1, var2, var3.fluid));
   }

   private static void spawnDripParticle(Level var0, BlockPos var1, BlockState var2, Fluid var3) {
      Vec3 var4 = var2.getOffset(var0, var1);
      double var5 = 0.0625;
      double var7 = (double)var1.getX() + 0.5 + var4.x;
      double var9 = (double)((float)(var1.getY() + 1) - 0.6875F) - 0.0625;
      double var11 = (double)var1.getZ() + 0.5 + var4.z;
      Fluid var13 = getDripFluid(var0, var3);
      SimpleParticleType var14 = var13.is(FluidTags.LAVA) ? ParticleTypes.DRIPPING_DRIPSTONE_LAVA : ParticleTypes.DRIPPING_DRIPSTONE_WATER;
      var0.addParticle(var14, var7, var9, var11, 0.0, 0.0, 0.0);
   }

   @Nullable
   private static BlockPos findTip(BlockState var0, LevelAccessor var1, BlockPos var2, int var3, boolean var4) {
      if (isTip(var0, var4)) {
         return var2;
      } else {
         Direction var5 = var0.getValue(TIP_DIRECTION);
         BiPredicate var6 = (var1x, var2x) -> var2x.is(Blocks.POINTED_DRIPSTONE) && var2x.getValue(TIP_DIRECTION) == var5;
         return findBlockVertical(var1, var2, var5.getAxisDirection(), var6, var1x -> isTip(var1x, var4), var3).orElse(null);
      }
   }

   @Nullable
   private static Direction calculateTipDirection(LevelReader var0, BlockPos var1, Direction var2) {
      Direction var3;
      if (isValidPointedDripstonePlacement(var0, var1, var2)) {
         var3 = var2;
      } else {
         if (!isValidPointedDripstonePlacement(var0, var1, var2.getOpposite())) {
            return null;
         }

         var3 = var2.getOpposite();
      }

      return var3;
   }

   private static DripstoneThickness calculateDripstoneThickness(LevelReader var0, BlockPos var1, Direction var2, boolean var3) {
      Direction var4 = var2.getOpposite();
      BlockState var5 = var0.getBlockState(var1.relative(var2));
      if (isPointedDripstoneWithDirection(var5, var4)) {
         return !var3 && var5.getValue(THICKNESS) != DripstoneThickness.TIP_MERGE ? DripstoneThickness.TIP : DripstoneThickness.TIP_MERGE;
      } else if (!isPointedDripstoneWithDirection(var5, var2)) {
         return DripstoneThickness.TIP;
      } else {
         DripstoneThickness var6 = var5.getValue(THICKNESS);
         if (var6 != DripstoneThickness.TIP && var6 != DripstoneThickness.TIP_MERGE) {
            BlockState var7 = var0.getBlockState(var1.relative(var4));
            return !isPointedDripstoneWithDirection(var7, var2) ? DripstoneThickness.BASE : DripstoneThickness.MIDDLE;
         } else {
            return DripstoneThickness.FRUSTUM;
         }
      }
   }

   public static boolean canDrip(BlockState var0) {
      return isStalactite(var0) && var0.getValue(THICKNESS) == DripstoneThickness.TIP && !var0.getValue(WATERLOGGED);
   }

   private static boolean canTipGrow(BlockState var0, ServerLevel var1, BlockPos var2) {
      Direction var3 = var0.getValue(TIP_DIRECTION);
      BlockPos var4 = var2.relative(var3);
      BlockState var5 = var1.getBlockState(var4);
      if (!var5.getFluidState().isEmpty()) {
         return false;
      } else {
         return var5.isAir() ? true : isUnmergedTipWithDirection(var5, var3.getOpposite());
      }
   }

   private static Optional<BlockPos> findRootBlock(Level var0, BlockPos var1, BlockState var2, int var3) {
      Direction var4 = var2.getValue(TIP_DIRECTION);
      BiPredicate var5 = (var1x, var2x) -> var2x.is(Blocks.POINTED_DRIPSTONE) && var2x.getValue(TIP_DIRECTION) == var4;
      return findBlockVertical(var0, var1, var4.getOpposite().getAxisDirection(), var5, var0x -> !var0x.is(Blocks.POINTED_DRIPSTONE), var3);
   }

   private static boolean isValidPointedDripstonePlacement(LevelReader var0, BlockPos var1, Direction var2) {
      BlockPos var3 = var1.relative(var2.getOpposite());
      BlockState var4 = var0.getBlockState(var3);
      return var4.isFaceSturdy(var0, var3, var2) || isPointedDripstoneWithDirection(var4, var2);
   }

   private static boolean isTip(BlockState var0, boolean var1) {
      if (!var0.is(Blocks.POINTED_DRIPSTONE)) {
         return false;
      } else {
         DripstoneThickness var2 = var0.getValue(THICKNESS);
         return var2 == DripstoneThickness.TIP || var1 && var2 == DripstoneThickness.TIP_MERGE;
      }
   }

   private static boolean isUnmergedTipWithDirection(BlockState var0, Direction var1) {
      return isTip(var0, false) && var0.getValue(TIP_DIRECTION) == var1;
   }

   private static boolean isStalactite(BlockState var0) {
      return isPointedDripstoneWithDirection(var0, Direction.DOWN);
   }

   private static boolean isStalagmite(BlockState var0) {
      return isPointedDripstoneWithDirection(var0, Direction.UP);
   }

   private static boolean isStalactiteStartPos(BlockState var0, LevelReader var1, BlockPos var2) {
      return isStalactite(var0) && !var1.getBlockState(var2.above()).is(Blocks.POINTED_DRIPSTONE);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   private static boolean isPointedDripstoneWithDirection(BlockState var0, Direction var1) {
      return var0.is(Blocks.POINTED_DRIPSTONE) && var0.getValue(TIP_DIRECTION) == var1;
   }

   @Nullable
   private static BlockPos findFillableCauldronBelowStalactiteTip(Level var0, BlockPos var1, Fluid var2) {
      Predicate var3 = var1x -> var1x.getBlock() instanceof AbstractCauldronBlock && ((AbstractCauldronBlock)var1x.getBlock()).canReceiveStalactiteDrip(var2);
      BiPredicate var4 = (var1x, var2x) -> canDripThrough(var0, var1x, var2x);
      return findBlockVertical(var0, var1, Direction.DOWN.getAxisDirection(), var4, var3, 11).orElse(null);
   }

   @Nullable
   public static BlockPos findStalactiteTipAboveCauldron(Level var0, BlockPos var1) {
      BiPredicate var2 = (var1x, var2x) -> canDripThrough(var0, var1x, var2x);
      return findBlockVertical(var0, var1, Direction.UP.getAxisDirection(), var2, PointedDripstoneBlock::canDrip, 11).orElse(null);
   }

   public static Fluid getCauldronFillFluidType(ServerLevel var0, BlockPos var1) {
      return getFluidAboveStalactite(var0, var1, var0.getBlockState(var1))
         .map(var0x -> var0x.fluid)
         .filter(PointedDripstoneBlock::canFillCauldron)
         .orElse(Fluids.EMPTY);
   }

   private static Optional<PointedDripstoneBlock.FluidInfo> getFluidAboveStalactite(Level var0, BlockPos var1, BlockState var2) {
      return !isStalactite(var2) ? Optional.empty() : findRootBlock(var0, var1, var2, 11).map(var1x -> {
         BlockPos var2x = var1x.above();
         BlockState var3 = var0.getBlockState(var2x);
         Object var4;
         if (var3.is(Blocks.MUD) && !var0.dimensionType().ultraWarm()) {
            var4 = Fluids.WATER;
         } else {
            var4 = var0.getFluidState(var2x).getType();
         }

         return new PointedDripstoneBlock.FluidInfo(var2x, (Fluid)var4, var3);
      });
   }

   private static boolean canFillCauldron(Fluid var0) {
      return var0 == Fluids.LAVA || var0 == Fluids.WATER;
   }

   private static boolean canGrow(BlockState var0, BlockState var1) {
      return var0.is(Blocks.DRIPSTONE_BLOCK) && var1.is(Blocks.WATER) && var1.getFluidState().isSource();
   }

   private static Fluid getDripFluid(Level var0, Fluid var1) {
      if (var1.isSame(Fluids.EMPTY)) {
         return var0.dimensionType().ultraWarm() ? Fluids.LAVA : Fluids.WATER;
      } else {
         return var1;
      }
   }

   private static Optional<BlockPos> findBlockVertical(
      LevelAccessor var0, BlockPos var1, Direction.AxisDirection var2, BiPredicate<BlockPos, BlockState> var3, Predicate<BlockState> var4, int var5
   ) {
      Direction var6 = Direction.get(var2, Direction.Axis.Y);
      BlockPos.MutableBlockPos var7 = var1.mutable();

      for (int var8 = 1; var8 < var5; var8++) {
         var7.move(var6);
         BlockState var9 = var0.getBlockState(var7);
         if (var4.test(var9)) {
            return Optional.of(var7.immutable());
         }

         if (var0.isOutsideBuildHeight(var7.getY()) || !var3.test(var7, var9)) {
            return Optional.empty();
         }
      }

      return Optional.empty();
   }

   private static boolean canDripThrough(BlockGetter var0, BlockPos var1, BlockState var2) {
      if (var2.isAir()) {
         return true;
      } else if (var2.isSolidRender(var0, var1)) {
         return false;
      } else if (!var2.getFluidState().isEmpty()) {
         return false;
      } else {
         VoxelShape var3 = var2.getCollisionShape(var0, var1);
         return !Shapes.joinIsNotEmpty(REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK, var3, BooleanOp.AND);
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
