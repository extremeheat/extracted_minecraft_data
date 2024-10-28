package net.minecraft.data.recipes;

import java.util.function.Function;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Recipe;

public class SpecialRecipeBuilder {
   private final Function<CraftingBookCategory, Recipe<?>> factory;

   public SpecialRecipeBuilder(Function<CraftingBookCategory, Recipe<?>> var1) {
      super();
      this.factory = var1;
   }

   public static SpecialRecipeBuilder special(Function<CraftingBookCategory, Recipe<?>> var0) {
      return new SpecialRecipeBuilder(var0);
   }

   public void save(RecipeOutput var1, String var2) {
      this.save(var1, ResourceKey.create(Registries.RECIPE, ResourceLocation.parse(var2)));
   }

   public void save(RecipeOutput var1, ResourceKey<Recipe<?>> var2) {
      var1.accept(var2, (Recipe)this.factory.apply(CraftingBookCategory.MISC), (AdvancementHolder)null);
   }
}
