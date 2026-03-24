package net.minecraft.network.protocol.common;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.util.Util;

public record ServerboundCustomPayloadPacket(CustomPacketPayload payload) implements Packet<ServerCommonPacketListener> {
   private static final int MAX_PAYLOAD_SIZE = 32767;
   public static final StreamCodec<FriendlyByteBuf, ServerboundCustomPayloadPacket> STREAM_CODEC;

   public ServerboundCustomPayloadPacket {
      super();
   }

   public PacketType<ServerboundCustomPayloadPacket> type() {
      return CommonPacketTypes.SERVERBOUND_CUSTOM_PAYLOAD;
   }

   public void handle(final ServerCommonPacketListener listener) {
      listener.handleCustomPayload(this);
   }

   static {
      STREAM_CODEC = CustomPacketPayload.codec((CustomPacketPayload.FallbackProvider)((id) -> DiscardedPayload.codec(id, 32767)), (List)Util.make(Lists.newArrayList(new CustomPacketPayload.TypeAndCodec[]{new CustomPacketPayload.TypeAndCodec(BrandPayload.TYPE, BrandPayload.STREAM_CODEC)}), (types) -> {
      })).map(ServerboundCustomPayloadPacket::new, ServerboundCustomPayloadPacket::payload);
   }
}
