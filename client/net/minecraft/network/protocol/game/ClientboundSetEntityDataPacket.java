package net.minecraft.network.protocol.game;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;

public class ClientboundSetEntityDataPacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: id int
   private final int field_233;
   @Nullable
   private final List<SynchedEntityData.DataItem<?>> packedItems;

   public ClientboundSetEntityDataPacket(int var1, SynchedEntityData var2, boolean var3) {
      super();
      this.field_233 = var1;
      if (var3) {
         this.packedItems = var2.getAll();
         var2.clearDirty();
      } else {
         this.packedItems = var2.packDirty();
      }

   }

   public ClientboundSetEntityDataPacket(FriendlyByteBuf var1) {
      super();
      this.field_233 = var1.readVarInt();
      this.packedItems = SynchedEntityData.unpack(var1);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.field_233);
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
      return this.field_233;
   }
}
