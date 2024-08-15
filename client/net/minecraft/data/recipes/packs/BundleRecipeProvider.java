package net.minecraft.data.recipes.packs;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

public class BundleRecipeProvider extends RecipeProvider {
   BundleRecipeProvider(HolderLookup.Provider var1, RecipeOutput var2) {
      super(var1, var2);
   }

   @Override
   protected void buildRecipes() {
      this.shaped(RecipeCategory.TOOLS, Items.BUNDLE)
         .define('-', Items.STRING)
         .define('#', Items.LEATHER)
         .pattern("-")
         .pattern("#")
         .unlockedBy("has_string", this.has(Items.STRING))
         .save(this.output);
   }

   public static class Runner extends RecipeProvider.Runner {
      public Runner(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
         super(var1, var2);
      }

      @Override
      protected RecipeProvider createRecipeProvider(HolderLookup.Provider var1, RecipeOutput var2) {
         return new BundleRecipeProvider(var1, var2);
      }

      @Override
      public String getName() {
         return "Bundle Recipes";
      }
   }
}
