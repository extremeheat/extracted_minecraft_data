package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.item.equipment.trim.TrimPatterns;

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
      return applyTrim(var2, var1.base(), var1.addition(), var1.template());
   }

   public static ItemStack applyTrim(HolderLookup.Provider var0, ItemStack var1, ItemStack var2, ItemStack var3) {
      Optional var4 = TrimMaterials.getFromIngredient(var0, var2);
      Optional var5 = TrimPatterns.getFromTemplate(var0, var3);
      if (var4.isPresent() && var5.isPresent()) {
         ArmorTrim var6 = (ArmorTrim)var1.get(DataComponents.TRIM);
         if (var6 != null && var6.hasPatternAndMaterial((Holder)var5.get(), (Holder)var4.get())) {
            return ItemStack.EMPTY;
         } else {
            ItemStack var7 = var1.copyWithCount(1);
            var7.set(DataComponents.TRIM, new ArmorTrim((Holder)var4.get(), (Holder)var5.get()));
            return var7;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public Optional<Ingredient> templateIngredient() {
      return this.template;
   }

   public Optional<Ingredient> baseIngredient() {
      return this.base;
   }

   public Optional<Ingredient> additionIngredient() {
      return this.addition;
   }

   public RecipeSerializer<SmithingTrimRecipe> getSerializer() {
      return RecipeSerializer.SMITHING_TRIM;
   }

   public PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = PlacementInfo.createFromOptionals(List.of(this.template, this.base, this.addition));
      }

      return this.placementInfo;
   }

   public List<RecipeDisplay> display() {
      SlotDisplay var1 = Ingredient.optionalIngredientToDisplay(this.base);
      SlotDisplay var2 = Ingredient.optionalIngredientToDisplay(this.addition);
      SlotDisplay var3 = Ingredient.optionalIngredientToDisplay(this.template);
      return List.of(new SmithingRecipeDisplay(var3, var1, var2, new SlotDisplay.SmithingTrimDemoSlotDisplay(var1, var2, var3), new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)));
   }

   public static class Serializer implements RecipeSerializer<SmithingTrimRecipe> {
      private static final MapCodec<SmithingTrimRecipe> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Ingredient.CODEC.optionalFieldOf("template").forGetter((var0x) -> {
            return var0x.template;
         }), Ingredient.CODEC.optionalFieldOf("base").forGetter((var0x) -> {
            return var0x.base;
         }), Ingredient.CODEC.optionalFieldOf("addition").forGetter((var0x) -> {
            return var0x.addition;
         })).apply(var0, SmithingTrimRecipe::new);
      });
      public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTrimRecipe> STREAM_CODEC;

      public Serializer() {
         super();
      }

      public MapCodec<SmithingTrimRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, SmithingTrimRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, (var0) -> {
            return var0.template;
         }, Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, (var0) -> {
            return var0.base;
         }, Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, (var0) -> {
            return var0.addition;
         }, SmithingTrimRecipe::new);
      }
   }
}
