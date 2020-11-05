package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;

public class SpecialRecipeBuilder {
   private final SimpleRecipeSerializer<?> serializer;

   public SpecialRecipeBuilder(SimpleRecipeSerializer<?> var1) {
      super();
      this.serializer = var1;
   }

   public static SpecialRecipeBuilder special(SimpleRecipeSerializer<?> var0) {
      return new SpecialRecipeBuilder(var0);
   }

   public void save(Consumer<FinishedRecipe> var1, final String var2) {
      var1.accept(new FinishedRecipe() {
         public void serializeRecipeData(JsonObject var1) {
         }

         public RecipeSerializer<?> getType() {
            return SpecialRecipeBuilder.this.serializer;
         }

         public ResourceLocation getId() {
            return new ResourceLocation(var2);
         }

         @Nullable
         public JsonObject serializeAdvancement() {
            return null;
         }

         public ResourceLocation getAdvancementId() {
            return new ResourceLocation("");
         }
      });
   }
}
