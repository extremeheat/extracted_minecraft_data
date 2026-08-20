package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.ItemLike;

public record ItemPredicate(Optional<HolderSet<Item>> items, MinMaxBounds.Ints count, DataComponentMatchers components) implements Predicate<ItemInstance> {
   public static final Codec<ItemPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.holderSet(Registries.ITEM).optionalFieldOf("items").forGetter(ItemPredicate::items), MinMaxBounds.Ints.CODEC.optionalFieldOf("count", MinMaxBounds.Ints.ANY).forGetter(ItemPredicate::count), DataComponentMatchers.CODEC.forGetter(ItemPredicate::components)).apply(i, ItemPredicate::new));

   public ItemPredicate {
      super();
   }

   public boolean test(final ItemInstance itemStack) {
      if (this.items.isPresent() && !itemStack.is((HolderSet)this.items.get())) {
         return false;
      } else if (!this.count.matches(itemStack.count())) {
         return false;
      } else {
         return this.components.test((DataComponentGetter)itemStack);
      }
   }

   public static class Builder {
      private Optional<HolderSet<Item>> items = Optional.empty();
      private MinMaxBounds.Ints count;
      private DataComponentMatchers components;

      public Builder() {
         super();
         this.count = MinMaxBounds.Ints.ANY;
         this.components = DataComponentMatchers.ANY;
      }

      public static Builder item() {
         return new Builder();
      }

      public Builder of(final HolderGetter<Item> lookup, final ItemLike... items) {
         this.items = Optional.of(HolderSet.direct((i) -> i.asItem().builtInRegistryHolder(), items));
         return this;
      }

      public Builder of(final HolderGetter<Item> lookup, final TagKey<Item> tag) {
         this.items = Optional.of(lookup.getOrThrow(tag));
         return this;
      }

      public Builder withCount(final MinMaxBounds.Ints count) {
         this.count = count;
         return this;
      }

      public Builder withComponents(final DataComponentMatchers components) {
         this.components = components;
         return this;
      }

      public ItemPredicate build() {
         return new ItemPredicate(this.items, this.count, this.components);
      }
   }
}
