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
   private final Enchantment.EnchantmentDefinition definition;
   @Nullable
   protected String descriptionId;
   private final Holder.Reference<Enchantment> builtInRegistryHolder = BuiltInRegistries.ENCHANTMENT.createIntrusiveHolder(this);

   public static Enchantment.Cost constantCost(int var0) {
      return new Enchantment.Cost(var0, 0);
   }

   public static Enchantment.Cost dynamicCost(int var0, int var1) {
      return new Enchantment.Cost(var0, var1);
   }

   public static Enchantment.EnchantmentDefinition definition(
      TagKey<Item> var0, TagKey<Item> var1, int var2, int var3, Enchantment.Cost var4, Enchantment.Cost var5, int var6, EquipmentSlot... var7
   ) {
      return new Enchantment.EnchantmentDefinition(var0, Optional.of(var1), var2, var3, var4, var5, var6, FeatureFlags.DEFAULT_FLAGS, var7);
   }

   public static Enchantment.EnchantmentDefinition definition(
      TagKey<Item> var0, int var1, int var2, Enchantment.Cost var3, Enchantment.Cost var4, int var5, EquipmentSlot... var6
   ) {
      return new Enchantment.EnchantmentDefinition(var0, Optional.empty(), var1, var2, var3, var4, var5, FeatureFlags.DEFAULT_FLAGS, var6);
   }

   public static Enchantment.EnchantmentDefinition definition(
      TagKey<Item> var0, int var1, int var2, Enchantment.Cost var3, Enchantment.Cost var4, int var5, FeatureFlagSet var6, EquipmentSlot... var7
   ) {
      return new Enchantment.EnchantmentDefinition(var0, Optional.empty(), var1, var2, var3, var4, var5, var6, var7);
   }

   @Nullable
   public static Enchantment byId(int var0) {
      return BuiltInRegistries.ENCHANTMENT.byId(var0);
   }

   public Enchantment(Enchantment.EnchantmentDefinition var1) {
      super();
      this.definition = var1;
   }

   public Map<EquipmentSlot, ItemStack> getSlotItems(LivingEntity var1) {
      EnumMap var2 = Maps.newEnumMap(EquipmentSlot.class);

      for(EquipmentSlot var6 : this.definition.slots()) {
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
      return this.definition.primaryItems.isEmpty() || var1.is((TagKey<Item>)this.definition.primaryItems.get());
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
         var2.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + var1));
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

   @Deprecated
   public Holder.Reference<Enchantment> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   @Override
   public FeatureFlagSet requiredFeatures() {
      return this.definition.requiredFeatures();
   }

   public static record Cost(int a, int b) {
      private final int base;
      private final int perLevel;

      public Cost(int var1, int var2) {
         super();
         this.base = var1;
         this.perLevel = var2;
      }

      public int calculate(int var1) {
         return this.base + this.perLevel * (var1 - 1);
      }
   }

   public static record EnchantmentDefinition(
      TagKey<Item> a, Optional<TagKey<Item>> b, int c, int d, Enchantment.Cost e, Enchantment.Cost f, int g, FeatureFlagSet h, EquipmentSlot[] i
   ) {
      private final TagKey<Item> supportedItems;
      final Optional<TagKey<Item>> primaryItems;
      private final int weight;
      private final int maxLevel;
      private final Enchantment.Cost minCost;
      private final Enchantment.Cost maxCost;
      private final int anvilCost;
      private final FeatureFlagSet requiredFeatures;
      private final EquipmentSlot[] slots;

      public EnchantmentDefinition(
         TagKey<Item> var1,
         Optional<TagKey<Item>> var2,
         int var3,
         int var4,
         Enchantment.Cost var5,
         Enchantment.Cost var6,
         int var7,
         FeatureFlagSet var8,
         EquipmentSlot[] var9
      ) {
         super();
         this.supportedItems = var1;
         this.primaryItems = var2;
         this.weight = var3;
         this.maxLevel = var4;
         this.minCost = var5;
         this.maxCost = var6;
         this.anvilCost = var7;
         this.requiredFeatures = var8;
         this.slots = var9;
      }
   }
}
