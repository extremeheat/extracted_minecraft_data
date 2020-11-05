package net.minecraft.world.item.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
   public static int getItemEnchantmentLevel(Enchantment var0, ItemStack var1) {
      if (var1.isEmpty()) {
         return 0;
      } else {
         ResourceLocation var2 = Registry.ENCHANTMENT.getKey(var0);
         ListTag var3 = var1.getEnchantmentTags();

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            CompoundTag var5 = var3.getCompound(var4);
            ResourceLocation var6 = ResourceLocation.tryParse(var5.getString("id"));
            if (var6 != null && var6.equals(var2)) {
               return Mth.clamp(var5.getInt("lvl"), 0, 255);
            }
         }

         return 0;
      }
   }

   public static Map<Enchantment, Integer> getEnchantments(ItemStack var0) {
      ListTag var1 = var0.is(Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantments(var0) : var0.getEnchantmentTags();
      return deserializeEnchantments(var1);
   }

   public static Map<Enchantment, Integer> deserializeEnchantments(ListTag var0) {
      LinkedHashMap var1 = Maps.newLinkedHashMap();

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         CompoundTag var3 = var0.getCompound(var2);
         Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(var3.getString("id"))).ifPresent((var2x) -> {
            Integer var10000 = (Integer)var1.put(var2x, var3.getInt("lvl"));
         });
      }

      return var1;
   }

   public static void setEnchantments(Map<Enchantment, Integer> var0, ItemStack var1) {
      ListTag var2 = new ListTag();
      Iterator var3 = var0.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         Enchantment var5 = (Enchantment)var4.getKey();
         if (var5 != null) {
            int var6 = (Integer)var4.getValue();
            CompoundTag var7 = new CompoundTag();
            var7.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(var5)));
            var7.putShort("lvl", (short)var6);
            var2.add(var7);
            if (var1.is(Items.ENCHANTED_BOOK)) {
               EnchantedBookItem.addEnchantment(var1, new EnchantmentInstance(var5, var6));
            }
         }
      }

      if (var2.isEmpty()) {
         var1.removeTagKey("Enchantments");
      } else if (!var1.is(Items.ENCHANTED_BOOK)) {
         var1.addTagElement("Enchantments", var2);
      }

   }

   private static void runIterationOnItem(EnchantmentHelper.EnchantmentVisitor var0, ItemStack var1) {
      if (!var1.isEmpty()) {
         ListTag var2 = var1.getEnchantmentTags();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            String var4 = var2.getCompound(var3).getString("id");
            int var5 = var2.getCompound(var3).getInt("lvl");
            Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(var4)).ifPresent((var2x) -> {
               var0.accept(var2x, var5);
            });
         }

      }
   }

   private static void runIterationOnInventory(EnchantmentHelper.EnchantmentVisitor var0, Iterable<ItemStack> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         runIterationOnItem(var0, var3);
      }

   }

   public static int getDamageProtection(Iterable<ItemStack> var0, DamageSource var1) {
      MutableInt var2 = new MutableInt();
      runIterationOnInventory((var2x, var3) -> {
         var2.add(var2x.getDamageProtection(var3, var1));
      }, var0);
      return var2.intValue();
   }

   public static float getDamageBonus(ItemStack var0, MobType var1) {
      MutableFloat var2 = new MutableFloat();
      runIterationOnItem((var2x, var3) -> {
         var2.add(var2x.getDamageBonus(var3, var1));
      }, var0);
      return var2.floatValue();
   }

   public static float getSweepingDamageRatio(LivingEntity var0) {
      int var1 = getEnchantmentLevel(Enchantments.SWEEPING_EDGE, var0);
      return var1 > 0 ? SweepingEdgeEnchantment.getSweepingDamageRatio(var1) : 0.0F;
   }

   public static void doPostHurtEffects(LivingEntity var0, Entity var1) {
      EnchantmentHelper.EnchantmentVisitor var2 = (var2x, var3) -> {
         var2x.doPostHurt(var0, var1, var3);
      };
      if (var0 != null) {
         runIterationOnInventory(var2, var0.getAllSlots());
      }

      if (var1 instanceof Player) {
         runIterationOnItem(var2, var0.getMainHandItem());
      }

   }

   public static void doPostDamageEffects(LivingEntity var0, Entity var1) {
      EnchantmentHelper.EnchantmentVisitor var2 = (var2x, var3) -> {
         var2x.doPostAttack(var0, var1, var3);
      };
      if (var0 != null) {
         runIterationOnInventory(var2, var0.getAllSlots());
      }

      if (var0 instanceof Player) {
         runIterationOnItem(var2, var0.getMainHandItem());
      }

   }

   public static int getEnchantmentLevel(Enchantment var0, LivingEntity var1) {
      Collection var2 = var0.getSlotItems(var1).values();
      if (var2 == null) {
         return 0;
      } else {
         int var3 = 0;
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            ItemStack var5 = (ItemStack)var4.next();
            int var6 = getItemEnchantmentLevel(var0, var5);
            if (var6 > var3) {
               var3 = var6;
            }
         }

         return var3;
      }
   }

   public static int getKnockbackBonus(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.KNOCKBACK, var0);
   }

   public static int getFireAspect(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.FIRE_ASPECT, var0);
   }

   public static int getRespiration(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.RESPIRATION, var0);
   }

   public static int getDepthStrider(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.DEPTH_STRIDER, var0);
   }

   public static int getBlockEfficiency(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, var0);
   }

   public static int getFishingLuckBonus(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.FISHING_LUCK, var0);
   }

   public static int getFishingSpeedBonus(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.FISHING_SPEED, var0);
   }

   public static int getMobLooting(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.MOB_LOOTING, var0);
   }

   public static boolean hasAquaAffinity(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.AQUA_AFFINITY, var0) > 0;
   }

   public static boolean hasFrostWalker(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.FROST_WALKER, var0) > 0;
   }

   public static boolean hasSoulSpeed(LivingEntity var0) {
      return getEnchantmentLevel(Enchantments.SOUL_SPEED, var0) > 0;
   }

   public static boolean hasBindingCurse(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.BINDING_CURSE, var0) > 0;
   }

   public static boolean hasVanishingCurse(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, var0) > 0;
   }

   public static int getLoyalty(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.LOYALTY, var0);
   }

   public static int getRiptide(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.RIPTIDE, var0);
   }

   public static boolean hasChanneling(ItemStack var0) {
      return getItemEnchantmentLevel(Enchantments.CHANNELING, var0) > 0;
   }

   @Nullable
   public static Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment var0, LivingEntity var1) {
      return getRandomItemWith(var0, var1, (var0x) -> {
         return true;
      });
   }

   @Nullable
   public static Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment var0, LivingEntity var1, Predicate<ItemStack> var2) {
      Map var3 = var0.getSlotItems(var1);
      if (var3.isEmpty()) {
         return null;
      } else {
         ArrayList var4 = Lists.newArrayList();
         Iterator var5 = var3.entrySet().iterator();

         while(var5.hasNext()) {
            Entry var6 = (Entry)var5.next();
            ItemStack var7 = (ItemStack)var6.getValue();
            if (!var7.isEmpty() && getItemEnchantmentLevel(var0, var7) > 0 && var2.test(var7)) {
               var4.add(var6);
            }
         }

         return var4.isEmpty() ? null : (Entry)var4.get(var1.getRandom().nextInt(var4.size()));
      }
   }

   public static int getEnchantmentCost(Random var0, int var1, int var2, ItemStack var3) {
      Item var4 = var3.getItem();
      int var5 = var4.getEnchantmentValue();
      if (var5 <= 0) {
         return 0;
      } else {
         if (var2 > 15) {
            var2 = 15;
         }

         int var6 = var0.nextInt(8) + 1 + (var2 >> 1) + var0.nextInt(var2 + 1);
         if (var1 == 0) {
            return Math.max(var6 / 3, 1);
         } else {
            return var1 == 1 ? var6 * 2 / 3 + 1 : Math.max(var6, var2 * 2);
         }
      }
   }

   public static ItemStack enchantItem(Random var0, ItemStack var1, int var2, boolean var3) {
      List var4 = selectEnchantment(var0, var1, var2, var3);
      boolean var5 = var1.is(Items.BOOK);
      if (var5) {
         var1 = new ItemStack(Items.ENCHANTED_BOOK);
      }

      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         EnchantmentInstance var7 = (EnchantmentInstance)var6.next();
         if (var5) {
            EnchantedBookItem.addEnchantment(var1, var7);
         } else {
            var1.enchant(var7.enchantment, var7.level);
         }
      }

      return var1;
   }

   public static List<EnchantmentInstance> selectEnchantment(Random var0, ItemStack var1, int var2, boolean var3) {
      ArrayList var4 = Lists.newArrayList();
      Item var5 = var1.getItem();
      int var6 = var5.getEnchantmentValue();
      if (var6 <= 0) {
         return var4;
      } else {
         var2 += 1 + var0.nextInt(var6 / 4 + 1) + var0.nextInt(var6 / 4 + 1);
         float var7 = (var0.nextFloat() + var0.nextFloat() - 1.0F) * 0.15F;
         var2 = Mth.clamp(Math.round((float)var2 + (float)var2 * var7), 1, 2147483647);
         List var8 = getAvailableEnchantmentResults(var2, var1, var3);
         if (!var8.isEmpty()) {
            var4.add(WeighedRandom.getRandomItem(var0, var8));

            while(var0.nextInt(50) <= var2) {
               filterCompatibleEnchantments(var8, (EnchantmentInstance)Util.lastOf(var4));
               if (var8.isEmpty()) {
                  break;
               }

               var4.add(WeighedRandom.getRandomItem(var0, var8));
               var2 /= 2;
            }
         }

         return var4;
      }
   }

   public static void filterCompatibleEnchantments(List<EnchantmentInstance> var0, EnchantmentInstance var1) {
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         if (!var1.enchantment.isCompatibleWith(((EnchantmentInstance)var2.next()).enchantment)) {
            var2.remove();
         }
      }

   }

   public static boolean isEnchantmentCompatible(Collection<Enchantment> var0, Enchantment var1) {
      Iterator var2 = var0.iterator();

      Enchantment var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (Enchantment)var2.next();
      } while(var3.isCompatibleWith(var1));

      return false;
   }

   public static List<EnchantmentInstance> getAvailableEnchantmentResults(int var0, ItemStack var1, boolean var2) {
      ArrayList var3 = Lists.newArrayList();
      Item var4 = var1.getItem();
      boolean var5 = var1.is(Items.BOOK);
      Iterator var6 = Registry.ENCHANTMENT.iterator();

      while(true) {
         while(true) {
            Enchantment var7;
            do {
               do {
                  do {
                     if (!var6.hasNext()) {
                        return var3;
                     }

                     var7 = (Enchantment)var6.next();
                  } while(var7.isTreasureOnly() && !var2);
               } while(!var7.isDiscoverable());
            } while(!var7.category.canEnchant(var4) && !var5);

            for(int var8 = var7.getMaxLevel(); var8 > var7.getMinLevel() - 1; --var8) {
               if (var0 >= var7.getMinCost(var8) && var0 <= var7.getMaxCost(var8)) {
                  var3.add(new EnchantmentInstance(var7, var8));
                  break;
               }
            }
         }
      }
   }

   @FunctionalInterface
   interface EnchantmentVisitor {
      void accept(Enchantment var1, int var2);
   }
}
