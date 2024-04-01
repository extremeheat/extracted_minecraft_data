package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class PotatoRefinementRecipe implements Recipe<Container> {
   private final RecipeType<?> type = RecipeType.POTATO_REFINEMENT;
   private final String group;
   private final CookingBookCategory category;
   final Ingredient ingredient;
   final Ingredient bottleIngredient;
   final ItemStack result;
   final float experience;
   protected final int refinementTime;

   public PotatoRefinementRecipe(Ingredient var1, Ingredient var2, ItemStack var3, float var4, int var5) {
      this("", CookingBookCategory.MISC, var1, var2, var3, var4, var5);
   }

   private PotatoRefinementRecipe(String var1, CookingBookCategory var2, Ingredient var3, Ingredient var4, ItemStack var5, float var6, int var7) {
      super();
      this.group = var1;
      this.category = var2;
      this.ingredient = var3;
      this.bottleIngredient = var4;
      this.result = var5;
      this.experience = var6;
      this.refinementTime = var7;
   }

   @Override
   public boolean matches(Container var1, Level var2) {
      return this.ingredient.test(var1.getItem(0)) && this.bottleIngredient.test(var1.getItem(2));
   }

   @Override
   public ItemStack assemble(Container var1, HolderLookup.Provider var2) {
      return this.result.copy();
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return true;
   }

   @Override
   public ItemStack getResultItem(HolderLookup.Provider var1) {
      return this.result;
   }

   @Override
   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.POTATO_REFINERY);
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.POTATO_REFINEMENT_RECIPE;
   }

   @Override
   public RecipeType<?> getType() {
      return this.type;
   }

   public int getRefinementTime() {
      return this.refinementTime;
   }

   public static class Serializer implements RecipeSerializer<PotatoRefinementRecipe> {
      private static final Codec<PotatoRefinementRecipe> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Ingredient.CODEC.fieldOf("ingredient").forGetter(var0x -> var0x.ingredient),
                  Ingredient.CODEC.fieldOf("bottle_ingredient").forGetter(var0x -> var0x.bottleIngredient),
                  ItemStack.CODEC.fieldOf("result").forGetter(var0x -> var0x.result),
                  Codec.FLOAT.fieldOf("experience").forGetter(var0x -> var0x.experience),
                  Codec.INT.fieldOf("refinement_time").forGetter(var0x -> var0x.refinementTime)
               )
               .apply(var0, PotatoRefinementRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, PotatoRefinementRecipe> STREAM_CODEC = StreamCodec.of(
         PotatoRefinementRecipe.Serializer::toNetwork, PotatoRefinementRecipe.Serializer::fromNetwork
      );

      public Serializer() {
         super();
      }

      @Override
      public Codec<PotatoRefinementRecipe> codec() {
         return CODEC;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, PotatoRefinementRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static PotatoRefinementRecipe fromNetwork(RegistryFriendlyByteBuf var0) {
         Ingredient var1 = Ingredient.CONTENTS_STREAM_CODEC.decode(var0);
         Ingredient var2 = Ingredient.CONTENTS_STREAM_CODEC.decode(var0);
         ItemStack var3 = ItemStack.STREAM_CODEC.decode(var0);
         float var4 = var0.readFloat();
         int var5 = var0.readInt();
         return new PotatoRefinementRecipe(var1, var2, var3, var4, var5);
      }

      private static void toNetwork(RegistryFriendlyByteBuf var0, PotatoRefinementRecipe var1) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(var0, var1.ingredient);
         Ingredient.CONTENTS_STREAM_CODEC.encode(var0, var1.bottleIngredient);
         ItemStack.STREAM_CODEC.encode(var0, var1.result);
         var0.writeFloat(var1.experience);
         var0.writeInt(var1.refinementTime);
      }
   }
}
