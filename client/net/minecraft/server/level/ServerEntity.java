package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int TOLERANCE_LEVEL_ROTATION = 1;
   private final ServerLevel level;
   private final Entity entity;
   private final int updateInterval;
   private final boolean trackDelta;
   private final Consumer<Packet<?>> broadcast;
   private final VecDeltaCodec positionCodec = new VecDeltaCodec();
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
      this.positionCodec.setBase(var2.trackingPosition());
      this.yRotp = Mth.floor(var2.getYRot() * 256.0F / 360.0F);
      this.xRotp = Mth.floor(var2.getXRot() * 256.0F / 360.0F);
      this.yHeadRotp = Mth.floor(var2.getYHeadRot() * 256.0F / 360.0F);
      this.wasOnGround = var2.isOnGround();
   }

   public void sendChanges() {
      List var1 = this.entity.getPassengers();
      if (!var1.equals(this.lastPassengers)) {
         this.lastPassengers = var1;
         this.broadcast.accept(new ClientboundSetPassengersPacket(this.entity));
      }

      Entity var3 = this.entity;
      if (var3 instanceof ItemFrame var2) {
         if (this.tickCount % 10 == 0) {
            ItemStack var17 = var2.getItem();
            if (var17.getItem() instanceof MapItem) {
               Integer var4 = MapItem.getMapId(var17);
               MapItemSavedData var5 = MapItem.getSavedData((Integer)var4, this.level);
               if (var5 != null) {
                  Iterator var6 = this.level.players().iterator();

                  while(var6.hasNext()) {
                     ServerPlayer var7 = (ServerPlayer)var6.next();
                     var5.tickCarriedBy(var7, var17);
                     Packet var8 = var5.getUpdatePacket(var4, var7);
                     if (var8 != null) {
                        var7.connection.send(var8);
                     }
                  }
               }
            }

            this.sendDirtyEntityData();
         }
      }

      if (this.tickCount % this.updateInterval == 0 || this.entity.hasImpulse || this.entity.getEntityData().isDirty()) {
         int var16;
         int var18;
         if (this.entity.isPassenger()) {
            var16 = Mth.floor(this.entity.getYRot() * 256.0F / 360.0F);
            var18 = Mth.floor(this.entity.getXRot() * 256.0F / 360.0F);
            boolean var20 = Math.abs(var16 - this.yRotp) >= 1 || Math.abs(var18 - this.xRotp) >= 1;
            if (var20) {
               this.broadcast.accept(new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)var16, (byte)var18, this.entity.isOnGround()));
               this.yRotp = var16;
               this.xRotp = var18;
            }

            this.positionCodec.setBase(this.entity.trackingPosition());
            this.sendDirtyEntityData();
            this.wasRiding = true;
         } else {
            ++this.teleportDelay;
            var16 = Mth.floor(this.entity.getYRot() * 256.0F / 360.0F);
            var18 = Mth.floor(this.entity.getXRot() * 256.0F / 360.0F);
            Vec3 var19 = this.entity.trackingPosition();
            boolean var21 = this.positionCodec.delta(var19).lengthSqr() >= 7.62939453125E-6;
            Object var22 = null;
            boolean var23 = var21 || this.tickCount % 60 == 0;
            boolean var24 = Math.abs(var16 - this.yRotp) >= 1 || Math.abs(var18 - this.xRotp) >= 1;
            if (this.tickCount > 0 || this.entity instanceof AbstractArrow) {
               long var9 = this.positionCodec.encodeX(var19);
               long var11 = this.positionCodec.encodeY(var19);
               long var13 = this.positionCodec.encodeZ(var19);
               boolean var15 = var9 < -32768L || var9 > 32767L || var11 < -32768L || var11 > 32767L || var13 < -32768L || var13 > 32767L;
               if (!var15 && this.teleportDelay <= 400 && !this.wasRiding && this.wasOnGround == this.entity.isOnGround()) {
                  if ((!var23 || !var24) && !(this.entity instanceof AbstractArrow)) {
                     if (var23) {
                        var22 = new ClientboundMoveEntityPacket.Pos(this.entity.getId(), (short)((int)var9), (short)((int)var11), (short)((int)var13), this.entity.isOnGround());
                     } else if (var24) {
                        var22 = new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)var16, (byte)var18, this.entity.isOnGround());
                     }
                  } else {
                     var22 = new ClientboundMoveEntityPacket.PosRot(this.entity.getId(), (short)((int)var9), (short)((int)var11), (short)((int)var13), (byte)var16, (byte)var18, this.entity.isOnGround());
                  }
               } else {
                  this.wasOnGround = this.entity.isOnGround();
                  this.teleportDelay = 0;
                  var22 = new ClientboundTeleportEntityPacket(this.entity);
               }
            }

            if ((this.trackDelta || this.entity.hasImpulse || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying()) && this.tickCount > 0) {
               Vec3 var25 = this.entity.getDeltaMovement();
               double var10 = var25.distanceToSqr(this.ap);
               if (var10 > 1.0E-7 || var10 > 0.0 && var25.lengthSqr() == 0.0) {
                  this.ap = var25;
                  this.broadcast.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
               }
            }

            if (var22 != null) {
               this.broadcast.accept(var22);
            }

            this.sendDirtyEntityData();
            if (var23) {
               this.positionCodec.setBase(var19);
            }

            if (var24) {
               this.yRotp = var16;
               this.xRotp = var18;
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
      var1.connection.send(new ClientboundRemoveEntitiesPacket(new int[]{this.entity.getId()}));
   }

   public void addPairing(ServerPlayer var1) {
      ServerGamePacketListenerImpl var10001 = var1.connection;
      Objects.requireNonNull(var10001);
      this.sendPairingData(var10001::send);
      this.entity.startSeenByPlayer(var1);
   }

   public void sendPairingData(Consumer<Packet<?>> var1) {
      if (this.entity.isRemoved()) {
         LOGGER.warn("Fetching packet for removed entity {}", this.entity);
      }

      Packet var2 = this.entity.getAddEntityPacket();
      this.yHeadRotp = Mth.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
      var1.accept(var2);
      if (!this.entity.getEntityData().isEmpty()) {
         var1.accept(new ClientboundSetEntityDataPacket(this.entity.getId(), this.entity.getEntityData(), true));
      }

      boolean var3 = this.trackDelta;
      if (this.entity instanceof LivingEntity) {
         Collection var4 = ((LivingEntity)this.entity).getAttributes().getSyncableAttributes();
         if (!var4.isEmpty()) {
            var1.accept(new ClientboundUpdateAttributesPacket(this.entity.getId(), var4));
         }

         if (((LivingEntity)this.entity).isFallFlying()) {
            var3 = true;
         }
      }

      this.ap = this.entity.getDeltaMovement();
      if (var3 && !(this.entity instanceof LivingEntity)) {
         var1.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
      }

      if (this.entity instanceof LivingEntity) {
         ArrayList var10 = Lists.newArrayList();
         EquipmentSlot[] var5 = EquipmentSlot.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EquipmentSlot var8 = var5[var7];
            ItemStack var9 = ((LivingEntity)this.entity).getItemBySlot(var8);
            if (!var9.isEmpty()) {
               var10.add(Pair.of(var8, var9.copy()));
            }
         }

         if (!var10.isEmpty()) {
            var1.accept(new ClientboundSetEquipmentPacket(this.entity.getId(), var10));
         }
      }

      if (this.entity instanceof LivingEntity) {
         LivingEntity var11 = (LivingEntity)this.entity;
         Iterator var13 = var11.getActiveEffects().iterator();

         while(var13.hasNext()) {
            MobEffectInstance var14 = (MobEffectInstance)var13.next();
            var1.accept(new ClientboundUpdateMobEffectPacket(this.entity.getId(), var14));
         }
      }

      if (!this.entity.getPassengers().isEmpty()) {
         var1.accept(new ClientboundSetPassengersPacket(this.entity));
      }

      if (this.entity.isPassenger()) {
         var1.accept(new ClientboundSetPassengersPacket(this.entity.getVehicle()));
      }

      if (this.entity instanceof Mob) {
         Mob var12 = (Mob)this.entity;
         if (var12.isLeashed()) {
            var1.accept(new ClientboundSetEntityLinkPacket(var12, var12.getLeashHolder()));
         }
      }

   }

   private void sendDirtyEntityData() {
      SynchedEntityData var1 = this.entity.getEntityData();
      if (var1.isDirty()) {
         this.broadcastAndSend(new ClientboundSetEntityDataPacket(this.entity.getId(), var1, false));
      }

      if (this.entity instanceof LivingEntity) {
         Set var2 = ((LivingEntity)this.entity).getAttributes().getDirtyAttributes();
         if (!var2.isEmpty()) {
            this.broadcastAndSend(new ClientboundUpdateAttributesPacket(this.entity.getId(), var2));
         }

         var2.clear();
      }

   }

   private void broadcastAndSend(Packet<?> var1) {
      this.broadcast.accept(var1);
      if (this.entity instanceof ServerPlayer) {
         ((ServerPlayer)this.entity).connection.send(var1);
      }

   }
}
