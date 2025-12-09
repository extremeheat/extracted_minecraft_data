package net.minecraft.client.gui.screens.worldselection;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FileUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelSummary;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SelectWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final WorldOptions TEST_OPTIONS = new WorldOptions((long)"test1".hashCode(), true, false);
   protected final Screen lastScreen;
   private final HeaderAndFooterLayout layout;
   private @Nullable Button deleteButton;
   private @Nullable Button selectButton;
   private @Nullable Button renameButton;
   private @Nullable Button copyButton;
   protected @Nullable EditBox searchBox;
   private @Nullable WorldSelectionList list;

   public SelectWorldScreen(Screen var1) {
      super(Component.translatable("selectWorld.title"));
      Objects.requireNonNull(Minecraft.getInstance().font);
      this.layout = new HeaderAndFooterLayout(this, 8 + 9 + 8 + 20 + 4, 60);
      this.lastScreen = var1;
   }

   protected void init() {
      LinearLayout var1 = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(4));
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(new StringWidget(this.title, this.font));
      LinearLayout var2 = (LinearLayout)var1.addChild(LinearLayout.horizontal().spacing(4));
      if (SharedConstants.DEBUG_WORLD_RECREATE) {
         var2.addChild(this.createDebugWorldRecreateButton());
      }

      this.searchBox = (EditBox)var2.addChild(new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, Component.translatable("selectWorld.search")));
      this.searchBox.setResponder((var1x) -> {
         if (this.list != null) {
            this.list.updateFilter(var1x);
         }

      });
      this.searchBox.setHint(Component.translatable("gui.selectWorld.search").setStyle(EditBox.SEARCH_HINT_STYLE));
      Consumer var3 = WorldSelectionList.WorldListEntry::joinWorld;
      this.list = (WorldSelectionList)this.layout.addToContents((new WorldSelectionList.Builder(this.minecraft, this)).width(this.width).height(this.layout.getContentHeight()).filter(this.searchBox.getValue()).oldList(this.list).onEntrySelect(this::updateButtonStatus).onEntryInteract(var3).build());
      this.createFooterButtons(var3, this.list);
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
      this.updateButtonStatus((LevelSummary)null);
   }

   private void createFooterButtons(Consumer<WorldSelectionList.WorldListEntry> var1, WorldSelectionList var2) {
      GridLayout var3 = (GridLayout)this.layout.addToFooter((new GridLayout()).columnSpacing(8).rowSpacing(4));
      var3.defaultCellSetting().alignHorizontallyCenter();
      GridLayout.RowHelper var4 = var3.createRowHelper(4);
      this.selectButton = (Button)var4.addChild(Button.builder(LevelSummary.PLAY_WORLD, (var2x) -> var2.getSelectedOpt().ifPresent(var1)).build(), 2);
      var4.addChild(Button.builder(Component.translatable("selectWorld.create"), (var2x) -> {
         Minecraft var10000 = this.minecraft;
         Objects.requireNonNull(var2);
         CreateWorldScreen.openFresh(var10000, var2::returnToScreen);
      }).build(), 2);
      this.renameButton = (Button)var4.addChild(Button.builder(Component.translatable("selectWorld.edit"), (var1x) -> var2.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::editWorld)).width(71).build());
      this.deleteButton = (Button)var4.addChild(Button.builder(Component.translatable("selectWorld.delete"), (var1x) -> var2.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::deleteWorld)).width(71).build());
      this.copyButton = (Button)var4.addChild(Button.builder(Component.translatable("selectWorld.recreate"), (var1x) -> var2.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::recreateWorld)).width(71).build());
      var4.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> this.minecraft.setScreen(this.lastScreen)).width(71).build());
   }

   private Button createDebugWorldRecreateButton() {
      return Button.builder(Component.literal("DEBUG recreate"), (var1) -> {
         try {
            String var2 = "DEBUG world";
            if (this.list != null && !this.list.children().isEmpty()) {
               WorldSelectionList.Entry var3 = (WorldSelectionList.Entry)this.list.children().getFirst();
               if (var3 instanceof WorldSelectionList.WorldListEntry) {
                  WorldSelectionList.WorldListEntry var4 = (WorldSelectionList.WorldListEntry)var3;
                  if (var4.getLevelName().equals("DEBUG world")) {
                     var4.doDeleteWorld();
                  }
               }
            }

            LevelSettings var6 = new LevelSettings("DEBUG world", GameType.SPECTATOR, false, Difficulty.NORMAL, true, new GameRules(WorldDataConfiguration.DEFAULT.enabledFeatures()), WorldDataConfiguration.DEFAULT);
            String var7 = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), "DEBUG world", "");
            this.minecraft.createWorldOpenFlows().createFreshLevel(var7, var6, TEST_OPTIONS, WorldPresets::createNormalWorldDimensions, this);
         } catch (IOException var5) {
            LOGGER.error("Failed to recreate the debug world", var5);
         }

      }).width(72).build();
   }

   protected void repositionElements() {
      if (this.list != null) {
         this.list.updateSize(this.width, this.layout);
      }

      this.layout.arrangeElements();
   }

   protected void setInitialFocus() {
      if (this.searchBox != null) {
         this.setInitialFocus(this.searchBox);
      }

   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void updateButtonStatus(@Nullable LevelSummary var1) {
      if (this.selectButton != null && this.renameButton != null && this.copyButton != null && this.deleteButton != null) {
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
   }

   public void removed() {
      if (this.list != null) {
         this.list.children().forEach(WorldSelectionList.Entry::close);
      }

   }
}
