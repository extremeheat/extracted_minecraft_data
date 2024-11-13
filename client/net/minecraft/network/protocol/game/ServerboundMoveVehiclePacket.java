package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record ServerboundMoveVehiclePacket(Vec3 position, float yRot, float xRot, boolean onGround) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundMoveVehiclePacket> STREAM_CODEC;

   public ServerboundMoveVehiclePacket(Vec3 var1, float var2, float var3, boolean var4) {
      super();
      this.position = var1;
      this.yRot = var2;
      this.xRot = var3;
      this.onGround = var4;
   }

   public static ServerboundMoveVehiclePacket fromEntity(Entity var0) {
      return new ServerboundMoveVehiclePacket(new Vec3(var0.lerpTargetX(), var0.lerpTargetY(), var0.lerpTargetZ()), var0.getYRot(), var0.getXRot(), var0.onGround());
   }

   public PacketType<ServerboundMoveVehiclePacket> type() {
      return GamePacketTypes.SERVERBOUND_MOVE_VEHICLE;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleMoveVehicle(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(Vec3.STREAM_CODEC, ServerboundMoveVehiclePacket::position, ByteBufCodecs.FLOAT, ServerboundMoveVehiclePacket::yRot, ByteBufCodecs.FLOAT, ServerboundMoveVehiclePacket::xRot, ByteBufCodecs.BOOL, ServerboundMoveVehiclePacket::onGround, ServerboundMoveVehiclePacket::new);
   }
}
