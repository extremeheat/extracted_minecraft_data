package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundHorseScreenOpenPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundHorseScreenOpenPacket> STREAM_CODEC = Packet.codec(ClientboundHorseScreenOpenPacket::write, ClientboundHorseScreenOpenPacket::new);
   private final int containerId;
   private final int inventoryColumns;
   private final int entityId;

   public ClientboundHorseScreenOpenPacket(int var1, int var2, int var3) {
      super();
      this.containerId = var1;
      this.inventoryColumns = var2;
      this.entityId = var3;
   }

   private ClientboundHorseScreenOpenPacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readUnsignedByte();
      this.inventoryColumns = var1.readVarInt();
      this.entityId = var1.readInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
      var1.writeVarInt(this.inventoryColumns);
      var1.writeInt(this.entityId);
   }

   public PacketType<ClientboundHorseScreenOpenPacket> type() {
      return GamePacketTypes.CLIENTBOUND_HORSE_SCREEN_OPEN;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleHorseScreenOpen(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getInventoryColumns() {
      return this.inventoryColumns;
   }

   public int getEntityId() {
      return this.entityId;
   }
}
