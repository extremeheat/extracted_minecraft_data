package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record ClientboundMoveVehiclePacket(Vec3 position, float yRot, float xRot) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundMoveVehiclePacket> STREAM_CODEC;

   public ClientboundMoveVehiclePacket(Vec3 var1, float var2, float var3) {
      super();
      this.position = var1;
      this.yRot = var2;
      this.xRot = var3;
   }

   public static ClientboundMoveVehiclePacket fromEntity(Entity var0) {
      return new ClientboundMoveVehiclePacket(var0.position(), var0.getYRot(), var0.getXRot());
   }

   public PacketType<ClientboundMoveVehiclePacket> type() {
      return GamePacketTypes.CLIENTBOUND_MOVE_VEHICLE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleMoveVehicle(this);
   }

   public Vec3 position() {
      return this.position;
   }

   public float yRot() {
      return this.yRot;
   }

   public float xRot() {
      return this.xRot;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(Vec3.STREAM_CODEC, ClientboundMoveVehiclePacket::position, ByteBufCodecs.FLOAT, ClientboundMoveVehiclePacket::yRot, ByteBufCodecs.FLOAT, ClientboundMoveVehiclePacket::xRot, ClientboundMoveVehiclePacket::new);
   }
}
