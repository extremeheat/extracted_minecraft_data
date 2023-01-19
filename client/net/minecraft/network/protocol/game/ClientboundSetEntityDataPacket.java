package net.minecraft.network.protocol.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;

public record ClientboundSetEntityDataPacket(int b, List<SynchedEntityData.DataValue<?>> c) implements Packet<ClientGamePacketListener> {
   private final int id;
   private final List<SynchedEntityData.DataValue<?>> packedItems;
   public static final int EOF_MARKER = 255;

   public ClientboundSetEntityDataPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt(), unpack(var1));
   }

   public ClientboundSetEntityDataPacket(int var1, List<SynchedEntityData.DataValue<?>> var2) {
      super();
      this.id = var1;
      this.packedItems = var2;
   }

   private static void pack(List<SynchedEntityData.DataValue<?>> var0, FriendlyByteBuf var1) {
      for(SynchedEntityData.DataValue var3 : var0) {
         var3.write(var1);
      }

      var1.writeByte(255);
   }

   private static List<SynchedEntityData.DataValue<?>> unpack(FriendlyByteBuf var0) {
      ArrayList var1 = new ArrayList();

      short var2;
      while((var2 = var0.readUnsignedByte()) != true) {
         var1.add(SynchedEntityData.DataValue.read(var0, var2));
      }

      return var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      pack(this.packedItems, var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetEntityData(this);
   }
}
