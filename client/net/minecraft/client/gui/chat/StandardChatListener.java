package net.minecraft.client.gui.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

public class StandardChatListener implements ChatListener {
   private final Minecraft minecraft;

   public StandardChatListener(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void handle(ChatType var1, Component var2) {
      this.minecraft.gui.getChat().addMessage(var2);
   }
}
