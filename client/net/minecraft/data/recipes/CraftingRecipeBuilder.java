package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.CraftingBookCategory;

public abstract class CraftingRecipeBuilder {
   public CraftingRecipeBuilder() {
      super();
   }

   protected static CraftingBookCategory determineBookCategory(RecipeCategory var0) {
      return switch(var0) {
         case BUILDING_BLOCKS -> CraftingBookCategory.BUILDING;
         case TOOLS, COMBAT -> CraftingBookCategory.EQUIPMENT;
         case REDSTONE -> CraftingBookCategory.REDSTONE;
         default -> CraftingBookCategory.MISC;
      };
   }

   protected abstract static class CraftingResult implements FinishedRecipe {
      private final CraftingBookCategory category;

      protected CraftingResult(CraftingBookCategory var1) {
         super();
         this.category = var1;
      }

      @Override
      public void serializeRecipeData(JsonObject var1) {
         var1.addProperty("category", this.category.getSerializedName());
      }
   }
}
