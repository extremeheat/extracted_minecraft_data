package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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
   public ItemStack getResultItem(HolderLookup.Provider var1) {
      return this.result;
   }

   @Override
   public NonNullList<Ingredient> getIngredients() {
      return this.ingredients;
   }

   public boolean matches(CraftingInput var1, Level var2) {
      if (var1.ingredientCount() != this.ingredients.size()) {
         return false;
      } else {
         return var1.size() == 1 && this.ingredients.size() == 1
            ? ((Ingredient)this.ingredients.getFirst()).test(var1.getItem(0))
            : var1.stackedContents().canCraft(this, null);
      }
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      return this.result.copy();
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= this.ingredients.size();
   }

   public static class Serializer implements RecipeSerializer<ShapelessRecipe> {
      private static final MapCodec<ShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  Codec.STRING.optionalFieldOf("group", "").forGetter(var0x -> var0x.group),
                  CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(var0x -> var0x.category),
                  ItemStack.STRICT_CODEC.fieldOf("result").forGetter(var0x -> var0x.result),
                  Ingredient.CODEC_NONEMPTY
                     .listOf()
                     .fieldOf("ingredients")
                     .flatXmap(
                        var0x -> {
                           Ingredient[] var1 = var0x.stream().filter(var0xx -> !var0xx.isEmpty()).toArray(Ingredient[]::new);
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
      public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> STREAM_CODEC = StreamCodec.of(
         ShapelessRecipe.Serializer::toNetwork, ShapelessRecipe.Serializer::fromNetwork
      );

      public Serializer() {
         super();
      }

      @Override
      public MapCodec<ShapelessRecipe> codec() {
         return CODEC;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static ShapelessRecipe fromNetwork(RegistryFriendlyByteBuf var0) {
         String var1 = var0.readUtf();
         CraftingBookCategory var2 = var0.readEnum(CraftingBookCategory.class);
         int var3 = var0.readVarInt();
         NonNullList var4 = NonNullList.withSize(var3, Ingredient.EMPTY);
         var4.replaceAll(var1x -> Ingredient.CONTENTS_STREAM_CODEC.decode(var0));
         ItemStack var5 = ItemStack.STREAM_CODEC.decode(var0);
         return new ShapelessRecipe(var1, var2, var5, var4);
      }

      private static void toNetwork(RegistryFriendlyByteBuf var0, ShapelessRecipe var1) {
         var0.writeUtf(var1.group);
         var0.writeEnum(var1.category);
         var0.writeVarInt(var1.ingredients.size());

         for (Ingredient var3 : var1.ingredients) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(var0, var3);
         }

         ItemStack.STREAM_CODEC.encode(var0, var1.result);
      }
   }
}
