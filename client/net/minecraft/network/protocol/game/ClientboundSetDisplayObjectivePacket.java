package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.scores.Objective;

public class ClientboundSetDisplayObjectivePacket implements Packet<ClientGamePacketListener> {
   private int slot;
   private String objectiveName;

   public ClientboundSetDisplayObjectivePacket() {
      super();
   }

   public ClientboundSetDisplayObjectivePacket(int var1, @Nullable Objective var2) {
      super();
      this.slot = var1;
      if (var2 == null) {
         this.objectiveName = "";
      } else {
         this.objectiveName = var2.getName();
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.slot = var1.readByte();
      this.objectiveName = var1.readUtf(16);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.slot);
      var1.writeUtf(this.objectiveName);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetDisplayObjective(this);
   }

   public int getSlot() {
      return this.slot;
   }

   @Nullable
   public String getObjectiveName() {
      return Objects.equals(this.objectiveName, "") ? null : this.objectiveName;
   }
}
