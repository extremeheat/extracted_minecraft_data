package net.minecraft.client.gui.screens.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
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

public class StatsScreen extends Screen implements StatsUpdateListener {
   private static final Component PENDING_TEXT = new TranslatableComponent("multiplayer.downloadingStats");
   protected final Screen lastScreen;
   private StatsScreen.GeneralStatisticsList statsList;
   private StatsScreen.ItemStatisticsList itemStatsList;
   private StatsScreen.MobsStatisticsList mobsStatsList;
   private final StatsCounter stats;
   @Nullable
   private ObjectSelectionList<?> activeList;
   private boolean isLoading = true;

   public StatsScreen(Screen var1, StatsCounter var2) {
      super(new TranslatableComponent("gui.stats"));
      this.lastScreen = var1;
      this.stats = var2;
   }

   protected void init() {
      this.isLoading = true;
      this.minecraft.getConnection().send((Packet)(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS)));
   }

   public void initLists() {
      this.statsList = new StatsScreen.GeneralStatisticsList(this.minecraft);
      this.itemStatsList = new StatsScreen.ItemStatisticsList(this.minecraft);
      this.mobsStatsList = new StatsScreen.MobsStatisticsList(this.minecraft);
   }

   public void initButtons() {
      this.addButton(new Button(this.width / 2 - 120, this.height - 52, 80, 20, new TranslatableComponent("stat.generalButton"), (var1x) -> {
         this.setActiveList(this.statsList);
      }));
      Button var1 = (Button)this.addButton(new Button(this.width / 2 - 40, this.height - 52, 80, 20, new TranslatableComponent("stat.itemsButton"), (var1x) -> {
         this.setActiveList(this.itemStatsList);
      }));
      Button var2 = (Button)this.addButton(new Button(this.width / 2 + 40, this.height - 52, 80, 20, new TranslatableComponent("stat.mobsButton"), (var1x) -> {
         this.setActiveList(this.mobsStatsList);
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height - 28, 200, 20, CommonComponents.GUI_DONE, (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      if (this.itemStatsList.children().isEmpty()) {
         var1.active = false;
      }

      if (this.mobsStatsList.children().isEmpty()) {
         var2.active = false;
      }

   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      if (this.isLoading) {
         this.renderBackground(var1);
         drawCenteredString(var1, this.font, PENDING_TEXT, this.width / 2, this.height / 2, 16777215);
         Font var10001 = this.font;
         String var10002 = LOADING_SYMBOLS[(int)(Util.getMillis() / 150L % (long)LOADING_SYMBOLS.length)];
         int var10003 = this.width / 2;
         int var10004 = this.height / 2;
         this.font.getClass();
         drawCenteredString(var1, var10001, var10002, var10003, var10004 + 9 * 2, 16777215);
      } else {
         this.getActiveList().render(var1, var2, var3, var4);
         drawCenteredString(var1, this.font, this.title, this.width / 2, 20, 16777215);
         super.render(var1, var2, var3, var4);
      }

   }

   public void onStatsUpdated() {
      if (this.isLoading) {
         this.initLists();
         this.initButtons();
         this.setActiveList(this.statsList);
         this.isLoading = false;
      }

   }

   public boolean isPauseScreen() {
      return !this.isLoading;
   }

   @Nullable
   public ObjectSelectionList<?> getActiveList() {
      return this.activeList;
   }

   public void setActiveList(@Nullable ObjectSelectionList<?> var1) {
      this.children.remove(this.statsList);
      this.children.remove(this.itemStatsList);
      this.children.remove(this.mobsStatsList);
      if (var1 != null) {
         this.children.add(0, var1);
         this.activeList = var1;
      }

   }

   private static String getTranslationKey(Stat<ResourceLocation> var0) {
      return "stat." + ((ResourceLocation)var0.getValue()).toString().replace(':', '.');
   }

   private int getColumnX(int var1) {
      return 115 + 40 * var1;
   }

   private void blitSlot(PoseStack var1, int var2, int var3, Item var4) {
      this.blitSlotIcon(var1, var2 + 1, var3 + 1, 0, 0);
      RenderSystem.enableRescaleNormal();
      this.itemRenderer.renderGuiItem(var4.getDefaultInstance(), var2 + 2, var3 + 2);
      RenderSystem.disableRescaleNormal();
   }

   private void blitSlotIcon(PoseStack var1, int var2, int var3, int var4, int var5) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(STATS_ICON_LOCATION);
      blit(var1, var2, var3, this.getBlitOffset(), (float)var4, (float)var5, 18, 18, 128, 128);
   }

   class MobsStatisticsList extends ObjectSelectionList<StatsScreen.MobsStatisticsList.MobRow> {
      public MobsStatisticsList(Minecraft var2) {
         int var10002 = StatsScreen.this.width;
         int var10003 = StatsScreen.this.height;
         int var10005 = StatsScreen.this.height - 64;
         StatsScreen.this.font.getClass();
         super(var2, var10002, var10003, 32, var10005, 9 * 4);
         Iterator var3 = Registry.ENTITY_TYPE.iterator();

         while(true) {
            EntityType var4;
            do {
               if (!var3.hasNext()) {
                  return;
               }

               var4 = (EntityType)var3.next();
            } while(StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(var4)) <= 0 && StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(var4)) <= 0);

            this.addEntry(new StatsScreen.MobsStatisticsList.MobRow(var4));
         }
      }

      protected void renderBackground(PoseStack var1) {
         StatsScreen.this.renderBackground(var1);
      }

      class MobRow extends ObjectSelectionList.Entry<StatsScreen.MobsStatisticsList.MobRow> {
         private final EntityType<?> type;
         private final Component mobName;
         private final Component kills;
         private final boolean hasKills;
         private final Component killedBy;
         private final boolean wasKilledBy;

         public MobRow(EntityType<?> var2) {
            super();
            this.type = var2;
            this.mobName = var2.getDescription();
            int var3 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(var2));
            if (var3 == 0) {
               this.kills = new TranslatableComponent("stat_type.minecraft.killed.none", new Object[]{this.mobName});
               this.hasKills = false;
            } else {
               this.kills = new TranslatableComponent("stat_type.minecraft.killed", new Object[]{var3, this.mobName});
               this.hasKills = true;
            }

            int var4 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(var2));
            if (var4 == 0) {
               this.killedBy = new TranslatableComponent("stat_type.minecraft.killed_by.none", new Object[]{this.mobName});
               this.wasKilledBy = false;
            } else {
               this.killedBy = new TranslatableComponent("stat_type.minecraft.killed_by", new Object[]{this.mobName, var4});
               this.wasKilledBy = true;
            }

         }

         public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            GuiComponent.drawString(var1, StatsScreen.this.font, this.mobName, var4 + 2, var3 + 1, 16777215);
            Font var10001 = StatsScreen.this.font;
            Component var10002 = this.kills;
            int var10003 = var4 + 2 + 10;
            int var10004 = var3 + 1;
            StatsScreen.this.font.getClass();
            GuiComponent.drawString(var1, var10001, var10002, var10003, var10004 + 9, this.hasKills ? 9474192 : 6316128);
            var10001 = StatsScreen.this.font;
            var10002 = this.killedBy;
            var10003 = var4 + 2 + 10;
            var10004 = var3 + 1;
            StatsScreen.this.font.getClass();
            GuiComponent.drawString(var1, var10001, var10002, var10003, var10004 + 9 * 2, this.wasKilledBy ? 9474192 : 6316128);
         }
      }
   }

   class ItemStatisticsList extends ObjectSelectionList<StatsScreen.ItemStatisticsList.ItemRow> {
      protected final List<StatType<Block>> blockColumns = Lists.newArrayList();
      protected final List<StatType<Item>> itemColumns;
      private final int[] iconOffsets = new int[]{3, 4, 1, 2, 5, 6};
      protected int headerPressed = -1;
      protected final List<Item> statItemList;
      protected final Comparator<Item> itemStatSorter = new StatsScreen.ItemStatisticsList.ItemComparator();
      @Nullable
      protected StatType<?> sortColumn;
      protected int sortOrder;

      public ItemStatisticsList(Minecraft var2) {
         super(var2, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
         this.blockColumns.add(Stats.BLOCK_MINED);
         this.itemColumns = Lists.newArrayList(new StatType[]{Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED});
         this.setRenderHeader(true, 20);
         Set var3 = Sets.newIdentityHashSet();
         Iterator var4 = Registry.ITEM.iterator();

         boolean var6;
         Iterator var7;
         StatType var8;
         while(var4.hasNext()) {
            Item var5 = (Item)var4.next();
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

         var4 = Registry.BLOCK.iterator();

         while(var4.hasNext()) {
            Block var10 = (Block)var4.next();
            var6 = false;
            var7 = this.blockColumns.iterator();

            while(var7.hasNext()) {
               var8 = (StatType)var7.next();
               if (var8.contains(var10) && StatsScreen.this.stats.getValue(var8.get(var10)) > 0) {
                  var6 = true;
               }
            }

            if (var6) {
               var3.add(var10.asItem());
            }
         }

         var3.remove(Items.AIR);
         this.statItemList = Lists.newArrayList(var3);

         for(int var9 = 0; var9 < this.statItemList.size(); ++var9) {
            this.addEntry(new StatsScreen.ItemStatisticsList.ItemRow());
         }

      }

      protected void renderHeader(PoseStack var1, int var2, int var3, Tesselator var4) {
         if (!this.minecraft.mouseHandler.isLeftPressed()) {
            this.headerPressed = -1;
         }

         int var5;
         for(var5 = 0; var5 < this.iconOffsets.length; ++var5) {
            StatsScreen.this.blitSlotIcon(var1, var2 + StatsScreen.this.getColumnX(var5) - 18, var3 + 1, 0, this.headerPressed == var5 ? 0 : 18);
         }

         int var6;
         if (this.sortColumn != null) {
            var5 = StatsScreen.this.getColumnX(this.getColumnIndex(this.sortColumn)) - 36;
            var6 = this.sortOrder == 1 ? 2 : 1;
            StatsScreen.this.blitSlotIcon(var1, var2 + var5, var3 + 1, 18 * var6, 0);
         }

         for(var5 = 0; var5 < this.iconOffsets.length; ++var5) {
            var6 = this.headerPressed == var5 ? 1 : 0;
            StatsScreen.this.blitSlotIcon(var1, var2 + StatsScreen.this.getColumnX(var5) - 18 + var6, var3 + 1 + var6, 18 * this.iconOffsets[var5], 18);
         }

      }

      public int getRowWidth() {
         return 375;
      }

      protected int getScrollbarPosition() {
         return this.width / 2 + 140;
      }

      protected void renderBackground(PoseStack var1) {
         StatsScreen.this.renderBackground(var1);
      }

      protected void clickedHeader(int var1, int var2) {
         this.headerPressed = -1;

         for(int var3 = 0; var3 < this.iconOffsets.length; ++var3) {
            int var4 = var1 - StatsScreen.this.getColumnX(var3);
            if (var4 >= -36 && var4 <= 0) {
               this.headerPressed = var3;
               break;
            }
         }

         if (this.headerPressed >= 0) {
            this.sortByColumn(this.getColumn(this.headerPressed));
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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

      protected void renderDecorations(PoseStack var1, int var2, int var3) {
         if (var3 >= this.y0 && var3 <= this.y1) {
            StatsScreen.ItemStatisticsList.ItemRow var4 = (StatsScreen.ItemStatisticsList.ItemRow)this.getEntryAtPosition((double)var2, (double)var3);
            int var5 = (this.width - this.getRowWidth()) / 2;
            if (var4 != null) {
               if (var2 < var5 + 40 || var2 > var5 + 40 + 20) {
                  return;
               }

               Item var10 = (Item)this.statItemList.get(this.children().indexOf(var4));
               this.renderMousehoverTooltip(var1, this.getString(var10), var2, var3);
            } else {
               Component var6 = null;
               int var7 = var2 - var5;

               for(int var8 = 0; var8 < this.iconOffsets.length; ++var8) {
                  int var9 = StatsScreen.this.getColumnX(var8);
                  if (var7 >= var9 - 18 && var7 <= var9) {
                     var6 = this.getColumn(var8).getDisplayName();
                     break;
                  }
               }

               this.renderMousehoverTooltip(var1, var6, var2, var3);
            }

         }
      }

      protected void renderMousehoverTooltip(PoseStack var1, @Nullable Component var2, int var3, int var4) {
         if (var2 != null) {
            int var5 = var3 + 12;
            int var6 = var4 - 12;
            int var7 = StatsScreen.this.font.width((FormattedText)var2);
            this.fillGradient(var1, var5 - 3, var6 - 3, var5 + var7 + 3, var6 + 8 + 3, -1073741824, -1073741824);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0F, 0.0F, 400.0F);
            StatsScreen.this.font.drawShadow(var1, (Component)var2, (float)var5, (float)var6, -1);
            RenderSystem.popMatrix();
         }
      }

      protected Component getString(Item var1) {
         return var1.getDescription();
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

         this.statItemList.sort(this.itemStatSorter);
      }

      class ItemRow extends ObjectSelectionList.Entry<StatsScreen.ItemStatisticsList.ItemRow> {
         private ItemRow() {
            super();
         }

         public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            Item var11 = (Item)StatsScreen.this.itemStatsList.statItemList.get(var2);
            StatsScreen.this.blitSlot(var1, var4 + 40, var3, var11);

            int var12;
            for(var12 = 0; var12 < StatsScreen.this.itemStatsList.blockColumns.size(); ++var12) {
               Stat var13;
               if (var11 instanceof BlockItem) {
                  var13 = ((StatType)StatsScreen.this.itemStatsList.blockColumns.get(var12)).get(((BlockItem)var11).getBlock());
               } else {
                  var13 = null;
               }

               this.renderStat(var1, var13, var4 + StatsScreen.this.getColumnX(var12), var3, var2 % 2 == 0);
            }

            for(var12 = 0; var12 < StatsScreen.this.itemStatsList.itemColumns.size(); ++var12) {
               this.renderStat(var1, ((StatType)StatsScreen.this.itemStatsList.itemColumns.get(var12)).get(var11), var4 + StatsScreen.this.getColumnX(var12 + StatsScreen.this.itemStatsList.blockColumns.size()), var3, var2 % 2 == 0);
            }

         }

         protected void renderStat(PoseStack var1, @Nullable Stat<?> var2, int var3, int var4, boolean var5) {
            String var6 = var2 == null ? "-" : var2.format(StatsScreen.this.stats.getValue(var2));
            GuiComponent.drawString(var1, StatsScreen.this.font, var6, var3 - StatsScreen.this.font.width(var6), var4 + 5, var5 ? 16777215 : 9474192);
         }

         // $FF: synthetic method
         ItemRow(Object var2) {
            this();
         }
      }

      class ItemComparator implements Comparator<Item> {
         private ItemComparator() {
            super();
         }

         public int compare(Item var1, Item var2) {
            int var3;
            int var4;
            if (ItemStatisticsList.this.sortColumn == null) {
               var3 = 0;
               var4 = 0;
            } else {
               StatType var5;
               if (ItemStatisticsList.this.blockColumns.contains(ItemStatisticsList.this.sortColumn)) {
                  var5 = ItemStatisticsList.this.sortColumn;
                  var3 = var1 instanceof BlockItem ? StatsScreen.this.stats.getValue(var5, ((BlockItem)var1).getBlock()) : -1;
                  var4 = var2 instanceof BlockItem ? StatsScreen.this.stats.getValue(var5, ((BlockItem)var2).getBlock()) : -1;
               } else {
                  var5 = ItemStatisticsList.this.sortColumn;
                  var3 = StatsScreen.this.stats.getValue(var5, var1);
                  var4 = StatsScreen.this.stats.getValue(var5, var2);
               }
            }

            return var3 == var4 ? ItemStatisticsList.this.sortOrder * Integer.compare(Item.getId(var1), Item.getId(var2)) : ItemStatisticsList.this.sortOrder * Integer.compare(var3, var4);
         }

         // $FF: synthetic method
         public int compare(Object var1, Object var2) {
            return this.compare((Item)var1, (Item)var2);
         }

         // $FF: synthetic method
         ItemComparator(Object var2) {
            this();
         }
      }
   }

   class GeneralStatisticsList extends ObjectSelectionList<StatsScreen.GeneralStatisticsList.Entry> {
      public GeneralStatisticsList(Minecraft var2) {
         super(var2, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
         ObjectArrayList var3 = new ObjectArrayList(Stats.CUSTOM.iterator());
         var3.sort(Comparator.comparing((var0) -> {
            return I18n.get(StatsScreen.getTranslationKey(var0));
         }));
         ObjectListIterator var4 = var3.iterator();

         while(var4.hasNext()) {
            Stat var5 = (Stat)var4.next();
            this.addEntry(new StatsScreen.GeneralStatisticsList.Entry(var5));
         }

      }

      protected void renderBackground(PoseStack var1) {
         StatsScreen.this.renderBackground(var1);
      }

      class Entry extends ObjectSelectionList.Entry<StatsScreen.GeneralStatisticsList.Entry> {
         private final Stat<ResourceLocation> stat;
         private final Component statDisplay;

         private Entry(Stat<ResourceLocation> var2) {
            super();
            this.stat = var2;
            this.statDisplay = new TranslatableComponent(StatsScreen.getTranslationKey(var2));
         }

         public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
            GuiComponent.drawString(var1, StatsScreen.this.font, this.statDisplay, var4 + 2, var3 + 1, var2 % 2 == 0 ? 16777215 : 9474192);
            String var11 = this.stat.format(StatsScreen.this.stats.getValue(this.stat));
            GuiComponent.drawString(var1, StatsScreen.this.font, var11, var4 + 2 + 213 - StatsScreen.this.font.width(var11), var3 + 1, var2 % 2 == 0 ? 16777215 : 9474192);
         }

         // $FF: synthetic method
         Entry(Stat var2, Object var3) {
            this(var2);
         }
      }
   }
}
