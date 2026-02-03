package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

public class SmeltingRecipe extends AbstractCookingRecipe {
   public static final MapCodec<SmeltingRecipe> MAP_CODEC = cookingMapCodec(SmeltingRecipe::new, 200);
   public static final StreamCodec<RegistryFriendlyByteBuf, SmeltingRecipe> STREAM_CODEC = cookingStreamCodec(SmeltingRecipe::new);
   public static final RecipeSerializer<SmeltingRecipe> SERIALIZER;

   public SmeltingRecipe(final Recipe.CommonInfo commonInfo, final AbstractCookingRecipe.CookingBookInfo bookInfo, final Ingredient ingredient, final ItemStackTemplate result, final float experience, final int cookingTime) {
      super(commonInfo, bookInfo, ingredient, result, experience, cookingTime);
   }

   protected Item furnaceIcon() {
      return Items.FURNACE;
   }

   public RecipeSerializer<SmeltingRecipe> getSerializer() {
      return SERIALIZER;
   }

   public RecipeType<SmeltingRecipe> getType() {
      return RecipeType.SMELTING;
   }

   public RecipeBookCategory recipeBookCategory() {
      RecipeBookCategory var10000;
      switch (this.category()) {
         case BLOCKS -> var10000 = RecipeBookCategories.FURNACE_BLOCKS;
         case FOOD -> var10000 = RecipeBookCategories.FURNACE_FOOD;
         case MISC -> var10000 = RecipeBookCategories.FURNACE_MISC;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   static {
      SERIALIZER = new RecipeSerializer<SmeltingRecipe>(MAP_CODEC, STREAM_CODEC);
   }
}
