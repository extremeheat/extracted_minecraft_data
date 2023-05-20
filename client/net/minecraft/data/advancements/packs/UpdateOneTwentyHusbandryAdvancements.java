package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.world.entity.EntityType;

public class UpdateOneTwentyHusbandryAdvancements implements AdvancementSubProvider {
   public UpdateOneTwentyHusbandryAdvancements() {
      super();
   }

   @Override
   public void generate(HolderLookup.Provider var1, Consumer<Advancement> var2) {
      Advancement var3 = AdvancementSubProvider.createPlaceholder("husbandry/breed_an_animal");
      Stream var4 = Stream.concat(VanillaHusbandryAdvancements.BREEDABLE_ANIMALS.stream(), Stream.of(EntityType.CAMEL, EntityType.SNIFFER));
      VanillaHusbandryAdvancements.createBreedAllAnimalsAdvancement(var3, var2, var4, VanillaHusbandryAdvancements.INDIRECTLY_BREEDABLE_ANIMALS.stream());
   }
}
