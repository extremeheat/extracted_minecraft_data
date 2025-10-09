package net.minecraft.client.gui.components.tabs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TabNavigationBar extends AbstractContainerEventHandler implements Renderable, NarratableEntry {
   private static final int NO_TAB = -1;
   private static final int MAX_WIDTH = 400;
   private static final int HEIGHT = 24;
   private static final int MARGIN = 14;
   private static final Component USAGE_NARRATION = Component.translatable("narration.tab_navigation.usage");
   private final LinearLayout layout = LinearLayout.horizontal();
   private int width;
   private final TabManager tabManager;
   private final ImmutableList<Tab> tabs;
   private final ImmutableList<TabButton> tabButtons;

   TabNavigationBar(int var1, TabManager var2, Iterable<Tab> var3) {
      super();
      this.width = var1;
      this.tabManager = var2;
      this.tabs = ImmutableList.copyOf(var3);
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      ImmutableList.Builder var4 = ImmutableList.builder();

      for(Tab var6 : var3) {
         var4.add((TabButton)this.layout.addChild(new TabButton(var2, var6, 0, 24)));
      }

      this.tabButtons = var4.build();
   }

   public static Builder builder(TabManager var0, int var1) {
      return new Builder(var0, var1);
   }

   public void setWidth(int var1) {
      this.width = var1;
   }

   public boolean isMouseOver(double var1, double var3) {
      return var1 >= (double)this.layout.getX() && var3 >= (double)this.layout.getY() && var1 < (double)(this.layout.getX() + this.layout.getWidth()) && var3 < (double)(this.layout.getY() + this.layout.getHeight());
   }

   public void setFocused(boolean var1) {
      super.setFocused(var1);
      if (this.getFocused() != null) {
         this.setFocused((GuiEventListener)null);
      }

   }

   public void setFocused(@Nullable GuiEventListener var1) {
      super.setFocused(var1);
      if (var1 instanceof TabButton var2) {
         if (var2.isActive()) {
            this.tabManager.setCurrentTab(var2.tab(), true);
         }
      }

   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      if (!this.isFocused()) {
         TabButton var2 = this.currentTabButton();
         if (var2 != null) {
            return ComponentPath.path((ContainerEventHandler)this, (ComponentPath)ComponentPath.leaf(var2));
         }
      }

      return var1 instanceof FocusNavigationEvent.TabNavigation ? null : super.nextFocusPath(var1);
   }

   public List<? extends GuiEventListener> children() {
      return this.tabButtons;
   }

   public List<Tab> getTabs() {
      return this.tabs;
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      return (NarratableEntry.NarrationPriority)this.tabButtons.stream().map(AbstractWidget::narrationPriority).max(Comparator.naturalOrder()).orElse(NarratableEntry.NarrationPriority.NONE);
   }

   public void updateNarration(NarrationElementOutput var1) {
      Optional var2 = this.tabButtons.stream().filter(AbstractWidget::isHovered).findFirst().or(() -> Optional.ofNullable(this.currentTabButton()));
      var2.ifPresent((var2x) -> {
         this.narrateListElementPosition(var1.nest(), var2x);
         var2x.updateNarration(var1);
      });
      if (this.isFocused()) {
         var1.add(NarratedElementType.USAGE, USAGE_NARRATION);
      }

   }

   protected void narrateListElementPosition(NarrationElementOutput var1, TabButton var2) {
      if (this.tabs.size() > 1) {
         int var3 = this.tabButtons.indexOf(var2);
         if (var3 != -1) {
            var1.add(NarratedElementType.POSITION, (Component)Component.translatable("narrator.position.tab", var3 + 1, this.tabs.size()));
         }
      }

   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      var1.blit(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR, 0, this.layout.getY() + this.layout.getHeight() - 2, 0.0F, 0.0F, ((TabButton)this.tabButtons.get(0)).getX(), 2, 32, 2);
      int var5 = ((TabButton)this.tabButtons.get(this.tabButtons.size() - 1)).getRight();
      var1.blit(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR, var5, this.layout.getY() + this.layout.getHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
      UnmodifiableIterator var6 = this.tabButtons.iterator();

      while(var6.hasNext()) {
         TabButton var7 = (TabButton)var6.next();
         var7.render(var1, var2, var3, var4);
      }

   }

   public ScreenRectangle getRectangle() {
      return this.layout.getRectangle();
   }

   public void arrangeElements() {
      int var1 = Math.min(400, this.width) - 28;
      int var2 = Mth.roundToward(var1 / this.tabs.size(), 2);
      UnmodifiableIterator var3 = this.tabButtons.iterator();

      while(var3.hasNext()) {
         TabButton var4 = (TabButton)var3.next();
         var4.setWidth(var2);
      }

      this.layout.arrangeElements();
      this.layout.setX(Mth.roundToward((this.width - var1) / 2, 2));
      this.layout.setY(0);
   }

   public void selectTab(int var1, boolean var2) {
      if (this.isFocused()) {
         this.setFocused((GuiEventListener)this.tabButtons.get(var1));
      } else if (((TabButton)this.tabButtons.get(var1)).isActive()) {
         this.tabManager.setCurrentTab((Tab)this.tabs.get(var1), var2);
      }

   }

   public void setTabActiveState(int var1, boolean var2) {
      if (var1 >= 0 && var1 < this.tabButtons.size()) {
         ((TabButton)this.tabButtons.get(var1)).active = var2;
      }

   }

   public void setTabTooltip(int var1, @Nullable Tooltip var2) {
      if (var1 >= 0 && var1 < this.tabButtons.size()) {
         ((TabButton)this.tabButtons.get(var1)).setTooltip(var2);
      }

   }

   public boolean keyPressed(KeyEvent var1) {
      if (var1.hasControlDownWithQuirk()) {
         int var2 = this.getNextTabIndex(var1);
         if (var2 != -1) {
            this.selectTab(Mth.clamp(var2, 0, this.tabs.size() - 1), true);
            return true;
         }
      }

      return false;
   }

   private int getNextTabIndex(KeyEvent var1) {
      return this.getNextTabIndex(this.currentTabIndex(), var1);
   }

   private int getNextTabIndex(int var1, KeyEvent var2) {
      int var3 = var2.getDigit();
      if (var3 != -1) {
         return Math.floorMod(var3 - 1, 10);
      } else if (var2.isCycleFocus() && var1 != -1) {
         int var4 = var2.hasShiftDown() ? var1 - 1 : var1 + 1;
         int var5 = Math.floorMod(var4, this.tabs.size());
         return ((TabButton)this.tabButtons.get(var5)).active ? var5 : this.getNextTabIndex(var5, var2);
      } else {
         return -1;
      }
   }

   private int currentTabIndex() {
      Tab var1 = this.tabManager.getCurrentTab();
      int var2 = this.tabs.indexOf(var1);
      return var2 != -1 ? var2 : -1;
   }

   @Nullable
   private TabButton currentTabButton() {
      int var1 = this.currentTabIndex();
      return var1 != -1 ? (TabButton)this.tabButtons.get(var1) : null;
   }

   public static class Builder {
      private final int width;
      private final TabManager tabManager;
      private final List<Tab> tabs = new ArrayList();

      Builder(TabManager var1, int var2) {
         super();
         this.tabManager = var1;
         this.width = var2;
      }

      public Builder addTabs(Tab... var1) {
         Collections.addAll(this.tabs, var1);
         return this;
      }

      public TabNavigationBar build() {
         return new TabNavigationBar(this.width, this.tabManager, this.tabs);
      }
   }
}
