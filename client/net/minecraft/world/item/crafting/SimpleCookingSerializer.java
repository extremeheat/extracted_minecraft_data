package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class SimpleCookingSerializer<T extends AbstractCookingRecipe> implements RecipeSerializer<T> {
   private final int defaultCookingTime;
   private final SimpleCookingSerializer.CookieBaker<T> factory;

   public SimpleCookingSerializer(SimpleCookingSerializer.CookieBaker<T> var1, int var2) {
      super();
      this.defaultCookingTime = var2;
      this.factory = var1;
   }

   public T fromJson(ResourceLocation var1, JsonObject var2) {
      String var3 = GsonHelper.getAsString(var2, "group", "");
      Object var4 = GsonHelper.isArrayNode(var2, "ingredient") ? GsonHelper.getAsJsonArray(var2, "ingredient") : GsonHelper.getAsJsonObject(var2, "ingredient");
      Ingredient var5 = Ingredient.fromJson((JsonElement)var4);
      String var6 = GsonHelper.getAsString(var2, "result");
      ResourceLocation var7 = new ResourceLocation(var6);
      ItemStack var8 = new ItemStack((ItemLike)Registry.ITEM.getOptional(var7).orElseThrow(() -> {
         return new IllegalStateException("Item: " + var6 + " does not exist");
      }));
      float var9 = GsonHelper.getAsFloat(var2, "experience", 0.0F);
      int var10 = GsonHelper.getAsInt(var2, "cookingtime", this.defaultCookingTime);
      return this.factory.create(var1, var3, var5, var8, var9, var10);
   }

   public T fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
      String var3 = var2.readUtf(32767);
      Ingredient var4 = Ingredient.fromNetwork(var2);
      ItemStack var5 = var2.readItem();
      float var6 = var2.readFloat();
      int var7 = var2.readVarInt();
      return this.factory.create(var1, var3, var4, var5, var6, var7);
   }

   public void toNetwork(FriendlyByteBuf var1, T var2) {
      var1.writeUtf(var2.group);
      var2.ingredient.toNetwork(var1);
      var1.writeItem(var2.result);
      var1.writeFloat(var2.experience);
      var1.writeVarInt(var2.cookingTime);
   }

   // $FF: synthetic method
   public Recipe fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
      return this.fromNetwork(var1, var2);
   }

   // $FF: synthetic method
   public Recipe fromJson(ResourceLocation var1, JsonObject var2) {
      return this.fromJson(var1, var2);
   }

   interface CookieBaker<T extends AbstractCookingRecipe> {
      T create(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4, float var5, int var6);
   }
}
