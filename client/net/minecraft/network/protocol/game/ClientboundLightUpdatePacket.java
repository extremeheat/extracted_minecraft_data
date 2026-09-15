package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.util.BitSet;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jspecify.annotations.Nullable;

public record ClientboundLightUpdatePacket(int x, int z, ClientboundLightUpdatePacketData lightData) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundLightUpdatePacket> STREAM_CODEC;

   public ClientboundLightUpdatePacket(final ChunkPos pos, final LevelLightEngine lightEngine, final @Nullable BitSet skyChangedLightSectionFilter, final @Nullable BitSet blockChangedLightSectionFilter) {
      this(pos.x(), pos.z(), new ClientboundLightUpdatePacketData(pos, lightEngine, skyChangedLightSectionFilter, blockChangedLightSectionFilter));
   }

   public ClientboundLightUpdatePacket {
      super();
   }

   public PacketType<ClientboundLightUpdatePacket> type() {
      return GamePacketTypes.CLIENTBOUND_LIGHT_UPDATE;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleLightUpdatePacket(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundLightUpdatePacket::x, ByteBufCodecs.VAR_INT, ClientboundLightUpdatePacket::z, ClientboundLightUpdatePacketData.STREAM_CODEC, ClientboundLightUpdatePacket::lightData, ClientboundLightUpdatePacket::new);
   }
}
