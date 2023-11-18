package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;

public record ItemPredicate(
   Optional<TagKey<Item>> b,
   Optional<HolderSet<Item>> c,
   MinMaxBounds.Ints d,
   MinMaxBounds.Ints e,
   List<EnchantmentPredicate> f,
   List<EnchantmentPredicate> g,
   Optional<Holder<Potion>> h,
   Optional<NbtPredicate> i
) {
   private final Optional<TagKey<Item>> tag;
   private final Optional<HolderSet<Item>> items;
   private final MinMaxBounds.Ints count;
   private final MinMaxBounds.Ints durability;
   private final List<EnchantmentPredicate> enchantments;
   private final List<EnchantmentPredicate> storedEnchantments;
   private final Optional<Holder<Potion>> potion;
   private final Optional<NbtPredicate> nbt;
   private static final Codec<HolderSet<Item>> ITEMS_CODEC = BuiltInRegistries.ITEM
      .holderByNameCodec()
      .listOf()
      .xmap(HolderSet::direct, var0 -> var0.stream().toList());
   public static final Codec<ItemPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.strictOptionalField(TagKey.codec(Registries.ITEM), "tag").forGetter(ItemPredicate::tag),
               ExtraCodecs.strictOptionalField(ITEMS_CODEC, "items").forGetter(ItemPredicate::items),
               ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "count", MinMaxBounds.Ints.ANY).forGetter(ItemPredicate::count),
               ExtraCodecs.strictOptionalField(MinMaxBounds.Ints.CODEC, "durability", MinMaxBounds.Ints.ANY).forGetter(ItemPredicate::durability),
               ExtraCodecs.strictOptionalField(EnchantmentPredicate.CODEC.listOf(), "enchantments", List.of()).forGetter(ItemPredicate::enchantments),
               ExtraCodecs.strictOptionalField(EnchantmentPredicate.CODEC.listOf(), "stored_enchantments", List.of())
                  .forGetter(ItemPredicate::storedEnchantments),
               ExtraCodecs.strictOptionalField(BuiltInRegistries.POTION.holderByNameCodec(), "potion").forGetter(ItemPredicate::potion),
               ExtraCodecs.strictOptionalField(NbtPredicate.CODEC, "nbt").forGetter(ItemPredicate::nbt)
            )
            .apply(var0, ItemPredicate::new)
   );

   public ItemPredicate(
      Optional<TagKey<Item>> var1,
      Optional<HolderSet<Item>> var2,
      MinMaxBounds.Ints var3,
      MinMaxBounds.Ints var4,
      List<EnchantmentPredicate> var5,
      List<EnchantmentPredicate> var6,
      Optional<Holder<Potion>> var7,
      Optional<NbtPredicate> var8
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
      if (this.tag.isPresent() && !var1.is(this.tag.get())) {
         return false;
      } else if (this.items.isPresent() && !var1.is(this.items.get())) {
         return false;
      } else if (!this.count.matches(var1.getCount())) {
         return false;
      } else if (!this.durability.isAny() && !var1.isDamageableItem()) {
         return false;
      } else if (!this.durability.matches(var1.getMaxDamage() - var1.getDamageValue())) {
         return false;
      } else if (this.nbt.isPresent() && !this.nbt.get().matches(var1)) {
         return false;
      } else {
         if (!this.enchantments.isEmpty()) {
            Map var2 = EnchantmentHelper.deserializeEnchantments(var1.getEnchantmentTags());

            for(EnchantmentPredicate var4 : this.enchantments) {
               if (!var4.containedIn(var2)) {
                  return false;
               }
            }
         }

         if (!this.storedEnchantments.isEmpty()) {
            Map var5 = EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(var1));

            for(EnchantmentPredicate var7 : this.storedEnchantments) {
               if (!var7.containedIn(var5)) {
                  return false;
               }
            }
         }

         return !this.potion.isPresent() || this.potion.get().value() == PotionUtils.getPotion(var1);
      }
   }

   public static Optional<ItemPredicate> fromJson(@Nullable JsonElement var0) {
      return var0 != null && !var0.isJsonNull()
         ? Optional.of(Util.getOrThrow(CODEC.parse(JsonOps.INSTANCE, var0), JsonParseException::new))
         : Optional.empty();
   }

   public JsonElement serializeToJson() {
      return Util.getOrThrow(CODEC.encodeStart(JsonOps.INSTANCE, this), IllegalStateException::new);
   }

   public static JsonElement serializeToJsonArray(List<ItemPredicate> var0) {
      return Util.getOrThrow(CODEC.listOf().encodeStart(JsonOps.INSTANCE, var0), IllegalStateException::new);
   }

   public static List<ItemPredicate> fromJsonArray(@Nullable JsonElement var0) {
      return var0 != null && !var0.isJsonNull() ? Util.getOrThrow(CODEC.listOf().parse(JsonOps.INSTANCE, var0), JsonParseException::new) : List.of();
   }

   public static class Builder {
      private final com.google.common.collect.ImmutableList.Builder<EnchantmentPredicate> enchantments = ImmutableList.builder();
      private final com.google.common.collect.ImmutableList.Builder<EnchantmentPredicate> storedEnchantments = ImmutableList.builder();
      private Optional<HolderSet<Item>> items = Optional.empty();
      private Optional<TagKey<Item>> tag = Optional.empty();
      private MinMaxBounds.Ints count = MinMaxBounds.Ints.ANY;
      private MinMaxBounds.Ints durability = MinMaxBounds.Ints.ANY;
      private Optional<Holder<Potion>> potion = Optional.empty();
      private Optional<NbtPredicate> nbt = Optional.empty();

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
         this.tag = Optional.of(var1);
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
         this.potion = Optional.of(var1.builtInRegistryHolder());
         return this;
      }

      public ItemPredicate.Builder hasNbt(CompoundTag var1) {
         this.nbt = Optional.of(new NbtPredicate(var1));
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
         ImmutableList var1 = this.enchantments.build();
         ImmutableList var2 = this.storedEnchantments.build();
         return new ItemPredicate(this.tag, this.items, this.count, this.durability, var1, var2, this.potion, this.nbt);
      }
   }
}
