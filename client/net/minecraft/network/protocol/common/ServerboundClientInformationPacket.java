package net.minecraft.network.protocol.common;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ClientInformation;

public record ServerboundClientInformationPacket(ClientInformation a) implements Packet<ServerCommonPacketListener> {
   private final ClientInformation information;

   public ServerboundClientInformationPacket(FriendlyByteBuf var1) {
      this(new ClientInformation(var1));
   }

   public ServerboundClientInformationPacket(ClientInformation var1) {
      super();
      this.information = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      this.information.write(var1);
   }

   public void handle(ServerCommonPacketListener var1) {
      var1.handleClientInformation(this);
   }
}
