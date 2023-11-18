package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public interface FinishedRecipe {
   void serializeRecipeData(JsonObject var1);

   default JsonObject serializeRecipe() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("type", BuiltInRegistries.RECIPE_SERIALIZER.getKey(this.type()).toString());
      this.serializeRecipeData(var1);
      return var1;
   }

   ResourceLocation id();

   RecipeSerializer<?> type();

   @Nullable
   AdvancementHolder advancement();
}
