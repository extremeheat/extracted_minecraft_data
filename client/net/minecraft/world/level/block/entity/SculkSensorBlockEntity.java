package net.minecraft.world.level.block.entity;

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

public class SculkSensorBlockEntity extends BlockEntity implements GameEventListener.Provider<VibrationSystem.Listener>, VibrationSystem {
   private static final int DEFAULT_LAST_VIBRATION_FREQUENCY = 0;
   private VibrationSystem.Data vibrationData;
   private final VibrationSystem.Listener vibrationListener;
   private final VibrationSystem.User vibrationUser;
   private int lastVibrationFrequency;

   protected SculkSensorBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
      this.lastVibrationFrequency = 0;
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
      this.lastVibrationFrequency = var1.getIntOr("last_vibration_frequency", 0);
      RegistryOps var3 = var2.createSerializationContext(NbtOps.INSTANCE);
      this.vibrationData = (VibrationSystem.Data)var1.read("listener", VibrationSystem.Data.CODEC, var3).orElseGet(VibrationSystem.Data::new);
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.putInt("last_vibration_frequency", this.lastVibrationFrequency);
      RegistryOps var3 = var2.createSerializationContext(NbtOps.INSTANCE);
      var1.store("listener", VibrationSystem.Data.CODEC, var3, this.vibrationData);
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
         if (!var2.equals(this.blockPos) || !var3.is((Holder)GameEvent.BLOCK_DESTROY) && !var3.is((Holder)GameEvent.BLOCK_PLACE)) {
            return VibrationSystem.getGameEventFrequency(var3) == 0 ? false : SculkSensorBlock.canActivate(SculkSensorBlockEntity.this.getBlockState());
         } else {
            return false;
         }
      }

      public void onReceiveVibration(ServerLevel var1, BlockPos var2, Holder<GameEvent> var3, @Nullable Entity var4, @Nullable Entity var5, float var6) {
         BlockState var7 = SculkSensorBlockEntity.this.getBlockState();
         if (SculkSensorBlock.canActivate(var7)) {
            int var8 = VibrationSystem.getGameEventFrequency(var3);
            SculkSensorBlockEntity.this.setLastVibrationFrequency(var8);
            int var9 = VibrationSystem.getRedstoneStrengthForDistance(var6, this.getListenerRadius());
            Block var11 = var7.getBlock();
            if (var11 instanceof SculkSensorBlock) {
               SculkSensorBlock var10 = (SculkSensorBlock)var11;
               var10.activate(var4, var1, this.blockPos, var7, var9, var8);
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
