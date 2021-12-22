package net.minecraft.client.gui.screens.worldselection;

import com.mojang.blaze3d.vertex.PoseStack;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectWorldScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final Screen lastScreen;
   @Nullable
   private List<FormattedCharSequence> toolTip;
   private Button deleteButton;
   private Button selectButton;
   private Button renameButton;
   private Button copyButton;
   protected EditBox searchBox;
   private WorldSelectionList list;

   public SelectWorldScreen(Screen var1) {
      super(new TranslatableComponent("selectWorld.title"));
      this.lastScreen = var1;
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return super.mouseScrolled(var1, var3, var5);
   }

   public void tick() {
      this.searchBox.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, new TranslatableComponent("selectWorld.search"));
      this.searchBox.setResponder((var1) -> {
         this.list.refreshList(() -> {
            return var1;
         }, false);
      });
      this.list = new WorldSelectionList(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, () -> {
         return this.searchBox.getValue();
      }, this.list);
      this.addWidget(this.searchBox);
      this.addWidget(this.list);
      this.selectButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 154, this.height - 52, 150, 20, new TranslatableComponent("selectWorld.select"), (var1) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::joinWorld);
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 4, this.height - 52, 150, 20, new TranslatableComponent("selectWorld.create"), (var1) -> {
         this.minecraft.setScreen(CreateWorldScreen.create(this));
      }));
      this.renameButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 154, this.height - 28, 72, 20, new TranslatableComponent("selectWorld.edit"), (var1) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::editWorld);
      }));
      this.deleteButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 76, this.height - 28, 72, 20, new TranslatableComponent("selectWorld.delete"), (var1) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::deleteWorld);
      }));
      this.copyButton = (Button)this.addRenderableWidget(new Button(this.width / 2 + 4, this.height - 28, 72, 20, new TranslatableComponent("selectWorld.recreate"), (var1) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::recreateWorld);
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 82, this.height - 28, 72, 20, CommonComponents.GUI_CANCEL, (var1) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.updateButtonStatus(false);
      this.setInitialFocus(this.searchBox);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return super.keyPressed(var1, var2, var3) ? true : this.searchBox.keyPressed(var1, var2, var3);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public boolean charTyped(char var1, int var2) {
      return this.searchBox.charTyped(var1, var2);
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.toolTip = null;
      this.list.render(var1, var2, var3, var4);
      this.searchBox.render(var1, var2, var3, var4);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 8, 16777215);
      super.render(var1, var2, var3, var4);
      if (this.toolTip != null) {
         this.renderTooltip(var1, this.toolTip, var2, var3);
      }

   }

   public void setToolTip(List<FormattedCharSequence> var1) {
      this.toolTip = var1;
   }

   public void updateButtonStatus(boolean var1) {
      this.selectButton.active = var1;
      this.deleteButton.active = var1;
      this.renameButton.active = var1;
      this.copyButton.active = var1;
   }

   public void removed() {
      if (this.list != null) {
         this.list.children().forEach(WorldSelectionList.WorldListEntry::close);
      }

   }

   // $FF: synthetic method
   private void lambda$init$9(Button var1) {
      try {
         String var2 = "DEBUG world";
         if (!this.list.children().isEmpty()) {
            WorldSelectionList.WorldListEntry var3 = (WorldSelectionList.WorldListEntry)this.list.children().get(0);
            if (var3.getLevelName().equals("DEBUG world")) {
               var3.doDeleteWorld();
            }
         }

         RegistryAccess.RegistryHolder var10 = RegistryAccess.builtin();
         long var4 = (long)"test1".hashCode();
         WorldGenSettings var6 = WorldPreset.NORMAL.create(var10, var4, true, false);
         LevelSettings var7 = new LevelSettings("DEBUG world", GameType.SPECTATOR, false, Difficulty.NORMAL, true, new GameRules(), DataPackConfig.DEFAULT);
         String var8 = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), "DEBUG world", "");
         this.minecraft.createLevel(var8, var7, var10, var6);
      } catch (IOException var9) {
         LOGGER.error("Failed to recreate the debug world", var9);
      }

   }
}
