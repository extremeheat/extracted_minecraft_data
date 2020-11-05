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
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class Ingredient implements Predicate<ItemStack> {
   public static final Ingredient EMPTY = new Ingredient(Stream.empty());
   private final Ingredient.Value[] values;
   private ItemStack[] itemStacks;
   private IntList stackingIds;

   private Ingredient(Stream<? extends Ingredient.Value> var1) {
      super();
      this.values = (Ingredient.Value[])var1.toArray((var0) -> {
         return new Ingredient.Value[var0];
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
               if (var5.getItem() == var1.getItem()) {
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
      var1.writeVarInt(this.itemStacks.length);

      for(int var2 = 0; var2 < this.itemStacks.length; ++var2) {
         var1.writeItem(this.itemStacks[var2]);
      }

   }

   public JsonElement toJson() {
      if (this.values.length == 1) {
         return this.values[0].serialize();
      } else {
         JsonArray var1 = new JsonArray();
         Ingredient.Value[] var2 = this.values;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Ingredient.Value var5 = var2[var4];
            var1.add(var5.serialize());
         }

         return var1;
      }
   }

   public boolean isEmpty() {
      return this.values.length == 0 && (this.itemStacks == null || this.itemStacks.length == 0) && (this.stackingIds == null || this.stackingIds.isEmpty());
   }

   private static Ingredient fromValues(Stream<? extends Ingredient.Value> var0) {
      Ingredient var1 = new Ingredient(var0);
      return var1.values.length == 0 ? EMPTY : var1;
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
      }).map((var0x) -> {
         return new Ingredient.ItemValue(var0x);
      }));
   }

   public static Ingredient of(Tag<Item> var0) {
      return fromValues(Stream.of(new Ingredient.TagValue(var0)));
   }

   public static Ingredient fromNetwork(FriendlyByteBuf var0) {
      int var1 = var0.readVarInt();
      return fromValues(Stream.generate(() -> {
         return new Ingredient.ItemValue(var0.readItem());
      }).limit((long)var1));
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

   private static Ingredient.Value valueFromJson(JsonObject var0) {
      if (var0.has("item") && var0.has("tag")) {
         throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
      } else {
         ResourceLocation var1;
         if (var0.has("item")) {
            var1 = new ResourceLocation(GsonHelper.getAsString(var0, "item"));
            Item var3 = (Item)Registry.ITEM.getOptional(var1).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown item '" + var1 + "'");
            });
            return new Ingredient.ItemValue(new ItemStack(var3));
         } else if (var0.has("tag")) {
            var1 = new ResourceLocation(GsonHelper.getAsString(var0, "tag"));
            Tag var2 = SerializationTags.getInstance().getItems().getTag(var1);
            if (var2 == null) {
               throw new JsonSyntaxException("Unknown item tag '" + var1 + "'");
            } else {
               return new Ingredient.TagValue(var2);
            }
         } else {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
         }
      }
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1) {
      return this.test((ItemStack)var1);
   }

   static class TagValue implements Ingredient.Value {
      private final Tag<Item> tag;

      private TagValue(Tag<Item> var1) {
         super();
         this.tag = var1;
      }

      public Collection<ItemStack> getItems() {
         ArrayList var1 = Lists.newArrayList();
         Iterator var2 = this.tag.getValues().iterator();

         while(var2.hasNext()) {
            Item var3 = (Item)var2.next();
            var1.add(new ItemStack(var3));
         }

         return var1;
      }

      public JsonObject serialize() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("tag", SerializationTags.getInstance().getItems().getIdOrThrow(this.tag).toString());
         return var1;
      }

      // $FF: synthetic method
      TagValue(Tag var1, Object var2) {
         this(var1);
      }
   }

   static class ItemValue implements Ingredient.Value {
      private final ItemStack item;

      private ItemValue(ItemStack var1) {
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

      // $FF: synthetic method
      ItemValue(ItemStack var1, Object var2) {
         this(var1);
      }
   }

   interface Value {
      Collection<ItemStack> getItems();

      JsonObject serialize();
   }
}
