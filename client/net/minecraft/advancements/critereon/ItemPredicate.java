package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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
   private final TagKey<Item> tag;
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

   public ItemPredicate(
      @Nullable TagKey<Item> var1,
      @Nullable Set<Item> var2,
      MinMaxBounds.Ints var3,
      MinMaxBounds.Ints var4,
      EnchantmentPredicate[] var5,
      EnchantmentPredicate[] var6,
      @Nullable Potion var7,
      NbtPredicate var8
   ) {
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
      } else if (this.tag != null && !var1.is(this.tag)) {
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
         if (this.enchantments.length > 0) {
            Map var2 = EnchantmentHelper.deserializeEnchantments(var1.getEnchantmentTags());

            for(EnchantmentPredicate var6 : this.enchantments) {
               if (!var6.containedIn(var2)) {
                  return false;
               }
            }
         }

         if (this.storedEnchantments.length > 0) {
            Map var7 = EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(var1));

            for(EnchantmentPredicate var12 : this.storedEnchantments) {
               if (!var12.containedIn(var7)) {
                  return false;
               }
            }
         }

         Potion var8 = PotionUtils.getPotion(var1);
         return this.potion == null || this.potion == var8;
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
            JsonArray var6 = GsonHelper.getAsJsonArray(var1, "items", null);
            if (var6 != null) {
               com.google.common.collect.ImmutableSet.Builder var7 = ImmutableSet.builder();

               for(JsonElement var9 : var6) {
                  ResourceLocation var10 = new ResourceLocation(GsonHelper.convertToString(var9, "item"));
                  var7.add(Registry.ITEM.getOptional(var10).orElseThrow(() -> new JsonSyntaxException("Unknown item id '" + var10 + "'")));
               }

               var5 = var7.build();
            }

            TagKey var11 = null;
            if (var1.has("tag")) {
               ResourceLocation var12 = new ResourceLocation(GsonHelper.getAsString(var1, "tag"));
               var11 = TagKey.create(Registry.ITEM_REGISTRY, var12);
            }

            Potion var13 = null;
            if (var1.has("potion")) {
               ResourceLocation var14 = new ResourceLocation(GsonHelper.getAsString(var1, "potion"));
               var13 = Registry.POTION.getOptional(var14).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + var14 + "'"));
            }

            EnchantmentPredicate[] var15 = EnchantmentPredicate.fromJsonArray(var1.get("enchantments"));
            EnchantmentPredicate[] var16 = EnchantmentPredicate.fromJsonArray(var1.get("stored_enchantments"));
            return new ItemPredicate(var11, var5, var2, var3, var15, var16, var13, var4);
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
         if (this.items != null) {
            JsonArray var2 = new JsonArray();

            for(Item var4 : this.items) {
               var2.add(Registry.ITEM.getKey(var4).toString());
            }

            var1.add("items", var2);
         }

         if (this.tag != null) {
            var1.addProperty("tag", this.tag.location().toString());
         }

         var1.add("count", this.count.serializeToJson());
         var1.add("durability", this.durability.serializeToJson());
         var1.add("nbt", this.nbt.serializeToJson());
         if (this.enchantments.length > 0) {
            JsonArray var7 = new JsonArray();

            for(EnchantmentPredicate var6 : this.enchantments) {
               var7.add(var6.serializeToJson());
            }

            var1.add("enchantments", var7);
         }

         if (this.storedEnchantments.length > 0) {
            JsonArray var8 = new JsonArray();

            for(EnchantmentPredicate var14 : this.storedEnchantments) {
               var8.add(var14.serializeToJson());
            }

            var1.add("stored_enchantments", var8);
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
      private TagKey<Item> tag;
      private MinMaxBounds.Ints count = MinMaxBounds.Ints.ANY;
      private MinMaxBounds.Ints durability = MinMaxBounds.Ints.ANY;
      @Nullable
      private Potion potion;
      private NbtPredicate nbt = NbtPredicate.ANY;

      private Builder() {
         super();
      }

      public static ItemPredicate.Builder item() {
         return new ItemPredicate.Builder();
      }

      public ItemPredicate.Builder of(ItemLike... var1) {
         this.items = Stream.of(var1).map(ItemLike::asItem).collect(ImmutableSet.toImmutableSet());
         return this;
      }

      public ItemPredicate.Builder of(TagKey<Item> var1) {
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
         return new ItemPredicate(
            this.tag,
            this.items,
            this.count,
            this.durability,
            this.enchantments.toArray(EnchantmentPredicate.NONE),
            this.storedEnchantments.toArray(EnchantmentPredicate.NONE),
            this.potion,
            this.nbt
         );
      }
   }
}
