package net.minecraft.client.gui.screens.debug;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.debug.DebugEntryEnvironmentAttribute;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.MenuTabBar;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeDebugger;
import net.minecraft.world.attribute.EnvironmentAttributeLayerValues;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class DebugEnvironmentAttributeOptionsScreen extends Screen {
   private static final Component TITLE = Component.literal("Environment Attributes");
   private static final Component ATTRIBUTES_TAB = Component.literal("Attributes");
   private static final Component LAYERS_TAB = Component.literal("Layers");
   private static final Component SEARCH_HINT;
   private static final Component NONE;
   private static final int SPACING = 3;
   private static final int SEARCH_BOX_HEIGHT = 15;
   private static final int ITEM_HEIGHT = 15;
   private static final int LAYER_ITEM_HEIGHT = 25;
   private static final int ROW_WIDTH = 310;
   private static final int ROW_PADDING = 5;
   private static final int SCROLLBAR_MARGIN = 20;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final TabManager tabManager = new TabManager((x$0) -> this.addRenderableWidget(x$0), (x$0) -> this.removeWidget(x$0));
   private final @Nullable Screen parent;
   private @Nullable MenuTabBar tabBar;

   public DebugEnvironmentAttributeOptionsScreen(final @Nullable Screen parent) {
      super(TITLE);
      this.parent = parent;
   }

   public void onClose() {
      this.minecraft.gui.setScreen(this.parent);
   }

   protected void init() {
      this.tabBar = (MenuTabBar)this.addRenderableWidget(MenuTabBar.builder(this.tabManager, this.width).addTab(ATTRIBUTES_TAB, new AttributesTab()).addTab(LAYERS_TAB, new LayersTab()).build());
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (var1) -> this.onClose()).build());
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.tabBar.selectTab(0, false);
      this.repositionElements();
   }

   public void extractBlurredBackground(final GuiGraphicsExtractor graphics) {
      this.minecraft.gui.hud.extractDebugOverlay(graphics);
      super.extractBlurredBackground(graphics);
   }

   protected void repositionElements() {
      if (this.tabBar != null) {
         this.tabBar.arrangeElements(this.width);
         this.layout.setHeaderHeight(this.tabBar.getRectangle().bottom() + 3);
         this.layout.arrangeElements();
         this.tabManager.setTabArea(new ScreenRectangle(0, this.layout.getHeaderHeight(), this.width, this.layout.getContentHeight()));
      }
   }

   public boolean keyPressed(final KeyEvent event) {
      return this.tabBar != null && this.tabBar.keyPressed(event) || super.keyPressed(event);
   }

   private int rowWidth() {
      return Math.min(this.width - 20, 310);
   }

   private Component clip(final String text, final int maxWidth) {
      if (this.font.width(text) <= maxWidth) {
         return Component.literal(text);
      } else {
         String var10000 = this.font.plainSubstrByWidth(text, maxWidth - this.font.width((FormattedText)CommonComponents.ELLIPSIS));
         return Component.literal(var10000 + CommonComponents.ELLIPSIS.getString());
      }
   }

   private static boolean matches(final String text, final String filter) {
      return filter.isEmpty() || text.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT));
   }

   static {
      SEARCH_HINT = Component.literal("Search").withStyle(EditBox.SEARCH_HINT_STYLE);
      NONE = Component.literal("(none)");
   }

   private class AttributesTab extends GridLayoutTab {
      private final AttributeList list;
      private final EditBox search;

      private AttributesTab() {
         Objects.requireNonNull(DebugEnvironmentAttributeOptionsScreen.this);
         super();
         this.list = DebugEnvironmentAttributeOptionsScreen.this.new AttributeList();
         this.search = new EditBox(DebugEnvironmentAttributeOptionsScreen.this.font, 310, 15, DebugEnvironmentAttributeOptionsScreen.SEARCH_HINT);
         this.search.setHint(DebugEnvironmentAttributeOptionsScreen.SEARCH_HINT);
         EditBox var10000 = this.search;
         AttributeList var10001 = this.list;
         Objects.requireNonNull(var10001);
         var10000.setResponder(var10001::filterEntries);
         this.layout.rowSpacing(3);
         this.layout.addChild(this.search, 0, 0, (Consumer)(LayoutSettings::alignHorizontallyCenter));
         this.layout.addChild(this.list, 1, 0);
      }

      public void doLayout(final ScreenRectangle area) {
         this.search.setWidth(DebugEnvironmentAttributeOptionsScreen.this.rowWidth());
         int listTop = area.top() + 15 + 3;
         this.list.updateSizeAndPosition(area.width(), area.height() - 15 - 3, listTop);
         super.doLayout(area);
      }
   }

   private class LayersTab extends GridLayoutTab {
      private final LayerList list;

      private LayersTab() {
         Objects.requireNonNull(DebugEnvironmentAttributeOptionsScreen.this);
         super();
         this.list = DebugEnvironmentAttributeOptionsScreen.this.new LayerList();
         this.layout.addChild(this.list, 0, 0);
      }

      public void doLayout(final ScreenRectangle area) {
         this.list.refreshEntries();
         this.list.updateSizeAndPosition(DebugEnvironmentAttributeOptionsScreen.this.width, area.height(), area.top());
         super.doLayout(area);
      }
   }

   private class AttributeList extends ObjectSelectionList<Entry> {
      private AttributeList() {
         Objects.requireNonNull(DebugEnvironmentAttributeOptionsScreen.this);
         super(DebugEnvironmentAttributeOptionsScreen.this.minecraft, DebugEnvironmentAttributeOptionsScreen.this.width, DebugEnvironmentAttributeOptionsScreen.this.layout.getContentHeight(), DebugEnvironmentAttributeOptionsScreen.this.layout.getHeaderHeight(), 15);
         this.filterEntries("");
         EnvironmentAttribute<?> attribute = DebugEntryEnvironmentAttribute.getAttribute();
         this.setSelected((Entry)this.children().stream().filter((entry) -> Objects.equals(entry.attribute, attribute)).findFirst().orElse((Object)null));
      }

      public int getRowWidth() {
         return DebugEnvironmentAttributeOptionsScreen.this.rowWidth();
      }

      private void filterEntries(final String filter) {
         List<Entry> entries = new ArrayList();
         entries.add(new Entry((EnvironmentAttribute)null, DebugEnvironmentAttributeOptionsScreen.NONE));
         Stream var10000 = BuiltInRegistries.ENVIRONMENT_ATTRIBUTE.listElements().map((holder) -> new Entry((EnvironmentAttribute)holder.value(), Component.literal(holder.key().identifier().toString()))).filter((entry) -> DebugEnvironmentAttributeOptionsScreen.matches(entry.name.getString(), filter)).sorted(Comparator.comparing((entry) -> entry.name.getString()));
         Objects.requireNonNull(entries);
         var10000.forEach(entries::add);
         this.replaceEntries(entries);
         this.refreshScrollAmount();
      }

      public void setSelected(final Entry entry) {
         super.setSelected(entry);
         if (entry != null) {
            DebugEntryEnvironmentAttribute.setAttribute(entry.attribute);
         }

      }

      private class Entry extends ObjectSelectionList.Entry<Entry> {
         private final @Nullable EnvironmentAttribute<?> attribute;
         private final Component name;

         private Entry(final EnvironmentAttribute<?> attribute, final Component name) {
            Objects.requireNonNull(AttributeList.this);
            super();
            this.attribute = attribute;
            this.name = name;
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", this.name);
         }

         public void extractContent(final GuiGraphicsExtractor graphics, final boolean hovered) {
            graphics.text(DebugEnvironmentAttributeOptionsScreen.this.font, (Component)DebugEnvironmentAttributeOptionsScreen.this.clip(this.name.getString(), this.getContentWidth() - 10), this.getContentX() + 5, this.getContentY() + 2, -1);
         }

         public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
            AttributeList.this.setSelected(this);
            return super.mouseClicked(event, doubleClick);
         }
      }
   }

   private class LayerList extends ContainerObjectSelectionList<Entry> {
      private LayerList() {
         Objects.requireNonNull(DebugEnvironmentAttributeOptionsScreen.this);
         super(DebugEnvironmentAttributeOptionsScreen.this.minecraft, DebugEnvironmentAttributeOptionsScreen.this.width, DebugEnvironmentAttributeOptionsScreen.this.layout.getContentHeight(), DebugEnvironmentAttributeOptionsScreen.this.layout.getHeaderHeight(), 25);
      }

      public int getRowWidth() {
         return DebugEnvironmentAttributeOptionsScreen.this.rowWidth();
      }

      private void refreshEntries() {
         List<Entry> entries = new ArrayList();
         EnvironmentAttribute<?> environmentAttribute = DebugEntryEnvironmentAttribute.getAttribute();
         if (this.minecraft.level != null && environmentAttribute != null) {
            EnvironmentAttributeLayerValues layerValues = EnvironmentAttributeDebugger.getValuePerLayer(this.minecraft.level, environmentAttribute, this.minecraft.gameRenderer.mainCamera().position());

            for(EnvironmentAttributeLayerValues.LayerValue layer : layerValues.layers()) {
               if (layer.layerIndex() >= 0) {
                  entries.add(new Entry(environmentAttribute, layer));
               }
            }
         }

         this.replaceEntries(entries);
         this.refreshScrollAmount();
      }

      private class Entry extends ContainerObjectSelectionList.Entry<Entry> {
         private final CycleButton<Boolean> toggle;

         private Entry(final EnvironmentAttribute<?> attribute, final EnvironmentAttributeLayerValues.LayerValue layer) {
            Objects.requireNonNull(LayerList.this);
            super();
            CycleButton.Builder var10001 = CycleButton.onOffBuilder(!layer.disabled());
            int var10006 = layer.layerIndex();
            this.toggle = var10001.create(0, 0, 310, 20, Component.literal(var10006 + ". " + layer.kind()), (var3, enabled) -> EnvironmentAttributeDebugger.setLayerDisabled((Level)Objects.requireNonNull(LayerList.this.minecraft.level), attribute, layer.layerIndex(), !enabled));
         }

         public void extractContent(final GuiGraphicsExtractor graphics, final boolean hovered) {
            this.toggle.setRectangle(this.getContentWidth(), 20, this.getContentX(), this.getContentY());
            this.toggle.extractRenderState(graphics);
         }

         public List<? extends GuiEventListener> children() {
            return List.of(this.toggle);
         }

         public List<? extends NarratableEntry> narratables() {
            return List.of(this.toggle);
         }
      }
   }
}
