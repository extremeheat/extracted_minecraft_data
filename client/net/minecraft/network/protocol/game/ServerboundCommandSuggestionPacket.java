package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundCommandSuggestionPacket implements Packet<ServerGamePacketListener> {
   // $FF: renamed from: id int
   private final int field_218;
   private final String command;

   public ServerboundCommandSuggestionPacket(int var1, String var2) {
      super();
      this.field_218 = var1;
      this.command = var2;
   }

   public ServerboundCommandSuggestionPacket(FriendlyByteBuf var1) {
      super();
      this.field_218 = var1.readVarInt();
      this.command = var1.readUtf(32500);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.field_218);
      var1.writeUtf(this.command, 32500);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleCustomCommandSuggestions(this);
   }

   public int getId() {
      return this.field_218;
   }

   public String getCommand() {
      return this.command;
   }
}
