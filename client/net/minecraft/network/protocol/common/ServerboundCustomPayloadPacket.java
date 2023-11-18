package net.minecraft.network.protocol.common;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.ResourceLocation;

public record ServerboundCustomPayloadPacket(CustomPacketPayload a) implements Packet<ServerCommonPacketListener> {
   private final CustomPacketPayload payload;
   private static final int MAX_PAYLOAD_SIZE = 32767;
   private static final Map<ResourceLocation, FriendlyByteBuf.Reader<? extends CustomPacketPayload>> KNOWN_TYPES = ImmutableMap.builder()
      .put(BrandPayload.ID, BrandPayload::new)
      .build();

   public ServerboundCustomPayloadPacket(FriendlyByteBuf var1) {
      this(readPayload(var1.readResourceLocation(), var1));
   }

   public ServerboundCustomPayloadPacket(CustomPacketPayload var1) {
      super();
      this.payload = var1;
   }

   private static CustomPacketPayload readPayload(ResourceLocation var0, FriendlyByteBuf var1) {
      FriendlyByteBuf.Reader var2 = KNOWN_TYPES.get(var0);
      return (CustomPacketPayload)(var2 != null ? (CustomPacketPayload)var2.apply(var1) : readUnknownPayload(var0, var1));
   }

   private static DiscardedPayload readUnknownPayload(ResourceLocation var0, FriendlyByteBuf var1) {
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 32767) {
         var1.skipBytes(var2);
         return new DiscardedPayload(var0);
      } else {
         throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
      }
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeResourceLocation(this.payload.id());
      this.payload.write(var1);
   }

   public void handle(ServerCommonPacketListener var1) {
      var1.handleCustomPayload(this);
   }
}
