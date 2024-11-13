package net.minecraft.network.protocol.cookie;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public record ClientboundCookieRequestPacket(ResourceLocation key) implements Packet<ClientCookiePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundCookieRequestPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundCookieRequestPacket>codec(ClientboundCookieRequestPacket::write, ClientboundCookieRequestPacket::new);

   private ClientboundCookieRequestPacket(FriendlyByteBuf var1) {
      this(var1.readResourceLocation());
   }

   public ClientboundCookieRequestPacket(ResourceLocation var1) {
      super();
      this.key = var1;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeResourceLocation(this.key);
   }

   public PacketType<ClientboundCookieRequestPacket> type() {
      return CookiePacketTypes.CLIENTBOUND_COOKIE_REQUEST;
   }

   public void handle(ClientCookiePacketListener var1) {
      var1.handleRequestCookie(this);
   }
}
