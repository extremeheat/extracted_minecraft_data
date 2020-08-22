package net.minecraft.world.item.crafting;

import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;

public interface RecipeType {
   RecipeType CRAFTING = register("crafting");
   RecipeType SMELTING = register("smelting");
   RecipeType BLASTING = register("blasting");
   RecipeType SMOKING = register("smoking");
   RecipeType CAMPFIRE_COOKING = register("campfire_cooking");
   RecipeType STONECUTTING = register("stonecutting");

   static RecipeType register(final String var0) {
      return (RecipeType)Registry.register(Registry.RECIPE_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new RecipeType() {
         public String toString() {
            return var0;
         }
      });
   }

   default Optional tryMatch(Recipe var1, Level var2, Container var3) {
      return var1.matches(var3, var2) ? Optional.of(var1) : Optional.empty();
   }
}
