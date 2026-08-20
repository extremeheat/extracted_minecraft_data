package net.minecraft.network.protocol.game;

import net.minecraft.core.PositionAndRotation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;

public record ServerboundMoveVehiclePacket(PositionAndRotation movingTo, boolean onGround) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundMoveVehiclePacket> STREAM_CODEC;

   public ServerboundMoveVehiclePacket {
      super();
   }

   public static ServerboundMoveVehiclePacket fromEntity(final Entity entity) {
      return new ServerboundMoveVehiclePacket(entity.getClientPositionAndRotation(), entity.onGround());
   }

   public PacketType<ServerboundMoveVehiclePacket> type() {
      return GamePacketTypes.SERVERBOUND_MOVE_VEHICLE;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleMoveVehicle(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(PositionAndRotation.STREAM_CODEC, ServerboundMoveVehiclePacket::movingTo, ByteBufCodecs.BOOL, ServerboundMoveVehiclePacket::onGround, ServerboundMoveVehiclePacket::new);
   }
}
