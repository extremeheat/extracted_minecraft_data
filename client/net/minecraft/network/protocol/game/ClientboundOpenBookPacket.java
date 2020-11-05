package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;

public class ClientboundOpenBookPacket implements Packet<ClientGamePacketListener> {
   private InteractionHand hand;

   public ClientboundOpenBookPacket() {
      super();
   }

   public ClientboundOpenBookPacket(InteractionHand var1) {
      super();
      this.hand = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.hand = (InteractionHand)var1.readEnum(InteractionHand.class);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeEnum(this.hand);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleOpenBook(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }
}
