package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetTitlesAnimationPacket implements Packet<ClientGamePacketListener> {
   private final int fadeIn;
   private final int stay;
   private final int fadeOut;

   public ClientboundSetTitlesAnimationPacket(int var1, int var2, int var3) {
      super();
      this.fadeIn = var1;
      this.stay = var2;
      this.fadeOut = var3;
   }

   public ClientboundSetTitlesAnimationPacket(FriendlyByteBuf var1) {
      super();
      this.fadeIn = var1.readInt();
      this.stay = var1.readInt();
      this.fadeOut = var1.readInt();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.fadeIn);
      var1.writeInt(this.stay);
      var1.writeInt(this.fadeOut);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.setTitlesAnimation(this);
   }

   public int getFadeIn() {
      return this.fadeIn;
   }

   public int getStay() {
      return this.stay;
   }

   public int getFadeOut() {
      return this.fadeOut;
   }
}
