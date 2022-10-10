package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.ResourceLocation;

public class CustomRecipeBuilder {
   private final RecipeSerializers.SimpleSerializer<?> field_200501_a;

   public CustomRecipeBuilder(RecipeSerializers.SimpleSerializer<?> var1) {
      super();
      this.field_200501_a = var1;
   }

   public static CustomRecipeBuilder func_200500_a(RecipeSerializers.SimpleSerializer<?> var0) {
      return new CustomRecipeBuilder(var0);
   }

   public void func_200499_a(Consumer<IFinishedRecipe> var1, final String var2) {
      var1.accept(new IFinishedRecipe() {
         public JsonObject func_200441_a() {
            JsonObject var1 = new JsonObject();
            var1.addProperty("type", CustomRecipeBuilder.this.field_200501_a.func_199567_a());
            return var1;
         }

         public ResourceLocation func_200442_b() {
            return new ResourceLocation(var2);
         }

         @Nullable
         public JsonObject func_200440_c() {
            return null;
         }

         @Nullable
         public ResourceLocation func_200443_d() {
            return new ResourceLocation("");
         }
      });
   }
}
