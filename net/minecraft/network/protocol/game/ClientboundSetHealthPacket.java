package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetHealthPacket implements Packet {
   private float health;
   private int food;
   private float saturation;

   public ClientboundSetHealthPacket() {
   }

   public ClientboundSetHealthPacket(float var1, int var2, float var3) {
      this.health = var1;
      this.food = var2;
      this.saturation = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.health = var1.readFloat();
      this.food = var1.readVarInt();
      this.saturation = var1.readFloat();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
