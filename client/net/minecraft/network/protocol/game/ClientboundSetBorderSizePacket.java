package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderSizePacket implements Packet<ClientGamePacketListener> {
   private final double size;

   public ClientboundSetBorderSizePacket(WorldBorder var1) {
      super();
      this.size = var1.getLerpTarget();
   }

   public ClientboundSetBorderSizePacket(FriendlyByteBuf var1) {
      super();
      this.size = var1.readDouble();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeDouble(this.size);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetBorderSize(this);
   }

   public double getSize() {
      return this.size;
   }
}
