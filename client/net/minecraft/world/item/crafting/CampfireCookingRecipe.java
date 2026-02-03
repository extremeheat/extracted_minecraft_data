package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

public class CampfireCookingRecipe extends AbstractCookingRecipe {
   public static final MapCodec<CampfireCookingRecipe> MAP_CODEC = cookingMapCodec(CampfireCookingRecipe::new, 100);
   public static final StreamCodec<RegistryFriendlyByteBuf, CampfireCookingRecipe> STREAM_CODEC = cookingStreamCodec(CampfireCookingRecipe::new);
   public static final RecipeSerializer<CampfireCookingRecipe> SERIALIZER;

   public CampfireCookingRecipe(final Recipe.CommonInfo commonInfo, final AbstractCookingRecipe.CookingBookInfo bookInfo, final Ingredient ingredient, final ItemStackTemplate result, final float experience, final int cookingTime) {
      super(commonInfo, bookInfo, ingredient, result, experience, cookingTime);
   }

   protected Item furnaceIcon() {
      return Items.CAMPFIRE;
   }

   public RecipeSerializer<CampfireCookingRecipe> getSerializer() {
      return SERIALIZER;
   }

   public RecipeType<CampfireCookingRecipe> getType() {
      return RecipeType.CAMPFIRE_COOKING;
   }

   public RecipeBookCategory recipeBookCategory() {
      return RecipeBookCategories.CAMPFIRE;
   }

   static {
      SERIALIZER = new RecipeSerializer<CampfireCookingRecipe>(MAP_CODEC, STREAM_CODEC);
   }
}
