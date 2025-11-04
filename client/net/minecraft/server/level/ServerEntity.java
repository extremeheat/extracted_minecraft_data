package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveMinecartPacket;
import net.minecraft.network.protocol.game.ClientboundProjectilePowerPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartBehavior;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
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
   private final Synchronizer synchronizer;
   private final VecDeltaCodec positionCodec = new VecDeltaCodec();
   private byte lastSentYRot;
   private byte lastSentXRot;
   private byte lastSentYHeadRot;
   private Vec3 lastSentMovement;
   private int tickCount;
   private int teleportDelay;
   private List<Entity> lastPassengers = Collections.emptyList();
   private boolean wasRiding;
   private boolean wasOnGround;
   private @Nullable List<SynchedEntityData.DataValue<?>> trackedDataValues;

   public ServerEntity(ServerLevel var1, Entity var2, int var3, boolean var4, Synchronizer var5) {
      super();
      this.level = var1;
      this.synchronizer = var5;
      this.entity = var2;
      this.updateInterval = var3;
      this.trackDelta = var4;
      this.positionCodec.setBase(var2.trackingPosition());
      this.lastSentMovement = var2.getDeltaMovement();
      this.lastSentYRot = Mth.packDegrees(var2.getYRot());
      this.lastSentXRot = Mth.packDegrees(var2.getXRot());
      this.lastSentYHeadRot = Mth.packDegrees(var2.getYHeadRot());
      this.wasOnGround = var2.onGround();
      this.trackedDataValues = var2.getEntityData().getNonDefaultValues();
   }

   public void sendChanges() {
      this.entity.updateDataBeforeSync();
      List var1 = this.entity.getPassengers();
      if (!var1.equals(this.lastPassengers)) {
         this.synchronizer.sendToTrackingPlayersFiltered(new ClientboundSetPassengersPacket(this.entity), (var2x) -> var1.contains(var2x) == this.lastPassengers.contains(var2x));
         this.lastPassengers = var1;
      }

      Entity var3 = this.entity;
      if (var3 instanceof ItemFrame var2) {
         if (this.tickCount % 10 == 0) {
            ItemStack var26 = var2.getItem();
            if (var26.getItem() instanceof MapItem) {
               MapId var4 = (MapId)var26.get(DataComponents.MAP_ID);
               MapItemSavedData var5 = MapItem.getSavedData((MapId)var4, this.level);
               if (var5 != null) {
                  for(ServerPlayer var7 : this.level.players()) {
                     var5.tickCarriedBy(var7, var26);
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

      if (this.tickCount % this.updateInterval == 0 || this.entity.needsSync || this.entity.getEntityData().isDirty()) {
         byte var25 = Mth.packDegrees(this.entity.getYRot());
         byte var27 = Mth.packDegrees(this.entity.getXRot());
         boolean var28 = Math.abs(var25 - this.lastSentYRot) >= 1 || Math.abs(var27 - this.lastSentXRot) >= 1;
         if (this.entity.isPassenger()) {
            if (var28) {
               this.synchronizer.sendToTrackingPlayers(new ClientboundMoveEntityPacket.Rot(this.entity.getId(), var25, var27, this.entity.onGround()));
               this.lastSentYRot = var25;
               this.lastSentXRot = var27;
            }

            this.positionCodec.setBase(this.entity.trackingPosition());
            this.sendDirtyEntityData();
            this.wasRiding = true;
         } else {
            label197: {
               Entity var32 = this.entity;
               if (var32 instanceof AbstractMinecart) {
                  AbstractMinecart var29 = (AbstractMinecart)var32;
                  MinecartBehavior var33 = var29.getBehavior();
                  if (var33 instanceof NewMinecartBehavior) {
                     NewMinecartBehavior var31 = (NewMinecartBehavior)var33;
                     this.handleMinecartPosRot(var31, var25, var27, var28);
                     break label197;
                  }
               }

               ++this.teleportDelay;
               Vec3 var34 = this.entity.trackingPosition();
               boolean var35 = this.positionCodec.delta(var34).lengthSqr() >= 7.62939453125E-6;
               Object var9 = null;
               boolean var10 = var35 || this.tickCount % 60 == 0;
               boolean var11 = false;
               boolean var12 = false;
               long var13 = this.positionCodec.encodeX(var34);
               long var15 = this.positionCodec.encodeY(var34);
               long var17 = this.positionCodec.encodeZ(var34);
               boolean var19 = var13 < -32768L || var13 > 32767L || var15 < -32768L || var15 > 32767L || var17 < -32768L || var17 > 32767L;
               if (!this.entity.getRequiresPrecisePosition() && !var19 && this.teleportDelay <= 400 && !this.wasRiding && this.wasOnGround == this.entity.onGround()) {
                  if ((!var10 || !var28) && !(this.entity instanceof AbstractArrow)) {
                     if (var10) {
                        var9 = new ClientboundMoveEntityPacket.Pos(this.entity.getId(), (short)((int)var13), (short)((int)var15), (short)((int)var17), this.entity.onGround());
                        var11 = true;
                     } else if (var28) {
                        var9 = new ClientboundMoveEntityPacket.Rot(this.entity.getId(), var25, var27, this.entity.onGround());
                        var12 = true;
                     }
                  } else {
                     var9 = new ClientboundMoveEntityPacket.PosRot(this.entity.getId(), (short)((int)var13), (short)((int)var15), (short)((int)var17), var25, var27, this.entity.onGround());
                     var11 = true;
                     var12 = true;
                  }
               } else {
                  this.wasOnGround = this.entity.onGround();
                  this.teleportDelay = 0;
                  var9 = ClientboundEntityPositionSyncPacket.of(this.entity);
                  var11 = true;
                  var12 = true;
               }

               if (this.entity.needsSync || this.trackDelta || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying()) {
                  Vec3 var20 = this.entity.getDeltaMovement();
                  double var21 = var20.distanceToSqr(this.lastSentMovement);
                  if (var21 > 1.0E-7 || var21 > 0.0 && var20.lengthSqr() == 0.0) {
                     this.lastSentMovement = var20;
                     Entity var24 = this.entity;
                     if (var24 instanceof AbstractHurtingProjectile) {
                        AbstractHurtingProjectile var23 = (AbstractHurtingProjectile)var24;
                        this.synchronizer.sendToTrackingPlayers(new ClientboundBundlePacket(List.of(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.lastSentMovement), new ClientboundProjectilePowerPacket(var23.getId(), var23.accelerationPower))));
                     } else {
                        this.synchronizer.sendToTrackingPlayers(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.lastSentMovement));
                     }
                  }
               }

               if (var9 != null) {
                  this.synchronizer.sendToTrackingPlayers((Packet)var9);
               }

               this.sendDirtyEntityData();
               if (var11) {
                  this.positionCodec.setBase(var34);
               }

               if (var12) {
                  this.lastSentYRot = var25;
                  this.lastSentXRot = var27;
               }

               this.wasRiding = false;
            }
         }

         byte var30 = Mth.packDegrees(this.entity.getYHeadRot());
         if (Math.abs(var30 - this.lastSentYHeadRot) >= 1) {
            this.synchronizer.sendToTrackingPlayers(new ClientboundRotateHeadPacket(this.entity, var30));
            this.lastSentYHeadRot = var30;
         }

         this.entity.needsSync = false;
      }

      ++this.tickCount;
      if (this.entity.hurtMarked) {
         this.entity.hurtMarked = false;
         this.synchronizer.sendToTrackingPlayersAndSelf(new ClientboundSetEntityMotionPacket(this.entity));
      }

   }

   private void handleMinecartPosRot(NewMinecartBehavior var1, byte var2, byte var3, boolean var4) {
      this.sendDirtyEntityData();
      if (var1.lerpSteps.isEmpty()) {
         Vec3 var5 = this.entity.getDeltaMovement();
         double var6 = var5.distanceToSqr(this.lastSentMovement);
         Vec3 var8 = this.entity.trackingPosition();
         boolean var9 = this.positionCodec.delta(var8).lengthSqr() >= 7.62939453125E-6;
         boolean var10 = var9 || this.tickCount % 60 == 0;
         if (var10 || var4 || var6 > 1.0E-7) {
            this.synchronizer.sendToTrackingPlayers(new ClientboundMoveMinecartPacket(this.entity.getId(), List.of(new NewMinecartBehavior.MinecartStep(this.entity.position(), this.entity.getDeltaMovement(), this.entity.getYRot(), this.entity.getXRot(), 1.0F))));
         }
      } else {
         this.synchronizer.sendToTrackingPlayers(new ClientboundMoveMinecartPacket(this.entity.getId(), List.copyOf(var1.lerpSteps)));
         var1.lerpSteps.clear();
      }

      this.lastSentYRot = var2;
      this.lastSentXRot = var3;
      this.positionCodec.setBase(this.entity.position());
   }

   public void removePairing(ServerPlayer var1) {
      this.entity.stopSeenByPlayer(var1);
      var1.connection.send(new ClientboundRemoveEntitiesPacket(new int[]{this.entity.getId()}));
   }

   public void addPairing(ServerPlayer var1) {
      ArrayList var2 = new ArrayList();
      Objects.requireNonNull(var2);
      this.sendPairingData(var1, var2::add);
      var1.connection.send(new ClientboundBundlePacket(var2));
      this.entity.startSeenByPlayer(var1);
   }

   public void sendPairingData(ServerPlayer var1, Consumer<Packet<ClientGamePacketListener>> var2) {
      this.entity.updateDataBeforeSync();
      if (this.entity.isRemoved()) {
         LOGGER.warn("Fetching packet for removed entity {}", this.entity);
      }

      Packet var3 = this.entity.getAddEntityPacket(this);
      var2.accept(var3);
      if (this.trackedDataValues != null) {
         var2.accept(new ClientboundSetEntityDataPacket(this.entity.getId(), this.trackedDataValues));
      }

      Entity var5 = this.entity;
      if (var5 instanceof LivingEntity var4) {
         Collection var11 = var4.getAttributes().getSyncableAttributes();
         if (!var11.isEmpty()) {
            var2.accept(new ClientboundUpdateAttributesPacket(this.entity.getId(), var11));
         }
      }

      var5 = this.entity;
      if (var5 instanceof LivingEntity var9) {
         ArrayList var13 = Lists.newArrayList();

         for(EquipmentSlot var7 : EquipmentSlot.VALUES) {
            ItemStack var8 = var9.getItemBySlot(var7);
            if (!var8.isEmpty()) {
               var13.add(Pair.of(var7, var8.copy()));
            }
         }

         if (!var13.isEmpty()) {
            var2.accept(new ClientboundSetEquipmentPacket(this.entity.getId(), var13));
         }
      }

      if (!this.entity.getPassengers().isEmpty()) {
         var2.accept(new ClientboundSetPassengersPacket(this.entity));
      }

      if (this.entity.isPassenger()) {
         var2.accept(new ClientboundSetPassengersPacket(this.entity.getVehicle()));
      }

      var5 = this.entity;
      if (var5 instanceof Leashable var10) {
         if (var10.isLeashed()) {
            var2.accept(new ClientboundSetEntityLinkPacket(this.entity, var10.getLeashHolder()));
         }
      }

   }

   public Vec3 getPositionBase() {
      return this.positionCodec.getBase();
   }

   public Vec3 getLastSentMovement() {
      return this.lastSentMovement;
   }

   public float getLastSentXRot() {
      return Mth.unpackDegrees(this.lastSentXRot);
   }

   public float getLastSentYRot() {
      return Mth.unpackDegrees(this.lastSentYRot);
   }

   public float getLastSentYHeadRot() {
      return Mth.unpackDegrees(this.lastSentYHeadRot);
   }

   private void sendDirtyEntityData() {
      SynchedEntityData var1 = this.entity.getEntityData();
      List var2 = var1.packDirty();
      if (var2 != null) {
         this.trackedDataValues = var1.getNonDefaultValues();
         this.synchronizer.sendToTrackingPlayersAndSelf(new ClientboundSetEntityDataPacket(this.entity.getId(), var2));
      }

      if (this.entity instanceof LivingEntity) {
         Set var3 = ((LivingEntity)this.entity).getAttributes().getAttributesToSync();
         if (!var3.isEmpty()) {
            this.synchronizer.sendToTrackingPlayersAndSelf(new ClientboundUpdateAttributesPacket(this.entity.getId(), var3));
         }

         var3.clear();
      }

   }

   public interface Synchronizer {
      void sendToTrackingPlayers(Packet<? super ClientGamePacketListener> var1);

      void sendToTrackingPlayersAndSelf(Packet<? super ClientGamePacketListener> var1);

      void sendToTrackingPlayersFiltered(Packet<? super ClientGamePacketListener> var1, Predicate<ServerPlayer> var2);
   }
}
