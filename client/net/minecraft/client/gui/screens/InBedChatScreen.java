package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

public class InBedChatScreen extends ChatScreen {
   private Button leaveBedButton;

   public InBedChatScreen(String var1, boolean var2) {
      super(var1, var2);
   }

   protected void init() {
      super.init();
      this.leaveBedButton = Button.builder(Component.translatable("multiplayer.stopSleeping"), (var1) -> this.sendWakeUp()).bounds(this.width / 2 - 100, this.height - 40, 200, 20).build();
      this.addRenderableWidget(this.leaveBedButton);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      if (!this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer())) {
         this.leaveBedButton.render(var1, var2, var3, var4);
      } else {
         super.render(var1, var2, var3, var4);
      }
   }

   public void onClose() {
      this.sendWakeUp();
   }

   public boolean charTyped(CharacterEvent var1) {
      return !this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer()) ? true : super.charTyped(var1);
   }

   public boolean keyPressed(KeyEvent var1) {
      if (var1.isEscape()) {
         this.sendWakeUp();
      }

      if (!this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer())) {
         return true;
      } else if (var1.isConfirmation()) {
         this.handleChatInput(this.input.getValue(), true);
         this.input.setValue("");
         this.minecraft.gui.getChat().resetChatScroll();
         return true;
      } else {
         return super.keyPressed(var1);
      }
   }

   private void sendWakeUp() {
      ClientPacketListener var1 = this.minecraft.player.connection;
      var1.send(new ServerboundPlayerCommandPacket(this.minecraft.player, ServerboundPlayerCommandPacket.Action.STOP_SLEEPING));
   }

   public void onPlayerWokeUp() {
      String var1 = this.input.getValue();
      if (!this.isDraft && !var1.isEmpty()) {
         this.exitReason = ChatScreen.ExitReason.DONE;
         this.minecraft.setScreen(new ChatScreen(var1, false));
      } else {
         this.exitReason = ChatScreen.ExitReason.INTERRUPTED;
         this.minecraft.setScreen((Screen)null);
      }

   }
}
