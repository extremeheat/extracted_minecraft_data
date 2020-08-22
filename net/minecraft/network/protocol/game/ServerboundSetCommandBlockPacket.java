package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

public class ServerboundSetCommandBlockPacket implements Packet {
   private BlockPos pos;
   private String command;
   private boolean trackOutput;
   private boolean conditional;
   private boolean automatic;
   private CommandBlockEntity.Mode mode;

   public ServerboundSetCommandBlockPacket() {
   }

   public ServerboundSetCommandBlockPacket(BlockPos var1, String var2, CommandBlockEntity.Mode var3, boolean var4, boolean var5, boolean var6) {
      this.pos = var1;
      this.command = var2;
      this.trackOutput = var4;
      this.conditional = var5;
      this.automatic = var6;
      this.mode = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.pos = var1.readBlockPos();
      this.command = var1.readUtf(32767);
      this.mode = (CommandBlockEntity.Mode)var1.readEnum(CommandBlockEntity.Mode.class);
      byte var2 = var1.readByte();
      this.trackOutput = (var2 & 1) != 0;
      this.conditional = (var2 & 2) != 0;
      this.automatic = (var2 & 4) != 0;
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeBlockPos(this.pos);
      var1.writeUtf(this.command);
      var1.writeEnum(this.mode);
      int var2 = 0;
      if (this.trackOutput) {
         var2 |= 1;
      }

      if (this.conditional) {
         var2 |= 2;
      }

      if (this.automatic) {
         var2 |= 4;
      }

      var1.writeByte(var2);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetCommandBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean isTrackOutput() {
      return this.trackOutput;
   }

   public boolean isConditional() {
      return this.conditional;
   }

   public boolean isAutomatic() {
      return this.automatic;
   }

   public CommandBlockEntity.Mode getMode() {
      return this.mode;
   }
}
