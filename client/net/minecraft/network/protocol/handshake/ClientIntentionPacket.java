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
   public ClientIntentionPacket(int protocolVersion, String hostName, int port, ClientIntent intention) {
      super();
      this.protocolVersion = protocolVersion;
      this.hostName = hostName;
      this.port = port;
      this.intention = intention;
   }

   private ClientIntentionPacket(final FriendlyByteBuf input) {
      this(input.readVarInt(), input.readUtf(255), input.readUnsignedShort(), ClientIntent.byId(input.readVarInt()));
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.protocolVersion);
      output.writeUtf(this.hostName);
      output.writeShort(this.port);
      output.writeVarInt(this.intention.id());
   }

   public PacketType<ClientIntentionPacket> type() {
      return HandshakePacketTypes.CLIENT_INTENTION;
   }

   public void handle(final ServerHandshakePacketListener listener) {
      listener.handleIntention(this);
   }

   public boolean isTerminal() {
      return true;
   }
}
