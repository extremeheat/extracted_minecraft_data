package net.minecraft.world.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Marker extends Entity {
   public Marker(EntityType<?> var1, Level var2) {
      super(var1, var2);
      this.noPhysics = true;
   }

   public void tick() {
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
   }

   protected void readAdditionalSaveData(ValueInput var1) {
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
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

   public final boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      return false;
   }
}
