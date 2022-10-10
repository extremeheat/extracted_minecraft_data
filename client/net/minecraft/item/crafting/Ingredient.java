package net.minecraft.item.crafting;

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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public final class Ingredient implements Predicate<ItemStack> {
   private static final Predicate<? super Ingredient.IItemList> field_209362_b = (var0) -> {
      return !var0.func_199799_a().stream().allMatch(ItemStack::func_190926_b);
   };
   public static final Ingredient field_193370_a = new Ingredient(Stream.empty());
   private final Ingredient.IItemList[] field_199807_b;
   private ItemStack[] field_193371_b;
   private IntList field_194140_c;

   private Ingredient(Stream<? extends Ingredient.IItemList> var1) {
      super();
      this.field_199807_b = (Ingredient.IItemList[])var1.filter(field_209362_b).toArray((var0) -> {
         return new Ingredient.IItemList[var0];
      });
   }

   public ItemStack[] func_193365_a() {
      this.func_199806_d();
      return this.field_193371_b;
   }

   private void func_199806_d() {
      if (this.field_193371_b == null) {
         this.field_193371_b = (ItemStack[])Arrays.stream(this.field_199807_b).flatMap((var0) -> {
            return var0.func_199799_a().stream();
         }).distinct().toArray((var0) -> {
            return new ItemStack[var0];
         });
      }

   }

   public boolean test(@Nullable ItemStack var1) {
      if (var1 == null) {
         return false;
      } else if (this.field_199807_b.length == 0) {
         return var1.func_190926_b();
      } else {
         this.func_199806_d();
         ItemStack[] var2 = this.field_193371_b;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ItemStack var5 = var2[var4];
            if (var5.func_77973_b() == var1.func_77973_b()) {
               return true;
            }
         }

         return false;
      }
   }

   public IntList func_194139_b() {
      if (this.field_194140_c == null) {
         this.func_199806_d();
         this.field_194140_c = new IntArrayList(this.field_193371_b.length);
         ItemStack[] var1 = this.field_193371_b;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            ItemStack var4 = var1[var3];
            this.field_194140_c.add(RecipeItemHelper.func_194113_b(var4));
         }

         this.field_194140_c.sort(IntComparators.NATURAL_COMPARATOR);
      }

      return this.field_194140_c;
   }

   public void func_199564_a(PacketBuffer var1) {
      this.func_199806_d();
      var1.func_150787_b(this.field_193371_b.length);

      for(int var2 = 0; var2 < this.field_193371_b.length; ++var2) {
         var1.func_150788_a(this.field_193371_b[var2]);
      }

   }

   public JsonElement func_200304_c() {
      if (this.field_199807_b.length == 1) {
         return this.field_199807_b[0].func_200303_b();
      } else {
         JsonArray var1 = new JsonArray();
         Ingredient.IItemList[] var2 = this.field_199807_b;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Ingredient.IItemList var5 = var2[var4];
            var1.add(var5.func_200303_b());
         }

         return var1;
      }
   }

   public boolean func_203189_d() {
      return this.field_199807_b.length == 0 && (this.field_193371_b == null || this.field_193371_b.length == 0) && (this.field_194140_c == null || this.field_194140_c.isEmpty());
   }

   private static Ingredient func_209357_a(Stream<? extends Ingredient.IItemList> var0) {
      Ingredient var1 = new Ingredient(var0);
      return var1.field_199807_b.length == 0 ? field_193370_a : var1;
   }

   public static Ingredient func_199804_a(IItemProvider... var0) {
      return func_209357_a(Arrays.stream(var0).map((var0x) -> {
         return new Ingredient.SingleItemList(new ItemStack(var0x));
      }));
   }

   public static Ingredient func_193369_a(ItemStack... var0) {
      return func_209357_a(Arrays.stream(var0).map((var0x) -> {
         return new Ingredient.SingleItemList(var0x);
      }));
   }

   public static Ingredient func_199805_a(Tag<Item> var0) {
      return func_209357_a(Stream.of(new Ingredient.TagList(var0)));
   }

   public static Ingredient func_199566_b(PacketBuffer var0) {
      int var1 = var0.func_150792_a();
      return func_209357_a(Stream.generate(() -> {
         return new Ingredient.SingleItemList(var0.func_150791_c());
      }).limit((long)var1));
   }

   public static Ingredient func_199802_a(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         if (var0.isJsonObject()) {
            return func_209357_a(Stream.of(func_199803_a(var0.getAsJsonObject())));
         } else if (var0.isJsonArray()) {
            JsonArray var1 = var0.getAsJsonArray();
            if (var1.size() == 0) {
               throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            } else {
               return func_209357_a(StreamSupport.stream(var1.spliterator(), false).map((var0x) -> {
                  return func_199803_a(JsonUtils.func_151210_l(var0x, "item"));
               }));
            }
         } else {
            throw new JsonSyntaxException("Expected item to be object or array of objects");
         }
      } else {
         throw new JsonSyntaxException("Item cannot be null");
      }
   }

   public static Ingredient.IItemList func_199803_a(JsonObject var0) {
      if (var0.has("item") && var0.has("tag")) {
         throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
      } else {
         ResourceLocation var1;
         if (var0.has("item")) {
            var1 = new ResourceLocation(JsonUtils.func_151200_h(var0, "item"));
            Item var3 = (Item)IRegistry.field_212630_s.func_212608_b(var1);
            if (var3 == null) {
               throw new JsonSyntaxException("Unknown item '" + var1 + "'");
            } else {
               return new Ingredient.SingleItemList(new ItemStack(var3));
            }
         } else if (var0.has("tag")) {
            var1 = new ResourceLocation(JsonUtils.func_151200_h(var0, "tag"));
            Tag var2 = ItemTags.func_199903_a().func_199910_a(var1);
            if (var2 == null) {
               throw new JsonSyntaxException("Unknown item tag '" + var1 + "'");
            } else {
               return new Ingredient.TagList(var2);
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

   static class TagList implements Ingredient.IItemList {
      private final Tag<Item> field_199800_a;

      private TagList(Tag<Item> var1) {
         super();
         this.field_199800_a = var1;
      }

      public Collection<ItemStack> func_199799_a() {
         ArrayList var1 = Lists.newArrayList();
         Iterator var2 = this.field_199800_a.func_199885_a().iterator();

         while(var2.hasNext()) {
            Item var3 = (Item)var2.next();
            var1.add(new ItemStack(var3));
         }

         return var1;
      }

      public JsonObject func_200303_b() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("tag", this.field_199800_a.func_199886_b().toString());
         return var1;
      }

      // $FF: synthetic method
      TagList(Tag var1, Object var2) {
         this(var1);
      }
   }

   static class SingleItemList implements Ingredient.IItemList {
      private final ItemStack field_199801_a;

      private SingleItemList(ItemStack var1) {
         super();
         this.field_199801_a = var1;
      }

      public Collection<ItemStack> func_199799_a() {
         return Collections.singleton(this.field_199801_a);
      }

      public JsonObject func_200303_b() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("item", IRegistry.field_212630_s.func_177774_c(this.field_199801_a.func_77973_b()).toString());
         return var1;
      }

      // $FF: synthetic method
      SingleItemList(ItemStack var1, Object var2) {
         this(var1);
      }
   }

   interface IItemList {
      Collection<ItemStack> func_199799_a();

      JsonObject func_200303_b();
   }
}
