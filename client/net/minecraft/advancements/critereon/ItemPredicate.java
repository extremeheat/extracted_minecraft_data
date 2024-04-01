package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record ItemPredicate(Optional<HolderSet<Item>> b, MinMaxBounds.Ints c, DataComponentPredicate d, Map<ItemSubPredicate.Type<?>, ItemSubPredicate> e) {
   private final Optional<HolderSet<Item>> items;
   private final MinMaxBounds.Ints count;
   private final DataComponentPredicate components;
   private final Map<ItemSubPredicate.Type<?>, ItemSubPredicate> subPredicates;
   public static final Codec<ItemPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.strictOptionalField(RegistryCodecs.homogeneousList(Registries.ITEM), "items").forGetter(ItemPredicate::items),
               ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "count", MinMaxBounds.Ints.ANY).forGetter(ItemPredicate::count),
               ExtraCodecs.strictOptionalField(DataComponentPredicate.CODEC, "components", DataComponentPredicate.EMPTY).forGetter(ItemPredicate::components),
               ExtraCodecs.strictOptionalField(ItemSubPredicate.CODEC, "predicates", Map.of()).forGetter(ItemPredicate::subPredicates)
            )
            .apply(var0, ItemPredicate::new)
   );

   public ItemPredicate(
      Optional<HolderSet<Item>> var1, MinMaxBounds.Ints var2, DataComponentPredicate var3, Map<ItemSubPredicate.Type<?>, ItemSubPredicate> var4
   ) {
      super();
      this.items = var1;
      this.count = var2;
      this.components = var3;
      this.subPredicates = var4;
   }

   public boolean matches(ItemStack var1) {
      if (this.items.isPresent() && !var1.is(this.items.get())) {
         return false;
      } else if (!this.count.matches(var1.getCount())) {
         return false;
      } else if (!this.components.test((DataComponentHolder)var1)) {
         return false;
      } else {
         for(ItemSubPredicate var3 : this.subPredicates.values()) {
            if (!var3.matches(var1)) {
               return false;
            }
         }

         return true;
      }
   }

   public static class Builder {
      private Optional<HolderSet<Item>> items = Optional.empty();
      private MinMaxBounds.Ints count = MinMaxBounds.Ints.ANY;
      private DataComponentPredicate components = DataComponentPredicate.EMPTY;
      private final com.google.common.collect.ImmutableMap.Builder<ItemSubPredicate.Type<?>, ItemSubPredicate> subPredicates = ImmutableMap.builder();

      private Builder() {
         super();
      }

      public static ItemPredicate.Builder item() {
         return new ItemPredicate.Builder();
      }

      public ItemPredicate.Builder of(ItemLike... var1) {
         this.items = Optional.of(HolderSet.direct(var0 -> var0.asItem().builtInRegistryHolder(), var1));
         return this;
      }

      public ItemPredicate.Builder of(TagKey<Item> var1) {
         this.items = Optional.of(BuiltInRegistries.ITEM.getOrCreateTag(var1));
         return this;
      }

      public ItemPredicate.Builder withCount(MinMaxBounds.Ints var1) {
         this.count = var1;
         return this;
      }

      public <T extends ItemSubPredicate> ItemPredicate.Builder withSubPredicate(ItemSubPredicate.Type<T> var1, T var2) {
         this.subPredicates.put(var1, var2);
         return this;
      }

      public ItemPredicate.Builder hasComponents(DataComponentPredicate var1) {
         this.components = var1;
         return this;
      }

      public ItemPredicate build() {
         return new ItemPredicate(this.items, this.count, this.components, this.subPredicates.build());
      }
   }
}
