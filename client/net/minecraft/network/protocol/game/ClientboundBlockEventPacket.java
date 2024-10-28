package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.block.Block;

public class ClientboundBlockEventPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBlockEventPacket> STREAM_CODEC = Packet.codec(ClientboundBlockEventPacket::write, ClientboundBlockEventPacket::new);
   private final BlockPos pos;
   private final int b0;
   private final int b1;
   private final Block block;

   public ClientboundBlockEventPacket(BlockPos var1, Block var2, int var3, int var4) {
      super();
      this.pos = var1;
      this.block = var2;
      this.b0 = var3;
      this.b1 = var4;
   }

   private ClientboundBlockEventPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.pos = var1.readBlockPos();
      this.b0 = var1.readUnsignedByte();
      this.b1 = var1.readUnsignedByte();
      this.block = (Block)ByteBufCodecs.registry(Registries.BLOCK).decode(var1);
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeByte(this.b0);
      var1.writeByte(this.b1);
      ByteBufCodecs.registry(Registries.BLOCK).encode(var1, this.block);
   }

   public PacketType<ClientboundBlockEventPacket> type() {
      return GamePacketTypes.CLIENTBOUND_BLOCK_EVENT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBlockEvent(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getB0() {
      return this.b0;
   }

   public int getB1() {
      return this.b1;
   }

   public Block getBlock() {
      return this.block;
   }
}
