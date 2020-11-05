package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundRemoveEntitiesPacket implements Packet<ClientGamePacketListener> {
   private int[] entityIds;

   public ClientboundRemoveEntitiesPacket() {
      super();
   }

   public ClientboundRemoveEntitiesPacket(int... var1) {
      super();
      this.entityIds = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.entityIds = new int[var1.readVarInt()];

      for(int var2 = 0; var2 < this.entityIds.length; ++var2) {
         this.entityIds[var2] = var1.readVarInt();
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.entityIds.length);
      int[] var2 = this.entityIds;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         var1.writeVarInt(var5);
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRemoveEntity(this);
   }

   public int[] getEntityIds() {
      return this.entityIds;
   }
}
