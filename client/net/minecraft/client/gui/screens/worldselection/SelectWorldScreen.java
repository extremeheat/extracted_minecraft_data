package net.minecraft.client.gui.screens.worldselection;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.slf4j.Logger;

public class SelectWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
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
      super(Component.translatable("selectWorld.title"));
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
      this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, Component.translatable("selectWorld.search"));
      this.searchBox.setResponder((var1) -> {
         this.list.refreshList(var1);
      });
      this.list = new WorldSelectionList(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, this.getFilterSupplier(), this.list);
      this.addWidget(this.searchBox);
      this.addWidget(this.list);
      this.selectButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 154, this.height - 52, 150, 20, Component.translatable("selectWorld.select"), (var1) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::joinWorld);
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 4, this.height - 52, 150, 20, Component.translatable("selectWorld.create"), (var1) -> {
         CreateWorldScreen.openFresh(this.minecraft, this);
      }));
      this.renameButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 154, this.height - 28, 72, 20, Component.translatable("selectWorld.edit"), (var1) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::editWorld);
      }));
      this.deleteButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 76, this.height - 28, 72, 20, Component.translatable("selectWorld.delete"), (var1) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::deleteWorld);
      }));
      this.copyButton = (Button)this.addRenderableWidget(new Button(this.width / 2 + 4, this.height - 28, 72, 20, Component.translatable("selectWorld.recreate"), (var1) -> {
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
         this.list.children().forEach(WorldSelectionList.Entry::close);
      }

   }

   public Supplier<String> getFilterSupplier() {
      return () -> {
         return this.searchBox.getValue();
      };
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

         RegistryAccess.Frozen var8 = RegistryAccess.builtinCopy().freeze();
         WorldGenSettings var9 = WorldPresets.createNormalWorldFromPreset(var8, (long)"test1".hashCode());
         LevelSettings var5 = new LevelSettings("DEBUG world", GameType.SPECTATOR, false, Difficulty.NORMAL, true, new GameRules(), DataPackConfig.DEFAULT);
         String var6 = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), "DEBUG world", "");
         this.minecraft.createWorldOpenFlows().createFreshLevel(var6, var5, var8, var9);
      } catch (IOException var7) {
         LOGGER.error("Failed to recreate the debug world", var7);
      }

   }
}
