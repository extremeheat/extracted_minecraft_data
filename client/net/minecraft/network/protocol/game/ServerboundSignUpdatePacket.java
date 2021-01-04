package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ServerboundSignUpdatePacket implements Packet<ServerGamePacketListener> {
   private BlockPos pos;
   private String[] lines;

   public ServerboundSignUpdatePacket() {
      super();
   }

   public ServerboundSignUpdatePacket(BlockPos var1, Component var2, Component var3, Component var4, Component var5) {
      super();
      this.pos = var1;
      this.lines = new String[]{var2.getString(), var3.getString(), var4.getString(), var5.getString()};
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.pos = var1.readBlockPos();
      this.lines = new String[4];

      for(int var2 = 0; var2 < 4; ++var2) {
         this.lines[var2] = var1.readUtf(384);
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeBlockPos(this.pos);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.writeUtf(this.lines[var2]);
      }

   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSignUpdate(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public String[] getLines() {
      return this.lines;
   }
}
