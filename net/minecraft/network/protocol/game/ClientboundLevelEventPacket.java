package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundLevelEventPacket implements Packet {
   private int type;
   private BlockPos pos;
   private int data;
   private boolean globalEvent;

   public ClientboundLevelEventPacket() {
   }

   public ClientboundLevelEventPacket(int var1, BlockPos var2, int var3, boolean var4) {
      this.type = var1;
      this.pos = var2.immutable();
      this.data = var3;
      this.globalEvent = var4;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.type = var1.readInt();
      this.pos = var1.readBlockPos();
      this.data = var1.readInt();
      this.globalEvent = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeInt(this.type);
      var1.writeBlockPos(this.pos);
      var1.writeInt(this.data);
      var1.writeBoolean(this.globalEvent);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLevelEvent(this);
   }

   public boolean isGlobalEvent() {
      return this.globalEvent;
   }

   public int getType() {
      return this.type;
   }

   public int getData() {
      return this.data;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
