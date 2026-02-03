package net.minecraft.client.gui.screens.options;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.world.Difficulty;

public class DifficultyButtons {
   public DifficultyButtons() {
      super();
   }

   public static LayoutElement create(final Minecraft minecraft, final Screen screen) {
      CycleButton<Difficulty> difficultyButton = CycleButton.builder(Difficulty::getDisplayName, minecraft.level.getDifficulty()).withValues(Difficulty.values()).create(0, 0, 150, 20, Component.translatable("options.difficulty"), (button, value) -> minecraft.getConnection().send(new ServerboundChangeDifficultyPacket(value)));
      LockIconButton lockButton = new LockIconButton(0, 0, (button) -> minecraft.setScreen(new ConfirmScreen((result) -> onLockCallback(result, minecraft, screen, difficultyButton, (LockIconButton)button), Component.translatable("difficulty.lock.title"), Component.translatable("difficulty.lock.question", minecraft.level.getLevelData().getDifficulty().getDisplayName()))));
      difficultyButton.setWidth(difficultyButton.getWidth() - lockButton.getWidth());
      lockButton.setLocked(isDifficultyLocked(minecraft));
      lockButton.active = !lockButton.isLocked() && playerHasPermissionToChangeDifficulty(minecraft);
      difficultyButton.active = !lockButton.isLocked() && playerHasPermissionToChangeDifficulty(minecraft);
      EqualSpacingLayout linearLayout = new EqualSpacingLayout(150, 0, EqualSpacingLayout.Orientation.HORIZONTAL);
      linearLayout.addChild(difficultyButton);
      linearLayout.addChild(lockButton);
      return linearLayout;
   }

   private static boolean isDifficultyLocked(Minecraft minecraft) {
      return minecraft.level.getLevelData().isDifficultyLocked() || minecraft.level.getLevelData().isHardcore();
   }

   private static boolean playerHasPermissionToChangeDifficulty(Minecraft minecraft) {
      return minecraft.hasSingleplayerServer();
   }

   private static void onLockCallback(final boolean result, final Minecraft minecraft, final Screen screen, final CycleButton<Difficulty> difficultyButton, final LockIconButton lockButton) {
      minecraft.setScreen(screen);
      if (result && minecraft.level != null) {
         minecraft.getConnection().send(new ServerboundLockDifficultyPacket(true));
         lockButton.setLocked(true);
         lockButton.active = false;
         difficultyButton.active = false;
      }

   }
}
