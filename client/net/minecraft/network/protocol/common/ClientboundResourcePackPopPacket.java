package net.minecraft.network.protocol.common;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundResourcePackPopPacket(Optional<UUID> id) implements Packet<ClientCommonPacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundResourcePackPopPacket> STREAM_CODEC = Packet.codec(
      ClientboundResourcePackPopPacket::write, ClientboundResourcePackPopPacket::new
   );

   private ClientboundResourcePackPopPacket(FriendlyByteBuf var1) {
      this(var1.readOptional(UUIDUtil.STREAM_CODEC));
   }

   public ClientboundResourcePackPopPacket(Optional<UUID> id) {
      super();
      this.id = id;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeOptional(this.id, UUIDUtil.STREAM_CODEC);
   }

   @Override
   public PacketType<ClientboundResourcePackPopPacket> type() {
      return CommonPacketTypes.CLIENTBOUND_RESOURCE_PACK_POP;
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handleResourcePackPop(this);
   }
}
