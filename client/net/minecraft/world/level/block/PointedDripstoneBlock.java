package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
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
   public static final MapCodec<PointedDripstoneBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(Codec.BOOL.fieldOf("organic").forGetter(var0x -> var0x.organic), propertiesCodec()).apply(var0, PointedDripstoneBlock::new)
   );
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
   private final boolean organic;

   public boolean isBase(BlockState var1) {
      return this.organic ? var1.is(BlockTags.POTATOSTONE_BASE) : var1.is(Blocks.DRIPSTONE_BLOCK);
   }

   @Override
   public MapCodec<PointedDripstoneBlock> codec() {
      return CODEC;
   }

   public PointedDripstoneBlock(boolean var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.organic = var1;
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
      return this.isValidPointedDripstonePlacement(var2, var3, var1.getValue(TIP_DIRECTION));
   }

   public Block getLargeBlock() {
      return this.organic ? Blocks.TERREDEPOMME : Blocks.DRIPSTONE_BLOCK;
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
            DripstoneThickness var9 = this.calculateDripstoneThickness(var4, var5, var7, var8);
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
      if (this.canDrip(var1)) {
         float var5 = var4.nextFloat();
         if (!(var5 > 0.12F)) {
            this.getFluidAboveStalactite(var2, var3, var1)
               .filter(var1x -> var5 < 0.02F || canFillCauldron(var1x.fluid))
               .ifPresent(var3x -> spawnDripParticle(var2, var3, var1, var3x.fluid));
         }
      }
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (this.isStalagmite(var1) && !this.canSurvive(var1, var2, var3)) {
         var2.destroyBlock(var3, true);
      } else {
         this.spawnFallingStalactite(var1, var2, var3);
      }
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      this.maybeTransferFluid(var1, var2, var3, var4.nextFloat());
      float var5 = 0.011377778F;
      if (this.organic) {
         var5 = 0.2F;
      }

      if (var4.nextFloat() < var5) {
         if (this.organic) {
            this.growUp(var2, var3);
            this.growDown(var2, var3);
         } else if (this.isStalactiteStartPos(var1, var2, var3)) {
            this.growStalactiteOrStalagmiteIfPossible(var1, var2, var3, var4);
         }
      }
   }

   @VisibleForTesting
   public void maybeTransferFluid(BlockState var1, ServerLevel var2, BlockPos var3, float var4) {
      if (!(var4 > 0.17578125F) || !(var4 > 0.05859375F)) {
         if (this.isStalactiteStartPos(var1, var2, var3)) {
            Optional var5 = this.getFluidAboveStalactite(var2, var3, var1);
            if (!var5.isEmpty()) {
               Fluid var6 = ((PointedDripstoneBlock.FluidInfo)var5.get()).fluid;
               float var7;
               if (var6 == Fluids.WATER) {
                  var7 = 0.17578125F;
               } else {
                  if (var6 != Fluids.LAVA) {
                     return;
                  }

                  var7 = 0.05859375F;
               }

               if (!(var4 >= var7)) {
                  BlockPos var8 = this.findTip(var1, var2, var3, 11, false);
                  if (var8 != null) {
                     if (((PointedDripstoneBlock.FluidInfo)var5.get()).sourceState.is(Blocks.MUD) && var6 == Fluids.WATER) {
                        BlockState var13 = Blocks.CLAY.defaultBlockState();
                        var2.setBlockAndUpdate(((PointedDripstoneBlock.FluidInfo)var5.get()).pos, var13);
                        Block.pushEntitiesUp(
                           ((PointedDripstoneBlock.FluidInfo)var5.get()).sourceState, var13, var2, ((PointedDripstoneBlock.FluidInfo)var5.get()).pos
                        );
                        var2.gameEvent(GameEvent.BLOCK_CHANGE, ((PointedDripstoneBlock.FluidInfo)var5.get()).pos, GameEvent.Context.of(var13));
                        var2.levelEvent(1504, var8, 0);
                     } else {
                        BlockPos var9 = findFillableCauldronBelowStalactiteTip(var2, var8, var6);
                        if (var9 != null) {
                           var2.levelEvent(1504, var8, 0);
                           int var10 = var8.getY() - var9.getY();
                           int var11 = 50 + var10;
                           BlockState var12 = var2.getBlockState(var9);
                           var2.scheduleTick(var9, var12.getBlock(), var11);
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
      Direction var5 = this.calculateTipDirection(var2, var3, var4);
      if (var5 == null) {
         return null;
      } else {
         boolean var6 = !var1.isSecondaryUseActive();
         DripstoneThickness var7 = this.calculateDripstoneThickness(var2, var3, var5, var6);
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

   private void spawnFallingStalactite(BlockState var1, ServerLevel var2, BlockPos var3) {
      BlockPos.MutableBlockPos var4 = var3.mutable();

      for(BlockState var5 = var1; this.isStalactite(var5); var5 = var2.getBlockState(var4)) {
         FallingBlockEntity var6 = FallingBlockEntity.fall(var2, var4, var5);
         if (this.isTip(var5, true)) {
            int var7 = Math.max(1 + var3.getY() - var4.getY(), 6);
            float var8 = 1.0F * (float)var7;
            var6.setHurtsEntities(var8, 40);
            break;
         }

         var4.move(Direction.DOWN);
      }
   }

   @VisibleForTesting
   public void growStalactiteOrStalagmiteIfPossible(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BlockState var5 = var2.getBlockState(var3.above(1));
      BlockState var6 = var2.getBlockState(var3.above(2));
      if (this.canGrow(var5, var6)) {
         BlockPos var7 = this.findTip(var1, var2, var3, 7, false);
         if (var7 != null) {
            BlockState var8 = var2.getBlockState(var7);
            if (this.canDrip(var8) && this.canTipGrow(var8, var2, var7)) {
               if (var4.nextBoolean()) {
                  this.grow(var2, var7, Direction.DOWN);
               } else {
                  this.growStalagmiteBelow(var2, var7);
               }
            }
         }
      }
   }

   public void growUp(ServerLevel var1, BlockPos var2) {
      BlockPos.MutableBlockPos var3 = var2.mutable();

      for(int var4 = 0; var4 < 3; ++var4) {
         BlockState var5 = var1.getBlockState(var3);
         if (this.isUnmergedTipWithDirection(var5, Direction.UP) && !var1.getBlockState(var3.offset(0, -24, 0)).is(this)) {
            this.grow(var1, var3, Direction.UP);
            return;
         }

         var3.move(Direction.UP);
      }
   }

   public void growDown(ServerLevel var1, BlockPos var2) {
      BlockPos.MutableBlockPos var3 = var2.mutable();

      for(int var4 = 0; var4 < 3; ++var4) {
         BlockState var5 = var1.getBlockState(var3);
         if (this.isUnmergedTipWithDirection(var5, Direction.DOWN) && !var1.getBlockState(var3.offset(0, 24, 0)).is(this)) {
            this.grow(var1, var3, Direction.DOWN);
            return;
         }

         var3.move(Direction.DOWN);
      }
   }

   private void growStalagmiteBelow(ServerLevel var1, BlockPos var2) {
      BlockPos.MutableBlockPos var3 = var2.mutable();

      for(int var4 = 0; var4 < 10; ++var4) {
         var3.move(Direction.DOWN);
         BlockState var5 = var1.getBlockState(var3);
         if (!var5.getFluidState().isEmpty()) {
            return;
         }

         if (this.isUnmergedTipWithDirection(var5, Direction.UP) && this.canTipGrow(var5, var1, var3)) {
            this.grow(var1, var3, Direction.UP);
            return;
         }

         if (this.isValidPointedDripstonePlacement(var1, var3, Direction.UP) && !var1.isWaterAt(var3.below())) {
            this.grow(var1, var3.below(), Direction.UP);
            return;
         }

         if (!canDripThrough(var1, var3, var5)) {
            return;
         }
      }
   }

   private void grow(ServerLevel var1, BlockPos var2, Direction var3) {
      BlockPos var4 = var2.relative(var3);
      BlockState var5 = var1.getBlockState(var4);
      if (this.isUnmergedTipWithDirection(var5, var3.getOpposite())) {
         this.createMergedTips(var5, var1, var4);
      } else if (var5.isAir() || var5.is(Blocks.WATER)) {
         this.createDripstone(var1, var4, var3, DripstoneThickness.TIP);
      }
   }

   private void createDripstone(LevelAccessor var1, BlockPos var2, Direction var3, DripstoneThickness var4) {
      BlockState var5 = this.defaultBlockState()
         .setValue(TIP_DIRECTION, var3)
         .setValue(THICKNESS, var4)
         .setValue(WATERLOGGED, Boolean.valueOf(var1.getFluidState(var2).getType() == Fluids.WATER));
      var1.setBlock(var2, var5, 3);
   }

   private void createMergedTips(BlockState var1, LevelAccessor var2, BlockPos var3) {
      BlockPos var4;
      BlockPos var5;
      if (var1.getValue(TIP_DIRECTION) == Direction.UP) {
         var5 = var3;
         var4 = var3.above();
      } else {
         var4 = var3;
         var5 = var3.below();
      }

      this.createDripstone(var2, var4, Direction.DOWN, DripstoneThickness.TIP_MERGE);
      this.createDripstone(var2, var5, Direction.UP, DripstoneThickness.TIP_MERGE);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static void spawnDripParticle(Level var0, BlockPos var1, BlockState var2) {
      Block var4 = var2.getBlock();
      if (var4 instanceof PointedDripstoneBlock var3) {
         var3.getFluidAboveStalactite(var0, var1, var2).ifPresent(var3x -> spawnDripParticle(var0, var1, var2, var3x.fluid));
      }
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
   private BlockPos findTip(BlockState var1, LevelAccessor var2, BlockPos var3, int var4, boolean var5) {
      if (this.isTip(var1, var5)) {
         return var3;
      } else {
         Direction var6 = var1.getValue(TIP_DIRECTION);
         BiPredicate var7 = (var2x, var3x) -> var3x.is(this) && var3x.getValue(TIP_DIRECTION) == var6;
         return findBlockVertical(var2, var3, var6.getAxisDirection(), var7, var2x -> this.isTip(var2x, var5), var4).orElse(null);
      }
   }

   @Nullable
   private Direction calculateTipDirection(LevelReader var1, BlockPos var2, Direction var3) {
      Direction var4;
      if (this.isValidPointedDripstonePlacement(var1, var2, var3)) {
         var4 = var3;
      } else {
         if (!this.isValidPointedDripstonePlacement(var1, var2, var3.getOpposite())) {
            return null;
         }

         var4 = var3.getOpposite();
      }

      return var4;
   }

   private DripstoneThickness calculateDripstoneThickness(LevelReader var1, BlockPos var2, Direction var3, boolean var4) {
      Direction var5 = var3.getOpposite();
      BlockState var6 = var1.getBlockState(var2.relative(var3));
      if (this.isPointedDripstoneWithDirection(var6, var5)) {
         return !var4 && var6.getValue(THICKNESS) != DripstoneThickness.TIP_MERGE ? DripstoneThickness.TIP : DripstoneThickness.TIP_MERGE;
      } else if (!this.isPointedDripstoneWithDirection(var6, var3)) {
         return DripstoneThickness.TIP;
      } else {
         DripstoneThickness var7 = var6.getValue(THICKNESS);
         if (var7 != DripstoneThickness.TIP && var7 != DripstoneThickness.TIP_MERGE) {
            BlockState var8 = var1.getBlockState(var2.relative(var5));
            return !this.isPointedDripstoneWithDirection(var8, var3) ? DripstoneThickness.BASE : DripstoneThickness.MIDDLE;
         } else {
            return DripstoneThickness.FRUSTUM;
         }
      }
   }

   public boolean canDrip(BlockState var1) {
      return this.isStalactite(var1) && var1.getValue(THICKNESS) == DripstoneThickness.TIP && !var1.getValue(WATERLOGGED);
   }

   private boolean canTipGrow(BlockState var1, ServerLevel var2, BlockPos var3) {
      Direction var4 = var1.getValue(TIP_DIRECTION);
      BlockPos var5 = var3.relative(var4);
      BlockState var6 = var2.getBlockState(var5);
      if (!var6.getFluidState().isEmpty()) {
         return false;
      } else {
         return var6.isAir() ? true : this.isUnmergedTipWithDirection(var6, var4.getOpposite());
      }
   }

   private Optional<BlockPos> findRootBlock(Level var1, BlockPos var2, BlockState var3, int var4) {
      Direction var5 = var3.getValue(TIP_DIRECTION);
      BiPredicate var6 = (var2x, var3x) -> var3x.is(this) && var3x.getValue(TIP_DIRECTION) == var5;
      return findBlockVertical(var1, var2, var5.getOpposite().getAxisDirection(), var6, var1x -> !var1x.is(this), var4);
   }

   private boolean isValidPointedDripstonePlacement(LevelReader var1, BlockPos var2, Direction var3) {
      BlockPos var4 = var2.relative(var3.getOpposite());
      BlockState var5 = var1.getBlockState(var4);
      return (this.organic ? this.isBase(var5) : var5.isFaceSturdy(var1, var4, var3)) || this.isPointedDripstoneWithDirection(var5, var3);
   }

   private boolean isTip(BlockState var1, boolean var2) {
      if (!var1.is(this)) {
         return false;
      } else {
         DripstoneThickness var3 = var1.getValue(THICKNESS);
         return var3 == DripstoneThickness.TIP || var2 && var3 == DripstoneThickness.TIP_MERGE;
      }
   }

   private boolean isUnmergedTipWithDirection(BlockState var1, Direction var2) {
      return this.isTip(var1, false) && var1.getValue(TIP_DIRECTION) == var2;
   }

   private boolean isStalactite(BlockState var1) {
      return this.isPointedDripstoneWithDirection(var1, Direction.DOWN);
   }

   private boolean isStalagmite(BlockState var1) {
      return this.isPointedDripstoneWithDirection(var1, Direction.UP);
   }

   private boolean isStalactiteStartPos(BlockState var1, LevelReader var2, BlockPos var3) {
      return this.isStalactite(var1) && !var2.getBlockState(var3.above()).is(this);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   private boolean isPointedDripstoneWithDirection(BlockState var1, Direction var2) {
      return var1.is(this) && var1.getValue(TIP_DIRECTION) == var2;
   }

   @Nullable
   private static BlockPos findFillableCauldronBelowStalactiteTip(Level var0, BlockPos var1, Fluid var2) {
      Predicate var3 = var1x -> var1x.getBlock() instanceof AbstractCauldronBlock && ((AbstractCauldronBlock)var1x.getBlock()).canReceiveStalactiteDrip(var2);
      BiPredicate var4 = (var1x, var2x) -> canDripThrough(var0, var1x, var2x);
      return findBlockVertical(var0, var1, Direction.DOWN.getAxisDirection(), var4, var3, 11).orElse(null);
   }

   @Nullable
   public static BlockPos findStalactiteTipAboveCauldron(Block var0, Level var1, BlockPos var2) {
      if (var0 instanceof PointedDripstoneBlock var3) {
         BiPredicate var4 = (var1x, var2x) -> canDripThrough(var1, var1x, var2x);
         return findBlockVertical(var1, var2, Direction.UP.getAxisDirection(), var4, var3::canDrip, 11).orElse(null);
      } else {
         return null;
      }
   }

   public Fluid getCauldronFillFluidType(ServerLevel var1, BlockPos var2) {
      return this.getFluidAboveStalactite(var1, var2, var1.getBlockState(var2))
         .map(var0 -> var0.fluid)
         .filter(PointedDripstoneBlock::canFillCauldron)
         .orElse(Fluids.EMPTY);
   }

   private Optional<PointedDripstoneBlock.FluidInfo> getFluidAboveStalactite(Level var1, BlockPos var2, BlockState var3) {
      return !this.isStalactite(var3) ? Optional.empty() : this.findRootBlock(var1, var2, var3, 11).map(var1x -> {
         BlockPos var2xx = var1x.above();
         BlockState var3xx = var1.getBlockState(var2xx);
         Object var4;
         if (var3xx.is(Blocks.MUD) && !var1.dimensionType().ultraWarm()) {
            var4 = Fluids.WATER;
         } else {
            var4 = var1.getFluidState(var2xx).getType();
         }

         return new PointedDripstoneBlock.FluidInfo(var2xx, (Fluid)var4, var3xx);
      });
   }

   private static boolean canFillCauldron(Fluid var0) {
      return var0 == Fluids.LAVA || var0 == Fluids.WATER;
   }

   private boolean canGrow(BlockState var1, BlockState var2) {
      return var1.is(Blocks.DRIPSTONE_BLOCK) && var2.is(Blocks.WATER) && var2.getFluidState().isSource();
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

      for(int var8 = 1; var8 < var5; ++var8) {
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

   static record FluidInfo(BlockPos a, Fluid b, BlockState c) {
      final BlockPos pos;
      final Fluid fluid;
      final BlockState sourceState;

      FluidInfo(BlockPos var1, Fluid var2, BlockState var3) {
         super();
         this.pos = var1;
         this.fluid = var2;
         this.sourceState = var3;
      }
   }
}
