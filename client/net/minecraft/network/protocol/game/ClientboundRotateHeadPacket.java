package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundRotateHeadPacket implements Packet<ClientGamePacketListener> {
   private final int entityId;
   private final byte yHeadRot;

   public ClientboundRotateHeadPacket(Entity var1, byte var2) {
      super();
      this.entityId = var1.getId();
      this.yHeadRot = var2;
   }

   public ClientboundRotateHeadPacket(FriendlyByteBuf var1) {
      super();
      this.entityId = var1.readVarInt();
      this.yHeadRot = var1.readByte();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
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
