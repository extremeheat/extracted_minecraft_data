package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.ItemLike;

public final class Ingredient implements Predicate<ItemStack>, StackedContents.IngredientInfo<Holder<Item>> {
   public static final StreamCodec<RegistryFriendlyByteBuf, Ingredient> CONTENTS_STREAM_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Ingredient>> OPTIONAL_CONTENTS_STREAM_CODEC;
   public static final Codec<HolderSet<Item>> NON_AIR_HOLDER_SET_CODEC;
   public static final Codec<Ingredient> CODEC;
   private final HolderSet<Item> values;

   private Ingredient(final HolderSet<Item> values) {
      super();
      values.unwrap().ifRight((directValues) -> {
         if (directValues.isEmpty()) {
            throw new UnsupportedOperationException("Ingredients can't be empty");
         } else if (directValues.contains(Items.AIR.builtInRegistryHolder())) {
            throw new UnsupportedOperationException("Ingredient can't contain air");
         }
      });
      this.values = values;
   }

   public static boolean testOptionalIngredient(final Optional<Ingredient> ingredient, final ItemStack stack) {
      Optional var10000 = ingredient.map((value) -> value.test(stack));
      Objects.requireNonNull(stack);
      return (Boolean)var10000.orElseGet(stack::isEmpty);
   }

   /** @deprecated */
   @Deprecated
   public Stream<Holder<Item>> items() {
      return this.values.stream();
   }

   public boolean isEmpty() {
      return this.values.size() == 0;
   }

   public boolean test(final ItemStack input) {
      return input.is(this.values);
   }

   public boolean acceptsItem(final Holder<Item> item) {
      return this.values.contains(item);
   }

   public boolean equals(final Object o) {
      if (o instanceof Ingredient other) {
         return Objects.equals(this.values, other.values);
      } else {
         return false;
      }
   }

   public static Ingredient of(final ItemLike itemLike) {
      return new Ingredient(HolderSet.direct(itemLike.asItem().builtInRegistryHolder()));
   }

   public static Ingredient of(final ItemLike... items) {
      return of(Arrays.stream(items));
   }

   public static Ingredient of(final Stream<? extends ItemLike> stream) {
      return new Ingredient(HolderSet.direct(stream.map((e) -> e.asItem().builtInRegistryHolder()).toList()));
   }

   public static Ingredient of(final HolderSet<Item> tag) {
      return new Ingredient(tag);
   }

   public SlotDisplay display() {
      return (SlotDisplay)this.values.unwrap().map(SlotDisplay.TagSlotDisplay::new, (l) -> new SlotDisplay.Composite(l.stream().map(Ingredient::displayForSingleItem).toList()));
   }

   public static SlotDisplay optionalIngredientToDisplay(final Optional<Ingredient> ingredient) {
      return (SlotDisplay)ingredient.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE);
   }

   private static SlotDisplay displayForSingleItem(final Holder<Item> item) {
      SlotDisplay inputDisplay = new SlotDisplay.ItemSlotDisplay(item);
      ItemStackTemplate remainderStack = ((Item)item.value()).getCraftingRemainder();
      if (remainderStack != null) {
         SlotDisplay remainderDisplay = new SlotDisplay.ItemStackSlotDisplay(remainderStack);
         return new SlotDisplay.WithRemainder(inputDisplay, remainderDisplay);
      } else {
         return inputDisplay;
      }
   }

   static {
      CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(Registries.ITEM).map(Ingredient::new, (i) -> i.values);
      OPTIONAL_CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(Registries.ITEM).map((ingredient) -> ingredient.size() == 0 ? Optional.empty() : Optional.of(new Ingredient(ingredient)), (ingredient) -> (HolderSet)ingredient.map((i) -> i.values).orElse(HolderSet.empty()));
      NON_AIR_HOLDER_SET_CODEC = HolderSetCodec.create(Registries.ITEM, Item.CODEC, false);
      CODEC = ExtraCodecs.nonEmptyHolderSet(NON_AIR_HOLDER_SET_CODEC).xmap(Ingredient::new, (i) -> i.values);
   }
}
