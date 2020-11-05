package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class ClientboundAnimatePacket implements Packet<ClientGamePacketListener> {
   private int id;
   private int action;

   public ClientboundAnimatePacket() {
      super();
   }

   public ClientboundAnimatePacket(Entity var1, int var2) {
      super();
      this.id = var1.getId();
      this.action = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.id = var1.readVarInt();
      this.action = var1.readUnsignedByte();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.id);
      var1.writeByte(this.action);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAnimate(this);
   }

   public int getId() {
      return this.id;
   }

   public int getAction() {
      return this.action;
   }
}
