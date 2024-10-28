package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ImageButton extends Button {
   protected final WidgetSprites sprites;

   public ImageButton(int var1, int var2, int var3, int var4, WidgetSprites var5, Button.OnPress var6) {
      this(var1, var2, var3, var4, var5, var6, CommonComponents.EMPTY);
   }

   public ImageButton(int var1, int var2, int var3, int var4, WidgetSprites var5, Button.OnPress var6, Component var7) {
      super(var1, var2, var3, var4, var7, var6, DEFAULT_NARRATION);
      this.sprites = var5;
   }

   public ImageButton(int var1, int var2, WidgetSprites var3, Button.OnPress var4, Component var5) {
      this(0, 0, var1, var2, var3, var4, var5);
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      ResourceLocation var5 = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
      var1.blitSprite(var5, this.getX(), this.getY(), this.width, this.height);
   }
}
