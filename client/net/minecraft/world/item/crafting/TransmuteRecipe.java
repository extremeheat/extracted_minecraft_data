package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public class TransmuteRecipe implements CraftingRecipe {
   final String group;
   final CraftingBookCategory category;
   final Ingredient input;
   final Ingredient material;
   final Holder<Item> result;
   @Nullable
   private PlacementInfo placementInfo;

   public TransmuteRecipe(String var1, CraftingBookCategory var2, Ingredient var3, Ingredient var4, Holder<Item> var5) {
      super();
      this.group = var1;
      this.category = var2;
      this.input = var3;
      this.material = var4;
      this.result = var5;
   }

   public boolean matches(CraftingInput var1, Level var2) {
      boolean var3 = false;
      boolean var4 = false;

      for (int var5 = 0; var5 < var1.size(); var5++) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (!var3 && this.input.test(var6) && var6.getItem() != this.result.value()) {
               var3 = true;
            } else {
               if (var4 || !this.material.test(var6)) {
                  return false;
               }

               var4 = true;
            }
         }
      }

      return var3 && var4;
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      ItemStack var3 = ItemStack.EMPTY;

      for (int var4 = 0; var4 < var1.size(); var4++) {
         ItemStack var5 = var1.getItem(var4);
         if (!var5.isEmpty() && this.input.test(var5) && var5.getItem() != this.result.value()) {
            var3 = var5;
         }
      }

      return var3.transmuteCopy(this.result.value(), 1);
   }

   @Override
   public List<RecipeDisplay> display() {
      return List.of(
         new ShapelessCraftingRecipeDisplay(
            List.of(this.input.display(), this.material.display()),
            new SlotDisplay.ItemSlotDisplay(this.result),
            new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
         )
      );
   }

   @Override
   public RecipeSerializer<TransmuteRecipe> getSerializer() {
      return RecipeSerializer.TRANSMUTE;
   }

   @Override
   public String group() {
      return this.group;
   }

   @Override
   public PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = PlacementInfo.create(List.of(this.input, this.material));
      }

      return this.placementInfo;
   }

   @Override
   public CraftingBookCategory category() {
      return this.category;
   }

   public static class Serializer implements RecipeSerializer<TransmuteRecipe> {
      private static final MapCodec<TransmuteRecipe> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  Codec.STRING.optionalFieldOf("group", "").forGetter(var0x -> var0x.group),
                  CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(var0x -> var0x.category),
                  Ingredient.CODEC.fieldOf("input").forGetter(var0x -> var0x.input),
                  Ingredient.CODEC.fieldOf("material").forGetter(var0x -> var0x.material),
                  RegistryFixedCodec.create(Registries.ITEM).fieldOf("result").forGetter(var0x -> var0x.result)
               )
               .apply(var0, TransmuteRecipe::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, TransmuteRecipe> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.STRING_UTF8,
         var0 -> var0.group,
         CraftingBookCategory.STREAM_CODEC,
         var0 -> var0.category,
         Ingredient.CONTENTS_STREAM_CODEC,
         var0 -> var0.input,
         Ingredient.CONTENTS_STREAM_CODEC,
         var0 -> var0.material,
         ByteBufCodecs.holderRegistry(Registries.ITEM),
         var0 -> var0.result,
         TransmuteRecipe::new
      );

      public Serializer() {
         super();
      }

      @Override
      public MapCodec<TransmuteRecipe> codec() {
         return CODEC;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, TransmuteRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
