package net.minecraft.world.level.block.entity;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CalibratedSculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jspecify.annotations.Nullable;

public class CalibratedSculkSensorBlockEntity extends SculkSensorBlockEntity {
   public CalibratedSculkSensorBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      super(BlockEntityType.CALIBRATED_SCULK_SENSOR, worldPosition, blockState);
   }

   public VibrationSystem.User createVibrationUser() {
      return new VibrationUser(this.getBlockPos());
   }

   protected class VibrationUser extends SculkSensorBlockEntity.VibrationUser {
      public VibrationUser(final BlockPos blockPos) {
         Objects.requireNonNull(CalibratedSculkSensorBlockEntity.this);
         super(blockPos);
      }

      public int getListenerRadius() {
         return 16;
      }

      public boolean canReceiveVibration(final ServerLevel level, final BlockPos pos, final Holder<GameEvent> event, final GameEvent.@Nullable Context context) {
         int comparisonType = this.getBackSignal(level, this.blockPos, CalibratedSculkSensorBlockEntity.this.getBlockState());
         return comparisonType != 0 && VibrationSystem.getGameEventFrequency(event) != comparisonType ? false : super.canReceiveVibration(level, pos, event, context);
      }

      private int getBackSignal(final Level level, final BlockPos pos, final BlockState state) {
         Direction direction = ((Direction)state.getValue(CalibratedSculkSensorBlock.FACING)).getOpposite();
         return level.getSignal(pos.relative(direction), direction);
      }
   }
}
