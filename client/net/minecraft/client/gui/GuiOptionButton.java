package net.minecraft.client.gui;

import net.minecraft.client.settings.GameSettings;

public class GuiOptionButton extends GuiButton {
   private final GameSettings.Options field_146137_o;

   public GuiOptionButton(int var1, int var2, int var3, String var4) {
      this(var1, var2, var3, (GameSettings.Options)null, var4);
   }

   public GuiOptionButton(int var1, int var2, int var3, int var4, int var5, String var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.field_146137_o = null;
   }

   public GuiOptionButton(int var1, int var2, int var3, GameSettings.Options var4, String var5) {
      super(var1, var2, var3, 150, 20, var5);
      this.field_146137_o = var4;
   }

   public GameSettings.Options func_146136_c() {
      return this.field_146137_o;
   }
}
