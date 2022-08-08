package net.minecraft.client.gui.components;

import net.minecraft.client.Options;
import net.minecraft.network.chat.CommonComponents;

public abstract class AbstractOptionSliderButton extends AbstractSliderButton {
   protected final Options options;

   protected AbstractOptionSliderButton(Options var1, int var2, int var3, int var4, int var5, double var6) {
      super(var2, var3, var4, var5, CommonComponents.EMPTY, var6);
      this.options = var1;
   }
}
