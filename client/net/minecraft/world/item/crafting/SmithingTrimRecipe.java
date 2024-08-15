package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;

public class SmithingTrimRecipe implements SmithingRecipe {
   final Optional<Ingredient> template;
   final Optional<Ingredient> base;
   final Optional<Ingredient> addition;
   @Nullable
   private PlacementInfo placementInfo;

   public SmithingTrimRecipe(Optional<Ingredient> var1, Optional<Ingredient> var2, Optional<Ingredient> var3) {
      super();
      this.template = var1;
      this.base = var2;
      this.addition = var3;
   }

   public ItemStack assemble(SmithingRecipeInput var1, HolderLookup.Provider var2) {
      ItemStack var3 = var1.base();
      if (Ingredient.testOptionalIngredient(this.base, var3)) {
         Optional var4 = TrimMaterials.getFromIngredient(var2, var1.addition());
         Optional var5 = TrimPatterns.getFromTemplate(var2, var1.template());
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
      return RecipeSerializer.SMITHING_TRIM;
   }

   @Override
   public PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = PlacementInfo.createFromOptionals(List.of(this.template, this.base, this.addition));
      }

      return this.placementInfo;
   }

   public static class Serializer implements RecipeSerializer<SmithingTrimRecipe> {
      private static final MapCodec<SmithingTrimRecipe> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  Ingredient.CODEC.optionalFieldOf("template").forGetter(var0x -> var0x.template),
                  Ingredient.CODEC.optionalFieldOf("base").forGetter(var0x -> var0x.base),
                  Ingredient.CODEC.optionalFieldOf("addition").forGetter(var0x -> var0x.addition)
               )
               .apply(var0, SmithingTrimRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTrimRecipe> STREAM_CODEC = StreamCodec.composite(
         Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
         var0 -> var0.template,
         Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
         var0 -> var0.base,
         Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
         var0 -> var0.addition,
         SmithingTrimRecipe::new
      );

      public Serializer() {
         super();
      }

      @Override
      public MapCodec<SmithingTrimRecipe> codec() {
         return CODEC;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, SmithingTrimRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
