package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.ExperienceOrb;

public class ClientboundAddExperienceOrbPacket implements Packet<ClientGamePacketListener> {
   private final int id;
   private final double x;
   private final double y;
   private final double z;
   private final int value;

   public ClientboundAddExperienceOrbPacket(ExperienceOrb var1) {
      super();
      this.id = var1.getId();
      this.x = var1.getX();
      this.y = var1.getY();
      this.z = var1.getZ();
      this.value = var1.getValue();
   }

   public ClientboundAddExperienceOrbPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.value = var1.readShort();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
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
