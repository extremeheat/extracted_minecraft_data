package net.minecraft.world.level.block.entity;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class SculkSensorBlockEntity extends BlockEntity implements GameEventListener.Provider<VibrationSystem.Listener>, VibrationSystem {
   private static final int DEFAULT_LAST_VIBRATION_FREQUENCY = 0;
   private VibrationSystem.Data vibrationData;
   private final VibrationSystem.Listener vibrationListener;
   private final VibrationSystem.User vibrationUser;
   private int lastVibrationFrequency;

   protected SculkSensorBlockEntity(final BlockEntityType<?> type, final BlockPos worldPosition, final BlockState blockState) {
      super(type, worldPosition, blockState);
      this.lastVibrationFrequency = 0;
      this.vibrationUser = this.createVibrationUser();
      this.vibrationData = new VibrationSystem.Data();
      this.vibrationListener = new VibrationSystem.Listener(this);
   }

   public SculkSensorBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      this(BlockEntityType.SCULK_SENSOR, worldPosition, blockState);
   }

   public VibrationSystem.User createVibrationUser() {
      return new VibrationUser(this.getBlockPos());
   }

   protected void loadAdditional(final ValueInput input) {
      super.loadAdditional(input);
      this.lastVibrationFrequency = input.getIntOr("last_vibration_frequency", 0);
      this.vibrationData = (VibrationSystem.Data)input.read("listener", VibrationSystem.Data.CODEC).orElseGet(VibrationSystem.Data::new);
   }

   protected void saveAdditional(final ValueOutput output) {
      super.saveAdditional(output);
      output.putInt("last_vibration_frequency", this.lastVibrationFrequency);
      output.store("listener", VibrationSystem.Data.CODEC, this.vibrationData);
   }

   public VibrationSystem.Data getVibrationData() {
      return this.vibrationData;
   }

   public VibrationSystem.User getVibrationUser() {
      return this.vibrationUser;
   }

   public int getLastVibrationFrequency() {
      return this.lastVibrationFrequency;
   }

   public void setLastVibrationFrequency(final int lastVibrationFrequency) {
      this.lastVibrationFrequency = lastVibrationFrequency;
   }

   public VibrationSystem.Listener getListener() {
      return this.vibrationListener;
   }

   protected class VibrationUser implements VibrationSystem.User {
      public static final int LISTENER_RANGE = 8;
      protected final BlockPos blockPos;
      private final PositionSource positionSource;

      public VibrationUser(final BlockPos blockPos) {
         Objects.requireNonNull(SculkSensorBlockEntity.this);
         super();
         this.blockPos = blockPos;
         this.positionSource = new BlockPositionSource(blockPos);
      }

      public int getListenerRadius() {
         return 8;
      }

      public PositionSource getPositionSource() {
         return this.positionSource;
      }

      public boolean canTriggerAvoidVibration() {
         return true;
      }

      public boolean canReceiveVibration(final ServerLevel level, final BlockPos pos, final Holder<GameEvent> event, final GameEvent.@Nullable Context context) {
         if (!pos.equals(this.blockPos) || !event.is((Holder)GameEvent.BLOCK_DESTROY) && !event.is((Holder)GameEvent.BLOCK_PLACE)) {
            return VibrationSystem.getGameEventFrequency(event) == 0 ? false : SculkSensorBlock.canActivate(SculkSensorBlockEntity.this.getBlockState());
         } else {
            return false;
         }
      }

      public void onReceiveVibration(final ServerLevel level, final BlockPos pos, final Holder<GameEvent> event, final @Nullable Entity sourceEntity, final @Nullable Entity projectileOwner, final float receivingDistance) {
         BlockState state = SculkSensorBlockEntity.this.getBlockState();
         if (SculkSensorBlock.canActivate(state)) {
            int eventFrequency = VibrationSystem.getGameEventFrequency(event);
            SculkSensorBlockEntity.this.setLastVibrationFrequency(eventFrequency);
            int calculatedPower = VibrationSystem.getRedstoneStrengthForDistance(receivingDistance, this.getListenerRadius());
            Block var11 = state.getBlock();
            if (var11 instanceof SculkSensorBlock) {
               SculkSensorBlock sculkSensorBlock = (SculkSensorBlock)var11;
               sculkSensorBlock.activate(sourceEntity, level, this.blockPos, state, calculatedPower, eventFrequency);
            }
         }

      }

      public void onDataChanged() {
         SculkSensorBlockEntity.this.setChanged();
      }

      public boolean requiresAdjacentChunksToBeTicking() {
         return true;
      }
   }
}
