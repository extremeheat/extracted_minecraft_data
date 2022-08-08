package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundLockDifficultyPacket implements Packet<ServerGamePacketListener> {
   private final boolean locked;

   public ServerboundLockDifficultyPacket(boolean var1) {
      super();
      this.locked = var1;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleLockDifficulty(this);
   }

   public ServerboundLockDifficultyPacket(FriendlyByteBuf var1) {
      super();
      this.locked = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeBoolean(this.locked);
   }

   public boolean isLocked() {
      return this.locked;
   }
}
