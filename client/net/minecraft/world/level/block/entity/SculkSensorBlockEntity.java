package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import org.slf4j.Logger;

public class SculkSensorBlockEntity extends BlockEntity implements VibrationListener.VibrationListenerConfig {
   private static final Logger LOGGER = LogUtils.getLogger();
   private VibrationListener listener;
   private int lastVibrationFrequency;

   public SculkSensorBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SCULK_SENSOR, var1, var2);
      this.listener = new VibrationListener(new BlockPositionSource(this.worldPosition), ((SculkSensorBlock)var2.getBlock()).getListenerRange(), this, (VibrationListener.ReceivingEvent)null, 0.0F, 0);
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.lastVibrationFrequency = var1.getInt("last_vibration_frequency");
      if (var1.contains("listener", 10)) {
         DataResult var10000 = VibrationListener.codec(this).parse(new Dynamic(NbtOps.INSTANCE, var1.getCompound("listener")));
         Logger var10001 = LOGGER;
         Objects.requireNonNull(var10001);
         var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
            this.listener = var1x;
         });
      }

   }

   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      var1.putInt("last_vibration_frequency", this.lastVibrationFrequency);
      DataResult var10000 = VibrationListener.codec(this).encodeStart(NbtOps.INSTANCE, this.listener);
      Logger var10001 = LOGGER;
      Objects.requireNonNull(var10001);
      var10000.resultOrPartial(var10001::error).ifPresent((var1x) -> {
         var1.put("listener", var1x);
      });
   }

   public VibrationListener getListener() {
      return this.listener;
   }

   public int getLastVibrationFrequency() {
      return this.lastVibrationFrequency;
   }

   public boolean canTriggerAvoidVibration() {
      return true;
   }

   public boolean shouldListen(ServerLevel var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable GameEvent.Context var5) {
      return !this.isRemoved() && (!var3.equals(this.getBlockPos()) || var4 != GameEvent.BLOCK_DESTROY && var4 != GameEvent.BLOCK_PLACE) ? SculkSensorBlock.canActivate(this.getBlockState()) : false;
   }

   public void onSignalReceive(ServerLevel var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5, @Nullable Entity var6, float var7) {
      BlockState var8 = this.getBlockState();
      if (SculkSensorBlock.canActivate(var8)) {
         this.lastVibrationFrequency = SculkSensorBlock.VIBRATION_FREQUENCY_FOR_EVENT.getInt(var4);
         SculkSensorBlock.activate(var5, var1, this.worldPosition, var8, getRedstoneStrengthForDistance(var7, var2.getListenerRadius()));
      }

   }

   public void onSignalSchedule() {
      this.setChanged();
   }

   public static int getRedstoneStrengthForDistance(float var0, int var1) {
      double var2 = (double)var0 / (double)var1;
      return Math.max(1, 15 - Mth.floor(var2 * 15.0));
   }

   public void setLastVibrationFrequency(int var1) {
      this.lastVibrationFrequency = var1;
   }
}
