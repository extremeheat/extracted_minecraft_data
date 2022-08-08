package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetHealthPacket implements Packet<ClientGamePacketListener> {
   private final float health;
   private final int food;
   private final float saturation;

   public ClientboundSetHealthPacket(float var1, int var2, float var3) {
      super();
      this.health = var1;
      this.food = var2;
      this.saturation = var3;
   }

   public ClientboundSetHealthPacket(FriendlyByteBuf var1) {
      super();
      this.health = var1.readFloat();
      this.food = var1.readVarInt();
      this.saturation = var1.readFloat();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeFloat(this.health);
      var1.writeVarInt(this.food);
      var1.writeFloat(this.saturation);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetHealth(this);
   }

   public float getHealth() {
      return this.health;
   }

   public int getFood() {
      return this.food;
   }

   public float getSaturation() {
      return this.saturation;
   }
}
