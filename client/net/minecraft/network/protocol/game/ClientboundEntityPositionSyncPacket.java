package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PositionMoveRotation;

public record ClientboundEntityPositionSyncPacket(int id, PositionMoveRotation values, boolean onGround) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundEntityPositionSyncPacket> STREAM_CODEC;

   public ClientboundEntityPositionSyncPacket(int var1, PositionMoveRotation var2, boolean var3) {
      super();
      this.id = var1;
      this.values = var2;
      this.onGround = var3;
   }

   public static ClientboundEntityPositionSyncPacket of(Entity var0) {
      return new ClientboundEntityPositionSyncPacket(var0.getId(), new PositionMoveRotation(var0.trackingPosition(), var0.getDeltaMovement(), var0.getYRot(), var0.getXRot()), var0.onGround());
   }

   public PacketType<ClientboundEntityPositionSyncPacket> type() {
      return GamePacketTypes.CLIENTBOUND_ENTITY_POSITION_SYNC;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleEntityPositionSync(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ClientboundEntityPositionSyncPacket::id, PositionMoveRotation.STREAM_CODEC, ClientboundEntityPositionSyncPacket::values, ByteBufCodecs.BOOL, ClientboundEntityPositionSyncPacket::onGround, ClientboundEntityPositionSyncPacket::new);
   }
}
