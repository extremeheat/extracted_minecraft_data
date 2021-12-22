package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public interface FinishedRecipe {
   void serializeRecipeData(JsonObject var1);

   default JsonObject serializeRecipe() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("type", Registry.RECIPE_SERIALIZER.getKey(this.getType()).toString());
      this.serializeRecipeData(var1);
      return var1;
   }

   ResourceLocation getId();

   RecipeSerializer<?> getType();

   @Nullable
   JsonObject serializeAdvancement();

   @Nullable
   ResourceLocation getAdvancementId();
}
