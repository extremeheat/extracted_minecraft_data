package net.minecraft.network.protocol.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.syncher.SynchedEntityData;

public record ClientboundSetEntityDataPacket(int id, List<SynchedEntityData.DataValue<?>> packedItems) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetEntityDataPacket> STREAM_CODEC = Packet.codec(
      ClientboundSetEntityDataPacket::write, ClientboundSetEntityDataPacket::new
   );
   public static final int EOF_MARKER = 255;

   private ClientboundSetEntityDataPacket(RegistryFriendlyByteBuf var1) {
      this(var1.readVarInt(), unpack(var1));
   }

   public ClientboundSetEntityDataPacket(int id, List<SynchedEntityData.DataValue<?>> packedItems) {
      super();
      this.id = id;
      this.packedItems = packedItems;
   }

   private static void pack(List<SynchedEntityData.DataValue<?>> var0, RegistryFriendlyByteBuf var1) {
      for (SynchedEntityData.DataValue var3 : var0) {
         var3.write(var1);
      }

      var1.writeByte(255);
   }

   private static List<SynchedEntityData.DataValue<?>> unpack(RegistryFriendlyByteBuf var0) {
      ArrayList var1 = new ArrayList();

      short var2;
      while ((var2 = var0.readUnsignedByte()) != 255) {
         var1.add(SynchedEntityData.DataValue.read(var0, var2));
      }

      return var1;
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      pack(this.packedItems, var1);
   }

   @Override
   public PacketType<ClientboundSetEntityDataPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_ENTITY_DATA;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetEntityData(this);
   }
}
