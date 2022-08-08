package net.minecraft.world.item.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShapelessRecipe implements CraftingRecipe {
   private final ResourceLocation id;
   final String group;
   final ItemStack result;
   final NonNullList<Ingredient> ingredients;

   public ShapelessRecipe(ResourceLocation var1, String var2, ItemStack var3, NonNullList<Ingredient> var4) {
      super();
      this.id = var1;
      this.group = var2;
      this.result = var3;
      this.ingredients = var4;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SHAPELESS_RECIPE;
   }

   public String getGroup() {
      return this.group;
   }

   public ItemStack getResultItem() {
      return this.result;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.ingredients;
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      StackedContents var3 = new StackedContents();
      int var4 = 0;

      for(int var5 = 0; var5 < var1.getContainerSize(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            ++var4;
            var3.accountStack(var6, 1);
         }
      }

      return var4 == this.ingredients.size() && var3.canCraft(this, (IntList)null);
   }

   public ItemStack assemble(CraftingContainer var1) {
      return this.result.copy();
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= this.ingredients.size();
   }

   public static class Serializer implements RecipeSerializer<ShapelessRecipe> {
      public Serializer() {
         super();
      }

      public ShapelessRecipe fromJson(ResourceLocation var1, JsonObject var2) {
         String var3 = GsonHelper.getAsString(var2, "group", "");
         NonNullList var4 = itemsFromJson(GsonHelper.getAsJsonArray(var2, "ingredients"));
         if (var4.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
         } else if (var4.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
         } else {
            ItemStack var5 = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(var2, "result"));
            return new ShapelessRecipe(var1, var3, var5, var4);
         }
      }

      private static NonNullList<Ingredient> itemsFromJson(JsonArray var0) {
         NonNullList var1 = NonNullList.create();

         for(int var2 = 0; var2 < var0.size(); ++var2) {
            Ingredient var3 = Ingredient.fromJson(var0.get(var2));
            if (!var3.isEmpty()) {
               var1.add(var3);
            }
         }

         return var1;
      }

      public ShapelessRecipe fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
         String var3 = var2.readUtf();
         int var4 = var2.readVarInt();
         NonNullList var5 = NonNullList.withSize(var4, Ingredient.EMPTY);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            var5.set(var6, Ingredient.fromNetwork(var2));
         }

         ItemStack var7 = var2.readItem();
         return new ShapelessRecipe(var1, var3, var7, var5);
      }

      public void toNetwork(FriendlyByteBuf var1, ShapelessRecipe var2) {
         var1.writeUtf(var2.group);
         var1.writeVarInt(var2.ingredients.size());
         Iterator var3 = var2.ingredients.iterator();

         while(var3.hasNext()) {
            Ingredient var4 = (Ingredient)var3.next();
            var4.toNetwork(var1);
         }

         var1.writeItem(var2.result);
      }

      // $FF: synthetic method
      public Recipe fromNetwork(ResourceLocation var1, FriendlyByteBuf var2) {
         return this.fromNetwork(var1, var2);
      }

      // $FF: synthetic method
      public Recipe fromJson(ResourceLocation var1, JsonObject var2) {
         return this.fromJson(var1, var2);
      }
   }
}
