package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public interface Recipe<C extends Container> {
   Codec<Recipe<?>> CODEC = BuiltInRegistries.RECIPE_SERIALIZER.byNameCodec().dispatch(Recipe::getSerializer, RecipeSerializer::codec);
   StreamCodec<RegistryFriendlyByteBuf, Recipe<?>> STREAM_CODEC = ByteBufCodecs.registry(Registries.RECIPE_SERIALIZER)
      .dispatch(Recipe::getSerializer, RecipeSerializer::streamCodec);

   boolean matches(C var1, Level var2);

   ItemStack assemble(C var1, HolderLookup.Provider var2);

   boolean canCraftInDimensions(int var1, int var2);

   ItemStack getResultItem(HolderLookup.Provider var1);

   default NonNullList<ItemStack> getRemainingItems(C var1) {
      NonNullList var2 = NonNullList.withSize(var1.getContainerSize(), ItemStack.EMPTY);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         Item var4 = var1.getItem(var3).getItem();
         if (var4.hasCraftingRemainingItem()) {
            var2.set(var3, new ItemStack(var4.getCraftingRemainingItem()));
         }
      }

      return var2;
   }

   default NonNullList<Ingredient> getIngredients() {
      return NonNullList.create();
   }

   default boolean isSpecial() {
      return false;
   }

   default boolean showNotification() {
      return true;
   }

   default String getGroup() {
      return "";
   }

   default ItemStack getToastSymbol() {
      return new ItemStack(Blocks.CRAFTING_TABLE);
   }

   RecipeSerializer<?> getSerializer();

   RecipeType<?> getType();

   default boolean isIncomplete() {
      NonNullList var1 = this.getIngredients();
      return var1.isEmpty() || var1.stream().anyMatch(var0 -> var0.getItems().length == 0);
   }
}