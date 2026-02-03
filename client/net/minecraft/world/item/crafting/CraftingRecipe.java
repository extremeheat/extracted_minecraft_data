package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public interface CraftingRecipe extends Recipe<CraftingInput> {
   default RecipeType<CraftingRecipe> getType() {
      return RecipeType.CRAFTING;
   }

   RecipeSerializer<? extends CraftingRecipe> getSerializer();

   CraftingBookCategory category();

   default NonNullList<ItemStack> getRemainingItems(final CraftingInput input) {
      return defaultCraftingReminder(input);
   }

   static NonNullList<ItemStack> defaultCraftingReminder(final CraftingInput input) {
      NonNullList<ItemStack> result = NonNullList.<ItemStack>withSize(input.size(), ItemStack.EMPTY);

      for(int slot = 0; slot < result.size(); ++slot) {
         Item item = input.getItem(slot).getItem();
         ItemStackTemplate remainder = item.getCraftingRemainder();
         result.set(slot, remainder != null ? remainder.create() : ItemStack.EMPTY);
      }

      return result;
   }

   default RecipeBookCategory recipeBookCategory() {
      RecipeBookCategory var10000;
      switch (this.category()) {
         case BUILDING -> var10000 = RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
         case EQUIPMENT -> var10000 = RecipeBookCategories.CRAFTING_EQUIPMENT;
         case REDSTONE -> var10000 = RecipeBookCategories.CRAFTING_REDSTONE;
         case MISC -> var10000 = RecipeBookCategories.CRAFTING_MISC;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static record CraftingBookInfo(CraftingBookCategory category, String group) implements Recipe.BookInfo<CraftingBookCategory> {
      public static final MapCodec<CraftingBookInfo> MAP_CODEC;
      public static final StreamCodec<RegistryFriendlyByteBuf, CraftingBookInfo> STREAM_CODEC;

      public CraftingBookInfo {
         super();
      }

      static {
         MAP_CODEC = Recipe.BookInfo.mapCodec(CraftingBookCategory.CODEC, CraftingBookCategory.MISC, CraftingBookInfo::new);
         STREAM_CODEC = Recipe.BookInfo.streamCodec(CraftingBookCategory.STREAM_CODEC, CraftingBookInfo::new);
      }
   }
}
