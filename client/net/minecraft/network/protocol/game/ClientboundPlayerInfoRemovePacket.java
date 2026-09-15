package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundPlayerInfoRemovePacket(List<UUID> profileIds) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundPlayerInfoRemovePacket> STREAM_CODEC;

   public ClientboundPlayerInfoRemovePacket {
      super();
   }

   public PacketType<ClientboundPlayerInfoRemovePacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_INFO_REMOVE;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handlePlayerInfoRemove(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundPlayerInfoRemovePacket::profileIds, ClientboundPlayerInfoRemovePacket::new);
   }
}
