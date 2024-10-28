package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundContainerSetDataPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundContainerSetDataPacket> STREAM_CODEC = Packet.codec(ClientboundContainerSetDataPacket::write, ClientboundContainerSetDataPacket::new);
   private final int containerId;
   private final int id;
   private final int value;

   public ClientboundContainerSetDataPacket(int var1, int var2, int var3) {
      super();
      this.containerId = var1;
      this.id = var2;
      this.value = var3;
   }

   private ClientboundContainerSetDataPacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readUnsignedByte();
      this.id = var1.readShort();
      this.value = var1.readShort();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
      var1.writeShort(this.id);
      var1.writeShort(this.value);
   }

   public PacketType<ClientboundContainerSetDataPacket> type() {
      return GamePacketTypes.CLIENTBOUND_CONTAINER_SET_DATA;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleContainerSetData(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getId() {
      return this.id;
   }

   public int getValue() {
      return this.value;
   }
}
