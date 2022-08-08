package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class Ingredient implements Predicate<ItemStack> {
   public static final Ingredient EMPTY = new Ingredient(Stream.empty());
   private final Value[] values;
   @Nullable
   private ItemStack[] itemStacks;
   @Nullable
   private IntList stackingIds;

   private Ingredient(Stream<? extends Value> var1) {
      super();
      this.values = (Value[])var1.toArray((var0) -> {
         return new Value[var0];
      });
   }

   public ItemStack[] getItems() {
      this.dissolve();
      return this.itemStacks;
   }

   private void dissolve() {
      if (this.itemStacks == null) {
         this.itemStacks = (ItemStack[])Arrays.stream(this.values).flatMap((var0) -> {
            return var0.getItems().stream();
         }).distinct().toArray((var0) -> {
            return new ItemStack[var0];
         });
      }

   }

   public boolean test(@Nullable ItemStack var1) {
      if (var1 == null) {
         return false;
      } else {
         this.dissolve();
         if (this.itemStacks.length == 0) {
            return var1.isEmpty();
         } else {
            ItemStack[] var2 = this.itemStacks;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               ItemStack var5 = var2[var4];
               if (var5.is(var1.getItem())) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public IntList getStackingIds() {
      if (this.stackingIds == null) {
         this.dissolve();
         this.stackingIds = new IntArrayList(this.itemStacks.length);
         ItemStack[] var1 = this.itemStacks;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            ItemStack var4 = var1[var3];
            this.stackingIds.add(StackedContents.getStackingIndex(var4));
         }

         this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
      }

      return this.stackingIds;
   }

   public void toNetwork(FriendlyByteBuf var1) {
      this.dissolve();
      var1.writeCollection(Arrays.asList(this.itemStacks), FriendlyByteBuf::writeItem);
   }

   public JsonElement toJson() {
      if (this.values.length == 1) {
         return this.values[0].serialize();
      } else {
         JsonArray var1 = new JsonArray();
         Value[] var2 = this.values;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Value var5 = var2[var4];
            var1.add(var5.serialize());
         }

         return var1;
      }
   }

   public boolean isEmpty() {
      return this.values.length == 0 && (this.itemStacks == null || this.itemStacks.length == 0) && (this.stackingIds == null || this.stackingIds.isEmpty());
   }

   private static Ingredient fromValues(Stream<? extends Value> var0) {
      Ingredient var1 = new Ingredient(var0);
      return var1.values.length == 0 ? EMPTY : var1;
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
      return fromValues(var0.filter((var0x) -> {
         return !var0x.isEmpty();
      }).map(ItemValue::new));
   }

   public static Ingredient of(TagKey<Item> var0) {
      return fromValues(Stream.of(new TagValue(var0)));
   }

   public static Ingredient fromNetwork(FriendlyByteBuf var0) {
      return fromValues(var0.readList(FriendlyByteBuf::readItem).stream().map(ItemValue::new));
   }

   public static Ingredient fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         if (var0.isJsonObject()) {
            return fromValues(Stream.of(valueFromJson(var0.getAsJsonObject())));
         } else if (var0.isJsonArray()) {
            JsonArray var1 = var0.getAsJsonArray();
            if (var1.size() == 0) {
               throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            } else {
               return fromValues(StreamSupport.stream(var1.spliterator(), false).map((var0x) -> {
                  return valueFromJson(GsonHelper.convertToJsonObject(var0x, "item"));
               }));
            }
         } else {
            throw new JsonSyntaxException("Expected item to be object or array of objects");
         }
      } else {
         throw new JsonSyntaxException("Item cannot be null");
      }
   }

   private static Value valueFromJson(JsonObject var0) {
      if (var0.has("item") && var0.has("tag")) {
         throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
      } else if (var0.has("item")) {
         Item var3 = ShapedRecipe.itemFromJson(var0);
         return new ItemValue(new ItemStack(var3));
      } else if (var0.has("tag")) {
         ResourceLocation var1 = new ResourceLocation(GsonHelper.getAsString(var0, "tag"));
         TagKey var2 = TagKey.create(Registry.ITEM_REGISTRY, var1);
         return new TagValue(var2);
      } else {
         throw new JsonParseException("An ingredient entry needs either a tag or an item");
      }
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1) {
      return this.test((ItemStack)var1);
   }

   private interface Value {
      Collection<ItemStack> getItems();

      JsonObject serialize();
   }

   static class TagValue implements Value {
      private final TagKey<Item> tag;

      TagValue(TagKey<Item> var1) {
         super();
         this.tag = var1;
      }

      public Collection<ItemStack> getItems() {
         ArrayList var1 = Lists.newArrayList();
         Iterator var2 = Registry.ITEM.getTagOrEmpty(this.tag).iterator();

         while(var2.hasNext()) {
            Holder var3 = (Holder)var2.next();
            var1.add(new ItemStack(var3));
         }

         return var1;
      }

      public JsonObject serialize() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("tag", this.tag.location().toString());
         return var1;
      }
   }

   static class ItemValue implements Value {
      private final ItemStack item;

      ItemValue(ItemStack var1) {
         super();
         this.item = var1;
      }

      public Collection<ItemStack> getItems() {
         return Collections.singleton(this.item);
      }

      public JsonObject serialize() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("item", Registry.ITEM.getKey(this.item.getItem()).toString());
         return var1;
      }
   }
}
