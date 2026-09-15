package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.InteractionHand;

public record ServerboundUseItemPacket(InteractionHand hand, int sequence, float yRot, float xRot) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundUseItemPacket> STREAM_CODEC;

   public ServerboundUseItemPacket {
      super();
   }

   public PacketType<ServerboundUseItemPacket> type() {
      return GamePacketTypes.SERVERBOUND_USE_ITEM;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleUseItem(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(InteractionHand.STREAM_CODEC, ServerboundUseItemPacket::hand, ByteBufCodecs.VAR_INT, ServerboundUseItemPacket::sequence, ByteBufCodecs.FLOAT, ServerboundUseItemPacket::yRot, ByteBufCodecs.FLOAT, ServerboundUseItemPacket::xRot, ServerboundUseItemPacket::new);
   }
}
