package net.minecraft.network.protocol.common;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.protocol.common.custom.ModListPayload;
import net.minecraft.util.Util;

public record ServerboundCustomPayloadPacket(CustomPacketPayload payload) implements Packet<ServerCommonPacketListener> {
   private static final int MAX_PAYLOAD_SIZE = 32767;
   public static final StreamCodec<FriendlyByteBuf, ServerboundCustomPayloadPacket> STREAM_CODEC = CustomPacketPayload.codec((CustomPacketPayload.FallbackProvider)((id) -> DiscardedPayload.codec(id, 32767)), (List)Util.make(new ArrayList(), (types) -> {
      types.add(new CustomPacketPayload.TypeAndCodec(BrandPayload.TYPE, BrandPayload.STREAM_CODEC));
      types.add(new CustomPacketPayload.TypeAndCodec(ModListPayload.TYPE, ModListPayload.STREAM_CODEC));
   })).map(ServerboundCustomPayloadPacket::new, ServerboundCustomPayloadPacket::payload);

   public ServerboundCustomPayloadPacket {
      super();
   }

   public PacketType<ServerboundCustomPayloadPacket> type() {
      return CommonPacketTypes.SERVERBOUND_CUSTOM_PAYLOAD;
   }

   public void handle(final ServerCommonPacketListener listener) {
      listener.handleCustomPayload(this);
   }
}
