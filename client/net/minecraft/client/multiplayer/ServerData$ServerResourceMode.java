package net.minecraft.client.multiplayer;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public enum ServerData$ServerResourceMode {
   ENABLED("enabled"),
   DISABLED("disabled"),
   PROMPT("prompt");

   private final IChatComponent field_152594_d;

   private ServerData$ServerResourceMode(String var3) {
      this.field_152594_d = new ChatComponentTranslation("addServer.resourcePack." + var3);
   }

   public IChatComponent func_152589_a() {
      return this.field_152594_d;
   }
}
