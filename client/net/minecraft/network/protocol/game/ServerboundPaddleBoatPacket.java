package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPaddleBoatPacket implements Packet<ServerGamePacketListener> {
   private final boolean left;
   private final boolean right;

   public ServerboundPaddleBoatPacket(boolean var1, boolean var2) {
      super();
      this.left = var1;
      this.right = var2;
   }

   public ServerboundPaddleBoatPacket(FriendlyByteBuf var1) {
      super();
      this.left = var1.readBoolean();
      this.right = var1.readBoolean();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
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
