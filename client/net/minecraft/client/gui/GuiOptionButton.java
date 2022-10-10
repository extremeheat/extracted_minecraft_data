package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;

public abstract class GuiOptionButton extends GuiButton {
   @Nullable
   private final GameSettings.Options field_146137_o;

   public GuiOptionButton(int var1, int var2, int var3, String var4) {
      this(var1, var2, var3, (GameSettings.Options)null, var4);
   }

   public GuiOptionButton(int var1, int var2, int var3, @Nullable GameSettings.Options var4, String var5) {
      this(var1, var2, var3, 150, 20, var4, var5);
   }

   public GuiOptionButton(int var1, int var2, int var3, int var4, int var5, @Nullable GameSettings.Options var6, String var7) {
      super(var1, var2, var3, var4, var5, var7);
      this.field_146137_o = var6;
   }

   @Nullable
   public GameSettings.Options func_146136_c() {
      return this.field_146137_o;
   }
}
