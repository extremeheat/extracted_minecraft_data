package net.minecraft.world.item.crafting;

import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public record SelectableRecipe<T extends Recipe<?>>(SlotDisplay optionDisplay, Optional<RecipeHolder<T>> recipe) {
   public SelectableRecipe {
      super();
   }

   public static <T extends Recipe<?>> StreamCodec<RegistryFriendlyByteBuf, SelectableRecipe<T>> noRecipeCodec() {
      return StreamCodec.composite(SlotDisplay.STREAM_CODEC, SelectableRecipe::optionDisplay, (slotDisplay) -> new SelectableRecipe(slotDisplay, Optional.empty()));
   }

   public static record SingleInputEntry<T extends Recipe<?>>(Ingredient input, SelectableRecipe<T> recipe) {
      public SingleInputEntry {
         super();
      }

      public static <T extends Recipe<?>> StreamCodec<RegistryFriendlyByteBuf, SingleInputEntry<T>> noRecipeCodec() {
         return StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC, SingleInputEntry::input, SelectableRecipe.noRecipeCodec(), SingleInputEntry::recipe, SingleInputEntry::new);
      }
   }

   public static record SingleInputSet<T extends Recipe<?>>(List<SingleInputEntry<T>> entries) {
      public SingleInputSet {
         super();
      }

      public static <T extends Recipe<?>> SingleInputSet<T> empty() {
         return new SingleInputSet<T>(List.of());
      }

      public static <T extends Recipe<?>> StreamCodec<RegistryFriendlyByteBuf, SingleInputSet<T>> noRecipeCodec() {
         return StreamCodec.composite(SelectableRecipe.SingleInputEntry.noRecipeCodec().apply(ByteBufCodecs.list()), SingleInputSet::entries, SingleInputSet::new);
      }

      public boolean acceptsInput(final ItemStack input) {
         return this.entries.stream().anyMatch((e) -> e.input.test(input));
      }

      public SingleInputSet<T> selectByInput(final ItemStack input) {
         return new SingleInputSet<T>(this.entries.stream().filter((e) -> e.input.test(input)).toList());
      }

      public boolean isEmpty() {
         return this.entries.isEmpty();
      }

      public int size() {
         return this.entries.size();
      }
   }
}
