package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundJigsawGeneratePacket implements Packet<ServerGamePacketListener> {
   private final BlockPos pos;
   private final int levels;
   private final boolean keepJigsaws;

   public ServerboundJigsawGeneratePacket(BlockPos var1, int var2, boolean var3) {
      super();
      this.pos = var1;
      this.levels = var2;
      this.keepJigsaws = var3;
   }

   public ServerboundJigsawGeneratePacket(FriendlyByteBuf var1) {
      super();
      this.pos = var1.readBlockPos();
      this.levels = var1.readVarInt();
      this.keepJigsaws = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeVarInt(this.levels);
      var1.writeBoolean(this.keepJigsaws);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleJigsawGenerate(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int levels() {
      return this.levels;
   }

   public boolean keepJigsaws() {
      return this.keepJigsaws;
   }
}
