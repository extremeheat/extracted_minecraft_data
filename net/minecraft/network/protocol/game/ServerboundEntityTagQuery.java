package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundEntityTagQuery implements Packet {
   private int transactionId;
   private int entityId;

   public ServerboundEntityTagQuery() {
   }

   public ServerboundEntityTagQuery(int var1, int var2) {
      this.transactionId = var1;
      this.entityId = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.transactionId = var1.readVarInt();
      this.entityId = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.transactionId);
      var1.writeVarInt(this.entityId);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleEntityTagQuery(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public int getEntityId() {
      return this.entityId;
   }
}
