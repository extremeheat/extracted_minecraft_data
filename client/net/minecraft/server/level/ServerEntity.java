package net.minecraft.server.level;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquippedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.ModifiableAttributeMap;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ServerLevel level;
   private final Entity entity;
   private final int updateInterval;
   private final boolean trackDelta;
   private final Consumer<Packet<?>> broadcast;
   private long xp;
   private long yp;
   private long zp;
   private int yRotp;
   private int xRotp;
   private int yHeadRotp;
   private Vec3 ap;
   private int tickCount;
   private int teleportDelay;
   private List<Entity> lastPassengers;
   private boolean wasRiding;
   private boolean wasOnGround;

   public ServerEntity(ServerLevel var1, Entity var2, int var3, boolean var4, Consumer<Packet<?>> var5) {
      super();
      this.ap = Vec3.ZERO;
      this.lastPassengers = Collections.emptyList();
      this.level = var1;
      this.broadcast = var5;
      this.entity = var2;
      this.updateInterval = var3;
      this.trackDelta = var4;
      this.updateSentPos();
      this.yRotp = Mth.floor(var2.yRot * 256.0F / 360.0F);
      this.xRotp = Mth.floor(var2.xRot * 256.0F / 360.0F);
      this.yHeadRotp = Mth.floor(var2.getYHeadRot() * 256.0F / 360.0F);
      this.wasOnGround = var2.onGround;
   }

   public void sendChanges() {
      List var1 = this.entity.getPassengers();
      if (!var1.equals(this.lastPassengers)) {
         this.lastPassengers = var1;
         this.broadcast.accept(new ClientboundSetPassengersPacket(this.entity));
      }

      if (this.entity instanceof ItemFrame && this.tickCount % 10 == 0) {
         ItemFrame var2 = (ItemFrame)this.entity;
         ItemStack var3 = var2.getItem();
         if (var3.getItem() instanceof MapItem) {
            MapItemSavedData var4 = MapItem.getOrCreateSavedData(var3, this.level);
            Iterator var5 = this.level.players().iterator();

            while(var5.hasNext()) {
               ServerPlayer var6 = (ServerPlayer)var5.next();
               var4.tickCarriedBy(var6, var3);
               Packet var7 = ((MapItem)var3.getItem()).getUpdatePacket(var3, this.level, var6);
               if (var7 != null) {
                  var6.connection.send(var7);
               }
            }
         }

         this.sendDirtyEntityData();
      }

      if (this.tickCount % this.updateInterval == 0 || this.entity.hasImpulse || this.entity.getEntityData().isDirty()) {
         int var16;
         int var17;
         if (this.entity.isPassenger()) {
            var16 = Mth.floor(this.entity.yRot * 256.0F / 360.0F);
            var17 = Mth.floor(this.entity.xRot * 256.0F / 360.0F);
            boolean var19 = Math.abs(var16 - this.yRotp) >= 1 || Math.abs(var17 - this.xRotp) >= 1;
            if (var19) {
               this.broadcast.accept(new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)var16, (byte)var17, this.entity.onGround));
               this.yRotp = var16;
               this.xRotp = var17;
            }

            this.updateSentPos();
            this.sendDirtyEntityData();
            this.wasRiding = true;
         } else {
            ++this.teleportDelay;
            var16 = Mth.floor(this.entity.yRot * 256.0F / 360.0F);
            var17 = Mth.floor(this.entity.xRot * 256.0F / 360.0F);
            Vec3 var18 = (new Vec3(this.entity.x, this.entity.y, this.entity.z)).subtract(ClientboundMoveEntityPacket.packetToEntity(this.xp, this.yp, this.zp));
            boolean var20 = var18.lengthSqr() >= 7.62939453125E-6D;
            Object var21 = null;
            boolean var22 = var20 || this.tickCount % 60 == 0;
            boolean var8 = Math.abs(var16 - this.yRotp) >= 1 || Math.abs(var17 - this.xRotp) >= 1;
            if (this.tickCount > 0 || this.entity instanceof AbstractArrow) {
               long var9 = ClientboundMoveEntityPacket.entityToPacket(var18.x);
               long var11 = ClientboundMoveEntityPacket.entityToPacket(var18.y);
               long var13 = ClientboundMoveEntityPacket.entityToPacket(var18.z);
               boolean var15 = var9 < -32768L || var9 > 32767L || var11 < -32768L || var11 > 32767L || var13 < -32768L || var13 > 32767L;
               if (!var15 && this.teleportDelay <= 400 && !this.wasRiding && this.wasOnGround == this.entity.onGround) {
                  if ((!var22 || !var8) && !(this.entity instanceof AbstractArrow)) {
                     if (var22) {
                        var21 = new ClientboundMoveEntityPacket.Pos(this.entity.getId(), (short)((int)var9), (short)((int)var11), (short)((int)var13), this.entity.onGround);
                     } else if (var8) {
                        var21 = new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)var16, (byte)var17, this.entity.onGround);
                     }
                  } else {
                     var21 = new ClientboundMoveEntityPacket.PosRot(this.entity.getId(), (short)((int)var9), (short)((int)var11), (short)((int)var13), (byte)var16, (byte)var17, this.entity.onGround);
                  }
               } else {
                  this.wasOnGround = this.entity.onGround;
                  this.teleportDelay = 0;
                  var21 = new ClientboundTeleportEntityPacket(this.entity);
               }
            }

            if ((this.trackDelta || this.entity.hasImpulse || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying()) && this.tickCount > 0) {
               Vec3 var23 = this.entity.getDeltaMovement();
               double var10 = var23.distanceToSqr(this.ap);
               if (var10 > 1.0E-7D || var10 > 0.0D && var23.lengthSqr() == 0.0D) {
                  this.ap = var23;
                  this.broadcast.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
               }
            }

            if (var21 != null) {
               this.broadcast.accept(var21);
            }

            this.sendDirtyEntityData();
            if (var22) {
               this.updateSentPos();
            }

            if (var8) {
               this.yRotp = var16;
               this.xRotp = var17;
            }

            this.wasRiding = false;
         }

         var16 = Mth.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
         if (Math.abs(var16 - this.yHeadRotp) >= 1) {
            this.broadcast.accept(new ClientboundRotateHeadPacket(this.entity, (byte)var16));
            this.yHeadRotp = var16;
         }

         this.entity.hasImpulse = false;
      }

      ++this.tickCount;
      if (this.entity.hurtMarked) {
         this.broadcastAndSend(new ClientboundSetEntityMotionPacket(this.entity));
         this.entity.hurtMarked = false;
      }

   }

   public void removePairing(ServerPlayer var1) {
      this.entity.stopSeenByPlayer(var1);
      var1.sendRemoveEntity(this.entity);
   }

   public void addPairing(ServerPlayer var1) {
      ServerGamePacketListenerImpl var10001 = var1.connection;
      this.sendPairingData(var10001::send);
      this.entity.startSeenByPlayer(var1);
      var1.cancelRemoveEntity(this.entity);
   }

   public void sendPairingData(Consumer<Packet<?>> var1) {
      if (this.entity.removed) {
         LOGGER.warn("Fetching packet for removed entity " + this.entity);
      }

      Packet var2 = this.entity.getAddEntityPacket();
      this.yHeadRotp = Mth.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
      var1.accept(var2);
      if (!this.entity.getEntityData().isEmpty()) {
         var1.accept(new ClientboundSetEntityDataPacket(this.entity.getId(), this.entity.getEntityData(), true));
      }

      boolean var3 = this.trackDelta;
      if (this.entity instanceof LivingEntity) {
         ModifiableAttributeMap var4 = (ModifiableAttributeMap)((LivingEntity)this.entity).getAttributes();
         Collection var5 = var4.getSyncableAttributes();
         if (!var5.isEmpty()) {
            var1.accept(new ClientboundUpdateAttributesPacket(this.entity.getId(), var5));
         }

         if (((LivingEntity)this.entity).isFallFlying()) {
            var3 = true;
         }
      }

      this.ap = this.entity.getDeltaMovement();
      if (var3 && !(var2 instanceof ClientboundAddMobPacket)) {
         var1.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
      }

      if (this.entity instanceof LivingEntity) {
         EquipmentSlot[] var9 = EquipmentSlot.values();
         int var11 = var9.length;

         for(int var6 = 0; var6 < var11; ++var6) {
            EquipmentSlot var7 = var9[var6];
            ItemStack var8 = ((LivingEntity)this.entity).getItemBySlot(var7);
            if (!var8.isEmpty()) {
               var1.accept(new ClientboundSetEquippedItemPacket(this.entity.getId(), var7, var8));
            }
         }
      }

      if (this.entity instanceof LivingEntity) {
         LivingEntity var10 = (LivingEntity)this.entity;
         Iterator var12 = var10.getActiveEffects().iterator();

         while(var12.hasNext()) {
            MobEffectInstance var13 = (MobEffectInstance)var12.next();
            var1.accept(new ClientboundUpdateMobEffectPacket(this.entity.getId(), var13));
         }
      }

      if (!this.entity.getPassengers().isEmpty()) {
         var1.accept(new ClientboundSetPassengersPacket(this.entity));
      }

      if (this.entity.isPassenger()) {
         var1.accept(new ClientboundSetPassengersPacket(this.entity.getVehicle()));
      }

   }

   private void sendDirtyEntityData() {
      SynchedEntityData var1 = this.entity.getEntityData();
      if (var1.isDirty()) {
         this.broadcastAndSend(new ClientboundSetEntityDataPacket(this.entity.getId(), var1, false));
      }

      if (this.entity instanceof LivingEntity) {
         ModifiableAttributeMap var2 = (ModifiableAttributeMap)((LivingEntity)this.entity).getAttributes();
         Set var3 = var2.getDirtyAttributes();
         if (!var3.isEmpty()) {
            this.broadcastAndSend(new ClientboundUpdateAttributesPacket(this.entity.getId(), var3));
         }

         var3.clear();
      }

   }

   private void updateSentPos() {
      this.xp = ClientboundMoveEntityPacket.entityToPacket(this.entity.x);
      this.yp = ClientboundMoveEntityPacket.entityToPacket(this.entity.y);
      this.zp = ClientboundMoveEntityPacket.entityToPacket(this.entity.z);
   }

   public Vec3 sentPos() {
      return ClientboundMoveEntityPacket.packetToEntity(this.xp, this.yp, this.zp);
   }

   private void broadcastAndSend(Packet<?> var1) {
      this.broadcast.accept(var1);
      if (this.entity instanceof ServerPlayer) {
         ((ServerPlayer)this.entity).connection.send(var1);
      }

   }
}
