package net.minecraft.client.gui.screens.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
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
   protected final Screen lastScreen;
   private StatsScreen.GeneralStatisticsList statsList;
   private StatsScreen.ItemStatisticsList itemStatsList;
   private StatsScreen.MobsStatisticsList mobsStatsList;
   private final StatsCounter stats;
   @Nullable
   private ObjectSelectionList activeList;
   private boolean isLoading = true;

   public StatsScreen(Screen var1, StatsCounter var2) {
      super(new TranslatableComponent("gui.stats", new Object[0]));
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
      this.addButton(new Button(this.width / 2 - 120, this.height - 52, 80, 20, I18n.get("stat.generalButton"), (var1x) -> {
         this.setActiveList(this.statsList);
      }));
      Button var1 = (Button)this.addButton(new Button(this.width / 2 - 40, this.height - 52, 80, 20, I18n.get("stat.itemsButton"), (var1x) -> {
         this.setActiveList(this.itemStatsList);
      }));
      Button var2 = (Button)this.addButton(new Button(this.width / 2 + 40, this.height - 52, 80, 20, I18n.get("stat.mobsButton"), (var1x) -> {
         this.setActiveList(this.mobsStatsList);
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height - 28, 200, 20, I18n.get("gui.done"), (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      if (this.itemStatsList.children().isEmpty()) {
         var1.active = false;
      }

      if (this.mobsStatsList.children().isEmpty()) {
         var2.active = false;
      }

   }

   public void render(int var1, int var2, float var3) {
      if (this.isLoading) {
         this.renderBackground();
         this.drawCenteredString(this.font, I18n.get("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
         Font var10001 = this.font;
         String var10002 = LOADING_SYMBOLS[(int)(Util.getMillis() / 150L % (long)LOADING_SYMBOLS.length)];
         int var10003 = this.width / 2;
         int var10004 = this.height / 2;
         this.font.getClass();
         this.drawCenteredString(var10001, var10002, var10003, var10004 + 9 * 2, 16777215);
      } else {
         this.getActiveList().render(var1, var2, var3);
         this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
         super.render(var1, var2, var3);
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
   public ObjectSelectionList getActiveList() {
      return this.activeList;
   }

   public void setActiveList(@Nullable ObjectSelectionList var1) {
      this.children.remove(this.statsList);
      this.children.remove(this.itemStatsList);
      this.children.remove(this.mobsStatsList);
      if (var1 != null) {
         this.children.add(0, var1);
         this.activeList = var1;
      }

   }

   private int getColumnX(int var1) {
      return 115 + 40 * var1;
   }

   private void blitSlot(int var1, int var2, Item var3) {
      this.blitSlotIcon(var1 + 1, var2 + 1, 0, 0);
      RenderSystem.enableRescaleNormal();
      this.itemRenderer.renderGuiItem(var3.getDefaultInstance(), var1 + 2, var2 + 2);
      RenderSystem.disableRescaleNormal();
   }

   private void blitSlotIcon(int var1, int var2, int var3, int var4) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(STATS_ICON_LOCATION);
      blit(var1, var2, this.getBlitOffset(), (float)var3, (float)var4, 18, 18, 128, 128);
   }

   class MobsStatisticsList extends ObjectSelectionList {
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

      protected void renderBackground() {
         StatsScreen.this.renderBackground();
      }

      class MobRow extends ObjectSelectionList.Entry {
         private final EntityType type;

         public MobRow(EntityType var2) {
            this.type = var2;
         }

         public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
            String var10 = I18n.get(Util.makeDescriptionId("entity", EntityType.getKey(this.type)));
            int var11 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(this.type));
            int var12 = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(this.type));
            MobsStatisticsList.this.drawString(StatsScreen.this.font, var10, var3 + 2, var2 + 1, 16777215);
            StatsScreen.MobsStatisticsList var10000 = MobsStatisticsList.this;
            Font var10001 = StatsScreen.this.font;
            String var10002 = this.killsMessage(var10, var11);
            int var10003 = var3 + 2 + 10;
            int var10004 = var2 + 1;
            StatsScreen.this.font.getClass();
            var10000.drawString(var10001, var10002, var10003, var10004 + 9, var11 == 0 ? 6316128 : 9474192);
            var10000 = MobsStatisticsList.this;
            var10001 = StatsScreen.this.font;
            var10002 = this.killedByMessage(var10, var12);
            var10003 = var3 + 2 + 10;
            var10004 = var2 + 1;
            StatsScreen.this.font.getClass();
            var10000.drawString(var10001, var10002, var10003, var10004 + 9 * 2, var12 == 0 ? 6316128 : 9474192);
         }

         private String killsMessage(String var1, int var2) {
            String var3 = Stats.ENTITY_KILLED.getTranslationKey();
            return var2 == 0 ? I18n.get(var3 + ".none", var1) : I18n.get(var3, var2, var1);
         }

         private String killedByMessage(String var1, int var2) {
            String var3 = Stats.ENTITY_KILLED_BY.getTranslationKey();
            return var2 == 0 ? I18n.get(var3 + ".none", var1) : I18n.get(var3, var1, var2);
         }
      }
   }

   class ItemStatisticsList extends ObjectSelectionList {
      protected final List blockColumns = Lists.newArrayList();
      protected final List itemColumns;
      private final int[] iconOffsets = new int[]{3, 4, 1, 2, 5, 6};
      protected int headerPressed = -1;
      protected final List statItemList;
      protected final Comparator itemStatSorter = new StatsScreen.ItemStatisticsList.ItemComparator();
      @Nullable
      protected StatType sortColumn;
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

      protected void renderHeader(int var1, int var2, Tesselator var3) {
         if (!this.minecraft.mouseHandler.isLeftPressed()) {
            this.headerPressed = -1;
         }

         int var4;
         for(var4 = 0; var4 < this.iconOffsets.length; ++var4) {
            StatsScreen.this.blitSlotIcon(var1 + StatsScreen.this.getColumnX(var4) - 18, var2 + 1, 0, this.headerPressed == var4 ? 0 : 18);
         }

         int var5;
         if (this.sortColumn != null) {
            var4 = StatsScreen.this.getColumnX(this.getColumnIndex(this.sortColumn)) - 36;
            var5 = this.sortOrder == 1 ? 2 : 1;
            StatsScreen.this.blitSlotIcon(var1 + var4, var2 + 1, 18 * var5, 0);
         }

         for(var4 = 0; var4 < this.iconOffsets.length; ++var4) {
            var5 = this.headerPressed == var4 ? 1 : 0;
            StatsScreen.this.blitSlotIcon(var1 + StatsScreen.this.getColumnX(var4) - 18 + var5, var2 + 1 + var5, 18 * this.iconOffsets[var4], 18);
         }

      }

      public int getRowWidth() {
         return 375;
      }

      protected int getScrollbarPosition() {
         return this.width / 2 + 140;
      }

      protected void renderBackground() {
         StatsScreen.this.renderBackground();
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

      private StatType getColumn(int var1) {
         return var1 < this.blockColumns.size() ? (StatType)this.blockColumns.get(var1) : (StatType)this.itemColumns.get(var1 - this.blockColumns.size());
      }

      private int getColumnIndex(StatType var1) {
         int var2 = this.blockColumns.indexOf(var1);
         if (var2 >= 0) {
            return var2;
         } else {
            int var3 = this.itemColumns.indexOf(var1);
            return var3 >= 0 ? var3 + this.blockColumns.size() : -1;
         }
      }

      protected void renderDecorations(int var1, int var2) {
         if (var2 >= this.y0 && var2 <= this.y1) {
            StatsScreen.ItemStatisticsList.ItemRow var3 = (StatsScreen.ItemStatisticsList.ItemRow)this.getEntryAtPosition((double)var1, (double)var2);
            int var4 = (this.width - this.getRowWidth()) / 2;
            if (var3 != null) {
               if (var1 < var4 + 40 || var1 > var4 + 40 + 20) {
                  return;
               }

               Item var9 = (Item)this.statItemList.get(this.children().indexOf(var3));
               this.renderMousehoverTooltip(this.getString(var9), var1, var2);
            } else {
               TranslatableComponent var5 = null;
               int var6 = var1 - var4;

               for(int var7 = 0; var7 < this.iconOffsets.length; ++var7) {
                  int var8 = StatsScreen.this.getColumnX(var7);
                  if (var6 >= var8 - 18 && var6 <= var8) {
                     var5 = new TranslatableComponent(this.getColumn(var7).getTranslationKey(), new Object[0]);
                     break;
                  }
               }

               this.renderMousehoverTooltip(var5, var1, var2);
            }

         }
      }

      protected void renderMousehoverTooltip(@Nullable Component var1, int var2, int var3) {
         if (var1 != null) {
            String var4 = var1.getColoredString();
            int var5 = var2 + 12;
            int var6 = var3 - 12;
            int var7 = StatsScreen.this.font.width(var4);
            this.fillGradient(var5 - 3, var6 - 3, var5 + var7 + 3, var6 + 8 + 3, -1073741824, -1073741824);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0F, 0.0F, 400.0F);
            StatsScreen.this.font.drawShadow(var4, (float)var5, (float)var6, -1);
            RenderSystem.popMatrix();
         }
      }

      protected Component getString(Item var1) {
         return var1.getDescription();
      }

      protected void sortByColumn(StatType var1) {
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

      class ItemRow extends ObjectSelectionList.Entry {
         private ItemRow() {
         }

         public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
            Item var10 = (Item)StatsScreen.this.itemStatsList.statItemList.get(var1);
            StatsScreen.this.blitSlot(var3 + 40, var2, var10);

            int var11;
            for(var11 = 0; var11 < StatsScreen.this.itemStatsList.blockColumns.size(); ++var11) {
               Stat var12;
               if (var10 instanceof BlockItem) {
                  var12 = ((StatType)StatsScreen.this.itemStatsList.blockColumns.get(var11)).get(((BlockItem)var10).getBlock());
               } else {
                  var12 = null;
               }

               this.renderStat(var12, var3 + StatsScreen.this.getColumnX(var11), var2, var1 % 2 == 0);
            }

            for(var11 = 0; var11 < StatsScreen.this.itemStatsList.itemColumns.size(); ++var11) {
               this.renderStat(((StatType)StatsScreen.this.itemStatsList.itemColumns.get(var11)).get(var10), var3 + StatsScreen.this.getColumnX(var11 + StatsScreen.this.itemStatsList.blockColumns.size()), var2, var1 % 2 == 0);
            }

         }

         protected void renderStat(@Nullable Stat var1, int var2, int var3, boolean var4) {
            String var5 = var1 == null ? "-" : var1.format(StatsScreen.this.stats.getValue(var1));
            ItemStatisticsList.this.drawString(StatsScreen.this.font, var5, var2 - StatsScreen.this.font.width(var5), var3 + 5, var4 ? 16777215 : 9474192);
         }

         // $FF: synthetic method
         ItemRow(Object var2) {
            this();
         }
      }

      class ItemComparator implements Comparator {
         private ItemComparator() {
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

   class GeneralStatisticsList extends ObjectSelectionList {
      public GeneralStatisticsList(Minecraft var2) {
         super(var2, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
         Iterator var3 = Stats.CUSTOM.iterator();

         while(var3.hasNext()) {
            Stat var4 = (Stat)var3.next();
            this.addEntry(new StatsScreen.GeneralStatisticsList.Entry(var4));
         }

      }

      protected void renderBackground() {
         StatsScreen.this.renderBackground();
      }

      class Entry extends ObjectSelectionList.Entry {
         private final Stat stat;

         private Entry(Stat var2) {
            this.stat = var2;
         }

         public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
            Component var10 = (new TranslatableComponent("stat." + ((ResourceLocation)this.stat.getValue()).toString().replace(':', '.'), new Object[0])).withStyle(ChatFormatting.GRAY);
            GeneralStatisticsList.this.drawString(StatsScreen.this.font, var10.getString(), var3 + 2, var2 + 1, var1 % 2 == 0 ? 16777215 : 9474192);
            String var11 = this.stat.format(StatsScreen.this.stats.getValue(this.stat));
            GeneralStatisticsList.this.drawString(StatsScreen.this.font, var11, var3 + 2 + 213 - StatsScreen.this.font.width(var11), var2 + 1, var1 % 2 == 0 ? 16777215 : 9474192);
         }

         // $FF: synthetic method
         Entry(Stat var2, Object var3) {
            this(var2);
         }
      }
   }
}
