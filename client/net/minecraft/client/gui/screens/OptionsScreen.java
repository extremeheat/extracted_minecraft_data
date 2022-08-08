package net.minecraft.client.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.Difficulty;

public class OptionsScreen extends Screen {
   private final Screen lastScreen;
   private final Options options;
   private CycleButton<Difficulty> difficultyButton;
   private LockIconButton lockButton;

   public OptionsScreen(Screen var1, Options var2) {
      super(Component.translatable("options.title"));
      this.lastScreen = var1;
      this.options = var2;
   }

   protected void init() {
      int var1 = 0;
      OptionInstance[] var2 = new OptionInstance[]{this.options.fov()};
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         OptionInstance var5 = var2[var4];
         int var6 = this.width / 2 - 155 + var1 % 2 * 160;
         int var7 = this.height / 6 - 12 + 24 * (var1 >> 1);
         this.addRenderableWidget(var5.createButton(this.minecraft.options, var6, var7, 150));
         ++var1;
      }

      if (this.minecraft.level != null && this.minecraft.hasSingleplayerServer()) {
         this.difficultyButton = (CycleButton)this.addRenderableWidget(createDifficultyButton(var1, this.width, this.height, "options.difficulty", this.minecraft));
         if (!this.minecraft.level.getLevelData().isHardcore()) {
            this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
            this.lockButton = (LockIconButton)this.addRenderableWidget(new LockIconButton(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y, (var1x) -> {
               this.minecraft.setScreen(new ConfirmScreen(this::lockCallback, Component.translatable("difficulty.lock.title"), Component.translatable("difficulty.lock.question", this.minecraft.level.getLevelData().getDifficulty().getDisplayName())));
            }));
            this.lockButton.setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
            this.lockButton.active = !this.lockButton.isLocked();
            this.difficultyButton.active = !this.lockButton.isLocked();
         } else {
            this.difficultyButton.active = false;
         }
      } else {
         this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 6 - 12 + 24 * (var1 >> 1), 150, 20, Component.translatable("options.online"), (var1x) -> {
            this.minecraft.setScreen(new OnlineOptionsScreen(this, this.options));
         }));
      }

      this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, Component.translatable("options.skinCustomisation"), (var1x) -> {
         this.minecraft.setScreen(new SkinCustomizationScreen(this, this.options));
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, Component.translatable("options.sounds"), (var1x) -> {
         this.minecraft.setScreen(new SoundOptionsScreen(this, this.options));
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, Component.translatable("options.video"), (var1x) -> {
         this.minecraft.setScreen(new VideoSettingsScreen(this, this.options));
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, Component.translatable("options.controls"), (var1x) -> {
         this.minecraft.setScreen(new ControlsScreen(this, this.options));
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, Component.translatable("options.language"), (var1x) -> {
         this.minecraft.setScreen(new LanguageSelectScreen(this, this.options, this.minecraft.getLanguageManager()));
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, Component.translatable("options.chat.title"), (var1x) -> {
         this.minecraft.setScreen(new ChatOptionsScreen(this, this.options));
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, Component.translatable("options.resourcepack"), (var1x) -> {
         this.minecraft.setScreen(new PackSelectionScreen(this, this.minecraft.getResourcePackRepository(), this::updatePackList, this.minecraft.getResourcePackDirectory(), Component.translatable("resourcePack.title")));
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, Component.translatable("options.accessibility.title"), (var1x) -> {
         this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.options));
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, CommonComponents.GUI_DONE, (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public static CycleButton<Difficulty> createDifficultyButton(int var0, int var1, int var2, String var3, Minecraft var4) {
      return CycleButton.builder(Difficulty::getDisplayName).withValues((Object[])Difficulty.values()).withInitialValue(var4.level.getDifficulty()).create(var1 / 2 - 155 + var0 % 2 * 160, var2 / 6 - 12 + 24 * (var0 >> 1), 150, 20, Component.translatable(var3), (var1x, var2x) -> {
         var4.getConnection().send((Packet)(new ServerboundChangeDifficultyPacket(var2x)));
      });
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
}
