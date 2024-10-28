package net.minecraft.world.item.enchantment;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Enchantment implements FeatureElement {
   private final EnchantmentDefinition definition;
   @Nullable
   protected String descriptionId;
   private final Holder.Reference<Enchantment> builtInRegistryHolder;

   public static Cost constantCost(int var0) {
      return new Cost(var0, 0);
   }

   public static Cost dynamicCost(int var0, int var1) {
      return new Cost(var0, var1);
   }

   public static EnchantmentDefinition definition(TagKey<Item> var0, TagKey<Item> var1, int var2, int var3, Cost var4, Cost var5, int var6, EquipmentSlot... var7) {
      return new EnchantmentDefinition(var0, Optional.of(var1), var2, var3, var4, var5, var6, FeatureFlags.DEFAULT_FLAGS, var7);
   }

   public static EnchantmentDefinition definition(TagKey<Item> var0, int var1, int var2, Cost var3, Cost var4, int var5, EquipmentSlot... var6) {
      return new EnchantmentDefinition(var0, Optional.empty(), var1, var2, var3, var4, var5, FeatureFlags.DEFAULT_FLAGS, var6);
   }

   public static EnchantmentDefinition definition(TagKey<Item> var0, int var1, int var2, Cost var3, Cost var4, int var5, FeatureFlagSet var6, EquipmentSlot... var7) {
      return new EnchantmentDefinition(var0, Optional.empty(), var1, var2, var3, var4, var5, var6, var7);
   }

   @Nullable
   public static Enchantment byId(int var0) {
      return (Enchantment)BuiltInRegistries.ENCHANTMENT.byId(var0);
   }

   public Enchantment(EnchantmentDefinition var1) {
      super();
      this.builtInRegistryHolder = BuiltInRegistries.ENCHANTMENT.createIntrusiveHolder(this);
      this.definition = var1;
   }

   public Map<EquipmentSlot, ItemStack> getSlotItems(LivingEntity var1) {
      EnumMap var2 = Maps.newEnumMap(EquipmentSlot.class);
      EquipmentSlot[] var3 = this.definition.slots();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EquipmentSlot var6 = var3[var5];
         ItemStack var7 = var1.getItemBySlot(var6);
         if (!var7.isEmpty()) {
            var2.put(var6, var7);
         }
      }

      return var2;
   }

   public final TagKey<Item> getSupportedItems() {
      return this.definition.supportedItems();
   }

   public final boolean isPrimaryItem(ItemStack var1) {
      return this.definition.primaryItems.isEmpty() || var1.is((TagKey)this.definition.primaryItems.get());
   }

   public final int getWeight() {
      return this.definition.weight();
   }

   public final int getAnvilCost() {
      return this.definition.anvilCost();
   }

   public final int getMinLevel() {
      return 1;
   }

   public final int getMaxLevel() {
      return this.definition.maxLevel();
   }

   public final int getMinCost(int var1) {
      return this.definition.minCost().calculate(var1);
   }

   public final int getMaxCost(int var1) {
      return this.definition.maxCost().calculate(var1);
   }

   public int getDamageProtection(int var1, DamageSource var2) {
      return 0;
   }

   public float getDamageBonus(int var1, @Nullable EntityType<?> var2) {
      return 0.0F;
   }

   public final boolean isCompatibleWith(Enchantment var1) {
      return this.checkCompatibility(var1) && var1.checkCompatibility(this);
   }

   protected boolean checkCompatibility(Enchantment var1) {
      return this != var1;
   }

   protected String getOrCreateDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this));
      }

      return this.descriptionId;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   public Component getFullname(int var1) {
      MutableComponent var2 = Component.translatable(this.getDescriptionId());
      if (this.isCurse()) {
         var2.withStyle(ChatFormatting.RED);
      } else {
         var2.withStyle(ChatFormatting.GRAY);
      }

      if (var1 != 1 || this.getMaxLevel() != 1) {
         var2.append(CommonComponents.SPACE).append((Component)Component.translatable("enchantment.level." + var1));
      }

      return var2;
   }

   public boolean canEnchant(ItemStack var1) {
      return var1.getItem().builtInRegistryHolder().is(this.definition.supportedItems());
   }

   public void doPostAttack(LivingEntity var1, Entity var2, int var3) {
   }

   public void doPostHurt(LivingEntity var1, Entity var2, int var3) {
   }

   public void doPostItemStackHurt(LivingEntity var1, Entity var2, int var3) {
   }

   public boolean isTreasureOnly() {
      return false;
   }

   public boolean isCurse() {
      return false;
   }

   public boolean isTradeable() {
      return true;
   }

   public boolean isDiscoverable() {
      return true;
   }

   /** @deprecated */
   @Deprecated
   public Holder.Reference<Enchantment> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   public FeatureFlagSet requiredFeatures() {
      return this.definition.requiredFeatures();
   }

   public static record Cost(int base, int perLevel) {
      public Cost(int base, int perLevel) {
         super();
         this.base = base;
         this.perLevel = perLevel;
      }

      public int calculate(int var1) {
         return this.base + this.perLevel * (var1 - 1);
      }

      public int base() {
         return this.base;
      }

      public int perLevel() {
         return this.perLevel;
      }
   }

   public static record EnchantmentDefinition(TagKey<Item> supportedItems, Optional<TagKey<Item>> primaryItems, int weight, int maxLevel, Cost minCost, Cost maxCost, int anvilCost, FeatureFlagSet requiredFeatures, EquipmentSlot[] slots) {
      final Optional<TagKey<Item>> primaryItems;

      public EnchantmentDefinition(TagKey<Item> supportedItems, Optional<TagKey<Item>> primaryItems, int weight, int maxLevel, Cost minCost, Cost maxCost, int anvilCost, FeatureFlagSet requiredFeatures, EquipmentSlot[] slots) {
         super();
         this.supportedItems = supportedItems;
         this.primaryItems = primaryItems;
         this.weight = weight;
         this.maxLevel = maxLevel;
         this.minCost = minCost;
         this.maxCost = maxCost;
         this.anvilCost = anvilCost;
         this.requiredFeatures = requiredFeatures;
         this.slots = slots;
      }

      public TagKey<Item> supportedItems() {
         return this.supportedItems;
      }

      public Optional<TagKey<Item>> primaryItems() {
         return this.primaryItems;
      }

      public int weight() {
         return this.weight;
      }

      public int maxLevel() {
         return this.maxLevel;
      }

      public Cost minCost() {
         return this.minCost;
      }

      public Cost maxCost() {
         return this.maxCost;
      }

      public int anvilCost() {
         return this.anvilCost;
      }

      public FeatureFlagSet requiredFeatures() {
         return this.requiredFeatures;
      }

      public EquipmentSlot[] slots() {
         return this.slots;
      }
   }
}
