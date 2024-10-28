package net.minecraft.network.protocol.game;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class ServerboundTeleportToEntityPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundTeleportToEntityPacket> STREAM_CODEC = Packet.codec(ServerboundTeleportToEntityPacket::write, ServerboundTeleportToEntityPacket::new);
   private final UUID uuid;

   public ServerboundTeleportToEntityPacket(UUID var1) {
      super();
      this.uuid = var1;
   }

   private ServerboundTeleportToEntityPacket(FriendlyByteBuf var1) {
      super();
      this.uuid = var1.readUUID();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeUUID(this.uuid);
   }

   public PacketType<ServerboundTeleportToEntityPacket> type() {
      return GamePacketTypes.SERVERBOUND_TELEPORT_TO_ENTITY;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleTeleportToEntityPacket(this);
   }

   @Nullable
   public Entity getEntity(ServerLevel var1) {
      return var1.getEntity(this.uuid);
   }
}
