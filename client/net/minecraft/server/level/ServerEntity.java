package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
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
   private Vec3 ap = Vec3.ZERO;
   private int tickCount;
   private int teleportDelay;
   private List<Entity> lastPassengers = Collections.emptyList();
   private boolean wasRiding;
   private boolean wasOnGround;
   @Nullable
   private List<SynchedEntityData.DataValue<?>> trackedDataValues;

   public ServerEntity(ServerLevel var1, Entity var2, int var3, boolean var4, Consumer<Packet<?>> var5) {
      super();
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
      this.trackedDataValues = var2.getEntityData().getNonDefaultValues();
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void sendChanges() {
      List var1 = this.entity.getPassengers();
      if (!var1.equals(this.lastPassengers)) {
         this.lastPassengers = var1;
         this.broadcast.accept(new ClientboundSetPassengersPacket(this.entity));
      }

      Entity var3 = this.entity;
      if (var3 instanceof ItemFrame var2 && this.tickCount % 10 == 0) {
         ItemStack var19 = var2.getItem();
         if (var19.getItem() instanceof MapItem) {
            Integer var4 = MapItem.getMapId(var19);
            MapItemSavedData var5 = MapItem.getSavedData(var4, this.level);
            if (var5 != null) {
               for(ServerPlayer var7 : this.level.players()) {
                  var5.tickCarriedBy(var7, var19);
                  Packet var8 = var5.getUpdatePacket(var4, var7);
                  if (var8 != null) {
                     var7.connection.send(var8);
                  }
               }
            }
         }

         this.sendDirtyEntityData();
      }

      if (this.tickCount % this.updateInterval == 0 || this.entity.hasImpulse || this.entity.getEntityData().isDirty()) {
         if (this.entity.isPassenger()) {
            int var17 = Mth.floor(this.entity.getYRot() * 256.0F / 360.0F);
            int var21 = Mth.floor(this.entity.getXRot() * 256.0F / 360.0F);
            boolean var23 = Math.abs(var17 - this.yRotp) >= 1 || Math.abs(var21 - this.xRotp) >= 1;
            if (var23) {
               this.broadcast.accept(new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)var17, (byte)var21, this.entity.isOnGround()));
               this.yRotp = var17;
               this.xRotp = var21;
            }

            this.positionCodec.setBase(this.entity.trackingPosition());
            this.sendDirtyEntityData();
            this.wasRiding = true;
         } else {
            ++this.teleportDelay;
            int var16 = Mth.floor(this.entity.getYRot() * 256.0F / 360.0F);
            int var20 = Mth.floor(this.entity.getXRot() * 256.0F / 360.0F);
            Vec3 var22 = this.entity.trackingPosition();
            boolean var24 = this.positionCodec.delta(var22).lengthSqr() >= 7.62939453125E-6;
            Object var25 = null;
            boolean var26 = var24 || this.tickCount % 60 == 0;
            boolean var27 = Math.abs(var16 - this.yRotp) >= 1 || Math.abs(var20 - this.xRotp) >= 1;
            if (this.tickCount > 0 || this.entity instanceof AbstractArrow) {
               long var9 = this.positionCodec.encodeX(var22);
               long var11 = this.positionCodec.encodeY(var22);
               long var13 = this.positionCodec.encodeZ(var22);
               boolean var15 = var9 < -32768L || var9 > 32767L || var11 < -32768L || var11 > 32767L || var13 < -32768L || var13 > 32767L;
               if (var15 || this.teleportDelay > 400 || this.wasRiding || this.wasOnGround != this.entity.isOnGround()) {
                  this.wasOnGround = this.entity.isOnGround();
                  this.teleportDelay = 0;
                  var25 = new ClientboundTeleportEntityPacket(this.entity);
               } else if ((!var26 || !var27) && !(this.entity instanceof AbstractArrow)) {
                  if (var26) {
                     var25 = new ClientboundMoveEntityPacket.Pos(
                        this.entity.getId(), (short)((int)var9), (short)((int)var11), (short)((int)var13), this.entity.isOnGround()
                     );
                  } else if (var27) {
                     var25 = new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)var16, (byte)var20, this.entity.isOnGround());
                  }
               } else {
                  var25 = new ClientboundMoveEntityPacket.PosRot(
                     this.entity.getId(), (short)((int)var9), (short)((int)var11), (short)((int)var13), (byte)var16, (byte)var20, this.entity.isOnGround()
                  );
               }
            }

            if ((this.trackDelta || this.entity.hasImpulse || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying())
               && this.tickCount > 0) {
               Vec3 var28 = this.entity.getDeltaMovement();
               double var10 = var28.distanceToSqr(this.ap);
               if (var10 > 1.0E-7 || var10 > 0.0 && var28.lengthSqr() == 0.0) {
                  this.ap = var28;
                  this.broadcast.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
               }
            }

            if (var25 != null) {
               this.broadcast.accept((Packet<?>)var25);
            }

            this.sendDirtyEntityData();
            if (var26) {
               this.positionCodec.setBase(var22);
            }

            if (var27) {
               this.yRotp = var16;
               this.xRotp = var20;
            }

            this.wasRiding = false;
         }

         int var18 = Mth.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
         if (Math.abs(var18 - this.yHeadRotp) >= 1) {
            this.broadcast.accept(new ClientboundRotateHeadPacket(this.entity, (byte)var18));
            this.yHeadRotp = var18;
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
      var1.connection.send(new ClientboundRemoveEntitiesPacket(this.entity.getId()));
   }

   public void addPairing(ServerPlayer var1) {
      this.sendPairingData(var1.connection::send);
      this.entity.startSeenByPlayer(var1);
   }

   public void sendPairingData(Consumer<Packet<?>> var1) {
      if (this.entity.isRemoved()) {
         LOGGER.warn("Fetching packet for removed entity {}", this.entity);
      }

      Packet var2 = this.entity.getAddEntityPacket();
      this.yHeadRotp = Mth.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
      var1.accept(var2);
      if (this.trackedDataValues != null) {
         var1.accept(new ClientboundSetEntityDataPacket(this.entity.getId(), this.trackedDataValues));
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

         for(EquipmentSlot var8 : EquipmentSlot.values()) {
            ItemStack var9 = ((LivingEntity)this.entity).getItemBySlot(var8);
            if (!var9.isEmpty()) {
               var10.add(Pair.of(var8, var9.copy()));
            }
         }

         if (!var10.isEmpty()) {
            var1.accept(new ClientboundSetEquipmentPacket(this.entity.getId(), var10));
         }
      }

      if (this.entity instanceof LivingEntity var11) {
         for(MobEffectInstance var14 : var11.getActiveEffects()) {
            var1.accept(new ClientboundUpdateMobEffectPacket(this.entity.getId(), var14));
         }
      }

      if (!this.entity.getPassengers().isEmpty()) {
         var1.accept(new ClientboundSetPassengersPacket(this.entity));
      }

      if (this.entity.isPassenger()) {
         var1.accept(new ClientboundSetPassengersPacket(this.entity.getVehicle()));
      }

      if (this.entity instanceof Mob var12 && ((Mob)var12).isLeashed()) {
         var1.accept(new ClientboundSetEntityLinkPacket((Entity)var12, ((Mob)var12).getLeashHolder()));
      }
   }

   private void sendDirtyEntityData() {
      SynchedEntityData var1 = this.entity.getEntityData();
      List var2 = var1.packDirty();
      if (var2 != null) {
         this.trackedDataValues = var1.getNonDefaultValues();
         this.broadcastAndSend(new ClientboundSetEntityDataPacket(this.entity.getId(), var2));
      }

      if (this.entity instanceof LivingEntity) {
         Set var3 = ((LivingEntity)this.entity).getAttributes().getDirtyAttributes();
         if (!var3.isEmpty()) {
            this.broadcastAndSend(new ClientboundUpdateAttributesPacket(this.entity.getId(), var3));
         }

         var3.clear();
      }
   }

   private void broadcastAndSend(Packet<?> var1) {
      this.broadcast.accept(var1);
      if (this.entity instanceof ServerPlayer) {
         ((ServerPlayer)this.entity).connection.send(var1);
      }
   }
}
