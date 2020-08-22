package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundLockDifficultyPacket implements Packet {
   private boolean locked;

   public ServerboundLockDifficultyPacket() {
   }

   public ServerboundLockDifficultyPacket(boolean var1) {
      this.locked = var1;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleLockDifficulty(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.locked = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeBoolean(this.locked);
   }

   public boolean isLocked() {
      return this.locked;
   }
}
