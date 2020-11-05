package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundEntityEventPacket implements Packet<ClientGamePacketListener> {
   private int entityId;
   private byte eventId;

   public ClientboundEntityEventPacket() {
      super();
   }

   public ClientboundEntityEventPacket(Entity var1, byte var2) {
      super();
      this.entityId = var1.getId();
      this.eventId = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.entityId = var1.readInt();
      this.eventId = var1.readByte();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeInt(this.entityId);
      var1.writeByte(this.eventId);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleEntityEvent(this);
   }

   public Entity getEntity(Level var1) {
      return var1.getEntity(this.entityId);
   }

   public byte getEventId() {
      return this.eventId;
   }
}
