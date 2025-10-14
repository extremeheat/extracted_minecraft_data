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
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
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
import net.minecraft.resources.ResourceLocation;
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

public class StatsScreen extends Screen {
   private static final Component TITLE = Component.translatable("gui.stats");
   static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot");
   static final ResourceLocation HEADER_SPRITE = ResourceLocation.withDefaultNamespace("statistics/header");
   static final ResourceLocation SORT_UP_SPRITE = ResourceLocation.withDefaultNamespace("statistics/sort_up");
   static final ResourceLocation SORT_DOWN_SPRITE = ResourceLocation.withDefaultNamespace("statistics/sort_down");
   private static final Component PENDING_TEXT = Component.translatable("multiplayer.downloadingStats");
   static final Component NO_VALUE_DISPLAY = Component.translatable("stats.none");
   private static final Component GENERAL_BUTTON = Component.translatable("stat.generalButton");
   private static final Component ITEMS_BUTTON = Component.translatable("stat.itemsButton");
   private static final Component MOBS_BUTTON = Component.translatable("stat.mobsButton");
   protected final Screen lastScreen;
   private static final int LIST_WIDTH = 280;
   final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final TabManager tabManager = new TabManager((var1x) -> {
      AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
   }, (var1x) -> this.removeWidget(var1x));
   @Nullable
   private TabNavigationBar tabNavigationBar;
   final StatsCounter stats;
   private boolean isLoading = true;

   public StatsScreen(Screen var1, StatsCounter var2) {
      super(TITLE);
      this.lastScreen = var1;
      this.stats = var2;
   }

   protected void init() {
      Component var1 = PENDING_TEXT;
      this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width).addTabs(new LoadingTab(this.getFont(), GENERAL_BUTTON, var1), new LoadingTab(this.getFont(), ITEMS_BUTTON, var1), new LoadingTab(this.getFont(), MOBS_BUTTON, var1)).build();
      this.addRenderableWidget(this.tabNavigationBar);
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (var1x) -> this.onClose()).width(200).build());
      this.tabNavigationBar.setTabActiveState(0, true);
      this.tabNavigationBar.setTabActiveState(1, false);
      this.tabNavigationBar.setTabActiveState(2, false);
      this.layout.visitWidgets((var1x) -> {
         var1x.setTabOrderGroup(1);
         this.addRenderableWidget(var1x);
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

         this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width).addTabs(new StatisticsTab(GENERAL_BUTTON, new GeneralStatisticsList(this.minecraft)), new StatisticsTab(ITEMS_BUTTON, new ItemStatisticsList(this.minecraft)), new StatisticsTab(MOBS_BUTTON, new MobsStatisticsList(this.minecraft))).build();
         this.setFocused(this.tabNavigationBar);
         this.addRenderableWidget(this.tabNavigationBar);
         this.setTabActiveStateAndTooltip(1);
         this.setTabActiveStateAndTooltip(2);
         this.tabNavigationBar.selectTab(0, false);
         this.repositionElements();
         this.isLoading = false;
      }

   }

   private void setTabActiveStateAndTooltip(int var1) {
      if (this.tabNavigationBar != null) {
         boolean var10000;
         label20: {
            Object var4 = this.tabNavigationBar.getTabs().get(var1);
            if (var4 instanceof StatisticsTab) {
               StatisticsTab var3 = (StatisticsTab)var4;
               if (!var3.list.children().isEmpty()) {
                  var10000 = true;
                  break label20;
               }
            }

            var10000 = false;
         }

         boolean var2 = var10000;
         this.tabNavigationBar.setTabActiveState(var1, var2);
         if (var2) {
            this.tabNavigationBar.setTabTooltip(var1, (Tooltip)null);
         } else {
            this.tabNavigationBar.setTabTooltip(var1, Tooltip.create(Component.translatable("gui.stats.none_found")));
         }

      }
   }

   protected void repositionElements() {
      if (this.tabNavigationBar != null) {
         this.tabNavigationBar.setWidth(this.width);
         this.tabNavigationBar.arrangeElements();
         int var1 = this.tabNavigationBar.getRectangle().bottom();
         ScreenRectangle var2 = new ScreenRectangle(0, var1, this.width, this.height - this.layout.getFooterHeight() - var1);
         this.tabNavigationBar.getTabs().forEach((var1x) -> var1x.visitChildren((var1) -> var1.setHeight(var2.height())));
         this.tabManager.setTabArea(var2);
         this.layout.setHeaderHeight(var1);
         this.layout.arrangeElements();
      }
   }

   public boolean keyPressed(KeyEvent var1) {
      return this.tabNavigationBar != null && this.tabNavigationBar.keyPressed(var1) ? true : super.keyPressed(var1);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR, 0, this.height - this.layout.getFooterHeight(), 0.0F, 0.0F, this.width, 2, 32, 2);
   }

   protected void renderMenuBackground(GuiGraphics var1) {
      var1.blit(RenderPipelines.GUI_TEXTURED, CreateWorldScreen.TAB_HEADER_BACKGROUND, 0, 0, 0.0F, 0.0F, this.width, this.layout.getHeaderHeight(), 16, 16);
      this.renderMenuBackground(var1, 0, this.layout.getHeaderHeight(), this.width, this.height);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   static String getTranslationKey(Stat<ResourceLocation> var0) {
      String var10000 = ((ResourceLocation)var0.getValue()).toString();
      return "stat." + var10000.replace(':', '.');
   }

   class StatisticsTab extends GridLayoutTab {
      protected final AbstractSelectionList<?> list;

      public StatisticsTab(final Component var2, final AbstractSelectionList<?> var3) {
         super(var2);
         this.layout.addChild(var3, 1, 1);
         this.list = var3;
      }

      public void doLayout(ScreenRectangle var1) {
         this.list.updateSizeAndPosition(StatsScreen.this.width, StatsScreen.this.layout.getContentHeight(), StatsScreen.this.layout.getHeaderHeight());
         super.doLayout(var1);
      }
   }

   class GeneralStatisticsList extends ObjectSelectionList<Entry> {
      public GeneralStatisticsList(final Minecraft var2) {
         super(var2, StatsScreen.this.width, StatsScreen.this.layout.getContentHeight(), 33, 14);
         ObjectArrayList var3 = new ObjectArrayList(Stats.CUSTOM.iterator());
         var3.sort(Comparator.comparing((var0) -> I18n.get(StatsScreen.getTranslationKey(var0))));
         ObjectListIterator var4 = var3.iterator();

         while(var4.hasNext()) {
            Stat var5 = (Stat)var4.next();
            this.addEntry(new Entry(var5));
         }

      }

      public int getRowWidth() {
         return 280;
      }

      protected void renderListBackground(GuiGraphics var1) {
      }

      protected void renderListSeparators(GuiGraphics var1) {
      }

      class Entry extends ObjectSelectionList.Entry<Entry> {
         private final Stat<ResourceLocation> stat;
         private final Component statDisplay;

         Entry(final Stat<ResourceLocation> var2) {
            super();
            this.stat = var2;
            this.statDisplay = Component.translatable(StatsScreen.getTranslationKey(var2));
         }

         private String getValueText() {
            return this.stat.format(StatsScreen.this.stats.getValue(this.stat));
         }

         public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
            int var10000 = this.getContentYMiddle();
            Objects.requireNonNull(StatsScreen.this.font);
            int var6 = var10000 - 9 / 2;
            int var7 = GeneralStatisticsList.this.children().indexOf(this);
            int var8 = var7 % 2 == 0 ? -1 : -4539718;
            var1.drawString(StatsScreen.this.font, this.statDisplay, this.getContentX() + 2, var6, var8);
            String var9 = this.getValueText();
            var1.drawString(StatsScreen.this.font, var9, this.getContentRight() - StatsScreen.this.font.width(var9) - 4, var6, var8);
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", Component.empty().append(this.statDisplay).append(CommonComponents.SPACE).append(this.getValueText()));
         }
      }
   }

   class ItemStatisticsList extends ContainerObjectSelectionList<Entry> {
      private static final int SLOT_BG_SIZE = 18;
      private static final int SLOT_STAT_HEIGHT = 22;
      private static final int SLOT_BG_Y = 1;
      private static final int SORT_NONE = 0;
      private static final int SORT_DOWN = -1;
      private static final int SORT_UP = 1;
      protected final List<StatType<Block>> blockColumns = Lists.newArrayList();
      protected final List<StatType<Item>> itemColumns;
      protected final Comparator<ItemRow> itemStatSorter = new ItemRowComparator();
      @Nullable
      protected StatType<?> sortColumn;
      protected int sortOrder;

      public ItemStatisticsList(final Minecraft var2) {
         super(var2, StatsScreen.this.width, StatsScreen.this.layout.getContentHeight(), 33, 22);
         this.blockColumns.add(Stats.BLOCK_MINED);
         this.itemColumns = Lists.newArrayList(new StatType[]{Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED});
         Set var3 = Sets.newIdentityHashSet();

         for(Item var5 : BuiltInRegistries.ITEM) {
            boolean var6 = false;

            for(StatType var8 : this.itemColumns) {
               if (var8.contains(var5) && StatsScreen.this.stats.getValue(var8.get(var5)) > 0) {
                  var6 = true;
               }
            }

            if (var6) {
               var3.add(var5);
            }
         }

         for(Block var11 : BuiltInRegistries.BLOCK) {
            boolean var13 = false;

            for(StatType var15 : this.blockColumns) {
               if (var15.contains(var11) && StatsScreen.this.stats.getValue(var15.get(var11)) > 0) {
                  var13 = true;
               }
            }

            if (var13) {
               var3.add(var11.asItem());
            }
         }

         var3.remove(Items.AIR);
         if (!var3.isEmpty()) {
            this.addEntry(new HeaderEntry());

            for(Item var12 : var3) {
               this.addEntry(new ItemRow(var12));
            }
         }

      }

      protected void renderListBackground(GuiGraphics var1) {
      }

      int getColumnX(int var1) {
         return 75 + 40 * var1;
      }

      public int getRowWidth() {
         return 280;
      }

      StatType<?> getColumn(int var1) {
         return var1 < this.blockColumns.size() ? (StatType)this.blockColumns.get(var1) : (StatType)this.itemColumns.get(var1 - this.blockColumns.size());
      }

      int getColumnIndex(StatType<?> var1) {
         int var2 = this.blockColumns.indexOf(var1);
         if (var2 >= 0) {
            return var2;
         } else {
            int var3 = this.itemColumns.indexOf(var1);
            return var3 >= 0 ? var3 + this.blockColumns.size() : -1;
         }
      }

      protected void sortByColumn(StatType<?> var1) {
         if (var1 != this.sortColumn) {
            this.sortColumn = var1;
            this.sortOrder = -1;
         } else if (this.sortOrder == -1) {
            this.sortOrder = 1;
         } else {
            this.sortColumn = null;
            this.sortOrder = 0;
         }

         this.sortItems(this.itemStatSorter);
      }

      protected void sortItems(Comparator<ItemRow> var1) {
         List var2 = this.getItemRows();
         var2.sort(var1);
         this.clearEntriesExcept((Entry)this.children().getFirst());

         for(ItemRow var4 : var2) {
            this.addEntry(var4);
         }

      }

      private List<ItemRow> getItemRows() {
         ArrayList var1 = new ArrayList();
         this.children().forEach((var1x) -> {
            if (var1x instanceof ItemRow var2) {
               var1.add(var2);
            }

         });
         return var1;
      }

      protected void renderListSeparators(GuiGraphics var1) {
      }

      class ItemRowComparator implements Comparator<ItemRow> {
         ItemRowComparator() {
            super();
         }

         public int compare(ItemRow var1, ItemRow var2) {
            Item var3 = var1.getItem();
            Item var4 = var2.getItem();
            int var5;
            int var6;
            if (ItemStatisticsList.this.sortColumn == null) {
               var5 = 0;
               var6 = 0;
            } else if (ItemStatisticsList.this.blockColumns.contains(ItemStatisticsList.this.sortColumn)) {
               StatType var7 = ItemStatisticsList.this.sortColumn;
               var5 = var3 instanceof BlockItem ? StatsScreen.this.stats.getValue(var7, ((BlockItem)var3).getBlock()) : -1;
               var6 = var4 instanceof BlockItem ? StatsScreen.this.stats.getValue(var7, ((BlockItem)var4).getBlock()) : -1;
            } else {
               StatType var8 = ItemStatisticsList.this.sortColumn;
               var5 = StatsScreen.this.stats.getValue(var8, var3);
               var6 = StatsScreen.this.stats.getValue(var8, var4);
            }

            return var5 == var6 ? ItemStatisticsList.this.sortOrder * Integer.compare(Item.getId(var3), Item.getId(var4)) : ItemStatisticsList.this.sortOrder * Integer.compare(var5, var6);
         }

         // $FF: synthetic method
         public int compare(final Object var1, final Object var2) {
            return this.compare((ItemRow)var1, (ItemRow)var2);
         }
      }

      abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
         Entry() {
            super();
         }
      }

      class ItemRow extends Entry {
         private final Item item;
         private final ItemRowWidget itemRowWidget;

         ItemRow(final Item var2) {
            super();
            this.item = var2;
            this.itemRowWidget = new ItemRowWidget(var2.getDefaultInstance());
         }

         protected Item getItem() {
            return this.item;
         }

         public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
            this.itemRowWidget.setPosition(this.getContentX(), this.getContentY());
            this.itemRowWidget.render(var1, var2, var3, var5);
            ItemStatisticsList var6 = ItemStatisticsList.this;
            int var7 = var6.children().indexOf(this);

            for(int var8 = 0; var8 < var6.blockColumns.size(); ++var8) {
               Item var11 = this.item;
               Stat var9;
               if (var11 instanceof BlockItem var10) {
                  var9 = ((StatType)var6.blockColumns.get(var8)).get(var10.getBlock());
               } else {
                  var9 = null;
               }

               int var10003 = this.getContentX() + ItemStatisticsList.this.getColumnX(var8);
               int var10004 = this.getContentYMiddle();
               Objects.requireNonNull(StatsScreen.this.font);
               this.renderStat(var1, var9, var10003, var10004 - 9 / 2, var7 % 2 == 0);
            }

            for(int var12 = 0; var12 < var6.itemColumns.size(); ++var12) {
               Stat var10002 = ((StatType)var6.itemColumns.get(var12)).get(this.item);
               int var13 = this.getContentX() + ItemStatisticsList.this.getColumnX(var12 + var6.blockColumns.size());
               int var14 = this.getContentYMiddle();
               Objects.requireNonNull(StatsScreen.this.font);
               this.renderStat(var1, var10002, var13, var14 - 9 / 2, var7 % 2 == 0);
            }

         }

         protected void renderStat(GuiGraphics var1, @Nullable Stat<?> var2, int var3, int var4, boolean var5) {
            Object var6 = var2 == null ? StatsScreen.NO_VALUE_DISPLAY : Component.literal(var2.format(StatsScreen.this.stats.getValue(var2)));
            var1.drawString(StatsScreen.this.font, (Component)var6, var3 - StatsScreen.this.font.width((FormattedText)var6), var4, var5 ? -1 : -4539718);
         }

         public List<? extends NarratableEntry> narratables() {
            return List.of(this.itemRowWidget);
         }

         public List<? extends GuiEventListener> children() {
            return List.of(this.itemRowWidget);
         }

         class ItemRowWidget extends ItemDisplayWidget {
            ItemRowWidget(final ItemStack var2) {
               super(ItemStatisticsList.this.minecraft, 1, 1, 18, 18, var2.getHoverName(), var2, false, true);
            }

            protected void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)StatsScreen.SLOT_SPRITE, ItemRow.this.getContentX(), ItemRow.this.getContentY(), 18, 18);
               super.renderWidget(var1, var2, var3, var4);
            }

            protected void renderTooltip(GuiGraphics var1, int var2, int var3) {
               super.renderTooltip(var1, ItemRow.this.getContentX() + 18, ItemRow.this.getContentY() + 18);
            }
         }
      }

      class HeaderEntry extends Entry {
         private static final ResourceLocation BLOCK_MINED_SPRITE = ResourceLocation.withDefaultNamespace("statistics/block_mined");
         private static final ResourceLocation ITEM_BROKEN_SPRITE = ResourceLocation.withDefaultNamespace("statistics/item_broken");
         private static final ResourceLocation ITEM_CRAFTED_SPRITE = ResourceLocation.withDefaultNamespace("statistics/item_crafted");
         private static final ResourceLocation ITEM_USED_SPRITE = ResourceLocation.withDefaultNamespace("statistics/item_used");
         private static final ResourceLocation ITEM_PICKED_UP_SPRITE = ResourceLocation.withDefaultNamespace("statistics/item_picked_up");
         private static final ResourceLocation ITEM_DROPPED_SPRITE = ResourceLocation.withDefaultNamespace("statistics/item_dropped");
         private final StatSortButton blockMined;
         private final StatSortButton itemBroken;
         private final StatSortButton itemCrafted;
         private final StatSortButton itemUsed;
         private final StatSortButton itemPickedUp;
         private final StatSortButton itemDropped;
         private final List<AbstractWidget> children = new ArrayList();

         HeaderEntry() {
            super();
            this.blockMined = new StatSortButton(0, BLOCK_MINED_SPRITE);
            this.itemBroken = new StatSortButton(1, ITEM_BROKEN_SPRITE);
            this.itemCrafted = new StatSortButton(2, ITEM_CRAFTED_SPRITE);
            this.itemUsed = new StatSortButton(3, ITEM_USED_SPRITE);
            this.itemPickedUp = new StatSortButton(4, ITEM_PICKED_UP_SPRITE);
            this.itemDropped = new StatSortButton(5, ITEM_DROPPED_SPRITE);
            this.children.addAll(List.of(this.blockMined, this.itemBroken, this.itemCrafted, this.itemUsed, this.itemPickedUp, this.itemDropped));
         }

         public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
            this.blockMined.setPosition(this.getContentX() + ItemStatisticsList.this.getColumnX(0) - 18, this.getContentY() + 1);
            this.blockMined.render(var1, var2, var3, var5);
            this.itemBroken.setPosition(this.getContentX() + ItemStatisticsList.this.getColumnX(1) - 18, this.getContentY() + 1);
            this.itemBroken.render(var1, var2, var3, var5);
            this.itemCrafted.setPosition(this.getContentX() + ItemStatisticsList.this.getColumnX(2) - 18, this.getContentY() + 1);
            this.itemCrafted.render(var1, var2, var3, var5);
            this.itemUsed.setPosition(this.getContentX() + ItemStatisticsList.this.getColumnX(3) - 18, this.getContentY() + 1);
            this.itemUsed.render(var1, var2, var3, var5);
            this.itemPickedUp.setPosition(this.getContentX() + ItemStatisticsList.this.getColumnX(4) - 18, this.getContentY() + 1);
            this.itemPickedUp.render(var1, var2, var3, var5);
            this.itemDropped.setPosition(this.getContentX() + ItemStatisticsList.this.getColumnX(5) - 18, this.getContentY() + 1);
            this.itemDropped.render(var1, var2, var3, var5);
            if (ItemStatisticsList.this.sortColumn != null) {
               int var6 = ItemStatisticsList.this.getColumnX(ItemStatisticsList.this.getColumnIndex(ItemStatisticsList.this.sortColumn)) - 36;
               ResourceLocation var7 = ItemStatisticsList.this.sortOrder == 1 ? StatsScreen.SORT_UP_SPRITE : StatsScreen.SORT_DOWN_SPRITE;
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var7, this.getContentX() + var6, this.getContentY() + 1, 18, 18);
            }

         }

         public List<? extends GuiEventListener> children() {
            return this.children;
         }

         public List<? extends NarratableEntry> narratables() {
            return this.children;
         }

         class StatSortButton extends ImageButton {
            private final ResourceLocation sprite;

            StatSortButton(final int var2, final ResourceLocation var3) {
               super(18, 18, new WidgetSprites(StatsScreen.HEADER_SPRITE, StatsScreen.SLOT_SPRITE), (var2x) -> ItemStatisticsList.this.sortByColumn(ItemStatisticsList.this.getColumn(var2)), ItemStatisticsList.this.getColumn(var2).getDisplayName());
               this.sprite = var3;
               this.setTooltip(Tooltip.create(this.getMessage()));
            }

            public void renderContents(GuiGraphics var1, int var2, int var3, float var4) {
               ResourceLocation var5 = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, var5, this.getX(), this.getY(), this.width, this.height);
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, this.getX(), this.getY(), this.width, this.height);
            }
         }
      }
   }

   class MobsStatisticsList extends ObjectSelectionList<MobRow> {
      public MobsStatisticsList(final Minecraft var2) {
         int var10002 = StatsScreen.this.width;
         int var10003 = StatsScreen.this.layout.getContentHeight();
         Objects.requireNonNull(StatsScreen.this.font);
         super(var2, var10002, var10003, 33, 9 * 4);

         for(EntityType var4 : BuiltInRegistries.ENTITY_TYPE) {
            if (StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(var4)) > 0 || StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(var4)) > 0) {
               this.addEntry(new MobRow(var4));
            }
         }

      }

      public int getRowWidth() {
         return 280;
      }

      protected void renderListBackground(GuiGraphics var1) {
      }

      protected void renderListSeparators(GuiGraphics var1) {
      }

      class MobRow extends ObjectSelectionList.Entry<MobRow> {
         private final Component mobName;
         private final Component kills;
         private final Component killedBy;
         private final boolean hasKills;
         private final boolean wasKilledBy;

         public MobRow(final EntityType<?> var2) {
            super();
            this.mobName = var2.getDescription();
            int var3 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(var2));
            if (var3 == 0) {
               this.kills = Component.translatable("stat_type.minecraft.killed.none", this.mobName);
               this.hasKills = false;
            } else {
               this.kills = Component.translatable("stat_type.minecraft.killed", var3, this.mobName);
               this.hasKills = true;
            }

            int var4 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(var2));
            if (var4 == 0) {
               this.killedBy = Component.translatable("stat_type.minecraft.killed_by.none", this.mobName);
               this.wasKilledBy = false;
            } else {
               this.killedBy = Component.translatable("stat_type.minecraft.killed_by", this.mobName, var4);
               this.wasKilledBy = true;
            }

         }

         public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
            var1.drawString(StatsScreen.this.font, (Component)this.mobName, this.getContentX() + 2, this.getContentY() + 1, -1);
            Font var10001 = StatsScreen.this.font;
            Component var10002 = this.kills;
            int var10003 = this.getContentX() + 2 + 10;
            int var10004 = this.getContentY() + 1;
            Objects.requireNonNull(StatsScreen.this.font);
            var1.drawString(var10001, var10002, var10003, var10004 + 9, this.hasKills ? -4539718 : -8355712);
            var10001 = StatsScreen.this.font;
            var10002 = this.killedBy;
            var10003 = this.getContentX() + 2 + 10;
            var10004 = this.getContentY() + 1;
            Objects.requireNonNull(StatsScreen.this.font);
            var1.drawString(var10001, var10002, var10003, var10004 + 9 * 2, this.wasKilledBy ? -4539718 : -8355712);
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", CommonComponents.joinForNarration(this.kills, this.killedBy));
         }
      }
   }
}
