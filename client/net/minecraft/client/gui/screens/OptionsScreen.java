package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.FrameWidget;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.LinearLayoutWidget;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.SpacerWidget;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.telemetry.TelemetryInfoScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.Difficulty;

public class OptionsScreen extends Screen {
   private static final Component SKIN_CUSTOMIZATION = Component.translatable("options.skinCustomisation");
   private static final Component SOUNDS = Component.translatable("options.sounds");
   private static final Component VIDEO = Component.translatable("options.video");
   private static final Component CONTROLS = Component.translatable("options.controls");
   private static final Component LANGUAGE = Component.translatable("options.language");
   private static final Component CHAT = Component.translatable("options.chat.title");
   private static final Component RESOURCEPACK = Component.translatable("options.resourcepack");
   private static final Component ACCESSIBILITY = Component.translatable("options.accessibility.title");
   private static final Component TELEMETRY = Component.translatable("options.telemetry");
   private static final int COLUMNS = 2;
   private final Screen lastScreen;
   private final Options options;
   private CycleButton<Difficulty> difficultyButton;
   private LockIconButton lockButton;

   public OptionsScreen(Screen var1, Options var2) {
      super(Component.translatable("options.title"));
      this.lastScreen = var1;
      this.options = var2;
   }

   @Override
   protected void init() {
      GridWidget var1 = new GridWidget();
      var1.defaultCellSetting().paddingHorizontal(5).paddingBottom(4).alignHorizontallyCenter();
      GridWidget.RowHelper var2 = var1.createRowHelper(2);
      var2.addChild(this.options.fov().createButton(this.minecraft.options, 0, 0, 150));
      var2.addChild(this.createOnlineButton());
      var2.addChild(SpacerWidget.height(26), 2);
      var2.addChild(this.openScreenButton(SKIN_CUSTOMIZATION, () -> new SkinCustomizationScreen(this, this.options)));
      var2.addChild(this.openScreenButton(SOUNDS, () -> new SoundOptionsScreen(this, this.options)));
      var2.addChild(this.openScreenButton(VIDEO, () -> new VideoSettingsScreen(this, this.options)));
      var2.addChild(this.openScreenButton(CONTROLS, () -> new ControlsScreen(this, this.options)));
      var2.addChild(this.openScreenButton(LANGUAGE, () -> new LanguageSelectScreen(this, this.options, this.minecraft.getLanguageManager())));
      var2.addChild(this.openScreenButton(CHAT, () -> new ChatOptionsScreen(this, this.options)));
      var2.addChild(
         this.openScreenButton(
            RESOURCEPACK,
            () -> new PackSelectionScreen(
                  this,
                  this.minecraft.getResourcePackRepository(),
                  this::updatePackList,
                  this.minecraft.getResourcePackDirectory(),
                  Component.translatable("resourcePack.title")
               )
         )
      );
      var2.addChild(this.openScreenButton(ACCESSIBILITY, () -> new AccessibilityOptionsScreen(this, this.options)));
      var2.addChild(this.openScreenButton(TELEMETRY, () -> new TelemetryInfoScreen(this, this.options)));
      var2.addChild(
         Button.builder(CommonComponents.GUI_DONE, var1x -> this.minecraft.setScreen(this.lastScreen)).width(200).build(),
         2,
         var2.newCellSettings().paddingTop(6)
      );
      var1.pack();
      FrameWidget.alignInRectangle(var1, 0, this.height / 6 - 12, this.width, this.height, 0.5F, 0.0F);
      this.addRenderableWidget(var1);
   }

   private AbstractWidget createOnlineButton() {
      if (this.minecraft.level != null && this.minecraft.hasSingleplayerServer()) {
         this.difficultyButton = createDifficultyButton(0, 0, "options.difficulty", this.minecraft);
         if (!this.minecraft.level.getLevelData().isHardcore()) {
            this.lockButton = new LockIconButton(
               0,
               0,
               var1x -> this.minecraft
                     .setScreen(
                        new ConfirmScreen(
                           this::lockCallback,
                           Component.translatable("difficulty.lock.title"),
                           Component.translatable("difficulty.lock.question", this.minecraft.level.getLevelData().getDifficulty().getDisplayName())
                        )
                     )
            );
            this.difficultyButton.setWidth(this.difficultyButton.getWidth() - this.lockButton.getWidth());
            this.lockButton.setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
            this.lockButton.active = !this.lockButton.isLocked();
            this.difficultyButton.active = !this.lockButton.isLocked();
            LinearLayoutWidget var1 = new LinearLayoutWidget(150, 0, LinearLayoutWidget.Orientation.HORIZONTAL);
            var1.addChild(this.difficultyButton);
            var1.addChild(this.lockButton);
            var1.pack();
            return var1;
         } else {
            this.difficultyButton.active = false;
            return this.difficultyButton;
         }
      } else {
         return Button.builder(
               Component.translatable("options.online"),
               var1x -> this.minecraft.setScreen(OnlineOptionsScreen.createOnlineOptionsScreen(this.minecraft, this, this.options))
            )
            .bounds(this.width / 2 + 5, this.height / 6 - 12 + 24, 150, 20)
            .build();
      }
   }

   public static CycleButton<Difficulty> createDifficultyButton(int var0, int var1, String var2, Minecraft var3) {
      return CycleButton.builder(Difficulty::getDisplayName)
         .withValues(Difficulty.values())
         .withInitialValue(var3.level.getDifficulty())
         .create(var0, var1, 150, 20, Component.translatable(var2), (var1x, var2x) -> var3.getConnection().send(new ServerboundChangeDifficultyPacket(var2x)));
   }

   private void updatePackList(PackRepository var1) {
      ImmutableList var2 = ImmutableList.copyOf(this.options.resourcePacks);
      this.options.resourcePacks.clear();
      this.options.incompatibleResourcePacks.clear();

      for(Pack var4 : var1.getSelectedPacks()) {
         if (!var4.isFixedPosition()) {
            this.options.resourcePacks.add(var4.getId());
            if (!var4.getCompatibility().isCompatible()) {
               this.options.incompatibleResourcePacks.add(var4.getId());
            }
         }
      }

      this.options.save();
      ImmutableList var5 = ImmutableList.copyOf(this.options.resourcePacks);
      if (!var5.equals(var2)) {
         this.minecraft.reloadResourcePacks();
      }
   }

   private void lockCallback(boolean var1) {
      this.minecraft.setScreen(this);
      if (var1 && this.minecraft.level != null) {
         this.minecraft.getConnection().send(new ServerboundLockDifficultyPacket(true));
         this.lockButton.setLocked(true);
         this.lockButton.active = false;
         this.difficultyButton.active = false;
      }
   }

   @Override
   public void removed() {
      this.options.save();
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 15, 16777215);
      super.render(var1, var2, var3, var4);
   }

   private Button openScreenButton(Component var1, Supplier<Screen> var2) {
      return Button.builder(var1, var2x -> this.minecraft.setScreen((Screen)var2.get())).build();
   }
}
