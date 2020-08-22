package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;

public class ClientboundSetEntityDataPacket implements Packet {
   private int id;
   private List packedItems;

   public ClientboundSetEntityDataPacket() {
   }

   public ClientboundSetEntityDataPacket(int var1, SynchedEntityData var2, boolean var3) {
      this.id = var1;
      if (var3) {
         this.packedItems = var2.getAll();
         var2.clearDirty();
      } else {
         this.packedItems = var2.packDirty();
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.id = var1.readVarInt();
      this.packedItems = SynchedEntityData.unpack(var1);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.id);
      SynchedEntityData.pack(this.packedItems, var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetEntityData(this);
   }

   public List getUnpackedData() {
      return this.packedItems;
   }

   public int getId() {
      return this.id;
   }
}
