package net.minecraft.world.item.crafting;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public record SelectableRecipe<T extends Recipe<?>>(SlotDisplay optionDisplay, Optional<RecipeHolder<T>> recipe) {
   public SelectableRecipe(SlotDisplay var1, Optional<RecipeHolder<T>> var2) {
      super();
      this.optionDisplay = var1;
      this.recipe = var2;
   }

   public static <T extends Recipe<?>> StreamCodec<RegistryFriendlyByteBuf, SelectableRecipe<T>> noRecipeCodec() {
      return StreamCodec.composite(SlotDisplay.STREAM_CODEC, SelectableRecipe::optionDisplay, (var0) -> {
         return new SelectableRecipe(var0, Optional.empty());
      });
   }

   public SlotDisplay optionDisplay() {
      return this.optionDisplay;
   }

   public Optional<RecipeHolder<T>> recipe() {
      return this.recipe;
   }

   public static record SingleInputSet<T extends Recipe<?>>(List<SingleInputEntry<T>> entries) {
      public SingleInputSet(List<SingleInputEntry<T>> var1) {
         super();
         this.entries = var1;
      }

      public static <T extends Recipe<?>> SingleInputSet<T> empty() {
         return new SingleInputSet(List.of());
      }

      public static <T extends Recipe<?>> StreamCodec<RegistryFriendlyByteBuf, SingleInputSet<T>> noRecipeCodec() {
         return StreamCodec.composite(SelectableRecipe.SingleInputEntry.noRecipeCodec().apply(ByteBufCodecs.list()), SingleInputSet::entries, SingleInputSet::new);
      }

      public boolean acceptsInput(ItemStack var1) {
         return this.entries.stream().anyMatch((var1x) -> {
            return var1x.input.test(var1);
         });
      }

      public SingleInputSet<T> selectByInput(ItemStack var1) {
         return new SingleInputSet(this.entries.stream().filter((var1x) -> {
            return var1x.input.test(var1);
         }).toList());
      }

      public boolean isEmpty() {
         return this.entries.isEmpty();
      }

      public int size() {
         return this.entries.size();
      }

      public List<SingleInputEntry<T>> entries() {
         return this.entries;
      }
   }

   public static record SingleInputEntry<T extends Recipe<?>>(Ingredient input, SelectableRecipe<T> recipe) {
      final Ingredient input;

      public SingleInputEntry(Ingredient var1, SelectableRecipe<T> var2) {
         super();
         this.input = var1;
         this.recipe = var2;
      }

      public static <T extends Recipe<?>> StreamCodec<RegistryFriendlyByteBuf, SingleInputEntry<T>> noRecipeCodec() {
         return StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC, SingleInputEntry::input, SelectableRecipe.noRecipeCodec(), SingleInputEntry::recipe, SingleInputEntry::new);
      }

      public Ingredient input() {
         return this.input;
      }

      public SelectableRecipe<T> recipe() {
         return this.recipe;
      }
   }
}
