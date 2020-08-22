package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPaddleBoatPacket implements Packet {
   private boolean left;
   private boolean right;

   public ServerboundPaddleBoatPacket() {
   }

   public ServerboundPaddleBoatPacket(boolean var1, boolean var2) {
      this.left = var1;
      this.right = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.left = var1.readBoolean();
      this.right = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeBoolean(this.left);
      var1.writeBoolean(this.right);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePaddleBoat(this);
   }

   public boolean getLeft() {
      return this.left;
   }

   public boolean getRight() {
      return this.right;
   }
}
