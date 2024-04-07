package net.minecraft.client.gui.screens.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class StatsScreen extends Screen {
   private static final Component TITLE = Component.translatable("gui.stats");
   static final ResourceLocation SLOT_SPRITE = new ResourceLocation("container/slot");
   static final ResourceLocation HEADER_SPRITE = new ResourceLocation("statistics/header");
   static final ResourceLocation SORT_UP_SPRITE = new ResourceLocation("statistics/sort_up");
   static final ResourceLocation SORT_DOWN_SPRITE = new ResourceLocation("statistics/sort_down");
   private static final Component PENDING_TEXT = Component.translatable("multiplayer.downloadingStats");
   static final Component NO_VALUE_DISPLAY = Component.translatable("stats.none");
   private static final Component GENERAL_BUTTON = Component.translatable("stat.generalButton");
   private static final Component ITEMS_BUTTON = Component.translatable("stat.itemsButton");
   private static final Component MOBS_BUTTON = Component.translatable("stat.mobsButton");
   protected final Screen lastScreen;
   private static final int LIST_WIDTH = 280;
   private static final int PADDING = 5;
   private static final int FOOTER_HEIGHT = 58;
   private HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 33, 58);
   @Nullable
   private StatsScreen.GeneralStatisticsList statsList;
   @Nullable
   StatsScreen.ItemStatisticsList itemStatsList;
   @Nullable
   private StatsScreen.MobsStatisticsList mobsStatsList;
   final StatsCounter stats;
   @Nullable
   private ObjectSelectionList<?> activeList;
   private boolean isLoading = true;

   public StatsScreen(Screen var1, StatsCounter var2) {
      super(TITLE);
      this.lastScreen = var1;
      this.stats = var2;
   }

   @Override
   protected void init() {
      this.layout.addToContents(new LoadingDotsWidget(this.font, PENDING_TEXT));
      this.minecraft.getConnection().send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
   }

   public void initLists() {
      this.statsList = new StatsScreen.GeneralStatisticsList(this.minecraft);
      this.itemStatsList = new StatsScreen.ItemStatisticsList(this.minecraft);
      this.mobsStatsList = new StatsScreen.MobsStatisticsList(this.minecraft);
   }

   public void initButtons() {
      HeaderAndFooterLayout var1 = new HeaderAndFooterLayout(this, 33, 58);
      var1.addTitleHeader(TITLE, this.font);
      LinearLayout var2 = var1.addToFooter(LinearLayout.vertical()).spacing(5);
      var2.defaultCellSetting().alignHorizontallyCenter();
      LinearLayout var3 = var2.addChild(LinearLayout.horizontal()).spacing(5);
      var3.addChild(Button.builder(GENERAL_BUTTON, var1x -> this.setActiveList(this.statsList)).width(120).build());
      Button var4 = var3.addChild(Button.builder(ITEMS_BUTTON, var1x -> this.setActiveList(this.itemStatsList)).width(120).build());
      Button var5 = var3.addChild(Button.builder(MOBS_BUTTON, var1x -> this.setActiveList(this.mobsStatsList)).width(120).build());
      var2.addChild(Button.builder(CommonComponents.GUI_DONE, var1x -> this.onClose()).width(200).build());
      if (this.itemStatsList != null && this.itemStatsList.children().isEmpty()) {
         var4.active = false;
      }

      if (this.mobsStatsList != null && this.mobsStatsList.children().isEmpty()) {
         var5.active = false;
      }

      this.layout = var1;
      this.layout.visitWidgets(var1x -> {
         AbstractWidget var10000 = this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.activeList != null) {
         this.activeList.updateSize(this.width, this.layout);
      }
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void onStatsUpdated() {
      if (this.isLoading) {
         this.initLists();
         this.setActiveList(this.statsList);
         this.initButtons();
         this.setInitialFocus();
         this.isLoading = false;
      }
   }

   @Override
   public boolean isPauseScreen() {
      return !this.isLoading;
   }

   public void setActiveList(@Nullable ObjectSelectionList<?> var1) {
      if (this.activeList != null) {
         this.removeWidget(this.activeList);
      }

      if (var1 != null) {
         this.addRenderableWidget(var1);
         this.activeList = var1;
         this.repositionElements();
      }
   }

   static String getTranslationKey(Stat<ResourceLocation> var0) {
      return "stat." + ((ResourceLocation)var0.getValue()).toString().replace(':', '.');
   }

   class GeneralStatisticsList extends ObjectSelectionList<StatsScreen.GeneralStatisticsList.Entry> {
      public GeneralStatisticsList(Minecraft var2) {
         super(var2, StatsScreen.this.width, StatsScreen.this.height - 33 - 58, 33, 14);
         ObjectArrayList var3 = new ObjectArrayList(Stats.CUSTOM.iterator());
         var3.sort(Comparator.comparing(var0 -> I18n.get(StatsScreen.getTranslationKey((Stat<ResourceLocation>)var0))));
         ObjectListIterator var4 = var3.iterator();

         while (var4.hasNext()) {
            Stat var5 = (Stat)var4.next();
            this.addEntry(new StatsScreen.GeneralStatisticsList.Entry(var5));
         }
      }

      @Override
      public int getRowWidth() {
         return 280;
      }

      class Entry extends ObjectSelectionList.Entry<StatsScreen.GeneralStatisticsList.Entry> {
         private final Stat<ResourceLocation> stat;
         private final Component statDisplay;

         Entry(Stat<ResourceLocation> var2) {
            super();
            this.stat = var2;
            this.statDisplay = Component.translatable(StatsScreen.getTranslationKey(var2));
         }

         private String getValueText() {
            return this.stat.format(StatsScreen.this.stats.getValue(this.stat));
         }

         @Override
         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            int var11 = var3 + var6 / 2 - 9 / 2;
            int var12 = var2 % 2 == 0 ? -1 : -4539718;
            var1.drawString(StatsScreen.this.font, this.statDisplay, var4 + 2, var11, var12);
            String var13 = this.getValueText();
            var1.drawString(StatsScreen.this.font, var13, var4 + var5 - StatsScreen.this.font.width(var13) - 4, var11, var12);
         }

         @Override
         public Component getNarration() {
            return Component.translatable(
               "narrator.select", Component.empty().append(this.statDisplay).append(CommonComponents.SPACE).append(this.getValueText())
            );
         }
      }
   }

   class ItemStatisticsList extends ObjectSelectionList<StatsScreen.ItemStatisticsList.ItemRow> {
      private static final int SLOT_BG_SIZE = 18;
      private static final int SLOT_STAT_HEIGHT = 22;
      private static final int SLOT_BG_Y = 1;
      private static final int SORT_NONE = 0;
      private static final int SORT_DOWN = -1;
      private static final int SORT_UP = 1;
      private final ResourceLocation[] iconSprites = new ResourceLocation[]{
         new ResourceLocation("statistics/block_mined"),
         new ResourceLocation("statistics/item_broken"),
         new ResourceLocation("statistics/item_crafted"),
         new ResourceLocation("statistics/item_used"),
         new ResourceLocation("statistics/item_picked_up"),
         new ResourceLocation("statistics/item_dropped")
      };
      protected final List<StatType<Block>> blockColumns;
      protected final List<StatType<Item>> itemColumns;
      protected final Comparator<StatsScreen.ItemStatisticsList.ItemRow> itemStatSorter = new StatsScreen.ItemStatisticsList.ItemRowComparator();
      @Nullable
      protected StatType<?> sortColumn;
      protected int headerPressed = -1;
      protected int sortOrder;

      public ItemStatisticsList(Minecraft var2) {
         super(var2, StatsScreen.this.width, StatsScreen.this.height - 33 - 58, 33, 22);
         this.blockColumns = Lists.newArrayList();
         this.blockColumns.add(Stats.BLOCK_MINED);
         this.itemColumns = Lists.newArrayList(new StatType[]{Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED});
         this.setRenderHeader(true, 22);
         Set var3 = Sets.newIdentityHashSet();

         for (Item var5 : BuiltInRegistries.ITEM) {
            boolean var6 = false;

            for (StatType var8 : this.itemColumns) {
               if (var8.contains(var5) && StatsScreen.this.stats.getValue(var8.get(var5)) > 0) {
                  var6 = true;
               }
            }

            if (var6) {
               var3.add(var5);
            }
         }

         for (Block var11 : BuiltInRegistries.BLOCK) {
            boolean var13 = false;

            for (StatType var15 : this.blockColumns) {
               if (var15.contains(var11) && StatsScreen.this.stats.getValue(var15.get(var11)) > 0) {
                  var13 = true;
               }
            }

            if (var13) {
               var3.add(var11.asItem());
            }
         }

         var3.remove(Items.AIR);

         for (Item var12 : var3) {
            this.addEntry(new StatsScreen.ItemStatisticsList.ItemRow(var12));
         }
      }

      int getColumnX(int var1) {
         return 75 + 40 * var1;
      }

      @Override
      protected void renderHeader(GuiGraphics var1, int var2, int var3) {
         if (!this.minecraft.mouseHandler.isLeftPressed()) {
            this.headerPressed = -1;
         }

         for (int var4 = 0; var4 < this.iconSprites.length; var4++) {
            ResourceLocation var5 = this.headerPressed == var4 ? StatsScreen.SLOT_SPRITE : StatsScreen.HEADER_SPRITE;
            var1.blitSprite(var5, var2 + this.getColumnX(var4) - 18, var3 + 1, 0, 18, 18);
         }

         if (this.sortColumn != null) {
            int var6 = this.getColumnX(this.getColumnIndex(this.sortColumn)) - 36;
            ResourceLocation var8 = this.sortOrder == 1 ? StatsScreen.SORT_UP_SPRITE : StatsScreen.SORT_DOWN_SPRITE;
            var1.blitSprite(var8, var2 + var6, var3 + 1, 0, 18, 18);
         }

         for (int var7 = 0; var7 < this.iconSprites.length; var7++) {
            int var9 = this.headerPressed == var7 ? 1 : 0;
            var1.blitSprite(this.iconSprites[var7], var2 + this.getColumnX(var7) - 18 + var9, var3 + 1 + var9, 0, 18, 18);
         }
      }

      @Override
      public int getRowWidth() {
         return 280;
      }

      @Override
      protected boolean clickedHeader(int var1, int var2) {
         this.headerPressed = -1;

         for (int var3 = 0; var3 < this.iconSprites.length; var3++) {
            int var4 = var1 - this.getColumnX(var3);
            if (var4 >= -36 && var4 <= 0) {
               this.headerPressed = var3;
               break;
            }
         }

         if (this.headerPressed >= 0) {
            this.sortByColumn(this.getColumn(this.headerPressed));
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
         } else {
            return super.clickedHeader(var1, var2);
         }
      }

      private StatType<?> getColumn(int var1) {
         return var1 < this.blockColumns.size() ? this.blockColumns.get(var1) : this.itemColumns.get(var1 - this.blockColumns.size());
      }

      private int getColumnIndex(StatType<?> var1) {
         int var2 = this.blockColumns.indexOf(var1);
         if (var2 >= 0) {
            return var2;
         } else {
            int var3 = this.itemColumns.indexOf(var1);
            return var3 >= 0 ? var3 + this.blockColumns.size() : -1;
         }
      }

      @Override
      protected void renderDecorations(GuiGraphics var1, int var2, int var3) {
         if (var3 >= this.getY() && var3 <= this.getBottom()) {
            StatsScreen.ItemStatisticsList.ItemRow var4 = this.getHovered();
            int var5 = this.getRowLeft();
            if (var4 != null) {
               if (var2 < var5 || var2 > var5 + 18) {
                  return;
               }

               Item var6 = var4.getItem();
               var1.renderTooltip(StatsScreen.this.font, var6.getDescription(), var2, var3);
            } else {
               Component var10 = null;
               int var7 = var2 - var5;

               for (int var8 = 0; var8 < this.iconSprites.length; var8++) {
                  int var9 = this.getColumnX(var8);
                  if (var7 >= var9 - 18 && var7 <= var9) {
                     var10 = this.getColumn(var8).getDisplayName();
                     break;
                  }
               }

               if (var10 != null) {
                  var1.renderTooltip(StatsScreen.this.font, var10, var2, var3);
               }
            }
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

         this.children().sort(this.itemStatSorter);
      }

      class ItemRow extends ObjectSelectionList.Entry<StatsScreen.ItemStatisticsList.ItemRow> {
         private final Item item;

         ItemRow(Item var2) {
            super();
            this.item = var2;
         }

         public Item getItem() {
            return this.item;
         }

         @Override
         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            var1.blitSprite(StatsScreen.SLOT_SPRITE, var4, var3, 0, 18, 18);
            var1.renderFakeItem(this.item.getDefaultInstance(), var4 + 1, var3 + 1);
            if (StatsScreen.this.itemStatsList != null) {
               for (int var11 = 0; var11 < StatsScreen.this.itemStatsList.blockColumns.size(); var11++) {
                  Stat var12;
                  if (this.item instanceof BlockItem var13) {
                     var12 = StatsScreen.this.itemStatsList.blockColumns.get(var11).get(var13.getBlock());
                  } else {
                     var12 = null;
                  }

                  this.renderStat(var1, var12, var4 + ItemStatisticsList.this.getColumnX(var11), var3 + var6 / 2 - 9 / 2, var2 % 2 == 0);
               }

               for (int var15 = 0; var15 < StatsScreen.this.itemStatsList.itemColumns.size(); var15++) {
                  this.renderStat(
                     var1,
                     StatsScreen.this.itemStatsList.itemColumns.get(var15).get(this.item),
                     var4 + ItemStatisticsList.this.getColumnX(var15 + StatsScreen.this.itemStatsList.blockColumns.size()),
                     var3 + var6 / 2 - 9 / 2,
                     var2 % 2 == 0
                  );
               }
            }
         }

         protected void renderStat(GuiGraphics var1, @Nullable Stat<?> var2, int var3, int var4, boolean var5) {
            Object var6 = var2 == null ? StatsScreen.NO_VALUE_DISPLAY : Component.literal(var2.format(StatsScreen.this.stats.getValue(var2)));
            var1.drawString(StatsScreen.this.font, (Component)var6, var3 - StatsScreen.this.font.width((FormattedText)var6), var4, var5 ? -1 : -4539718);
         }

         @Override
         public Component getNarration() {
            return Component.translatable("narrator.select", this.item.getDescription());
         }
      }

      class ItemRowComparator implements Comparator<StatsScreen.ItemStatisticsList.ItemRow> {
         ItemRowComparator() {
            super();
         }

         public int compare(StatsScreen.ItemStatisticsList.ItemRow var1, StatsScreen.ItemStatisticsList.ItemRow var2) {
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

            return var5 == var6
               ? ItemStatisticsList.this.sortOrder * Integer.compare(Item.getId(var3), Item.getId(var4))
               : ItemStatisticsList.this.sortOrder * Integer.compare(var5, var6);
         }
      }
   }

   class MobsStatisticsList extends ObjectSelectionList<StatsScreen.MobsStatisticsList.MobRow> {
      public MobsStatisticsList(Minecraft var2) {
         super(var2, StatsScreen.this.width, StatsScreen.this.height - 33 - 58, 33, 9 * 4);

         for (EntityType var4 : BuiltInRegistries.ENTITY_TYPE) {
            if (StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(var4)) > 0 || StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(var4)) > 0) {
               this.addEntry(new StatsScreen.MobsStatisticsList.MobRow(var4));
            }
         }
      }

      @Override
      public int getRowWidth() {
         return 280;
      }

      class MobRow extends ObjectSelectionList.Entry<StatsScreen.MobsStatisticsList.MobRow> {
         private final Component mobName;
         private final Component kills;
         private final Component killedBy;
         private final boolean hasKills;
         private final boolean wasKilledBy;

         public MobRow(EntityType<?> var2) {
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

         @Override
         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            var1.drawString(StatsScreen.this.font, this.mobName, var4 + 2, var3 + 1, -1);
            var1.drawString(StatsScreen.this.font, this.kills, var4 + 2 + 10, var3 + 1 + 9, this.hasKills ? -4539718 : -8355712);
            var1.drawString(StatsScreen.this.font, this.killedBy, var4 + 2 + 10, var3 + 1 + 9 * 2, this.wasKilledBy ? -4539718 : -8355712);
         }

         @Override
         public Component getNarration() {
            return Component.translatable("narrator.select", CommonComponents.joinForNarration(this.kills, this.killedBy));
         }
      }
   }
}
