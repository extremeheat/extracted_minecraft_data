package net.minecraft.world.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;

public class Marker extends Entity {
   private static final String DATA_TAG = "data";
   private CompoundTag data = new CompoundTag();

   public Marker(EntityType<?> var1, Level var2) {
      super(var1, var2);
      this.noPhysics = true;
   }

   public void tick() {
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      this.data = var1.getCompound("data");
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.put("data", this.data.copy());
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity var1) {
      throw new IllegalStateException("Markers should never be sent");
   }

   protected boolean canAddPassenger(Entity var1) {
      return false;
   }

   protected boolean couldAcceptPassenger() {
      return false;
   }

   protected void addPassenger(Entity var1) {
      throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   public boolean isIgnoringBlockTriggers() {
      return true;
   }
}
