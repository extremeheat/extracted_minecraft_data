package net.minecraft.data.recipes.packs;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.TransmuteRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

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
      this.bundleRecipes();
   }

   private void bundleRecipes() {
      Ingredient var1 = this.tag(ItemTags.BUNDLES);

      for (DyeColor var5 : DyeColor.values()) {
         TransmuteRecipeBuilder.transmute(RecipeCategory.TOOLS, var1, Ingredient.of(DyeItem.byColor(var5)), BundleItem.getByColor(var5))
            .group("bundle_dye")
            .unlockedBy("has_bundle", this.has(ItemTags.BUNDLES))
            .save(this.output);
      }
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
