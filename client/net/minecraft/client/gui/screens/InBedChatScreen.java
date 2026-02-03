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

   public InBedChatScreen(final String initial, final boolean isDraft) {
      super(initial, isDraft);
   }

   protected void init() {
      super.init();
      this.leaveBedButton = Button.builder(Component.translatable("multiplayer.stopSleeping"), (button) -> this.sendWakeUp()).bounds(this.width / 2 - 100, this.height - 40, 200, 20).build();
      this.addRenderableWidget(this.leaveBedButton);
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      if (!this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer())) {
         this.leaveBedButton.render(graphics, mouseX, mouseY, a);
      } else {
         super.render(graphics, mouseX, mouseY, a);
      }
   }

   public void onClose() {
      this.sendWakeUp();
   }

   public boolean charTyped(final CharacterEvent event) {
      return !this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer()) ? true : super.charTyped(event);
   }

   public boolean keyPressed(final KeyEvent event) {
      if (event.isEscape()) {
         this.sendWakeUp();
      }

      if (!this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer())) {
         return true;
      } else if (event.isConfirmation()) {
         this.handleChatInput(this.input.getValue(), true);
         this.input.setValue("");
         this.minecraft.gui.getChat().resetChatScroll();
         return true;
      } else {
         return super.keyPressed(event);
      }
   }

   private void sendWakeUp() {
      ClientPacketListener connection = this.minecraft.player.connection;
      connection.send(new ServerboundPlayerCommandPacket(this.minecraft.player, ServerboundPlayerCommandPacket.Action.STOP_SLEEPING));
   }

   public void onPlayerWokeUp() {
      String text = this.input.getValue();
      if (!this.isDraft && !text.isEmpty()) {
         this.exitReason = ChatScreen.ExitReason.DONE;
         this.minecraft.setScreen(new ChatScreen(text, false));
      } else {
         this.exitReason = ChatScreen.ExitReason.INTERRUPTED;
         this.minecraft.setScreen((Screen)null);
      }

   }
}
