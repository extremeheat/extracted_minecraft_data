package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundPlayerRotationPacket(float yRot, float xRot) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundPlayerRotationPacket> STREAM_CODEC;

   public ClientboundPlayerRotationPacket(float var1, float var2) {
      super();
      this.yRot = var1;
      this.xRot = var2;
   }

   public PacketType<ClientboundPlayerRotationPacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_ROTATION;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRotatePlayer(this);
   }

   public float yRot() {
      return this.yRot;
   }

   public float xRot() {
      return this.xRot;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, ClientboundPlayerRotationPacket::yRot, ByteBufCodecs.FLOAT, ClientboundPlayerRotationPacket::xRot, ClientboundPlayerRotationPacket::new);
   }
}
