package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.packs.repository.KnownPack;

public record ClientboundSelectKnownPacks(List<KnownPack> knownPacks) implements Packet<ClientConfigurationPacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundSelectKnownPacks> STREAM_CODEC;

   public ClientboundSelectKnownPacks(List<KnownPack> var1) {
      super();
      this.knownPacks = var1;
   }

   public PacketType<ClientboundSelectKnownPacks> type() {
      return ConfigurationPacketTypes.CLIENTBOUND_SELECT_KNOWN_PACKS;
   }

   public void handle(ClientConfigurationPacketListener var1) {
      var1.handleSelectKnownPacks(this);
   }

   public List<KnownPack> knownPacks() {
      return this.knownPacks;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(KnownPack.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundSelectKnownPacks::knownPacks, ClientboundSelectKnownPacks::new);
   }
}
