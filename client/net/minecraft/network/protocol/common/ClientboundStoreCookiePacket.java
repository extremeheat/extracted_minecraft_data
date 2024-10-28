package net.minecraft.network.protocol.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public record ClientboundStoreCookiePacket(ResourceLocation key, byte[] payload) implements Packet<ClientCommonPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundStoreCookiePacket> STREAM_CODEC = Packet.codec(ClientboundStoreCookiePacket::write, ClientboundStoreCookiePacket::new);
   private static final int MAX_PAYLOAD_SIZE = 5120;
   public static final StreamCodec<ByteBuf, byte[]> PAYLOAD_STREAM_CODEC = ByteBufCodecs.byteArray(5120);

   private ClientboundStoreCookiePacket(FriendlyByteBuf var1) {
      this(var1.readResourceLocation(), (byte[])PAYLOAD_STREAM_CODEC.decode(var1));
   }

   public ClientboundStoreCookiePacket(ResourceLocation key, byte[] payload) {
      super();
      this.key = key;
      this.payload = payload;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeResourceLocation(this.key);
      PAYLOAD_STREAM_CODEC.encode(var1, this.payload);
   }

   public PacketType<ClientboundStoreCookiePacket> type() {
      return CommonPacketTypes.CLIENTBOUND_STORE_COOKIE;
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handleStoreCookie(this);
   }

   public ResourceLocation key() {
      return this.key;
   }

   public byte[] payload() {
      return this.payload;
   }
}
