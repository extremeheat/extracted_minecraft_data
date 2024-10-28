package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundBlockEntityTagQueryPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundBlockEntityTagQueryPacket> STREAM_CODEC = Packet.codec(ServerboundBlockEntityTagQueryPacket::write, ServerboundBlockEntityTagQueryPacket::new);
   private final int transactionId;
   private final BlockPos pos;

   public ServerboundBlockEntityTagQueryPacket(int var1, BlockPos var2) {
      super();
      this.transactionId = var1;
      this.pos = var2;
   }

   private ServerboundBlockEntityTagQueryPacket(FriendlyByteBuf var1) {
      super();
      this.transactionId = var1.readVarInt();
      this.pos = var1.readBlockPos();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.transactionId);
      var1.writeBlockPos(this.pos);
   }

   public PacketType<ServerboundBlockEntityTagQueryPacket> type() {
      return GamePacketTypes.SERVERBOUND_BLOCK_ENTITY_TAG_QUERY;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleBlockEntityTagQuery(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
