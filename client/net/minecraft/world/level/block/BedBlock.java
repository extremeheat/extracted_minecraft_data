package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.ArrayUtils;

public class BedBlock extends HorizontalDirectionalBlock implements EntityBlock {
   public static final EnumProperty<BedPart> PART;
   public static final BooleanProperty OCCUPIED;
   protected static final int HEIGHT = 9;
   protected static final VoxelShape BASE;
   private static final int LEG_WIDTH = 3;
   protected static final VoxelShape LEG_NORTH_WEST;
   protected static final VoxelShape LEG_SOUTH_WEST;
   protected static final VoxelShape LEG_NORTH_EAST;
   protected static final VoxelShape LEG_SOUTH_EAST;
   protected static final VoxelShape NORTH_SHAPE;
   protected static final VoxelShape SOUTH_SHAPE;
   protected static final VoxelShape WEST_SHAPE;
   protected static final VoxelShape EAST_SHAPE;
   private final DyeColor color;

   public BedBlock(DyeColor var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.color = var1;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PART, BedPart.FOOT)).setValue(OCCUPIED, false));
   }

   @Nullable
   public static Direction getBedOrientation(BlockGetter var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      return var2.getBlock() instanceof BedBlock ? (Direction)var2.getValue(FACING) : null;
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return InteractionResult.CONSUME;
      } else {
         if (var1.getValue(PART) != BedPart.HEAD) {
            var3 = var3.relative((Direction)var1.getValue(FACING));
            var1 = var2.getBlockState(var3);
            if (!var1.is(this)) {
               return InteractionResult.CONSUME;
            }
         }

         if (!canSetSpawn(var2)) {
            var2.removeBlock(var3, false);
            BlockPos var7 = var3.relative(((Direction)var1.getValue(FACING)).getOpposite());
            if (var2.getBlockState(var7).is(this)) {
               var2.removeBlock(var7, false);
            }

            var2.explode((Entity)null, DamageSource.badRespawnPointExplosion(), (ExplosionDamageCalculator)null, (double)var3.getX() + 0.5, (double)var3.getY() + 0.5, (double)var3.getZ() + 0.5, 5.0F, true, Explosion.BlockInteraction.DESTROY);
            return InteractionResult.SUCCESS;
         } else if ((Boolean)var1.getValue(OCCUPIED)) {
            if (!this.kickVillagerOutOfBed(var2, var3)) {
               var4.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
            }

            return InteractionResult.SUCCESS;
         } else {
            var4.startSleepInBed(var3).ifLeft((var1x) -> {
               if (var1x.getMessage() != null) {
                  var4.displayClientMessage(var1x.getMessage(), true);
               }

            });
            return InteractionResult.SUCCESS;
         }
      }
   }

   public static boolean canSetSpawn(Level var0) {
      return var0.dimensionType().bedWorks();
   }

   private boolean kickVillagerOutOfBed(Level var1, BlockPos var2) {
      List var3 = var1.getEntitiesOfClass(Villager.class, new AABB(var2), LivingEntity::isSleeping);
      if (var3.isEmpty()) {
         return false;
      } else {
         ((Villager)var3.get(0)).stopSleeping();
         return true;
      }
   }

   public void fallOn(Level var1, BlockState var2, BlockPos var3, Entity var4, float var5) {
      super.fallOn(var1, var2, var3, var4, var5 * 0.5F);
   }

   public void updateEntityAfterFallOn(BlockGetter var1, Entity var2) {
      if (var2.isSuppressingBounce()) {
         super.updateEntityAfterFallOn(var1, var2);
      } else {
         this.bounceUp(var2);
      }

   }

   private void bounceUp(Entity var1) {
      Vec3 var2 = var1.getDeltaMovement();
      if (var2.y < 0.0) {
         double var3 = var1 instanceof LivingEntity ? 1.0 : 0.8;
         var1.setDeltaMovement(var2.x, -var2.y * 0.6600000262260437 * var3, var2.z);
      }

   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == getNeighbourDirection((BedPart)var1.getValue(PART), (Direction)var1.getValue(FACING))) {
         return var3.is(this) && var3.getValue(PART) != var1.getValue(PART) ? (BlockState)var1.setValue(OCCUPIED, (Boolean)var3.getValue(OCCUPIED)) : Blocks.AIR.defaultBlockState();
      } else {
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      }
   }

   private static Direction getNeighbourDirection(BedPart var0, Direction var1) {
      return var0 == BedPart.FOOT ? var1 : var1.getOpposite();
   }

   public void playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var1.isClientSide && var4.isCreative()) {
         BedPart var5 = (BedPart)var3.getValue(PART);
         if (var5 == BedPart.FOOT) {
            BlockPos var6 = var2.relative(getNeighbourDirection(var5, (Direction)var3.getValue(FACING)));
            BlockState var7 = var1.getBlockState(var6);
            if (var7.is(this) && var7.getValue(PART) == BedPart.HEAD) {
               var1.setBlock(var6, Blocks.AIR.defaultBlockState(), 35);
               var1.levelEvent(var4, 2001, var6, Block.getId(var7));
            }
         }
      }

      super.playerWillDestroy(var1, var2, var3, var4);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Direction var2 = var1.getHorizontalDirection();
      BlockPos var3 = var1.getClickedPos();
      BlockPos var4 = var3.relative(var2);
      Level var5 = var1.getLevel();
      return var5.getBlockState(var4).canBeReplaced(var1) && var5.getWorldBorder().isWithinBounds(var4) ? (BlockState)this.defaultBlockState().setValue(FACING, var2) : null;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Direction var5 = getConnectedDirection(var1).getOpposite();
      switch (var5) {
         case NORTH:
            return NORTH_SHAPE;
         case SOUTH:
            return SOUTH_SHAPE;
         case WEST:
            return WEST_SHAPE;
         default:
            return EAST_SHAPE;
      }
   }

   public static Direction getConnectedDirection(BlockState var0) {
      Direction var1 = (Direction)var0.getValue(FACING);
      return var0.getValue(PART) == BedPart.HEAD ? var1.getOpposite() : var1;
   }

   public static DoubleBlockCombiner.BlockType getBlockType(BlockState var0) {
      BedPart var1 = (BedPart)var0.getValue(PART);
      return var1 == BedPart.HEAD ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
   }

   private static boolean isBunkBed(BlockGetter var0, BlockPos var1) {
      return var0.getBlockState(var1.below()).getBlock() instanceof BedBlock;
   }

   public static Optional<Vec3> findStandUpPosition(EntityType<?> var0, CollisionGetter var1, BlockPos var2, float var3) {
      Direction var4 = (Direction)var1.getBlockState(var2).getValue(FACING);
      Direction var5 = var4.getClockWise();
      Direction var6 = var5.isFacingAngle(var3) ? var5.getOpposite() : var5;
      if (isBunkBed(var1, var2)) {
         return findBunkBedStandUpPosition(var0, var1, var2, var4, var6);
      } else {
         int[][] var7 = bedStandUpOffsets(var4, var6);
         Optional var8 = findStandUpPositionAtOffset(var0, var1, var2, var7, true);
         return var8.isPresent() ? var8 : findStandUpPositionAtOffset(var0, var1, var2, var7, false);
      }
   }

   private static Optional<Vec3> findBunkBedStandUpPosition(EntityType<?> var0, CollisionGetter var1, BlockPos var2, Direction var3, Direction var4) {
      int[][] var5 = bedSurroundStandUpOffsets(var3, var4);
      Optional var6 = findStandUpPositionAtOffset(var0, var1, var2, var5, true);
      if (var6.isPresent()) {
         return var6;
      } else {
         BlockPos var7 = var2.below();
         Optional var8 = findStandUpPositionAtOffset(var0, var1, var7, var5, true);
         if (var8.isPresent()) {
            return var8;
         } else {
            int[][] var9 = bedAboveStandUpOffsets(var3);
            Optional var10 = findStandUpPositionAtOffset(var0, var1, var2, var9, true);
            if (var10.isPresent()) {
               return var10;
            } else {
               Optional var11 = findStandUpPositionAtOffset(var0, var1, var2, var5, false);
               if (var11.isPresent()) {
                  return var11;
               } else {
                  Optional var12 = findStandUpPositionAtOffset(var0, var1, var7, var5, false);
                  return var12.isPresent() ? var12 : findStandUpPositionAtOffset(var0, var1, var2, var9, false);
               }
            }
         }
      }
   }

   private static Optional<Vec3> findStandUpPositionAtOffset(EntityType<?> var0, CollisionGetter var1, BlockPos var2, int[][] var3, boolean var4) {
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();
      int[][] var6 = var3;
      int var7 = var3.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         int[] var9 = var6[var8];
         var5.set(var2.getX() + var9[0], var2.getY(), var2.getZ() + var9[1]);
         Vec3 var10 = DismountHelper.findSafeDismountLocation(var0, var1, var5, var4);
         if (var10 != null) {
            return Optional.of(var10);
         }
      }

      return Optional.empty();
   }

   public PushReaction getPistonPushReaction(BlockState var1) {
      return PushReaction.DESTROY;
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, PART, OCCUPIED);
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new BedBlockEntity(var1, var2, this.color);
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      super.setPlacedBy(var1, var2, var3, var4, var5);
      if (!var1.isClientSide) {
         BlockPos var6 = var2.relative((Direction)var3.getValue(FACING));
         var1.setBlock(var6, (BlockState)var3.setValue(PART, BedPart.HEAD), 3);
         var1.blockUpdated(var2, Blocks.AIR);
         var3.updateNeighbourShapes(var1, var2, 3);
      }

   }

   public DyeColor getColor() {
      return this.color;
   }

   public long getSeed(BlockState var1, BlockPos var2) {
      BlockPos var3 = var2.relative((Direction)var1.getValue(FACING), var1.getValue(PART) == BedPart.HEAD ? 0 : 1);
      return Mth.getSeed(var3.getX(), var2.getY(), var3.getZ());
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   private static int[][] bedStandUpOffsets(Direction var0, Direction var1) {
      return (int[][])ArrayUtils.addAll(bedSurroundStandUpOffsets(var0, var1), bedAboveStandUpOffsets(var0));
   }

   private static int[][] bedSurroundStandUpOffsets(Direction var0, Direction var1) {
      return new int[][]{{var1.getStepX(), var1.getStepZ()}, {var1.getStepX() - var0.getStepX(), var1.getStepZ() - var0.getStepZ()}, {var1.getStepX() - var0.getStepX() * 2, var1.getStepZ() - var0.getStepZ() * 2}, {-var0.getStepX() * 2, -var0.getStepZ() * 2}, {-var1.getStepX() - var0.getStepX() * 2, -var1.getStepZ() - var0.getStepZ() * 2}, {-var1.getStepX() - var0.getStepX(), -var1.getStepZ() - var0.getStepZ()}, {-var1.getStepX(), -var1.getStepZ()}, {-var1.getStepX() + var0.getStepX(), -var1.getStepZ() + var0.getStepZ()}, {var0.getStepX(), var0.getStepZ()}, {var1.getStepX() + var0.getStepX(), var1.getStepZ() + var0.getStepZ()}};
   }

   private static int[][] bedAboveStandUpOffsets(Direction var0) {
      return new int[][]{{0, 0}, {-var0.getStepX(), -var0.getStepZ()}};
   }

   static {
      PART = BlockStateProperties.BED_PART;
      OCCUPIED = BlockStateProperties.OCCUPIED;
      BASE = Block.box(0.0, 3.0, 0.0, 16.0, 9.0, 16.0);
      LEG_NORTH_WEST = Block.box(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
      LEG_SOUTH_WEST = Block.box(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
      LEG_NORTH_EAST = Block.box(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
      LEG_SOUTH_EAST = Block.box(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);
      NORTH_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_NORTH_EAST);
      SOUTH_SHAPE = Shapes.or(BASE, LEG_SOUTH_WEST, LEG_SOUTH_EAST);
      WEST_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_SOUTH_WEST);
      EAST_SHAPE = Shapes.or(BASE, LEG_NORTH_EAST, LEG_SOUTH_EAST);
   }
}
