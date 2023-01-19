package net.minecraft.network.protocol.game;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;

public class ClientboundSetEntityDataPacket implements Packet<ClientGamePacketListener> {
   private final int id;
   @Nullable
   private final List<SynchedEntityData.DataItem<?>> packedItems;

   public ClientboundSetEntityDataPacket(int var1, SynchedEntityData var2, boolean var3) {
      super();
      this.id = var1;
      if (var3) {
         this.packedItems = var2.getAll();
         var2.clearDirty();
      } else {
         this.packedItems = var2.packDirty();
      }
   }

   public ClientboundSetEntityDataPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.packedItems = SynchedEntityData.unpack(var1);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      SynchedEntityData.pack(this.packedItems, var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetEntityData(this);
   }

   @Nullable
   public List<SynchedEntityData.DataItem<?>> getUnpackedData() {
      return this.packedItems;
   }

   public int getId() {
      return this.id;
   }
}
