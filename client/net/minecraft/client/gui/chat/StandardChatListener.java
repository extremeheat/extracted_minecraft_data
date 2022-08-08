package net.minecraft.client.gui.chat;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

public class StandardChatListener implements ChatListener {
   private final Minecraft minecraft;

   public StandardChatListener(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void handle(ChatType var1, Component var2, @Nullable ChatSender var3) {
      var1.chat().ifPresent((var3x) -> {
         Component var4 = var3x.decorate(var2, var3);
         if (var3 == null) {
            this.minecraft.gui.getChat().addMessage(var4);
         } else {
            this.minecraft.gui.getChat().enqueueMessage(var4);
         }

      });
   }
}
