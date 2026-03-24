package net.minecraft.client.gui.components.tabs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
import org.jspecify.annotations.Nullable;

public class TabNavigationBar extends AbstractContainerEventHandler implements NarratableEntry, Renderable {
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

   private TabNavigationBar(final int width, final TabManager tabManager, final Iterable<Tab> tabs) {
      super();
      this.width = width;
      this.tabManager = tabManager;
      this.tabs = ImmutableList.copyOf(tabs);
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      ImmutableList.Builder<TabButton> tabButtonsBuilder = ImmutableList.builder();

      for(Tab tab : tabs) {
         tabButtonsBuilder.add((TabButton)this.layout.addChild(new TabButton(tabManager, tab, 0, 24)));
      }

      this.tabButtons = tabButtonsBuilder.build();
   }

   public static Builder builder(final TabManager tabManager, final int width) {
      return new Builder(tabManager, width);
   }

   public void updateWidth(final int width) {
      this.width = width;
      this.arrangeElements();
   }

   public boolean isMouseOver(final double mouseX, final double mouseY) {
      return mouseX >= (double)this.layout.getX() && mouseY >= (double)this.layout.getY() && mouseX < (double)(this.layout.getX() + this.layout.getWidth()) && mouseY < (double)(this.layout.getY() + this.layout.getHeight());
   }

   public void setFocused(final boolean focused) {
      super.setFocused(focused);
      if (this.getFocused() != null) {
         this.setFocused((GuiEventListener)null);
      }

   }

   public void setFocused(final @Nullable GuiEventListener focused) {
      super.setFocused(focused);
      if (focused instanceof TabButton button) {
         if (button.isActive()) {
            this.tabManager.setCurrentTab(button.tab(), true);
         }
      }

   }

   public @Nullable ComponentPath nextFocusPath(final FocusNavigationEvent navigationEvent) {
      if (!this.isFocused()) {
         TabButton button = this.currentTabButton();
         if (button != null) {
            return ComponentPath.path((ContainerEventHandler)this, (ComponentPath)ComponentPath.leaf(button));
         }
      }

      return navigationEvent instanceof FocusNavigationEvent.TabNavigation ? null : super.nextFocusPath(navigationEvent);
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

   public void updateNarration(final NarrationElementOutput output) {
      Optional<TabButton> selected = this.tabButtons.stream().filter(AbstractWidget::isHovered).findFirst().or(() -> Optional.ofNullable(this.currentTabButton()));
      selected.ifPresent((button) -> {
         this.narrateListElementPosition(output.nest(), button);
         button.updateNarration(output);
      });
      if (this.isFocused()) {
         output.add(NarratedElementType.USAGE, USAGE_NARRATION);
      }

   }

   protected void narrateListElementPosition(final NarrationElementOutput output, final TabButton widget) {
      if (this.tabs.size() > 1) {
         int index = this.tabButtons.indexOf(widget);
         if (index != -1) {
            output.add(NarratedElementType.POSITION, (Component)Component.translatable("narrator.position.tab", index + 1, this.tabs.size()));
         }
      }

   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      graphics.blit(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR, 0, this.layout.getY() + this.layout.getHeight() - 2, 0.0F, 0.0F, ((TabButton)this.tabButtons.get(0)).getX(), 2, 32, 2);
      int afterLastTab = ((TabButton)this.tabButtons.get(this.tabButtons.size() - 1)).getRight();
      graphics.blit(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR, afterLastTab, this.layout.getY() + this.layout.getHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
      UnmodifiableIterator var6 = this.tabButtons.iterator();

      while(var6.hasNext()) {
         TabButton value = (TabButton)var6.next();
         value.extractRenderState(graphics, mouseX, mouseY, a);
      }

   }

   public ScreenRectangle getRectangle() {
      return this.layout.getRectangle();
   }

   public void arrangeElements() {
      int tabsWidth = Math.min(400, this.width) - 28;
      int tabWidth = Mth.roundToward(tabsWidth / this.tabs.size(), 2);
      UnmodifiableIterator var3 = this.tabButtons.iterator();

      while(var3.hasNext()) {
         TabButton button = (TabButton)var3.next();
         button.setWidth(tabWidth);
      }

      this.layout.arrangeElements();
      this.layout.setX(Mth.roundToward((this.width - tabsWidth) / 2, 2));
      this.layout.setY(0);
   }

   public void selectTab(final int index, final boolean playSound) {
      if (this.isFocused()) {
         this.setFocused((GuiEventListener)this.tabButtons.get(index));
      } else if (((TabButton)this.tabButtons.get(index)).isActive()) {
         this.tabManager.setCurrentTab((Tab)this.tabs.get(index), playSound);
      }

   }

   public void setTabActiveState(final int index, final boolean active) {
      if (index >= 0 && index < this.tabButtons.size()) {
         ((TabButton)this.tabButtons.get(index)).active = active;
      }

   }

   public void setTabTooltip(final int index, final @Nullable Tooltip hint) {
      if (index >= 0 && index < this.tabButtons.size()) {
         ((TabButton)this.tabButtons.get(index)).setTooltip(hint);
      }

   }

   public boolean keyPressed(final KeyEvent event) {
      if (event.hasControlDownWithQuirk()) {
         int tabIndex = this.getNextTabIndex(event);
         if (tabIndex != -1) {
            this.selectTab(Mth.clamp(tabIndex, 0, this.tabs.size() - 1), true);
            return true;
         }
      }

      return false;
   }

   private int getNextTabIndex(final KeyEvent event) {
      return this.getNextTabIndex(this.currentTabIndex(), event);
   }

   private int getNextTabIndex(final int currentTab, final KeyEvent event) {
      int digit = event.getDigit();
      if (digit != -1) {
         return Math.floorMod(digit - 1, 10);
      } else if (event.isCycleFocus() && currentTab != -1) {
         int nextTabIndex = event.hasShiftDown() ? currentTab - 1 : currentTab + 1;
         int index = Math.floorMod(nextTabIndex, this.tabs.size());
         return ((TabButton)this.tabButtons.get(index)).active ? index : this.getNextTabIndex(index, event);
      } else {
         return -1;
      }
   }

   private int currentTabIndex() {
      Tab currentTab = this.tabManager.getCurrentTab();
      int index = this.tabs.indexOf(currentTab);
      return index != -1 ? index : -1;
   }

   private @Nullable TabButton currentTabButton() {
      int index = this.currentTabIndex();
      return index != -1 ? (TabButton)this.tabButtons.get(index) : null;
   }

   public static class Builder {
      private final int width;
      private final TabManager tabManager;
      private final List<Tab> tabs = new ArrayList();

      private Builder(final TabManager tabManager, final int width) {
         super();
         this.tabManager = tabManager;
         this.width = width;
      }

      public Builder addTabs(final Tab... tabs) {
         Collections.addAll(this.tabs, tabs);
         return this;
      }

      public TabNavigationBar build() {
         return new TabNavigationBar(this.width, this.tabManager, this.tabs);
      }
   }
}
