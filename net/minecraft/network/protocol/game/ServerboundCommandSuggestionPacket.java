package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundCommandSuggestionPacket implements Packet {
   private int id;
   private String command;

   public ServerboundCommandSuggestionPacket() {
   }

   public ServerboundCommandSuggestionPacket(int var1, String var2) {
      this.id = var1;
      this.command = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.id = var1.readVarInt();
      this.command = var1.readUtf(32500);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.id);
      var1.writeUtf(this.command, 32500);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleCustomCommandSuggestions(this);
   }

   public int getId() {
      return this.id;
   }

   public String getCommand() {
      return this.command;
   }
}
