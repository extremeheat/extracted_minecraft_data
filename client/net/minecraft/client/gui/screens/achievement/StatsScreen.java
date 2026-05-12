package net.minecraft.client.gui.screens.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ItemDisplayWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.LoadingTab;
import net.minecraft.client.gui.components.tabs.MenuTabBar;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public class StatsScreen extends Screen {
   private static final Component TITLE = Component.translatable("gui.stats");
   private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");
   private static final Identifier HEADER_SPRITE = Identifier.withDefaultNamespace("statistics/header");
   private static final Identifier SORT_UP_SPRITE = Identifier.withDefaultNamespace("statistics/sort_up");
   private static final Identifier SORT_DOWN_SPRITE = Identifier.withDefaultNamespace("statistics/sort_down");
   private static final Component PENDING_TEXT = Component.translatable("multiplayer.downloadingStats");
   private static final Component NO_VALUE_DISPLAY = Component.translatable("stats.none");
   private static final Component GENERAL_BUTTON = Component.translatable("stat.generalButton");
   private static final Component ITEMS_BUTTON = Component.translatable("stat.itemsButton");
   private static final Component MOBS_BUTTON = Component.translatable("stat.mobsButton");
   protected final Screen lastScreen;
   private static final int LIST_WIDTH = 280;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final TabManager tabManager = new TabManager((x$0) -> this.addRenderableWidget(x$0), (x$0) -> this.removeWidget(x$0));
   private @Nullable MenuTabBar tabNavigationBar;
   private final StatsCounter stats;
   private boolean isLoading = true;

   public StatsScreen(final Screen lastScreen, final StatsCounter stats) {
      super(TITLE);
      this.lastScreen = lastScreen;
      this.stats = stats;
   }

   protected void init() {
      Component loadingTitle = PENDING_TEXT;
      this.tabNavigationBar = MenuTabBar.builder(this.tabManager, this.width).addTabs(new LoadingTab(this.getFont(), GENERAL_BUTTON, loadingTitle), new LoadingTab(this.getFont(), ITEMS_BUTTON, loadingTitle), new LoadingTab(this.getFont(), MOBS_BUTTON, loadingTitle)).build();
      this.addRenderableWidget(this.tabNavigationBar);
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).width(200).build());
      this.tabNavigationBar.setTabActiveState(0, true);
      this.tabNavigationBar.setTabActiveState(1, false);
      this.tabNavigationBar.setTabActiveState(2, false);
      this.layout.visitWidgets((button) -> {
         button.setTabOrderGroup(1);
         this.addRenderableWidget(button);
      });
      this.tabNavigationBar.selectTab(0, false);
      this.repositionElements();
      this.minecraft.getConnection().send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
   }

   public void onStatsUpdated() {
      if (this.isLoading) {
         if (this.tabNavigationBar != null) {
            this.removeWidget(this.tabNavigationBar);
         }

         this.tabNavigationBar = MenuTabBar.builder(this.tabManager, this.width).addTabs(new StatisticsTab(GENERAL_BUTTON, new GeneralStatisticsList(this.minecraft)), new StatisticsTab(ITEMS_BUTTON, new ItemStatisticsList(this.minecraft)), new StatisticsTab(MOBS_BUTTON, new MobsStatisticsList(this.minecraft))).build();
         this.setFocused(this.tabNavigationBar);
         this.addRenderableWidget(this.tabNavigationBar);
         this.setTabActiveStateAndTooltip(1);
         this.setTabActiveStateAndTooltip(2);
         this.tabNavigationBar.selectTab(0, false);
         this.repositionElements();
         this.isLoading = false;
      }

   }

   private void setTabActiveStateAndTooltip(final int index) {
      if (this.tabNavigationBar != null) {
         boolean var10000;
         label20: {
            Object var4 = this.tabNavigationBar.getTabs().get(index);
            if (var4 instanceof StatisticsTab) {
               StatisticsTab statsTab = (StatisticsTab)var4;
               if (!statsTab.list.children().isEmpty()) {
                  var10000 = true;
                  break label20;
               }
            }

            var10000 = false;
         }

         boolean active = var10000;
         this.tabNavigationBar.setTabActiveState(index, active);
         if (active) {
            this.tabNavigationBar.setTabTooltip(index, (Tooltip)null);
         } else {
            this.tabNavigationBar.setTabTooltip(index, Tooltip.create(Component.translatable("gui.stats.none_found")));
         }

      }
   }

   protected void repositionElements() {
      if (this.tabNavigationBar != null) {
         this.tabNavigationBar.arrangeElements(this.width);
         int tabAreaTop = this.tabNavigationBar.getRectangle().bottom();
         ScreenRectangle tabArea = new ScreenRectangle(0, tabAreaTop, this.width, this.height - this.layout.getFooterHeight() - tabAreaTop);
         this.tabNavigationBar.getTabs().forEach((tab) -> tab.visitChildren((child) -> child.setHeight(tabArea.height())));
         this.tabManager.setTabArea(tabArea);
         this.layout.setHeaderHeight(tabAreaTop);
         this.layout.arrangeElements();
      }
   }

   public boolean keyPressed(final KeyEvent event) {
      return this.tabNavigationBar != null && this.tabNavigationBar.keyPressed(event) ? true : super.keyPressed(event);
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int xm, final int ym, final float a) {
      super.extractRenderState(graphics, xm, ym, a);
      graphics.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR, 0, this.height - this.layout.getFooterHeight(), 0.0F, 0.0F, this.width, 2, 32, 2);
   }

   protected void extractMenuBackground(final GuiGraphicsExtractor graphics) {
      graphics.blit(RenderPipelines.GUI_TEXTURED, CreateWorldScreen.TAB_HEADER_BACKGROUND, 0, 0, 0.0F, 0.0F, this.width, this.layout.getHeaderHeight(), 16, 16);
      this.extractMenuBackground(graphics, 0, this.layout.getHeaderHeight(), this.width, this.height);
   }

   public void onClose() {
      this.minecraft.gui.setScreen(this.lastScreen);
   }

   private static String getTranslationKey(final Stat<Identifier> stat) {
      String var10000 = ((Identifier)stat.getValue()).toString();
      return "stat." + var10000.replace(':', '.');
   }

   private class StatisticsTab extends GridLayoutTab {
      protected final AbstractSelectionList<?> list;

      public StatisticsTab(final Component title, final AbstractSelectionList<?> list) {
         Objects.requireNonNull(StatsScreen.this);
         super(title);
         this.layout.addChild(list, 1, 1);
         this.list = list;
      }

      public void doLayout(final ScreenRectangle screenRectangle) {
         this.list.updateSizeAndPosition(StatsScreen.this.width, StatsScreen.this.layout.getContentHeight(), StatsScreen.this.layout.getHeaderHeight());
         super.doLayout(screenRectangle);
      }
   }

   private class GeneralStatisticsList extends ObjectSelectionList<Entry> {
      public GeneralStatisticsList(final Minecraft minecraft) {
         Objects.requireNonNull(StatsScreen.this);
         super(minecraft, StatsScreen.this.width, StatsScreen.this.layout.getContentHeight(), 33, 14);
         ObjectArrayList<Stat<Identifier>> stats = new ObjectArrayList(Stats.CUSTOM.iterator());
         stats.sort(Comparator.comparing((k) -> I18n.get(StatsScreen.getTranslationKey(k))));
         ObjectListIterator var4 = stats.iterator();

         while(var4.hasNext()) {
            Stat<Identifier> stat = (Stat)var4.next();
            this.addEntry(new Entry(stat));
         }

      }

      public int getRowWidth() {
         return 280;
      }

      protected void extractListBackground(final GuiGraphicsExtractor graphics) {
      }

      protected void extractListSeparators(final GuiGraphicsExtractor graphics) {
      }

      private class Entry extends ObjectSelectionList.Entry<Entry> {
         private final Stat<Identifier> stat;
         private final Component statDisplay;

         private Entry(final Stat<Identifier> stat) {
            Objects.requireNonNull(GeneralStatisticsList.this);
            super();
            this.stat = stat;
            this.statDisplay = Component.translatable(StatsScreen.getTranslationKey(stat));
         }

         private String getValueText() {
            return this.stat.format(StatsScreen.this.stats.getValue(this.stat));
         }

         public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
            int var10000 = this.getContentYMiddle();
            Objects.requireNonNull(StatsScreen.this.font);
            int y = var10000 - 9 / 2;
            int index = GeneralStatisticsList.this.children().indexOf(this);
            int color = index % 2 == 0 ? -1 : -4539718;
            graphics.text(StatsScreen.this.font, this.statDisplay, this.getContentX() + 2, y, color);
            String msg = this.getValueText();
            graphics.text(StatsScreen.this.font, msg, this.getContentRight() - StatsScreen.this.font.width(msg) - 4, y, color);
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", Component.empty().append(this.statDisplay).append(CommonComponents.SPACE).append(this.getValueText()));
         }
      }
   }

   private class ItemStatisticsList extends ContainerObjectSelectionList<Entry> {
      private static final int SLOT_BG_SIZE = 18;
      private static final int SLOT_STAT_HEIGHT = 22;
      private static final int SLOT_BG_Y = 1;
      private static final int SORT_NONE = 0;
      private static final int SORT_DOWN = -1;
      private static final int SORT_UP = 1;
      protected final List<StatType<Block>> blockColumns;
      protected final List<StatType<Item>> itemColumns;
      protected final Comparator<ItemRow> itemStatSorter;
      protected @Nullable StatType<?> sortColumn;
      protected int sortOrder;

      public ItemStatisticsList(final Minecraft minecraft) {
         Objects.requireNonNull(StatsScreen.this);
         super(minecraft, StatsScreen.this.width, StatsScreen.this.layout.getContentHeight(), 33, 22);
         this.itemStatSorter = new ItemRowComparator();
         this.blockColumns = Lists.newArrayList();
         this.blockColumns.add(Stats.BLOCK_MINED);
         this.itemColumns = Lists.newArrayList(new StatType[]{Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED});
         Set<Item> items = Sets.newIdentityHashSet();

         for(Item item : BuiltInRegistries.ITEM) {
            boolean addToList = false;

            for(StatType<Item> type : this.itemColumns) {
               if (type.contains(item) && StatsScreen.this.stats.getValue(type.get(item)) > 0) {
                  addToList = true;
               }
            }

            if (addToList) {
               items.add(item);
            }
         }

         for(Block block : BuiltInRegistries.BLOCK) {
            boolean addToList = false;

            for(StatType<Block> type : this.blockColumns) {
               if (type.contains(block) && StatsScreen.this.stats.getValue(type.get(block)) > 0) {
                  addToList = true;
               }
            }

            if (addToList) {
               items.add(block.asItem());
            }
         }

         items.remove(Items.AIR);
         if (!items.isEmpty()) {
            this.addEntry(new HeaderEntry());

            for(Item item : items) {
               this.addEntry(new ItemRow(item));
            }
         }

      }

      protected void extractListBackground(final GuiGraphicsExtractor graphics) {
      }

      private int getColumnX(final int col) {
         return 75 + 40 * col;
      }

      public int getRowWidth() {
         return 280;
      }

      private StatType<?> getColumn(final int i) {
         return i < this.blockColumns.size() ? (StatType)this.blockColumns.get(i) : (StatType)this.itemColumns.get(i - this.blockColumns.size());
      }

      private int getColumnIndex(final StatType<?> column) {
         int i = this.blockColumns.indexOf(column);
         if (i >= 0) {
            return i;
         } else {
            int j = this.itemColumns.indexOf(column);
            return j >= 0 ? j + this.blockColumns.size() : -1;
         }
      }

      protected void sortByColumn(final StatType<?> column) {
         if (column != this.sortColumn) {
            this.sortColumn = column;
            this.sortOrder = -1;
         } else if (this.sortOrder == -1) {
            this.sortOrder = 1;
         } else {
            this.sortColumn = null;
            this.sortOrder = 0;
         }

         this.sortItems(this.itemStatSorter);
      }

      protected void sortItems(final Comparator<ItemRow> comparator) {
         List<ItemRow> itemRows = this.getItemRows();
         itemRows.sort(comparator);
         this.clearEntriesExcept((Entry)this.children().getFirst());

         for(ItemRow newChild : itemRows) {
            this.addEntry(newChild);
         }

      }

      private List<ItemRow> getItemRows() {
         List<ItemRow> itemRows = new ArrayList();
         this.children().forEach((entry) -> {
            if (entry instanceof ItemRow itemRow) {
               itemRows.add(itemRow);
            }

         });
         return itemRows;
      }

      protected void extractListSeparators(final GuiGraphicsExtractor graphics) {
      }

      private class ItemRowComparator implements Comparator<ItemRow> {
         private ItemRowComparator() {
            Objects.requireNonNull(ItemStatisticsList.this);
            super();
         }

         public int compare(final ItemRow one, final ItemRow two) {
            Item item1 = one.getItem();
            Item item2 = two.getItem();
            int key1;
            int key2;
            if (ItemStatisticsList.this.sortColumn == null) {
               key1 = 0;
               key2 = 0;
            } else if (ItemStatisticsList.this.blockColumns.contains(ItemStatisticsList.this.sortColumn)) {
               StatType<Block> type = ItemStatisticsList.this.sortColumn;
               int var10000;
               if (item1 instanceof BlockItem) {
                  BlockItem blockItem = (BlockItem)item1;
                  var10000 = StatsScreen.this.stats.getValue(type, blockItem.getBlock());
               } else {
                  var10000 = -1;
               }

               key1 = var10000;
               if (item2 instanceof BlockItem) {
                  BlockItem blockItem = (BlockItem)item2;
                  var10000 = StatsScreen.this.stats.getValue(type, blockItem.getBlock());
               } else {
                  var10000 = -1;
               }

               key2 = var10000;
            } else {
               StatType<Item> type = ItemStatisticsList.this.sortColumn;
               key1 = StatsScreen.this.stats.getValue(type, item1);
               key2 = StatsScreen.this.stats.getValue(type, item2);
            }

            return key1 == key2 ? ItemStatisticsList.this.sortOrder * Integer.compare(Item.getId(item1), Item.getId(item2)) : ItemStatisticsList.this.sortOrder * Integer.compare(key1, key2);
         }
      }

      private abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
         private Entry() {
            super();
         }
      }

      private class ItemRow extends Entry {
         private final Item item;
         private final ItemRowWidget itemRowWidget;

         private ItemRow(final Item item) {
            Objects.requireNonNull(ItemStatisticsList.this);
            super();
            this.item = item;
            this.itemRowWidget = new ItemRowWidget(item.getDefaultInstance());
         }

         protected Item getItem() {
            return this.item;
         }

         public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
            this.itemRowWidget.setPosition(this.getContentX(), this.getContentY());
            this.itemRowWidget.extractRenderState(graphics, mouseX, mouseY, a);
            ItemStatisticsList itemStatsList = ItemStatisticsList.this;
            int index = itemStatsList.children().indexOf(this);

            for(int col = 0; col < itemStatsList.blockColumns.size(); ++col) {
               Item var11 = this.item;
               Stat<Block> stat;
               if (var11 instanceof BlockItem blockItem) {
                  stat = ((StatType)itemStatsList.blockColumns.get(col)).get(blockItem.getBlock());
               } else {
                  stat = null;
               }

               int var10003 = this.getContentX() + ItemStatisticsList.this.getColumnX(col);
               int var10004 = this.getContentYMiddle();
               Objects.requireNonNull(StatsScreen.this.font);
               this.extractStat(graphics, stat, var10003, var10004 - 9 / 2, index % 2 == 0);
            }

            for(int col = 0; col < itemStatsList.itemColumns.size(); ++col) {
               Stat var10002 = ((StatType)itemStatsList.itemColumns.get(col)).get(this.item);
               int var13 = this.getContentX() + ItemStatisticsList.this.getColumnX(col + itemStatsList.blockColumns.size());
               int var14 = this.getContentYMiddle();
               Objects.requireNonNull(StatsScreen.this.font);
               this.extractStat(graphics, var10002, var13, var14 - 9 / 2, index % 2 == 0);
            }

         }

         protected void extractStat(final GuiGraphicsExtractor graphics, final @Nullable Stat<?> stat, final int x, final int y, final boolean shaded) {
            Component msg = (Component)(stat == null ? StatsScreen.NO_VALUE_DISPLAY : Component.literal(stat.format(StatsScreen.this.stats.getValue(stat))));
            graphics.text(StatsScreen.this.font, msg, x - StatsScreen.this.font.width((FormattedText)msg), y, shaded ? -1 : -4539718);
         }

         public List<? extends NarratableEntry> narratables() {
            return List.of(this.itemRowWidget);
         }

         public List<? extends GuiEventListener> children() {
            return List.of(this.itemRowWidget);
         }

         private class ItemRowWidget extends ItemDisplayWidget {
            private ItemRowWidget(final ItemStack itemStack) {
               Objects.requireNonNull(ItemRow.this);
               super(ItemStatisticsList.this.minecraft, 1, 1, 18, 18, itemStack.getHoverName(), itemStack, false, true);
            }

            protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)StatsScreen.SLOT_SPRITE, ItemRow.this.getContentX(), ItemRow.this.getContentY(), 18, 18);
               super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
            }

            protected void extractTooltip(final GuiGraphicsExtractor graphics, final int x, final int y) {
               super.extractTooltip(graphics, ItemRow.this.getContentX() + 18, ItemRow.this.getContentY() + 18);
            }
         }
      }

      private class HeaderEntry extends Entry {
         private static final Identifier BLOCK_MINED_SPRITE = Identifier.withDefaultNamespace("statistics/block_mined");
         private static final Identifier ITEM_BROKEN_SPRITE = Identifier.withDefaultNamespace("statistics/item_broken");
         private static final Identifier ITEM_CRAFTED_SPRITE = Identifier.withDefaultNamespace("statistics/item_crafted");
         private static final Identifier ITEM_USED_SPRITE = Identifier.withDefaultNamespace("statistics/item_used");
         private static final Identifier ITEM_PICKED_UP_SPRITE = Identifier.withDefaultNamespace("statistics/item_picked_up");
         private static final Identifier ITEM_DROPPED_SPRITE = Identifier.withDefaultNamespace("statistics/item_dropped");
         private final StatSortButton blockMined;
         private final StatSortButton itemBroken;
         private final StatSortButton itemCrafted;
         private final StatSortButton itemUsed;
         private final StatSortButton itemPickedUp;
         private final StatSortButton itemDropped;
         private final List<AbstractWidget> children;

         private HeaderEntry() {
            Objects.requireNonNull(ItemStatisticsList.this);
            super();
            this.children = new ArrayList();
            this.blockMined = new StatSortButton(0, BLOCK_MINED_SPRITE);
            this.itemBroken = new StatSortButton(1, ITEM_BROKEN_SPRITE);
            this.itemCrafted = new StatSortButton(2, ITEM_CRAFTED_SPRITE);
            this.itemUsed = new StatSortButton(3, ITEM_USED_SPRITE);
            this.itemPickedUp = new StatSortButton(4, ITEM_PICKED_UP_SPRITE);
            this.itemDropped = new StatSortButton(5, ITEM_DROPPED_SPRITE);
            this.children.addAll(List.of(this.blockMined, this.itemBroken, this.itemCrafted, this.itemUsed, this.itemPickedUp, this.itemDropped));
         }

         public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
            this.blockMined.setPosition(this.getContentX() + ItemStatisticsList.this.getColumnX(0) - 18, this.getContentY() + 1);
            this.blockMined.extractRenderState(graphics, mouseX, mouseY, a);
            this.itemBroken.setPosition(this.getContentX() + ItemStatisticsList.this.getColumnX(1) - 18, this.getContentY() + 1);
            this.itemBroken.extractRenderState(graphics, mouseX, mouseY, a);
            this.itemCrafted.setPosition(this.getContentX() + ItemStatisticsList.this.getColumnX(2) - 18, this.getContentY() + 1);
            this.itemCrafted.extractRenderState(graphics, mouseX, mouseY, a);
            this.itemUsed.setPosition(this.getContentX() + ItemStatisticsList.this.getColumnX(3) - 18, this.getContentY() + 1);
            this.itemUsed.extractRenderState(graphics, mouseX, mouseY, a);
            this.itemPickedUp.setPosition(this.getContentX() + ItemStatisticsList.this.getColumnX(4) - 18, this.getContentY() + 1);
            this.itemPickedUp.extractRenderState(graphics, mouseX, mouseY, a);
            this.itemDropped.setPosition(this.getContentX() + ItemStatisticsList.this.getColumnX(5) - 18, this.getContentY() + 1);
            this.itemDropped.extractRenderState(graphics, mouseX, mouseY, a);
            if (ItemStatisticsList.this.sortColumn != null) {
               int offset = ItemStatisticsList.this.getColumnX(ItemStatisticsList.this.getColumnIndex(ItemStatisticsList.this.sortColumn)) - 36;
               Identifier sprite = ItemStatisticsList.this.sortOrder == 1 ? StatsScreen.SORT_UP_SPRITE : StatsScreen.SORT_DOWN_SPRITE;
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)sprite, this.getContentX() + offset, this.getContentY() + 1, 18, 18);
            }

         }

         public List<? extends GuiEventListener> children() {
            return this.children;
         }

         public List<? extends NarratableEntry> narratables() {
            return this.children;
         }

         private class StatSortButton extends ImageButton {
            private final Identifier sprite;

            private StatSortButton(final int column, final Identifier sprite) {
               Objects.requireNonNull(HeaderEntry.this);
               super(18, 18, new WidgetSprites(StatsScreen.HEADER_SPRITE, StatsScreen.SLOT_SPRITE), (button) -> ItemStatisticsList.this.sortByColumn(ItemStatisticsList.this.getColumn(column)), ItemStatisticsList.this.getColumn(column).getDisplayName());
               this.sprite = sprite;
               this.setTooltip(Tooltip.create(this.getMessage()));
            }

            public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
               Identifier background = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, background, this.getX(), this.getY(), this.width, this.height);
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, this.getX(), this.getY(), this.width, this.height);
            }
         }
      }
   }

   private class MobsStatisticsList extends ObjectSelectionList<MobRow> {
      public MobsStatisticsList(final Minecraft minecraft) {
         Objects.requireNonNull(StatsScreen.this);
         int var10002 = StatsScreen.this.width;
         int var10003 = StatsScreen.this.layout.getContentHeight();
         Objects.requireNonNull(StatsScreen.this.font);
         super(minecraft, var10002, var10003, 33, 9 * 4);

         for(EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            if (StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(type)) > 0 || StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(type)) > 0) {
               this.addEntry(new MobRow(type));
            }
         }

      }

      public int getRowWidth() {
         return 280;
      }

      protected void extractListBackground(final GuiGraphicsExtractor graphics) {
      }

      protected void extractListSeparators(final GuiGraphicsExtractor graphics) {
      }

      private class MobRow extends ObjectSelectionList.Entry<MobRow> {
         private final Component mobName;
         private final Component kills;
         private final Component killedBy;
         private final boolean hasKills;
         private final boolean wasKilledBy;

         public MobRow(final EntityType<?> type) {
            Objects.requireNonNull(MobsStatisticsList.this);
            super();
            this.mobName = type.getDescription();
            int kills = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(type));
            if (kills == 0) {
               this.kills = Component.translatable("stat_type.minecraft.killed.none", this.mobName);
               this.hasKills = false;
            } else {
               this.kills = Component.translatable("stat_type.minecraft.killed", kills, this.mobName);
               this.hasKills = true;
            }

            int killedBy = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(type));
            if (killedBy == 0) {
               this.killedBy = Component.translatable("stat_type.minecraft.killed_by.none", this.mobName);
               this.wasKilledBy = false;
            } else {
               this.killedBy = Component.translatable("stat_type.minecraft.killed_by", this.mobName, killedBy);
               this.wasKilledBy = true;
            }

         }

         public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
            graphics.text(StatsScreen.this.font, (Component)this.mobName, this.getContentX() + 2, this.getContentY() + 1, -1);
            Font var10001 = StatsScreen.this.font;
            Component var10002 = this.kills;
            int var10003 = this.getContentX() + 2 + 10;
            int var10004 = this.getContentY() + 1;
            Objects.requireNonNull(StatsScreen.this.font);
            graphics.text(var10001, var10002, var10003, var10004 + 9, this.hasKills ? -4539718 : -8355712);
            var10001 = StatsScreen.this.font;
            var10002 = this.killedBy;
            var10003 = this.getContentX() + 2 + 10;
            var10004 = this.getContentY() + 1;
            Objects.requireNonNull(StatsScreen.this.font);
            graphics.text(var10001, var10002, var10003, var10004 + 9 * 2, this.wasKilledBy ? -4539718 : -8355712);
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", CommonComponents.joinForNarration(this.kills, this.killedBy));
         }
      }
   }
}
