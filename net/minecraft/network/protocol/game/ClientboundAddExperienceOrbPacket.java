package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.ExperienceOrb;

public class ClientboundAddExperienceOrbPacket implements Packet {
   private int id;
   private double x;
   private double y;
   private double z;
   private int value;

   public ClientboundAddExperienceOrbPacket() {
   }

   public ClientboundAddExperienceOrbPacket(ExperienceOrb var1) {
      this.id = var1.getId();
      this.x = var1.getX();
      this.y = var1.getY();
      this.z = var1.getZ();
      this.value = var1.getValue();
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.id = var1.readVarInt();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.value = var1.readShort();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.id);
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeShort(this.value);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddExperienceOrb(this);
   }

   public int getId() {
      return this.id;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public int getValue() {
      return this.value;
   }
}
