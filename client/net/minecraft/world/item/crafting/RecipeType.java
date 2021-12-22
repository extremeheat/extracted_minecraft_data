package net.minecraft.world.item.crafting;

import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;

public interface RecipeType<T extends Recipe<?>> {
   RecipeType<CraftingRecipe> CRAFTING = register("crafting");
   RecipeType<SmeltingRecipe> SMELTING = register("smelting");
   RecipeType<BlastingRecipe> BLASTING = register("blasting");
   RecipeType<SmokingRecipe> SMOKING = register("smoking");
   RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking");
   RecipeType<StonecutterRecipe> STONECUTTING = register("stonecutting");
   RecipeType<UpgradeRecipe> SMITHING = register("smithing");

   static <T extends Recipe<?>> RecipeType<T> register(final String var0) {
      return (RecipeType)Registry.register(Registry.RECIPE_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new RecipeType<T>() {
         public String toString() {
            return var0;
         }
      });
   }

   default <C extends Container> Optional<T> tryMatch(Recipe<C> var1, Level var2, C var3) {
      return var1.matches(var3, var2) ? Optional.of(var1) : Optional.empty();
   }
}
