package net.minecraft.network.protocol.handshake;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientIntentionPacket(int protocolVersion, String hostName, int port, ClientIntent intention) implements Packet<ServerHandshakePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientIntentionPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientIntentionPacket>codec(ClientIntentionPacket::write, ClientIntentionPacket::new);
   private static final int MAX_HOST_LENGTH = 255;

   /** @deprecated */
   @Deprecated
   public ClientIntentionPacket(int var1, String var2, int var3, ClientIntent var4) {
      super();
      this.protocolVersion = var1;
      this.hostName = var2;
      this.port = var3;
      this.intention = var4;
   }

   private ClientIntentionPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt(), var1.readUtf(255), var1.readUnsignedShort(), ClientIntent.byId(var1.readVarInt()));
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.protocolVersion);
      var1.writeUtf(this.hostName);
      var1.writeShort(this.port);
      var1.writeVarInt(this.intention.id());
   }

   public PacketType<ClientIntentionPacket> type() {
      return HandshakePacketTypes.CLIENT_INTENTION;
   }

   public void handle(ServerHandshakePacketListener var1) {
      var1.handleIntention(this);
   }

   public boolean isTerminal() {
      return true;
   }
}
