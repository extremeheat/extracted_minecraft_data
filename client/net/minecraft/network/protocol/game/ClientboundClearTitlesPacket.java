package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundClearTitlesPacket implements Packet<ClientGamePacketListener> {
   private final boolean resetTimes;

   public ClientboundClearTitlesPacket(boolean var1) {
      super();
      this.resetTimes = var1;
   }

   public ClientboundClearTitlesPacket(FriendlyByteBuf var1) {
      super();
      this.resetTimes = var1.readBoolean();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeBoolean(this.resetTimes);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleTitlesClear(this);
   }

   public boolean shouldResetTimes() {
      return this.resetTimes;
   }
}
