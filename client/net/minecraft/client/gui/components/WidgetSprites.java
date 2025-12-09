package net.minecraft.client.gui.components;

import net.minecraft.resources.Identifier;

public record WidgetSprites(Identifier enabled, Identifier disabled, Identifier enabledFocused, Identifier disabledFocused) {
   public WidgetSprites(Identifier var1) {
      this(var1, var1, var1, var1);
   }

   public WidgetSprites(Identifier var1, Identifier var2) {
      this(var1, var1, var2, var2);
   }

   public WidgetSprites(Identifier var1, Identifier var2, Identifier var3) {
      this(var1, var2, var3, var2);
   }

   public WidgetSprites(Identifier var1, Identifier var2, Identifier var3, Identifier var4) {
      super();
      this.enabled = var1;
      this.disabled = var2;
      this.enabledFocused = var3;
      this.disabledFocused = var4;
   }

   public Identifier get(boolean var1, boolean var2) {
      if (var1) {
         return var2 ? this.enabledFocused : this.enabled;
      } else {
         return var2 ? this.disabledFocused : this.disabled;
      }
   }
}
