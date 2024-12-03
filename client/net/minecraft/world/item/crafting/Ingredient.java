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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.ItemLike;

public final class Ingredient implements StackedContents.IngredientInfo<Holder<Item>>, Predicate<ItemStack> {
   public static final StreamCodec<RegistryFriendlyByteBuf, Ingredient> CONTENTS_STREAM_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Ingredient>> OPTIONAL_CONTENTS_STREAM_CODEC;
   public static final Codec<HolderSet<Item>> NON_AIR_HOLDER_SET_CODEC;
   public static final Codec<Ingredient> CODEC;
   private final HolderSet<Item> values;

   private Ingredient(HolderSet<Item> var1) {
      super();
      var1.unwrap().ifRight((var0) -> {
         if (var0.isEmpty()) {
            throw new UnsupportedOperationException("Ingredients can't be empty");
         } else if (var0.contains(Items.AIR.builtInRegistryHolder())) {
            throw new UnsupportedOperationException("Ingredient can't contain air");
         }
      });
      this.values = var1;
   }

   public static boolean testOptionalIngredient(Optional<Ingredient> var0, ItemStack var1) {
      Optional var10000 = var0.map((var1x) -> var1x.test(var1));
      Objects.requireNonNull(var1);
      return (Boolean)var10000.orElseGet(var1::isEmpty);
   }

   /** @deprecated */
   @Deprecated
   public Stream<Holder<Item>> items() {
      return this.values.stream();
   }

   public boolean isEmpty() {
      return this.values.size() == 0;
   }

   public boolean test(ItemStack var1) {
      return var1.is(this.values);
   }

   public boolean acceptsItem(Holder<Item> var1) {
      return this.values.contains(var1);
   }

   public boolean equals(Object var1) {
      if (var1 instanceof Ingredient var2) {
         return Objects.equals(this.values, var2.values);
      } else {
         return false;
      }
   }

   public static Ingredient of(ItemLike var0) {
      return new Ingredient(HolderSet.direct(var0.asItem().builtInRegistryHolder()));
   }

   public static Ingredient of(ItemLike... var0) {
      return of(Arrays.stream(var0));
   }

   public static Ingredient of(Stream<? extends ItemLike> var0) {
      return new Ingredient(HolderSet.direct(var0.map((var0x) -> var0x.asItem().builtInRegistryHolder()).toList()));
   }

   public static Ingredient of(HolderSet<Item> var0) {
      return new Ingredient(var0);
   }

   public SlotDisplay display() {
      return (SlotDisplay)this.values.unwrap().map(SlotDisplay.TagSlotDisplay::new, (var0) -> new SlotDisplay.Composite(var0.stream().map(Ingredient::displayForSingleItem).toList()));
   }

   public static SlotDisplay optionalIngredientToDisplay(Optional<Ingredient> var0) {
      return (SlotDisplay)var0.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE);
   }

   private static SlotDisplay displayForSingleItem(Holder<Item> var0) {
      SlotDisplay.ItemSlotDisplay var1 = new SlotDisplay.ItemSlotDisplay(var0);
      ItemStack var2 = ((Item)var0.value()).getCraftingRemainder();
      if (!var2.isEmpty()) {
         SlotDisplay.ItemStackSlotDisplay var3 = new SlotDisplay.ItemStackSlotDisplay(var2);
         return new SlotDisplay.WithRemainder(var1, var3);
      } else {
         return var1;
      }
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((ItemStack)var1);
   }

   // $FF: synthetic method
   public boolean acceptsItem(final Object var1) {
      return this.acceptsItem((Holder)var1);
   }

   static {
      CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(Registries.ITEM).map(Ingredient::new, (var0) -> var0.values);
      OPTIONAL_CONTENTS_STREAM_CODEC = ByteBufCodecs.holderSet(Registries.ITEM).map((var0) -> var0.size() == 0 ? Optional.empty() : Optional.of(new Ingredient(var0)), (var0) -> (HolderSet)var0.map((var0x) -> var0x.values).orElse(HolderSet.direct()));
      NON_AIR_HOLDER_SET_CODEC = HolderSetCodec.create(Registries.ITEM, Item.CODEC, false);
      CODEC = ExtraCodecs.nonEmptyHolderSet(NON_AIR_HOLDER_SET_CODEC).xmap(Ingredient::new, (var0) -> var0.values);
   }
}
