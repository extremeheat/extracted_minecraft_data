package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundRemoveEntitiesPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundRemoveEntitiesPacket> STREAM_CODEC = Packet.codec(ClientboundRemoveEntitiesPacket::write, ClientboundRemoveEntitiesPacket::new);
   private final IntList entityIds;

   public ClientboundRemoveEntitiesPacket(IntList var1) {
      super();
      this.entityIds = new IntArrayList(var1);
   }

   public ClientboundRemoveEntitiesPacket(int... var1) {
      super();
      this.entityIds = new IntArrayList(var1);
   }

   private ClientboundRemoveEntitiesPacket(FriendlyByteBuf var1) {
      super();
      this.entityIds = var1.readIntIdList();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeIntIdList(this.entityIds);
   }

   public PacketType<ClientboundRemoveEntitiesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_REMOVE_ENTITIES;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRemoveEntities(this);
   }

   public IntList getEntityIds() {
      return this.entityIds;
   }
}
