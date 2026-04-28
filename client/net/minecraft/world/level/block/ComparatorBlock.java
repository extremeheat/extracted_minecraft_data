package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;
import org.jspecify.annotations.Nullable;

public class ComparatorBlock extends DiodeBlock implements EntityBlock {
   public static final MapCodec<ComparatorBlock> CODEC = simpleCodec(ComparatorBlock::new);
   public static final EnumProperty<ComparatorMode> MODE;

   public MapCodec<ComparatorBlock> codec() {
      return CODEC;
   }

   public ComparatorBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(MODE, ComparatorMode.COMPARE));
   }

   protected int getDelay(final BlockState state) {
      return 2;
   }

   public BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      return directionToNeighbour == Direction.DOWN && !this.canSurviveOn(level, neighbourPos, neighbourState) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected int getOutputSignal(final BlockGetter level, final BlockPos pos, final BlockState state) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof ComparatorBlockEntity comparatorBlockEntity) {
         return comparatorBlockEntity.getOutputSignal();
      } else {
         return 0;
      }
   }

   private int calculateOutputSignal(final Level level, final BlockPos pos, final BlockState state) {
      int inputSignal = this.getInputSignal(level, pos, state);
      if (inputSignal == 0) {
         return 0;
      } else {
         int alternateSignal = this.getAlternateSignal(level, pos, state);
         if (alternateSignal > inputSignal) {
            return 0;
         } else {
            return state.getValue(MODE) == ComparatorMode.SUBTRACT ? inputSignal - alternateSignal : inputSignal;
         }
      }
   }

   protected boolean shouldTurnOn(final Level level, final BlockPos pos, final BlockState state) {
      int input = this.getInputSignal(level, pos, state);
      if (input == 0) {
         return false;
      } else {
         int sideInput = this.getAlternateSignal(level, pos, state);
         if (input > sideInput) {
            return true;
         } else {
            return input == sideInput && state.getValue(MODE) == ComparatorMode.COMPARE;
         }
      }
   }

   protected int getInputSignal(final Level level, final BlockPos pos, final BlockState state) {
      int resultSignal = super.getInputSignal(level, pos, state);
      Direction direction = (Direction)state.getValue(FACING);
      BlockPos targetPos = pos.relative(direction);
      BlockState targetState = level.getBlockState(targetPos);
      if (targetState.hasAnalogOutputSignal()) {
         resultSignal = targetState.getAnalogOutputSignal(level, targetPos, direction.getOpposite());
      } else if (resultSignal < 15 && targetState.isRedstoneConductor(level, targetPos)) {
         targetPos = targetPos.relative(direction);
         targetState = level.getBlockState(targetPos);
         ItemFrame itemFrame = this.getItemFrame(level, direction, targetPos);
         int itemFrameOrBlockSignal = Math.max(itemFrame == null ? -2147483648 : itemFrame.getAnalogOutput(), targetState.hasAnalogOutputSignal() ? targetState.getAnalogOutputSignal(level, targetPos, direction.getOpposite()) : -2147483648);
         if (itemFrameOrBlockSignal != -2147483648) {
            resultSignal = itemFrameOrBlockSignal;
         }
      }

      return resultSignal;
   }

   private @Nullable ItemFrame getItemFrame(final Level level, final Direction direction, final BlockPos tPos) {
      List<ItemFrame> itemFrames = level.getEntitiesOfClass(ItemFrame.class, new AABB((double)tPos.getX(), (double)tPos.getY(), (double)tPos.getZ(), (double)(tPos.getX() + 1), (double)(tPos.getY() + 1), (double)(tPos.getZ() + 1)), (entity) -> entity.getDirection() == direction);
      return itemFrames.size() == 1 ? (ItemFrame)itemFrames.get(0) : null;
   }

   protected InteractionResult useWithoutItem(BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (!player.getAbilities().mayBuild) {
         return InteractionResult.PASS;
      } else {
         state = (BlockState)state.cycle(MODE);
         float pitch = state.getValue(MODE) == ComparatorMode.SUBTRACT ? 0.55F : 0.5F;
         level.playSound(player, (BlockPos)pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, pitch);
         level.setBlock(pos, state, 2);
         if (level.getBlockState(pos).is(this)) {
            this.refreshOutputState(level, pos, state);
         }

         return InteractionResult.SUCCESS;
      }
   }

   protected void checkTickOnNeighbor(final Level level, final BlockPos pos, final BlockState state) {
      if (!level.getBlockTicks().willTickThisTick(pos, this)) {
         int outputValue = this.calculateOutputSignal(level, pos, state);
         BlockEntity blockEntity = level.getBlockEntity(pos);
         int var10000;
         if (blockEntity instanceof ComparatorBlockEntity) {
            ComparatorBlockEntity comparatorBlockEntity = (ComparatorBlockEntity)blockEntity;
            var10000 = comparatorBlockEntity.getOutputSignal();
         } else {
            var10000 = 0;
         }

         int oldValue = var10000;
         if (outputValue != oldValue || (Boolean)state.getValue(POWERED) != this.shouldTurnOn(level, pos, state)) {
            TickPriority priority = this.shouldPrioritize(level, pos, state) ? TickPriority.HIGH : TickPriority.NORMAL;
            level.scheduleTick(pos, this, 2, priority);
         }

      }
   }

   private void refreshOutputState(final Level level, final BlockPos pos, final BlockState state) {
      int outputValue = this.calculateOutputSignal(level, pos, state);
      BlockEntity blockEntity = level.getBlockEntity(pos);
      int oldValue = 0;
      if (blockEntity instanceof ComparatorBlockEntity comparatorBlockEntity) {
         oldValue = comparatorBlockEntity.getOutputSignal();
         comparatorBlockEntity.setOutputSignal(outputValue);
      }

      if (oldValue != outputValue || state.getValue(MODE) == ComparatorMode.COMPARE) {
         boolean sourceOn = this.shouldTurnOn(level, pos, state);
         boolean isOn = (Boolean)state.getValue(POWERED);
         if (isOn && !sourceOn) {
            level.setBlock(pos, (BlockState)state.setValue(POWERED, false), 2);
         } else if (!isOn && sourceOn) {
            level.setBlock(pos, (BlockState)state.setValue(POWERED, true), 2);
         }

         this.updateNeighborsInFront(level, pos, state);
      }

   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      this.refreshOutputState(level, pos, state);
   }

   protected boolean triggerEvent(final BlockState state, final Level level, final BlockPos pos, final int b0, final int b1) {
      super.triggerEvent(state, level, pos, b0, b1);
      BlockEntity blockEntity = level.getBlockEntity(pos);
      return blockEntity != null && blockEntity.triggerEvent(b0, b1);
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new ComparatorBlockEntity(worldPosition, blockState);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, MODE, POWERED);
   }

   static {
      MODE = BlockStateProperties.MODE_COMPARATOR;
   }
}
