package net.minecraft.client.gui.screens;

import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.network.chat.Component;

public class OptionsSubScreen extends Screen {
   protected final Screen lastScreen;
   protected final Options options;

   public OptionsSubScreen(Screen var1, Options var2, Component var3) {
      super(var3);
      this.lastScreen = var1;
      this.options = var2;
   }

   @Override
   public void removed() {
      this.minecraft.options.save();
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   protected void basicListRender(GuiGraphics var1, OptionsList var2, int var3, int var4, float var5) {
      super.render(var1, var3, var4, var5);
      var2.render(var1, var3, var4, var5);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
   }
}
