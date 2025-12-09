package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.task.RealmCreationTask;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.level.storage.LevelSummary;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class RealmsSelectFileToUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Component TITLE = Component.translatable("mco.upload.select.world.title");
   private static final Component UNABLE_TO_LOAD_WORLD = Component.translatable("selectWorld.unable_to_load");
   private final @Nullable RealmCreationTask realmCreationTask;
   private final RealmsResetWorldScreen lastScreen;
   private final long realmId;
   private final int slotId;
   private final HeaderAndFooterLayout layout;
   protected @Nullable EditBox searchBox;
   private @Nullable WorldSelectionList list;
   private @Nullable Button uploadButton;

   public RealmsSelectFileToUploadScreen(@Nullable RealmCreationTask var1, long var2, int var4, RealmsResetWorldScreen var5) {
      super(TITLE);
      Objects.requireNonNull(Minecraft.getInstance().font);
      this.layout = new HeaderAndFooterLayout(this, 8 + 9 + 8 + 20 + 4, 33);
      this.realmCreationTask = var1;
      this.lastScreen = var5;
      this.realmId = var2;
      this.slotId = var4;
   }

   public void init() {
      LinearLayout var1 = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(4));
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(new StringWidget(this.title, this.font));
      this.searchBox = (EditBox)var1.addChild(new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, Component.translatable("selectWorld.search")));
      this.searchBox.setResponder((var1x) -> {
         if (this.list != null) {
            this.list.updateFilter(var1x);
         }

      });

      try {
         this.list = (WorldSelectionList)this.layout.addToContents((new WorldSelectionList.Builder(this.minecraft, this)).width(this.width).height(this.layout.getContentHeight()).filter(this.searchBox.getValue()).oldList(this.list).uploadWorld().onEntrySelect(this::updateButtonState).onEntryInteract(this::upload).build());
      } catch (Exception var3) {
         LOGGER.error("Couldn't load level list", var3);
         this.minecraft.setScreen(new RealmsGenericErrorScreen(UNABLE_TO_LOAD_WORLD, Component.nullToEmpty(var3.getMessage()), this.lastScreen));
         return;
      }

      LinearLayout var2 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var2.defaultCellSetting().alignHorizontallyCenter();
      this.uploadButton = (Button)var2.addChild(Button.builder(Component.translatable("mco.upload.button.name"), (var1x) -> this.list.getSelectedOpt().ifPresent(this::upload)).build());
      var2.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> this.onClose()).build());
      this.updateButtonState((LevelSummary)null);
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      if (this.list != null) {
         this.list.updateSize(this.width, this.layout);
      }

      this.layout.arrangeElements();
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.searchBox);
   }

   private void updateButtonState(@Nullable LevelSummary var1) {
      if (this.list != null && this.uploadButton != null) {
         this.uploadButton.active = this.list.getSelected() != null;
      }

   }

   private void upload(WorldSelectionList.WorldListEntry var1) {
      this.minecraft.setScreen(new RealmsUploadScreen(this.realmCreationTask, this.realmId, this.slotId, this.lastScreen, var1.getLevelSummary()));
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }
}
