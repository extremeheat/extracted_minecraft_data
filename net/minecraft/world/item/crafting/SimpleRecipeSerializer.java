package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class SimpleRecipeSerializer implements RecipeSerializer {
   private final Function constructor;

   public SimpleRecipeSerializer(Function var1) {
      this.constructor = var1;
   }

   public Recipe fromJson(ResourceLocation var1, JsonObject var2) {
      return (Recipe)this.constructor.apply(var1);
   }

   public Recipe fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
      return (Recipe)this.constructor.apply(var1);
   }

   public void toNetwork(FriendlyByteBuf var1, Recipe var2) {
   }
}
