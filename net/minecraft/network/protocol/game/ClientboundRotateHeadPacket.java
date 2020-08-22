package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundRotateHeadPacket implements Packet {
   private int entityId;
   private byte yHeadRot;

   public ClientboundRotateHeadPacket() {
   }

   public ClientboundRotateHeadPacket(Entity var1, byte var2) {
      this.entityId = var1.getId();
      this.yHeadRot = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.entityId = var1.readVarInt();
      this.yHeadRot = var1.readByte();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.entityId);
      var1.writeByte(this.yHeadRot);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRotateMob(this);
   }

   public Entity getEntity(Level var1) {
      return var1.getEntity(this.entityId);
   }

   public byte getYHeadRot() {
      return this.yHeadRot;
   }
}
