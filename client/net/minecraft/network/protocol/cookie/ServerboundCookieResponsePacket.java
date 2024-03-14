package net.minecraft.network.protocol.cookie;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.resources.ResourceLocation;

public record ServerboundCookieResponsePacket(ResourceLocation b, @Nullable byte[] c) implements Packet<ServerCookiePacketListener> {
   private final ResourceLocation key;
   @Nullable
   private final byte[] payload;
   public static final StreamCodec<FriendlyByteBuf, ServerboundCookieResponsePacket> STREAM_CODEC = Packet.codec(
      ServerboundCookieResponsePacket::write, ServerboundCookieResponsePacket::new
   );

   private ServerboundCookieResponsePacket(FriendlyByteBuf var1) {
      this(var1.readResourceLocation(), var1.readNullable(ClientboundStoreCookiePacket.PAYLOAD_STREAM_CODEC));
   }

   public ServerboundCookieResponsePacket(ResourceLocation var1, @Nullable byte[] var2) {
      super();
      this.key = var1;
      this.payload = var2;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeResourceLocation(this.key);
      var1.writeNullable(this.payload, ClientboundStoreCookiePacket.PAYLOAD_STREAM_CODEC);
   }

   @Override
   public PacketType<ServerboundCookieResponsePacket> type() {
      return CookiePacketTypes.SERVERBOUND_COOKIE_RESPONSE;
   }

   public void handle(ServerCookiePacketListener var1) {
      var1.handleCookieResponse(this);
   }
}
