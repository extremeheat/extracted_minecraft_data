package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class ClientboundSetEntityLinkPacket implements Packet {
   private int sourceId;
   private int destId;

   public ClientboundSetEntityLinkPacket() {
   }

   public ClientboundSetEntityLinkPacket(Entity var1, @Nullable Entity var2) {
      this.sourceId = var1.getId();
      this.destId = var2 != null ? var2.getId() : 0;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.sourceId = var1.readInt();
      this.destId = var1.readInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
