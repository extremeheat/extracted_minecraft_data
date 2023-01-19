package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundRemoveEntitiesPacket implements Packet<ClientGamePacketListener> {
   private final IntList entityIds;

   public ClientboundRemoveEntitiesPacket(IntList var1) {
      super();
      this.entityIds = new IntArrayList(var1);
   }

   public ClientboundRemoveEntitiesPacket(int... var1) {
      super();
      this.entityIds = new IntArrayList(var1);
   }

   public ClientboundRemoveEntitiesPacket(FriendlyByteBuf var1) {
      super();
      this.entityIds = var1.readIntIdList();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeIntIdList(this.entityIds);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRemoveEntities(this);
   }

   public IntList getEntityIds() {
      return this.entityIds;
   }
}
