package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;

public class ServerboundSwingPacket implements Packet<ServerGamePacketListener> {
   private final InteractionHand hand;

   public ServerboundSwingPacket(InteractionHand var1) {
      super();
      this.hand = var1;
   }

   public ServerboundSwingPacket(FriendlyByteBuf var1) {
      super();
      this.hand = var1.readEnum(InteractionHand.class);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.hand);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleAnimate(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }
}
