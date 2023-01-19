package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;

public class OutOfMemoryScreen extends Screen {
   private MultiLineLabel message = MultiLineLabel.EMPTY;

   public OutOfMemoryScreen() {
      super(Component.translatable("outOfMemory.error"));
   }

   @Override
   protected void init() {
      this.addRenderableWidget(
         new Button(
            this.width / 2 - 155,
            this.height / 4 + 120 + 12,
            150,
            20,
            Component.translatable("gui.toTitle"),
            var1 -> this.minecraft.setScreen(new TitleScreen())
         )
      );
      this.addRenderableWidget(
         new Button(this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, 150, 20, Component.translatable("menu.quit"), var1 -> this.minecraft.stop())
      );
      this.message = MultiLineLabel.create(this.font, Component.translatable("outOfMemory.message"), 295);
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, this.height / 4 - 60 + 20, 16777215);
      this.message.renderLeftAligned(var1, this.width / 2 - 145, this.height / 4, 9, 10526880);
      super.render(var1, var2, var3, var4);
   }
}
