package net.minecraft.network.protocol.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundLoginCompressionPacket implements Packet<ClientLoginPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundLoginCompressionPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundLoginCompressionPacket>codec(ClientboundLoginCompressionPacket::write, ClientboundLoginCompressionPacket::new);
   private final int compressionThreshold;

   public ClientboundLoginCompressionPacket(int var1) {
      super();
      this.compressionThreshold = var1;
   }

   private ClientboundLoginCompressionPacket(FriendlyByteBuf var1) {
      super();
      this.compressionThreshold = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.compressionThreshold);
   }

   public PacketType<ClientboundLoginCompressionPacket> type() {
      return LoginPacketTypes.CLIENTBOUND_LOGIN_COMPRESSION;
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleCompression(this);
   }

   public int getCompressionThreshold() {
      return this.compressionThreshold;
   }
}
