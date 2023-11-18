package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShapelessRecipe implements CraftingRecipe {
   final String group;
   final CraftingBookCategory category;
   final ItemStack result;
   final NonNullList<Ingredient> ingredients;

   public ShapelessRecipe(String var1, CraftingBookCategory var2, ItemStack var3, NonNullList<Ingredient> var4) {
      super();
      this.group = var1;
      this.category = var2;
      this.result = var3;
      this.ingredients = var4;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SHAPELESS_RECIPE;
   }

   @Override
   public String getGroup() {
      return this.group;
   }

   @Override
   public CraftingBookCategory category() {
      return this.category;
   }

   @Override
   public ItemStack getResultItem(RegistryAccess var1) {
      return this.result;
   }

   @Override
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

      return var4 == this.ingredients.size() && var3.canCraft(this, null);
   }

   public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
      return this.result.copy();
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= this.ingredients.size();
   }

   public static class Serializer implements RecipeSerializer<ShapelessRecipe> {
      private static final Codec<ShapelessRecipe> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(var0x -> var0x.group),
                  CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(var0x -> var0x.category),
                  CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(var0x -> var0x.result),
                  Ingredient.CODEC_NONEMPTY
                     .listOf()
                     .fieldOf("ingredients")
                     .flatXmap(
                        var0x -> {
                           Ingredient[] var1 = var0x.stream().filter(var0xx -> !var0xx.isEmpty()).toArray(var0xx -> new Ingredient[var0xx]);
                           if (var1.length == 0) {
                              return DataResult.error(() -> "No ingredients for shapeless recipe");
                           } else {
                              return var1.length > 9
                                 ? DataResult.error(() -> "Too many ingredients for shapeless recipe")
                                 : DataResult.success(NonNullList.of(Ingredient.EMPTY, var1));
                           }
                        },
                        DataResult::success
                     )
                     .forGetter(var0x -> var0x.ingredients)
               )
               .apply(var0, ShapelessRecipe::new)
      );

      public Serializer() {
         super();
      }

      @Override
      public Codec<ShapelessRecipe> codec() {
         return CODEC;
      }

      public ShapelessRecipe fromNetwork(FriendlyByteBuf var1) {
         String var2 = var1.readUtf();
         CraftingBookCategory var3 = var1.readEnum(CraftingBookCategory.class);
         int var4 = var1.readVarInt();
         NonNullList var5 = NonNullList.withSize(var4, Ingredient.EMPTY);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            var5.set(var6, Ingredient.fromNetwork(var1));
         }

         ItemStack var7 = var1.readItem();
         return new ShapelessRecipe(var2, var3, var7, var5);
      }

      public void toNetwork(FriendlyByteBuf var1, ShapelessRecipe var2) {
         var1.writeUtf(var2.group);
         var1.writeEnum(var2.category);
         var1.writeVarInt(var2.ingredients.size());

         for(Ingredient var4 : var2.ingredients) {
            var4.toNetwork(var1);
         }

         var1.writeItem(var2.result);
      }
   }
}
