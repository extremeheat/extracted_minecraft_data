package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record ItemPredicate(Optional<HolderSet<Item>> items, MinMaxBounds.Ints count, DataComponentPredicate components, Map<ItemSubPredicate.Type<?>, ItemSubPredicate> subPredicates) implements Predicate<ItemStack> {
   public static final Codec<ItemPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("items").forGetter(ItemPredicate::items), MinMaxBounds.Ints.CODEC.optionalFieldOf("count", MinMaxBounds.Ints.ANY).forGetter(ItemPredicate::count), DataComponentPredicate.CODEC.optionalFieldOf("components", DataComponentPredicate.EMPTY).forGetter(ItemPredicate::components), ItemSubPredicate.CODEC.optionalFieldOf("predicates", Map.of()).forGetter(ItemPredicate::subPredicates)).apply(var0, ItemPredicate::new);
   });

   public ItemPredicate(Optional<HolderSet<Item>> items, MinMaxBounds.Ints count, DataComponentPredicate components, Map<ItemSubPredicate.Type<?>, ItemSubPredicate> subPredicates) {
      super();
      this.items = items;
      this.count = count;
      this.components = components;
      this.subPredicates = subPredicates;
   }

   public boolean test(ItemStack var1) {
      if (this.items.isPresent() && !var1.is((HolderSet)this.items.get())) {
         return false;
      } else if (!this.count.matches(var1.getCount())) {
         return false;
      } else if (!this.components.test((DataComponentHolder)var1)) {
         return false;
      } else {
         Iterator var2 = this.subPredicates.values().iterator();

         ItemSubPredicate var3;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (ItemSubPredicate)var2.next();
         } while(var3.matches(var1));

         return false;
      }
   }

   public Optional<HolderSet<Item>> items() {
      return this.items;
   }

   public MinMaxBounds.Ints count() {
      return this.count;
   }

   public DataComponentPredicate components() {
      return this.components;
   }

   public Map<ItemSubPredicate.Type<?>, ItemSubPredicate> subPredicates() {
      return this.subPredicates;
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((ItemStack)var1);
   }

   public static class Builder {
      private Optional<HolderSet<Item>> items = Optional.empty();
      private MinMaxBounds.Ints count;
      private DataComponentPredicate components;
      private final ImmutableMap.Builder<ItemSubPredicate.Type<?>, ItemSubPredicate> subPredicates;

      private Builder() {
         super();
         this.count = MinMaxBounds.Ints.ANY;
         this.components = DataComponentPredicate.EMPTY;
         this.subPredicates = ImmutableMap.builder();
      }

      public static Builder item() {
         return new Builder();
      }

      public Builder of(ItemLike... var1) {
         this.items = Optional.of(HolderSet.direct((var0) -> {
            return var0.asItem().builtInRegistryHolder();
         }, (Object[])var1));
         return this;
      }

      public Builder of(TagKey<Item> var1) {
         this.items = Optional.of(BuiltInRegistries.ITEM.getOrCreateTag(var1));
         return this;
      }

      public Builder withCount(MinMaxBounds.Ints var1) {
         this.count = var1;
         return this;
      }

      public <T extends ItemSubPredicate> Builder withSubPredicate(ItemSubPredicate.Type<T> var1, T var2) {
         this.subPredicates.put(var1, var2);
         return this;
      }

      public Builder hasComponents(DataComponentPredicate var1) {
         this.components = var1;
         return this;
      }

      public ItemPredicate build() {
         return new ItemPredicate(this.items, this.count, this.components, this.subPredicates.build());
      }
   }
}
