package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundBlockDestructionPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundBlockDestructionPacket> STREAM_CODEC = Packet.codec(ClientboundBlockDestructionPacket::write, ClientboundBlockDestructionPacket::new);
   private final int id;
   private final BlockPos pos;
   private final int progress;

   public ClientboundBlockDestructionPacket(int var1, BlockPos var2, int var3) {
      super();
      this.id = var1;
      this.pos = var2;
      this.progress = var3;
   }

   private ClientboundBlockDestructionPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.pos = var1.readBlockPos();
      this.progress = var1.readUnsignedByte();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeBlockPos(this.pos);
      var1.writeByte(this.progress);
   }

   public PacketType<ClientboundBlockDestructionPacket> type() {
      return GamePacketTypes.CLIENTBOUND_BLOCK_DESTRUCTION;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBlockDestruction(this);
   }

   public int getId() {
      return this.id;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getProgress() {
      return this.progress;
   }
}
