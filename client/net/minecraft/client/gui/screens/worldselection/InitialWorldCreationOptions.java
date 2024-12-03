package net.minecraft.client.gui.screens.worldselection;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

public record InitialWorldCreationOptions(WorldCreationUiState.SelectedGameMode selectedGameMode, Set<GameRules.Key<GameRules.BooleanValue>> disabledGameRules, @Nullable ResourceKey<FlatLevelGeneratorPreset> flatLevelPreset) {
   public InitialWorldCreationOptions(WorldCreationUiState.SelectedGameMode var1, Set<GameRules.Key<GameRules.BooleanValue>> var2, @Nullable ResourceKey<FlatLevelGeneratorPreset> var3) {
      super();
      this.selectedGameMode = var1;
      this.disabledGameRules = var2;
      this.flatLevelPreset = var3;
   }
}
