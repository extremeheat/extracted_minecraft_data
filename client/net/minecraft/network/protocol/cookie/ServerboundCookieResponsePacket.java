package net.minecraft.network.protocol.cookie;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record ServerboundCookieResponsePacket(Identifier key, byte @Nullable [] payload) implements Packet<ServerCookiePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundCookieResponsePacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundCookieResponsePacket>codec(ServerboundCookieResponsePacket::write, ServerboundCookieResponsePacket::new);

   private ServerboundCookieResponsePacket(final FriendlyByteBuf input) {
      this(input.readIdentifier(), (byte[])input.readNullable(ClientboundStoreCookiePacket.PAYLOAD_STREAM_CODEC));
   }

   public ServerboundCookieResponsePacket {
      super();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeIdentifier(this.key);
      output.writeNullable(this.payload, ClientboundStoreCookiePacket.PAYLOAD_STREAM_CODEC);
   }

   public PacketType<ServerboundCookieResponsePacket> type() {
      return CookiePacketTypes.SERVERBOUND_COOKIE_RESPONSE;
   }

   public void handle(final ServerCookiePacketListener listener) {
      listener.handleCookieResponse(this);
   }
}
