package net.minecraft.network.protocol.login;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.login.custom.CustomQueryPayload;
import net.minecraft.network.protocol.login.custom.DiscardedQueryPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientboundCustomQueryPacket(int transactionId, CustomQueryPayload payload) implements Packet<ClientLoginPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundCustomQueryPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundCustomQueryPacket>codec(ClientboundCustomQueryPacket::write, ClientboundCustomQueryPacket::new);
   private static final int MAX_PAYLOAD_SIZE = 1048576;

   private ClientboundCustomQueryPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt(), readPayload(var1.readResourceLocation(), var1));
   }

   public ClientboundCustomQueryPacket(int var1, CustomQueryPayload var2) {
      super();
      this.transactionId = var1;
      this.payload = var2;
   }

   private static CustomQueryPayload readPayload(ResourceLocation var0, FriendlyByteBuf var1) {
      return readUnknownPayload(var0, var1);
   }

   private static DiscardedQueryPayload readUnknownPayload(ResourceLocation var0, FriendlyByteBuf var1) {
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 1048576) {
         var1.skipBytes(var2);
         return new DiscardedQueryPayload(var0);
      } else {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.transactionId);
      var1.writeResourceLocation(this.payload.id());
      this.payload.write(var1);
   }

   public PacketType<ClientboundCustomQueryPacket> type() {
      return LoginPacketTypes.CLIENTBOUND_CUSTOM_QUERY;
   }

   public void handle(ClientLoginPacketListener var1) {
      var1.handleCustomQuery(this);
   }
}
