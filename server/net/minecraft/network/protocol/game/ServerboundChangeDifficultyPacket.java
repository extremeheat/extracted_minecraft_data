package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.Difficulty;

public class ServerboundChangeDifficultyPacket implements Packet<ServerGamePacketListener> {
   private Difficulty difficulty;

   public ServerboundChangeDifficultyPacket() {
      super();
   }

   public ServerboundChangeDifficultyPacket(Difficulty var1) {
      super();
      this.difficulty = var1;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChangeDifficulty(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.difficulty = Difficulty.byId(var1.readUnsignedByte());
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.difficulty.getId());
   }

   public Difficulty getDifficulty() {
      return this.difficulty;
   }
}
