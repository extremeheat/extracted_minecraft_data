package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundForgetLevelChunkPacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: x int
   private final int field_260;
   // $FF: renamed from: z int
   private final int field_261;

   public ClientboundForgetLevelChunkPacket(int var1, int var2) {
      super();
      this.field_260 = var1;
      this.field_261 = var2;
   }

   public ClientboundForgetLevelChunkPacket(FriendlyByteBuf var1) {
      super();
      this.field_260 = var1.readInt();
      this.field_261 = var1.readInt();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.field_260);
      var1.writeInt(this.field_261);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleForgetLevelChunk(this);
   }

   public int getX() {
      return this.field_260;
   }

   public int getZ() {
      return this.field_261;
   }
}
