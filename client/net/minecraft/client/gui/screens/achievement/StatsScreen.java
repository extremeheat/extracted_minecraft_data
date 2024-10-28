package net.minecraft.client.gui.screens.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
import net.minecraft.core.Holder;
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
   private static final int PADDING = 5;
   private static final int FOOTER_HEIGHT = 58;
   private HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 33, 58);
   @Nullable
   private GeneralStatisticsList statsList;
   @Nullable
   ItemStatisticsList itemStatsList;
   @Nullable
   private MobsStatisticsList mobsStatsList;
   final StatsCounter stats;
   @Nullable
   private ObjectSelectionList<?> activeList;
   private boolean isLoading = true;

   public StatsScreen(Screen var1, StatsCounter var2) {
      super(TITLE);
      this.lastScreen = var1;
      this.stats = var2;
   }

   protected void init() {
      this.layout.addToContents(new LoadingDotsWidget(this.font, PENDING_TEXT));
      this.minecraft.getConnection().send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
   }

   public void initLists() {
      this.statsList = new GeneralStatisticsList(this.minecraft);
      this.itemStatsList = new ItemStatisticsList(this.minecraft);
      this.mobsStatsList = new MobsStatisticsList(this.minecraft);
   }

   public void initButtons() {
      HeaderAndFooterLayout var1 = new HeaderAndFooterLayout(this, 33, 58);
      var1.addTitleHeader(TITLE, this.font);
      LinearLayout var2 = ((LinearLayout)var1.addToFooter(LinearLayout.vertical())).spacing(5);
      var2.defaultCellSetting().alignHorizontallyCenter();
      LinearLayout var3 = ((LinearLayout)var2.addChild(LinearLayout.horizontal())).spacing(5);
      var3.addChild(Button.builder(GENERAL_BUTTON, (var1x) -> {
         this.setActiveList(this.statsList);
      }).width(120).build());
      Button var4 = (Button)var3.addChild(Button.builder(ITEMS_BUTTON, (var1x) -> {
         this.setActiveList(this.itemStatsList);
      }).width(120).build());
      Button var5 = (Button)var3.addChild(Button.builder(MOBS_BUTTON, (var1x) -> {
         this.setActiveList(this.mobsStatsList);
      }).width(120).build());
      var2.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> {
         this.onClose();
      }).width(200).build());
      if (this.itemStatsList != null && this.itemStatsList.children().isEmpty()) {
         var4.active = false;
      }

      if (this.mobsStatsList != null && this.mobsStatsList.children().isEmpty()) {
         var5.active = false;
      }

      this.layout = var1;
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.activeList != null) {
         this.activeList.updateSize(this.width, this.layout);
      }

   }

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
      String var10000 = ((ResourceLocation)var0.getValue()).toString();
      return "stat." + var10000.replace(':', '.');
   }

   private class GeneralStatisticsList extends ObjectSelectionList<Entry> {
      public GeneralStatisticsList(final Minecraft var2) {
         super(var2, StatsScreen.this.width, StatsScreen.this.height - 33 - 58, 33, 14);
         ObjectArrayList var3 = new ObjectArrayList(Stats.CUSTOM.iterator());
         var3.sort(Comparator.comparing((var0) -> {
            return I18n.get(StatsScreen.getTranslationKey(var0));
         }));
         ObjectListIterator var4 = var3.iterator();

         while(var4.hasNext()) {
            Stat var5 = (Stat)var4.next();
            this.addEntry(new Entry(var5));
         }

      }

      public int getRowWidth() {
         return 280;
      }

      private class Entry extends ObjectSelectionList.Entry<Entry> {
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

         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            int var10000 = var3 + var6 / 2;
            Objects.requireNonNull(StatsScreen.this.font);
            int var11 = var10000 - 9 / 2;
            int var12 = var2 % 2 == 0 ? -1 : -4539718;
            var1.drawString(StatsScreen.this.font, this.statDisplay, var4 + 2, var11, var12);
            String var13 = this.getValueText();
            var1.drawString(StatsScreen.this.font, var13, var4 + var5 - StatsScreen.this.font.width(var13) - 4, var11, var12);
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", Component.empty().append(this.statDisplay).append(CommonComponents.SPACE).append(this.getValueText()));
         }
      }
   }

   class ItemStatisticsList extends ObjectSelectionList<ItemRow> {
      private static final int SLOT_BG_SIZE = 18;
      private static final int SLOT_STAT_HEIGHT = 22;
      private static final int SLOT_BG_Y = 1;
      private static final int SORT_NONE = 0;
      private static final int SORT_DOWN = -1;
      private static final int SORT_UP = 1;
      private final ResourceLocation[] iconSprites = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("statistics/block_mined"), ResourceLocation.withDefaultNamespace("statistics/item_broken"), ResourceLocation.withDefaultNamespace("statistics/item_crafted"), ResourceLocation.withDefaultNamespace("statistics/item_used"), ResourceLocation.withDefaultNamespace("statistics/item_picked_up"), ResourceLocation.withDefaultNamespace("statistics/item_dropped")};
      protected final List<StatType<Block>> blockColumns = Lists.newArrayList();
      protected final List<StatType<Item>> itemColumns;
      protected final Comparator<ItemRow> itemStatSorter = new ItemRowComparator();
      @Nullable
      protected StatType<?> sortColumn;
      protected int headerPressed = -1;
      protected int sortOrder;

      public ItemStatisticsList(final Minecraft var2) {
         super(var2, StatsScreen.this.width, StatsScreen.this.height - 33 - 58, 33, 22);
         this.blockColumns.add(Stats.BLOCK_MINED);
         this.itemColumns = Lists.newArrayList(new StatType[]{Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED});
         this.setRenderHeader(true, 22);
         Set var3 = Sets.newIdentityHashSet();
         Iterator var4 = BuiltInRegistries.ITEM.iterator();

         Item var5;
         boolean var6;
         Iterator var7;
         StatType var8;
         while(var4.hasNext()) {
            var5 = (Item)var4.next();
            var6 = false;
            var7 = this.itemColumns.iterator();

            while(var7.hasNext()) {
               var8 = (StatType)var7.next();
               if (var8.contains(var5) && StatsScreen.this.stats.getValue(var8.get(var5)) > 0) {
                  var6 = true;
               }
            }

            if (var6) {
               var3.add(var5);
            }
         }

         var4 = BuiltInRegistries.BLOCK.iterator();

         while(var4.hasNext()) {
            Block var9 = (Block)var4.next();
            var6 = false;
            var7 = this.blockColumns.iterator();

            while(var7.hasNext()) {
               var8 = (StatType)var7.next();
               if (var8.contains(var9) && StatsScreen.this.stats.getValue(var8.get(var9)) > 0) {
                  var6 = true;
               }
            }

            if (var6) {
               var3.add(var9.asItem());
            }
         }

         var3.remove(Items.AIR);
         var4 = var3.iterator();

         while(var4.hasNext()) {
            var5 = (Item)var4.next();
            this.addEntry(new ItemRow(var5));
         }

      }

      int getColumnX(int var1) {
         return 75 + 40 * var1;
      }

      protected void renderHeader(GuiGraphics var1, int var2, int var3) {
         if (!this.minecraft.mouseHandler.isLeftPressed()) {
            this.headerPressed = -1;
         }

         int var4;
         ResourceLocation var5;
         for(var4 = 0; var4 < this.iconSprites.length; ++var4) {
            var5 = this.headerPressed == var4 ? StatsScreen.SLOT_SPRITE : StatsScreen.HEADER_SPRITE;
            var1.blitSprite((ResourceLocation)var5, var2 + this.getColumnX(var4) - 18, var3 + 1, 0, 18, 18);
         }

         if (this.sortColumn != null) {
            var4 = this.getColumnX(this.getColumnIndex(this.sortColumn)) - 36;
            var5 = this.sortOrder == 1 ? StatsScreen.SORT_UP_SPRITE : StatsScreen.SORT_DOWN_SPRITE;
            var1.blitSprite((ResourceLocation)var5, var2 + var4, var3 + 1, 0, 18, 18);
         }

         for(var4 = 0; var4 < this.iconSprites.length; ++var4) {
            int var6 = this.headerPressed == var4 ? 1 : 0;
            var1.blitSprite((ResourceLocation)this.iconSprites[var4], var2 + this.getColumnX(var4) - 18 + var6, var3 + 1 + var6, 0, 18, 18);
         }

      }

      public int getRowWidth() {
         return 280;
      }

      protected boolean clickedHeader(int var1, int var2) {
         this.headerPressed = -1;

         for(int var3 = 0; var3 < this.iconSprites.length; ++var3) {
            int var4 = var1 - this.getColumnX(var3);
            if (var4 >= -36 && var4 <= 0) {
               this.headerPressed = var3;
               break;
            }
         }

         if (this.headerPressed >= 0) {
            this.sortByColumn(this.getColumn(this.headerPressed));
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
         } else {
            return super.clickedHeader(var1, var2);
         }
      }

      private StatType<?> getColumn(int var1) {
         return var1 < this.blockColumns.size() ? (StatType)this.blockColumns.get(var1) : (StatType)this.itemColumns.get(var1 - this.blockColumns.size());
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

      protected void renderDecorations(GuiGraphics var1, int var2, int var3) {
         if (var3 >= this.getY() && var3 <= this.getBottom()) {
            ItemRow var4 = (ItemRow)this.getHovered();
            int var5 = this.getRowLeft();
            if (var4 != null) {
               if (var2 < var5 || var2 > var5 + 18) {
                  return;
               }

               Item var10 = var4.getItem();
               var1.renderTooltip(StatsScreen.this.font, var10.getDescription(), var2, var3);
            } else {
               Component var6 = null;
               int var7 = var2 - var5;

               for(int var8 = 0; var8 < this.iconSprites.length; ++var8) {
                  int var9 = this.getColumnX(var8);
                  if (var7 >= var9 - 18 && var7 <= var9) {
                     var6 = this.getColumn(var8).getDisplayName();
                     break;
                  }
               }

               if (var6 != null) {
                  var1.renderTooltip(StatsScreen.this.font, var6, var2, var3);
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

      private class ItemRowComparator implements Comparator<ItemRow> {
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
            } else {
               StatType var7;
               if (ItemStatisticsList.this.blockColumns.contains(ItemStatisticsList.this.sortColumn)) {
                  var7 = ItemStatisticsList.this.sortColumn;
                  var5 = var3 instanceof BlockItem ? StatsScreen.this.stats.getValue(var7, ((BlockItem)var3).getBlock()) : -1;
                  var6 = var4 instanceof BlockItem ? StatsScreen.this.stats.getValue(var7, ((BlockItem)var4).getBlock()) : -1;
               } else {
                  var7 = ItemStatisticsList.this.sortColumn;
                  var5 = StatsScreen.this.stats.getValue(var7, var3);
                  var6 = StatsScreen.this.stats.getValue(var7, var4);
               }
            }

            return var5 == var6 ? ItemStatisticsList.this.sortOrder * Integer.compare(Item.getId(var3), Item.getId(var4)) : ItemStatisticsList.this.sortOrder * Integer.compare(var5, var6);
         }

         // $FF: synthetic method
         public int compare(final Object var1, final Object var2) {
            return this.compare((ItemRow)var1, (ItemRow)var2);
         }
      }

      private class ItemRow extends ObjectSelectionList.Entry<ItemRow> {
         private final Item item;

         ItemRow(final Item var2) {
            super();
            this.item = var2;
         }

         public Item getItem() {
            return this.item;
         }

         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            var1.blitSprite((ResourceLocation)StatsScreen.SLOT_SPRITE, var4, var3, 0, 18, 18);
            var1.renderFakeItem(this.item.getDefaultInstance(), var4 + 1, var3 + 1);
            if (StatsScreen.this.itemStatsList != null) {
               int var11;
               int var10003;
               int var10004;
               for(var11 = 0; var11 < StatsScreen.this.itemStatsList.blockColumns.size(); ++var11) {
                  Item var14 = this.item;
                  Stat var12;
                  if (var14 instanceof BlockItem) {
                     BlockItem var13 = (BlockItem)var14;
                     var12 = ((StatType)StatsScreen.this.itemStatsList.blockColumns.get(var11)).get(var13.getBlock());
                  } else {
                     var12 = null;
                  }

                  var10003 = var4 + ItemStatisticsList.this.getColumnX(var11);
                  var10004 = var3 + var6 / 2;
                  Objects.requireNonNull(StatsScreen.this.font);
                  this.renderStat(var1, var12, var10003, var10004 - 9 / 2, var2 % 2 == 0);
               }

               for(var11 = 0; var11 < StatsScreen.this.itemStatsList.itemColumns.size(); ++var11) {
                  Stat var10002 = ((StatType)StatsScreen.this.itemStatsList.itemColumns.get(var11)).get(this.item);
                  var10003 = var4 + ItemStatisticsList.this.getColumnX(var11 + StatsScreen.this.itemStatsList.blockColumns.size());
                  var10004 = var3 + var6 / 2;
                  Objects.requireNonNull(StatsScreen.this.font);
                  this.renderStat(var1, var10002, var10003, var10004 - 9 / 2, var2 % 2 == 0);
               }
            }

         }

         protected void renderStat(GuiGraphics var1, @Nullable Stat<?> var2, int var3, int var4, boolean var5) {
            Object var6 = var2 == null ? StatsScreen.NO_VALUE_DISPLAY : Component.literal(var2.format(StatsScreen.this.stats.getValue(var2)));
            var1.drawString(StatsScreen.this.font, (Component)var6, var3 - StatsScreen.this.font.width((FormattedText)var6), var4, var5 ? -1 : -4539718);
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", this.item.getDescription());
         }
      }
   }

   private class MobsStatisticsList extends ObjectSelectionList<MobRow> {
      public MobsStatisticsList(final Minecraft var2) {
         int var10002 = StatsScreen.this.width;
         int var10003 = StatsScreen.this.height - 33 - 58;
         Objects.requireNonNull(StatsScreen.this.font);
         super(var2, var10002, var10003, 33, 9 * 4);
         Iterator var3 = BuiltInRegistries.ENTITY_TYPE.iterator();

         while(true) {
            EntityType var4;
            do {
               if (!var3.hasNext()) {
                  return;
               }

               var4 = (EntityType)var3.next();
            } while(StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(var4)) <= 0 && StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(var4)) <= 0);

            this.addEntry(new MobRow(var4));
         }
      }

      public int getRowWidth() {
         return 280;
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

         public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            var1.drawString(StatsScreen.this.font, (Component)this.mobName, var4 + 2, var3 + 1, -1);
            Font var10001 = StatsScreen.this.font;
            Component var10002 = this.kills;
            int var10003 = var4 + 2 + 10;
            int var10004 = var3 + 1;
            Objects.requireNonNull(StatsScreen.this.font);
            var1.drawString(var10001, var10002, var10003, var10004 + 9, this.hasKills ? -4539718 : -8355712);
            var10001 = StatsScreen.this.font;
            var10002 = this.killedBy;
            var10003 = var4 + 2 + 10;
            var10004 = var3 + 1;
            Objects.requireNonNull(StatsScreen.this.font);
            var1.drawString(var10001, var10002, var10003, var10004 + 9 * 2, this.wasKilledBy ? -4539718 : -8355712);
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", CommonComponents.joinForNarration(this.kills, this.killedBy));
         }
      }
   }
}
