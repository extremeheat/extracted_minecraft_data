package net.minecraft.client.gui.components;

import java.time.Duration;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

public abstract class AbstractWidget implements Renderable, GuiEventListener, LayoutElement, NarratableEntry {
   private static final double PERIOD_PER_SCROLLED_PIXEL = 0.5;
   private static final double MIN_SCROLL_PERIOD = 3.0;
   protected int width;
   protected int height;
   private int x;
   private int y;
   private Component message;
   protected boolean isHovered;
   public boolean active = true;
   public boolean visible = true;
   protected float alpha = 1.0F;
   private int tabOrderGroup;
   private boolean focused;
   private final WidgetTooltipHolder tooltip = new WidgetTooltipHolder();

   public AbstractWidget(int var1, int var2, int var3, int var4, Component var5) {
      super();
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
      this.message = var5;
   }

   @Override
   public int getHeight() {
      return this.height;
   }

   @Override
   public final void render(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.visible) {
         this.isHovered = var1.containsPointInScissor(var2, var3)
            && var2 >= this.getX()
            && var3 >= this.getY()
            && var2 < this.getX() + this.width
            && var3 < this.getY() + this.height;
         this.renderWidget(var1, var2, var3, var4);
         this.tooltip.refreshTooltipForNextRenderPass(this.isHovered(), this.isFocused(), this.getRectangle());
      }
   }

   public void setTooltip(@Nullable Tooltip var1) {
      this.tooltip.set(var1);
   }

   @Nullable
   public Tooltip getTooltip() {
      return this.tooltip.get();
   }

   public void setTooltipDelay(Duration var1) {
      this.tooltip.setDelay(var1);
   }

   protected MutableComponent createNarrationMessage() {
      return wrapDefaultNarrationMessage(this.getMessage());
   }

   public static MutableComponent wrapDefaultNarrationMessage(Component var0) {
      return Component.translatable("gui.narrate.button", var0);
   }

   protected abstract void renderWidget(GuiGraphics var1, int var2, int var3, float var4);

   protected static void renderScrollingString(GuiGraphics var0, Font var1, Component var2, int var3, int var4, int var5, int var6, int var7) {
      renderScrollingString(var0, var1, var2, (var3 + var5) / 2, var3, var4, var5, var6, var7);
   }

   protected static void renderScrollingString(GuiGraphics var0, Font var1, Component var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      int var9 = var1.width(var2);
      int var10 = (var5 + var7 - 9) / 2 + 1;
      int var11 = var6 - var4;
      if (var9 > var11) {
         int var12 = var9 - var11;
         double var13 = (double)Util.getMillis() / 1000.0;
         double var15 = Math.max((double)var12 * 0.5, 3.0);
         double var17 = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * var13 / var15)) / 2.0 + 0.5;
         double var19 = Mth.lerp(var17, 0.0, (double)var12);
         var0.enableScissor(var4, var5, var6, var7);
         var0.drawString(var1, var2, var4 - (int)var19, var10, var8);
         var0.disableScissor();
      } else {
         int var21 = Mth.clamp(var3, var4 + var9 / 2, var6 - var9 / 2);
         var0.drawCenteredString(var1, var2, var21, var10, var8);
      }
   }

   protected void renderScrollingString(GuiGraphics var1, Font var2, int var3, int var4) {
      int var5 = this.getX() + var3;
      int var6 = this.getX() + this.getWidth() - var3;
      renderScrollingString(var1, var2, this.getMessage(), var5, this.getY(), var6, this.getY() + this.getHeight(), var4);
   }

   public void onClick(double var1, double var3) {
   }

   public void onRelease(double var1, double var3) {
   }

   protected void onDrag(double var1, double var3, double var5, double var7) {
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.active && this.visible) {
         if (this.isValidClickButton(var5)) {
            boolean var6 = this.clicked(var1, var3);
            if (var6) {
               this.playDownSound(Minecraft.getInstance().getSoundManager());
               this.onClick(var1, var3);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseReleased(double var1, double var3, int var5) {
      if (this.isValidClickButton(var5)) {
         this.onRelease(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   protected boolean isValidClickButton(int var1) {
      return var1 == 0;
   }

   @Override
   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.isValidClickButton(var5)) {
         this.onDrag(var1, var3, var6, var8);
         return true;
      } else {
         return false;
      }
   }

   protected boolean clicked(double var1, double var3) {
      return this.active
         && this.visible
         && var1 >= (double)this.getX()
         && var3 >= (double)this.getY()
         && var1 < (double)(this.getX() + this.getWidth())
         && var3 < (double)(this.getY() + this.getHeight());
   }

   @Nullable
   @Override
   public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      if (!this.active || !this.visible) {
         return null;
      } else {
         return !this.isFocused() ? ComponentPath.leaf(this) : null;
      }
   }

   @Override
   public boolean isMouseOver(double var1, double var3) {
      return this.active
         && this.visible
         && var1 >= (double)this.getX()
         && var3 >= (double)this.getY()
         && var1 < (double)(this.getX() + this.width)
         && var3 < (double)(this.getY() + this.height);
   }

   public void playDownSound(SoundManager var1) {
      playButtonClickSound(var1);
   }

   public static void playButtonClickSound(SoundManager var0) {
      var0.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   @Override
   public int getWidth() {
      return this.width;
   }

   public void setWidth(int var1) {
      this.width = var1;
   }

   public void setHeight(int var1) {
      this.height = var1;
   }

   public void setAlpha(float var1) {
      this.alpha = var1;
   }

   public void setMessage(Component var1) {
      this.message = var1;
   }

   public Component getMessage() {
      return this.message;
   }

   @Override
   public boolean isFocused() {
      return this.focused;
   }

   public boolean isHovered() {
      return this.isHovered;
   }

   public boolean isHoveredOrFocused() {
      return this.isHovered() || this.isFocused();
   }

   @Override
   public boolean isActive() {
      return this.visible && this.active;
   }

   @Override
   public void setFocused(boolean var1) {
      this.focused = var1;
   }

   @Override
   public NarratableEntry.NarrationPriority narrationPriority() {
      if (this.isFocused()) {
         return NarratableEntry.NarrationPriority.FOCUSED;
      } else {
         return this.isHovered ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
      }
   }

   @Override
   public final void updateNarration(NarrationElementOutput var1) {
      this.updateWidgetNarration(var1);
      this.tooltip.updateNarration(var1);
   }

   protected abstract void updateWidgetNarration(NarrationElementOutput var1);

   protected void defaultButtonNarrationText(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.createNarrationMessage());
      if (this.active) {
         if (this.isFocused()) {
            var1.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.focused"));
         } else {
            var1.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
         }
      }
   }

   @Override
   public int getX() {
      return this.x;
   }

   @Override
   public void setX(int var1) {
      this.x = var1;
   }

   @Override
   public int getY() {
      return this.y;
   }

   @Override
   public void setY(int var1) {
      this.y = var1;
   }

   public int getRight() {
      return this.getX() + this.getWidth();
   }

   public int getBottom() {
      return this.getY() + this.getHeight();
   }

   @Override
   public void visitWidgets(Consumer<AbstractWidget> var1) {
      var1.accept(this);
   }

   public void setSize(int var1, int var2) {
      this.width = var1;
      this.height = var2;
   }

   @Override
   public ScreenRectangle getRectangle() {
      return LayoutElement.super.getRectangle();
   }

   public void setRectangle(int var1, int var2, int var3, int var4) {
      this.setSize(var1, var2);
      this.setPosition(var3, var4);
   }

   @Override
   public int getTabOrderGroup() {
      return this.tabOrderGroup;
   }

   public void setTabOrderGroup(int var1) {
      this.tabOrderGroup = var1;
   }
}
