package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.Level;

public class SmithingTrimRecipe implements SmithingRecipe {
   final Ingredient template;
   final Ingredient base;
   final Ingredient addition;

   public SmithingTrimRecipe(Ingredient var1, Ingredient var2, Ingredient var3) {
      super();
      this.template = var1;
      this.base = var2;
      this.addition = var3;
   }

   @Override
   public boolean matches(Container var1, Level var2) {
      return this.template.test(var1.getItem(0)) && this.base.test(var1.getItem(1)) && this.addition.test(var1.getItem(2));
   }

   @Override
   public ItemStack assemble(Container var1, HolderLookup.Provider var2) {
      ItemStack var3 = var1.getItem(1);
      if (this.base.test(var3)) {
         Optional var4 = TrimMaterials.getFromIngredient(var2, var1.getItem(2));
         Optional var5 = TrimPatterns.getFromTemplate(var2, var1.getItem(0));
         if (var4.isPresent() && var5.isPresent()) {
            ArmorTrim var6 = var3.get(DataComponents.TRIM);
            if (var6 != null && var6.hasPatternAndMaterial((Holder<TrimPattern>)var5.get(), (Holder<TrimMaterial>)var4.get())) {
               return ItemStack.EMPTY;
            }

            ItemStack var7 = var3.copyWithCount(1);
            var7.set(DataComponents.TRIM, new ArmorTrim((Holder<TrimMaterial>)var4.get(), (Holder<TrimPattern>)var5.get()));
            return var7;
         }
      }

      return ItemStack.EMPTY;
   }

   @Override
   public ItemStack getResultItem(HolderLookup.Provider var1) {
      ItemStack var2 = new ItemStack(Items.IRON_CHESTPLATE);
      Optional var3 = var1.lookupOrThrow(Registries.TRIM_PATTERN).listElements().findFirst();
      Optional var4 = var1.lookupOrThrow(Registries.TRIM_MATERIAL).get(TrimMaterials.REDSTONE);
      if (var3.isPresent() && var4.isPresent()) {
         var2.set(DataComponents.TRIM, new ArmorTrim((Holder<TrimMaterial>)var4.get(), (Holder<TrimPattern>)var3.get()));
      }

      return var2;
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
      return RecipeSerializer.SMITHING_TRIM;
   }

   @Override
   public boolean isIncomplete() {
      return Stream.of(this.template, this.base, this.addition).anyMatch(Ingredient::isEmpty);
   }

   public static class Serializer implements RecipeSerializer<SmithingTrimRecipe> {
      private static final Codec<SmithingTrimRecipe> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Ingredient.CODEC.fieldOf("template").forGetter(var0x -> var0x.template),
                  Ingredient.CODEC.fieldOf("base").forGetter(var0x -> var0x.base),
                  Ingredient.CODEC.fieldOf("addition").forGetter(var0x -> var0x.addition)
               )
               .apply(var0, SmithingTrimRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTrimRecipe> STREAM_CODEC = StreamCodec.of(
         SmithingTrimRecipe.Serializer::toNetwork, SmithingTrimRecipe.Serializer::fromNetwork
      );

      public Serializer() {
         super();
      }

      @Override
      public Codec<SmithingTrimRecipe> codec() {
         return CODEC;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, SmithingTrimRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static SmithingTrimRecipe fromNetwork(RegistryFriendlyByteBuf var0) {
         Ingredient var1 = Ingredient.CONTENTS_STREAM_CODEC.decode(var0);
         Ingredient var2 = Ingredient.CONTENTS_STREAM_CODEC.decode(var0);
         Ingredient var3 = Ingredient.CONTENTS_STREAM_CODEC.decode(var0);
         return new SmithingTrimRecipe(var1, var2, var3);
      }

      private static void toNetwork(RegistryFriendlyByteBuf var0, SmithingTrimRecipe var1) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(var0, var1.template);
         Ingredient.CONTENTS_STREAM_CODEC.encode(var0, var1.base);
         Ingredient.CONTENTS_STREAM_CODEC.encode(var0, var1.addition);
      }
   }
}
