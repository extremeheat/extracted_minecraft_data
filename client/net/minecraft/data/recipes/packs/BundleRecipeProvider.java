package net.minecraft.data.recipes.packs;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class BundleRecipeProvider extends RecipeProvider {
   public BundleRecipeProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super(var1, var2);
   }

   protected void buildRecipes(RecipeOutput var1) {
      ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.BUNDLE).define('#', (ItemLike)Items.RABBIT_HIDE).define('-', (ItemLike)Items.STRING).pattern("-#-").pattern("# #").pattern("###").unlockedBy("has_string", has(Items.STRING)).save(var1);
   }
}
