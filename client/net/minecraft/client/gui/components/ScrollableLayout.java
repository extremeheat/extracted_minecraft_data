package net.minecraft.client.gui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import org.jspecify.annotations.Nullable;

public class ScrollableLayout implements Layout {
   private static final int DEFAULT_SCROLLBAR_SPACING = 4;
   private final Layout content;
   private final Container container;
   private final ReserveStrategy reserveStrategy;
   private final int scrollbarSpacing;
   private int minWidth;
   private int minHeight;
   private int maxHeight;

   public ScrollableLayout(final Minecraft minecraft, final Layout content, final int maxHeight) {
      super();
      this.content = content;
      this.maxHeight = maxHeight;
      this.reserveStrategy = ScrollableLayout.ReserveStrategy.BOTH;
      this.scrollbarSpacing = 4;
      this.container = new Container(minecraft, 0, maxHeight, AbstractScrollArea.defaultSettings(10));
   }

   public void setMinWidth(final int minWidth) {
      this.minWidth = minWidth;
      this.container.setWidth(Math.max(this.content.getWidth(), minWidth));
   }

   public void setMinHeight(final int minHeight) {
      this.minHeight = minHeight;
      this.container.setHeight(Math.max(this.content.getHeight(), minHeight));
   }

   public void setMaxHeight(final int maxHeight) {
      this.maxHeight = maxHeight;
      this.container.setHeight(Math.min(this.content.getHeight(), maxHeight));
      this.container.refreshScrollAmount();
   }

   public void arrangeElements() {
      this.content.arrangeElements();
      int contentWidth = this.content.getWidth();
      int var10000;
      switch (this.reserveStrategy.ordinal()) {
         case 0 -> var10000 = this.container.scrollbarReserve();
         case 1 -> var10000 = 2 * this.container.scrollbarReserve();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      int scrollbarReserve = var10000;
      this.container.setWidth(Math.max(contentWidth, this.minWidth) + scrollbarReserve);
      this.container.setHeight(Math.clamp((long)this.container.getHeight(), this.minHeight, this.maxHeight));
      this.container.refreshScrollAmount();
   }

   public void visitChildren(final Consumer<LayoutElement> layoutElementVisitor) {
      layoutElementVisitor.accept(this.container);
   }

   public void setX(final int x) {
      this.container.setX(x);
   }

   public void setY(final int y) {
      this.container.setY(y);
   }

   public int getX() {
      return this.container.getX();
   }

   public int getY() {
      return this.container.getY();
   }

   public int getWidth() {
      return this.container.getWidth();
   }

   public int getHeight() {
      return this.container.getHeight();
   }

   private class Container extends AbstractContainerWidget {
      private final Minecraft minecraft;
      private final List<AbstractWidget> children;

      public Container(final Minecraft minecraft, final int width, final int height, final AbstractScrollArea.ScrollbarSettings scrollbarSettings) {
         Objects.requireNonNull(ScrollableLayout.this);
         super(0, 0, width, height, CommonComponents.EMPTY, scrollbarSettings);
         this.children = new ArrayList();
         this.minecraft = minecraft;
         Layout var10000 = ScrollableLayout.this.content;
         List var10001 = this.children;
         Objects.requireNonNull(var10001);
         var10000.visitWidgets(var10001::add);
      }

      protected int contentHeight() {
         return ScrollableLayout.this.content.getHeight();
      }

      protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
         graphics.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);

         for(AbstractWidget child : this.children) {
            child.extractRenderState(graphics, mouseX, mouseY, a);
         }

         graphics.disableScissor();
         this.extractScrollbar(graphics, mouseX, mouseY);
      }

      protected void updateWidgetNarration(final NarrationElementOutput output) {
      }

      public ScreenRectangle getBorderForArrowNavigation(final ScreenDirection opposite) {
         GuiEventListener focused = this.getFocused();
         return focused != null ? focused.getBorderForArrowNavigation(opposite) : (new ScreenRectangle(this.getX(), this.getY(), this.width, this.contentHeight())).getBorder(opposite);
      }

      public void setFocused(final @Nullable GuiEventListener focused) {
         super.setFocused(focused);
         if (focused != null && this.minecraft.getLastInputType().isKeyboard()) {
            ScreenRectangle area = this.getRectangle();
            ScreenRectangle focusedRect = focused.getRectangle();
            int topDelta = focusedRect.top() - area.top();
            int bottomDelta = focusedRect.bottom() - area.bottom();
            double scrollRate = this.scrollRate();
            if (topDelta < 0) {
               this.setScrollAmount(this.scrollAmount() + (double)topDelta - scrollRate);
            } else if (bottomDelta > 0) {
               this.setScrollAmount(this.scrollAmount() + (double)bottomDelta + scrollRate);
            }

         }
      }

      public void setX(final int x) {
         super.setX(x);
         ScrollableLayout.this.content.setX(x + (ScrollableLayout.this.reserveStrategy == ScrollableLayout.ReserveStrategy.BOTH ? this.scrollbarReserve() : 0));
      }

      public void setY(final int y) {
         super.setY(y);
         ScrollableLayout.this.content.setY(y - (int)this.scrollAmount());
      }

      private int scrollbarReserve() {
         return ScrollableLayout.this.scrollbarSpacing + this.scrollbarWidth();
      }

      public void setScrollAmount(final double scrollAmount) {
         super.setScrollAmount(scrollAmount);
         ScrollableLayout.this.content.setY(this.getRectangle().top() - (int)this.scrollAmount());
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public Collection<? extends NarratableEntry> getNarratables() {
         return this.children;
      }
   }

   public static enum ReserveStrategy {
      RIGHT,
      BOTH;

      private ReserveStrategy() {
      }

      // $FF: synthetic method
      private static ReserveStrategy[] $values() {
         return new ReserveStrategy[]{RIGHT, BOTH};
      }
   }
}
