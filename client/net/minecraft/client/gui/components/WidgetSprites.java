package net.minecraft.client.gui.components;

import net.minecraft.resources.ResourceLocation;

public record WidgetSprites(ResourceLocation enabled, ResourceLocation disabled, ResourceLocation enabledFocused, ResourceLocation disabledFocused) {
   public WidgetSprites(ResourceLocation var1, ResourceLocation var2) {
      this(var1, var1, var2, var2);
   }

   public WidgetSprites(ResourceLocation var1, ResourceLocation var2, ResourceLocation var3) {
      this(var1, var2, var3, var2);
   }

   public WidgetSprites(ResourceLocation enabled, ResourceLocation disabled, ResourceLocation enabledFocused, ResourceLocation disabledFocused) {
      super();
      this.enabled = enabled;
      this.disabled = disabled;
      this.enabledFocused = enabledFocused;
      this.disabledFocused = disabledFocused;
   }

   public ResourceLocation get(boolean var1, boolean var2) {
      if (var1) {
         return var2 ? this.enabledFocused : this.enabled;
      } else {
         return var2 ? this.disabledFocused : this.disabled;
      }
   }
}
