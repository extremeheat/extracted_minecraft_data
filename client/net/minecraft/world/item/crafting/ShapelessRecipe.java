package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public class ShapelessRecipe implements CraftingRecipe {
   final String group;
   final CraftingBookCategory category;
   final ItemStack result;
   final List<Ingredient> ingredients;
   @Nullable
   private PlacementInfo placementInfo;

   public ShapelessRecipe(String var1, CraftingBookCategory var2, ItemStack var3, List<Ingredient> var4) {
      super();
      this.group = var1;
      this.category = var2;
      this.result = var3;
      this.ingredients = var4;
   }

   public RecipeSerializer<ShapelessRecipe> getSerializer() {
      return RecipeSerializer.SHAPELESS_RECIPE;
   }

   public String group() {
      return this.group;
   }

   public CraftingBookCategory category() {
      return this.category;
   }

   public PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = PlacementInfo.create(this.ingredients);
      }

      return this.placementInfo;
   }

   public boolean matches(CraftingInput var1, Level var2) {
      if (var1.ingredientCount() != this.ingredients.size()) {
         return false;
      } else {
         return var1.size() == 1 && this.ingredients.size() == 1 ? ((Ingredient)this.ingredients.getFirst()).test(var1.getItem(0)) : var1.stackedContents().canCraft((Recipe)this, (StackedContents.Output)null);
      }
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      return this.result.copy();
   }

   public List<RecipeDisplay> display() {
      return List.of(new ShapelessCraftingRecipeDisplay(this.ingredients.stream().map(Ingredient::display).toList(), new SlotDisplay.ItemStackSlotDisplay(this.result), new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
   }

   public static class Serializer implements RecipeSerializer<ShapelessRecipe> {
      private static final MapCodec<ShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Codec.STRING.optionalFieldOf("group", "").forGetter((var0x) -> {
            return var0x.group;
         }), CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter((var0x) -> {
            return var0x.category;
         }), ItemStack.STRICT_CODEC.fieldOf("result").forGetter((var0x) -> {
            return var0x.result;
         }), Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter((var0x) -> {
            return var0x.ingredients;
         })).apply(var0, ShapelessRecipe::new);
      });
      public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> STREAM_CODEC;

      public Serializer() {
         super();
      }

      public MapCodec<ShapelessRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, (var0) -> {
            return var0.group;
         }, CraftingBookCategory.STREAM_CODEC, (var0) -> {
            return var0.category;
         }, ItemStack.STREAM_CODEC, (var0) -> {
            return var0.result;
         }, Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), (var0) -> {
            return var0.ingredients;
         }, ShapelessRecipe::new);
      }
   }
}
