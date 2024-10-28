package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SmithingTransformRecipe implements SmithingRecipe {
   final Ingredient template;
   final Ingredient base;
   final Ingredient addition;
   final ItemStack result;

   public SmithingTransformRecipe(Ingredient var1, Ingredient var2, Ingredient var3, ItemStack var4) {
      super();
      this.template = var1;
      this.base = var2;
      this.addition = var3;
      this.result = var4;
   }

   public boolean matches(Container var1, Level var2) {
      return this.template.test(var1.getItem(0)) && this.base.test(var1.getItem(1)) && this.addition.test(var1.getItem(2));
   }

   public ItemStack assemble(Container var1, HolderLookup.Provider var2) {
      ItemStack var3 = var1.getItem(1).transmuteCopy(this.result.getItem(), this.result.getCount());
      var3.applyComponents(this.result.getComponentsPatch());
      return var3;
   }

   public ItemStack getResultItem(HolderLookup.Provider var1) {
      return this.result;
   }

   public boolean isTemplateIngredient(ItemStack var1) {
      return this.template.test(var1);
   }

   public boolean isBaseIngredient(ItemStack var1) {
      return this.base.test(var1);
   }

   public boolean isAdditionIngredient(ItemStack var1) {
      return this.addition.test(var1);
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SMITHING_TRANSFORM;
   }

   public boolean isIncomplete() {
      return Stream.of(this.template, this.base, this.addition).anyMatch(Ingredient::isEmpty);
   }

   public static class Serializer implements RecipeSerializer<SmithingTransformRecipe> {
      private static final MapCodec<SmithingTransformRecipe> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Ingredient.CODEC.fieldOf("template").forGetter((var0x) -> {
            return var0x.template;
         }), Ingredient.CODEC.fieldOf("base").forGetter((var0x) -> {
            return var0x.base;
         }), Ingredient.CODEC.fieldOf("addition").forGetter((var0x) -> {
            return var0x.addition;
         }), ItemStack.CODEC.fieldOf("result").forGetter((var0x) -> {
            return var0x.result;
         })).apply(var0, SmithingTransformRecipe::new);
      });
      public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

      public Serializer() {
         super();
      }

      public MapCodec<SmithingTransformRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static SmithingTransformRecipe fromNetwork(RegistryFriendlyByteBuf var0) {
         Ingredient var1 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(var0);
         Ingredient var2 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(var0);
         Ingredient var3 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(var0);
         ItemStack var4 = (ItemStack)ItemStack.STREAM_CODEC.decode(var0);
         return new SmithingTransformRecipe(var1, var2, var3, var4);
      }

      private static void toNetwork(RegistryFriendlyByteBuf var0, SmithingTransformRecipe var1) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(var0, var1.template);
         Ingredient.CONTENTS_STREAM_CODEC.encode(var0, var1.base);
         Ingredient.CONTENTS_STREAM_CODEC.encode(var0, var1.addition);
         ItemStack.STREAM_CODEC.encode(var0, var1.result);
      }
   }
}
