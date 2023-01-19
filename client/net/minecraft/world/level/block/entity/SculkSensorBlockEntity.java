package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
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
      this.listener = new VibrationListener(new BlockPositionSource(this.worldPosition), ((SculkSensorBlock)var2.getBlock()).getListenerRange(), this);
   }

   @Override
   public void load(CompoundTag var1) {
      super.load(var1);
      this.lastVibrationFrequency = var1.getInt("last_vibration_frequency");
      if (var1.contains("listener", 10)) {
         VibrationListener.codec(this)
            .parse(new Dynamic(NbtOps.INSTANCE, var1.getCompound("listener")))
            .resultOrPartial(LOGGER::error)
            .ifPresent(var1x -> this.listener = var1x);
      }
   }

   @Override
   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      var1.putInt("last_vibration_frequency", this.lastVibrationFrequency);
      VibrationListener.codec(this).encodeStart(NbtOps.INSTANCE, this.listener).resultOrPartial(LOGGER::error).ifPresent(var1x -> var1.put("listener", var1x));
   }

   public VibrationListener getListener() {
      return this.listener;
   }

   public int getLastVibrationFrequency() {
      return this.lastVibrationFrequency;
   }

   @Override
   public boolean canTriggerAvoidVibration() {
      return true;
   }

   @Override
   public boolean shouldListen(ServerLevel var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable GameEvent.Context var5) {
      return !var3.equals(this.getBlockPos()) || var4 != GameEvent.BLOCK_DESTROY && var4 != GameEvent.BLOCK_PLACE
         ? SculkSensorBlock.canActivate(this.getBlockState())
         : false;
   }

   @Override
   public void onSignalReceive(
      ServerLevel var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5, @Nullable Entity var6, float var7
   ) {
      BlockState var8 = this.getBlockState();
      if (SculkSensorBlock.canActivate(var8)) {
         this.lastVibrationFrequency = VibrationListener.getGameEventFrequency(var4);
         SculkSensorBlock.activate(var5, var1, this.worldPosition, var8, getRedstoneStrengthForDistance(var7, var2.getListenerRadius()));
      }
   }

   @Override
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
