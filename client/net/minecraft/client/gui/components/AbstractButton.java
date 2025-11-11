package net.minecraft.client.gui.components;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;

public abstract class AbstractButton extends AbstractWidget.WithInactiveMessage {
   protected static final int TEXT_MARGIN = 2;
   private static final WidgetSprites SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("widget/button"), Identifier.withDefaultNamespace("widget/button_disabled"), Identifier.withDefaultNamespace("widget/button_highlighted"));
   private @Nullable Supplier<Boolean> overrideRenderHighlightedSprite;

   public AbstractButton(int var1, int var2, int var3, int var4, Component var5) {
      super(var1, var2, var3, var4, var5);
   }

   public abstract void onPress(InputWithModifiers var1);

   protected final void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderContents(var1, var2, var3, var4);
      this.handleCursor(var1);
   }

   protected abstract void renderContents(GuiGraphics var1, int var2, int var3, float var4);

   protected void renderDefaultLabel(ActiveTextCollector var1) {
      this.renderScrollingStringOverContents(var1, this.getMessage(), 2);
   }

   protected final void renderDefaultSprite(GuiGraphics var1) {
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.active, this.overrideRenderHighlightedSprite != null ? (Boolean)this.overrideRenderHighlightedSprite.get() : this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
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

   public void setOverrideRenderHighlightedSprite(Supplier<Boolean> var1) {
      this.overrideRenderHighlightedSprite = var1;
   }
}
