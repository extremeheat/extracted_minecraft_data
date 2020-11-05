package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundSelectTradePacket implements Packet<ServerGamePacketListener> {
   private int item;

   public ServerboundSelectTradePacket() {
      super();
   }

   public ServerboundSelectTradePacket(int var1) {
      super();
      this.item = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.item = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.item);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSelectTrade(this);
   }

   public int getItem() {
      return this.item;
   }
}
