package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundMountScreenOpenPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundMountScreenOpenPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundMountScreenOpenPacket>codec(ClientboundMountScreenOpenPacket::write, ClientboundMountScreenOpenPacket::new);
   private final int containerId;
   private final int inventoryColumns;
   private final int entityId;

   public ClientboundMountScreenOpenPacket(int var1, int var2, int var3) {
      super();
      this.containerId = var1;
      this.inventoryColumns = var2;
      this.entityId = var3;
   }

   private ClientboundMountScreenOpenPacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readContainerId();
      this.inventoryColumns = var1.readVarInt();
      this.entityId = var1.readInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeContainerId(this.containerId);
      var1.writeVarInt(this.inventoryColumns);
      var1.writeInt(this.entityId);
   }

   public PacketType<ClientboundMountScreenOpenPacket> type() {
      return GamePacketTypes.CLIENTBOUND_MOUNT_SCREEN_OPEN;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleMountScreenOpen(this);
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
