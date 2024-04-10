package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.task.RealmCreationTask;
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
   public static final Component TITLE = Component.translatable("mco.upload.select.world.title");
   private static final Component UNABLE_TO_LOAD_WORLD = Component.translatable("selectWorld.unable_to_load");
   static final Component WORLD_TEXT = Component.translatable("selectWorld.world");
   private static final Component HARDCORE_TEXT = Component.translatable("mco.upload.hardcore").withColor(-65536);
   private static final Component COMMANDS_TEXT = Component.translatable("selectWorld.commands");
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
   @Nullable
   private final RealmCreationTask realmCreationTask;
   private final RealmsResetWorldScreen lastScreen;
   private final long realmId;
   private final int slotId;
   Button uploadButton;
   List<LevelSummary> levelList = Lists.newArrayList();
   int selectedWorld = -1;
   RealmsSelectFileToUploadScreen.WorldSelectionList worldSelectionList;

   public RealmsSelectFileToUploadScreen(@Nullable RealmCreationTask var1, long var2, int var4, RealmsResetWorldScreen var5) {
      super(TITLE);
      this.realmCreationTask = var1;
      this.lastScreen = var5;
      this.realmId = var2;
      this.slotId = var4;
   }

   private void loadLevelList() {
      LevelStorageSource.LevelCandidates var1 = this.minecraft.getLevelSource().findLevelCandidates();
      this.levelList = this.minecraft.getLevelSource().loadLevelSummaries(var1).join().stream().filter(LevelSummary::canUpload).collect(Collectors.toList());

      for (LevelSummary var3 : this.levelList) {
         this.worldSelectionList.addEntry(var3);
      }
   }

   @Override
   public void init() {
      this.worldSelectionList = this.addRenderableWidget(new RealmsSelectFileToUploadScreen.WorldSelectionList());

      try {
         this.loadLevelList();
      } catch (Exception var2) {
         LOGGER.error("Couldn't load level list", var2);
         this.minecraft.setScreen(new RealmsGenericErrorScreen(UNABLE_TO_LOAD_WORLD, Component.nullToEmpty(var2.getMessage()), this.lastScreen));
         return;
      }

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
      this.addLabel(new RealmsLabel(Component.translatable("mco.upload.select.world.subtitle"), this.width / 2, row(-1), -6250336));
      if (this.levelList.isEmpty()) {
         this.addLabel(new RealmsLabel(Component.translatable("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, -1));
      }
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
   }

   private void upload() {
      if (this.selectedWorld != -1 && !this.levelList.get(this.selectedWorld).isHardcore()) {
         LevelSummary var1 = this.levelList.get(this.selectedWorld);
         this.minecraft.setScreen(new RealmsUploadScreen(this.realmCreationTask, this.realmId, this.slotId, this.lastScreen, var1));
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 13, -1);
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

      public Entry(final LevelSummary param2) {
         super();
         this.levelSummary = nullx;
         this.name = nullx.getLevelName();
         this.id = Component.translatable("mco.upload.entry.id", nullx.getLevelId(), RealmsSelectFileToUploadScreen.formatLastPlayed(nullx));
         this.info = nullx.getInfo();
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.renderItem(var1, var2, var4, var3);
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         RealmsSelectFileToUploadScreen.this.worldSelectionList.selectItem(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.levelSummary));
         return super.mouseClicked(var1, var3, var5);
      }

      protected void renderItem(GuiGraphics var1, int var2, int var3, int var4) {
         String var5;
         if (this.name.isEmpty()) {
            var5 = RealmsSelectFileToUploadScreen.WORLD_TEXT + " " + (var2 + 1);
         } else {
            var5 = this.name;
         }

         var1.drawString(RealmsSelectFileToUploadScreen.this.font, var5, var3 + 2, var4 + 1, 16777215, false);
         var1.drawString(RealmsSelectFileToUploadScreen.this.font, this.id, var3 + 2, var4 + 12, -8355712, false);
         var1.drawString(RealmsSelectFileToUploadScreen.this.font, this.info, var3 + 2, var4 + 12 + 10, -8355712, false);
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
            RealmsSelectFileToUploadScreen.this.height - 40 - RealmsSelectFileToUploadScreen.row(0),
            RealmsSelectFileToUploadScreen.row(0),
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

      public void setSelected(@Nullable RealmsSelectFileToUploadScreen.Entry var1) {
         super.setSelected(var1);
         RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf(var1);
         RealmsSelectFileToUploadScreen.this.uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0
            && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount()
            && !RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld).isHardcore();
      }
   }
}
