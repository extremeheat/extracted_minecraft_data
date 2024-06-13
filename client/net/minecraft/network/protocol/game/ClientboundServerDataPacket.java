package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundServerDataPacket(Component motd, Optional<byte[]> iconBytes) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundServerDataPacket> STREAM_CODEC = StreamCodec.composite(
      ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC,
      ClientboundServerDataPacket::motd,
      ByteBufCodecs.BYTE_ARRAY.apply(ByteBufCodecs::optional),
      ClientboundServerDataPacket::iconBytes,
      ClientboundServerDataPacket::new
   );

   public ClientboundServerDataPacket(Component motd, Optional<byte[]> iconBytes) {
      super();
      this.motd = motd;
      this.iconBytes = iconBytes;
   }

   @Override
   public PacketType<ClientboundServerDataPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SERVER_DATA;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleServerData(this);
   }
}
