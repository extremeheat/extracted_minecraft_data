package net.minecraft.network.protocol.game;

import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;

public record ClientboundTeleportEntityPacket(int id, PositionMoveRotation change, Set<Relative> relatives, boolean onGround) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundTeleportEntityPacket> STREAM_CODEC;

   public ClientboundTeleportEntityPacket(int var1, PositionMoveRotation var2, Set<Relative> var3, boolean var4) {
      super();
      this.id = var1;
      this.change = var2;
      this.relatives = var3;
      this.onGround = var4;
   }

   public static ClientboundTeleportEntityPacket teleport(int var0, PositionMoveRotation var1, Set<Relative> var2, boolean var3) {
      return new ClientboundTeleportEntityPacket(var0, var1, var2, var3);
   }

   public PacketType<ClientboundTeleportEntityPacket> type() {
      return GamePacketTypes.CLIENTBOUND_TELEPORT_ENTITY;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleTeleportEntity(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundTeleportEntityPacket::id, PositionMoveRotation.STREAM_CODEC, ClientboundTeleportEntityPacket::change, Relative.SET_STREAM_CODEC, ClientboundTeleportEntityPacket::relatives, ByteBufCodecs.BOOL, ClientboundTeleportEntityPacket::onGround, ClientboundTeleportEntityPacket::new);
   }
}
