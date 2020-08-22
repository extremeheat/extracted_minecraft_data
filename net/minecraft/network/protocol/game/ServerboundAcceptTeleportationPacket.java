package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundAcceptTeleportationPacket implements Packet {
   private int id;

   public ServerboundAcceptTeleportationPacket() {
   }

   public ServerboundAcceptTeleportationPacket(int var1) {
      this.id = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.id = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.id);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleAcceptTeleportPacket(this);
   }

   public int getId() {
      return this.id;
   }
}
