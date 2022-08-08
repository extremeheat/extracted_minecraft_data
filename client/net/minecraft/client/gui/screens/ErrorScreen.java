package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ErrorScreen extends Screen {
   private final Component message;

   public ErrorScreen(Component var1, Component var2) {
      super(var1);
      this.message = var2;
   }

   protected void init() {
      super.init();
      this.addRenderableWidget(new Button(this.width / 2 - 100, 140, 200, 20, CommonComponents.GUI_CANCEL, (var1) -> {
         this.minecraft.setScreen((Screen)null);
      }));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.fillGradient(var1, 0, 0, this.width, this.height, -12574688, -11530224);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 90, 16777215);
      drawCenteredString(var1, this.font, this.message, this.width / 2, 110, 16777215);
      super.render(var1, var2, var3, var4);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
