package net.minecraft.client.gui.screens.worldselection;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import org.jspecify.annotations.Nullable;

public record InitialWorldCreationOptions(WorldCreationUiState.SelectedGameMode selectedGameMode, GameRuleMap gameRuleOverwrites, @Nullable ResourceKey<FlatLevelGeneratorPreset> flatLevelPreset) {
   public InitialWorldCreationOptions(WorldCreationUiState.SelectedGameMode var1, GameRuleMap var2, @Nullable ResourceKey<FlatLevelGeneratorPreset> var3) {
      super();
      this.selectedGameMode = var1;
      this.gameRuleOverwrites = var2;
      this.flatLevelPreset = var3;
   }
}
