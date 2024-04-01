package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class Ingredient implements Predicate<ItemStack> {
   public static final Ingredient EMPTY = new Ingredient(Stream.empty());
   public static final StreamCodec<RegistryFriendlyByteBuf, Ingredient> CONTENTS_STREAM_CODEC = ItemStack.LIST_STREAM_CODEC
      .map(var0 -> fromValues(var0.stream().map(Ingredient.ItemValue::new)), var0 -> Arrays.asList(var0.getItems()));
   private final Ingredient.Value[] values;
   @Nullable
   private ItemStack[] itemStacks;
   @Nullable
   private IntList stackingIds;
   public static final Codec<Ingredient> CODEC = codec(true);
   public static final Codec<Ingredient> CODEC_NONEMPTY = codec(false);

   private Ingredient(Stream<? extends Ingredient.Value> var1) {
      super();
      this.values = var1.toArray(var0 -> new Ingredient.Value[var0]);
   }

   private Ingredient(Ingredient.Value[] var1) {
      super();
      this.values = var1;
   }

   public ItemStack[] getItems() {
      if (this.itemStacks == null) {
         this.itemStacks = Arrays.stream(this.values).flatMap(var0 -> var0.getItems().stream()).distinct().toArray(var0 -> new ItemStack[var0]);
      }

      return this.itemStacks;
   }

   public boolean test(@Nullable ItemStack var1) {
      if (var1 == null) {
         return false;
      } else if (this.isEmpty()) {
         return var1.isEmpty();
      } else {
         for(ItemStack var5 : this.getItems()) {
            if (var5.is(var1.getItem())) {
               return true;
            }
         }

         return false;
      }
   }

   public IntList getStackingIds() {
      if (this.stackingIds == null) {
         ItemStack[] var1 = this.getItems();
         this.stackingIds = new IntArrayList(var1.length);

         for(ItemStack var5 : var1) {
            this.stackingIds.add(StackedContents.getStackingIndex(var5));
         }

         this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
      }

      return this.stackingIds;
   }

   public boolean isEmpty() {
      return this.values.length == 0;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof Ingredient var2 ? Arrays.equals((Object[])this.values, (Object[])var2.values) : false;
   }

   private static Ingredient fromValues(Stream<? extends Ingredient.Value> var0) {
      Ingredient var1 = new Ingredient(var0);
      return var1.isEmpty() ? EMPTY : var1;
   }

   public static Ingredient of() {
      return EMPTY;
   }

   public static Ingredient of(ItemLike... var0) {
      return of(Arrays.stream(var0).map(ItemStack::new));
   }

   public static Ingredient of(ItemStack... var0) {
      return of(Arrays.stream(var0));
   }

   public static Ingredient of(Stream<ItemStack> var0) {
      return fromValues(var0.filter(var0x -> !var0x.isEmpty()).map(Ingredient.ItemValue::new));
   }

   public static Ingredient of(TagKey<Item> var0) {
      return fromValues(Stream.of(new Ingredient.TagValue(var0)));
   }

   private static Codec<Ingredient> codec(boolean var0) {
      Codec var1 = Codec.list(Ingredient.Value.CODEC)
         .comapFlatMap(
            var1x -> !var0 && var1x.size() < 1
                  ? DataResult.error(() -> "Item array cannot be empty, at least one item must be defined")
                  : DataResult.success(var1x.toArray(new Ingredient.Value[0])),
            List::of
         );
      return ExtraCodecs.either(var1, Ingredient.Value.CODEC)
         .flatComapMap(
            var0x -> (Ingredient)var0x.map(Ingredient::new, var0xx -> new Ingredient(new Ingredient.Value[]{var0xx})),
            var1x -> {
               if (var1x.values.length == 1) {
                  return DataResult.success(Either.right(var1x.values[0]));
               } else {
                  return var1x.values.length == 0 && !var0
                     ? DataResult.error(() -> "Item array cannot be empty, at least one item must be defined")
                     : DataResult.success(Either.left(var1x.values));
               }
            }
         );
   }

   static record ItemValue(ItemStack b) implements Ingredient.Value {
      private final ItemStack item;
      static final Codec<Ingredient.ItemValue> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(ItemStack.SIMPLE_ITEM_CODEC.fieldOf("item").forGetter(var0x -> var0x.item)).apply(var0, Ingredient.ItemValue::new)
      );

      private ItemValue(ItemStack var1) {
         super();
         this.item = var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof Ingredient.ItemValue)) {
            return false;
         } else {
            Ingredient.ItemValue var2 = (Ingredient.ItemValue)var1;
            return var2.item.getItem().equals(this.item.getItem()) && var2.item.getCount() == this.item.getCount();
         }
      }

      @Override
      public Collection<ItemStack> getItems() {
         return Collections.singleton(this.item);
      }
   }

   static record TagValue(TagKey<Item> b) implements Ingredient.Value {
      private final TagKey<Item> tag;
      static final Codec<Ingredient.TagValue> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(var0x -> var0x.tag)).apply(var0, Ingredient.TagValue::new)
      );

      TagValue(TagKey<Item> var1) {
         super();
         this.tag = var1;
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof Ingredient.TagValue var2 ? var2.tag.location().equals(this.tag.location()) : false;
      }

      @Override
      public Collection<ItemStack> getItems() {
         ArrayList var1 = Lists.newArrayList();

         for(Holder var3 : BuiltInRegistries.ITEM.getTagOrEmpty(this.tag)) {
            var1.add(new ItemStack(var3));
         }

         return var1;
      }
   }

   interface Value {
      Codec<Ingredient.Value> CODEC = ExtraCodecs.xor(Ingredient.ItemValue.CODEC, Ingredient.TagValue.CODEC)
         .xmap(var0 -> (Ingredient.Value)var0.map(var0x -> var0x, var0x -> var0x), var0 -> {
            if (var0 instanceof Ingredient.TagValue var1) {
               return Either.right(var1);
            } else if (var0 instanceof Ingredient.ItemValue var2) {
               return Either.left(var2);
            } else {
               throw new UnsupportedOperationException("This is neither an item value nor a tag value.");
            }
         });

      Collection<ItemStack> getItems();
   }
}
