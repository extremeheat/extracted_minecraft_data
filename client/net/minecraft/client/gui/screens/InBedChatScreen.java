package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

public class InBedChatScreen extends ChatScreen {
   private Button leaveBedButton;

   public InBedChatScreen() {
      super("");
   }

   protected void init() {
      super.init();
      this.leaveBedButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 40, 200, 20, Component.translatable("multiplayer.stopSleeping"), (var1) -> {
         this.sendWakeUp();
      }));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.leaveBedButton.visible = this.getDisplayedPreviewText() == null;
      super.render(var1, var2, var3, var4);
   }

   public void onClose() {
      this.sendWakeUp();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.sendWakeUp();
      } else if (var1 == 257 || var1 == 335) {
         if (this.handleChatInput(this.input.getValue(), true)) {
            this.minecraft.setScreen((Screen)null);
            this.input.setValue("");
            this.minecraft.gui.getChat().resetChatScroll();
         }

         return true;
      }

      return super.keyPressed(var1, var2, var3);
   }

   private void sendWakeUp() {
      ClientPacketListener var1 = this.minecraft.player.connection;
      var1.send((Packet)(new ServerboundPlayerCommandPacket(this.minecraft.player, ServerboundPlayerCommandPacket.Action.STOP_SLEEPING)));
   }

   public void onPlayerWokeUp() {
      if (this.input.getValue().isEmpty()) {
         this.minecraft.setScreen((Screen)null);
      } else {
         this.minecraft.setScreen(new ChatScreen(this.input.getValue()));
      }

   }
}
