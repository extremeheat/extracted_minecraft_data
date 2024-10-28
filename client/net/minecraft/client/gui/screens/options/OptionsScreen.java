package net.minecraft.client.gui.screens.options;

import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.telemetry.TelemetryInfoScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.Difficulty;

public class OptionsScreen extends Screen {
   private static final Component TITLE = Component.translatable("options.title");
   private static final Component SKIN_CUSTOMIZATION = Component.translatable("options.skinCustomisation");
   private static final Component SOUNDS = Component.translatable("options.sounds");
   private static final Component VIDEO = Component.translatable("options.video");
   private static final Component CONTROLS = Component.translatable("options.controls");
   private static final Component LANGUAGE = Component.translatable("options.language");
   private static final Component CHAT = Component.translatable("options.chat");
   private static final Component RESOURCEPACK = Component.translatable("options.resourcepack");
   private static final Component ACCESSIBILITY = Component.translatable("options.accessibility");
   private static final Component TELEMETRY = Component.translatable("options.telemetry");
   private static final Tooltip TELEMETRY_DISABLED_TOOLTIP = Tooltip.create(Component.translatable("options.telemetry.disabled"));
   private static final Component CREDITS_AND_ATTRIBUTION = Component.translatable("options.credits_and_attribution");
   private static final int COLUMNS = 2;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 33);
   private final Screen lastScreen;
   private final Options options;
   @Nullable
   private CycleButton<Difficulty> difficultyButton;
   @Nullable
   private LockIconButton lockButton;

   public OptionsScreen(Screen var1, Options var2) {
      super(TITLE);
      this.lastScreen = var1;
      this.options = var2;
   }

   protected void init() {
      LinearLayout var1 = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(8));
      var1.addChild(new StringWidget(TITLE, this.font), (Consumer)(LayoutSettings::alignHorizontallyCenter));
      LinearLayout var2 = ((LinearLayout)var1.addChild(LinearLayout.horizontal())).spacing(8);
      var2.addChild(this.options.fov().createButton(this.minecraft.options));
      var2.addChild(this.createOnlineButton());
      GridLayout var3 = new GridLayout();
      var3.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
      GridLayout.RowHelper var4 = var3.createRowHelper(2);
      var4.addChild(this.openScreenButton(SKIN_CUSTOMIZATION, () -> {
         return new SkinCustomizationScreen(this, this.options);
      }));
      var4.addChild(this.openScreenButton(SOUNDS, () -> {
         return new SoundOptionsScreen(this, this.options);
      }));
      var4.addChild(this.openScreenButton(VIDEO, () -> {
         return new VideoSettingsScreen(this, this.minecraft, this.options);
      }));
      var4.addChild(this.openScreenButton(CONTROLS, () -> {
         return new ControlsScreen(this, this.options);
      }));
      var4.addChild(this.openScreenButton(LANGUAGE, () -> {
         return new LanguageSelectScreen(this, this.options, this.minecraft.getLanguageManager());
      }));
      var4.addChild(this.openScreenButton(CHAT, () -> {
         return new ChatOptionsScreen(this, this.options);
      }));
      var4.addChild(this.openScreenButton(RESOURCEPACK, () -> {
         return new PackSelectionScreen(this.minecraft.getResourcePackRepository(), this::applyPacks, this.minecraft.getResourcePackDirectory(), Component.translatable("resourcePack.title"));
      }));
      var4.addChild(this.openScreenButton(ACCESSIBILITY, () -> {
         return new AccessibilityOptionsScreen(this, this.options);
      }));
      Button var5 = (Button)var4.addChild(this.openScreenButton(TELEMETRY, () -> {
         return new TelemetryInfoScreen(this, this.options);
      }));
      if (!this.minecraft.allowsTelemetry()) {
         var5.active = false;
         var5.setTooltip(TELEMETRY_DISABLED_TOOLTIP);
      }

      var4.addChild(this.openScreenButton(CREDITS_AND_ATTRIBUTION, () -> {
         return new CreditsAndAttributionScreen(this);
      }));
      this.layout.addToContents(var3);
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (var1x) -> {
         this.onClose();
      }).width(200).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private void applyPacks(PackRepository var1) {
      this.options.updateResourcePacks(var1);
      this.minecraft.setScreen(this);
   }

   private LayoutElement createOnlineButton() {
      if (this.minecraft.level != null && this.minecraft.hasSingleplayerServer()) {
         this.difficultyButton = createDifficultyButton(0, 0, "options.difficulty", this.minecraft);
         if (!this.minecraft.level.getLevelData().isHardcore()) {
            this.lockButton = new LockIconButton(0, 0, (var1x) -> {
               this.minecraft.setScreen(new ConfirmScreen(this::lockCallback, Component.translatable("difficulty.lock.title"), Component.translatable("difficulty.lock.question", this.minecraft.level.getLevelData().getDifficulty().getDisplayName())));
            });
            this.difficultyButton.setWidth(this.difficultyButton.getWidth() - this.lockButton.getWidth());
            this.lockButton.setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
            this.lockButton.active = !this.lockButton.isLocked();
            this.difficultyButton.active = !this.lockButton.isLocked();
            EqualSpacingLayout var1 = new EqualSpacingLayout(150, 0, EqualSpacingLayout.Orientation.HORIZONTAL);
            var1.addChild(this.difficultyButton);
            var1.addChild(this.lockButton);
            return var1;
         } else {
            this.difficultyButton.active = false;
            return this.difficultyButton;
         }
      } else {
         return Button.builder(Component.translatable("options.online"), (var1x) -> {
            this.minecraft.setScreen(new OnlineOptionsScreen(this, this.options));
         }).bounds(this.width / 2 + 5, this.height / 6 - 12 + 24, 150, 20).build();
      }
   }

   public static CycleButton<Difficulty> createDifficultyButton(int var0, int var1, String var2, Minecraft var3) {
      return CycleButton.builder(Difficulty::getDisplayName).withValues((Object[])Difficulty.values()).withInitialValue(var3.level.getDifficulty()).create(var0, var1, 150, 20, Component.translatable(var2), (var1x, var2x) -> {
         var3.getConnection().send(new ServerboundChangeDifficultyPacket(var2x));
      });
   }

   private void lockCallback(boolean var1) {
      this.minecraft.setScreen(this);
      if (var1 && this.minecraft.level != null && this.lockButton != null && this.difficultyButton != null) {
         this.minecraft.getConnection().send(new ServerboundLockDifficultyPacket(true));
         this.lockButton.setLocked(true);
         this.lockButton.active = false;
         this.difficultyButton.active = false;
      }

   }

   public void removed() {
      this.options.save();
   }

   private Button openScreenButton(Component var1, Supplier<Screen> var2) {
      return Button.builder(var1, (var2x) -> {
         this.minecraft.setScreen((Screen)var2.get());
      }).build();
   }
}
