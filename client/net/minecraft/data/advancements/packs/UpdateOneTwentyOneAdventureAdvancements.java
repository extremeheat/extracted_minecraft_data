package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.world.entity.EntityType;

public class UpdateOneTwentyOneAdventureAdvancements implements AdvancementSubProvider {
   public UpdateOneTwentyOneAdventureAdvancements() {
      super();
   }

   @Override
   public void generate(HolderLookup.Provider var1, Consumer<AdvancementHolder> var2) {
      AdvancementHolder var3 = AdvancementSubProvider.createPlaceholder("adventure/root");
      VanillaAdventureAdvancements.createMonsterHunterAdvancement(
         var3,
         var2,
         Stream.concat(VanillaAdventureAdvancements.MOBS_TO_KILL.stream(), Stream.of(EntityType.BREEZE, EntityType.BOGGED)).collect(Collectors.toList())
      );
   }
}
