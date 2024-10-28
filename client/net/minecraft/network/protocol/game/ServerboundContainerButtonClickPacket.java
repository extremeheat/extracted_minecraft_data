package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundContainerButtonClickPacket(int containerId, int buttonId) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundContainerButtonClickPacket> STREAM_CODEC;

   public ServerboundContainerButtonClickPacket(int containerId, int buttonId) {
      super();
      this.containerId = containerId;
      this.buttonId = buttonId;
   }

   public PacketType<ServerboundContainerButtonClickPacket> type() {
      return GamePacketTypes.SERVERBOUND_CONTAINER_BUTTON_CLICK;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleContainerButtonClick(this);
   }

   public int containerId() {
      return this.containerId;
   }

   public int buttonId() {
      return this.buttonId;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ServerboundContainerButtonClickPacket::containerId, ByteBufCodecs.VAR_INT, ServerboundContainerButtonClickPacket::buttonId, ServerboundContainerButtonClickPacket::new);
   }
}
