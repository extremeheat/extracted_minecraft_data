package net.minecraft.client.gui.screens;

import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

public class OptionsSubScreen extends Screen {
   protected final Screen lastScreen;
   protected final Options options;

   public OptionsSubScreen(Screen var1, Options var2, Component var3) {
      super(var3);
      this.lastScreen = var1;
      this.options = var2;
   }

   public void removed() {
      this.minecraft.options.save();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }
}
