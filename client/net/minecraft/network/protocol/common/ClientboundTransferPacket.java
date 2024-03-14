package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundTransferPacket(String b, int c) implements Packet<ClientCommonPacketListener> {
   private final String host;
   private final int port;
   public static final StreamCodec<FriendlyByteBuf, ClientboundTransferPacket> STREAM_CODEC = Packet.codec(
      ClientboundTransferPacket::write, ClientboundTransferPacket::new
   );

   private ClientboundTransferPacket(FriendlyByteBuf var1) {
      this(var1.readUtf(), var1.readVarInt());
   }

   public ClientboundTransferPacket(String var1, int var2) {
      super();
      this.host = var1;
      this.port = var2;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.host);
      var1.writeVarInt(this.port);
   }

   @Override
   public PacketType<ClientboundTransferPacket> type() {
      return CommonPacketTypes.CLIENTBOUND_TRANSFER;
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handleTransfer(this);
   }
}
