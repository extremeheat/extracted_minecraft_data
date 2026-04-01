package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.phys.BlockHitResult;

public record ServerboundPickItemFromBlockPacket(BlockHitResult hitResult, boolean includeData) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundPickItemFromBlockPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundPickItemFromBlockPacket>codec(ServerboundPickItemFromBlockPacket::write, ServerboundPickItemFromBlockPacket::new);

   private ServerboundPickItemFromBlockPacket(final FriendlyByteBuf input) {
      this(input.readBlockHitResult(), input.readBoolean());
   }

   public ServerboundPickItemFromBlockPacket {
      super();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeBlockHitResult(this.hitResult);
      output.writeBoolean(this.includeData);
   }

   public PacketType<ServerboundPickItemFromBlockPacket> type() {
      return GamePacketTypes.SERVERBOUND_PICK_ITEM_FROM_BLOCK;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handlePickItemFromBlock(this);
   }
}
