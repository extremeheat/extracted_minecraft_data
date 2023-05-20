package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

public class UpdateOneTwentyAdventureAdvancements implements AdvancementSubProvider {
   public UpdateOneTwentyAdventureAdvancements() {
      super();
   }

   @Override
   public void generate(HolderLookup.Provider var1, Consumer<Advancement> var2) {
      Advancement var3 = AdvancementSubProvider.createPlaceholder("adventure/sleep_in_bed");
      VanillaAdventureAdvancements.createAdventuringTime(var2, var3, MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD_UPDATE_1_20);
   }
}
