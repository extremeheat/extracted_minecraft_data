package net.minecraft.network.protocol.common;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundTransferPacket(String host, int port, Map<String, String> properties) implements Packet<ClientCommonPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundTransferPacket> STREAM_CODEC;

   public ClientboundTransferPacket {
      super();
   }

   public PacketType<ClientboundTransferPacket> type() {
      return CommonPacketTypes.CLIENTBOUND_TRANSFER;
   }

   public void handle(final ClientCommonPacketListener listener) {
      listener.handleTransfer(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, ClientboundTransferPacket::host, ByteBufCodecs.VAR_INT, ClientboundTransferPacket::port, ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8), ClientboundTransferPacket::properties, ClientboundTransferPacket::new);
   }
}
