package net.minecraft.client.gui.screens.worldselection;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelSummary;
import org.slf4j.Logger;

public class SelectWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final WorldOptions TEST_OPTIONS = new WorldOptions((long)"test1".hashCode(), true, false);
   protected final Screen lastScreen;
   private Button deleteButton;
   private Button selectButton;
   private Button renameButton;
   private Button copyButton;
   protected EditBox searchBox;
   private WorldSelectionList list;

   public SelectWorldScreen(Screen var1) {
      super(Component.translatable("selectWorld.title"));
      this.lastScreen = var1;
   }

   protected void init() {
      this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, Component.translatable("selectWorld.search"));
      this.searchBox.setResponder((var1) -> {
         this.list.updateFilter(var1);
      });
      this.addWidget(this.searchBox);
      this.list = (WorldSelectionList)this.addRenderableWidget(new WorldSelectionList(this, this.minecraft, this.width, this.height - 112, 48, 36, this.searchBox.getValue(), this.list));
      this.selectButton = (Button)this.addRenderableWidget(Button.builder(LevelSummary.PLAY_WORLD, (var1) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::joinWorld);
      }).bounds(this.width / 2 - 154, this.height - 52, 150, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.create"), (var1) -> {
         CreateWorldScreen.openFresh(this.minecraft, this);
      }).bounds(this.width / 2 + 4, this.height - 52, 150, 20).build());
      this.renameButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit"), (var1) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::editWorld);
      }).bounds(this.width / 2 - 154, this.height - 28, 72, 20).build());
      this.deleteButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.delete"), (var1) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::deleteWorld);
      }).bounds(this.width / 2 - 76, this.height - 28, 72, 20).build());
      this.copyButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.recreate"), (var1) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::recreateWorld);
      }).bounds(this.width / 2 + 4, this.height - 28, 72, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (var1) -> {
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width / 2 + 82, this.height - 28, 72, 20).build());
      this.updateButtonStatus((LevelSummary)null);
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.searchBox);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.searchBox.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 8, 16777215);
   }

   public void updateButtonStatus(@Nullable LevelSummary var1) {
      if (var1 == null) {
         this.selectButton.setMessage(LevelSummary.PLAY_WORLD);
         this.selectButton.active = false;
         this.renameButton.active = false;
         this.copyButton.active = false;
         this.deleteButton.active = false;
      } else {
         this.selectButton.setMessage(var1.primaryActionMessage());
         this.selectButton.active = var1.primaryActionActive();
         this.renameButton.active = var1.canEdit();
         this.copyButton.active = var1.canRecreate();
         this.deleteButton.active = var1.canDelete();
      }

   }

   public void removed() {
      if (this.list != null) {
         this.list.children().forEach(WorldSelectionList.Entry::close);
      }

   }

   // $FF: synthetic method
   private void lambda$init$7(Button var1) {
      try {
         String var2 = "DEBUG world";
         if (!this.list.children().isEmpty()) {
            WorldSelectionList.Entry var3 = (WorldSelectionList.Entry)this.list.children().get(0);
            if (var3 instanceof WorldSelectionList.WorldListEntry) {
               WorldSelectionList.WorldListEntry var4 = (WorldSelectionList.WorldListEntry)var3;
               if (var4.getLevelName().equals("DEBUG world")) {
                  var4.doDeleteWorld();
               }
            }
         }

         LevelSettings var6 = new LevelSettings("DEBUG world", GameType.SPECTATOR, false, Difficulty.NORMAL, true, new GameRules(), WorldDataConfiguration.DEFAULT);
         String var7 = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), "DEBUG world", "");
         this.minecraft.createWorldOpenFlows().createFreshLevel(var7, var6, TEST_OPTIONS, WorldPresets::createNormalWorldDimensions, this);
      } catch (IOException var5) {
         LOGGER.error("Failed to recreate the debug world", var5);
      }

   }
}
