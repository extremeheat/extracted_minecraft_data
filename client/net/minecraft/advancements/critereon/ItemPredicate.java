package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
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
   private final Item item;
   private final MinMaxBounds.Ints count;
   private final MinMaxBounds.Ints durability;
   private final EnchantmentPredicate[] enchantments;
   @Nullable
   private final Potion potion;
   private final NbtPredicate nbt;

   public ItemPredicate() {
      super();
      this.tag = null;
      this.item = null;
      this.potion = null;
      this.count = MinMaxBounds.Ints.ANY;
      this.durability = MinMaxBounds.Ints.ANY;
      this.enchantments = new EnchantmentPredicate[0];
      this.nbt = NbtPredicate.ANY;
   }

   public ItemPredicate(@Nullable Tag<Item> var1, @Nullable Item var2, MinMaxBounds.Ints var3, MinMaxBounds.Ints var4, EnchantmentPredicate[] var5, @Nullable Potion var6, NbtPredicate var7) {
      super();
      this.tag = var1;
      this.item = var2;
      this.count = var3;
      this.durability = var4;
      this.enchantments = var5;
      this.potion = var6;
      this.nbt = var7;
   }

   public boolean matches(ItemStack var1) {
      if (this == ANY) {
         return true;
      } else if (this.tag != null && !this.tag.contains(var1.getItem())) {
         return false;
      } else if (this.item != null && var1.getItem() != this.item) {
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
         Map var2 = EnchantmentHelper.getEnchantments(var1);

         for(int var3 = 0; var3 < this.enchantments.length; ++var3) {
            if (!this.enchantments[var3].containedIn(var2)) {
               return false;
            }
         }

         Potion var4 = PotionUtils.getPotion(var1);
         if (this.potion != null && this.potion != var4) {
            return false;
         } else {
            return true;
         }
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
            Item var5 = null;
            if (var1.has("item")) {
               ResourceLocation var6 = new ResourceLocation(GsonHelper.getAsString(var1, "item"));
               var5 = (Item)Registry.ITEM.getOptional(var6).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown item id '" + var6 + "'");
               });
            }

            Tag var10 = null;
            if (var1.has("tag")) {
               ResourceLocation var7 = new ResourceLocation(GsonHelper.getAsString(var1, "tag"));
               var10 = ItemTags.getAllTags().getTag(var7);
               if (var10 == null) {
                  throw new JsonSyntaxException("Unknown item tag '" + var7 + "'");
               }
            }

            EnchantmentPredicate[] var11 = EnchantmentPredicate.fromJsonArray(var1.get("enchantments"));
            Potion var8 = null;
            if (var1.has("potion")) {
               ResourceLocation var9 = new ResourceLocation(GsonHelper.getAsString(var1, "potion"));
               var8 = (Potion)Registry.POTION.getOptional(var9).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown potion '" + var9 + "'");
               });
            }

            return new ItemPredicate(var10, var5, var2, var3, var11, var8, var4);
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
         if (this.item != null) {
            var1.addProperty("item", Registry.ITEM.getKey(this.item).toString());
         }

         if (this.tag != null) {
            var1.addProperty("tag", this.tag.getId().toString());
         }

         var1.add("count", this.count.serializeToJson());
         var1.add("durability", this.durability.serializeToJson());
         var1.add("nbt", this.nbt.serializeToJson());
         if (this.enchantments.length > 0) {
            JsonArray var2 = new JsonArray();
            EnchantmentPredicate[] var3 = this.enchantments;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               EnchantmentPredicate var6 = var3[var5];
               var2.add(var6.serializeToJson());
            }

            var1.add("enchantments", var2);
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
      @Nullable
      private Item item;
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

      public ItemPredicate.Builder of(ItemLike var1) {
         this.item = var1.asItem();
         return this;
      }

      public ItemPredicate.Builder of(Tag<Item> var1) {
         this.tag = var1;
         return this;
      }

      public ItemPredicate.Builder withCount(MinMaxBounds.Ints var1) {
         this.count = var1;
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

      public ItemPredicate build() {
         return new ItemPredicate(this.tag, this.item, this.count, this.durability, (EnchantmentPredicate[])this.enchantments.toArray(new EnchantmentPredicate[0]), this.potion, this.nbt);
      }
   }
}
