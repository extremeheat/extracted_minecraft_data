package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundContainerButtonClickPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundContainerButtonClickPacket> STREAM_CODEC = Packet.codec(
      ServerboundContainerButtonClickPacket::write, ServerboundContainerButtonClickPacket::new
   );
   private final int containerId;
   private final int buttonId;

   public ServerboundContainerButtonClickPacket(int var1, int var2) {
      super();
      this.containerId = var1;
      this.buttonId = var2;
   }

   private ServerboundContainerButtonClickPacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readByte();
      this.buttonId = var1.readByte();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
      var1.writeByte(this.buttonId);
   }

   @Override
   public PacketType<ServerboundContainerButtonClickPacket> type() {
      return GamePacketTypes.SERVERBOUND_CONTAINER_BUTTON_CLICK;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleContainerButtonClick(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getButtonId() {
      return this.buttonId;
   }
}
