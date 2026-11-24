package net.minecraft.client.gui.components.tabs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.GroupNarration;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationTrigger;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.InputQuirks;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class TabNavigationBar extends AbstractContainerWidget {
   private static final int NO_TAB = -1;
   private static final Component USAGE_NARRATION = Component.translatable("narration.tab_navigation.usage");
   private static final Component USAGE_NARRATION_MAC = Component.translatable("narration.tab_navigation.mac.usage");
   private static final GroupNarration GROUP_NARRATION = GroupNarration.list("narrator.position.tab");
   private static final GroupNarration NAMED_GROUP_NARRATION = GroupNarration.list("narrator.position.tab.index");
   protected final FrameLayout layout;
   private final TabManager tabManager;
   protected final ImmutableList<Tab> tabs;
   protected final ImmutableList<TabButton> tabButtons;

   protected TabNavigationBar(final int x, final int y, final int width, final int height, final LinearLayout.Orientation orientation, final int spacing, final TabManager tabManager, final Map<Align, TabGroup> tabGroups, final Component name) {
      super(x, y, width, height, name);
      this.tabManager = tabManager;
      this.layout = new FrameLayout();
      this.layout.setPosition(x, y);
      this.layout.setMinWidth(width);
      this.layout.setMinHeight(height);
      ImmutableList.Builder<TabButton> tabButtonBuilder = ImmutableList.builder();
      ImmutableList.Builder<Tab> tabsBuilder = ImmutableList.builder();
      boolean horizontal = orientation == LinearLayout.Orientation.HORIZONTAL;

      for(Align alignment : tabGroups.keySet()) {
         TabGroup tabGroup = (TabGroup)tabGroups.get(alignment);
         LinearLayout linearLayout = (new LinearLayout(0, 0, orientation)).spacing(spacing);
         LayoutSettings settings = this.layout.newChildLayoutSettings();
         this.layout.addChild(linearLayout, horizontal ? settings.alignHorizontally(alignment.value) : settings.alignVertically(alignment.value));

         for(TabButton tabButton : tabGroup.tabButtons()) {
            tabButtonBuilder.add(tabButton);
            linearLayout.addChild(tabButton);
         }

         tabsBuilder.addAll(tabGroup.tabs());
      }

      this.tabButtons = tabButtonBuilder.build();
      this.tabs = tabsBuilder.build();
      this.layout.arrangeElements();
   }

   protected int contentHeight() {
      return this.height;
   }

   public static Builder builder(final TabManager tabManager, final int x, final int y, final int width, final int height) {
      return builder(tabManager, x, y, width, height, LinearLayout.Orientation.HORIZONTAL);
   }

   public static Builder builder(final TabManager tabManager, final int x, final int y, final int width, final int height, final LinearLayout.Orientation orientation) {
      return new Builder(tabManager, x, y, width, height, orientation);
   }

   public void arrangeElements(final int width) {
      this.layout.setPosition(this.getX(), this.getY());
      this.layout.arrangeElements();
   }

   public boolean isMouseOver(final double mouseX, final double mouseY) {
      AtomicBoolean mouseOver = new AtomicBoolean();
      this.layout.visitChildren((child) -> {
         if (child.getRectangle().containsPoint((int)mouseX, (int)mouseY)) {
            mouseOver.set(true);
         }

      });
      return mouseOver.get();
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

   public NarratableEntry.NarrationPriority narrationPriority(final NarrationTrigger narrationTrigger) {
      return NarratableEntry.NarrationPriority.of(this.isFocused(), this.getHoveredTabButton() != null, narrationTrigger);
   }

   public Collection<? extends NarratableEntry> getNarratables() {
      return (Collection<? extends NarratableEntry>)(this.currentTabButton() == null && this.getHoveredTabButton() == null ? List.of() : super.getNarratables());
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
      TabButton narrated = (TabButton)getNarratedChild(output.narrationTrigger(), this.getHoveredTabButton(), this.currentTabButton());
      if (narrated != null) {
         if (this.getMessage().equals(CommonComponents.EMPTY)) {
            GROUP_NARRATION.narratePosition(output.nest(), this.tabButtons, narrated);
         } else {
            Component position = NAMED_GROUP_NARRATION.createPositionNarration(this.tabButtons, narrated);
            if (position != null) {
               output.nest().add(NarratedElementType.POSITION, (Component)Component.translatable("narrator.position.tab.named", this.getMessage(), position));
            }
         }

         narrated.updateNarration(output);
      }

      if (this.isFocused()) {
         output.add(NarratedElementType.USAGE, InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY ? USAGE_NARRATION_MAC : USAGE_NARRATION);
      }

   }

   private @Nullable TabButton getHoveredTabButton() {
      UnmodifiableIterator var1 = this.tabButtons.iterator();

      while(var1.hasNext()) {
         TabButton button = (TabButton)var1.next();
         if (button.isHovered()) {
            return button;
         }
      }

      return null;
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics) {
      UnmodifiableIterator var2 = this.tabButtons.iterator();

      while(var2.hasNext()) {
         TabButton value = (TabButton)var2.next();
         value.extractRenderState(graphics);
      }

   }

   public ScreenRectangle getRectangle() {
      return this.layout.getRectangle();
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
            this.selectTab(Math.clamp((long)tabIndex, 0, this.tabs.size() - 1), true);
            return true;
         }
      }

      return false;
   }

   private int getNextTabIndex(final KeyEvent event) {
      return this.getNextTabIndex(this.currentTabIndex(), event);
   }

   private int getNextTabIndex(final int currentTab, final KeyEvent event) {
      OptionalInt numericKeyValue = InputConstants.getKey(event).getNumericKeyValue();
      if (numericKeyValue.isPresent()) {
         return Math.floorMod(numericKeyValue.getAsInt() - 1, 10);
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

   public @Nullable TabButton currentTabButton() {
      int index = this.currentTabIndex();
      return index != -1 ? (TabButton)this.tabButtons.get(index) : null;
   }

   protected static final class TabGroup {
      private final List<TabButton> tabButtons = new ArrayList();
      private final List<Tab> tabs = new ArrayList();

      protected TabGroup() {
         super();
      }

      public void add(final TabButton button, final Tab tab) {
         this.tabButtons.add(button);
         this.tabs.add(tab);
      }

      public List<TabButton> tabButtons() {
         return Collections.unmodifiableList(this.tabButtons);
      }

      public List<Tab> tabs() {
         return Collections.unmodifiableList(this.tabs);
      }
   }

   public static enum Align {
      LEFT_OR_TOP(0.0F),
      CENTER(0.5F),
      RIGHT_OR_BOTTOM(1.0F);

      private final float value;

      private Align(final float value) {
         this.value = value;
      }

      // $FF: synthetic method
      private static Align[] $values() {
         return new Align[]{LEFT_OR_TOP, CENTER, RIGHT_OR_BOTTOM};
      }
   }

   public static class Builder {
      protected final int x;
      protected final int y;
      protected final int width;
      protected final int height;
      protected final LinearLayout.Orientation orientation;
      protected final TabManager tabManager;
      protected final Map<Align, TabGroup> groups = new EnumMap(Align.class);
      protected int spacing;
      protected Component name;

      protected Builder(final TabManager tabManager, final int x, final int y, final int width, final int height, final LinearLayout.Orientation orientation) {
         super();
         this.name = CommonComponents.EMPTY;
         this.tabManager = tabManager;
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.orientation = orientation;
      }

      public Builder spacing(final int spacing) {
         this.spacing = spacing;
         return this;
      }

      public Builder name(final Component name) {
         this.name = name;
         return this;
      }

      public Builder addTabToGroup(final Align align, final TabButton button, final Tab tab) {
         this.group(align).add(button, tab);
         return this;
      }

      public Builder addTab(final TabButton button, final Tab tab) {
         this.group(TabNavigationBar.Align.LEFT_OR_TOP).add(button, tab);
         return this;
      }

      private TabGroup group(final Align align) {
         return (TabGroup)this.groups.computeIfAbsent(align, (var0) -> new TabGroup());
      }

      public TabNavigationBar build() {
         return new TabNavigationBar(this.x, this.y, this.width, this.height, this.orientation, this.spacing, this.tabManager, this.groups, this.name);
      }
   }
}
