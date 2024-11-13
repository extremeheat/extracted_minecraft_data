package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundPlayerLoadedPacket() implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<ByteBuf, ServerboundPlayerLoadedPacket> STREAM_CODEC = StreamCodec.<ByteBuf, ServerboundPlayerLoadedPacket>unit(new ServerboundPlayerLoadedPacket());

   public ServerboundPlayerLoadedPacket() {
      super();
   }

   public PacketType<ServerboundPlayerLoadedPacket> type() {
      return GamePacketTypes.SERVERBOUND_PLAYER_LOADED;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleAcceptPlayerLoad(this);
   }
}
