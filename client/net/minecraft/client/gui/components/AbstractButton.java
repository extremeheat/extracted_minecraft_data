package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

public abstract class AbstractButton extends AbstractWidget {
   protected static final int TEXT_MARGIN = 2;
   private static final WidgetSprites SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("widget/button"), ResourceLocation.withDefaultNamespace("widget/button_disabled"), ResourceLocation.withDefaultNamespace("widget/button_highlighted"));

   public AbstractButton(int var1, int var2, int var3, int var4, Component var5) {
      super(var1, var2, var3, var4, var5);
   }

   public abstract void onPress(InputWithModifiers var1);

   protected void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      Minecraft var5 = Minecraft.getInstance();
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
      int var6 = ARGB.color(this.alpha, this.active ? -1 : -6250336);
      this.renderString(var1, var5.font, var6);
      if (this.isHovered()) {
         var1.requestCursor(this.isActive() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
      }

   }

   public void renderString(GuiGraphics var1, Font var2, int var3) {
      this.renderScrollingString(var1, var2, 2, var3);
   }

   public void onClick(MouseButtonEvent var1, boolean var2) {
      this.onPress(var1);
   }

   public boolean keyPressed(KeyEvent var1) {
      if (!this.isActive()) {
         return false;
      } else if (var1.isSelection()) {
         this.playDownSound(Minecraft.getInstance().getSoundManager());
         this.onPress(var1);
         return true;
      } else {
         return false;
      }
   }
}
