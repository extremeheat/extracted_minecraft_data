package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class SmithingTransformRecipe implements SmithingRecipe {
   final Optional<Ingredient> template;
   final Optional<Ingredient> base;
   final Optional<Ingredient> addition;
   final ItemStack result;
   @Nullable
   private PlacementInfo placementInfo;

   public SmithingTransformRecipe(Optional<Ingredient> var1, Optional<Ingredient> var2, Optional<Ingredient> var3, ItemStack var4) {
      super();
      this.template = var1;
      this.base = var2;
      this.addition = var3;
      this.result = var4;
   }

   public ItemStack assemble(SmithingRecipeInput var1, HolderLookup.Provider var2) {
      ItemStack var3 = var1.base().transmuteCopy(this.result.getItem(), this.result.getCount());
      var3.applyComponents(this.result.getComponentsPatch());
      return var3;
   }

   @Override
   public ItemStack getResultItem(HolderLookup.Provider var1) {
      return this.result;
   }

   @Override
   public boolean isTemplateIngredient(ItemStack var1) {
      return Ingredient.testOptionalIngredient(this.template, var1);
   }

   @Override
   public boolean isBaseIngredient(ItemStack var1) {
      return Ingredient.testOptionalIngredient(this.base, var1);
   }

   @Override
   public boolean isAdditionIngredient(ItemStack var1) {
      return Ingredient.testOptionalIngredient(this.addition, var1);
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SMITHING_TRANSFORM;
   }

   @Override
   public PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = PlacementInfo.createFromOptionals(List.of(this.template, this.base, this.addition));
      }

      return this.placementInfo;
   }

   public static class Serializer implements RecipeSerializer<SmithingTransformRecipe> {
      private static final MapCodec<SmithingTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  Ingredient.CODEC.optionalFieldOf("template").forGetter(var0x -> var0x.template),
                  Ingredient.CODEC.optionalFieldOf("base").forGetter(var0x -> var0x.base),
                  Ingredient.CODEC.optionalFieldOf("addition").forGetter(var0x -> var0x.addition),
                  ItemStack.STRICT_CODEC.fieldOf("result").forGetter(var0x -> var0x.result)
               )
               .apply(var0, SmithingTransformRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> STREAM_CODEC = StreamCodec.composite(
         Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
         var0 -> var0.template,
         Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
         var0 -> var0.base,
         Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
         var0 -> var0.addition,
         ItemStack.STREAM_CODEC,
         var0 -> var0.result,
         SmithingTransformRecipe::new
      );

      public Serializer() {
         super();
      }

      @Override
      public MapCodec<SmithingTransformRecipe> codec() {
         return CODEC;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
