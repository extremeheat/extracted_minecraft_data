package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record ItemPredicate(Optional<HolderSet<Item>> items, MinMaxBounds.Ints count, DataComponentMatchers components) implements Predicate<ItemStack> {
   public static final Codec<ItemPredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("items").forGetter(ItemPredicate::items), MinMaxBounds.Ints.CODEC.optionalFieldOf("count", MinMaxBounds.Ints.ANY).forGetter(ItemPredicate::count), DataComponentMatchers.CODEC.forGetter(ItemPredicate::components)).apply(var0, ItemPredicate::new));

   public ItemPredicate(Optional<HolderSet<Item>> var1, MinMaxBounds.Ints var2, DataComponentMatchers var3) {
      super();
      this.items = var1;
      this.count = var2;
      this.components = var3;
   }

   public boolean test(ItemStack var1) {
      if (this.items.isPresent() && !var1.is((HolderSet)this.items.get())) {
         return false;
      } else if (!this.count.matches(var1.getCount())) {
         return false;
      } else {
         return this.components.test((DataComponentGetter)var1);
      }
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((ItemStack)var1);
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

      public Builder of(HolderGetter<Item> var1, ItemLike... var2) {
         this.items = Optional.of(HolderSet.direct((var0) -> var0.asItem().builtInRegistryHolder(), var2));
         return this;
      }

      public Builder of(HolderGetter<Item> var1, TagKey<Item> var2) {
         this.items = Optional.of(var1.getOrThrow(var2));
         return this;
      }

      public Builder withCount(MinMaxBounds.Ints var1) {
         this.count = var1;
         return this;
      }

      public Builder withComponents(DataComponentMatchers var1) {
         this.components = var1;
         return this;
      }

      public ItemPredicate build() {
         return new ItemPredicate(this.items, this.count, this.components);
      }
   }
}
