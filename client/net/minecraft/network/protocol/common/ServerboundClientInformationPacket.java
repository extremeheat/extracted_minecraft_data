package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.level.ClientInformation;

public record ServerboundClientInformationPacket(ClientInformation b) implements Packet<ServerCommonPacketListener> {
   private final ClientInformation information;
   public static final StreamCodec<FriendlyByteBuf, ServerboundClientInformationPacket> STREAM_CODEC = Packet.codec(
      ServerboundClientInformationPacket::write, ServerboundClientInformationPacket::new
   );

   private ServerboundClientInformationPacket(FriendlyByteBuf var1) {
      this(new ClientInformation(var1));
   }

   public ServerboundClientInformationPacket(ClientInformation var1) {
      super();
      this.information = var1;
   }

   private void write(FriendlyByteBuf var1) {
      this.information.write(var1);
   }

   @Override
   public PacketType<ServerboundClientInformationPacket> type() {
      return CommonPacketTypes.SERVERBOUND_CLIENT_INFORMATION;
   }

   public void handle(ServerCommonPacketListener var1) {
      var1.handleClientInformation(this);
   }
}
