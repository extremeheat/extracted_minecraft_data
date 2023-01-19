package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class SimpleCraftingRecipeSerializer<T extends CraftingRecipe> implements RecipeSerializer<T> {
   private final SimpleCraftingRecipeSerializer.Factory<T> constructor;

   public SimpleCraftingRecipeSerializer(SimpleCraftingRecipeSerializer.Factory<T> var1) {
      super();
      this.constructor = var1;
   }

   public T fromJson(ResourceLocation var1, JsonObject var2) {
      CraftingBookCategory var3 = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(var2, "category", null), CraftingBookCategory.MISC);
      return this.constructor.create(var1, var3);
   }

   public T fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
      CraftingBookCategory var3 = var2.readEnum(CraftingBookCategory.class);
      return this.constructor.create(var1, var3);
   }

   public void toNetwork(FriendlyByteBuf var1, T var2) {
      var1.writeEnum(var2.category());
   }

   @FunctionalInterface
   public interface Factory<T extends CraftingRecipe> {
      T create(ResourceLocation var1, CraftingBookCategory var2);
   }
}
