package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class ClientboundSetEntityLinkPacket implements Packet<ClientGamePacketListener> {
   private final int sourceId;
   private final int destId;

   public ClientboundSetEntityLinkPacket(Entity var1, @Nullable Entity var2) {
      super();
      this.sourceId = var1.getId();
      this.destId = var2 != null ? var2.getId() : 0;
   }

   public ClientboundSetEntityLinkPacket(FriendlyByteBuf var1) {
      super();
      this.sourceId = var1.readInt();
      this.destId = var1.readInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.sourceId);
      var1.writeInt(this.destId);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleEntityLinkPacket(this);
   }

   public int getSourceId() {
      return this.sourceId;
   }

   public int getDestId() {
      return this.destId;
   }
}
