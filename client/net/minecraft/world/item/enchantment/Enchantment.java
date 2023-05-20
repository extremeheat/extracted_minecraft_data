package net.minecraft.world.item.enchantment;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;

public abstract class Enchantment {
   private final EquipmentSlot[] slots;
   private final Enchantment.Rarity rarity;
   public final EnchantmentCategory category;
   @Nullable
   protected String descriptionId;

   @Nullable
   public static Enchantment byId(int var0) {
      return BuiltInRegistries.ENCHANTMENT.byId(var0);
   }

   protected Enchantment(Enchantment.Rarity var1, EnchantmentCategory var2, EquipmentSlot[] var3) {
      super();
      this.rarity = var1;
      this.category = var2;
      this.slots = var3;
   }

   public Map<EquipmentSlot, ItemStack> getSlotItems(LivingEntity var1) {
      EnumMap var2 = Maps.newEnumMap(EquipmentSlot.class);

      for(EquipmentSlot var6 : this.slots) {
         ItemStack var7 = var1.getItemBySlot(var6);
         if (!var7.isEmpty()) {
            var2.put(var6, var7);
         }
      }

      return var2;
   }

   public Enchantment.Rarity getRarity() {
      return this.rarity;
   }

   public int getMinLevel() {
      return 1;
   }

   public int getMaxLevel() {
      return 1;
   }

   public int getMinCost(int var1) {
      return 1 + var1 * 10;
   }

   public int getMaxCost(int var1) {
      return this.getMinCost(var1) + 5;
   }

   public int getDamageProtection(int var1, DamageSource var2) {
      return 0;
   }

   public float getDamageBonus(int var1, MobType var2) {
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
      return this.category.canEnchant(var1.getItem());
   }

   public void doPostAttack(LivingEntity var1, Entity var2, int var3) {
   }

   public void doPostHurt(LivingEntity var1, Entity var2, int var3) {
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

   public static enum Rarity {
      COMMON(10),
      UNCOMMON(5),
      RARE(2),
      VERY_RARE(1);

      private final int weight;

      private Rarity(int var3) {
         this.weight = var3;
      }

      public int getWeight() {
         return this.weight;
      }
   }
}
