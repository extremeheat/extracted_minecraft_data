package net.minecraft.data.advancements.packs;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityType;

public class UpdateOneTwentyAdvancements extends VanillaHusbandryAdvancements {
   public UpdateOneTwentyAdvancements() {
      super();
   }

   @Override
   public void generate(HolderLookup.Provider var1, Consumer<Advancement> var2) {
      Advancement var3 = this.createRoot(var2);
      Advancement var4 = this.createBreedAnAnimalAdvancement(var3, var2);
      this.createBreedAllAnimalsAdvancement(var4, var2);
   }

   @Override
   public EntityType<?>[] getBreedableAnimals() {
      EntityType[] var1 = super.getBreedableAnimals();
      List var2 = Arrays.stream(var1).collect(Collectors.toList());
      var2.add(EntityType.CAMEL);
      return var2.toArray(var1);
   }
}
