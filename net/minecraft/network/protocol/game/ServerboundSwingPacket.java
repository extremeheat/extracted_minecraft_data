package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;

public class ServerboundSwingPacket implements Packet {
   private InteractionHand hand;

   public ServerboundSwingPacket() {
   }

   public ServerboundSwingPacket(InteractionHand var1) {
      this.hand = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.hand = (InteractionHand)var1.readEnum(InteractionHand.class);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeEnum(this.hand);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleAnimate(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }
}
