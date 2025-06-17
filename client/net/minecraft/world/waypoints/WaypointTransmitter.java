package net.minecraft.world.waypoints;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

public interface WaypointTransmitter extends Waypoint {
   int REALLY_FAR_DISTANCE = 332;

   boolean isTransmittingWaypoint();

   Optional<Connection> makeWaypointConnectionWith(ServerPlayer var1);

   Waypoint.Icon waypointIcon();

   static boolean doesSourceIgnoreReceiver(LivingEntity var0, ServerPlayer var1) {
      if (var1.isSpectator()) {
         return false;
      } else if (!var0.isSpectator() && !var0.hasIndirectPassenger(var1)) {
         double var2 = Math.min(var0.getAttributeValue(Attributes.WAYPOINT_TRANSMIT_RANGE), var1.getAttributeValue(Attributes.WAYPOINT_RECEIVE_RANGE));
         return (double)var0.distanceTo(var1) >= var2;
      } else {
         return true;
      }
   }

   static boolean isChunkVisible(ChunkPos var0, ServerPlayer var1) {
      return var1.getChunkTrackingView().isInViewDistance(var0.x, var0.z);
   }

   static boolean isReallyFar(LivingEntity var0, ServerPlayer var1) {
      return var0.distanceTo(var1) > 332.0F;
   }

   public interface BlockConnection extends Connection {
      int distanceManhattan();

      default boolean isBroken() {
         return this.distanceManhattan() > 1;
      }
   }

   public static class EntityBlockConnection implements BlockConnection {
      private final LivingEntity source;
      private final Waypoint.Icon icon;
      private final ServerPlayer receiver;
      private BlockPos lastPosition;

      public EntityBlockConnection(LivingEntity var1, Waypoint.Icon var2, ServerPlayer var3) {
         super();
         this.source = var1;
         this.receiver = var3;
         this.icon = var2;
         this.lastPosition = var1.blockPosition();
      }

      public void connect() {
         this.receiver.connection.send(ClientboundTrackedWaypointPacket.addWaypointPosition(this.source.getUUID(), this.icon, this.lastPosition));
      }

      public void disconnect() {
         this.receiver.connection.send(ClientboundTrackedWaypointPacket.removeWaypoint(this.source.getUUID()));
      }

      public void update() {
         BlockPos var1 = this.source.blockPosition();
         if (var1.distManhattan(this.lastPosition) > 0) {
            this.receiver.connection.send(ClientboundTrackedWaypointPacket.updateWaypointPosition(this.source.getUUID(), this.icon, var1));
            this.lastPosition = var1;
         }

      }

      public int distanceManhattan() {
         return this.lastPosition.distManhattan(this.source.blockPosition());
      }

      public boolean isBroken() {
         return WaypointTransmitter.BlockConnection.super.isBroken() || WaypointTransmitter.doesSourceIgnoreReceiver(this.source, this.receiver);
      }
   }

   public interface ChunkConnection extends Connection {
      int distanceChessboard();

      default boolean isBroken() {
         return this.distanceChessboard() > 1;
      }
   }

   public static class EntityChunkConnection implements ChunkConnection {
      private final LivingEntity source;
      private final Waypoint.Icon icon;
      private final ServerPlayer receiver;
      private ChunkPos lastPosition;

      public EntityChunkConnection(LivingEntity var1, Waypoint.Icon var2, ServerPlayer var3) {
         super();
         this.source = var1;
         this.icon = var2;
         this.receiver = var3;
         this.lastPosition = var1.chunkPosition();
      }

      public int distanceChessboard() {
         return this.lastPosition.getChessboardDistance(this.source.chunkPosition());
      }

      public void connect() {
         this.receiver.connection.send(ClientboundTrackedWaypointPacket.addWaypointChunk(this.source.getUUID(), this.icon, this.lastPosition));
      }

      public void disconnect() {
         this.receiver.connection.send(ClientboundTrackedWaypointPacket.removeWaypoint(this.source.getUUID()));
      }

      public void update() {
         ChunkPos var1 = this.source.chunkPosition();
         if (var1.getChessboardDistance(this.lastPosition) > 0) {
            this.receiver.connection.send(ClientboundTrackedWaypointPacket.updateWaypointChunk(this.source.getUUID(), this.icon, var1));
            this.lastPosition = var1;
         }

      }

      public boolean isBroken() {
         return !WaypointTransmitter.ChunkConnection.super.isBroken() && !WaypointTransmitter.doesSourceIgnoreReceiver(this.source, this.receiver) ? WaypointTransmitter.isChunkVisible(this.lastPosition, this.receiver) : true;
      }
   }

   public static class EntityAzimuthConnection implements Connection {
      private final LivingEntity source;
      private final Waypoint.Icon icon;
      private final ServerPlayer receiver;
      private float lastAngle;

      public EntityAzimuthConnection(LivingEntity var1, Waypoint.Icon var2, ServerPlayer var3) {
         super();
         this.source = var1;
         this.icon = var2;
         this.receiver = var3;
         Vec3 var4 = var3.position().subtract(var1.position()).rotateClockwise90();
         this.lastAngle = (float)Mth.atan2(var4.z(), var4.x());
      }

      public boolean isBroken() {
         return WaypointTransmitter.doesSourceIgnoreReceiver(this.source, this.receiver) || WaypointTransmitter.isChunkVisible(this.source.chunkPosition(), this.receiver) || !WaypointTransmitter.isReallyFar(this.source, this.receiver);
      }

      public void connect() {
         this.receiver.connection.send(ClientboundTrackedWaypointPacket.addWaypointAzimuth(this.source.getUUID(), this.icon, this.lastAngle));
      }

      public void disconnect() {
         this.receiver.connection.send(ClientboundTrackedWaypointPacket.removeWaypoint(this.source.getUUID()));
      }

      public void update() {
         Vec3 var1 = this.receiver.position().subtract(this.source.position()).rotateClockwise90();
         float var2 = (float)Mth.atan2(var1.z(), var1.x());
         if (Mth.abs(var2 - this.lastAngle) > 0.008726646F) {
            this.receiver.connection.send(ClientboundTrackedWaypointPacket.updateWaypointAzimuth(this.source.getUUID(), this.icon, var2));
            this.lastAngle = var2;
         }

      }
   }

   public interface Connection {
      void connect();

      void disconnect();

      void update();

      boolean isBroken();
   }
}
