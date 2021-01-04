package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.global.LightningBolt;

public class ClientboundAddGlobalEntityPacket implements Packet<ClientGamePacketListener> {
   private int id;
   private double x;
   private double y;
   private double z;
   private int type;

   public ClientboundAddGlobalEntityPacket() {
      super();
   }

   public ClientboundAddGlobalEntityPacket(Entity var1) {
      super();
      this.id = var1.getId();
      this.x = var1.x;
      this.y = var1.y;
      this.z = var1.z;
      if (var1 instanceof LightningBolt) {
         this.type = 1;
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.id = var1.readVarInt();
      this.type = var1.readByte();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.id);
      var1.writeByte(this.type);
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddGlobalEntity(this);
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

   public int getType() {
      return this.type;
   }
}
