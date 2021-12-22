package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.Difficulty;

public class ServerboundChangeDifficultyPacket implements Packet<ServerGamePacketListener> {
   private final Difficulty difficulty;

   public ServerboundChangeDifficultyPacket(Difficulty var1) {
      super();
      this.difficulty = var1;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChangeDifficulty(this);
   }

   public ServerboundChangeDifficultyPacket(FriendlyByteBuf var1) {
      super();
      this.difficulty = Difficulty.byId(var1.readUnsignedByte());
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeByte(this.difficulty.getId());
   }

   public Difficulty getDifficulty() {
      return this.difficulty;
   }
}
