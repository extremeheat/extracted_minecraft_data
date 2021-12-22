package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;

public class ServerboundUseItemPacket implements Packet<ServerGamePacketListener> {
   private final InteractionHand hand;

   public ServerboundUseItemPacket(InteractionHand var1) {
      super();
      this.hand = var1;
   }

   public ServerboundUseItemPacket(FriendlyByteBuf var1) {
      super();
      this.hand = (InteractionHand)var1.readEnum(InteractionHand.class);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.hand);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleUseItem(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }
}
