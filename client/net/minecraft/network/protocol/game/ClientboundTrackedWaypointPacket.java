package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.minecraft.world.waypoints.TrackedWaypointManager;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointManager;

public record ClientboundTrackedWaypointPacket(Operation operation, TrackedWaypoint waypoint) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTrackedWaypointPacket> STREAM_CODEC;

   public ClientboundTrackedWaypointPacket(Operation var1, TrackedWaypoint var2) {
      super();
      this.operation = var1;
      this.waypoint = var2;
   }

   public static ClientboundTrackedWaypointPacket removeWaypoint(UUID var0) {
      return new ClientboundTrackedWaypointPacket(ClientboundTrackedWaypointPacket.Operation.UNTRACK, TrackedWaypoint.empty(var0));
   }

   public static ClientboundTrackedWaypointPacket addWaypointPosition(UUID var0, Waypoint.Icon var1, Vec3i var2) {
      return new ClientboundTrackedWaypointPacket(ClientboundTrackedWaypointPacket.Operation.TRACK, TrackedWaypoint.setPosition(var0, var1, var2));
   }

   public static ClientboundTrackedWaypointPacket updateWaypointPosition(UUID var0, Waypoint.Icon var1, Vec3i var2) {
      return new ClientboundTrackedWaypointPacket(ClientboundTrackedWaypointPacket.Operation.UPDATE, TrackedWaypoint.setPosition(var0, var1, var2));
   }

   public static ClientboundTrackedWaypointPacket addWaypointChunk(UUID var0, Waypoint.Icon var1, ChunkPos var2) {
      return new ClientboundTrackedWaypointPacket(ClientboundTrackedWaypointPacket.Operation.TRACK, TrackedWaypoint.setChunk(var0, var1, var2));
   }

   public static ClientboundTrackedWaypointPacket updateWaypointChunk(UUID var0, Waypoint.Icon var1, ChunkPos var2) {
      return new ClientboundTrackedWaypointPacket(ClientboundTrackedWaypointPacket.Operation.UPDATE, TrackedWaypoint.setChunk(var0, var1, var2));
   }

   public static ClientboundTrackedWaypointPacket addWaypointAzimuth(UUID var0, Waypoint.Icon var1, float var2) {
      return new ClientboundTrackedWaypointPacket(ClientboundTrackedWaypointPacket.Operation.TRACK, TrackedWaypoint.setAzimuth(var0, var1, var2));
   }

   public static ClientboundTrackedWaypointPacket updateWaypointAzimuth(UUID var0, Waypoint.Icon var1, float var2) {
      return new ClientboundTrackedWaypointPacket(ClientboundTrackedWaypointPacket.Operation.UPDATE, TrackedWaypoint.setAzimuth(var0, var1, var2));
   }

   public PacketType<ClientboundTrackedWaypointPacket> type() {
      return GamePacketTypes.CLIENTBOUND_WAYPOINT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleWaypoint(this);
   }

   public void apply(TrackedWaypointManager var1) {
      this.operation.action.accept(var1, this.waypoint);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ClientboundTrackedWaypointPacket.Operation.STREAM_CODEC, ClientboundTrackedWaypointPacket::operation, TrackedWaypoint.STREAM_CODEC, ClientboundTrackedWaypointPacket::waypoint, ClientboundTrackedWaypointPacket::new);
   }

   static enum Operation {
      TRACK(WaypointManager::trackWaypoint),
      UNTRACK(WaypointManager::untrackWaypoint),
      UPDATE(WaypointManager::updateWaypoint);

      final BiConsumer<TrackedWaypointManager, TrackedWaypoint> action;
      public static final IntFunction<Operation> BY_ID = ByIdMap.<Operation>continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
      public static final StreamCodec<ByteBuf, Operation> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

      private Operation(final BiConsumer<TrackedWaypointManager, TrackedWaypoint> var3) {
         this.action = var3;
      }

      // $FF: synthetic method
      private static Operation[] $values() {
         return new Operation[]{TRACK, UNTRACK, UPDATE};
      }
   }
}
