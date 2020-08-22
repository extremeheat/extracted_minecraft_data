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
   private final Tag tag;
   @Nullable
   private final Item item;
   private final MinMaxBounds.Ints count;
   private final MinMaxBounds.Ints durability;
   private final EnchantmentPredicate[] enchantments;
   private final EnchantmentPredicate[] storedEnchantments;
   @Nullable
   private final Potion potion;
   private final NbtPredicate nbt;

   public ItemPredicate() {
      this.tag = null;
      this.item = null;
      this.potion = null;
      this.count = MinMaxBounds.Ints.ANY;
      this.durability = MinMaxBounds.Ints.ANY;
      this.enchantments = EnchantmentPredicate.NONE;
      this.storedEnchantments = EnchantmentPredicate.NONE;
      this.nbt = NbtPredicate.ANY;
   }

   public ItemPredicate(@Nullable Tag var1, @Nullable Item var2, MinMaxBounds.Ints var3, MinMaxBounds.Ints var4, EnchantmentPredicate[] var5, EnchantmentPredicate[] var6, @Nullable Potion var7, NbtPredicate var8) {
      this.tag = var1;
      this.item = var2;
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

            Potion var11 = null;
            if (var1.has("potion")) {
               ResourceLocation var8 = new ResourceLocation(GsonHelper.getAsString(var1, "potion"));
               var11 = (Potion)Registry.POTION.getOptional(var8).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown potion '" + var8 + "'");
               });
            }

            EnchantmentPredicate[] var12 = EnchantmentPredicate.fromJsonArray(var1.get("enchantments"));
            EnchantmentPredicate[] var9 = EnchantmentPredicate.fromJsonArray(var1.get("stored_enchantments"));
            return new ItemPredicate(var10, var5, var2, var3, var12, var9, var11, var4);
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
         JsonArray var2;
         EnchantmentPredicate[] var3;
         int var4;
         int var5;
         EnchantmentPredicate var6;
         if (this.enchantments.length > 0) {
            var2 = new JsonArray();
            var3 = this.enchantments;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               var6 = var3[var5];
               var2.add(var6.serializeToJson());
            }

            var1.add("enchantments", var2);
         }

         if (this.storedEnchantments.length > 0) {
            var2 = new JsonArray();
            var3 = this.storedEnchantments;
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
               var6 = var3[var5];
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
      private final List enchantments = Lists.newArrayList();
      private final List storedEnchantments = Lists.newArrayList();
      @Nullable
      private Item item;
      @Nullable
      private Tag tag;
      private MinMaxBounds.Ints count;
      private MinMaxBounds.Ints durability;
      @Nullable
      private Potion potion;
      private NbtPredicate nbt;

      private Builder() {
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

      public ItemPredicate.Builder of(Tag var1) {
         this.tag = var1;
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
         return new ItemPredicate(this.tag, this.item, this.count, this.durability, (EnchantmentPredicate[])this.enchantments.toArray(EnchantmentPredicate.NONE), (EnchantmentPredicate[])this.storedEnchantments.toArray(EnchantmentPredicate.NONE), this.potion, this.nbt);
      }
   }
}
