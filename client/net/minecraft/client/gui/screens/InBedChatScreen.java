package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

public class InBedChatScreen extends ChatScreen {
   public InBedChatScreen() {
      super("");
   }

   @Override
   protected void init() {
      super.init();
      this.addRenderableWidget(
         new Button(this.width / 2 - 100, this.height - 40, 200, 20, Component.translatable("multiplayer.stopSleeping"), var1 -> this.sendWakeUp())
      );
   }

   @Override
   public void onClose() {
      this.sendWakeUp();
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.sendWakeUp();
      } else if (var1 == 257 || var1 == 335) {
         this.handleChatInput(this.input.getValue(), true);
         this.input.setValue("");
         this.minecraft.gui.getChat().resetChatScroll();
         return true;
      }

      return super.keyPressed(var1, var2, var3);
   }

   private void sendWakeUp() {
      ClientPacketListener var1 = this.minecraft.player.connection;
      var1.send(new ServerboundPlayerCommandPacket(this.minecraft.player, ServerboundPlayerCommandPacket.Action.STOP_SLEEPING));
   }

   public void onPlayerWokeUp() {
      if (this.input.getValue().isEmpty()) {
         this.minecraft.setScreen(null);
      } else {
         this.minecraft.setScreen(new ChatScreen(this.input.getValue()));
      }
   }
}
