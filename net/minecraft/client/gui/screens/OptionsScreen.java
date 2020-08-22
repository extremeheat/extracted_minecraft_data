package net.minecraft.client.gui.screens;

import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.resourcepacks.ResourcePackSelectScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.world.Difficulty;

public class OptionsScreen extends Screen {
   private static final Option[] OPTION_SCREEN_OPTIONS;
   private final Screen lastScreen;
   private final Options options;
   private Button difficultyButton;
   private LockIconButton lockButton;
   private Difficulty currentDifficulty;

   public OptionsScreen(Screen var1, Options var2) {
      super(new TranslatableComponent("options.title", new Object[0]));
      this.lastScreen = var1;
      this.options = var2;
   }

   protected void init() {
      int var1 = 0;
      Option[] var2 = OPTION_SCREEN_OPTIONS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Option var5 = var2[var4];
         int var6 = this.width / 2 - 155 + var1 % 2 * 160;
         int var7 = this.height / 6 - 12 + 24 * (var1 >> 1);
         this.addButton(var5.createButton(this.minecraft.options, var6, var7, 150));
         ++var1;
      }

      if (this.minecraft.level != null) {
         this.currentDifficulty = this.minecraft.level.getDifficulty();
         this.difficultyButton = (Button)this.addButton(new Button(this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), 150, 20, this.getDifficultyText(this.currentDifficulty), (var1x) -> {
            this.currentDifficulty = Difficulty.byId(this.currentDifficulty.getId() + 1);
            this.minecraft.getConnection().send((Packet)(new ServerboundChangeDifficultyPacket(this.currentDifficulty)));
            this.difficultyButton.setMessage(this.getDifficultyText(this.currentDifficulty));
         }));
         if (this.minecraft.hasSingleplayerServer() && !this.minecraft.level.getLevelData().isHardcore()) {
            this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
            this.lockButton = (LockIconButton)this.addButton(new LockIconButton(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y, (var1x) -> {
               this.minecraft.setScreen(new ConfirmScreen(this::lockCallback, new TranslatableComponent("difficulty.lock.title", new Object[0]), new TranslatableComponent("difficulty.lock.question", new Object[]{new TranslatableComponent("options.difficulty." + this.minecraft.level.getLevelData().getDifficulty().getKey(), new Object[0])})));
            }));
            this.lockButton.setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
            this.lockButton.active = !this.lockButton.isLocked();
            this.difficultyButton.active = !this.lockButton.isLocked();
         } else {
            this.difficultyButton.active = false;
         }
      } else {
         this.addButton(new OptionButton(this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), 150, 20, Option.REALMS_NOTIFICATIONS, Option.REALMS_NOTIFICATIONS.getMessage(this.options), (var1x) -> {
            Option.REALMS_NOTIFICATIONS.toggle(this.options);
            this.options.save();
            var1x.setMessage(Option.REALMS_NOTIFICATIONS.getMessage(this.options));
         }));
      }

      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.get("options.skinCustomisation"), (var1x) -> {
         this.minecraft.setScreen(new SkinCustomizationScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.get("options.sounds"), (var1x) -> {
         this.minecraft.setScreen(new SoundOptionsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.get("options.video"), (var1x) -> {
         this.minecraft.setScreen(new VideoSettingsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.get("options.controls"), (var1x) -> {
         this.minecraft.setScreen(new ControlsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.get("options.language"), (var1x) -> {
         this.minecraft.setScreen(new LanguageSelectScreen(this, this.options, this.minecraft.getLanguageManager()));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.get("options.chat.title"), (var1x) -> {
         this.minecraft.setScreen(new ChatOptionsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.get("options.resourcepack"), (var1x) -> {
         this.minecraft.setScreen(new ResourcePackSelectScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.get("options.accessibility.title"), (var1x) -> {
         this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.get("gui.done"), (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public String getDifficultyText(Difficulty var1) {
      return (new TranslatableComponent("options.difficulty", new Object[0])).append(": ").append(var1.getDisplayName()).getColoredString();
   }

   private void lockCallback(boolean var1) {
      this.minecraft.setScreen(this);
      if (var1 && this.minecraft.level != null) {
         this.minecraft.getConnection().send((Packet)(new ServerboundLockDifficultyPacket(true)));
         this.lockButton.setLocked(true);
         this.lockButton.active = false;
         this.difficultyButton.active = false;
      }

   }

   public void removed() {
      this.options.save();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 15, 16777215);
      super.render(var1, var2, var3);
   }

   static {
      OPTION_SCREEN_OPTIONS = new Option[]{Option.FOV};
   }
}
