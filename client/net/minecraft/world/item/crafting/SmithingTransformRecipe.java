package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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

   @Override
   public boolean matches(Container var1, Level var2) {
      return this.template.test(var1.getItem(0)) && this.base.test(var1.getItem(1)) && this.addition.test(var1.getItem(2));
   }

   @Override
   public ItemStack assemble(Container var1, RegistryAccess var2) {
      ItemStack var3 = this.result.copy();
      CompoundTag var4 = var1.getItem(1).getTag();
      if (var4 != null) {
         var3.setTag(var4.copy());
      }

      return var3;
   }

   @Override
   public ItemStack getResultItem(RegistryAccess var1) {
      return this.result;
   }

   @Override
   public boolean isTemplateIngredient(ItemStack var1) {
      return this.template.test(var1);
   }

   @Override
   public boolean isBaseIngredient(ItemStack var1) {
      return this.base.test(var1);
   }

   @Override
   public boolean isAdditionIngredient(ItemStack var1) {
      return this.addition.test(var1);
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SMITHING_TRANSFORM;
   }

   @Override
   public boolean isIncomplete() {
      return Stream.of(this.template, this.base, this.addition).anyMatch(Ingredient::isEmpty);
   }

   public static class Serializer implements RecipeSerializer<SmithingTransformRecipe> {
      private static final Codec<SmithingTransformRecipe> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Ingredient.CODEC.fieldOf("template").forGetter(var0x -> var0x.template),
                  Ingredient.CODEC.fieldOf("base").forGetter(var0x -> var0x.base),
                  Ingredient.CODEC.fieldOf("addition").forGetter(var0x -> var0x.addition),
                  ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(var0x -> var0x.result)
               )
               .apply(var0, SmithingTransformRecipe::new)
      );

      public Serializer() {
         super();
      }

      @Override
      public Codec<SmithingTransformRecipe> codec() {
         return CODEC;
      }

      public SmithingTransformRecipe fromNetwork(FriendlyByteBuf var1) {
         Ingredient var2 = Ingredient.fromNetwork(var1);
         Ingredient var3 = Ingredient.fromNetwork(var1);
         Ingredient var4 = Ingredient.fromNetwork(var1);
         ItemStack var5 = var1.readItem();
         return new SmithingTransformRecipe(var2, var3, var4, var5);
      }

      public void toNetwork(FriendlyByteBuf var1, SmithingTransformRecipe var2) {
         var2.template.toNetwork(var1);
         var2.base.toNetwork(var1);
         var2.addition.toNetwork(var1);
         var1.writeItem(var2.result);
      }
   }
}
