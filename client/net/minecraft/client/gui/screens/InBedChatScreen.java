package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

public class InBedChatScreen extends ChatScreen {
   public InBedChatScreen() {
      super("");
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, this.height - 40, 200, 20, I18n.get("multiplayer.stopSleeping"), (var1) -> {
         this.sendWakeUp();
      }));
   }

   public void onClose() {
      this.sendWakeUp();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.sendWakeUp();
      } else if (var1 == 257 || var1 == 335) {
         String var4 = this.input.getValue().trim();
         if (!var4.isEmpty()) {
            this.minecraft.player.chat(var4);
         }

         this.input.setValue("");
         this.minecraft.gui.getChat().resetChatScroll();
         return true;
      }

      return super.keyPressed(var1, var2, var3);
   }

   private void sendWakeUp() {
      ClientPacketListener var1 = this.minecraft.player.connection;
      var1.send((Packet)(new ServerboundPlayerCommandPacket(this.minecraft.player, ServerboundPlayerCommandPacket.Action.STOP_SLEEPING)));
   }
}
