package net.minecraft.client.gui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
   private static final int SCROLLBAR_SPACING = 4;
   private static final int SCROLLBAR_RESERVE = 10;
   final Layout content;
   private final Container container;
   private int minWidth;
   private int maxHeight;

   public ScrollableLayout(Minecraft var1, Layout var2, int var3) {
      super();
      this.content = var2;
      this.container = new Container(var1, 0, var3);
   }

   public void setMinWidth(int var1) {
      this.minWidth = var1;
      this.container.setWidth(Math.max(this.content.getWidth(), var1));
   }

   public void setMaxHeight(int var1) {
      this.maxHeight = var1;
      this.container.setHeight(Math.min(this.content.getHeight(), var1));
      this.container.refreshScrollAmount();
   }

   public void arrangeElements() {
      this.content.arrangeElements();
      int var1 = this.content.getWidth();
      this.container.setWidth(Math.max(var1 + 20, this.minWidth));
      this.container.setHeight(Math.min(this.content.getHeight(), this.maxHeight));
      this.container.refreshScrollAmount();
   }

   public void visitChildren(Consumer<LayoutElement> var1) {
      var1.accept(this.container);
   }

   public void setX(int var1) {
      this.container.setX(var1);
   }

   public void setY(int var1) {
      this.container.setY(var1);
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

   class Container extends AbstractContainerWidget {
      private final Minecraft minecraft;
      private final List<AbstractWidget> children = new ArrayList();

      public Container(final Minecraft var2, final int var3, final int var4) {
         super(0, 0, var3, var4, CommonComponents.EMPTY);
         this.minecraft = var2;
         Layout var10000 = ScrollableLayout.this.content;
         List var10001 = this.children;
         Objects.requireNonNull(var10001);
         var10000.visitWidgets(var10001::add);
      }

      protected int contentHeight() {
         return ScrollableLayout.this.content.getHeight();
      }

      protected double scrollRate() {
         return 10.0;
      }

      protected void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         var1.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);

         for(AbstractWidget var6 : this.children) {
            var6.render(var1, var2, var3, var4);
         }

         var1.disableScissor();
         this.renderScrollbar(var1, var2, var3);
      }

      protected void updateWidgetNarration(NarrationElementOutput var1) {
      }

      public ScreenRectangle getBorderForArrowNavigation(ScreenDirection var1) {
         return new ScreenRectangle(this.getX(), this.getY(), this.width, this.contentHeight());
      }

      public void setFocused(@Nullable GuiEventListener var1) {
         super.setFocused(var1);
         if (var1 != null && this.minecraft.getLastInputType().isKeyboard()) {
            ScreenRectangle var2 = this.getRectangle();
            ScreenRectangle var3 = var1.getRectangle();
            int var4 = var3.top() - var2.top();
            int var5 = var3.bottom() - var2.bottom();
            if (var4 < 0) {
               this.setScrollAmount(this.scrollAmount() + (double)var4 - 14.0);
            } else if (var5 > 0) {
               this.setScrollAmount(this.scrollAmount() + (double)var5 + 14.0);
            }

         }
      }

      public void setX(int var1) {
         super.setX(var1);
         ScrollableLayout.this.content.setX(var1 + 10);
      }

      public void setY(int var1) {
         super.setY(var1);
         ScrollableLayout.this.content.setY(var1 - (int)this.scrollAmount());
      }

      public void setScrollAmount(double var1) {
         super.setScrollAmount(var1);
         ScrollableLayout.this.content.setY(this.getRectangle().top() - (int)this.scrollAmount());
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public Collection<? extends NarratableEntry> getNarratables() {
         return this.children;
      }
   }
}
