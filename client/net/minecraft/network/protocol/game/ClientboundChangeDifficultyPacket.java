package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.Difficulty;

public class ClientboundChangeDifficultyPacket implements Packet<ClientGamePacketListener> {
   private Difficulty difficulty;
   private boolean locked;

   public ClientboundChangeDifficultyPacket() {
      super();
   }

   public ClientboundChangeDifficultyPacket(Difficulty var1, boolean var2) {
      super();
      this.difficulty = var1;
      this.locked = var2;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleChangeDifficulty(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.difficulty = Difficulty.byId(var1.readUnsignedByte());
      this.locked = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.difficulty.getId());
      var1.writeBoolean(this.locked);
   }

   public boolean isLocked() {
      return this.locked;
   }

   public Difficulty getDifficulty() {
      return this.difficulty;
   }
}
