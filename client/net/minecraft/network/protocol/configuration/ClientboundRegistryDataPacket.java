package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record ClientboundRegistryDataPacket(ResourceKey<? extends Registry<?>> registry, List<RegistrySynchronization.PackedRegistryEntry> entries) implements Packet<ClientConfigurationPacketListener> {
   private static final StreamCodec<ByteBuf, ResourceKey<? extends Registry<?>>> REGISTRY_KEY_STREAM_CODEC;
   public static final StreamCodec<FriendlyByteBuf, ClientboundRegistryDataPacket> STREAM_CODEC;

   public ClientboundRegistryDataPacket(ResourceKey<? extends Registry<?>> registry, List<RegistrySynchronization.PackedRegistryEntry> entries) {
      super();
      this.registry = registry;
      this.entries = entries;
   }

   public PacketType<ClientboundRegistryDataPacket> type() {
      return ConfigurationPacketTypes.CLIENTBOUND_REGISTRY_DATA;
   }

   public void handle(ClientConfigurationPacketListener var1) {
      var1.handleRegistryData(this);
   }

   public ResourceKey<? extends Registry<?>> registry() {
      return this.registry;
   }

   public List<RegistrySynchronization.PackedRegistryEntry> entries() {
      return this.entries;
   }

   static {
      REGISTRY_KEY_STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(ResourceKey::createRegistryKey, ResourceKey::location);
      STREAM_CODEC = StreamCodec.composite(REGISTRY_KEY_STREAM_CODEC, ClientboundRegistryDataPacket::registry, RegistrySynchronization.PackedRegistryEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundRegistryDataPacket::entries, ClientboundRegistryDataPacket::new);
   }
}
