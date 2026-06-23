package net.minecraft.world.level.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.Nullable;

public class BedBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<BedBlock> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DyeColor.CODEC.fieldOf("color").forGetter(BedBlock::getColor), propertiesCodec()).apply(i, BedBlock::new));
   public static final EnumProperty<BedPart> PART;
   public static final BooleanProperty OCCUPIED;
   private static final Map<Direction, VoxelShape> SHAPES;
   private final DyeColor color;

   public MapCodec<BedBlock> codec() {
      return CODEC;
   }

   public BedBlock(final DyeColor color, final BlockBehaviour.Properties properties) {
      super(properties);
      this.color = color;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PART, BedPart.FOOT)).setValue(OCCUPIED, false));
   }

   public static @Nullable Direction getBedOrientation(final BlockGetter level, final BlockPos pos) {
      BlockState blockState = level.getBlockState(pos);
      return blockState.getBlock() instanceof BedBlock ? (Direction)blockState.getValue(FACING) : null;
   }

   protected InteractionResult useWithoutItem(BlockState state, final Level level, BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS_SERVER;
      } else {
         if (state.getValue(PART) != BedPart.HEAD) {
            pos = pos.relative((Direction)state.getValue(FACING));
            state = level.getBlockState(pos);
            if (!state.is(this)) {
               return InteractionResult.CONSUME;
            }
         }

         BedRule bedRule = (BedRule)level.environmentAttributes().getValue(EnvironmentAttributes.BED_RULE, pos);
         if (bedRule.explodes()) {
            Optional var10000 = bedRule.errorMessage();
            Objects.requireNonNull(player);
            var10000.ifPresent(player::sendOverlayMessage);
            level.removeBlock(pos, false);
            BlockPos blockPos = pos.relative(((Direction)state.getValue(FACING)).getOpposite());
            if (level.getBlockState(blockPos).is(this)) {
               level.removeBlock(blockPos, false);
            }

            Vec3 boomPos = Vec3.atCenterOf(pos);
            level.explode((Entity)null, level.damageSources().badRespawnPointExplosion(boomPos), (ExplosionDamageCalculator)null, boomPos, 5.0F, true, Level.ExplosionInteraction.BLOCK);
            return InteractionResult.SUCCESS_SERVER;
         } else if ((Boolean)state.getValue(OCCUPIED)) {
            if (!this.kickVillagerOutOfBed(level, pos)) {
               player.sendOverlayMessage(Component.translatable("block.minecraft.bed.occupied"));
            }

            return InteractionResult.SUCCESS_SERVER;
         } else {
            player.startSleepInBed(pos).ifLeft((problem) -> {
               if (problem.message() != null) {
                  player.sendOverlayMessage(problem.message());
               }

            });
            return InteractionResult.SUCCESS_SERVER;
         }
      }
   }

   private boolean kickVillagerOutOfBed(final Level level, final BlockPos pos) {
      List<Villager> villagers = level.getEntitiesOfClass(Villager.class, new AABB(pos), LivingEntity::isSleeping);
      if (villagers.isEmpty()) {
         return false;
      } else {
         ((Villager)villagers.get(0)).stopSleeping();
         return true;
      }
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (directionToNeighbour == getNeighbourDirection((BedPart)state.getValue(PART), (Direction)state.getValue(FACING))) {
         return neighbourState.is(this) && neighbourState.getValue(PART) != state.getValue(PART) ? (BlockState)state.setValue(OCCUPIED, (Boolean)neighbourState.getValue(OCCUPIED)) : Blocks.AIR.defaultBlockState();
      } else {
         return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
      }
   }

   private static Direction getNeighbourDirection(final BedPart part, final Direction facing) {
      return part == BedPart.FOOT ? facing : facing.getOpposite();
   }

   public BlockState playerWillDestroy(final Level level, final BlockPos pos, final BlockState state, final Player player) {
      if (!level.isClientSide() && player.preventsBlockDrops()) {
         BedPart part = (BedPart)state.getValue(PART);
         if (part == BedPart.FOOT) {
            BlockPos headPos = pos.relative(getNeighbourDirection(part, (Direction)state.getValue(FACING)));
            BlockState headState = level.getBlockState(headPos);
            if (headState.is(this) && headState.getValue(PART) == BedPart.HEAD) {
               level.setBlock(headPos, Blocks.AIR.defaultBlockState(), 35);
               level.levelEvent(player, 2001, headPos, Block.getId(headState));
            }
         }
      }

      return super.playerWillDestroy(level, pos, state, player);
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      Direction facing = context.getHorizontalDirection();
      BlockPos pos = context.getClickedPos();
      BlockPos relative = pos.relative(facing);
      Level level = context.getLevel();
      return level.getBlockState(relative).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(relative) ? (BlockState)this.defaultBlockState().setValue(FACING, facing) : null;
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)SHAPES.get(getConnectedDirection(state).getOpposite());
   }

   public static Direction getConnectedDirection(final BlockState state) {
      Direction facing = (Direction)state.getValue(FACING);
      return state.getValue(PART) == BedPart.HEAD ? facing.getOpposite() : facing;
   }

   public static DoubleBlockCombiner.BlockType getBlockType(final BlockState state) {
      BedPart part = (BedPart)state.getValue(PART);
      return part == BedPart.HEAD ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
   }

   private static boolean isBunkBed(final BlockGetter level, final BlockPos pos) {
      return level.getBlockState(pos.below()).getBlock() instanceof BedBlock;
   }

   public static Optional<Vec3> findStandUpPosition(final EntityType<?> type, final CollisionGetter level, final BlockPos pos, final Direction forward, final float yaw) {
      Direction right = forward.getClockWise();
      Direction side = right.isFacingAngle(yaw) ? right.getOpposite() : right;
      if (isBunkBed(level, pos)) {
         return findBunkBedStandUpPosition(type, level, pos, forward, side);
      } else {
         int[][] offsets = bedStandUpOffsets(forward, side);
         Optional<Vec3> safePosition = findStandUpPositionAtOffset(type, level, pos, offsets, true);
         return safePosition.isPresent() ? safePosition : findStandUpPositionAtOffset(type, level, pos, offsets, false);
      }
   }

   private static Optional<Vec3> findBunkBedStandUpPosition(final EntityType<?> type, final CollisionGetter level, final BlockPos pos, final Direction forward, final Direction side) {
      int[][] offsets = bedSurroundStandUpOffsets(forward, side);
      Optional<Vec3> safePosition = findStandUpPositionAtOffset(type, level, pos, offsets, true);
      if (safePosition.isPresent()) {
         return safePosition;
      } else {
         BlockPos below = pos.below();
         Optional<Vec3> belowSafePosition = findStandUpPositionAtOffset(type, level, below, offsets, true);
         if (belowSafePosition.isPresent()) {
            return belowSafePosition;
         } else {
            int[][] aboveOffsets = bedAboveStandUpOffsets(forward);
            Optional<Vec3> aboveSafePosition = findStandUpPositionAtOffset(type, level, pos, aboveOffsets, true);
            if (aboveSafePosition.isPresent()) {
               return aboveSafePosition;
            } else {
               Optional<Vec3> unsafePosition = findStandUpPositionAtOffset(type, level, pos, offsets, false);
               if (unsafePosition.isPresent()) {
                  return unsafePosition;
               } else {
                  Optional<Vec3> belowUnsafePosition = findStandUpPositionAtOffset(type, level, below, offsets, false);
                  return belowUnsafePosition.isPresent() ? belowUnsafePosition : findStandUpPositionAtOffset(type, level, pos, aboveOffsets, false);
               }
            }
         }
      }
   }

   private static Optional<Vec3> findStandUpPositionAtOffset(final EntityType<?> type, final CollisionGetter level, final BlockPos pos, final int[][] offsets, final boolean checkDangerous) {
      BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

      for(int[] offset : offsets) {
         blockPos.set(pos.getX() + offset[0], pos.getY(), pos.getZ() + offset[1]);
         Vec3 position = DismountHelper.findSafeDismountLocation(type, level, blockPos, checkDangerous);
         if (position != null) {
            return Optional.of(position);
         }
      }

      return Optional.empty();
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, PART, OCCUPIED);
   }

   public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final @Nullable LivingEntity by, final ItemStack itemStack) {
      super.setPlacedBy(level, pos, state, by, itemStack);
      BlockPos otherPos = pos.relative((Direction)state.getValue(FACING));
      level.setBlockAndUpdate(otherPos, (BlockState)state.setValue(PART, BedPart.HEAD));
   }

   public DyeColor getColor() {
      return this.color;
   }

   protected long getSeed(final BlockState state, final BlockPos pos) {
      BlockPos sourcePos = pos.relative((Direction)state.getValue(FACING), state.getValue(PART) == BedPart.HEAD ? 0 : 1);
      return Mth.getSeed(sourcePos.getX(), pos.getY(), sourcePos.getZ());
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   private static int[][] bedStandUpOffsets(final Direction forward, final Direction side) {
      return (int[][])ArrayUtils.addAll(bedSurroundStandUpOffsets(forward, side), bedAboveStandUpOffsets(forward));
   }

   private static int[][] bedSurroundStandUpOffsets(final Direction forward, final Direction side) {
      return new int[][]{{side.getStepX(), side.getStepZ()}, {side.getStepX() - forward.getStepX(), side.getStepZ() - forward.getStepZ()}, {side.getStepX() - forward.getStepX() * 2, side.getStepZ() - forward.getStepZ() * 2}, {-forward.getStepX() * 2, -forward.getStepZ() * 2}, {-side.getStepX() - forward.getStepX() * 2, -side.getStepZ() - forward.getStepZ() * 2}, {-side.getStepX() - forward.getStepX(), -side.getStepZ() - forward.getStepZ()}, {-side.getStepX(), -side.getStepZ()}, {-side.getStepX() + forward.getStepX(), -side.getStepZ() + forward.getStepZ()}, {forward.getStepX(), forward.getStepZ()}, {side.getStepX() + forward.getStepX(), side.getStepZ() + forward.getStepZ()}};
   }

   private static int[][] bedAboveStandUpOffsets(final Direction forward) {
      return new int[][]{{0, 0}, {-forward.getStepX(), -forward.getStepZ()}};
   }

   static {
      PART = BlockStateProperties.BED_PART;
      OCCUPIED = BlockStateProperties.OCCUPIED;
      SHAPES = (Map)Util.make(() -> {
         VoxelShape northWestLeg = Block.box(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
         VoxelShape northEastLeg = Shapes.rotate(northWestLeg, OctahedralGroup.BLOCK_ROT_Y_90);
         return Shapes.rotateHorizontal(Shapes.or(Block.column(16.0, 3.0, 9.0), northWestLeg, northEastLeg));
      });
   }
}
