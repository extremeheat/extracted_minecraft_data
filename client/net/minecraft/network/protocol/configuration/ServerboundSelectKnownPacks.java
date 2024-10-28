package net.minecraft.network.protocol.configuration;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.packs.repository.KnownPack;

public record ServerboundSelectKnownPacks(List<KnownPack> knownPacks) implements Packet<ServerConfigurationPacketListener> {
   public static final StreamCodec<ByteBuf, ServerboundSelectKnownPacks> STREAM_CODEC;

   public ServerboundSelectKnownPacks(List<KnownPack> var1) {
      super();
      this.knownPacks = var1;
   }

   public PacketType<ServerboundSelectKnownPacks> type() {
      return ConfigurationPacketTypes.SERVERBOUND_SELECT_KNOWN_PACKS;
   }

   public void handle(ServerConfigurationPacketListener var1) {
      var1.handleSelectKnownPacks(this);
   }

   public List<KnownPack> knownPacks() {
      return this.knownPacks;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(KnownPack.STREAM_CODEC.apply(ByteBufCodecs.list(64)), ServerboundSelectKnownPacks::knownPacks, ServerboundSelectKnownPacks::new);
   }
}
