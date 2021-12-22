package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.ExperienceOrb;

public class ClientboundAddExperienceOrbPacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: id int
   private final int field_198;
   // $FF: renamed from: x double
   private final double field_199;
   // $FF: renamed from: y double
   private final double field_200;
   // $FF: renamed from: z double
   private final double field_201;
   private final int value;

   public ClientboundAddExperienceOrbPacket(ExperienceOrb var1) {
      super();
      this.field_198 = var1.getId();
      this.field_199 = var1.getX();
      this.field_200 = var1.getY();
      this.field_201 = var1.getZ();
      this.value = var1.getValue();
   }

   public ClientboundAddExperienceOrbPacket(FriendlyByteBuf var1) {
      super();
      this.field_198 = var1.readVarInt();
      this.field_199 = var1.readDouble();
      this.field_200 = var1.readDouble();
      this.field_201 = var1.readDouble();
      this.value = var1.readShort();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.field_198);
      var1.writeDouble(this.field_199);
      var1.writeDouble(this.field_200);
      var1.writeDouble(this.field_201);
      var1.writeShort(this.value);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddExperienceOrb(this);
   }

   public int getId() {
      return this.field_198;
   }

   public double getX() {
      return this.field_199;
   }

   public double getY() {
      return this.field_200;
   }

   public double getZ() {
      return this.field_201;
   }

   public int getValue() {
      return this.value;
   }
}
