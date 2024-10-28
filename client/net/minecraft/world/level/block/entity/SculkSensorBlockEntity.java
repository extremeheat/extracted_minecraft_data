package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
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
import org.slf4j.Logger;

public class SculkSensorBlockEntity extends BlockEntity implements GameEventListener.Provider<VibrationSystem.Listener>, VibrationSystem {
   private static final Logger LOGGER = LogUtils.getLogger();
   private VibrationSystem.Data vibrationData;
   private final VibrationSystem.Listener vibrationListener;
   private final VibrationSystem.User vibrationUser;
   private int lastVibrationFrequency;

   protected SculkSensorBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
      this.vibrationUser = this.createVibrationUser();
      this.vibrationData = new VibrationSystem.Data();
      this.vibrationListener = new VibrationSystem.Listener(this);
   }

   public SculkSensorBlockEntity(BlockPos var1, BlockState var2) {
      this(BlockEntityType.SCULK_SENSOR, var1, var2);
   }

   public VibrationSystem.User createVibrationUser() {
      return new VibrationUser(this.getBlockPos());
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.lastVibrationFrequency = var1.getInt("last_vibration_frequency");
      RegistryOps var3 = var2.createSerializationContext(NbtOps.INSTANCE);
      if (var1.contains("listener", 10)) {
         VibrationSystem.Data.CODEC.parse(var3, var1.getCompound("listener")).resultOrPartial((var0) -> {
            LOGGER.error("Failed to parse vibration listener for Sculk Sensor: '{}'", var0);
         }).ifPresent((var1x) -> {
            this.vibrationData = var1x;
         });
      }

   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.putInt("last_vibration_frequency", this.lastVibrationFrequency);
      RegistryOps var3 = var2.createSerializationContext(NbtOps.INSTANCE);
      VibrationSystem.Data.CODEC.encodeStart(var3, this.vibrationData).resultOrPartial((var0) -> {
         LOGGER.error("Failed to encode vibration listener for Sculk Sensor: '{}'", var0);
      }).ifPresent((var1x) -> {
         var1.put("listener", var1x);
      });
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

   public void setLastVibrationFrequency(int var1) {
      this.lastVibrationFrequency = var1;
   }

   public VibrationSystem.Listener getListener() {
      return this.vibrationListener;
   }

   // $FF: synthetic method
   public GameEventListener getListener() {
      return this.getListener();
   }

   protected class VibrationUser implements VibrationSystem.User {
      public static final int LISTENER_RANGE = 8;
      protected final BlockPos blockPos;
      private final PositionSource positionSource;

      public VibrationUser(final BlockPos var2) {
         super();
         this.blockPos = var2;
         this.positionSource = new BlockPositionSource(var2);
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

      public boolean canReceiveVibration(ServerLevel var1, BlockPos var2, Holder<GameEvent> var3, @Nullable GameEvent.Context var4) {
         return !var2.equals(this.blockPos) || !var3.is((Holder)GameEvent.BLOCK_DESTROY) && !var3.is((Holder)GameEvent.BLOCK_PLACE) ? SculkSensorBlock.canActivate(SculkSensorBlockEntity.this.getBlockState()) : false;
      }

      public void onReceiveVibration(ServerLevel var1, BlockPos var2, Holder<GameEvent> var3, @Nullable Entity var4, @Nullable Entity var5, float var6) {
         BlockState var7 = SculkSensorBlockEntity.this.getBlockState();
         if (SculkSensorBlock.canActivate(var7)) {
            SculkSensorBlockEntity.this.setLastVibrationFrequency(VibrationSystem.getGameEventFrequency(var3));
            int var8 = VibrationSystem.getRedstoneStrengthForDistance(var6, this.getListenerRadius());
            Block var10 = var7.getBlock();
            if (var10 instanceof SculkSensorBlock) {
               SculkSensorBlock var9 = (SculkSensorBlock)var10;
               var9.activate(var4, var1, this.blockPos, var7, var8, SculkSensorBlockEntity.this.getLastVibrationFrequency());
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
