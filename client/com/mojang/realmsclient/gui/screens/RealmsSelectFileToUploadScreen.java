package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.slf4j.Logger;

public class RealmsSelectFileToUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component UNABLE_TO_LOAD_WORLD = Component.translatable("selectWorld.unable_to_load");
   static final Component WORLD_TEXT = Component.translatable("selectWorld.world");
   static final Component HARDCORE_TEXT = Component.translatable("mco.upload.hardcore").withStyle(var0 -> var0.withColor(-65536));
   static final Component CHEATS_TEXT = Component.translatable("selectWorld.cheats");
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
   private final RealmsResetWorldScreen lastScreen;
   private final long worldId;
   private final int slotId;
   Button uploadButton;
   List<LevelSummary> levelList = Lists.newArrayList();
   int selectedWorld = -1;
   RealmsSelectFileToUploadScreen.WorldSelectionList worldSelectionList;
   private final Runnable callback;

   public RealmsSelectFileToUploadScreen(long var1, int var3, RealmsResetWorldScreen var4, Runnable var5) {
      super(Component.translatable("mco.upload.select.world.title"));
      this.lastScreen = var4;
      this.worldId = var1;
      this.slotId = var3;
      this.callback = var5;
   }

   private void loadLevelList() throws Exception {
      LevelStorageSource.LevelCandidates var1 = this.minecraft.getLevelSource().findLevelCandidates();
      this.levelList = this.minecraft
         .getLevelSource()
         .loadLevelSummaries(var1)
         .join()
         .stream()
         .filter(var0 -> !var0.requiresManualConversion() && !var0.isLocked())
         .collect(Collectors.toList());

      for(LevelSummary var3 : this.levelList) {
         this.worldSelectionList.addEntry(var3);
      }
   }

   @Override
   public void init() {
      this.worldSelectionList = new RealmsSelectFileToUploadScreen.WorldSelectionList();

      try {
         this.loadLevelList();
      } catch (Exception var2) {
         LOGGER.error("Couldn't load level list", var2);
         this.minecraft.setScreen(new RealmsGenericErrorScreen(UNABLE_TO_LOAD_WORLD, Component.nullToEmpty(var2.getMessage()), this.lastScreen));
         return;
      }

      this.addWidget(this.worldSelectionList);
      this.uploadButton = this.addRenderableWidget(
         Button.builder(Component.translatable("mco.upload.button.name"), var1 -> this.upload())
            .bounds(this.width / 2 - 154, this.height - 32, 153, 20)
            .build()
      );
      this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_BACK, var1 -> this.minecraft.setScreen(this.lastScreen))
            .bounds(this.width / 2 + 6, this.height - 32, 153, 20)
            .build()
      );
      this.addLabel(new RealmsLabel(Component.translatable("mco.upload.select.world.subtitle"), this.width / 2, row(-1), 10526880));
      if (this.levelList.isEmpty()) {
         this.addLabel(new RealmsLabel(Component.translatable("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, 16777215));
      }
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
   }

   private void upload() {
      if (this.selectedWorld != -1 && !this.levelList.get(this.selectedWorld).isHardcore()) {
         LevelSummary var1 = this.levelList.get(this.selectedWorld);
         this.minecraft.setScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.lastScreen, var1, this.callback));
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.worldSelectionList.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 13, 16777215);
      super.render(var1, var2, var3, var4);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   static Component gameModeName(LevelSummary var0) {
      return var0.getGameMode().getLongDisplayName();
   }

   static String formatLastPlayed(LevelSummary var0) {
      return DATE_FORMAT.format(new Date(var0.getLastPlayed()));
   }

   class Entry extends ObjectSelectionList.Entry<RealmsSelectFileToUploadScreen.Entry> {
      private final LevelSummary levelSummary;
      private final String name;
      private final Component id;
      private final Component info;

      public Entry(LevelSummary var2) {
         super();
         this.levelSummary = var2;
         this.name = var2.getLevelName();
         this.id = Component.translatable("mco.upload.entry.id", var2.getLevelId(), RealmsSelectFileToUploadScreen.formatLastPlayed(var2));
         Object var3;
         if (var2.isHardcore()) {
            var3 = RealmsSelectFileToUploadScreen.HARDCORE_TEXT;
         } else {
            var3 = RealmsSelectFileToUploadScreen.gameModeName(var2);
         }

         if (var2.hasCheats()) {
            var3 = Component.translatable("mco.upload.entry.cheats", ((Component)var3).getString(), RealmsSelectFileToUploadScreen.CHEATS_TEXT);
         }

         this.info = (Component)var3;
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.renderItem(var1, var2, var4, var3);
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         RealmsSelectFileToUploadScreen.this.worldSelectionList.selectItem(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.levelSummary));
         return true;
      }

      protected void renderItem(GuiGraphics var1, int var2, int var3, int var4) {
         String var5;
         if (this.name.isEmpty()) {
            var5 = RealmsSelectFileToUploadScreen.WORLD_TEXT + " " + (var2 + 1);
         } else {
            var5 = this.name;
         }

         var1.drawString(RealmsSelectFileToUploadScreen.this.font, var5, var3 + 2, var4 + 1, 16777215, false);
         var1.drawString(RealmsSelectFileToUploadScreen.this.font, this.id, var3 + 2, var4 + 12, 8421504, false);
         var1.drawString(RealmsSelectFileToUploadScreen.this.font, this.info, var3 + 2, var4 + 12 + 10, 8421504, false);
      }

      @Override
      public Component getNarration() {
         Component var1 = CommonComponents.joinLines(
            Component.literal(this.levelSummary.getLevelName()),
            Component.literal(RealmsSelectFileToUploadScreen.formatLastPlayed(this.levelSummary)),
            RealmsSelectFileToUploadScreen.gameModeName(this.levelSummary)
         );
         return Component.translatable("narrator.select", var1);
      }
   }

   class WorldSelectionList extends RealmsObjectSelectionList<RealmsSelectFileToUploadScreen.Entry> {
      public WorldSelectionList() {
         super(
            RealmsSelectFileToUploadScreen.this.width,
            RealmsSelectFileToUploadScreen.this.height,
            RealmsSelectFileToUploadScreen.row(0),
            RealmsSelectFileToUploadScreen.this.height - 40,
            36
         );
      }

      public void addEntry(LevelSummary var1) {
         this.addEntry(RealmsSelectFileToUploadScreen.this.new Entry(var1));
      }

      @Override
      public int getMaxPosition() {
         return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
      }

      @Override
      public void renderBackground(GuiGraphics var1) {
         RealmsSelectFileToUploadScreen.this.renderBackground(var1);
      }

      public void setSelected(@Nullable RealmsSelectFileToUploadScreen.Entry var1) {
         super.setSelected(var1);
         RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf(var1);
         RealmsSelectFileToUploadScreen.this.uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0
            && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount()
            && !RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld).isHardcore();
      }
   }
}
