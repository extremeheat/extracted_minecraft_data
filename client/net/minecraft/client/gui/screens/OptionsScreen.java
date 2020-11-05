package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.Difficulty;

public class OptionsScreen extends Screen {
   private static final Option[] OPTION_SCREEN_OPTIONS;
   private final Screen lastScreen;
   private final Options options;
   private Button difficultyButton;
   private LockIconButton lockButton;
   private Difficulty currentDifficulty;

   public OptionsScreen(Screen var1, Options var2) {
      super(new TranslatableComponent("options.title"));
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
               this.minecraft.setScreen(new ConfirmScreen(this::lockCallback, new TranslatableComponent("difficulty.lock.title"), new TranslatableComponent("difficulty.lock.question", new Object[]{new TranslatableComponent("options.difficulty." + this.minecraft.level.getLevelData().getDifficulty().getKey())})));
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

      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, new TranslatableComponent("options.skinCustomisation"), (var1x) -> {
         this.minecraft.setScreen(new SkinCustomizationScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, new TranslatableComponent("options.sounds"), (var1x) -> {
         this.minecraft.setScreen(new SoundOptionsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, new TranslatableComponent("options.video"), (var1x) -> {
         this.minecraft.setScreen(new VideoSettingsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, new TranslatableComponent("options.controls"), (var1x) -> {
         this.minecraft.setScreen(new ControlsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, new TranslatableComponent("options.language"), (var1x) -> {
         this.minecraft.setScreen(new LanguageSelectScreen(this, this.options, this.minecraft.getLanguageManager()));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, new TranslatableComponent("options.chat.title"), (var1x) -> {
         this.minecraft.setScreen(new ChatOptionsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, new TranslatableComponent("options.resourcepack"), (var1x) -> {
         this.minecraft.setScreen(new PackSelectionScreen(this, this.minecraft.getResourcePackRepository(), this::updatePackList, this.minecraft.getResourcePackDirectory(), new TranslatableComponent("resourcePack.title")));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, new TranslatableComponent("options.accessibility.title"), (var1x) -> {
         this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, CommonComponents.GUI_DONE, (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   private void updatePackList(PackRepository var1) {
      ImmutableList var2 = ImmutableList.copyOf(this.options.resourcePacks);
      this.options.resourcePacks.clear();
      this.options.incompatibleResourcePacks.clear();
      Iterator var3 = var1.getSelectedPacks().iterator();

      while(var3.hasNext()) {
         Pack var4 = (Pack)var3.next();
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

   private Component getDifficultyText(Difficulty var1) {
      return (new TranslatableComponent("options.difficulty")).append(": ").append(var1.getDisplayName());
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

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 15, 16777215);
      super.render(var1, var2, var3, var4);
   }

   static {
      OPTION_SCREEN_OPTIONS = new Option[]{Option.FOV};
   }
}
