package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;

public class ClientboundOpenBookPacket implements Packet<ClientGamePacketListener> {
   private final InteractionHand hand;

   public ClientboundOpenBookPacket(InteractionHand var1) {
      super();
      this.hand = var1;
   }

   public ClientboundOpenBookPacket(FriendlyByteBuf var1) {
      super();
      this.hand = var1.readEnum(InteractionHand.class);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.hand);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleOpenBook(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }
}
