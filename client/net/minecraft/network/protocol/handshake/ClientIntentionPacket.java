package net.minecraft.network.protocol.handshake;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientIntentionPacket(int protocolVersion, String hostName, int port, ClientIntent intention) implements Packet<ServerHandshakePacketListener> {
   private static final int MAX_HOST_LENGTH = 1024;
   public static final StreamCodec<FriendlyByteBuf, ClientIntentionPacket> STREAM_CODEC;

   /** @deprecated */
   @Deprecated
   public ClientIntentionPacket(int protocolVersion, String hostName, int port, ClientIntent intention) {
      super();
      this.protocolVersion = protocolVersion;
      this.hostName = hostName;
      this.port = port;
      this.intention = intention;
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

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientIntentionPacket::protocolVersion, ByteBufCodecs.stringUtf8(1024), ClientIntentionPacket::hostName, ByteBufCodecs.UNSIGNED_SHORT, ClientIntentionPacket::port, ClientIntent.STREAM_CODEC, ClientIntentionPacket::intention, ClientIntentionPacket::new);
   }
}
