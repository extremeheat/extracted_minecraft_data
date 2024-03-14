package net.minecraft.network.protocol.common;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.resources.ResourceLocation;

public record ServerboundCustomPayloadPacket(CustomPacketPayload b) implements Packet<ServerCommonPacketListener> {
   private final CustomPacketPayload payload;
   private static final int MAX_PAYLOAD_SIZE = 32767;
   public static final StreamCodec<FriendlyByteBuf, ServerboundCustomPayloadPacket> STREAM_CODEC = CustomPacketPayload.codec(
         var0 -> DiscardedPayload.codec(var0, 32767),
         Util.make(
            Lists.newArrayList(new CustomPacketPayload.TypeAndCodec[]{new CustomPacketPayload.TypeAndCodec<>(BrandPayload.TYPE, BrandPayload.STREAM_CODEC)}),
            var0 -> {
            }
         )
      )
      .map(ServerboundCustomPayloadPacket::new, ServerboundCustomPayloadPacket::payload);

   public ServerboundCustomPayloadPacket(CustomPacketPayload var1) {
      super();
      this.payload = var1;
   }

   @Override
   public PacketType<ServerboundCustomPayloadPacket> type() {
      return CommonPacketTypes.SERVERBOUND_CUSTOM_PAYLOAD;
   }

   public void handle(ServerCommonPacketListener var1) {
      var1.handleCustomPayload(this);
   }
}
