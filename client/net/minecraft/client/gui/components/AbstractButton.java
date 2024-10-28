package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class AbstractButton extends AbstractWidget {
   protected static final int TEXT_MARGIN = 2;
   private static final WidgetSprites SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/button"), ResourceLocation.withDefaultNamespace("widget/button_disabled"), ResourceLocation.withDefaultNamespace("widget/button_highlighted"));

   public AbstractButton(int var1, int var2, int var3, int var4, Component var5) {
      super(var1, var2, var3, var4, var5);
   }

   public abstract void onPress();

   protected void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      Minecraft var5 = Minecraft.getInstance();
      var1.setColor(1.0F, 1.0F, 1.0F, this.alpha);
      RenderSystem.enableBlend();
      RenderSystem.enableDepthTest();
      var1.blitSprite(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      int var6 = this.active ? 16777215 : 10526880;
      this.renderString(var1, var5.font, var6 | Mth.ceil(this.alpha * 255.0F) << 24);
   }

   public void renderString(GuiGraphics var1, Font var2, int var3) {
      this.renderScrollingString(var1, var2, 2, var3);
   }

   public void onClick(double var1, double var3) {
      this.onPress();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.active && this.visible) {
         if (CommonInputs.selected(var1)) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
