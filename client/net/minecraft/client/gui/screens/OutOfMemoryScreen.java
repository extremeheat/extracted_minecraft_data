package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class OutOfMemoryScreen extends Screen {
   private MultiLineLabel message = MultiLineLabel.EMPTY;

   public OutOfMemoryScreen() {
      super(Component.translatable("outOfMemory.title"));
   }

   @Override
   protected void init() {
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_TO_TITLE, var1 -> this.minecraft.setScreen(new TitleScreen()))
            .bounds(this.width / 2 - 155, this.height / 4 + 120 + 12, 150, 20)
            .build()
      );
      this.addRenderableWidget(
         Button.builder(Component.translatable("menu.quit"), var1 -> this.minecraft.stop())
            .bounds(this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, 150, 20)
            .build()
      );
      this.message = MultiLineLabel.create(this.font, Component.translatable("outOfMemory.message"), 295);
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      var1.drawCenteredString(this.font, this.title, this.width / 2, this.height / 4 - 60 + 20, 16777215);
      this.message.renderLeftAligned(var1, this.width / 2 - 145, this.height / 4, 9, 10526880);
      super.render(var1, var2, var3, var4);
   }
}
