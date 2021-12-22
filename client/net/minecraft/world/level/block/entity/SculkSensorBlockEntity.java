package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;

public class SculkSensorBlockEntity extends BlockEntity implements VibrationListener.VibrationListenerConfig {
   private final VibrationListener listener;
   private int lastVibrationFrequency;

   public SculkSensorBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SCULK_SENSOR, var1, var2);
      this.listener = new VibrationListener(new BlockPositionSource(this.worldPosition), ((SculkSensorBlock)var2.getBlock()).getListenerRange(), this);
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.lastVibrationFrequency = var1.getInt("last_vibration_frequency");
   }

   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      var1.putInt("last_vibration_frequency", this.lastVibrationFrequency);
   }

   public VibrationListener getListener() {
      return this.listener;
   }

   public int getLastVibrationFrequency() {
      return this.lastVibrationFrequency;
   }

   public boolean shouldListen(Level var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5) {
      boolean var6 = var4 == GameEvent.BLOCK_DESTROY && var3.equals(this.getBlockPos());
      boolean var7 = var4 == GameEvent.BLOCK_PLACE && var3.equals(this.getBlockPos());
      return !var6 && !var7 && SculkSensorBlock.canActivate(this.getBlockState());
   }

   public void onSignalReceive(Level var1, GameEventListener var2, GameEvent var3, int var4) {
      BlockState var5 = this.getBlockState();
      if (!var1.isClientSide() && SculkSensorBlock.canActivate(var5)) {
         this.lastVibrationFrequency = SculkSensorBlock.VIBRATION_STRENGTH_FOR_EVENT.getInt(var3);
         SculkSensorBlock.activate(var1, this.worldPosition, var5, getRedstoneStrengthForDistance(var4, var2.getListenerRadius()));
      }

   }

   public static int getRedstoneStrengthForDistance(int var0, int var1) {
      double var2 = (double)var0 / (double)var1;
      return Math.max(1, 15 - Mth.floor(var2 * 15.0D));
   }
}
