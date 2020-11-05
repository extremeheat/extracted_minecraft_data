package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundSetBeaconPacket implements Packet<ServerGamePacketListener> {
   private int primary;
   private int secondary;

   public ServerboundSetBeaconPacket() {
      super();
   }

   public ServerboundSetBeaconPacket(int var1, int var2) {
      super();
      this.primary = var1;
      this.secondary = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.primary = var1.readVarInt();
      this.secondary = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.primary);
      var1.writeVarInt(this.secondary);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetBeaconPacket(this);
   }

   public int getPrimary() {
      return this.primary;
   }

   public int getSecondary() {
      return this.secondary;
   }
}
