package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.time.Duration;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import org.jspecify.annotations.Nullable;

public abstract class AbstractWidget implements Renderable, GuiEventListener, LayoutElement, NarratableEntry {
   protected int width;
   protected int height;
   private int x;
   private int y;
   protected Component message;
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

   public int getHeight() {
      return this.height;
   }

   public final void render(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.visible) {
         this.isHovered = var1.containsPointInScissor(var2, var3) && this.areCoordinatesInRectangle((double)var2, (double)var3);
         this.renderWidget(var1, var2, var3, var4);
         this.tooltip.refreshTooltipForNextRenderPass(var1, var2, var3, this.isHovered(), this.isFocused(), this.getRectangle());
      }
   }

   protected void handleCursor(GuiGraphics var1) {
      if (this.isHovered()) {
         var1.requestCursor(this.isActive() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
      }

   }

   public void setTooltip(@Nullable Tooltip var1) {
      this.tooltip.set(var1);
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

   protected void renderScrollingStringOverContents(ActiveTextCollector var1, Component var2, int var3) {
      int var4 = this.getX() + var3;
      int var5 = this.getX() + this.getWidth() - var3;
      int var6 = this.getY();
      int var7 = this.getY() + this.getHeight();
      var1.acceptScrollingWithDefaultCenter(var2, var4, var5, var6, var7);
   }

   public void onClick(MouseButtonEvent var1, boolean var2) {
   }

   public void onRelease(MouseButtonEvent var1) {
   }

   protected void onDrag(MouseButtonEvent var1, double var2, double var4) {
   }

   public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
      if (!this.isActive()) {
         return false;
      } else {
         if (this.isValidClickButton(var1.buttonInfo())) {
            boolean var3 = this.isMouseOver(var1.x(), var1.y());
            if (var3) {
               this.playDownSound(Minecraft.getInstance().getSoundManager());
               this.onClick(var1, var2);
               return true;
            }
         }

         return false;
      }
   }

   public boolean mouseReleased(MouseButtonEvent var1) {
      if (this.isValidClickButton(var1.buttonInfo())) {
         this.onRelease(var1);
         return true;
      } else {
         return false;
      }
   }

   protected boolean isValidClickButton(MouseButtonInfo var1) {
      return var1.button() == 0;
   }

   public boolean mouseDragged(MouseButtonEvent var1, double var2, double var4) {
      if (this.isValidClickButton(var1.buttonInfo())) {
         this.onDrag(var1, var2, var4);
         return true;
      } else {
         return false;
      }
   }

   public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      if (!this.isActive()) {
         return null;
      } else {
         return !this.isFocused() ? ComponentPath.leaf(this) : null;
      }
   }

   public boolean isMouseOver(double var1, double var3) {
      return this.isActive() && this.areCoordinatesInRectangle(var1, var3);
   }

   public void playDownSound(SoundManager var1) {
      playButtonClickSound(var1);
   }

   public static void playButtonClickSound(SoundManager var0) {
      var0.play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

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

   public float getAlpha() {
      return this.alpha;
   }

   public void setMessage(Component var1) {
      this.message = var1;
   }

   public Component getMessage() {
      return this.message;
   }

   public boolean isFocused() {
      return this.focused;
   }

   public boolean isHovered() {
      return this.isHovered;
   }

   public boolean isHoveredOrFocused() {
      return this.isHovered() || this.isFocused();
   }

   public boolean isActive() {
      return this.visible && this.active;
   }

   public void setFocused(boolean var1) {
      this.focused = var1;
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      if (this.isFocused()) {
         return NarratableEntry.NarrationPriority.FOCUSED;
      } else {
         return this.isHovered ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
      }
   }

   public final void updateNarration(NarrationElementOutput var1) {
      this.updateWidgetNarration(var1);
      this.tooltip.updateNarration(var1);
   }

   protected abstract void updateWidgetNarration(NarrationElementOutput var1);

   protected void defaultButtonNarrationText(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
      if (this.active) {
         if (this.isFocused()) {
            var1.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.button.usage.focused"));
         } else {
            var1.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.button.usage.hovered"));
         }
      }

   }

   public int getX() {
      return this.x;
   }

   public void setX(int var1) {
      this.x = var1;
   }

   public int getY() {
      return this.y;
   }

   public void setY(int var1) {
      this.y = var1;
   }

   public int getRight() {
      return this.getX() + this.getWidth();
   }

   public int getBottom() {
      return this.getY() + this.getHeight();
   }

   public void visitWidgets(Consumer<AbstractWidget> var1) {
      var1.accept(this);
   }

   public void setSize(int var1, int var2) {
      this.width = var1;
      this.height = var2;
   }

   public ScreenRectangle getRectangle() {
      return LayoutElement.super.getRectangle();
   }

   private boolean areCoordinatesInRectangle(double var1, double var3) {
      return var1 >= (double)this.getX() && var3 >= (double)this.getY() && var1 < (double)this.getRight() && var3 < (double)this.getBottom();
   }

   public void setRectangle(int var1, int var2, int var3, int var4) {
      this.setSize(var1, var2);
      this.setPosition(var3, var4);
   }

   public int getTabOrderGroup() {
      return this.tabOrderGroup;
   }

   public void setTabOrderGroup(int var1) {
      this.tabOrderGroup = var1;
   }

   public abstract static class WithInactiveMessage extends AbstractWidget {
      private Component inactiveMessage;

      public static Component defaultInactiveMessage(Component var0) {
         return ComponentUtils.mergeStyles(var0, Style.EMPTY.withColor(-6250336));
      }

      public WithInactiveMessage(int var1, int var2, int var3, int var4, Component var5) {
         super(var1, var2, var3, var4, var5);
         this.inactiveMessage = defaultInactiveMessage(var5);
      }

      public Component getMessage() {
         return this.active ? super.getMessage() : this.inactiveMessage;
      }

      public void setMessage(Component var1) {
         super.setMessage(var1);
         this.inactiveMessage = defaultInactiveMessage(var1);
      }
   }
}
