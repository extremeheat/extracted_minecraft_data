package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;

public class ItemPredicate {
   public static final ItemPredicate ANY = new ItemPredicate();
   @Nullable
   private final Tag<Item> tag;
   @Nullable
   private final Set<Item> items;
   private final MinMaxBounds.Ints count;
   private final MinMaxBounds.Ints durability;
   private final EnchantmentPredicate[] enchantments;
   private final EnchantmentPredicate[] storedEnchantments;
   @Nullable
   private final Potion potion;
   private final NbtPredicate nbt;

   public ItemPredicate() {
      super();
      this.tag = null;
      this.items = null;
      this.potion = null;
      this.count = MinMaxBounds.Ints.ANY;
      this.durability = MinMaxBounds.Ints.ANY;
      this.enchantments = EnchantmentPredicate.NONE;
      this.storedEnchantments = EnchantmentPredicate.NONE;
      this.nbt = NbtPredicate.ANY;
   }

   public ItemPredicate(@Nullable Tag<Item> var1, @Nullable Set<Item> var2, MinMaxBounds.Ints var3, MinMaxBounds.Ints var4, EnchantmentPredicate[] var5, EnchantmentPredicate[] var6, @Nullable Potion var7, NbtPredicate var8) {
      super();
      this.tag = var1;
      this.items = var2;
      this.count = var3;
      this.durability = var4;
      this.enchantments = var5;
      this.storedEnchantments = var6;
      this.potion = var7;
      this.nbt = var8;
   }

   public boolean matches(ItemStack var1) {
      if (this == ANY) {
         return true;
      } else if (this.tag != null && !var1.method_86(this.tag)) {
         return false;
      } else if (this.items != null && !this.items.contains(var1.getItem())) {
         return false;
      } else if (!this.count.matches(var1.getCount())) {
         return false;
      } else if (!this.durability.isAny() && !var1.isDamageableItem()) {
         return false;
      } else if (!this.durability.matches(var1.getMaxDamage() - var1.getDamageValue())) {
         return false;
      } else if (!this.nbt.matches(var1)) {
         return false;
      } else {
         Map var2;
         EnchantmentPredicate[] var3;
         int var4;
         int var5;
         EnchantmentPredicate var6;
         if (this.enchantments.length > 0) {
            var2 = EnchantmentHelper.deserializeEnchantments(var1.getEnchantmentTags());
            var3 = this.enchantments;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               var6 = var3[var5];
               if (!var6.containedIn(var2)) {
                  return false;
               }
            }
         }

         if (this.storedEnchantments.length > 0) {
            var2 = EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(var1));
            var3 = this.storedEnchantments;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               var6 = var3[var5];
               if (!var6.containedIn(var2)) {
                  return false;
               }
            }
         }

         Potion var7 = PotionUtils.getPotion(var1);
         return this.potion == null || this.potion == var7;
      }
   }

   public static ItemPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(var0, "item");
         MinMaxBounds.Ints var2 = MinMaxBounds.Ints.fromJson(var1.get("count"));
         MinMaxBounds.Ints var3 = MinMaxBounds.Ints.fromJson(var1.get("durability"));
         if (var1.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
         } else {
            NbtPredicate var4 = NbtPredicate.fromJson(var1.get("nbt"));
            ImmutableSet var5 = null;
            JsonArray var6 = GsonHelper.getAsJsonArray(var1, "items", (JsonArray)null);
            if (var6 != null) {
               com.google.common.collect.ImmutableSet.Builder var7 = ImmutableSet.builder();
               Iterator var8 = var6.iterator();

               while(var8.hasNext()) {
                  JsonElement var9 = (JsonElement)var8.next();
                  ResourceLocation var10 = new ResourceLocation(GsonHelper.convertToString(var9, "item"));
                  var7.add((Item)Registry.ITEM.getOptional(var10).orElseThrow(() -> {
                     return new JsonSyntaxException("Unknown item id '" + var10 + "'");
                  }));
               }

               var5 = var7.build();
            }

            Tag var12 = null;
            if (var1.has("tag")) {
               ResourceLocation var13 = new ResourceLocation(GsonHelper.getAsString(var1, "tag"));
               var12 = SerializationTags.getInstance().getTagOrThrow(Registry.ITEM_REGISTRY, var13, (var0x) -> {
                  return new JsonSyntaxException("Unknown item tag '" + var0x + "'");
               });
            }

            Potion var14 = null;
            if (var1.has("potion")) {
               ResourceLocation var15 = new ResourceLocation(GsonHelper.getAsString(var1, "potion"));
               var14 = (Potion)Registry.POTION.getOptional(var15).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown potion '" + var15 + "'");
               });
            }

            EnchantmentPredicate[] var16 = EnchantmentPredicate.fromJsonArray(var1.get("enchantments"));
            EnchantmentPredicate[] var11 = EnchantmentPredicate.fromJsonArray(var1.get("stored_enchantments"));
            return new ItemPredicate(var12, var5, var2, var3, var16, var11, var14, var4);
         }
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         JsonArray var2;
         if (this.items != null) {
            var2 = new JsonArray();
            Iterator var3 = this.items.iterator();

            while(var3.hasNext()) {
               Item var4 = (Item)var3.next();
               var2.add(Registry.ITEM.getKey(var4).toString());
            }

            var1.add("items", var2);
         }

         if (this.tag != null) {
            var1.addProperty("tag", SerializationTags.getInstance().getIdOrThrow(Registry.ITEM_REGISTRY, this.tag, () -> {
               return new IllegalStateException("Unknown item tag");
            }).toString());
         }

         var1.add("count", this.count.serializeToJson());
         var1.add("durability", this.durability.serializeToJson());
         var1.add("nbt", this.nbt.serializeToJson());
         int var5;
         EnchantmentPredicate var6;
         EnchantmentPredicate[] var7;
         int var8;
         if (this.enchantments.length > 0) {
            var2 = new JsonArray();
            var7 = this.enchantments;
            var8 = var7.length;

            for(var5 = 0; var5 < var8; ++var5) {
               var6 = var7[var5];
               var2.add(var6.serializeToJson());
            }

            var1.add("enchantments", var2);
         }

         if (this.storedEnchantments.length > 0) {
            var2 = new JsonArray();
            var7 = this.storedEnchantments;
            var8 = var7.length;

            for(var5 = 0; var5 < var8; ++var5) {
               var6 = var7[var5];
               var2.add(var6.serializeToJson());
            }

            var1.add("stored_enchantments", var2);
         }

         if (this.potion != null) {
            var1.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
         }

         return var1;
      }
   }

   public static ItemPredicate[] fromJsonArray(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         JsonArray var1 = GsonHelper.convertToJsonArray(var0, "items");
         ItemPredicate[] var2 = new ItemPredicate[var1.size()];

         for(int var3 = 0; var3 < var2.length; ++var3) {
            var2[var3] = fromJson(var1.get(var3));
         }

         return var2;
      } else {
         return new ItemPredicate[0];
      }
   }

   public static class Builder {
      private final List<EnchantmentPredicate> enchantments = Lists.newArrayList();
      private final List<EnchantmentPredicate> storedEnchantments = Lists.newArrayList();
      @Nullable
      private Set<Item> items;
      @Nullable
      private Tag<Item> tag;
      private MinMaxBounds.Ints count;
      private MinMaxBounds.Ints durability;
      @Nullable
      private Potion potion;
      private NbtPredicate nbt;

      private Builder() {
         super();
         this.count = MinMaxBounds.Ints.ANY;
         this.durability = MinMaxBounds.Ints.ANY;
         this.nbt = NbtPredicate.ANY;
      }

      public static ItemPredicate.Builder item() {
         return new ItemPredicate.Builder();
      }

      // $FF: renamed from: of (net.minecraft.world.level.ItemLike[]) net.minecraft.advancements.critereon.ItemPredicate$Builder
      public ItemPredicate.Builder method_90(ItemLike... var1) {
         this.items = (Set)Stream.of(var1).map(ItemLike::asItem).collect(ImmutableSet.toImmutableSet());
         return this;
      }

      // $FF: renamed from: of (net.minecraft.tags.Tag) net.minecraft.advancements.critereon.ItemPredicate$Builder
      public ItemPredicate.Builder method_91(Tag<Item> var1) {
         this.tag = var1;
         return this;
      }

      public ItemPredicate.Builder withCount(MinMaxBounds.Ints var1) {
         this.count = var1;
         return this;
      }

      public ItemPredicate.Builder hasDurability(MinMaxBounds.Ints var1) {
         this.durability = var1;
         return this;
      }

      public ItemPredicate.Builder isPotion(Potion var1) {
         this.potion = var1;
         return this;
      }

      public ItemPredicate.Builder hasNbt(CompoundTag var1) {
         this.nbt = new NbtPredicate(var1);
         return this;
      }

      public ItemPredicate.Builder hasEnchantment(EnchantmentPredicate var1) {
         this.enchantments.add(var1);
         return this;
      }

      public ItemPredicate.Builder hasStoredEnchantment(EnchantmentPredicate var1) {
         this.storedEnchantments.add(var1);
         return this;
      }

      public ItemPredicate build() {
         return new ItemPredicate(this.tag, this.items, this.count, this.durability, (EnchantmentPredicate[])this.enchantments.toArray(EnchantmentPredicate.NONE), (EnchantmentPredicate[])this.storedEnchantments.toArray(EnchantmentPredicate.NONE), this.potion, this.nbt);
      }
   }
}
