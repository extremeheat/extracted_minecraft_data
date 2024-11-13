package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundPickItemFromEntityPacket(int id, boolean includeData) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<ByteBuf, ServerboundPickItemFromEntityPacket> STREAM_CODEC;

   public ServerboundPickItemFromEntityPacket(int var1, boolean var2) {
      super();
      this.id = var1;
      this.includeData = var2;
   }

   public PacketType<ServerboundPickItemFromEntityPacket> type() {
      return GamePacketTypes.SERVERBOUND_PICK_ITEM_FROM_ENTITY;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePickItemFromEntity(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ServerboundPickItemFromEntityPacket::id, ByteBufCodecs.BOOL, ServerboundPickItemFromEntityPacket::includeData, ServerboundPickItemFromEntityPacket::new);
   }
}
