package net.minecraft.data.advancements.packs;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

public class WinterDropAdventureAdvancements implements AdvancementSubProvider {
   public WinterDropAdventureAdvancements() {
      super();
   }

   public void generate(HolderLookup.Provider var1, Consumer<AdvancementHolder> var2) {
      AdvancementHolder var3 = AdvancementSubProvider.createPlaceholder("adventure/root");
      VanillaAdventureAdvancements.createMonsterHunterAdvancement(var3, var2, var1.lookupOrThrow(Registries.ENTITY_TYPE), (List)Stream.concat(VanillaAdventureAdvancements.MOBS_TO_KILL.stream(), Stream.of(EntityType.CREAKING_TRANSIENT)).collect(Collectors.toList()));
      AdvancementHolder var4 = AdvancementSubProvider.createPlaceholder("adventure/sleep_in_bed");
      VanillaAdventureAdvancements.createAdventuringTime(var1, var2, var4, MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD_WINTER_DROP);
   }
}
