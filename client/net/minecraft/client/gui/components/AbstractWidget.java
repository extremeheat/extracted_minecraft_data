package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.time.Duration;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

public abstract class AbstractWidget implements LayoutElement, Renderable, GuiEventListener, NarratableEntry {
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

   public AbstractWidget(final int x, final int y, final int width, final int height, final Component message) {
      super();
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.message = message;
   }

   public int getHeight() {
      return this.height;
   }

   public final void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      if (this.visible) {
         this.isHovered = graphics.containsPointInScissor(mouseX, mouseY) && this.areCoordinatesInRectangle((double)mouseX, (double)mouseY);
         this.extractWidgetRenderState(graphics, mouseX, mouseY, a);
         this.tooltip.refreshTooltipForNextRenderPass(graphics, mouseX, mouseY, this.isHovered(), this.isFocused(), this.getRectangle());
      }
   }

   protected void handleCursor(final GuiGraphicsExtractor graphics) {
      if (this.isHovered()) {
         graphics.requestCursor(this.isActive() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
      }

   }

   public void setTooltip(final @Nullable Tooltip tooltip) {
      this.tooltip.set(tooltip);
   }

   public void setTooltipDelay(final Duration delay) {
      this.tooltip.setDelay(delay);
   }

   protected MutableComponent createNarrationMessage() {
      return wrapDefaultNarrationMessage(this.getMessage());
   }

   public static MutableComponent wrapDefaultNarrationMessage(final Component message) {
      return Component.translatable("gui.narrate.button", message);
   }

   protected abstract void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a);

   protected void extractScrollingStringOverContents(final ActiveTextCollector output, final Component message, final int margin) {
      int left = this.getX() + margin;
      int right = this.getX() + this.getWidth() - margin;
      int top = this.getY();
      int bottom = this.getY() + this.getHeight();
      output.acceptScrollingWithDefaultCenter(message, left, right, top, bottom);
   }

   public void onClick(final MouseButtonEvent event, final boolean doubleClick) {
   }

   public void onRelease(final MouseButtonEvent event) {
   }

   protected void onDrag(final MouseButtonEvent event, final double dx, final double dy) {
   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (!this.isActive()) {
         return false;
      } else {
         if (this.isValidClickButton(event.buttonInfo())) {
            boolean isMouseOver = this.isMouseOver(event.x(), event.y());
            if (isMouseOver) {
               this.playDownSound(Minecraft.getInstance().getSoundManager());
               this.onClick(event, doubleClick);
               return true;
            }
         }

         return false;
      }
   }

   public boolean mouseReleased(final MouseButtonEvent event) {
      if (this.isValidClickButton(event.buttonInfo())) {
         this.onRelease(event);
         return true;
      } else {
         return false;
      }
   }

   protected boolean isValidClickButton(final MouseButtonInfo buttonInfo) {
      return buttonInfo.button() == 0;
   }

   public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
      if (this.isValidClickButton(event.buttonInfo())) {
         this.onDrag(event, dx, dy);
         return true;
      } else {
         return false;
      }
   }

   public @Nullable ComponentPath nextFocusPath(final FocusNavigationEvent navigationEvent) {
      if (!this.isActive()) {
         return null;
      } else {
         return !this.isFocused() ? ComponentPath.leaf(this) : null;
      }
   }

   public boolean isMouseOver(final double mouseX, final double mouseY) {
      return this.isActive() && this.areCoordinatesInRectangle(mouseX, mouseY);
   }

   public void playDownSound(final SoundManager soundManager) {
      playButtonClickSound(soundManager);
   }

   public static void playButtonClickSound(final SoundManager soundManager) {
      soundManager.play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(final int width) {
      this.width = width;
   }

   public void setHeight(final int height) {
      this.height = height;
   }

   public void setAlpha(final float alpha) {
      this.alpha = alpha;
   }

   public float getAlpha() {
      return this.alpha;
   }

   public void setMessage(final Component message) {
      this.message = message;
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

   public void setFocused(final boolean focused) {
      this.focused = focused;
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      if (this.isFocused()) {
         return NarratableEntry.NarrationPriority.FOCUSED;
      } else {
         return this.isHovered ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
      }
   }

   public final void updateNarration(final NarrationElementOutput output) {
      this.updateWidgetNarration(output);
      this.tooltip.updateNarration(output);
   }

   protected abstract void updateWidgetNarration(final NarrationElementOutput output);

   protected void defaultButtonNarrationText(final NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
      if (this.active) {
         if (this.isFocused()) {
            output.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.button.usage.focused"));
         } else {
            output.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.button.usage.hovered"));
         }
      }

   }

   public int getX() {
      return this.x;
   }

   public void setX(final int x) {
      this.x = x;
   }

   public int getY() {
      return this.y;
   }

   public void setY(final int y) {
      this.y = y;
   }

   public int getRight() {
      return this.getX() + this.getWidth();
   }

   public int getBottom() {
      return this.getY() + this.getHeight();
   }

   public void visitWidgets(final Consumer<AbstractWidget> widgetVisitor) {
      widgetVisitor.accept(this);
   }

   public void setSize(final int width, final int height) {
      this.width = width;
      this.height = height;
   }

   public ScreenRectangle getRectangle() {
      return LayoutElement.super.getRectangle();
   }

   private boolean areCoordinatesInRectangle(final double x, final double y) {
      return x >= (double)this.getX() && y >= (double)this.getY() && x < (double)this.getRight() && y < (double)this.getBottom();
   }

   public void setRectangle(final int width, final int height, final int x, final int y) {
      this.setSize(width, height);
      this.setPosition(x, y);
   }

   public int getTabOrderGroup() {
      return this.tabOrderGroup;
   }

   public void setTabOrderGroup(final int tabOrderGroup) {
      this.tabOrderGroup = tabOrderGroup;
   }

   public abstract static class WithInactiveMessage extends AbstractWidget {
      private Component inactiveMessage;

      public static Component defaultInactiveMessage(final Component activeMessage) {
         return ComponentUtils.mergeStyles(activeMessage, Style.EMPTY.withColor(-6250336));
      }

      public WithInactiveMessage(final int x, final int y, final int width, final int height, final Component message) {
         super(x, y, width, height, message);
         this.inactiveMessage = defaultInactiveMessage(message);
      }

      public Component getMessage() {
         return this.active ? super.getMessage() : this.inactiveMessage;
      }

      public void setMessage(final Component message) {
         super.setMessage(message);
         this.inactiveMessage = defaultInactiveMessage(message);
      }
   }
}
