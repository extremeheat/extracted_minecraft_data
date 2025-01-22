package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
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
import net.minecraft.world.item.equipment.trim.TrimPattern;

public class SmithingTrimRecipe implements SmithingRecipe {
   final Ingredient template;
   final Ingredient base;
   final Ingredient addition;
   final Holder<TrimPattern> pattern;
   @Nullable
   private PlacementInfo placementInfo;

   public SmithingTrimRecipe(Ingredient var1, Ingredient var2, Ingredient var3, Holder<TrimPattern> var4) {
      super();
      this.template = var1;
      this.base = var2;
      this.addition = var3;
      this.pattern = var4;
   }

   public ItemStack assemble(SmithingRecipeInput var1, HolderLookup.Provider var2) {
      return applyTrim(var2, var1.base(), var1.addition(), this.pattern);
   }

   public static ItemStack applyTrim(HolderLookup.Provider var0, ItemStack var1, ItemStack var2, Holder<TrimPattern> var3) {
      Optional var4 = TrimMaterials.getFromIngredient(var0, var2);
      if (var4.isPresent()) {
         ArmorTrim var5 = (ArmorTrim)var1.get(DataComponents.TRIM);
         ArmorTrim var6 = new ArmorTrim((Holder)var4.get(), var3);
         if (Objects.equals(var5, var6)) {
            return ItemStack.EMPTY;
         } else {
            ItemStack var7 = var1.copyWithCount(1);
            var7.set(DataComponents.TRIM, var6);
            return var7;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public Optional<Ingredient> templateIngredient() {
      return Optional.of(this.template);
   }

   public Ingredient baseIngredient() {
      return this.base;
   }

   public Optional<Ingredient> additionIngredient() {
      return Optional.of(this.addition);
   }

   public RecipeSerializer<SmithingTrimRecipe> getSerializer() {
      return RecipeSerializer.SMITHING_TRIM;
   }

   public PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = PlacementInfo.create(List.of(this.template, this.base, this.addition));
      }

      return this.placementInfo;
   }

   public List<RecipeDisplay> display() {
      SlotDisplay var1 = this.base.display();
      SlotDisplay var2 = this.addition.display();
      SlotDisplay var3 = this.template.display();
      return List.of(new SmithingRecipeDisplay(var3, var1, var2, new SlotDisplay.SmithingTrimDemoSlotDisplay(var1, var2, this.pattern), new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)));
   }

   public static class Serializer implements RecipeSerializer<SmithingTrimRecipe> {
      private static final MapCodec<SmithingTrimRecipe> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Ingredient.CODEC.fieldOf("template").forGetter((var0x) -> var0x.template), Ingredient.CODEC.fieldOf("base").forGetter((var0x) -> var0x.base), Ingredient.CODEC.fieldOf("addition").forGetter((var0x) -> var0x.addition), TrimPattern.CODEC.fieldOf("pattern").forGetter((var0x) -> var0x.pattern)).apply(var0, SmithingTrimRecipe::new));
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
         STREAM_CODEC = StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC, (var0) -> var0.template, Ingredient.CONTENTS_STREAM_CODEC, (var0) -> var0.base, Ingredient.CONTENTS_STREAM_CODEC, (var0) -> var0.addition, TrimPattern.STREAM_CODEC, (var0) -> var0.pattern, SmithingTrimRecipe::new);
      }
   }
}
