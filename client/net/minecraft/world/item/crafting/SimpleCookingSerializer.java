package net.minecraft.world.item.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

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
      CookingBookCategory var4 = CookingBookCategory.CODEC.byName(GsonHelper.getAsString(var2, "category", null), CookingBookCategory.MISC);
      Object var5 = GsonHelper.isArrayNode(var2, "ingredient")
         ? GsonHelper.getAsJsonArray(var2, "ingredient")
         : GsonHelper.getAsJsonObject(var2, "ingredient");
      Ingredient var6 = Ingredient.fromJson((JsonElement)var5);
      String var7 = GsonHelper.getAsString(var2, "result");
      ResourceLocation var8 = new ResourceLocation(var7);
      ItemStack var9 = new ItemStack(
         BuiltInRegistries.ITEM.getOptional(var8).orElseThrow(() -> new IllegalStateException("Item: " + var7 + " does not exist"))
      );
      float var10 = GsonHelper.getAsFloat(var2, "experience", 0.0F);
      int var11 = GsonHelper.getAsInt(var2, "cookingtime", this.defaultCookingTime);
      return this.factory.create(var1, var3, var4, var6, var9, var10, var11);
   }

   public T fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
      String var3 = var2.readUtf();
      CookingBookCategory var4 = var2.readEnum(CookingBookCategory.class);
      Ingredient var5 = Ingredient.fromNetwork(var2);
      ItemStack var6 = var2.readItem();
      float var7 = var2.readFloat();
      int var8 = var2.readVarInt();
      return this.factory.create(var1, var3, var4, var5, var6, var7, var8);
   }

   public void toNetwork(FriendlyByteBuf var1, T var2) {
      var1.writeUtf(var2.group);
      var1.writeEnum(var2.category());
      var2.ingredient.toNetwork(var1);
      var1.writeItem(var2.result);
      var1.writeFloat(var2.experience);
      var1.writeVarInt(var2.cookingTime);
   }

   interface CookieBaker<T extends AbstractCookingRecipe> {
      T create(ResourceLocation var1, String var2, CookingBookCategory var3, Ingredient var4, ItemStack var5, float var6, int var7);
   }
}
