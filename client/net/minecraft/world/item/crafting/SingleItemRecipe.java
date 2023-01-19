package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public abstract class SingleItemRecipe implements Recipe<Container> {
   protected final Ingredient ingredient;
   protected final ItemStack result;
   private final RecipeType<?> type;
   private final RecipeSerializer<?> serializer;
   protected final ResourceLocation id;
   protected final String group;

   public SingleItemRecipe(RecipeType<?> var1, RecipeSerializer<?> var2, ResourceLocation var3, String var4, Ingredient var5, ItemStack var6) {
      super();
      this.type = var1;
      this.serializer = var2;
      this.id = var3;
      this.group = var4;
      this.ingredient = var5;
      this.result = var6;
   }

   @Override
   public RecipeType<?> getType() {
      return this.type;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return this.serializer;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   @Override
   public String getGroup() {
      return this.group;
   }

   @Override
   public ItemStack getResultItem() {
      return this.result;
   }

   @Override
   public NonNullList<Ingredient> getIngredients() {
      NonNullList var1 = NonNullList.create();
      var1.add(this.ingredient);
      return var1;
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return true;
   }

   @Override
   public ItemStack assemble(Container var1) {
      return this.result.copy();
   }

   public static class Serializer<T extends SingleItemRecipe> implements RecipeSerializer<T> {
      final SingleItemRecipe.Serializer.SingleItemMaker<T> factory;

      protected Serializer(SingleItemRecipe.Serializer.SingleItemMaker<T> var1) {
         super();
         this.factory = var1;
      }

      public T fromJson(ResourceLocation var1, JsonObject var2) {
         String var3 = GsonHelper.getAsString(var2, "group", "");
         Ingredient var4;
         if (GsonHelper.isArrayNode(var2, "ingredient")) {
            var4 = Ingredient.fromJson(GsonHelper.getAsJsonArray(var2, "ingredient"));
         } else {
            var4 = Ingredient.fromJson(GsonHelper.getAsJsonObject(var2, "ingredient"));
         }

         String var5 = GsonHelper.getAsString(var2, "result");
         int var6 = GsonHelper.getAsInt(var2, "count");
         ItemStack var7 = new ItemStack(Registry.ITEM.get(new ResourceLocation(var5)), var6);
         return this.factory.create(var1, var3, var4, var7);
      }

      public T fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
         String var3 = var2.readUtf();
         Ingredient var4 = Ingredient.fromNetwork(var2);
         ItemStack var5 = var2.readItem();
         return this.factory.create(var1, var3, var4, var5);
      }

      public void toNetwork(FriendlyByteBuf var1, T var2) {
         var1.writeUtf(var2.group);
         var2.ingredient.toNetwork(var1);
         var1.writeItem(var2.result);
      }

      interface SingleItemMaker<T extends SingleItemRecipe> {
         T create(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4);
      }
   }
}
