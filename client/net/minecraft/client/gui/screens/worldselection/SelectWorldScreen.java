package net.minecraft.client.gui.screens.worldselection;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.WorldOptions;
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

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      return super.mouseScrolled(var1, var3, var5);
   }

   @Override
   public void tick() {
      this.searchBox.tick();
   }

   @Override
   protected void init() {
      this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, Component.translatable("selectWorld.search"));
      this.searchBox.setResponder(var1 -> this.list.updateFilter(var1));
      this.list = new WorldSelectionList(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, this.searchBox.getValue(), this.list);
      this.addWidget(this.searchBox);
      this.addWidget(this.list);
      this.selectButton = this.addRenderableWidget(
         Button.builder(
               Component.translatable("selectWorld.select"), var1 -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::joinWorld)
            )
            .bounds(this.width / 2 - 154, this.height - 52, 150, 20)
            .build()
      );
      this.addRenderableWidget(
         Button.builder(Component.translatable("selectWorld.create"), var1 -> CreateWorldScreen.openFresh(this.minecraft, this))
            .bounds(this.width / 2 + 4, this.height - 52, 150, 20)
            .build()
      );
      this.renameButton = this.addRenderableWidget(
         Button.builder(Component.translatable("selectWorld.edit"), var1 -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::editWorld))
            .bounds(this.width / 2 - 154, this.height - 28, 72, 20)
            .build()
      );
      this.deleteButton = this.addRenderableWidget(
         Button.builder(
               Component.translatable("selectWorld.delete"), var1 -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::deleteWorld)
            )
            .bounds(this.width / 2 - 76, this.height - 28, 72, 20)
            .build()
      );
      this.copyButton = this.addRenderableWidget(
         Button.builder(
               Component.translatable("selectWorld.recreate"), var1 -> this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::recreateWorld)
            )
            .bounds(this.width / 2 + 4, this.height - 28, 72, 20)
            .build()
      );
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_CANCEL, var1 -> this.minecraft.setScreen(this.lastScreen))
            .bounds(this.width / 2 + 82, this.height - 28, 72, 20)
            .build()
      );
      this.updateButtonStatus(false);
      this.setInitialFocus(this.searchBox);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return super.keyPressed(var1, var2, var3) ? true : this.searchBox.keyPressed(var1, var2, var3);
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return this.searchBox.charTyped(var1, var2);
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.list.render(var1, var2, var3, var4);
      this.searchBox.render(var1, var2, var3, var4);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 8, 16777215);
      super.render(var1, var2, var3, var4);
   }

   public void updateButtonStatus(boolean var1) {
      this.selectButton.active = var1;
      this.deleteButton.active = var1;
      this.renameButton.active = var1;
      this.copyButton.active = var1;
   }

   @Override
   public void removed() {
      if (this.list != null) {
         this.list.children().forEach(WorldSelectionList.Entry::close);
      }
   }
}
