package net.minecraft.data.recipes;

import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SpecialRecipeBuilder extends CraftingRecipeBuilder {
   final RecipeSerializer<?> serializer;

   public SpecialRecipeBuilder(RecipeSerializer<?> var1) {
      super();
      this.serializer = var1;
   }

   public static SpecialRecipeBuilder special(RecipeSerializer<? extends CraftingRecipe> var0) {
      return new SpecialRecipeBuilder(var0);
   }

   public void save(RecipeOutput var1, String var2) {
      this.save(var1, new ResourceLocation(var2));
   }

   public void save(RecipeOutput var1, final ResourceLocation var2) {
      var1.accept(new CraftingRecipeBuilder.CraftingResult(CraftingBookCategory.MISC) {
         @Override
         public RecipeSerializer<?> type() {
            return SpecialRecipeBuilder.this.serializer;
         }

         @Override
         public ResourceLocation id() {
            return var2;
         }

         @Nullable
         @Override
         public AdvancementHolder advancement() {
            return null;
         }
      });
   }
}
