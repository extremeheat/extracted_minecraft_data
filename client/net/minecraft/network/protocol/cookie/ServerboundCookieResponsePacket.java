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

   private ServerboundCookieResponsePacket(FriendlyByteBuf var1) {
      this(var1.readIdentifier(), (byte[])var1.readNullable(ClientboundStoreCookiePacket.PAYLOAD_STREAM_CODEC));
   }

   public ServerboundCookieResponsePacket(Identifier var1, byte @Nullable [] var2) {
      super();
      this.key = var1;
      this.payload = var2;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeIdentifier(this.key);
      var1.writeNullable(this.payload, ClientboundStoreCookiePacket.PAYLOAD_STREAM_CODEC);
   }

   public PacketType<ServerboundCookieResponsePacket> type() {
      return CookiePacketTypes.SERVERBOUND_COOKIE_RESPONSE;
   }

   public void handle(ServerCookiePacketListener var1) {
      var1.handleCookieResponse(this);
   }
}
