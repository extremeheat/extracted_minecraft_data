package net.minecraft.client.gui.chat;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

public class OverlayChatListener implements ChatListener {
   private final Minecraft minecraft;

   public OverlayChatListener(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   @Override
   public void handle(ChatType var1, Component var2, @Nullable ChatSender var3) {
      var1.overlay().ifPresent(var3x -> {
         Component var4 = var3x.decorate(var2, var3);
         this.minecraft.gui.setOverlayMessage(var4, false);
      });
   }
}
