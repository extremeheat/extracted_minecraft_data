package net.minecraft.network.protocol.game;

import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;

public record ClientboundPlayerPositionPacket(int id, PositionMoveRotation change, Set<Relative> relatives) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundPlayerPositionPacket> STREAM_CODEC;

   public ClientboundPlayerPositionPacket(int var1, PositionMoveRotation var2, Set<Relative> var3) {
      super();
      this.id = var1;
      this.change = var2;
      this.relatives = var3;
   }

   public static ClientboundPlayerPositionPacket of(int var0, PositionMoveRotation var1, Set<Relative> var2) {
      return new ClientboundPlayerPositionPacket(var0, var1, var2);
   }

   public PacketType<ClientboundPlayerPositionPacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_POSITION;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleMovePlayer(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundPlayerPositionPacket::id, PositionMoveRotation.STREAM_CODEC, ClientboundPlayerPositionPacket::change, Relative.SET_STREAM_CODEC, ClientboundPlayerPositionPacket::relatives, ClientboundPlayerPositionPacket::new);
   }
}
