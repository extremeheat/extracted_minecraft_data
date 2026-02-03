package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundContainerSetDataPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundContainerSetDataPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundContainerSetDataPacket>codec(ClientboundContainerSetDataPacket::write, ClientboundContainerSetDataPacket::new);
   private final int containerId;
   private final int id;
   private final int value;

   public ClientboundContainerSetDataPacket(final int containerId, final int id, final int value) {
      super();
      this.containerId = containerId;
      this.id = id;
      this.value = value;
   }

   private ClientboundContainerSetDataPacket(final FriendlyByteBuf input) {
      super();
      this.containerId = input.readContainerId();
      this.id = input.readShort();
      this.value = input.readShort();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeContainerId(this.containerId);
      output.writeShort(this.id);
      output.writeShort(this.value);
   }

   public PacketType<ClientboundContainerSetDataPacket> type() {
      return GamePacketTypes.CLIENTBOUND_CONTAINER_SET_DATA;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleContainerSetData(this);
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
