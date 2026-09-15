package net.minecraft.network.protocol.common;

import io.netty.buffer.ByteBuf;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagNetworkSerialization;

public record ClientboundUpdateTagsPacket(Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> tags) implements Packet<ClientCommonPacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundUpdateTagsPacket> STREAM_CODEC;

   public ClientboundUpdateTagsPacket {
      super();
   }

   public PacketType<ClientboundUpdateTagsPacket> type() {
      return CommonPacketTypes.CLIENTBOUND_UPDATE_TAGS;
   }

   public void handle(final ClientCommonPacketListener listener) {
      listener.handleUpdateTags(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.map(IdentityHashMap::new, ResourceKey.REGISTRY_STREAM_CODEC, TagNetworkSerialization.NetworkPayload.STREAM_CODEC), ClientboundUpdateTagsPacket::tags, ClientboundUpdateTagsPacket::new);
   }
}
