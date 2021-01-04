package net.minecraft.client.gui.components;

import net.minecraft.client.Option;

public class OptionButton extends Button {
   private final Option option;

   public OptionButton(int var1, int var2, int var3, int var4, Option var5, String var6, Button.OnPress var7) {
      super(var1, var2, var3, var4, var6, var7);
      this.option = var5;
   }
}
