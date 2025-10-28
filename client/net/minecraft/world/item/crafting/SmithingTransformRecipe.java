package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;
import org.jspecify.annotations.Nullable;

public class SmithingTransformRecipe implements SmithingRecipe {
   final Optional<Ingredient> template;
   final Ingredient base;
   final Optional<Ingredient> addition;
   final TransmuteResult result;
   private @Nullable PlacementInfo placementInfo;

   public SmithingTransformRecipe(Optional<Ingredient> var1, Ingredient var2, Optional<Ingredient> var3, TransmuteResult var4) {
      super();
      this.template = var1;
      this.base = var2;
      this.addition = var3;
      this.result = var4;
   }

   public ItemStack assemble(SmithingRecipeInput var1, HolderLookup.Provider var2) {
      return this.result.apply(var1.base());
   }

   public Optional<Ingredient> templateIngredient() {
      return this.template;
   }

   public Ingredient baseIngredient() {
      return this.base;
   }

   public Optional<Ingredient> additionIngredient() {
      return this.addition;
   }

   public RecipeSerializer<SmithingTransformRecipe> getSerializer() {
      return RecipeSerializer.SMITHING_TRANSFORM;
   }

   public PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = PlacementInfo.createFromOptionals(List.of(this.template, Optional.of(this.base), this.addition));
      }

      return this.placementInfo;
   }

   public List<RecipeDisplay> display() {
      return List.of(new SmithingRecipeDisplay(Ingredient.optionalIngredientToDisplay(this.template), this.base.display(), Ingredient.optionalIngredientToDisplay(this.addition), this.result.display(), new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)));
   }

   public static class Serializer implements RecipeSerializer<SmithingTransformRecipe> {
      private static final MapCodec<SmithingTransformRecipe> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Ingredient.CODEC.optionalFieldOf("template").forGetter((var0x) -> var0x.template), Ingredient.CODEC.fieldOf("base").forGetter((var0x) -> var0x.base), Ingredient.CODEC.optionalFieldOf("addition").forGetter((var0x) -> var0x.addition), TransmuteResult.CODEC.fieldOf("result").forGetter((var0x) -> var0x.result)).apply(var0, SmithingTransformRecipe::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> STREAM_CODEC;

      public Serializer() {
         super();
      }

      public MapCodec<SmithingTransformRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, (var0) -> var0.template, Ingredient.CONTENTS_STREAM_CODEC, (var0) -> var0.base, Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, (var0) -> var0.addition, TransmuteResult.STREAM_CODEC, (var0) -> var0.result, SmithingTransformRecipe::new);
      }
   }
}
