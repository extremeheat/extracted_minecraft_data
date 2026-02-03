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

   Optional<Connection> makeWaypointConnectionWith(ServerPlayer player);

   Waypoint.Icon waypointIcon();

   static boolean doesSourceIgnoreReceiver(final LivingEntity source, final ServerPlayer receiver) {
      if (receiver.isSpectator()) {
         return false;
      } else if (!source.isSpectator() && !source.hasIndirectPassenger(receiver)) {
         double broadcastRange = Math.min(source.getAttributeValue(Attributes.WAYPOINT_TRANSMIT_RANGE), receiver.getAttributeValue(Attributes.WAYPOINT_RECEIVE_RANGE));
         return (double)source.distanceTo(receiver) >= broadcastRange;
      } else {
         return true;
      }
   }

   static boolean isChunkVisible(final ChunkPos chunkPos, final ServerPlayer receiver) {
      return receiver.getChunkTrackingView().isInViewDistance(chunkPos.x(), chunkPos.z());
   }

   static boolean isReallyFar(final LivingEntity source, final ServerPlayer receiver) {
      return source.distanceTo(receiver) > 332.0F;
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

      public EntityBlockConnection(final LivingEntity source, final Waypoint.Icon icon, final ServerPlayer receiver) {
         super();
         this.source = source;
         this.receiver = receiver;
         this.icon = icon;
         this.lastPosition = source.blockPosition();
      }

      public void connect() {
         this.receiver.connection.send(ClientboundTrackedWaypointPacket.addWaypointPosition(this.source.getUUID(), this.icon, this.lastPosition));
      }

      public void disconnect() {
         this.receiver.connection.send(ClientboundTrackedWaypointPacket.removeWaypoint(this.source.getUUID()));
      }

      public void update() {
         BlockPos currentPosition = this.source.blockPosition();
         if (currentPosition.distManhattan(this.lastPosition) > 0) {
            this.receiver.connection.send(ClientboundTrackedWaypointPacket.updateWaypointPosition(this.source.getUUID(), this.icon, currentPosition));
            this.lastPosition = currentPosition;
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

      public EntityChunkConnection(final LivingEntity source, final Waypoint.Icon icon, final ServerPlayer receiver) {
         super();
         this.source = source;
         this.icon = icon;
         this.receiver = receiver;
         this.lastPosition = source.chunkPosition();
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
         ChunkPos currentPosition = this.source.chunkPosition();
         if (currentPosition.getChessboardDistance(this.lastPosition) > 0) {
            this.receiver.connection.send(ClientboundTrackedWaypointPacket.updateWaypointChunk(this.source.getUUID(), this.icon, currentPosition));
            this.lastPosition = currentPosition;
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

      public EntityAzimuthConnection(final LivingEntity source, final Waypoint.Icon icon, final ServerPlayer receiver) {
         super();
         this.source = source;
         this.icon = icon;
         this.receiver = receiver;
         Vec3 direction = receiver.position().subtract(source.position()).rotateClockwise90();
         this.lastAngle = (float)Mth.atan2(direction.z(), direction.x());
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
         Vec3 direction = this.receiver.position().subtract(this.source.position()).rotateClockwise90();
         float currentAngle = (float)Mth.atan2(direction.z(), direction.x());
         if (Mth.abs(currentAngle - this.lastAngle) > 0.008726646F) {
            this.receiver.connection.send(ClientboundTrackedWaypointPacket.updateWaypointAzimuth(this.source.getUUID(), this.icon, currentAngle));
            this.lastAngle = currentAngle;
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
