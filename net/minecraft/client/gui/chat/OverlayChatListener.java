package net.minecraft.client.gui.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

public class OverlayChatListener implements ChatListener {
   private final Minecraft minecraft;

   public OverlayChatListener(Minecraft var1) {
      this.minecraft = var1;
   }

   public void handle(ChatType var1, Component var2) {
      this.minecraft.gui.setOverlayMessage(var2, false);
   }
}
