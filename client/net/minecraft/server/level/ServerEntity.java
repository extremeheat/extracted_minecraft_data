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
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundProjectilePowerPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int TOLERANCE_LEVEL_ROTATION = 1;
   private static final double TOLERANCE_LEVEL_POSITION = 7.62939453125E-6;
   public static final int FORCED_POS_UPDATE_PERIOD = 60;
   private static final int FORCED_TELEPORT_PERIOD = 400;
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
      this.wasOnGround = var2.onGround();
      this.trackedDataValues = var2.getEntityData().getNonDefaultValues();
   }

   public void sendChanges() {
      List var1 = this.entity.getPassengers();
      if (!var1.equals(this.lastPassengers)) {
         this.broadcast.accept(new ClientboundSetPassengersPacket(this.entity));
         removedPassengers(var1, this.lastPassengers).forEach(var0 -> {
            if (var0 instanceof ServerPlayer var1x) {
               var1x.connection.teleport(var1x.getX(), var1x.getY(), var1x.getZ(), var1x.getYRot(), var1x.getXRot());
            }
         });
         this.lastPassengers = var1;
      }

      if (this.entity instanceof ItemFrame var2 && this.tickCount % 10 == 0) {
         ItemStack var21 = var2.getItem();
         if (var21.getItem() instanceof MapItem) {
            MapId var4 = var21.get(DataComponents.MAP_ID);
            MapItemSavedData var5 = MapItem.getSavedData(var4, this.level);
            if (var5 != null) {
               for (ServerPlayer var7 : this.level.players()) {
                  var5.tickCarriedBy(var7, var21);
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
            int var19 = Mth.floor(this.entity.getYRot() * 256.0F / 360.0F);
            int var23 = Mth.floor(this.entity.getXRot() * 256.0F / 360.0F);
            boolean var25 = Math.abs(var19 - this.yRotp) >= 1 || Math.abs(var23 - this.xRotp) >= 1;
            if (var25) {
               this.broadcast.accept(new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)var19, (byte)var23, this.entity.onGround()));
               this.yRotp = var19;
               this.xRotp = var23;
            }

            this.positionCodec.setBase(this.entity.trackingPosition());
            this.sendDirtyEntityData();
            this.wasRiding = true;
         } else {
            this.teleportDelay++;
            int var18 = Mth.floor(this.entity.getYRot() * 256.0F / 360.0F);
            int var22 = Mth.floor(this.entity.getXRot() * 256.0F / 360.0F);
            Vec3 var24 = this.entity.trackingPosition();
            boolean var26 = this.positionCodec.delta(var24).lengthSqr() >= 7.62939453125E-6;
            Object var27 = null;
            boolean var28 = var26 || this.tickCount % 60 == 0;
            boolean var29 = Math.abs(var18 - this.yRotp) >= 1 || Math.abs(var22 - this.xRotp) >= 1;
            boolean var9 = false;
            boolean var10 = false;
            if (this.tickCount > 0 || this.entity instanceof AbstractArrow) {
               long var11 = this.positionCodec.encodeX(var24);
               long var13 = this.positionCodec.encodeY(var24);
               long var15 = this.positionCodec.encodeZ(var24);
               boolean var17 = var11 < -32768L || var11 > 32767L || var13 < -32768L || var13 > 32767L || var15 < -32768L || var15 > 32767L;
               if (var17 || this.teleportDelay > 400 || this.wasRiding || this.wasOnGround != this.entity.onGround()) {
                  this.wasOnGround = this.entity.onGround();
                  this.teleportDelay = 0;
                  var27 = new ClientboundTeleportEntityPacket(this.entity);
                  var9 = true;
                  var10 = true;
               } else if ((!var28 || !var29) && !(this.entity instanceof AbstractArrow)) {
                  if (var28) {
                     var27 = new ClientboundMoveEntityPacket.Pos(
                        this.entity.getId(), (short)((int)var11), (short)((int)var13), (short)((int)var15), this.entity.onGround()
                     );
                     var9 = true;
                  } else if (var29) {
                     var27 = new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)var18, (byte)var22, this.entity.onGround());
                     var10 = true;
                  }
               } else {
                  var27 = new ClientboundMoveEntityPacket.PosRot(
                     this.entity.getId(), (short)((int)var11), (short)((int)var13), (short)((int)var15), (byte)var18, (byte)var22, this.entity.onGround()
                  );
                  var9 = true;
                  var10 = true;
               }
            }

            if ((this.trackDelta || this.entity.hasImpulse || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying())
               && this.tickCount > 0) {
               Vec3 var30 = this.entity.getDeltaMovement();
               double var12 = var30.distanceToSqr(this.ap);
               if (var12 > 1.0E-7 || var12 > 0.0 && var30.lengthSqr() == 0.0) {
                  this.ap = var30;
                  if (this.entity instanceof AbstractHurtingProjectile var14) {
                     this.broadcast
                        .accept(
                           new ClientboundBundlePacket(
                              List.of(
                                 new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap),
                                 new ClientboundProjectilePowerPacket(var14.getId(), var14.accelerationPower)
                              )
                           )
                        );
                  } else {
                     this.broadcast.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
                  }
               }
            }

            if (var27 != null) {
               this.broadcast.accept((Packet<?>)var27);
            }

            this.sendDirtyEntityData();
            if (var9) {
               this.positionCodec.setBase(var24);
            }

            if (var10) {
               this.yRotp = var18;
               this.xRotp = var22;
            }

            this.wasRiding = false;
         }

         int var20 = Mth.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
         if (Math.abs(var20 - this.yHeadRotp) >= 1) {
            this.broadcast.accept(new ClientboundRotateHeadPacket(this.entity, (byte)var20));
            this.yHeadRotp = var20;
         }

         this.entity.hasImpulse = false;
      }

      this.tickCount++;
      if (this.entity.hurtMarked) {
         this.entity.hurtMarked = false;
         this.broadcastAndSend(new ClientboundSetEntityMotionPacket(this.entity));
      }
   }

   private static Stream<Entity> removedPassengers(List<Entity> var0, List<Entity> var1) {
      return var1.stream().filter(var1x -> !var0.contains(var1x));
   }

   public void removePairing(ServerPlayer var1) {
      this.entity.stopSeenByPlayer(var1);
      var1.connection.send(new ClientboundRemoveEntitiesPacket(this.entity.getId()));
   }

   public void addPairing(ServerPlayer var1) {
      ArrayList var2 = new ArrayList();
      this.sendPairingData(var1, var2::add);
      var1.connection.send(new ClientboundBundlePacket(var2));
      this.entity.startSeenByPlayer(var1);
   }

   public void sendPairingData(ServerPlayer var1, Consumer<Packet<ClientGamePacketListener>> var2) {
      if (this.entity.isRemoved()) {
         LOGGER.warn("Fetching packet for removed entity {}", this.entity);
      }

      Packet var3 = this.entity.getAddEntityPacket();
      this.yHeadRotp = Mth.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
      var2.accept(var3);
      if (this.trackedDataValues != null) {
         var2.accept(new ClientboundSetEntityDataPacket(this.entity.getId(), this.trackedDataValues));
      }

      boolean var4 = this.trackDelta;
      if (this.entity instanceof LivingEntity) {
         Collection var5 = ((LivingEntity)this.entity).getAttributes().getSyncableAttributes();
         if (!var5.isEmpty()) {
            var2.accept(new ClientboundUpdateAttributesPacket(this.entity.getId(), var5));
         }

         if (((LivingEntity)this.entity).isFallFlying()) {
            var4 = true;
         }
      }

      this.ap = this.entity.getDeltaMovement();
      if (var4 && !(this.entity instanceof LivingEntity)) {
         var2.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
      }

      if (this.entity instanceof LivingEntity) {
         ArrayList var11 = Lists.newArrayList();

         for (EquipmentSlot var9 : EquipmentSlot.values()) {
            ItemStack var10 = ((LivingEntity)this.entity).getItemBySlot(var9);
            if (!var10.isEmpty()) {
               var11.add(Pair.of(var9, var10.copy()));
            }
         }

         if (!var11.isEmpty()) {
            var2.accept(new ClientboundSetEquipmentPacket(this.entity.getId(), var11));
         }
      }

      if (!this.entity.getPassengers().isEmpty()) {
         var2.accept(new ClientboundSetPassengersPacket(this.entity));
      }

      if (this.entity.isPassenger()) {
         var2.accept(new ClientboundSetPassengersPacket(this.entity.getVehicle()));
      }

      if (this.entity instanceof Mob var12 && var12.isLeashed()) {
         var2.accept(new ClientboundSetEntityLinkPacket(var12, var12.getLeashHolder()));
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
         Set var3 = ((LivingEntity)this.entity).getAttributes().getAttributesToSync();
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
